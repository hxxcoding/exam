package com.yf.exam.modules.paper.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yf.exam.core.annon.LogInject;
import com.yf.exam.core.api.ApiRest;
import com.yf.exam.core.api.controller.BaseController;
import com.yf.exam.core.api.dto.BaseIdReqDTO;
import com.yf.exam.core.api.dto.BaseIdRespDTO;
import com.yf.exam.core.api.dto.BaseIdsReqDTO;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.excel.ExportExcel;
import com.yf.exam.modules.exam.dto.request.SendMsgReqDTO;
import com.yf.exam.modules.paper.dto.export.PaperExportDTO;
import com.yf.exam.modules.paper.dto.ext.OnlinePaperQuDetailDTO;
import com.yf.exam.modules.paper.dto.ext.PaperQuDetailDTO;
import com.yf.exam.modules.paper.dto.request.PaperAnswerDTO;
import com.yf.exam.modules.paper.dto.request.PaperCreateReqDTO;
import com.yf.exam.modules.paper.dto.request.PaperQueryReqDTO;
import com.yf.exam.modules.paper.dto.request.PaperQuQueryDTO;
import com.yf.exam.modules.paper.dto.response.ExamDetailRespDTO;
import com.yf.exam.modules.paper.dto.response.ExamResultRespDTO;
import com.yf.exam.modules.paper.dto.response.PaperDetailRespDTO;
import com.yf.exam.modules.paper.dto.response.PaperQuPointsRespDTO;
import com.yf.exam.modules.paper.service.PaperService;
import com.yf.exam.modules.user.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
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
    public ApiRest<IPage<PaperDetailRespDTO>> paging(@RequestBody PagingReqDTO<PaperQueryReqDTO> reqDTO) {

        //分页查询并转换
        IPage<PaperDetailRespDTO> page = baseService.paging(reqDTO);

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
        String paperId = baseService.createPaper(UserUtils.getUserId(), reqDTO.getExamId(), reqDTO.getSeat(), reqDTO.getPassword());
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
     * 获取正在考试的用户列表(在线监考)
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "获取正在考试的用户列表")
    @RequiresPermissions("paper:online-paper:paging")
    @RequestMapping(value = "/online-paper/paging", method = { RequestMethod.POST })
    public ApiRest<IPage<PaperDetailRespDTO>> monitor(@RequestBody PagingReqDTO<PaperDetailRespDTO> reqDTO) {
        IPage<PaperDetailRespDTO> page = baseService.onlinePaperPaging(reqDTO);
        return super.success(page);
    }

    /**
     * 向正在考试的用户发送信息  if (paperId == "*") sendAll即向全体在考试同学发送
     */
    @ApiOperation(value = "向正在考试的用户发送信息")
    @LogInject(title = "向正在考试的用户发送信息")
    @RequiresPermissions("paper:send-msg")
    @RequestMapping(value = "/send-msg", method = { RequestMethod.POST })
    public ApiRest<?> sendMsg(@RequestBody SendMsgReqDTO reqDTO) {
        if (reqDTO.getPaperId().equals("*")) { // 全部用户
            PaperWebSocketServer.sendMessageToAll(reqDTO.getMessage());
        } else {
            PaperWebSocketServer.sendMessageToPaper(reqDTO.getPaperId(), reqDTO.getMessage());
        }
        return super.success();
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
     * 同步填充答案
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "填充答案")
    @RequiresPermissions("paper:fill-answer")
    @RequestMapping(value = "/fill-answer", method = { RequestMethod.POST})
    public ApiRest<?> fillAnswer(@RequestBody PaperAnswerDTO reqDTO) {
        //根据ID填充
        baseService.fillAnswer(reqDTO);
        return super.success();
    }

    /**
     * 异步填充答案
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "填充答案")
    @RequiresPermissions("paper:fill-answer")
    @RequestMapping(value = "/fill-answer-by-async", method = { RequestMethod.POST})
    public ApiRest<?> fillAnswerByAsync(@RequestBody PaperAnswerDTO reqDTO) {
        //根据ID填充
        baseService.fillAnswerByAsync(reqDTO);
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
        baseService.handExam(reqDTO.getId());
        return super.success();
    }

    /**
     * 交卷操作
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "强制交卷操作")
    @LogInject(title = "强制交卷")
    @RequiresPermissions("paper:hand-exam-by-force")
    @RequestMapping(value = "/hand-exam-by-force", method = { RequestMethod.POST})
    public ApiRest<?> handleExamByForce(@RequestBody BaseIdReqDTO reqDTO) {
        baseService.handExam(reqDTO.getId());
        return super.success();
    }

    /**
     * 退回交卷
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "退回交卷")
    @LogInject(title = "退回交卷")
    @RequiresPermissions("paper:back-exam")
    @RequestMapping(value = "/back-exam", method = { RequestMethod.POST})
    public ApiRest<?> backExam(@RequestBody BaseIdReqDTO reqDTO) {
        //根据ID删除
        baseService.backExam(reqDTO.getId());
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
        ExamResultRespDTO respDTO = baseService.paperResult(reqDTO.getId());
        return super.success(respDTO);
    }

    /**
     * 获取office题用户答案的详细得分点
     */
    @ApiOperation(value = "office题学生得分详情")
    @RequiresPermissions("paper:paper-result")
    @RequestMapping(value = "/paper-result/office/points", method = { RequestMethod.POST })
    public ApiRest<List<PaperQuPointsRespDTO>> quOfficePoints(@RequestBody PaperQuQueryDTO reqDTO) {
        List<PaperQuPointsRespDTO> res = baseService.quOfficePoints(reqDTO.getPaperId(), reqDTO.getQuId());
        return super.success(res);
    }

    /**
     * 导出excel文件
     */
    @RequiresPermissions("paper:export")
    @RequestMapping(value = "/export", method = { RequestMethod.POST })
    public void exportFile(HttpServletResponse response, @RequestBody PaperQueryReqDTO reqDTO) {
        try {
            String title = "成绩单";
            if (reqDTO.getMaxScore() != null && reqDTO.getMaxScore()) {
                title += "(最高分)";
            }
            List<PaperExportDTO> list = baseService.listForExport(reqDTO);
            // 导出文件名
            String fileName = "导出成绩-" + System.currentTimeMillis() + ".xlsx";
            new ExportExcel(title, PaperExportDTO.class).setDataList(list).write(response, fileName).dispose();
        } catch (Exception e) {
            throw new ServiceException("导出失败:" + e.getMessage());
        }
    }

    /**
     * 批量导出试卷
     */
    @RequiresPermissions("paper:export")
    @RequestMapping(value = "/export/paper", method = { RequestMethod.POST })
    public ApiRest<String> batchExportPaper(@RequestBody BaseIdsReqDTO reqDTO) {
        String url = baseService.listPaperForExport(reqDTO.getIds());
        return super.success(url);
    }

}
