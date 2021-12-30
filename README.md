
# TODO
* wrapper 缓存失效
* excel题型和题库设置成名称导入
* 数据库 UNIQUE 字段
* 设置考试/练习模式 考试模式没有'我的成绩'权限
* 退出考试页面但未提交试卷 到时间自动交卷（计算分数）
* ？考试次数限制 tryCount >  考试可以重复做吗？
* 考试记录默认查询管理员所属部门的成员成绩gg
* deal 考试结束 自动提交已经保存的题目 如果用户没有点击交卷就退出考试 该怎么办？
* 限制考试机器 (IP) 通过日志系统
* 操作日志 https://exam.yfhl.net/#/admin/sys/admin/sys/log
* 多图片多文件支持
* 错题功能是否需要删除
* deal 权限过滤 ShiroConfig.java
* deal 学生导入 注册时需要选择部门 注册之前要和对数据库是否存在该用户 用户管理增加部门
* deal 考试页面水印
* deal 个人信息显示所属部门
* deal 开始考试全屏 bug 上传文件时退出全屏
* deal excel 上传用户
* deal 限时考试按照 limit_time 和 end_time 以最短的为准倒计时 倒计时加上小时
* deal 我的成绩中查看试卷
* deal excel 导入导出试题 (setup sql_mode)
* deal 查询用户的答题记录
* deal 考试记录选择上级部门 包含 下级部门
* deal 考试题暂存
* deal 如已经开始过当前考试 但未交卷，开始考试后读取已经做过的题（试卷状态 el_paper: state）
* deal 答题页面文件如果不是图片显示下载链接
* deal 简答题 支持
* deal 文件上传，删除文件时删除本地文件和操作题answer
* deal 文件命名规范  name_time.xxx
* deal 填空题 支持
* deal Redis Config
* not complete 考试页面退出提醒

# bug list
* deal 按用户的部门查询 而不是按试卷的部门，试卷的部门数据库冗余
* deal 考试时间获取失败的bug
* deal 填空题不新增选项
* deal 填空题测试考试不显示的bug
* deal 考试结果中填空题答案不显示的bug
* deal 未答题题目数显示错误的bug
* deal 判断题和简答题的分数数据库记录错误
* deal 考试状态 限时考试结束后改为不限时考试后 考试状态已结束
* 回车刷新？
* 用户在未保存的情况下，重复上传文件导致中间文件不能被删除/先完成多图片多文件支持
  解决方案：设置缓存区 提交表单后再copy文件到正式的环境
* 考试页面文件重复上传 后端应做删除
* 题库管理 题目数量统计出错 
* 删除题目的同时 选项未删除
* 事务支持
* 试卷创建页面 表单提交 限时 / 次数bug

# 介绍
一款多角色在线培训考试系统，系统集成了用户管理、角色管理、部门管理、题库管理、试题管理、试题导入导出、考试管理、在线考试、错题训练等功能，考试流程完善。

# 技术栈
SpringBoot / Redis / Shiro / Vue / MySQL

# 产品功能

## 系统完善：完善的权限控制和用户系统
权限控制：基于Shiro和JWT开发的权限控制功能。    
用户系统：用户管理、部门管理、角色管理等。    

## 多角色：多角色支持    
考试端：学生学员角色、支持在线考试、查看分数、训练错题。    
管理端：题库管理、试题管理、考试管理、用户部门管理、查看考试情况等等。    

## 定员考试：考试权限定义    
完全公开：任何人员都可以参与考试。    
指定部门：只有选中部门的人员才可以看到考试。    

## 多题型：常用题型支持    
支持题型：单选题、多选题、判断题 + 判断题 + 操作题(not complete)。    

# 环境要求
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION

# 快速运行
1、自行安装MySQL数据库（版本5.7），将`安装资源中`的`数据库初始化.sql`导入到安装好的数据库    
2、安装Java环境，要求JDK版本1.8   
3、请修改外置配置文件：application.properties 上传文件的路径
4、添加环境变量 MYSQL_PASSWORD  
5、如果无意外，可通过：http://localhost:8101 访问到项目了    
6、管理员账号密码：admin/admin 学员账号：person/person