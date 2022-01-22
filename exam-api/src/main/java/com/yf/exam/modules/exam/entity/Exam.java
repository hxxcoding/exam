package com.yf.exam.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import java.util.Date;

/**
* <p>
* 考试实体类
* </p>
*
* @author 聪明笨狗
* @since 2020-07-25 16:18
*/
@Data
@TableName("el_exam")
public class Exam extends Model<Exam> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 考试名称
     */
    private String title;

    /**
     * 考试描述
     */
    private String content;

    /**
     * 1公开2部门3定员
     */
    @TableField("open_type")
    private Integer openType;

    /**
     * 组题方式1题库,2指定
     */
    @TableField("join_type")
    private Integer joinType;

    /**
     * 考试类型 0模拟练习,1正式考试
     */
    @TableField("exam_type")
    private Integer examType;

    /**
     * 正式考试需要的考试密码
     */
    private String password;

    /**
     * 难度:0不限,1普通,2较难
     */
    private Integer level;

    /**
     * 考试状态
     */
    private Integer state;

    /**
     * 是否限时
     */
    @TableField("time_limit")
    private Boolean timeLimit;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private Date endTime;

    /**
     * 是否限次
     */
    @TableField("try_limit")
    private Boolean tryLimit;

    /**
     * 限制次数
     */
    @TableField("limit_times")
    private Integer limitTimes;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 总分数
     */
    @TableField("total_score")
    private Integer totalScore;

    /**
     * 总时长（分钟）
     */
    @TableField("total_time")
    private Integer totalTime;

    /**
     * 及格分数
     */
    @TableField("qualify_score")
    private Integer qualifyScore;
    
}
