package com.byteblog.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Agent 对话请求体
 */
@Data
public class AgentChatDTO {

    @NotBlank(message = "问题不能为空")
    @Size(max = 500, message = "问题太长了，精简到 500 字以内")
    private String question;
}
