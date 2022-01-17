package com.yf.exam.modules.qu.dto.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="office文件格式读取", description="office文件格式读取")
public class ReadFormatReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件url", required = true)
    String url;

    @ApiModelProperty(value = "方法", required = true)
    String method;

    @ApiModelProperty(value = "得分点", required=true)
    String pos;

}
