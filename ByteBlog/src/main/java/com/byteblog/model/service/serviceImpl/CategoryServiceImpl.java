package com.byteblog.model.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.byteblog.common.ResultCode;
import com.byteblog.exception.BusinessException;
import com.byteblog.model.dto.CategoryDto;
import com.byteblog.model.entity.Article;
import com.byteblog.model.entity.Category;
import com.byteblog.model.mapper.ArticleMapper;
import com.byteblog.model.mapper.CategoryMapper;
import com.byteblog.model.service.CategoryService;
import com.byteblog.model.vo.CategoryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 种类分类业务实现类
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;

    @Override
    public List<CategoryVo> listCategories() {

        return categoryMapper.selectCategoryWithArticleCount();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVo insertCategory(CategoryDto dto) {
        Long count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>().eq(Category::getName, dto.getName()));
        if (count > 0) {
            throw new BusinessException(ResultCode.REPEATED_NAME);
        }
        // 复制并保存
        Category category = new Category();
        BeanUtils.copyProperties(dto,category);
        categoryMapper.insert(category);
        return toVO(category);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVo updateCategory(Long id, CategoryDto dto) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        // 校验重名（排除自己）
        if (!category.getName().equals(dto.getName())) {
            Long count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                    .eq(Category::getName, dto.getName())
                    .ne(Category::getId, id));
            if (count > 0) {
                throw new BusinessException(ResultCode.CATEGORY_REPETITION);
            }
        }
        BeanUtils.copyProperties(dto, category);
        category.setId(id);
        categoryMapper.updateById(category);
        // 直接返回内存对象转 VO
        return toVO(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        categoryMapper.deleteById(id);

    }

    private CategoryVo toVO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryVo vo = new CategoryVo();
        BeanUtils.copyProperties(category, vo);
        vo.setArticleCount(articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getCategoryId, category.getId())));
        return vo;
    }
}
