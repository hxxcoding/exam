package com.yf.exam.modules.sys.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yf.exam.core.api.ApiRest;
import com.yf.exam.core.api.controller.BaseController;
import com.yf.exam.core.api.dto.BaseIdReqDTO;
import com.yf.exam.core.api.dto.BaseIdRespDTO;
import com.yf.exam.core.api.dto.BaseSortReqDTO;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.modules.sys.user.dto.SysMenuDTO;
import com.yf.exam.modules.sys.user.dto.response.TreeSelectDTO;
import com.yf.exam.modules.sys.user.entity.SysMenu;
import com.yf.exam.modules.sys.user.entity.SysRoleMenu;
import com.yf.exam.modules.sys.user.service.SysMenuService;
import com.yf.exam.modules.sys.user.service.SysRoleMenuService;
import com.yf.exam.modules.user.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单管理控制器
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2021-01-12
 */
@Api(tags = {"管理用户"})
@RestController
@RequestMapping("/exam/api/sys/menu")
public class SysMenuController extends BaseController {

    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @ApiOperation(value = "查询菜单")
    @RequiresPermissions("sys:menu:list")
    @RequestMapping(value = "/list", method = { RequestMethod.POST })
    public ApiRest<List<SysMenu>> menuList() {
        return super.success(sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(true, SysMenu::getParentId, SysMenu::getSort)));
    }

    @ApiOperation(value = "查询菜单详细信息")
    @RequiresPermissions("sys:menu:detail")
    @RequestMapping(value = "/detail", method = { RequestMethod.POST })
    public ApiRest<SysMenu> menuDetail(@RequestBody BaseIdReqDTO reqDTO) {
        return super.success(sysMenuService.getById(reqDTO.getId()));
    }

    @ApiOperation(value = "添加或修改")
    @RequiresPermissions("sys:menu:save")
    @RequestMapping(value = "/save", method = { RequestMethod.POST })
    public ApiRest<Object> saveMenu(@RequestBody SysMenuDTO reqDTO) {
        SysMenu sysMenu = new SysMenu();
        BeanMapper.copy(reqDTO, sysMenu);
        sysMenuService.saveOrUpdate(sysMenu);
        return super.success(new BaseIdRespDTO(sysMenu.getId()));
    }

    @ApiOperation(value = "删除")
    @RequiresPermissions("sys:menu:delete")
    @RequestMapping(value = "/delete", method = { RequestMethod.POST })
    public ApiRest<Object> deleteMenu(@RequestBody BaseIdReqDTO reqDTO) {
        sysMenuService.removeById(reqDTO.getId());
        return super.success();
    }

    @ApiOperation(value = "根据roleId查询选中的菜单列表")
    @RequiresPermissions("sys:menu:list-tree-by-role")
    @RequestMapping(value = "/list-tree-by-role", method = { RequestMethod.POST })
    public ApiRest<Object> listTreeByRoleId(@RequestBody BaseIdReqDTO reqDTO) {
        List<SysMenu> allMenus = sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(true, SysMenu::getParentId, SysMenu::getSort));
        Map<String, Object> map = new HashMap<>();
        List<String> checkedKeys = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().lambda()
                .eq(SysRoleMenu::getRoleId, reqDTO.getId()))
            .stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        List<TreeSelectDTO> tree = sysMenuService.buildMenuTreeSelect(allMenus);
        map.put("checkedKeys", checkedKeys);
        map.put("tree", tree);
        return super.success(map);
        // TODO 重构到Service层
    }

    /**
     * 分类排序
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "分类排序")
    @RequiresPermissions("sys:menu:sort")
    @RequestMapping(value = "/sort", method = { RequestMethod.POST })
    public ApiRest<?> sort(@RequestBody BaseSortReqDTO reqDTO) {
        sysMenuService.sort(reqDTO.getId(), reqDTO.getSort());
        return super.success();
    }

}
