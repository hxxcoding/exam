package com.yf.exam.modules.qu.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ApiModel(value="office文件解析", description="office文件解析")
public class AnalyzeWordRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "段落索引", required=true)
    Integer index;

    @ApiModelProperty(value = "段落内容", required=true)
    String paragraph;
}
