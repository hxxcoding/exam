package com.yf.exam.modules.sys.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yf.exam.modules.sys.user.entity.SysRoleMenu;

import java.util.List;

public interface SysRoleMenuService extends IService<SysRoleMenu> {
    /**
     * 根据身份获取菜单
     * @param roleId
     * @return
     */
    List<String> listPermsByRoleId(String roleId);

    void saveAll(String roleId, List<String> menuIds);
}
