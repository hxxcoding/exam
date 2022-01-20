package com.yf.exam.modules.qu.dto;

import com.yf.exam.core.api.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author Xiaoxiao Hu
 * @date 2022/1/20 18:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PPTSlideDTO extends BaseDTO {

    @ApiModelProperty(value = "幻灯片页面索引", required=true)
    Integer pos;

    @ApiModelProperty(value = "段落内容", required=true)
    String slideName;
}
