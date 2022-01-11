package com.yf.exam.modules.sys.user.utils;

import com.yf.exam.modules.sys.user.entity.SysMenu;
import com.yf.exam.modules.sys.user.enums.MenuType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuUtils {

    /**
     * 获取路由名称
     * @param menu 菜单信息
     * @return 路由名称
     */
    public static String getRouteName(SysMenu menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 是否为菜单内部跳转
     * @param menu 菜单信息
     * @return 结果
     */
    public static boolean isMenuFrame(SysMenu menu) {
        return menu.getParentId().equals("0") && MenuType.MENU.equals(menu.getType())
                && menu.getIsFrame();
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public static String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        // 非外链并且是一级目录（类型为目录）
        if (menu.getParentId().equals("0") && MenuType.DIR.equals(menu.getType())
                && !menu.getIsFrame()) {
            routerPath = "/" + menu.getPath();
        } else if (isMenuFrame(menu)) { // 非外链并且是一级目录（类型为菜单）
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public static String getComponent(SysMenu menu) {
        String component = "Layout";
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && !menu.getParentId().equals("0") && menu.getIsFrame()) {
            component = "InnerLink";
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = "ParentView";
        }
        return component;
    }

    /**
     * 是否为parent_view组件
     * @param menu 菜单信息
     * @return 结果
     */
    public static boolean isParentView(SysMenu menu) {
        return !menu.getParentId().equals("0") && MenuType.DIR.equals(menu.getType());
    }

    /**
     * 是否为内链组件
     * @param menu 菜单信息
     * @return 结果
     */
    public static boolean isInnerLink(SysMenu menu) {
        return !menu.getIsFrame() && menu.getPath().startsWith("http");
    }

    /**
     * 根据父节点的ID获取所有子节点
     * @return String
     */
    public static List<SysMenu> getChildPerms(List<SysMenu> menus, String parentId) {
        List<SysMenu> returnList = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        return returnList;
    }


    /**
     * 递归列表
     * @param list
     * @param t
     */
    private static void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private static List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> menuList = new ArrayList<>();
        for (SysMenu menu : list) {
            if (menu.getParentId().equals(t.getId())) {
                menuList.add(menu);
            }
        }
        return menuList;
    }

    /**
     * 判断是否有子节点
     */
    private static boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }

}
