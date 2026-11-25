package com.byteblog.model.service;

import com.byteblog.model.dto.AgentChatDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 博客助手 Agent 业务接口
 */
public interface AgentService {

    /**
     * 提问并以 SSE 流的方式返回回答。
     * 返回 SseEmitter 后由 Spring 接管连接，内容在独立线程里逐步写入。
     */
    SseEmitter chat(AgentChatDTO dto);
}
