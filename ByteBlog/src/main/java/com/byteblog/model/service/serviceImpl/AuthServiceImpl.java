package com.byteblog.model.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.byteblog.common.ResultCode;
import com.byteblog.exception.BusinessException;
import com.byteblog.model.dto.LogionDto;
import com.byteblog.model.entity.User;
import com.byteblog.model.mapper.UserMapper;
import com.byteblog.model.service.AuthService;
import com.byteblog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 后台登录用户业务实体类
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    // 注入mapper
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();


    @Override
    public String login(LogionDto dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        return jwtUtil.generate(user.getUsername());
    }
}

