package com.byteblog.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.byteblog.model.entity.SiteStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统计网站实体类Mapper
 */
@Mapper
public interface SiteStatMapper extends BaseMapper<SiteStat> {

    @Select("SELECT SUM(uv) FROM visit_stat")
    Long selectTotalUv();

    /**
     * 站点首次初始化的时间，用作「运行天数」的起点。
     * blog_info 只有一行配置记录，取 id 最小的那条。
     */
    @Select("SELECT create_time FROM blog_info ORDER BY id LIMIT 1")
    LocalDateTime selectLaunchTime();

    @Select("SELECT COUNT(*) FROM article WHERE status = 1 AND deleted = 0")
    Long countArticles();

    @Select("SELECT COUNT(*) FROM project WHERE deleted = 0")
    Long countProjects();

    @Select("SELECT COUNT(*) FROM message WHERE status = 1")
    Long countMessages();
}
