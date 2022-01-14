package com.yf.exam.modules.qu.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
* <p>
* 问题题目实体类
* </p>
*
* @author 聪明笨狗
* @since 2020-05-25 13:23
*/
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("el_qu")
public class Qu extends Model<Qu> {

    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 题目类型
     */
    @TableField("qu_type")
    private Integer quType;

    /**
     * 1普通,2较难
     */
    private Integer level;

    /**
     * 题目图片
     */
    private String image;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题目答案(填空题 / office题)
     */
    private String answer;

    /**
     * 整题解析
     */
    private String analysis;

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
