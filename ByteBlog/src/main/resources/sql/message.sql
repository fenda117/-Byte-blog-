DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
                           `id`          BIGINT       NOT NULL AUTO_INCREMENT,
                           `name`        VARCHAR(50)  CHARACTER SET utf8mb4 NOT NULL DEFAULT '匿名访客' COMMENT '留言人昵称',
                           `avatar`      VARCHAR(500) DEFAULT NULL COMMENT '留言人头像URL',
                           `content`     VARCHAR(500) NOT NULL COMMENT '留言内容',
                           `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父留言ID，0为顶层',
                           `reply_name`  VARCHAR(50)  DEFAULT NULL COMMENT '被回复人的昵称',
                           `is_top`      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否置顶：0否 1是',
                           `ip_location` VARCHAR(100) DEFAULT NULL COMMENT 'IP属地',
                           `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '0待审核 1已通过',
                           `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           KEY `idx_status` (`status`),
                           KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

-- 初始化数据
INSERT INTO `message` (`name`, `avatar`, `content`, `parent_id`, `reply_name`, `is_top`, `ip_location`, `status`) VALUES
('小张', 'https://api.dicebear.com/7.x/notionists/svg?seed=小张', '博客做得真好看，封面和主题切换都很丝滑！', 0, NULL, 1, '江苏省南京市', 1),
('李四', 'https://api.dicebear.com/7.x/notionists/svg?seed=李四', '同感，博主太强了！', 1, '小张', 0, '浙江省杭州市', 1),
('面试官老王', 'https://api.dicebear.com/7.x/notionists/svg?seed=老王', 'AI 智能体模块很有意思，期待你的面试表现。', 0, NULL, 0, '北京市', 1);