package com.yf.exam.modules.paper.dto.request;

import com.yf.exam.core.api.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bool
 */
@Data
@ApiModel(value="查找试卷题目详情请求类", description="查找试卷题目详情请求类")
public class PaperQuQueryDTO extends BaseDTO {

    @ApiModelProperty(value = "题目类型", required = true)
    private Integer quType;

    @ApiModelProperty(value = "试卷ID", required=true)
    private String paperId;

    @ApiModelProperty(value = "题目ID", required=true)
    private String quId;

}
