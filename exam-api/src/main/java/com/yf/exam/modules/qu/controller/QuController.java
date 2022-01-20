package com.yf.exam.modules.qu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.yf.exam.ability.upload.dto.FileUrlReqDTO;
import com.yf.exam.core.api.ApiRest;
import com.yf.exam.core.api.controller.BaseController;
import com.yf.exam.core.api.dto.BaseIdReqDTO;
import com.yf.exam.core.api.dto.BaseIdRespDTO;
import com.yf.exam.core.api.dto.BaseIdsReqDTO;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.core.utils.excel.ExportExcel;
import com.yf.exam.core.utils.excel.ImportExcel;
import com.yf.exam.modules.qu.dto.PPTSlideDTO;
import com.yf.exam.modules.qu.dto.QuDTO;
import com.yf.exam.modules.qu.dto.WordParagraphDTO;
import com.yf.exam.modules.qu.dto.export.QuExportDTO;
import com.yf.exam.modules.qu.dto.ext.QuDetailDTO;
import com.yf.exam.modules.qu.dto.request.QuQueryReqDTO;
import com.yf.exam.modules.qu.dto.request.ReadFormatReqDTO;
import com.yf.exam.modules.qu.entity.Qu;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import com.yf.exam.modules.qu.service.QuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
* <p>
* 问题题目控制器
* </p>
*
* @author 聪明笨狗
* @since 2020-05-25 13:25
*/
@Api(tags={"问题题目"})
@RestController
@RequestMapping("/exam/api/qu/qu")
public class QuController extends BaseController {

    @Autowired
    private QuService baseService;
    @Autowired
    private QuAnswerOfficeService quAnswerOfficeService;

    /**
     * 添加或修改
     *
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "添加或修改")
    @RequiresPermissions("qu:save")
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public ApiRest<BaseIdRespDTO> save(@RequestBody QuDetailDTO reqDTO) {
        baseService.save(reqDTO);
        return super.success();
    }

    /**
     * 批量删除
     *
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "批量删除")
    @RequiresPermissions("qu:delete")
    @RequestMapping(value = "/delete", method = {RequestMethod.POST})
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
    @RequiresPermissions("qu:detail")
    @RequestMapping(value = "/detail", method = {RequestMethod.POST})
    public ApiRest<QuDetailDTO> detail(@RequestBody BaseIdReqDTO reqDTO) {
        QuDetailDTO dto = baseService.detail(reqDTO.getId());
        return super.success(dto);
    }

    /**
     * 分页查找
     *
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "分页查找")
    @RequiresPermissions("qu:paging")
    @RequestMapping(value = "/paging", method = {RequestMethod.POST})
    public ApiRest<IPage<QuDTO>> paging(@RequestBody PagingReqDTO<QuQueryReqDTO> reqDTO) {

        //分页查询并转换
        IPage<QuDTO> page = baseService.paging(reqDTO);

        return super.success(page);
    }

    /**
     * 查找列表，每次最多返回200条数据
     *
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "查找列表")
    @RequiresPermissions("qu:list")
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public ApiRest<List<QuDTO>> list(@RequestBody QuDTO reqDTO) {

        //分页查询并转换
        QueryWrapper<Qu> wrapper = new QueryWrapper<>();

        //转换并返回
        List<Qu> list = baseService.list(wrapper);

        //转换数据
        List<QuDTO> dtoList = BeanMapper.mapList(list, QuDTO.class);

        return super.success(dtoList);
    }

    /**
     * word文件提取段落,索引
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "word文件分析")
    @RequiresPermissions("qu:office:analyse")
    @RequestMapping(value = "/office/analyse/word", method = { RequestMethod.POST })
    public ApiRest<List<WordParagraphDTO>> wordAnalyse(@RequestBody FileUrlReqDTO reqDTO) {
        List<WordParagraphDTO> paragraphs = quAnswerOfficeService.wordParagraphAnalyze(reqDTO.getUrl());
        return super.success(paragraphs);
    }

    /**
     * ppt文件提取段落,索引
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "ppt文件分析")
    @RequiresPermissions("qu:office:analyse")
    @RequestMapping(value = "/office/analyse/ppt", method = { RequestMethod.POST })
    public ApiRest<List<PPTSlideDTO>> pptAnalyse(@RequestBody FileUrlReqDTO reqDTO) {
        List<PPTSlideDTO> slides = quAnswerOfficeService.pptSlideAnalyze(reqDTO.getUrl());
        return super.success(slides);
    }

    /**
     * 提取Office文件的判分方法
     * @param reqDTO
     * @return
     */
    @ApiOperation(value = "office题判分方法获取")
    @RequiresPermissions("qu:office:analyse")
    @RequestMapping(value = "/office/method", method = { RequestMethod.POST })
    public ApiRest<List<String>> getQuOfficeMethods(@RequestBody QuQueryReqDTO reqDTO) {
        List<String> methods = quAnswerOfficeService.listQuOfficeMethods(reqDTO.getQuType());
        return super.success(methods);
    }

