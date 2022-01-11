package com.yf.exam.modules.sys.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yf.exam.modules.sys.user.dto.response.RouterTreeDTO;
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

}
