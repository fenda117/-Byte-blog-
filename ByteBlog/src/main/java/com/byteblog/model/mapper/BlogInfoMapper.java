package com.byteblog.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.byteblog.model.entity.BlogInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 个人信息实体类Mapper
 */
@Mapper
public interface BlogInfoMapper extends BaseMapper<BlogInfo> {

}
