package com.byteblog.model.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.byteblog.model.entity.Article;
import com.byteblog.model.entity.Project;
import com.byteblog.model.mapper.ArticleMapper;
import com.byteblog.model.mapper.ProjectMapper;
import com.byteblog.model.service.SearchItemService;
import com.byteblog.model.vo.SearchItemVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 全站搜索业务实体类
 */
@Service
@RequiredArgsConstructor
public class SearchItemImpl implements SearchItemService {

    private final ArticleMapper articleMapper;
    private final ProjectMapper projectMapper;

    @Override
    public List<SearchItemVo> fullSearch(String keyword) {
        List<SearchItemVo> resultList = new ArrayList<>();
        if (!StringUtils.hasText(keyword)) {
            return resultList;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 搜索文章
        List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1)
                .and(w -> w.like(Article::getTitle, keyword).or().like(Article::getSummary, keyword)));
       for (Article article : articles) {
           SearchItemVo vo = new SearchItemVo();
            vo.setId(article.getId());
            vo.setTitle(article.getTitle());
            vo.setSummary(article.getSummary());
            vo.setType("article");
            if(article.getCreateTime()!=null){
                vo.setCreateTime(article.getCreateTime().format(formatter));
            }
            resultList.add(vo);
       }
       // 搜索项目
        List<Project> projects = projectMapper.selectList(new LambdaQueryWrapper<Project>()
                .and(w -> w.like(Project::getName, keyword).or().like(Project::getDescription, keyword)));
       for (Project project : projects) {
           SearchItemVo vo = new SearchItemVo();
           vo.setId(project.getId());
           vo.setTitle(project.getName());
           vo.setSummary(project.getDescription());
           vo.setType("project");
           if(project.getCreateTime()!=null){
               vo.setCreateTime(project.getCreateTime().format(formatter));
           }
           resultList.add(vo);
       }
       // 排序，按时间倒序
        resultList.sort(Comparator.comparing(SearchItemVo::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return resultList;
    }
}
