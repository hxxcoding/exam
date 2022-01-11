package com.yf.exam.modules.sys.user.dto.response;

import com.yf.exam.modules.sys.user.dto.ext.MetaDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * 菜单树结构响应类
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022-01-11
 */
@Data
@ApiModel(value="菜单树结构响应类", description="菜单树结构响应类")
public class RouterTreeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String path;

    private Boolean hidden;

    private String redirect;

    private String component;

    private String query;

    private Boolean alwaysShow;

    private MetaDTO meta;

    @ApiModelProperty(value = "子列表", required=true)
    private List<RouterTreeDTO> children;
}
