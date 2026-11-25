package com.byteblog.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 留言板 请求体
 */
@Data
public class MessageDTO {
    @NotBlank(message = "昵称不能为空")
    private String name;

    // 头像
    private String avatar;

    @NotBlank(message = "留言内容不能为空")
    private String content;

    // 父级Id
    private Long parentId;



   // 被回复人的昵称
    private String replyName;
}
