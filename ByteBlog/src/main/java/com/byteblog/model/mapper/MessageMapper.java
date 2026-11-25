package com.byteblog.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.byteblog.model.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留言板实体类Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
