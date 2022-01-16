package com.yf.exam.modules.paper.dto.ext;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author Xiaoxiao Hu
 * @date 2022/1/16 16:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="试卷题目详情类", description="试卷题目详情类")
public class OnlinePaperQuDetailDTO extends PaperQuDetailDTO{

    @ApiModelProperty(value = "实际得分(主观题)", required=true)
    @JSONField(serialize = false)
    private Integer actualScore;

    @ApiModelProperty(value = "是否答对", required=true)
    @JSONField(serialize = false)
    private Boolean isRight;
}
