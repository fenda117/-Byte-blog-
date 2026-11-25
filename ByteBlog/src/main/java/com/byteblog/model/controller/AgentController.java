package com.byteblog.model.controller;

import com.byteblog.common.Result;
import com.byteblog.config.AiProperties;
import com.byteblog.model.dto.AgentChatDTO;
import com.byteblog.model.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 博客助手 Agent 接口。
 *
 * 访客可直接调用，不需要登录——所以要在 AuthInterceptor 里放行 /api/agent/。
 */
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;
    private final AiProperties aiProperties;

    /**
     * 流式对话。
     * produces 必须声明为 text/event-stream，否则浏览器不会按 SSE 解析。
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@Valid @RequestBody AgentChatDTO dto) {
        return agentService.chat(dto);
    }

    /**
     * 前端用它显示"已接入 / 未配置"标签，也方便你部署后自检。
     * 只暴露是否就绪，不返回 Key 本身。
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> status() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("ready", aiProperties.isReady());
        data.put("model", aiProperties.getModel() == null ? "" : aiProperties.getModel());
        data.put("baseUrl", aiProperties.getBaseUrl() == null ? "" : aiProperties.getBaseUrl());
        return Result.success(data);
    }
}
