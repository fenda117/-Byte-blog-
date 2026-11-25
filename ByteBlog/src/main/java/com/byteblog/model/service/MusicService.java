package com.byteblog.model.service;

import com.byteblog.model.dto.MusicDto;
import com.byteblog.model.entity.Music;

import java.util.List;

/**
 * 音乐播放器业务接口
 */
public interface MusicService {

    List<Music> listMusic();

    Music createMusic(MusicDto dto);

    Music updateMusic(Long id, MusicDto dto);

    void deleteMusic(Long id);


}
