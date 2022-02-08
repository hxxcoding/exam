package com.yf.exam.modules.sys.user.controller;

import com.anji.captcha.model.vo.CaptchaVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yf.exam.core.annon.LogInject;
import com.yf.exam.core.api.ApiRest;
import com.yf.exam.core.api.controller.BaseController;
import com.yf.exam.core.api.dto.BaseIdReqDTO;
import com.yf.exam.core.api.dto.BaseIdsReqDTO;
import com.yf.exam.core.api.dto.BaseStateReqDTO;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.excel.ExportExcel;
import com.yf.exam.core.utils.excel.ImportExcel;
import com.yf.exam.modules.sys.user.dto.SysUserDTO;
import com.yf.exam.modules.sys.user.dto.export.SysUserExportDTO;
import com.yf.exam.modules.sys.user.dto.request.SysUserLoginReqDTO;
import com.yf.exam.modules.sys.user.dto.request.SysUserSaveReqDTO;
import com.yf.exam.modules.sys.user.dto.response.RouterTreeDTO;
import com.yf.exam.modules.sys.user.dto.response.SysUserLoginDTO;
import com.yf.exam.modules.sys.user.entity.SysMenu;
import com.yf.exam.modules.sys.user.entity.SysUser;
import com.yf.exam.modules.sys.user.service.SysMenuService;
import com.yf.exam.modules.sys.user.service.SysUserRoleService;
import com.yf.exam.modules.sys.user.service.SysUserService;
import com.yf.exam.modules.user.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
@RequestMapping("/exam/api/sys/user")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService baseService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 用户登录
     * @return
     */
    @LogInject(title = "登录系统")
    @CrossOrigin
    @ApiOperation(value = "用户登录")
    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public ApiRest<SysUserLoginDTO> login(@RequestBody SysUserLoginReqDTO reqDTO) {
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(reqDTO.getCaptchaVerification());
        SysUserLoginDTO respDTO = baseService.login(reqDTO.getUsername(), reqDTO.getPassword(), captchaVO);
        return super.success(respDTO);
    }

    /**
     * 用户登录
     * @return
     */
    @CrossOrigin
    @ApiOperation(value = "用户退出登录")
    @RequestMapping(value = "/logout", method = {RequestMethod.POST})
    public ApiRest<?> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        baseService.logout(token);
        return super.success();
    }

    /**
     * 踢人下线
     * @return
     */
    @ApiOperation(value = "踢人下线")
    @RequiresPermissions("sys:user:kickout")
    @RequestMapping(value = "/kickout", method = {RequestMethod.POST})
    public ApiRest<?> kickout(@RequestBody BaseIdReqDTO reqDTO) {
        baseService.kickout(reqDTO.getId());
        return super.success();
    }

    /**
     * 获取会话
     * @return
     */
    @ApiOperation(value = "获取会话")
    @RequestMapping(value = "/info", method = {RequestMethod.POST})
    public ApiRest<SysUserLoginDTO> info(@RequestParam("token") String token) {
        SysUserLoginDTO respDTO = baseService.token(token);
        return success(respDTO);
    }

    /**
     * 修改用户资料
     * @return
     */
    @ApiOperation(value = "用户本人修改资料")
    @RequiresAuthentication
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public ApiRest<?> update(@RequestBody SysUserDTO reqDTO) {
        baseService.update(reqDTO);
        return success();
    }


    /**
     * 保存或修改系统用户
     * @return
     */
    @ApiOperation(value = "保存或修改")
    @RequiresPermissions("sys:user:save")
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public ApiRest<?> save(@RequestBody SysUserSaveReqDTO reqDTO) {
        baseService.save(reqDTO);
        return success();
    }


    /**
     * 批量删除
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "批量删除")
    @RequiresPermissions("sys:user:delete")
    @RequestMapping(value = "/delete", method = { RequestMethod.POST})
    public ApiRest<?> edit(@RequestBody BaseIdsReqDTO reqDTO) {
        //根据ID删除
        baseService.removeByIds(reqDTO.getIds());
        return super.success();
    }

    /**
     * 分页查找
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "分页查找")
    @RequiresPermissions("sys:user:paging")
    @RequestMapping(value = "/paging", method = { RequestMethod.POST})
    public ApiRest<IPage<SysUserDTO>> paging(@RequestBody PagingReqDTO<SysUserDTO> reqDTO) {

        //分页查询并转换
        IPage<SysUserDTO> page = baseService.paging(reqDTO);
        return super.success(page);
    }

    /**
     * 修改状态
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "修改状态")
    @RequiresPermissions("sys:user:state")
    @RequestMapping(value = "/state", method = { RequestMethod.POST})
    public ApiRest<?> state(@RequestBody BaseStateReqDTO reqDTO) {

        // 条件
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .in(SysUser::getId, reqDTO.getIds())
                .ne(SysUser::getUserName, "admin");


        SysUser record = new SysUser();
        record.setState(reqDTO.getState());
        baseService.update(record, wrapper);

        return super.success();
    }


//    /**
//     * 保存或修改系统用户
//     * @return
//     */
//    @ApiOperation(value = "学员注册")
//    @RequestMapping(value = "/reg", method = {RequestMethod.POST})
//    public ApiRest<SysUserLoginDTO> reg(@RequestBody SysUserDTO reqDTO) {
//        SysUserLoginDTO respDTO = baseService.reg(reqDTO);
//        return success(respDTO);
//    }
//
//    /**
//     * 快速注册，如果手机号存在则登录，不存在就注册
//     * @return
//     */
//    @ApiOperation(value = "快速注册")
//    @RequestMapping(value = "/quick-reg", method = {RequestMethod.POST})
//    public ApiRest<SysUserLoginDTO> quick(@RequestBody SysUserDTO reqDTO) {
//        SysUserLoginDTO respDTO = baseService.quickReg(reqDTO);
//        return success(respDTO);
//    }

    @ApiOperation(value = "获取当前登录用户的router")
    @RequiresAuthentication
    @RequestMapping(value = "/router", method = { RequestMethod.POST })
    public ApiRest<Object> getRouter() {
        String userId = UserUtils.getUserId();
        List<String> roles = sysUserRoleService.listRoles(userId);
        List<SysMenu> menus = sysMenuService.listTreeByRoleIds(roles);
        List<RouterTreeDTO> routers = sysMenuService.buildMenus(menus);
        return super.success(routers);
    }


    /**
     * 用户导入数据模板
     */
    @RequestMapping(value = "import/template")
    @RequiresPermissions("sys:user:import:template")
    public void importFileTemplate(HttpServletResponse response) {
        try {
            String fileName = "用户导入模板.xlsx";
            new ExportExcel("用户数据", SysUserExportDTO.class, 1).write(response, fileName).dispose();
        } catch (Exception e) {
            throw new ServiceException("导入模板下载失败！失败信息："+e.getMessage());
        }
    }

    /**
     * 导入Excel
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "import")
    @RequiresPermissions("sys:user:import")
    public ApiRest importFile(@RequestParam("file") MultipartFile file) {
        try {
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<SysUserExportDTO> list = ei.getDataList(SysUserExportDTO.class);
            return super.success(baseService.importExcel(list));
        } catch (IOException | InvalidFormatException | IllegalAccessException | InstantiationException e) {
            return super.failure();
        }
    }
}
