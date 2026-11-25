package com.byteblog.model.vo;

import lombok.Data;

/**
 * 全站搜索实体类
 */
@Data
public class SearchItemVo {


    private Long id;
    private String title;       // 标题（文章名或项目名）
    private String summary;     // 摘要（文章摘要或项目描述）
    private String type;        // 类型："article" 或 "project"
    private String category;    // 如果是文章，展示分类名；如果是项目，可以为空
    private String createTime;

}
