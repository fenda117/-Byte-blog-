DROP TABLE IF EXISTS `visit_stat`;
CREATE TABLE `visit_stat` (
                              `id`        BIGINT NOT NULL AUTO_INCREMENT,
                              `stat_date` DATE   NOT NULL COMMENT '统计日期',
                              `pv`        INT    NOT NULL DEFAULT 0 COMMENT '页面浏览量',
                              `uv`        INT    NOT NULL DEFAULT 0 COMMENT '独立访客数',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客统计表';

-- 初始化数据
INSERT INTO `visit_stat` (`stat_date`, `pv`, `uv`) VALUES
                                                       (CURDATE() - INTERVAL 2 DAY, 1280, 320),
                                                       (CURDATE() - INTERVAL 1 DAY, 1120, 300),
                                                       (CURDATE(), 880, 210);