package com.yf.exam.modules.exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yf.exam.core.api.ApiRest;
import com.yf.exam.core.api.controller.BaseController;
import com.yf.exam.core.api.dto.BaseIdReqDTO;
import com.yf.exam.core.api.dto.BaseIdsReqDTO;
import com.yf.exam.core.api.dto.BaseStateReqDTO;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.modules.exam.dto.ExamDTO;
import com.yf.exam.modules.exam.dto.request.ExamSaveReqDTO;
import com.yf.exam.modules.exam.dto.request.SendMsgReqDTO;
import com.yf.exam.modules.exam.dto.response.ExamOnlineRespDTO;
import com.yf.exam.modules.exam.dto.response.ExamPreviewRespDTO;
import com.yf.exam.modules.exam.dto.response.ExamReviewRespDTO;
import com.yf.exam.modules.exam.entity.Exam;
import com.yf.exam.modules.exam.service.ExamService;
import com.yf.exam.modules.paper.dto.request.PaperQuQueryDTO;
import com.yf.exam.modules.paper.dto.response.PaperQuPointsRespDTO;
import com.yf.exam.modules.paper.service.WebSocketServer;
import com.yf.exam.modules.sys.user.dto.SysUserDTO;
import com.yf.exam.modules.sys.user.entity.SysUser;
import com.yf.exam.modules.sys.user.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
* <p>
* 考试控制器
* </p>
*
* @author 聪明笨狗
* @since 2020-07-25 16:18
*/
@Api(tags={"考试"})
@RestController
@RequestMapping("/exam/api/exam/exam")
public class ExamController extends BaseController {

    @Autowired
    private ExamService baseService;

    /**
    * 添加或修改
    * @param reqDTO
    * @return
    */
    @ApiOperation(value = "添加或修改")
    @RequiresPermissions("exam:save")
    @RequestMapping(value = "/save", method = { RequestMethod.POST })
    public ApiRest save(@RequestBody ExamSaveReqDTO reqDTO) {
        //复制参数
        baseService.save(reqDTO);
        return super.success();
    }

    /**
    * 批量删除
    * @param reqDTO
    * @return
    */
    @ApiOperation(value = "批量删除")
    @RequiresPermissions("exam:delete")
    @RequestMapping(value = "/delete", method = { RequestMethod.POST })
    public ApiRest edit(@RequestBody BaseIdsReqDTO reqDTO) {
        //根据ID删除
        baseService.removeByIds(reqDTO.getIds());
        return super.success();
    }

    /**
    * 查找详情
    * @param reqDTO
    * @return
    */
    @ApiOperation(value = "查找详情")
    @RequiresPermissions("exam:detail")
    @RequestMapping(value = "/detail", method = { RequestMethod.POST})
    public ApiRest<ExamSaveReqDTO> find(@RequestBody BaseIdReqDTO reqDTO) {
        ExamSaveReqDTO dto = baseService.findDetail(reqDTO.getId());
        return super.success(dto);
    }

    /**
     * 获取正在考试的用户列表(在线监考)
     */
    @ApiOperation(value = "获取正在考试的用户列表")
    @RequiresPermissions("exam:online-user:paging")
    @RequestMapping(value = "/online-user/paging", method = { RequestMethod.POST })
    public ApiRest<IPage<SysUserDTO>> monitor(@RequestBody PagingReqDTO<SysUserDTO> reqDTO) {
        IPage<SysUserDTO> page = baseService.monitorPaging(reqDTO);
        return super.success(page);
    }

    /**
     * 向正在考试的用户发送信息  userId == null 即默认向全体铜壶发送
     */
    @ApiOperation(value = "向正在考试的用户发送信息")
    @RequiresPermissions("exam:send-msg")
    @RequestMapping(value = "/send-msg", method = { RequestMethod.POST })
    public ApiRest<List<SysUserDTO>> sendMsgToAll(@RequestBody SendMsgReqDTO reqDTO) {
        if (reqDTO.getUserId() == null) {
            WebSocketServer.sendMessageToAll(reqDTO.getMessage());
        } else {
            WebSocketServer.sendMessageToUser(reqDTO.getUserId(), reqDTO.getMessage());
        }
        return super.success();
    }

    /**
     * 修改考试状态
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "修改考试状态")
    @RequiresPermissions("exam:state")
    @RequestMapping(value = "/state", method = { RequestMethod.POST})
    public ApiRest state(@RequestBody BaseStateReqDTO reqDTO) {

        QueryWrapper<Exam> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(Exam::getId, reqDTO.getIds());
        Exam exam = new Exam();
        exam.setState(reqDTO.getState());
        exam.setUpdateTime(new Date());

        baseService.update(exam, wrapper);
        return super.success();
    }

    /**
    * 分页查找
    * @param reqDTO
    * @return
    */
    @ApiOperation(value = "分页查找")
    @RequiresPermissions("exam:paging")
    @RequestMapping(value = "/paging", method = { RequestMethod.POST})
    public ApiRest<IPage<ExamDTO>> paging(@RequestBody PagingReqDTO<ExamDTO> reqDTO) {

        //分页查询并转换
        IPage<ExamDTO> page = baseService.paging(reqDTO);

        return super.success(page);
    }

    /**
     * 分页查找
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "考试视角分页查找考试")
    @RequiresPermissions("exam:online-paging")
    @RequestMapping(value = "/online-paging", method = { RequestMethod.POST})
    public ApiRest<IPage<ExamOnlineRespDTO>> myPaging(@RequestBody PagingReqDTO<ExamDTO> reqDTO) {

        //分页查询并转换
        IPage<ExamOnlineRespDTO> page = baseService.onlinePaging(reqDTO);
        return super.success(page);
    }

    /**
     * 考试预览准备
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "考试预览准备")
    @RequiresPermissions("exam:online-paging")
    @RequestMapping(value = "/online-preview", method = { RequestMethod.POST})
    public ApiRest<ExamPreviewRespDTO> myPreview(@RequestBody BaseIdReqDTO reqDTO) {
        ExamPreviewRespDTO respDTO = baseService.onlinePreview(reqDTO.getId());
        return super.success(respDTO);
    }


//    /**
//     * 分页查找
//     * @param reqDTO
//     * @return
//     */
//    @ApiOperation(value = "待阅试卷")
//    @RequestMapping(value = "/review-paging", method = { RequestMethod.POST})
//    public ApiRest<IPage<ExamReviewRespDTO>> reviewPaging(@RequestBody PagingReqDTO<ExamDTO> reqDTO) {
//
//        //分页查询并转换
//        IPage<ExamReviewRespDTO> page = baseService.reviewPaging(reqDTO);
//
//        return super.success(page);
//    }


}
