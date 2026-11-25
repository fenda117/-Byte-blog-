package com.byteblog.model.vo;

import lombok.Data;

/**
 *  文章存档 响应体
 */
@Data
public class ArchiveVo {
    // 月份
    private String month;
    // 计数
    private Long count;
}
