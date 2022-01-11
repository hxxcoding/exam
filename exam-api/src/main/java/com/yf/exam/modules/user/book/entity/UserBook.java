package com.yf.exam.modules.user.book.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/**
* <p>
* 错题本实体类
* </p>
*
* @author 聪明笨狗
* @since 2020-05-27 17:56
*/
@Data
@TableName("el_user_book")
public class UserBook extends Model<UserBook> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 考试ID
     */
    @TableField("exam_id")
    private String examId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 题目ID
     */
    @TableField("qu_id")
    private String quId;

    /**
     * 加入时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 最近错误时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 错误时间
     */
    @TableField("wrong_count")
    private Integer wrongCount;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 错题序号
     */
    private Integer sort;
    
}