    /**
     * 读取office题文件格式
     * @param reqDTO
     * @return
     */
    @RequiresPermissions("qu:office:analyse")
    @RequestMapping(value = "/office/read-format", method = { RequestMethod.POST })
    public ApiRest<String> readFormat(@RequestBody ReadFormatReqDTO reqDTO) {
        String format = quAnswerOfficeService.readFormat(reqDTO.getUrl(), reqDTO.getMethod(), reqDTO.getPos());
        return super.success(format);
    }

    /**
     * 导出excel文件
     */
    @RequiresPermissions("qu:export")
    @RequestMapping(value = "/export")
    public ApiRest exportFile(HttpServletResponse response, @RequestBody QuQueryReqDTO reqDTO) {


        // 导出文件名
        String fileName = "导出的试题-" + System.currentTimeMillis() + ".xlsx";

        try {

            int no = 0;
            String quId = "";
            List<QuExportDTO> list = baseService.listForExport(reqDTO);
            for (QuExportDTO item : list) {
                if (!quId.equals(item.getQId())) {
                    quId = item.getQId();
                    no += 1;
                } else {
                    item.setQuType("0");
                    item.setQContent("");
                    item.setQAnalysis("");
                    item.setRepoList(null);
                    item.setQImage("");
                }
                item.setNo(String.valueOf(no));
            }
            new ExportExcel("试题", QuExportDTO.class).setDataList(list).write(response, fileName).dispose();
            return super.success();
        } catch (Exception e) {
            return failure(e.getMessage());
        }
    }

    /**
     * 导入Excel
     *
     * @param file
     * @return
     */
    @RequiresPermissions("qu:import")
    @RequestMapping(value = "import")
    public ApiRest importFile(@RequestParam("file") MultipartFile file) {
        try {
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<QuExportDTO> list = ei.getDataList(QuExportDTO.class);
            // 导入成功
            return super.success(baseService.importExcel(list));
        } catch (IOException | InvalidFormatException | IllegalAccessException | InstantiationException e) {
            return super.failure();
        }
    }

    /**
     * 下载导入试题数据模板
     */
    @RequiresPermissions("qu:import:template")
    @RequestMapping(value = "import/template")
    public ApiRest importFileTemplate(HttpServletResponse response) {
        try {
            String fileName = "试题导入模板.xlsx";
            List<QuExportDTO> list = Lists.newArrayList();

            QuExportDTO l1 = new QuExportDTO();
            l1.setNo("正式导入，请删除此说明行：数字，相同的数字表示同一题的序列");
            l1.setQContent("问题内容");
            l1.setQAnalysis("整个问题的解析");
            l1.setQuType("只能填写1、2、3、4、5；1表示单选题，2表示多选题，3表示判断题，4表示操作题，5表示填空题");
            l1.setQImage("题目附件，完整URL"); // TODO
            l1.setAImage("答案图片，完整URL，只限一个");
            l1.setRepoList(Arrays.asList(new String[]{"已存在题库的ID，多个用逗号隔开，题库ID错误无法导入"}));
            l1.setAContent("候选答案1");
            l1.setAIsRight("只能填写0或1，0表示否，1表示是");
            l1.setAAnalysis("这个项是正确的");


            QuExportDTO l2 = new QuExportDTO();
            l2.setQContent("找出以下可以被2整除的数（多选）");
            l2.setQAnalysis("最基本的数学题，不做过多解析");
            l2.setQuType("2");
            l2.setNo("1");
            l2.setAIsRight("1");
            l2.setAContent("数字：2");
            l2.setAAnalysis("2除以2=1，对的");

            QuExportDTO l3 = new QuExportDTO();
            l3.setNo("1");
            l3.setAIsRight("0");
            l3.setAContent("数字：3");
            l3.setAAnalysis("3除以2=1.5，不能被整除");

            QuExportDTO l4 = new QuExportDTO();
            l4.setNo("1");
            l4.setAIsRight("1");
            l4.setAContent("数字：6");
            l4.setAAnalysis("6除以2=3，对的");



            list.add(l1);
            list.add(l2);
            list.add(l3);
            list.add(l4);

            new ExportExcel("试题数据", QuExportDTO.class, 1).setDataList(list).write(response, fileName).dispose();
            return super.success();
        } catch (Exception e) {
            return super.failure("导入模板下载失败！失败信息："+e.getMessage());
        }
    }
}
