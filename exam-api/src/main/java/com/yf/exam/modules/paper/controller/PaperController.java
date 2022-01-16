package com.yf.exam.modules.paper.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yf.exam.core.api.ApiRest;
import com.yf.exam.core.api.controller.BaseController;
import com.yf.exam.core.api.dto.BaseIdReqDTO;
import com.yf.exam.core.api.dto.BaseIdRespDTO;
import com.yf.exam.core.api.dto.BaseIdsReqDTO;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.modules.paper.dto.PaperDTO;
import com.yf.exam.modules.paper.dto.ext.OnlinePaperQuDetailDTO;
import com.yf.exam.modules.paper.dto.ext.PaperQuDetailDTO;
import com.yf.exam.modules.paper.dto.request.PaperAnswerDTO;
import com.yf.exam.modules.paper.dto.request.PaperCreateReqDTO;
import com.yf.exam.modules.paper.dto.request.PaperListReqDTO;
import com.yf.exam.modules.paper.dto.request.PaperQuQueryDTO;
import com.yf.exam.modules.paper.dto.response.ExamDetailRespDTO;
import com.yf.exam.modules.paper.dto.response.ExamResultRespDTO;
import com.yf.exam.modules.paper.dto.response.PaperListRespDTO;
import com.yf.exam.modules.paper.dto.response.PaperQuOfficePointsRespDTO;
import com.yf.exam.modules.paper.entity.Paper;
import com.yf.exam.modules.paper.service.PaperService;
import com.yf.exam.modules.user.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* <p>
* 试卷控制器
* </p>
*
* @author 聪明笨狗
* @since 2020-05-25 16:33
*/
@Api(tags={"试卷"})
@RestController
@RequestMapping("/exam/api/paper/paper")
public class PaperController extends BaseController {

    @Autowired
    private PaperService baseService;

    /**
     * 分页查找
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "分页查找")
    @RequiresPermissions("paper:paging")
    @RequestMapping(value = "/paging", method = { RequestMethod.POST})
    public ApiRest<IPage<PaperListRespDTO>> paging(@RequestBody PagingReqDTO<PaperListReqDTO> reqDTO) {

        //分页查询并转换
        IPage<PaperListRespDTO> page = baseService.paging(reqDTO);

        return super.success(page);
    }

    /**
     * 创建试卷
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "创建试卷")
    @RequiresPermissions("paper:create-paper")
    @RequestMapping(value = "/create-paper", method = { RequestMethod.POST})
    public ApiRest<BaseIdRespDTO> save(@RequestBody PaperCreateReqDTO reqDTO) {
        //复制参数
        String paperId = baseService.createPaper(UserUtils.getUserId(), reqDTO.getExamId());
        return super.success(new BaseIdRespDTO(paperId));
    }

    /**
     * 批量删除
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "试卷详情")
    @RequiresPermissions("paper:paper-detail")
    @RequestMapping(value = "/paper-detail", method = { RequestMethod.POST})
    public ApiRest<ExamDetailRespDTO> paperDetail(@RequestBody BaseIdReqDTO reqDTO) {
        //根据ID查找试卷的试题
        ExamDetailRespDTO respDTO = baseService.paperDetail(reqDTO.getId());
        return super.success(respDTO);
    }

    /**
     * 批量删除
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "试题详情")
    @RequiresPermissions("paper:qu-detail")
    @RequestMapping(value = "/qu-detail", method = { RequestMethod.POST})
    public ApiRest<PaperQuDetailDTO> quDetail(@RequestBody PaperQuQueryDTO reqDTO) {
        //根据ID查找
        OnlinePaperQuDetailDTO respDTO = baseService.findQuDetail(reqDTO.getPaperId(), reqDTO.getQuId());
        return super.success(respDTO);
    }

    /**
     * 填充答案
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "填充答案")
    @RequiresPermissions("paper:fill-answer")
    @RequestMapping(value = "/fill-answer", method = { RequestMethod.POST})
    public ApiRest<PaperQuDetailDTO> fillAnswer(@RequestBody PaperAnswerDTO reqDTO) {
        //根据ID填充
        baseService.fillAnswer(reqDTO);
        return super.success();
    }


    /**
     * 交卷操作
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "交卷操作")
    @RequiresPermissions("paper:hand-exam")
    @RequestMapping(value = "/hand-exam", method = { RequestMethod.POST})
    public ApiRest<?> handleExam(@RequestBody BaseIdReqDTO reqDTO) {
        //根据ID删除
        baseService.handExam(reqDTO.getId());
        return super.success();
    }


    /**
     * 试卷结果
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "试卷详情")
    @RequiresPermissions("paper:paper-result")
    @RequestMapping(value = "/paper-result", method = { RequestMethod.POST})
    public ApiRest<ExamResultRespDTO> paperResult(@RequestBody BaseIdReqDTO reqDTO) {
        //根据ID删除
        ExamResultRespDTO respDTO = baseService.paperResult(reqDTO.getId());
        return super.success(respDTO);
    }

    /**
     * 获取office题用户答案的详细得分点
     */
    @ApiOperation(value = "office题学生得分详情")
    @RequiresPermissions("paper:paper-result")
    @RequestMapping(value = "/paper-result/office/points", method = { RequestMethod.POST})
    public ApiRest<List<PaperQuOfficePointsRespDTO>> quOfficePoints(@RequestBody PaperQuQueryDTO reqDTO) {
        List<PaperQuOfficePointsRespDTO> res = baseService.quOfficePoints(reqDTO.getPaperId(), reqDTO.getQuId());
        return super.success(res);
    }

}
