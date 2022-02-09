package com.yf.exam.modules.sys.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.core.api.ApiError;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.api.enums.CommonState;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.core.utils.RedisUtil;
import com.yf.exam.core.utils.StringUtils;
import com.yf.exam.core.utils.passwd.PassHandler;
import com.yf.exam.core.utils.passwd.PassInfo;
import com.yf.exam.modules.Constant;
import com.yf.exam.modules.shiro.jwt.JwtUtils;
import com.yf.exam.modules.sys.depart.dto.SysDepartDTO;
import com.yf.exam.modules.sys.depart.entity.SysDepart;
import com.yf.exam.modules.sys.depart.service.SysDepartService;
import com.yf.exam.modules.sys.user.dto.SysUserDTO;
import com.yf.exam.modules.sys.user.dto.export.SysUserExportDTO;
import com.yf.exam.modules.sys.user.dto.request.SysUserSaveReqDTO;
import com.yf.exam.modules.sys.user.dto.response.SysUserLoginDTO;
import com.yf.exam.modules.sys.user.entity.SysUser;
import com.yf.exam.modules.sys.user.enums.RoleType;
import com.yf.exam.modules.sys.user.mapper.SysUserMapper;
import com.yf.exam.modules.sys.user.service.SysRoleMenuService;
import com.yf.exam.modules.sys.user.service.SysUserRoleService;
import com.yf.exam.modules.sys.user.service.SysUserService;
import com.yf.exam.modules.user.UserUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
* <p>
* 语言设置 服务实现类
* </p>
*
* @author 聪明笨狗
* @since 2020-04-13 16:57
*/
@Service
@CacheConfig(cacheNames = "sysUser", keyGenerator = "keyGenerator")
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysDepartService sysDepartService;
    @Autowired
    private CaptchaService captchaService;


    @Override
    public IPage<SysUserDTO> paging(PagingReqDTO<SysUserDTO> reqDTO) {

        // 创建分页对象
        // 也可以使用xml直接写 sql 语句实现, 参考 PaperMapper.paging
        IPage<SysUser> query = new Page<>(reqDTO.getCurrent(), reqDTO.getSize());

        //查询条件
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();

        SysUserDTO params = reqDTO.getParams();

        if(params!=null){
            if(!StringUtils.isBlank(params.getUserName())){
                wrapper.lambda().like(SysUser::getUserName, params.getUserName());
            }

            if(!StringUtils.isBlank(params.getRealName())){
                wrapper.lambda().like(SysUser::getRealName, params.getRealName());
            }

            if(!StringUtils.isBlank(params.getDepartId())){
                List<String> ids = sysDepartService.listAllSubIds(params.getDepartId());
                wrapper.lambda().in(SysUser::getDepartId, ids);
            }

            if (!StringUtils.isBlank(params.getRoleIds())) {
                List<String> roles = JSONObject.parseArray(params.getRoleIds(), String.class);
                roles.forEach(id -> wrapper.lambda().like(SysUser::getRoleIds, id));
            }
        }

        //获得数据
        IPage<SysUser> page = this.page(query, wrapper);
        //转换结果
        IPage<SysUserDTO> pageData = JSON.parseObject(JSON.toJSONString(page), new TypeReference<Page<SysUserDTO>>(){});
        return pageData;
     }

    @Override
    public SysUserLoginDTO login(String userName, String password, CaptchaVO captchaVO) {

        ResponseModel response = captchaService.verification(captchaVO);
        if (!response.isSuccess()) {
            throw new ServiceException(response.getRepMsg());
        }

        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getUserName, userName);

        SysUser user = this.getOne(wrapper, false);
        if(user == null){
            throw new ServiceException(ApiError.ERROR_90010002);
        }

        // 被禁用
        if(user.getState().equals(CommonState.ABNORMAL)){
            throw new ServiceException(ApiError.ERROR_90010005);
        }

        boolean check = PassHandler.checkPass(password, user.getSalt(), user.getPassword());
        if(!check){
            throw new ServiceException(ApiError.ERROR_90010002);
        }
        SysUserLoginDTO sysUserLoginDTO = this.setToken(user);
        RedisUtil.hset(Constant.TOKEN, sysUserLoginDTO.getUserName(), sysUserLoginDTO.getToken(), JwtUtils.getExpireTime() / 1000);
        return sysUserLoginDTO;
    }

    @Override
    public SysUserLoginDTO token(String token) {

        // 获得会话
        String username = JwtUtils.getUsername(token);

        // 校验结果
        boolean check = JwtUtils.verify(token, username);


        if(!check){
            throw new ServiceException(ApiError.ERROR_90010001);
        }

        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getUserName, username);

        SysUser user = this.getOne(wrapper, false);
        if(user == null){
            throw new ServiceException(ApiError.ERROR_10010002);
        }

        // 被禁用
        if(user.getState().equals(CommonState.ABNORMAL)){
            throw new ServiceException(ApiError.ERROR_90010005);
        }

        return this.setToken(user);
    }

    @Override
    public void logout(String token) {

        // 仅退出当前会话
        SecurityUtils.getSubject().logout();
    }

    @Override
    public void kickout(String userName) {
        if (userName.equals("*")) { // userName == "*" 强退所有用户
            RedisUtil.delete(Constant.TOKEN);
        } else {
            RedisUtil.hdel(Constant.TOKEN, userName);
        }
    }

    @Override
    public void update(SysUserDTO reqDTO) {


       String pass = reqDTO.getPassword();
       if(!StringUtils.isBlank(pass)){
           PassInfo passInfo = PassHandler.buildPassword(pass);
           SysUser user = this.getById(UserUtils.getUserId());
           user.setPassword(passInfo.getPassword());
           user.setSalt(passInfo.getSalt());
           this.updateById(user);
       }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(SysUserSaveReqDTO reqDTO) {

        List<String> roles = reqDTO.getRoles();

        if(CollectionUtils.isEmpty(roles)){
            throw new ServiceException(ApiError.ERROR_90010003);
        }

        if (this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, reqDTO.getUserName())) != 0) {
            throw new ServiceException("用户名已存在!");
        }

        String departId = null;
        if (reqDTO.getId() == null && roles.contains(RoleType.TEACHER)) {
            departId = IdWorker.getIdStr();
            SysDepartDTO depart = new SysDepartDTO();
            depart.setParentId("0");
            depart.setDeptName(reqDTO.getUserName() + "-" + reqDTO.getRealName());
            depart.setId(departId);
            sysDepartService.save(depart);
        }

        // 保存基本信息
        SysUser user = new SysUser();
        BeanMapper.copy(reqDTO, user);
        if (departId != null) {
            user.setDepartId(departId);
        }

        // 添加模式
        if(StringUtils.isBlank(user.getId())){
            user.setId(IdWorker.getIdStr());
        }

        // 修改密码
        if(!StringUtils.isBlank(reqDTO.getPassword())){
            PassInfo pass = PassHandler.buildPassword(reqDTO.getPassword());
            user.setPassword(pass.getPassword());
            user.setSalt(pass.getSalt());
        }

        // 保存角色信息
        String roleIds = sysUserRoleService.saveRoles(user.getId(), roles);
        user.setRoleIds(roleIds);
        this.saveOrUpdate(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysUserLoginDTO reg(SysUserDTO reqDTO) {

        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getUserName, reqDTO.getUserName());

        int count = this.count(wrapper);

        if(count > 0){
            throw new ServiceException(1, "用户名已存在，换一个吧！");
        }


        // 保存用户
        SysUser user = new SysUser();
        user.setId(IdWorker.getIdStr());
        user.setUserName(reqDTO.getUserName());
        user.setRealName(reqDTO.getRealName());
        PassInfo passInfo = PassHandler.buildPassword(reqDTO.getPassword());
        user.setPassword(passInfo.getPassword());
        user.setSalt(passInfo.getSalt());

        // 保存角色
        List<String> roles = new ArrayList<>();
        roles.add("student");
        String roleIds = sysUserRoleService.saveRoles(user.getId(), roles);
        user.setRoleIds(roleIds);
        this.save(user);

        return this.setToken(user);
    }

    @Override
    public SysUserLoginDTO quickReg(SysUserDTO reqDTO) {

        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getUserName, reqDTO.getUserName());
        wrapper.last(" LIMIT 1 ");
        SysUser user = this.getOne(wrapper);
        if(user!=null){
            return this.setToken(user);
        }

        return this.reg(reqDTO);
    }


    /**
     * 保存会话信息
     * @param user
     * @return
     */
    private SysUserLoginDTO setToken(SysUser user){

        SysUserLoginDTO respDTO = new SysUserLoginDTO();
        BeanMapper.copy(user, respDTO);

        // 生成Token
        String token = JwtUtils.sign(user.getUserName());
        respDTO.setToken(token);

        // 填充角色
        List<String> roles = sysUserRoleService.listRoles(user.getId());
        Set<String> perms = new HashSet<>();
        roles.forEach(role -> perms.addAll(sysRoleMenuService.listPermsByRoleId(role)));
        respDTO.setRoles(roles);
        respDTO.setPerms(new ArrayList<>(perms));
        return respDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int importExcel(List<SysUserExportDTO> dtoList) {
        this.checkExcel(dtoList);
        int count = 0;
        SysUser user;
        try {
            for (SysUserExportDTO item : dtoList) {
                user = new SysUser();
                user.setId(IdWorker.getIdStr());
                user.setUserName(item.getUserName());
                user.setRealName(item.getRealName());
                PassInfo passInfo = PassHandler.buildPassword(item.getPassword());
                user.setPassword(passInfo.getPassword());
                user.setSalt(passInfo.getSalt());
                user.setDepartId(item.getDepartId());
                // 保存角色
                List<String> roles = new ArrayList<>();
                roles.add("student");
                String roleIds = sysUserRoleService.saveRoles(user.getId(), roles);
                user.setRoleIds(roleIds);
                this.save(user);
                count++;
            }
        } catch (ServiceException e) {
            throw new ServiceException(1, "第" + count + "行导入出现问题: " + e.getMessage());
        }
        return count;
    }



    /**
     * 校验Excel
     * @param list
     * @throws Exception
     */
    private void checkExcel(List<SysUserExportDTO> list) throws ServiceException {

        // 约定第三行开始导入
        int line = 3;
        StringBuilder sb = new StringBuilder();

        if (CollectionUtils.isEmpty(list)) {
            throw new ServiceException(1, "您导入的数据似乎是一个空表格！");
        }
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        for (SysUserExportDTO item : list) {
            wrapper.lambda().eq(SysUser::getUserName, item.getUserName());
            SysUser user = this.getOne(wrapper);
            if (user != null) {
                sb.append("第").append(line).append("行用户名已存在");
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(item.getRealName())) {
                sb.append("第").append(line).append("行姓名不能为空");
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(item.getDeptName())) {
                sb.append("第").append(line).append("行选课号不能为空");
            } else {
                // 根据 deptName 获取 departId
                SysDepart depart = sysDepartService.getOne(new QueryWrapper<SysDepart>()
                        .lambda().eq(SysDepart::getDeptName, item.getDeptName().trim()));
                if (depart == null) {
                    sb.append("第").append(line).append("行\"选课号\"不存在,请先添加该课程(班级)");
                } else {
                    item.setDepartId(depart.getId());
                }
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(item.getUserName())) {
                sb.append("第").append(line).append("行学号不能为空/");
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(item.getPassword())) {
                sb.append("第").append(line).append("行密码不能为空/");
            }
            line++;
        }
        if (!"".equals(sb.toString())) {
            throw new ServiceException(1, sb.toString());
        }
    }

}
