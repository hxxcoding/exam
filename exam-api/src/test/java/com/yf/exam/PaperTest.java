package com.yf.exam;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yf.exam.core.utils.poi.WordUtils;
import com.yf.exam.modules.paper.dto.ext.PaperQuDetailDTO;
import com.yf.exam.modules.paper.dto.response.ExamDetailRespDTO;
import com.yf.exam.modules.paper.service.PaperService;
import com.yf.exam.modules.qu.entity.Qu;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;
import com.yf.exam.modules.qu.enums.QuType;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import com.yf.exam.modules.qu.service.QuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class PaperTest {

    @Autowired
    private PaperService examService;
    @Autowired
    private QuAnswerOfficeService quAnswerOfficeService;
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
        System.out.println("分栏：" + docx.getColsNum(39) + " 分栏分割线" + docx.isColsLine(39));
        System.out.println("插入图片环绕类型：" + docx.getPicTextAround(4));
    }

    @Test
    public void testOfficeSave() {
        Qu qu = quService.getOne(new QueryWrapper<Qu>().lambda().eq(Qu::getQuType, QuType.SAQ));
        quAnswerOfficeService.save(new QuAnswerOffice().setQuId(qu.getId())
                .setMethod("getIndentationHanging").setPos(2).setAnswer("420").setScore(3));
        quAnswerOfficeService.save(new QuAnswerOffice().setQuId(qu.getId())
                .setMethod("getFontSize").setPos(2).setAnswer("13").setScore(3));
        quAnswerOfficeService.save(new QuAnswerOffice().setQuId(qu.getId())
                .setMethod("getUnderlineType").setPos(2).setAnswer("WAVY_DOUBLE").setScore(3));
        quAnswerOfficeService.save(new QuAnswerOffice().setQuId(qu.getId())
                .setMethod("getPgMarLeft").setAnswer("1985").setScore(3));
    }

    @Test
    public void testOffice() {
        WordUtils docx = new WordUtils("/Users/hxx/Desktop/test.docx");
        List<QuAnswerOffice> officeAnswers = quAnswerOfficeService.list();
        int totalScore = 0;
        for (QuAnswerOffice officeAnswer : officeAnswers) {
            String answer = docx.executeMethod(officeAnswer.getMethod(), officeAnswer.getPos()).toString();
            System.out.println(answer + "," + officeAnswer.getAnswer());
            if (officeAnswer.getAnswer().equals(answer)) {
                totalScore += officeAnswer.getScore();
            }
        }
        System.out.println(totalScore);
    }
}
