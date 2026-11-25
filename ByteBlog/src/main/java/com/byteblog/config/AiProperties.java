package com.byteblog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI Agent 配置。
 *
 * 全部配置项都在 application.properties 里，改完重启即可生效，不需要改代码。
 * 只要把 ai.enabled 改成 true、ai.api-key 填上，前端就能正常对话。
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    /** 总开关：false 时前端会提示"尚未配置"，不会真的发起调用 */
    private boolean enabled = false;

    /** API Key，最关键的项，留空就是未配置 */
    private String apiKey = "";

    /** 兼容 OpenAI 协议的接口地址（DeepSeek / 通义千问 / 智谱 都支持） */
    private String baseUrl = "https://api.deepseek.com/v1";

    /** 模型名：deepseek-chat、qwen-plus、glm-4-flash …… */
    private String model = "deepseek-chat";

    /** 随机性，0 最稳，1 最发散 */
    private Double temperature = 0.7;

    /** 单次回答的最大 token 数，控制成本 */
    private Integer maxTokens = 800;

    /** 单次请求超时（秒） */
    private Integer timeoutSeconds = 60;

    /** 关键词检索命中几篇文章作为上下文 */
    private Integer retrieveLimit = 3;

    /** 留言审批相关配置 */
    private Audit audit = new Audit();

    @Data
    public static class Audit {

        /** 留言提交后是否交给 AI 先审一道 */
        private boolean enabled = true;

        /**
         * 置信度门槛。AI 说通过/拒绝但把握不到这个数，就不自动执行，转人工。
         * 0.75 是试出来的：再低会把擦边留言直接放出去，再高则大量转人工。
         */
        private double confidence = 0.75;

        /** 审批用哪个模型，留空则用 ai.model。可以填个更便宜的 */
        private String model = "";

        /** 审批超时（秒）。超时就转人工，不让访客干等 */
        private int timeoutSeconds = 10;
    }

    /**
     * 是否具备真正调用大模型的条件。
     * 开关开着且 Key 不为空才算就绪，避免 Key 忘填时直接抛 401。
     */
    public boolean isReady() {
        return enabled && apiKey != null && !apiKey.isBlank();
    }

    /** 审批实际用哪个模型 */
    public String auditModel() {
        return (audit.getModel() == null || audit.getModel().isBlank()) ? model : audit.getModel();
    }
}
