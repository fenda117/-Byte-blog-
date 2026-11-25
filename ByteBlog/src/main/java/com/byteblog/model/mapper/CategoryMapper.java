package com.byteblog.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.byteblog.model.entity.Category;
import com.byteblog.model.vo.CategoryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 种类分类实体类Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询所有分类以及每个分类下的文章数量
     */
    @Select("SELECT c.id, c.name, c.sort, COUNT(a.id) AS articleCount " +
            "FROM category c " +
            "LEFT JOIN article a ON c.id = a.category_id " +
            "AND a.status = 1 " +   // 只统计已发布的文章
            "AND a.deleted = 0 " +  // 且未被逻辑删除的
            "GROUP BY c.id, c.name, c.sort " +
            "ORDER BY c.sort ASC")
    List<CategoryVo> selectCategoryWithArticleCount();

}
