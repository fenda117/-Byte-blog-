DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
                           `id`           BIGINT       NOT NULL AUTO_INCREMENT,
                           `title`        VARCHAR(200) NOT NULL COMMENT '标题',
                           `summary`      VARCHAR(500) DEFAULT NULL COMMENT '摘要',
                           `content_md`   LONGTEXT     COMMENT 'Markdown 原文',
                           `content_html` LONGTEXT     COMMENT '渲染后的 HTML',
                           `category_id`  BIGINT       DEFAULT NULL COMMENT '分类 id',
                           `tags`         VARCHAR(500) NOT NULL DEFAULT '' COMMENT '标签，逗号分隔',
                           `cover_url`    VARCHAR(500) DEFAULT NULL COMMENT '封面图',
                           `view_count`   INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
                           `featured`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否精选：0否 1是',
                           `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '0草稿 1发布',
                           `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删 1已删',
                           `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           KEY `idx_category` (`category_id`),
                           KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 初始化数据
INSERT INTO `article` (`title`, `summary`, `content_md`, `category_id`, `tags`, `view_count`, `featured`, `status`) VALUES
                                                                                                                        ('从零搭建 Spring Boot + Vue3 全栈项目', '记录搭建个人博客的全过程，包括技术选型、目录结构和踩坑记录。',
                                                                                                                         '# 从零搭建全栈项目\n\n## 技术选型\n- 后端：Spring Boot 3\n- 前端：Vue3\n\n正文内容……', 1, 'SpringBoot,Vue3,全栈', 128, 1, 1),
                                                                                                                        ('Spring AI 接入 DeepSeek 实现流式对话', '用 Spring AI 的 ChatClient 接入 DeepSeek，实现打字机效果的流式输出。',
                                                                                                                         '# Spring AI 流式对话\n\n正文内容……', 3, 'SpringAI,DeepSeek,SSE', 96, 0, 1);