package com.yf.exam;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yf.exam.core.utils.poi.ExcelUtils;
import com.yf.exam.core.utils.poi.WordUtils;
import com.yf.exam.modules.paper.dto.ext.PaperQuDetailDTO;
import com.yf.exam.modules.paper.dto.response.ExamDetailRespDTO;
import com.yf.exam.modules.paper.service.PaperService;
import com.yf.exam.modules.qu.entity.Qu;
import com.yf.exam.modules.qu.entity.QuAnswer;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;
import com.yf.exam.modules.qu.enums.QuType;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import com.yf.exam.modules.qu.service.QuAnswerService;
import com.yf.exam.modules.qu.service.QuService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class PaperTest {

    @Autowired
    private PaperService examService;
    @Autowired
    private QuAnswerOfficeService quAnswerOfficeService;
    @Autowired
    private QuAnswerService quAnswerService;
    @Autowired
    private QuService quService;

    @Test
    public void testCreate(){
        //examService.createPaper("aaaa", "4e867df4d47a42ad896f66fd1f9d0a8e");
    }


    @Test
    public void testPaperDetail(){
        ExamDetailRespDTO respDTO = examService.paperDetail("1264852438679150594");
        System.out.println(JSON.toJSONString(respDTO));
    }

    @Test
    public void testQuDetail(){
        PaperQuDetailDTO respDTO = examService.findQuDetail("1264872568393834497", "5859c292a6ad486da6d9ea41084affe2");
        System.out.println(JSON.toJSONString(respDTO));
    }

    @Test
    public void wordUtilsTest() {
        WordUtils docx = new WordUtils("/Users/hxx/Desktop/test.docx");
        System.out.println("纸张大小" + docx.getPgSzW() + " x " + docx.getPgSzH());
        System.out.println("上下左右页边距" + docx.getPgMarTop() + ", " + docx.getPgMarBottom() +
                ", " + docx.getPgMarLeft() + ", " + docx.getPgMarRight());
        System.out.println("首行缩进" + docx.getIndentationFirstLine(2) + " 悬挂缩进：" + docx.getIndentationHanging(2));
        System.out.println("字号：" + docx.getFontSize(2) + "，字体：" + docx.getFontFamily(2) + "\n" + " 下划线类型" +
                docx.getUnderlineType(2) + " 是否斜体：" + docx.isItalic(2) + " 是否加粗：" + docx.isBold(2) + "\n" +
                "行间距：" + docx.getSpacingBetween(2) + " 项目符号：" + docx.getNumFmt(8));
        System.out.println("分栏：" + docx.getColsNum(38) + " 分栏分割线" + docx.isColsLine(38));
        System.out.println("插入图片环绕类型：" + docx.getPicTextAround(4));
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
        WordUtils docx = new WordUtils("/Users/hxx/Desktop/office/2021word-题目要求8.docx");
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
        System.out.println("数字格式:" + xlsx.getNumFormatCode("D10"));
        System.out.println("获取公式:" + xlsx.getFunction("H2"));
        System.out.println("获取数字值:" + xlsx.getNumValue("D10"));
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
     * 删除选项内容前后的空格和换行
     */
    @Test
    public void trimString() {
        List<QuAnswer> list = quAnswerService.list();
        list.forEach(item -> {
            item.setContent(item.getContent().trim());
        });
        quAnswerService.updateBatchById(list);
    }
}
