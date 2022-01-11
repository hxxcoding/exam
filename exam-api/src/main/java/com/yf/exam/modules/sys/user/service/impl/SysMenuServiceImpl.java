package com.yf.exam.modules.sys.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.modules.sys.user.dto.ext.MetaDTO;
import com.yf.exam.modules.sys.user.dto.response.RouterTreeDTO;
import com.yf.exam.modules.sys.user.entity.SysMenu;
import com.yf.exam.modules.sys.user.entity.SysRoleMenu;
import com.yf.exam.modules.sys.user.enums.MenuType;
import com.yf.exam.modules.sys.user.enums.RoleType;
import com.yf.exam.modules.sys.user.mapper.SysMenuMapper;
import com.yf.exam.modules.sys.user.service.SysMenuService;
import com.yf.exam.modules.sys.user.service.SysRoleMenuService;
import com.yf.exam.modules.sys.user.utils.MenuUtils;
import com.yf.exam.modules.user.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenu> listUrlAndPermission() {
        return this.list(new QueryWrapper<SysMenu>().lambda()
                .select(SysMenu::getUrl, SysMenu::getPerms)
                .isNotNull(SysMenu::getUrl)
                .orderByAsc(SysMenu::getSort));
    }

    @Override
    public List<SysMenu> listByRoleIds(List<String> roleIds) {
        if (roleIds.contains(RoleType.SUPER_ADMIN)) {
            return this.list();
        }
        List<SysRoleMenu> roleMenuList = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().lambda()
                .select(SysRoleMenu::getMenuId)
                .in(SysRoleMenu::getRoleId, roleIds));
        List<String> menuIds = roleMenuList.stream()
                .map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        return this.listByIds(menuIds);
    }

    @Override
    public List<SysMenu> listTreeByRoleIds(List<String> roleIds) {
        List<SysMenu> menus = null;
        if (UserUtils.isAdmin(true)) {
            menus = this.list(new QueryWrapper<SysMenu>().lambda()
                    .in(SysMenu::getType, MenuType.DIR, MenuType.MENU)
                    .eq(SysMenu::getIsAvailable, true)
                    .orderByAsc(SysMenu::getParentId, SysMenu::getSort));
        } else {
            List<String> menuIds = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().lambda()
                    .select(SysRoleMenu::getMenuId)
                    .in(SysRoleMenu::getRoleId, roleIds))
                .stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
            menus = this.list(new QueryWrapper<SysMenu>().lambda()
                    .in(SysMenu::getType, MenuType.DIR, MenuType.MENU)
                    .eq(SysMenu::getIsAvailable, true)
                    .in(SysMenu::getId, menuIds)
                    .orderByAsc(SysMenu::getParentId, SysMenu::getSort));
        }
        return MenuUtils.getChildPerms(menus, "0");
    }

    @Override
    public List<RouterTreeDTO> buildMenus(List<SysMenu> menus) {
        List<RouterTreeDTO> routers = new LinkedList<>();
        for (SysMenu menu : menus) {
            RouterTreeDTO router = new RouterTreeDTO();
            router.setHidden(!menu.getIsVisible());
            router.setName(MenuUtils.getRouteName(menu));
            router.setPath(MenuUtils.getRouterPath(menu));
            router.setComponent(MenuUtils.getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaDTO(menu.getName(), menu.getIcon(), menu.getNoCache(), menu.getPath()));
            List<SysMenu> cMenus = menu.getChildren();
            if (!cMenus.isEmpty() && MenuType.DIR.equals(menu.getType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (MenuUtils.isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterTreeDTO> childrenList = new ArrayList<>();
                RouterTreeDTO children = new RouterTreeDTO();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaDTO(menu.getName(), menu.getIcon(), menu.getNoCache(), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().equals("0") && MenuUtils.isInnerLink(menu)) {
                router.setMeta(new MetaDTO(menu.getName(), menu.getIcon(), null, null));
                router.setPath("/inner");
                List<RouterTreeDTO> childrenList = new ArrayList<>();
                RouterTreeDTO children = new RouterTreeDTO();
                children.setPath(menu.getPath());
                children.setComponent("InnerLink");
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaDTO(menu.getName(), menu.getIcon(), null, menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

}
