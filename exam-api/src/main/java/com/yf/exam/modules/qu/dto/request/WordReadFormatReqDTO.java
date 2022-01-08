package com.yf.exam.modules.qu.dto.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="word格式读取", description="word格式读取")
public class WordReadFormatReqDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件url", required = true)
    String url;

    @ApiModelProperty(value = "方法", required = true)
    String method;

    @ApiModelProperty(value = "段落", required=true)
    Integer pos;

}
