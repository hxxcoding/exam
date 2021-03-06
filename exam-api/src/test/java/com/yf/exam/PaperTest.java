package com.yf.exam;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yf.exam.core.utils.file.PdfUtils;
import com.yf.exam.core.utils.poi.ExcelUtils;
import com.yf.exam.core.utils.poi.WordUtils;
import com.yf.exam.modules.paper.dto.ext.PaperQuDetailDTO;
import com.yf.exam.modules.paper.dto.response.ExamDetailRespDTO;
import com.yf.exam.modules.paper.dto.response.ExamResultRespDTO;
import com.yf.exam.modules.paper.entity.PaperQu;
import com.yf.exam.modules.paper.service.PaperQuService;
import com.yf.exam.modules.paper.service.PaperService;
import com.yf.exam.modules.qu.entity.Qu;
import com.yf.exam.modules.qu.entity.QuAnswer;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;
import com.yf.exam.modules.qu.enums.QuType;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import com.yf.exam.modules.qu.service.QuAnswerService;
import com.yf.exam.modules.qu.service.QuService;
import com.yf.exam.modules.sys.depart.entity.SysDepart;
import com.yf.exam.modules.sys.depart.service.SysDepartService;
import com.yf.exam.modules.sys.user.entity.SysUser;
import com.yf.exam.modules.sys.user.service.SysUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wiki.xsx.core.pdf.doc.XEasyPdfDocument;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class PaperTest {

    @Autowired
    private PaperService paperService;
    @Autowired
    private PaperQuService paperQuService;
    @Autowired
    private QuAnswerOfficeService quAnswerOfficeService;
    @Autowired
    private QuAnswerService quAnswerService;
    @Autowired
    private QuService quService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysDepartService sysDepartService;

    @Test
    public void testCreate(){
        //examService.createPaper("aaaa", "4e867df4d47a42ad896f66fd1f9d0a8e");
    }


    @Test
    public void testPaperDetail(){
        ExamDetailRespDTO respDTO = paperService.paperDetail("1264852438679150594");
        System.out.println(JSON.toJSONString(respDTO));
    }

    @Test
    public void testQuDetail(){
        PaperQuDetailDTO respDTO = paperService.findQuDetail("1264872568393834497", "5859c292a6ad486da6d9ea41084affe2");
        System.out.println(JSON.toJSONString(respDTO));
    }

    @Test
    public void wordUtilsTest() {
        WordUtils docx = new WordUtils("/Users/hxx/Desktop/office/test.docx");
        System.out.println("????????????" + docx.getPgSzW() + " x " + docx.getPgSzH());
        System.out.println("?????????????????????" + docx.getPgMarTop() + ", " + docx.getPgMarBottom() +
                ", " + docx.getPgMarLeft() + ", " + docx.getPgMarRight() +
                ", ?????????" + docx.getPgMarGutter() +
                ", ???????????????" + docx.getPgMarHeader() + ", ???????????????" + docx.getPgMarFooter());
        System.out.println("????????????" + docx.getIndentationFirstLine(2) + " ???????????????" + docx.getIndentationHanging(2));
        System.out.println("?????????" + docx.getFontSize(2) + "????????????" + docx.getFontFamily(2) + "\n" + " ???????????????" +
                docx.getUnderlineType(2) + " ???????????????" + docx.isItalic(2) + " ???????????????" + docx.isBold(2) + "\n" +
                "????????????" + docx.getSpacingBetween(2) + " ???????????????" + docx.getNumFmt(8));
        System.out.println("?????????" + docx.getColsNum(38) + " ???????????????" + docx.isColsLine(38));
        System.out.println("???????????????????????????" + docx.getPicTextAround(4));
    }

    @Test
    public void testOfficeSave() {
        Qu qu = quService.getOne(new QueryWrapper<Qu>().lambda().eq(Qu::getQuType, QuType.WORD));
        quAnswerOfficeService.save(new QuAnswerOffice().setQuId(qu.getId())
                .setMethod("getIndentationHanging").setPos("2").setAnswer("420").setScore(3));
        quAnswerOfficeService.save(new QuAnswerOffice().setQuId(qu.getId())
                .setMethod("getFontSize").setPos("2").setAnswer("13").setScore(3));
        quAnswerOfficeService.save(new QuAnswerOffice().setQuId(qu.getId())
                .setMethod("getUnderlineType").setPos("2").setAnswer("WAVY_DOUBLE").setScore(3));
        quAnswerOfficeService.save(new QuAnswerOffice().setQuId(qu.getId())
                .setMethod("getPgMarLeft").setAnswer("1985").setScore(3));
    }

    @Test
    public void testOffice() {
        WordUtils docx = new WordUtils("/Users/hxx/Desktop/office/2021word-????????????8.docx");
        List<QuAnswerOffice> officeAnswers = quAnswerOfficeService.list();
        int totalScore = 0;
        for (QuAnswerOffice officeAnswer : officeAnswers) {
            Integer position = Integer.getInteger(officeAnswer.getPos());
            String answer = docx.executeMethod(officeAnswer.getMethod(), position).toString();
            System.out.println(answer + "," + officeAnswer.getAnswer());
            if (officeAnswer.getAnswer().equals(answer)) {
                totalScore += officeAnswer.getScore();
            }
        }
        System.out.println(totalScore);
    }

    @Test
    public void testExcelUtils() {
//        System.out.println(xssfWorkbook.getStylesSource().getCTStylesheet());
//        System.out.println(xssfWorkbook.getSheetAt(0).getCTWorksheet());
//        System.out.println(xssfWorkbook.getStylesSource().getCTStylesheet());
        ExcelUtils xlsx = new ExcelUtils("/Users/hxx/Desktop/answer.xlsx");
        System.out.println("????????????:" + xlsx.getNumFormatCode("D10"));
        System.out.println("????????????:" + xlsx.getFunction("H2"));
        System.out.println("???????????????:" + xlsx.getNumValue("D10"));
    }

    @Test
    public void testExcelSave() {
        Qu qu = quService.getOne(new QueryWrapper<Qu>().lambda().eq(Qu::getQuType, QuType.EXCEL));
        quAnswerOfficeService.save(new QuAnswerOffice().setQuId(qu.getId())
                .setMethod("getNumFormatCode").setPos("D10").setAnswer("0.0_").setScore(3));
        quAnswerOfficeService.save(new QuAnswerOffice().setQuId(qu.getId())
                .setMethod("getFunction").setPos("H2").setAnswer("SUM(D2:F2)").setScore(4));
        quAnswerOfficeService.save(new QuAnswerOffice().setQuId(qu.getId())
                .setMethod("getNumValue").setPos("D10").setAnswer("1590.8333333333333").setScore(3));
    }

    /**
     * ??????????????????????????????????????????
     */
    @Test
    public void trimString() {
        List<QuAnswer> list = quAnswerService.list();
        list.forEach(item -> {
            item.setContent(item.getContent().trim());
        });
        quAnswerService.updateBatchById(list);
    }

    @Test
    public void paperTest() {
        List<String> ids = new ArrayList<>();
        ids.add("1497815335831515138");
        System.out.println(paperService.listPaperForExport(ids));
    }

    @Test
    public void migrateDB() {
        String oldIpAdd = "1.14.65.11";
        String newIpAdd = "101.43.213.207";

        List<Qu> quList = quService.list();
        quList.forEach(item -> {
            if (item.getImage().contains(oldIpAdd)) { // ????????????ip
                item.setImage(item.getImage().replace(oldIpAdd, newIpAdd));
            }
            if (item.getAnswer().contains(oldIpAdd)) { // ????????????ip
                item.setAnswer(item.getAnswer().replace(oldIpAdd, newIpAdd));
            }
        });
        quService.updateBatchById(quList);

        List<PaperQu> paperQuList = paperQuService.list();
        paperQuList.forEach(item -> {
            if (item.getAnswer().contains(oldIpAdd)) { // ???????????????????????????ip
                item.setAnswer(item.getAnswer().replace(oldIpAdd, newIpAdd));
            }
        });
        paperQuService.updateBatchById(paperQuList);
    }

    @Test
    public void testXEasyPdf() {
        String outPath = "/Users/hxx/Desktop/example.pdf";
        ExamResultRespDTO paperResult = paperService.paperResult("1497815335831515138");
        SysUser user = sysUserService.getById(paperResult.getUserId()); // ????????????
        SysDepart depart = sysDepartService.getById(user.getDepartId()); // ????????????
        PdfUtils.getPaperPdfDocument(new XEasyPdfDocument(), paperResult, user, depart).save(outPath).close();
    }
}
