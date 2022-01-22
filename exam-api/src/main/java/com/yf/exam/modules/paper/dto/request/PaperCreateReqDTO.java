package com.yf.exam.modules.paper.dto.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.yf.exam.core.api.dto.BaseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bool
 */
@Data
@ApiModel(value="试卷创建请求类", description="试卷创建请求类")
public class PaperCreateReqDTO extends BaseDTO {

    @JSONField(serialize = false)
    private String userId;

    @ApiModelProperty(value = "考试ID", required=true)
    private String examId;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "考试密码", required=true)
    private String password;

}
