package com.byteblog.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目展示 响应体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectVo {

    private long id;

    private String name;
    // 项目描述
    private String description;
    // 技术栈列表
    private List<String> techStackList;
    // GitHub地址
    private String githubUrl;
    // 项目地址
    private String demoUrl;
    // 封面地址
    private String coverUrl;
    // 是否置顶or精选
    private Boolean featured;
    // 浏览量
    private Integer viewCount;

    private LocalDateTime createTime;

}
