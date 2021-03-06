package com.yf.exam.modules.exam.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.yf.exam.modules.exam.dto.ExamDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* <p>
* 考试分页响应类
* </p>
*
* @author 聪明笨狗
* @since 2020-07-25 16:18
*/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="在线考试分页响应类", description="在线考试分页响应类")
public class ExamOnlineRespDTO extends ExamDTO {

    @JSONField(serialize = false)
    @ApiModelProperty(value = "正式考试需要的考试密码", required=true)
    private String password;

}
