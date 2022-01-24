package com.yf.exam.modules.exam.dto.request;

import com.yf.exam.core.api.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *  向在考考生发送信息的请求类
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022/1/23 20:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="发送信息请求类", description="发送信息请求类")
public class SendMsgReqDTO extends BaseDTO {

    @ApiModelProperty(value = "试卷ID", required=true)
    String paperId;

    @ApiModelProperty(value = "需要发送的信息", required=true)
    String message;
}
