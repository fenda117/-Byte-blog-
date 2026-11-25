package com.byteblog.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.byteblog.model.entity.Article;
import com.byteblog.model.vo.ArchiveVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章实体类Mapper
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') AS month, COUNT(*) AS count " +
            "FROM article WHERE deleted = 0 AND status = 1 " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m') ORDER BY month DESC")
    List<ArchiveVo> selectArchives();

}
