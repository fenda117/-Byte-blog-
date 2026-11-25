package com.byteblog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.byteblog.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 项目实体类
 * 继承公共实体类
 * 实现了lombok@Data,@NoArgsConstructor,@AllArgsConstructor注解 无需手动创建无参、有参、setter等方法
 * 实现了@EqualsAndHashCode(callSuper = true)注解 自动生成equals() 和 hashCode() 方法
 * 实现了@TableName("project")注解，告诉Mybatis 对应数据库表名
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("project")
public class Project extends BaseEntity {

    private String name;
    // 项目介绍
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
    // 浏览量
    private Integer viewCount;
    // 排序
    private Integer sort;

}
