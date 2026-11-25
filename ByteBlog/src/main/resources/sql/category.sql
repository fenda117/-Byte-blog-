DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
                            `id`         BIGINT      NOT NULL AUTO_INCREMENT,
                            `name`       VARCHAR(50) NOT NULL COMMENT '分类名',
                            `sort`       INT         NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
                            `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

-- 初始化数据
INSERT INTO `category` (`name`, `sort`) VALUES ('后端', 1), ('前端', 2), ('AI', 3), ('JVM', 4);