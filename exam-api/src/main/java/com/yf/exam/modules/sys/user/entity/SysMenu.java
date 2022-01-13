package com.yf.exam.modules.sys.user.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_menu")
public class SysMenu extends Model<SysMenu> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 父菜单ID
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 菜单类型
     */
    private String type;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由参数
     */
    private String query;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否外部链接
     */
    @TableField("is_frame")
    private Boolean isFrame;

    /**
     * 是否禁用缓存
     */
    @TableField("no_cache")
    private Boolean noCache;

    /**
     * 是否可见
     */
    @TableField("is_visible")
    private Boolean isVisible;

    /**
     * 是否可用
     */
    @TableField("is_available")
    private Boolean isAvailable;

    /**
     * 图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();
}
