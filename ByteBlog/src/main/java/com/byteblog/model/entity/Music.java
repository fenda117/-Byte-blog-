package com.byteblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 音乐播放器实体类
 * 实现了序列化
 */
@Data
@TableName("music")
public class Music implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 歌曲名
    private String title;
    // 作者
    private String author;
    // 封面图
    private String coverUrl;
    // 播放地址
    private String audioUrl;
    // 播放时长
    private Integer duration;
    // 排序
    private Integer sort;

    private LocalDateTime createTime;

}
