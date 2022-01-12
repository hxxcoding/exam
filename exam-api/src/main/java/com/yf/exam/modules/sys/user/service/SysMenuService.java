package com.yf.exam.modules.sys.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yf.exam.modules.sys.user.dto.response.RouterTreeDTO;
import com.yf.exam.modules.sys.user.dto.response.TreeSelectDTO;
import com.yf.exam.modules.sys.user.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单的url和permission
     * @return
     */
    List<SysMenu> listUrlAndPermission();

    /**
     * 根据身份获取菜单
     * @param roleIds
     * @return
     */
    List<SysMenu> listByRoleIds(List<String> roleIds);

    List<SysMenu> listTreeByRoleIds(List<String> roleIds);

    List<RouterTreeDTO> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<TreeSelectDTO> buildMenuTreeSelect(List<SysMenu> menus);

}
