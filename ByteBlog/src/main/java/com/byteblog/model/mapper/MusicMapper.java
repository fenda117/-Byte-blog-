package com.byteblog.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.byteblog.model.entity.Music;
import org.apache.ibatis.annotations.Mapper;

/**
 * 音乐播放Mapper
 */
@Mapper
public interface MusicMapper extends BaseMapper<Music> {
}
