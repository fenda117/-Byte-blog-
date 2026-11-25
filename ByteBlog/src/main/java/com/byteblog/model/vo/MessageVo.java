package com.byteblog.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 留言板实体类 响应体
 */
@Data
@TableName("message")
public class MessageVo {

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
        private LocalDateTime createTime;

        /* ---------- 审核相关 ---------- */

        /** 0待审核 1通过 2已拒绝 */
        private Integer status;

        /** AI 审批结果：0待处理 1AI通过 2AI拒绝 3转人工 */
        private Integer auditStatus;

        /** AI 给的理由 */
        private String auditReason;

        /** ai / manual / system */
        private String auditBy;

        private LocalDateTime auditTime;

        // 装子留言回复用
        private List<MessageVo> children;

}
