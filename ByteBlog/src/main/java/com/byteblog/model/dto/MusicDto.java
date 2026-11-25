package com.byteblog.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;



/**
 * 音乐播放器 请求体
 */
@Data
public class MusicDto {


    // 歌曲名
    @NotBlank(message = "歌曲名不能为空")
    private String title;
    // 作者
    @NotBlank(message = "歌手名不能为空")
    private String author;
    // 封面图
    private String coverUrl;
    // 播放地址
    private String audioUrl;
    // 播放时长
    private Integer duration;
    // 排序
    private Integer sort;


}
