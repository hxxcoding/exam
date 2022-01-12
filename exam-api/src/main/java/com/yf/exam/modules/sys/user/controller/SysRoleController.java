package com.yf.exam.modules.sys.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yf.exam.core.api.ApiRest;
import com.yf.exam.core.api.controller.BaseController;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.modules.sys.user.dto.SysRoleDTO;
import com.yf.exam.modules.sys.user.dto.request.SysRoleMenuSaveReqDTO;
import com.yf.exam.modules.sys.user.entity.SysRole;
import com.yf.exam.modules.sys.user.entity.SysRoleMenu;
import com.yf.exam.modules.sys.user.service.SysRoleMenuService;
import com.yf.exam.modules.sys.user.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 管理用户控制器
 * </p>
 *
 * @author 聪明笨狗
 * @since 2020-04-13 16:57
 */
@Api(tags = {"管理用户"})
@RestController
@RequestMapping("/exam/api/sys/role")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService baseService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 分页查找
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "分页查找")
    @RequestMapping(value = "/paging", method = { RequestMethod.POST })
    public ApiRest<IPage<SysRoleDTO>> paging(@RequestBody PagingReqDTO<SysRoleDTO> reqDTO) {

        //分页查询并转换
        IPage<SysRoleDTO> page = baseService.paging(reqDTO);
        return super.success(page);
    }

    /**
     * 查找列表，每次最多返回200条数据
     * @return
     */
    @ApiOperation(value = "查找列表")
    @RequestMapping(value = "/list", method = { RequestMethod.POST })
    public ApiRest<List<SysRoleDTO>> list() {

        //分页查询并转换
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();

        //转换并返回
        List<SysRole> list = baseService.list(wrapper);

        //转换数据
        List<SysRoleDTO> dtoList = BeanMapper.mapList(list, SysRoleDTO.class);

        return super.success(dtoList);
    }

    @ApiOperation(value = "修改身份菜单授权")
    @RequestMapping(value = "/menu/update", method = { RequestMethod.POST })
    public ApiRest<?> updateRoleMenu(@RequestBody SysRoleMenuSaveReqDTO reqDTO) {
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>()
                .lambda().eq(SysRoleMenu::getRoleId, reqDTO.getId()));
        List<SysRoleMenu> list = new ArrayList<>();
        for (String menuId : reqDTO.getMenuIds()) {
            list.add(new SysRoleMenu()
                    .setMenuId(menuId)
                    .setRoleId(reqDTO.getId()));
        }
        sysRoleMenuService.saveBatch(list);
        return super.success();
        // TODO 重构到Service层
    }
}
