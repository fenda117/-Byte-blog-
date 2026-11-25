package com.byteblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 个人信息实体类
 * 实现了序列化
 * 实现了lombok@Data,@NoArgsConstructor,@AllArgsConstructor注解 无需手动创建无参、有参、setter等方法
 * 实现了@TableName("project")注解，告诉Mybatis 对应数据库表名
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="blog_info",autoResultMap=true)
public class BlogInfo implements Serializable {
    // 指定id=1，不自动生成
    @TableId(type = IdType.INPUT)
    private Long id;

    private String name;
    // 头像
    private String avatarUrl;
    // 个性签名
    private String signature;
    // 关于我
    private String aboutMe;
    // GitHub链接等数据库存JSON
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> socialLinks;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
