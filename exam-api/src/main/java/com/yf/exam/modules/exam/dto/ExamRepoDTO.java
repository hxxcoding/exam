package com.yf.exam.modules.exam.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
* <p>
* 考试题库数据传输类
* </p>
*
* @author 聪明笨狗
* @since 2020-09-05 11:14
*/
@Data
@ApiModel(value="考试题库", description="考试题库")
public class ExamRepoDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    
    @ApiModelProperty(value = "ID", required=true)
    private String id;
    
    @ApiModelProperty(value = "考试ID", required=true)
    private String examId;
    
    @ApiModelProperty(value = "题库ID", required=true)
    private String repoId;
    
    @ApiModelProperty(value = "单选题数量", required=true)
    private Integer radioCount;
    
    @ApiModelProperty(value = "单选题分数", required=true)
    private Integer radioScore;
    
    @ApiModelProperty(value = "多选题数量", required=true)
    private Integer multiCount;
    
    @ApiModelProperty(value = "多选题分数", required=true)
    private Integer multiScore;
    
    @ApiModelProperty(value = "判断题数量", required=true)
    private Integer judgeCount;
    
    @ApiModelProperty(value = "判断题分数", required=true)
    private Integer judgeScore;

    @ApiModelProperty(value = "操作题数量", required=true)
    private Integer saqCount;

    @ApiModelProperty(value = "操作题分数", required=true)
    private Integer saqScore;

    @ApiModelProperty(value = "填空题数量", required=true)
    private Integer blankCount;

    @ApiModelProperty(value = "填空题分数", required=true)
    private Integer blankScore;

    @ApiModelProperty(value = "word操作题数量", required=true)
    private Integer wordCount;

    @ApiModelProperty(value = "word操作题分数", required=true)
    private Integer wordScore;

    @ApiModelProperty(value = "excel操作题数量", required=true)
    private Integer excelCount;

    @ApiModelProperty(value = "excel操作题分数", required=true)
    private Integer excelScore;

    @ApiModelProperty(value = "ppt操作题数量", required=true)
    private Integer pptCount;

    @ApiModelProperty(value = "ppt操作题分数", required=true)
    private Integer pptScore;
    
}
