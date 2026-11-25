package com.byteblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 种类分类实体类，文章，项目实体类的前置条件
 * 可以给文章or项目打上tags
 */
@Data
@TableName("category")
public class Category implements Serializable {
    // 序列化
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    // 排序
    private Integer sort;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
