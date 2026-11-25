package com.byteblog.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章 响应体
 *
 */
@Data
public class ArticleVo {

    // 主键必须与 BaseEntity 的 Long 一致：BeanUtils.copyProperties 遇到
    // Long -> Integer 类型不兼容会静默跳过，导致接口返回 "id": null
    private Long id;

    private String title;
    // 总结
    private String summary;
    // 原文
    private String contentMd;
    // 渲染后的html
    private String contentHtml;
    // 分类id
    private String category;
    // 标签
    private List<String> tags;
    // 封面地址
    private String coverUrl;
    // 浏览量
    private Integer viewCount;
    // 是否为精选or置顶
    private Integer featured;
    // 状态 0为草稿1为发布成功
    private Integer status;

    private LocalDateTime createTime;
}
