package com.byteblog.model.service.serviceImpl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.byteblog.common.ResultCode;
import com.byteblog.exception.BusinessException;
import com.byteblog.model.dto.ArticleDto;
import com.byteblog.model.entity.Article;
import com.byteblog.model.entity.Category;
import com.byteblog.model.mapper.ArticleMapper;
import com.byteblog.model.mapper.CategoryMapper;
import com.byteblog.model.service.ArticleService;
import com.byteblog.model.vo.ArchiveVo;
import com.byteblog.model.vo.ArticleVo;
import com.byteblog.util.IpLocationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文章实体类业务实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    // 注入mapper
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final IpLocationUtils ipLocationUtils;

    /**
     *  列表查询文章
     * @param category 种类
     * @param tag 标签
     * @param keyword 关键字
     * @return
     */
    @Override
    public List<ArticleVo> listArticles(String category, String tag, String keyword) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus,1);
        // 关键词搜索(标题或摘要)
        if(StringUtils.hasText(keyword)){
            wrapper.and(w->w.like(Article::getTitle,keyword).or().like(Article::getSummary,keyword));
        }
        // 标签模糊
        if(StringUtils.hasText(tag)){
            wrapper.like(Article::getTags,tag);
        }
        // 分类过滤
        if(StringUtils.hasText(category)){
            Category cat1 = categoryMapper.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getName, category));
            if(cat1 != null){
                wrapper.eq(Article::getCategoryId,cat1.getId());
            }else {
                return  new ArrayList<>();
            }
        }
        wrapper.orderByDesc(Article::getCreateTime);
        List<Article> articles = articleMapper.selectList(wrapper);
        if (articles.isEmpty()) {
            return new ArrayList<>();
        }
        // 收集所有文章里用到的 categoryId，去重
        Set<Long> categoryIds = articles.stream()
                .map(Article::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        // 准备一个 Map，用来快速查找 id -> 分类名
        Map<Long, String> categoryMap = new HashMap<>();
        // 如果分类 ID 集合不为空，只发一次 SQL 批量查出来
        if (!categoryIds.isEmpty()) {
            List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
            // 把查出来的分类列表转成 Map (id -> name)
            categoryMap = categories.stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
        }
        // 组装 VO
        List<ArticleVo> voList = new ArrayList<>();
        for (Article a : articles) {
            // 从 Map 里直接取名字，如果没有就是 null
            String categoryName = categoryMap.get(a.getCategoryId());
            ArticleVo vo = toVo(a, categoryName, false);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public ArticleVo getArticle(Long id) {
        Article article = articleMapper.selectById(id);
        // 查文章
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        // 查分类名（如果分类ID不为空）
        String categoryName = "";
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                categoryName = category.getName();
            }
        }
        ArticleVo vo = toVo(article, categoryName, true);
        // 老数据只存了Markdown 原文，contentHtml 为空时用原文兜底，避免详情页空白
        if (!StringUtils.hasText(vo.getContentHtml())) {
            vo.setContentHtml(vo.getContentMd());
        }
        // 处理 Redis 浏览量逻辑
        try {
            String ip = IpLocationUtils.getRealIp();
            String ipKey = "view:ip:article:" + id + ":" + ip;
            Boolean isFirstView = stringRedisTemplate.opsForValue().setIfAbsent(ipKey, "1", 1, TimeUnit.HOURS);
            if (Boolean.TRUE.equals(isFirstView)) {
                stringRedisTemplate.opsForHash().increment("view:article", id.toString(), 1);
            }
            // 把 Redis 里的最新增量叠加到 Vo 上返回
            Object incrementObj = stringRedisTemplate.opsForHash().get("view:article", id.toString());
            int increment = incrementObj != null ? Integer.parseInt(incrementObj.toString()) : 0;
            vo.setViewCount((article.getViewCount() == null ? 0 : article.getViewCount()) + increment);
        } catch (NumberFormatException e) {
            // Redis 不可用：降级为只返回数据库里的浏览量
            log.warn("Redis 浏览量统计失败，已降级处理: {}", e.getMessage());
            vo.setViewCount(article.getViewCount() == null ? 0 : article.getViewCount());
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVo createArticle(ArticleDto dto) {
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        article.setContentHtml(dto.getContentMd());
        // 初始化浏览量
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        article.setCreateTime(LocalDateTime.now());
        articleMapper.insert(article);
        String categoryName = "";
        if (article.getCategoryId() != null) {
            Category cat = categoryMapper.selectById(article.getCategoryId());
            if (cat != null) categoryName = cat.getName();
        }
        return toVo(article, categoryName, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVo updateArticle(Long id, ArticleDto dto) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        BeanUtils.copyProperties(dto, article);
        article.setId(id);
        articleMapper.updateById(article);
        String categoryName = "";
        if (article.getCategoryId() != null) {
            Category cat = categoryMapper.selectById(article.getCategoryId());
            if (cat != null) categoryName = cat.getName();
        }
        return toVo(article, categoryName, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long Id) {
        if(articleMapper.selectById(Id) == null){
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        articleMapper.deleteById(Id);
    }

    @Override
    public List<ArchiveVo> listArchives() {
        return articleMapper.selectArchives();
    }

    private ArticleVo toVo(Article a, String categoryName, boolean isDetail) {
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(a, articleVo);
        articleVo.setCategory(categoryName);
        // 处理标签
        if(StringUtils.hasText(a.getTags())){
            // tags 在库里是 "SpringBoot,Vue3,全栈" 这种逗号串，
            // Arrays.asList(整串) 只会得到一个元素，前端渲染出来就是一整行
            articleVo.setTags(Arrays.stream(a.getTags().split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .toList());
        }else {
            articleVo.setTags(List.of());
        }
        if(!isDetail){
            articleVo.setContentHtml(null);
            articleVo.setContentMd(null);
        }
        return articleVo;
    }
}
