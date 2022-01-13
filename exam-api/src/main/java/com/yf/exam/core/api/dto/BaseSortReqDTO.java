package com.yf.exam.core.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 排序请求类
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2021-01-13
 */
@Data
@ApiModel(value="排序请求类", description="排序请求类")
public class BaseSortReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类ID")
    private String id;

    @ApiModelProperty(value = "排序，0下降，1上升")
    private Integer sort;
}
