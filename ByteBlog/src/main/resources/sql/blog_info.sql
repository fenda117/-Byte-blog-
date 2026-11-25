DROP TABLE IF EXISTS `blog_info`;
CREATE TABLE `blog_info` (
                             `id`           BIGINT       NOT NULL COMMENT '主键，恒为1',
                             `name`         VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '昵称',
                             `avatar_url`   VARCHAR(500) DEFAULT NULL COMMENT '头像地址',
                             `signature`    VARCHAR(200) DEFAULT NULL COMMENT '一句话签名',
                             `about_me`     TEXT         COMMENT '关于我',
                             `social_links` JSON         COMMENT '社交链接 JSON 对象',
                             `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博主信息表';

-- 初始化数据
INSERT INTO `blog_info` (`id`, `name`, `signature`, `social_links`) VALUES
    (1, '你的名字', 'Java 后端 / Vue3 全栈学习者 · 正在求职',
     JSON_OBJECT('GitHub','https://github.com/yourname','邮箱','yourname@example.com','QQ','123456789'));