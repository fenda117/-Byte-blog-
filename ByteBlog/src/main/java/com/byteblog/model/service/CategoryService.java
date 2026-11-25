package com.byteblog.model.service;


import com.byteblog.model.dto.CategoryDto;
import com.byteblog.model.entity.Category;
import com.byteblog.model.vo.CategoryVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 种类分类业务接口
 * 实现简单的CRUD
 */

public interface CategoryService {

    List<CategoryVo> listCategories();

    CategoryVo insertCategory(CategoryDto dto);

    CategoryVo updateCategory(Long id, CategoryDto dto);

    void deleteCategory(Long id);

}
