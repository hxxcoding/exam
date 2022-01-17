package com.yf.exam.modules.qu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ApiModel(value="word文件段落", description="word文件段落")
public class WordParagraphDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "段落索引", required=true)
    String pos;

    @ApiModelProperty(value = "段落内容", required=true)
    String paragraph;
}
