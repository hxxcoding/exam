package com.yf.exam.modules.paper.dto.ext;

import com.alibaba.fastjson.annotation.JSONField;
import com.yf.exam.modules.paper.dto.PaperQuDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * <p>
 * 在线考试时试卷考题请求类, 忽略了PaperQuDTO中的 "actualScore", "isRight" 字段
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2021-01-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="试卷考题", description="试卷考题")
public class OnlinePaperQuDTO extends PaperQuDTO {

    @ApiModelProperty(value = "实际得分(主观题)", required=true)
    @JSONField(serialize = false)
    private Integer actualScore;

    @ApiModelProperty(value = "是否答对", required=true)
    @JSONField(serialize = false)
    private Boolean isRight;
}
