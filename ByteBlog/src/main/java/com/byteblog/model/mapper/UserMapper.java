package com.byteblog.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.byteblog.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台登录用户Mapper
 */
@Mapper
public interface UserMapper  extends BaseMapper<User> {
}
