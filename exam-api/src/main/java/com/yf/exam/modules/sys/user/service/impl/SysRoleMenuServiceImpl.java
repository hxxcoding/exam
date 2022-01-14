package com.yf.exam.modules.sys.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.modules.sys.user.entity.SysMenu;
import com.yf.exam.modules.sys.user.entity.SysRoleMenu;
import com.yf.exam.modules.sys.user.enums.RoleType;
import com.yf.exam.modules.sys.user.mapper.SysRoleMenuMapper;
import com.yf.exam.modules.sys.user.service.SysMenuService;
import com.yf.exam.modules.sys.user.service.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "sysRoleMenu", keyGenerator = "keyGenerator")
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    @Cacheable(sync = true)
    public List<String> listPermsByRoleId(String roleId) {
        List<SysMenu> queryList = null;
        if (roleId.equals(RoleType.SUPER_ADMIN)) {  // SUPER_ADMIN拥有全部权限
            queryList = sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                    .select(SysMenu::getPerms));
        } else {  // 查询roleId拥有的权限
            // 查询roleId拥有的菜单ID
            List<SysRoleMenu> roleMenuList = this.list(new QueryWrapper<SysRoleMenu>().lambda()
                    .select(SysRoleMenu::getMenuId)
                    .eq(SysRoleMenu::getRoleId, roleId));
            List<String> menuIds = roleMenuList.stream()
                    .map(SysRoleMenu::getMenuId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(menuIds)) {
                return null;
            }
            // 查询菜单ID对应的权限
            queryList = sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                    .select(SysMenu::getPerms)
                    .in(SysMenu::getId, menuIds));
        }
        // 经过
        // 获取Perms字符串 -> 去空 -> 分割 -> Stream<String[]>转换为Stream<String> -> 去重
        // 后返回List<String>权限标识数据
        return queryList.stream()
                .map(SysMenu::getPerms)
                .filter(perm -> !StringUtils.isEmpty(perm))
                .map(perm -> perm.split(","))
                .flatMap(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void saveAll(String roleId, List<String> menuIds) {
        this.remove(new QueryWrapper<SysRoleMenu>()
                .lambda().eq(SysRoleMenu::getRoleId, roleId));
        List<SysRoleMenu> list = new ArrayList<>();
        for (String menuId : menuIds) {
            list.add(new SysRoleMenu()
                    .setMenuId(menuId)
                    .setRoleId(roleId));
        }
        this.saveBatch(list);
    }

}
