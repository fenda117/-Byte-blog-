package com.byteblog.model.service;

/**
 * 留言 AI 审批。
 *
 * 访客提交留言后，先让大模型判断一次：能明确判定的直接出结果，
 * 判不了的（置信度不够、格式解析失败、模型超时、压根没配 Key）一律转人工。
 *
 * 这样做的理由：留言板不能因为 AI 挂了就不能留言，也不能让 AI 一个人说了算。
 */
public interface MessageAuditService {

    /**
     * 异步审批一条留言。
     * 提交接口立刻返回，审批在独立线程池里跑，访客不用等。
     *
     * @param messageId 留言 id
     */
    void auditAsync(Long messageId);
}
