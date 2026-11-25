package com.byteblog.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *  文章 实体类
 */
@Data
public class ArticleDto {

    @NotBlank(message = "标题不能为空")
    @Size(max = 50, message = "标题不能超过 50 字")
    private String title;
    // 总结
    private String summary;
    @NotBlank(message = "正文不能为空")
    private String contentMd;
    // 分类Id
    private Long categoryId;
    // 标签
    private String tags;
    // 封面地址
    private String coverUrl;
    // 是否精选
    private Integer featured;
    // 状态
    private Integer status;


}
