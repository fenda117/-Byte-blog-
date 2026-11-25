package com.byteblog.model.controller;


import com.byteblog.common.Result;
import com.byteblog.model.dto.MusicDto;
import com.byteblog.model.entity.Music;
import com.byteblog.model.service.MusicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 音乐播放器控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/music")
public class MusicController {
    // 注入service
    private final MusicService musicService;

    @GetMapping()
    public Result<List<Music>> list(){
        return Result.success(musicService.listMusic());
    }
    @PostMapping
    public Result<Music> create(@Valid @RequestBody MusicDto dto){
        Music music = musicService.createMusic(dto);
        return Result.success("添加成功",music);
    }
    @PutMapping("/{id}")
    public Result<Music> updateMusic(@PathVariable Long id, @Valid @RequestBody MusicDto dto){
        Music music = musicService.updateMusic(id, dto);
        return Result.success("更新成功",music);
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id){
        musicService.deleteMusic(id);
        return Result.success("删除成功",null);
    }
}
