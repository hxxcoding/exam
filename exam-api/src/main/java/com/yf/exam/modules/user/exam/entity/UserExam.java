package com.yf.exam.modules.user.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import java.util.Date;

/**
* <p>
* 考试记录实体类
* </p>
*
* @author 聪明笨狗
* @since 2020-09-21 15:13
*/
@Data
@TableName("el_user_exam")
public class UserExam extends Model<UserExam> {

    private static final long serialVersionUID = 1L;
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    /**
    * 用户ID
    */
    @TableField("user_id")
    private String userId;
    
    /**
    * 考试ID
    */
    @TableField("exam_id")
    private String examId;
    
    /**
    * 考试次数
    */
    @TableField("try_count")
    private Integer tryCount;
    
    /**
    * 最高分数
    */
    @TableField("max_score")
    private Integer maxScore;
    
    /**
    * 是否通过
    */
    private Boolean passed;
    
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
    
}
