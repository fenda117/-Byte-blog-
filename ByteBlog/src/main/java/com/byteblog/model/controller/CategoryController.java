package com.byteblog.model.controller;


import com.byteblog.common.Result;
import com.byteblog.model.dto.CategoryDto;
import com.byteblog.model.entity.Category;
import com.byteblog.model.service.CategoryService;
import com.byteblog.model.vo.CategoryVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 种类分类控制器
 * 简单的CRUD
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    // 查看标签
    @GetMapping
    public Result<List<CategoryVo>> list() {
        return Result.success(categoryService.listCategories());
    }
    // 新增标签
    @PostMapping
    public Result<CategoryVo> insert(@Valid @RequestBody CategoryDto dto) {
        return Result.success(categoryService.insertCategory(dto));
    }
    // 修改标签
    @PutMapping("/{id}")
    public Result<CategoryVo> update(@PathVariable Long id, @Valid @RequestBody CategoryDto dto) {
        return Result.success(categoryService.updateCategory(id, dto));
    }
    // 删除标签
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

}
