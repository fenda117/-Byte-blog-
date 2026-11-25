package com.byteblog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.byteblog.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends BaseEntity {

    private String title;
    // 总结
    private String summary;
    // 渲染后的html
    private String contentHtml;
    // 分类名
    private Long categoryId;
    // 标签
    private String tags;
    // 封面地址
    private String coverUrl;
    // 浏览量
    private Integer viewCount;
    // 是否为精选or置顶
    private Integer featured;
    // 原文
    private String contentMd;
    // 状态 0为草稿1为发布成功
    private Integer status;

}
