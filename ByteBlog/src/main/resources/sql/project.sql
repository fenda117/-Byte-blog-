DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
                           `id`          BIGINT       NOT NULL AUTO_INCREMENT,
                           `name`        VARCHAR(200) NOT NULL COMMENT '项目名称',
                           `description` TEXT         NOT NULL COMMENT '项目介绍',
                           `tech_stack`  VARCHAR(500) NOT NULL DEFAULT '' COMMENT '技术栈，逗号分隔',
                           `github_url`  VARCHAR(500) DEFAULT NULL COMMENT 'GitHub 仓库',
                           `demo_url`    VARCHAR(500) DEFAULT NULL COMMENT '在线演示地址',
                           `cover_url`   VARCHAR(500) DEFAULT NULL COMMENT '封面图',
                           `featured`    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否精选：0否 1是',
                           `view_count`  INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
                           `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
                           `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
                           `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           KEY `idx_featured_sort` (`featured`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目展示表';

-- 初始化数据
INSERT INTO `project` (`name`, `description`, `tech_stack`, `github_url`, `featured`, `view_count`, `sort`) VALUES
                                                                                                                ('个人技术博客', '前后端分离的个人博客，内置 AI 智能体，支持三主题切换。',
                                                                                                                 'Java,SpringBoot,Vue3,MyBatis-Plus,MySQL', 'https://github.com/yourname/blog', 1, 350, 1),
                                                                                                                ('AI 智能问答助手', '基于 Spring AI 与 DeepSeek 的流式对话智能体，支持 Function Calling 工具调用。',
                                                                                                                 'Java,SpringAI,DeepSeek,SSE', 'https://github.com/yourname/ai-agent', 1, 280, 2);