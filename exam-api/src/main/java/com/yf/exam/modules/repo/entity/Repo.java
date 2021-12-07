package com.yf.exam.modules.repo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/**
* <p>
* 题库实体类
* </p>
*
* @author 聪明笨狗
* @since 2020-05-25 13:23
*/
@Data
@TableName("el_repo")
public class Repo extends Model<Repo> {

    private static final long serialVersionUID = 1L;

    /**
     * 题库ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 题库编号
     */
    private String code;

    /**
     * 题库名称
     */
    private String title;

    /**
     * 单选数量
     */
    @TableField("radio_count")
    private Integer radioCount;

    /**
     * 多选数量
     */
    @TableField("multi_count")
    private Integer multiCount;

    /**
     * 判断数量
     */
    @TableField("judge_count")
    private Integer judgeCount;

    /**
     * 操作数量
     */
    @TableField("saq_count")
    private Integer saqCount;

    /**
     * 填空数量
     */
    @TableField("blank_count")
    private Integer blankCount;


    /**
     * 题库备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
    
}
