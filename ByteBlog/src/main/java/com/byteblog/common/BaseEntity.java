package com.byteblog.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公共实体类
 * id主键自增，创建时间，更新时间自动填充
 * 删除方式：带逻辑性
 */
@Data
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    // 主键自增
    @TableId(type = IdType.AUTO)
    private Long id;
    // 插入时自动填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    // 插入更新时都自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    // 逻辑删除，删数据变成 update deleted=1
    @TableLogic
    private Integer deleted;
}
