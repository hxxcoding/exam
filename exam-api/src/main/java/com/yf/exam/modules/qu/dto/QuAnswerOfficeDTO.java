package com.yf.exam.modules.qu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Xiaoxiao Hu
 */

@Data
@ApiModel(value="候选答案", description="候选答案")
public class QuAnswerOfficeDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "答案ID", required=true)
    private String id;

    @ApiModelProperty(value = "问题ID", required=true)
    private String quId;

    @ApiModelProperty(value = "方法/函数", required=true)
    private String method;

    @ApiModelProperty(value = "定位参数", required=true)
    private String pos;

    @ApiModelProperty(value = "答案", required=true)
    private String answer;

    @ApiModelProperty(value = "分数", required=true)
    private Integer score;

    @ApiModelProperty(value = "备注", required=true)
    private String remark;

}
