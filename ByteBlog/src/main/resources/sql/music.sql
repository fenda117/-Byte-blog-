DROP TABLE IF EXISTS `music`;
CREATE TABLE `music` (
                         `id`         BIGINT       NOT NULL AUTO_INCREMENT,
                         `title`      VARCHAR(200) NOT NULL COMMENT '歌名',
                         `author`     VARCHAR(200) DEFAULT NULL COMMENT '歌手/作者',
                         `cover_url`  VARCHAR(500) DEFAULT NULL COMMENT '封面图',
                         `audio_url`  VARCHAR(500) DEFAULT NULL COMMENT '音频地址',
                         `duration`   INT          DEFAULT 0 COMMENT '时长(秒)',
                         `sort`       INT          NOT NULL DEFAULT 0 COMMENT '排序',
                         `create_time` DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='音乐表';

-- 初始化数据
INSERT INTO `music` (`title`, `author`, `duration`, `sort`) VALUES
                                                                ('晴天', '周杰伦', 269, 1),
                                                                ('夜曲', '周杰伦', 226, 2),
                                                                ('富士山下', '陈奕迅', 258, 3);