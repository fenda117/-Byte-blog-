package com.byteblog.model.service.serviceImpl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.byteblog.common.ResultCode;
import com.byteblog.exception.BusinessException;
import com.byteblog.model.dto.MusicDto;
import com.byteblog.model.entity.Music;
import com.byteblog.model.mapper.MusicMapper;
import com.byteblog.model.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 音乐播放器业务层
 */
@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    //注入Mapper
    private final MusicMapper musicMapper;


    @Override
    public List<Music> listMusic() {
        return musicMapper.selectList(new LambdaQueryWrapper<Music>().orderByAsc(Music::getSort));
    }

    @Override
    public Music createMusic(MusicDto dto) {
        Music music = new Music();
        BeanUtils.copyProperties(dto,music);
        musicMapper.insert(music);
        return music;
    }

    @Override
    public Music updateMusic(Long id, MusicDto dto)  {
        Music music = musicMapper.selectById(id);
        if(music == null){
            throw new BusinessException(ResultCode.MUSIC_NOT_FOUND);

        }
        BeanUtils.copyProperties(dto,music);
        musicMapper.updateById(music);
        return music;
    }

    @Override
    public void deleteMusic(Long id) {
       if(musicMapper.selectById(id) == null){
           throw new BusinessException(ResultCode.MUSIC_NOT_FOUND);
       }
       musicMapper.deleteById(id);
    }
}
