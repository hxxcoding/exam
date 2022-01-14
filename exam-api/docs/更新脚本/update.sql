ALTER TABLE `el_qu` ADD COLUMN `image` varchar(500) NOT NULL DEFAULT '' COMMENT '题目图片' AFTER `level`;
ALTER TABLE `el_qu_answer` ADD COLUMN `image` varchar(500) NOT NULL DEFAULT '' COMMENT '选项图片' AFTER `is_right`;
ALTER TABLE `el_exam` ADD COLUMN `try_limit` tinyint(3) NOT NULL DEFAULT '0' COMMENT '是否限次' AFTER `end_time`;
ALTER TABLE `el_exam` ADD COLUMN `limit_times` int(11) DEFAULT NULL COMMENT '限制次数' AFTER `try_limit`;
ALTER TABLE `sys_config` ADD COLUMN `site_notice` varchar(2000) NOT NULL COMMENT '系统通知' AFTER `back_logo`;

ALTER TABLE `el_exam_repo`
ADD COLUMN `word_count` int(11) NOT NULL DEFAULT 0 COMMENT 'word操作题数量 QuType=10' AFTER `blank_score`,
ADD COLUMN `word_score` int(11) NOT NULL DEFAULT 0 COMMENT 'word操作题分数' AFTER `word_count`,
ADD COLUMN `excel_count` int(11) NOT NULL DEFAULT 0 COMMENT 'excel操作题数量 QuType=11' AFTER `word_score`,
ADD COLUMN `excel_score` int(11) NOT NULL DEFAULT 0 COMMENT 'excel操作题分数' AFTER `excel_count`,
ADD COLUMN `ppt_count` int(11) NOT NULL DEFAULT 0 COMMENT 'ppt操作题数量 QuType=12' AFTER `excel_score`,
ADD COLUMN `ppt_score` int(11) NOT NULL DEFAULT 0 COMMENT 'ppt操作题分数' AFTER `ppt_count`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`id`) USING BTREE;

ALTER TABLE `el_repo`
ADD COLUMN `word_count` int(11) NOT NULL DEFAULT 0 COMMENT 'word操作题数量' AFTER `blank_count`,
ADD COLUMN `excel_count` int(11) NOT NULL DEFAULT 0 COMMENT 'excel操作题数量' AFTER `word_count`,
ADD COLUMN `ppt_count` int(11) NOT NULL DEFAULT 0 COMMENT 'ppt操作题数量' AFTER `excel_count`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`id`) USING BTREE;

ALTER TABLE `yf_exam_lite`.`el_qu`
    CHANGE COLUMN `remark` `answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '填空题/office题答案' AFTER `content`,
    MODIFY COLUMN `analysis` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '整题解析' AFTER `answer`;