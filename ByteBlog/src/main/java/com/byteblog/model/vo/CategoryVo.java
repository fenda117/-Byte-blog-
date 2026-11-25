package com.byteblog.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 种类分类 响应体
 */
@Data
public class CategoryVo {


    private Long id;

    private String name;

    private Integer sort;

    // 统计分类下的数量
    private Long articleCount;


}
