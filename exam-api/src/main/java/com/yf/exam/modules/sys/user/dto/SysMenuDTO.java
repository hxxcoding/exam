package com.yf.exam.modules.sys.user.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统菜单传输类
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022-01-11
 */
@Data
@ApiModel(value="菜单信息", description="菜单信息")
public class SysMenuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单ID", required = true)
    private String id;

    @ApiModelProperty(value = "菜单名称", required = true)
    private String name;

    @ApiModelProperty(value = "父菜单ID", required = true)
    private String parentId;

    @ApiModelProperty(value = "菜单类型", required = true)
    private String type;

    @ApiModelProperty(value = "路由地址", required = true)
    private String path;

    @ApiModelProperty(value = "组件路径", required = true)
    private String component;

    @ApiModelProperty(value = "路由参数", required = true)
    private String query;

    @ApiModelProperty(value = "权限标识", required = true)
    private String perms;

    @ApiModelProperty(value = "排序", required = true)
    private Integer sort;

    @ApiModelProperty(value = "是否外链", required = true)
    private Boolean isFrame;

    @ApiModelProperty(value = "是否禁用缓存", required = true)
    private Boolean noCache;

    @ApiModelProperty(value = "是否可视", required = true)
    private Boolean isVisible;

    @ApiModelProperty(value = "是否可用", required = true)
    private Boolean isAvailable;

    @ApiModelProperty(value = "菜单图标", required = true)
    private String icon;

    @ApiModelProperty(value = "备注", required = true)
    private String remark;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date createTime;

    @ApiModelProperty(value = "更新时间", required = true)
    private Date updateTime;
}
