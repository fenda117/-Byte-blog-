package com.byteblog.agent.state;

import dev.langchain4j.data.message.ChatMessage;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 博客 Agent 的全局共享状态。
 * 所有节点（route_intent、organize、knowledge_qa、weather、comment_moderate 等）
 * 都读写这一个 State 实例。
 * 当前只定义“骨架阶段”必需的字段，后续板块会按需扩充。
 * 字段以常量形式暴露 key，避免节点里手写字符串导致拼写错误。
 */
public class ByteBlogAgentState extends AgentState {
    /** 对话消息历史。骨架阶段先不用，后续接入 LangChain4j 时再填充。 */
    public static final String MESSAGES = "messages";
    /** 当前意图：ORGANIZE / QA / WEATHER / COMMENT_REVIEW / UNKNOWN。 */
    public static final String INTENT = "intent";
    /** 会话或审批线程标识。HITL 中断恢复时依赖它定位 checkpoint。 */
    public static final String THREAD_ID = "threadId";

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            MESSAGES, Channels.appender(ArrayList::new)  // 累加，不覆盖
    );
    /**
     * AgentState 的构造器必须接收一个 Map 作为初始数据。
     * LangGraph4j 在创建 State 实例时会调用这个构造器。
     */
    public ByteBlogAgentState(Map<String, Object> initData) {
        super(initData);
    }
    public String intent() {
        return this.<String>value(INTENT).orElse("UNKNOWN");
    }
    public String threadId() {
        return this.<String>value(THREAD_ID).orElse("");
    }
    public List<ChatMessage> messages() {
        return this.<List<ChatMessage>>value(MESSAGES).orElse(List.of());
    }

}
