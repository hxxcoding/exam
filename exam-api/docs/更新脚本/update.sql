ALTER TABLE `el_qu` ADD COLUMN `image` varchar(500) NOT NULL DEFAULT '' COMMENT '题目图片' AFTER `level`;
ALTER TABLE `el_qu_answer` ADD COLUMN `image` varchar(500) NOT NULL DEFAULT '' COMMENT '选项图片' AFTER `is_right`;
ALTER TABLE `el_exam` ADD COLUMN `try_limit` tinyint(3) NOT NULL DEFAULT '0' COMMENT '是否限次' AFTER `end_time`;
ALTER TABLE `el_exam` ADD COLUMN `limit_times` int(11) DEFAULT NULL COMMENT '限制次数' AFTER `try_limit`;
ALTER TABLE `sys_config` ADD COLUMN `site_notice` varchar(2000) NOT NULL COMMENT '系统通知' AFTER `back_logo`;