package com.byteblog.model.service;

import com.byteblog.model.dto.LogionDto;

/**
 * 后台登录用户业务接口
 */
public interface AuthService {
    /**
     * 登录成功返回 token
     * */
    String login(LogionDto dto);
}
