package com.byteblog.common;

import lombok.Getter;

/**
 * 统一结果集枚举类
 * 2xx-成功
 * 4xx-客户端错误
 * 5xx-服务器错误
 * 1xxx起-自定义业务码
 */
@Getter
public enum ResultCode {
    //========通用========
    SUCCESS(200,"操作成功"),
    BAD_REQUEST(400,"请求参数错误"),
    NOT_FOUND(404,"资源不存在"),
    INTERNAL_ERROR(500,"系统内部异常"),
    //========业务逻辑层========
    REPEATED_NAME(4001,"该分类名称已存在，请换一个吧"),
    CATEGORY_NOT_FOUND(4002,"分类不存在"),
    CATEGORY_REPETITION(4003,"重复分类"),
    NOT_FOUND_PROJECT(4004,"还没有项目哦，新建一个吧"),
    MESSAGE_NOT_FOUND(4005,"回复的留言不存在或已被删除"),
    MUSIC_NOT_FOUND(4005,"音乐不存在或已被删除"),
    ARTICLE_NOT_FOUND(4006,"文章不存在或已被删除"),
    USERNAME_OR_PASSWORD_ERROR(4007,"用户名或密码错误");
    private final int code;
    private final String msg;
    // 有参构造函数
    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
