package com.yf.exam.modules.qu.dto;

import com.yf.exam.core.api.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ApiModel(value="word文件段落", description="word文件段落")
public class WordParagraphDTO extends BaseDTO {

    @ApiModelProperty(value = "段落索引", required=true)
    String pos;

    @ApiModelProperty(value = "段落内容", required=true)
    String paragraph;
}
