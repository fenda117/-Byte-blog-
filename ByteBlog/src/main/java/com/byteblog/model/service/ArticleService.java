package com.byteblog.model.service;

import com.byteblog.model.dto.ArticleDto;
import com.byteblog.model.entity.Article;
import com.byteblog.model.vo.ArchiveVo;
import com.byteblog.model.vo.ArticleVo;

import java.util.List;

/**
 * 文章实体类业务接口
 */
public interface ArticleService {
    /**
     * 展示文章首页
     * @param category 种类
     * @param tag 标签
     * @param keyword 关键字
     * @return 返回文章首页
     */
    List<ArticleVo> listArticles(String category, String tag, String keyword);

    /**
     * 访问文章(浏览量+1 配合redis使用)
     * @param id
     * @return
     */
    ArticleVo getArticle(Long id);

    /**
     * 创建文章
     * @param dto
     * @return
     */
    ArticleVo createArticle(ArticleDto dto);

    /**
     * 修改文章的方法
     * @param dto
     * @return
     */
    ArticleVo updateArticle(Long Id,ArticleDto dto);

    /**
     * 删除文章的方法
     * @param Id
     * @return
     */
    void deleteArticle(Long Id);

    /**
     * 文章存档(按月)
     * @return
     */
    List<ArchiveVo> listArchives();
}
