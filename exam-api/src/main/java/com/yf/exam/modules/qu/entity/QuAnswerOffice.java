package com.yf.exam.modules.qu.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("el_qu_answer_office")
@Accessors(chain = true)
public class QuAnswerOffice extends Model<QuAnswerOffice> {
    private static final long serialVersionUID = 1L;

    /**
     * 答案ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 问题ID
     */
    @TableField("qu_id")
    private String quId;

    /**
     * 判分函数
     */
    private String method;

    /**
     * 段落号
     */
    private Integer pos;

    /**
     * 答案
     */
    private String answer;

    /**
     * 分数
     */
    private Integer score;

}