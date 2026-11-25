package com.byteblog.model.vo;


import com.byteblog.model.entity.SiteStat;
import lombok.Data;

import java.util.List;

/**
 * 统计网站 响应体
 */
@Data
public class SiteStatVo {
    // 文章总数
    private Long articleCount;
    // 项目总数
    private Long projectCount;
    // 留言总数
    private Long messageCount;
    // 运行天数
    private Long runDays;
    // 总访客数
    private Long visitorCount;

    // 近七天趋势图
    private List<SiteStat> trendList;

}
