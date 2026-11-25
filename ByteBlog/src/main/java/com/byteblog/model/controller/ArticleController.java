package com.byteblog.model.controller;

import com.byteblog.common.Result;
import com.byteblog.model.dto.ArticleDto;
import com.byteblog.model.entity.Article;
import com.byteblog.model.mapper.ArticleMapper;
import com.byteblog.model.service.ArticleService;
import com.byteblog.model.vo.ArchiveVo;
import com.byteblog.model.vo.ArticleVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章页控制器
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 文章列表（支持按分类、标签、关键词过滤）
     * @param category
     * @param tag
     * @param keyword
     * @return
     */
    @GetMapping
    public Result<List<ArticleVo>> list(@RequestParam(required = false) String category,
                                        @RequestParam(required = false) String tag,
                                        @RequestParam(required = false) String keyword) {
        return Result.success(articleService.listArticles(category, tag, keyword));
    }

    /**
     * 文章按月存档
     * @return
     */
    @GetMapping("/archives")
    public Result<List<ArchiveVo>> archives() {
        return Result.success(articleService.listArchives());
    }

    /**
     * 文章详情页，配合redis使用
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<ArticleVo> detail(@PathVariable Long id) {
        return Result.success(articleService.getArticle(id));
    }

    /**
     * 创建文章
     * @param dto
     * @return
     */
    @PostMapping
    public Result<ArticleVo> createArticle(@Valid @RequestBody ArticleDto dto) {
        ArticleVo vo = articleService.createArticle(dto);
        return Result.success("文章创建成功🎇",vo);
    }

    /**
     * 更新文章
     * @param id
     * @param dto
     * @return
     */
    @PutMapping("/{id}")
    public Result<ArticleVo> updateArticle(@PathVariable Long id,@Valid @RequestBody ArticleDto dto) {
        ArticleVo vo = articleService.updateArticle(id,dto);
        return Result.success("文章更新成功",vo);
    }

    /**
     * 删除文章
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success("文章删除成功",null);
    }
}
