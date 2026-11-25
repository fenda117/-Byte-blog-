package com.byteblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 留言板块实体类
 */
@Data
@TableName("message")
public class Message implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    // 头像
    private String avatar;
    // 内容
    private String content;
    // 父级留言(回复用)
    private Long parentId;
    // 被回复人昵称
    private String replyName;
    // 是否置顶
    private Boolean isTop;
    // ip地址
    private String ipLocation;
    // 审核留言状态 0待审核1通过2已拒绝
    private Integer status;

    /** AI 审批结果：0待处理 1AI通过 2AI拒绝 3转人工 */
    private Integer auditStatus;

    /** AI 给的理由，或转人工的原因 */
    private String auditReason;

    /** 这条最终由谁定的：ai / manual / system */
    private String auditBy;

    /** 审批时间 */
    private LocalDateTime auditTime;

    private LocalDateTime createTime;

}
