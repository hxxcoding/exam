package com.yf.exam.modules.exam.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.yf.exam.modules.exam.dto.ExamDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *  在线考试预览响应类
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022/1/22 14:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="在线考试预览响应类", description="在线考试预览响应类")
public class ExamPreviewRespDTO extends ExamDTO {

    @JSONField(serialize = false)
    @ApiModelProperty(value = "正式考试需要的考试密码", required=true)
    private String password;

    @ApiModelProperty(value = "是否已经开始", required=true)
    private Boolean isStart;

    @ApiModelProperty(value = "考场座位", required=true)
    private String seat;

}