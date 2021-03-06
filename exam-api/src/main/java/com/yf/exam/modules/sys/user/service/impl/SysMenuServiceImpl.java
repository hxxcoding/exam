package com.yf.exam.modules.sys.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.modules.sys.user.dto.ext.MetaDTO;
import com.yf.exam.modules.sys.user.dto.response.RouterTreeDTO;
import com.yf.exam.modules.sys.user.dto.response.TreeSelectDTO;
import com.yf.exam.modules.sys.user.entity.SysMenu;
import com.yf.exam.modules.sys.user.entity.SysRoleMenu;
import com.yf.exam.modules.sys.user.enums.MenuType;
import com.yf.exam.modules.sys.user.mapper.SysMenuMapper;
import com.yf.exam.modules.sys.user.service.SysMenuService;
import com.yf.exam.modules.sys.user.service.SysRoleMenuService;
import com.yf.exam.modules.sys.user.utils.MenuUtils;
import com.yf.exam.modules.user.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenu> listTreeByRoleIds(List<String> roleIds) {
        List<SysMenu> menus = null; // TODO 修改为join语句
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
            if (CollectionUtils.isEmpty(menuIds)) {
                return new ArrayList<>();
            }
            menus = this.list(new QueryWrapper<SysMenu>().lambda()
                    .in(SysMenu::getType, MenuType.DIR, MenuType.MENU)
                    .in(SysMenu::getId, menuIds)
                    .eq(SysMenu::getIsAvailable, true)
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

    @Override
    public List<TreeSelectDTO> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTree = MenuUtils.buildMenuTree(menus);
        return menuTree.stream().map(TreeSelectDTO::new).collect(Collectors.toList());
    }

    @Override
    public void sort(String id, Integer sort) {

        SysMenu menu = this.getById(id);
        SysMenu exchange = null;

        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
        // 同级排序
        wrapper.lambda()
                .eq(SysMenu::getParentId, menu.getParentId());
        wrapper.last("LIMIT 1");

        // 上升
        if(sort == 0){
            // 同级排序
            wrapper.lambda()
                    .lt(SysMenu::getSort, menu.getSort())
                    .orderByDesc(SysMenu::getSort);
            exchange = this.getOne(wrapper, false);
        }

        // 下降
        if(sort == 1){
            // 同级排序
            wrapper.lambda()
                    .gt(SysMenu::getSort, menu.getSort())
                    .orderByAsc(SysMenu::getSort);
            exchange = this.getOne(wrapper, false);
        }

        if (exchange != null) {
            SysMenu a = new SysMenu();
            a.setId(id);
            a.setSort(exchange.getSort());
            SysMenu b = new SysMenu();
            b.setId(exchange.getId());
            b.setSort(menu.getSort());
            this.updateById(a);
            this.updateById(b);
        }
    }

}
