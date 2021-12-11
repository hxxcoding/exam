
# TODO
* deal 考试页面水印
* deal 个人信息显示所属部门
* deal 开始考试全屏 bug 上传文件时退出全屏
* 设置考试/练习模式 考试模式没有'我的成绩'权限
* excel 上传用户
* 退出考试页面但未提交试卷 到时间自动交卷（计算分数）
* ？考试次数限制 tryCount >  考试可以重复做吗？
* 考试记录默认查询管理员所属部门的成员成绩
* 考试结束 自动提交已经保存的题目 如果用户没有点击交卷就退出考试 该怎么办？
* 限制考试机器 (IP)
* 操作日志 https://exam.yfhl.net/#/admin/sys/admin/sys/log
* 权限过滤 ShiroConfig.java
* 学生导入 注册时需要选择部门 注册之前要和对数据库是否存在该用户 用户管理增加部门
* 多图片多文件支持
* 错题功能是否需要删除
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

# 项目演示
开源版本：https://lite.yfhl.net  
管理账号：admin/admin ~~~~学员账号：person/person   

商业版本：https://lp-exam.yfhl.net

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
难易程度：普通、困难。    

## 便捷组卷：题库组卷    
题库组卷：指定题库、分数、数量；题目、选项随机排序、杜绝作弊    


# 环境要求
JDK 1.8+  [点此下载](https://cdn.yfhl.net/java-win/jdk-8u181-windows-x64.exe)        
Mysql5.7+  [点此下载](https://cdn.yfhl.net/java-win/mysql-installer-community-5.7.31.0.msi)  
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION    _BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION



# 快速运行
1、自行安装MySQL数据库（版本最好大于5.7），将`安装资源中`的`数据库初始化.sql`导入到安装好的数据库    
2、安装Java环境，要求JDK版本大于1.7    
3、请修改外置配置文件：application.properties 改成您自己的MySQL配置、上传文件的路径
4、Windows通过start.bat运行，Linux运行start.sh运行    
5、如果无意外，可通过：http://localhost:8101 访问到项目了    
6、管理员账号密码：admin/admin 学员账号：person/person    


# 其它支持

网站：https://lp-exam.yfhl.net 

QQ交流群：865330294 

杨经理：     
    邮箱：626264481@qq.com   
    手机：18710213152 

郭经理：     
    邮箱：835487894@qq.com   
    手机：18603038204




![输入图片说明](https://images.gitee.com/uploads/images/2020/1207/173238_e6c22c67_2189748.jpeg "17-32-10.jpg")
![主界面](https://images.gitee.com/uploads/images/2020/1019/182239_4a87af30_2189748.jpeg "222.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2020/1019/182532_04c42741_2189748.jpeg "444.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2020/1019/182543_44dcc2d7_2189748.jpeg "555.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2020/1019/182551_4d404492_2189748.jpeg "666.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2020/1019/183109_fdc30de8_2189748.jpeg "777.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2020/1019/183117_30b44530_2189748.jpeg "888.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2020/1019/183023_2f3baeb9_2189748.jpeg "999.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2020/1019/183032_f5016335_2189748.jpeg "1010.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2020/1019/183040_38fd74ed_2189748.jpeg "1111.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2020/1019/183047_a31619cd_2189748.jpeg "1212.jpg")

 
