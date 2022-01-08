package com.yf.exam.modules.qu.dto.response;

import com.yf.exam.modules.qu.dto.WordParagraphDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value="word文件分析", description="word文件分析")
public class WordAnalyzeRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "方法", required=true)
    List<String> methods;

    @ApiModelProperty(value = "段落", required=true)
    List<WordParagraphDTO> paragraphs;
}
