package com.yf.exam.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
* <p>
* 考试题库实体类
* </p>
*
* @author 聪明笨狗
* @since 2020-09-05 11:14
*/
@Data
@TableName("el_exam_repo")
public class ExamRepo extends Model<ExamRepo> {

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
    * 题库ID
    */
    @TableField("repo_id")
    private String repoId;
    
    /**
    * 单选题数量
    */
    @TableField("radio_count")
    private Integer radioCount;
    
    /**
    * 单选题分数
    */
    @TableField("radio_score")
    private Integer radioScore;
    
    /**
    * 多选题数量
    */
    @TableField("multi_count")
    private Integer multiCount;
    
    /**
    * 多选题分数
    */
    @TableField("multi_score")
    private Integer multiScore;
    
    /**
    * 判断题数量
    */
    @TableField("judge_count")
    private Integer judgeCount;
    
    /**
    * 判断题分数
    */
    @TableField("judge_score")
    private Integer judgeScore;

    /**
    * 简答题数量
    */
    @TableField("saq_count")
    private Integer saqCount;

    /**
    * 简答题分数
    */
    @TableField("saq_score")
    private Integer saqScore;

    /**
    * 填空题数量
    */
    @TableField("blank_count")
    private Integer blankCount;

    /**
    * 填空题分数
    */
    @TableField("blank_score")
    private Integer blankScore;

    /**
    * word操作题数量
    */
    @TableField("word_count")
    private Integer wordCount;

    /**
    * word操作题分数
    */
    @TableField("word_score")
    private Integer wordScore;

    /**
    * excel操作题数量
    */
    @TableField("excel_count")
    private Integer excelCount;

    /**
    * excel操作题分数
    */
    @TableField("excel_score")
    private Integer excelScore;

    /**
    * ppt操作题数量
    */
    @TableField("ppt_count")
    private Integer pptCount;

    /**
    * ppt操作题分数
    */
    @TableField("ppt_score")
    private Integer pptScore;
    
}
