package com.yf.exam.modules.sys.user.dto.request;

import com.yf.exam.modules.sys.user.dto.SysRoleDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="身份菜单请求类", description="身份菜单请求类")
public class SysRoleMenuSaveReqDTO extends SysRoleDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单id列表", required=true)
    List<String> menuIds;
}
