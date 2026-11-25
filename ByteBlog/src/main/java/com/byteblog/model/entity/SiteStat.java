package com.byteblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 统计网站界面实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("visit_stat")
public class SiteStat {

    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate statDate;
    private Integer pv;
    private Integer uv;

}
