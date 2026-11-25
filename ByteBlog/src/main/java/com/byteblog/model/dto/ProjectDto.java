package com.byteblog.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目展示 请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {

    @NotBlank(message = "项目名称不能为空")
    @Size(message = "项目名称不能大于50个字",max = 50)
    private String name;
    // 项目介绍
    @NotBlank(message = "项目介绍不能为空")
    private String description;
    // 用到的技术栈
    private String techStack;
    // 源码仓库地址
    private String githubUrl;
    // 在线演示地址
    private String demoUrl;
    // 封面图地址
    private String coverUrl;
    // 是否为精选or置顶
    private Integer featured;
    // 排序
    private Integer sort;
}
