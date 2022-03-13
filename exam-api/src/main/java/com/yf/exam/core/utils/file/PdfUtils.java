package com.yf.exam.core.utils.file;

import com.yf.exam.core.utils.StringUtils;
import com.yf.exam.modules.paper.dto.ext.PaperQuAnswerExtDTO;
import com.yf.exam.modules.paper.dto.ext.PaperQuDetailDTO;
import com.yf.exam.modules.paper.dto.response.ExamResultRespDTO;
import com.yf.exam.modules.qu.enums.QuType;
import com.yf.exam.modules.sys.depart.entity.SysDepart;
import com.yf.exam.modules.sys.user.entity.SysUser;
import wiki.xsx.core.pdf.component.XEasyPdfComponent;
import wiki.xsx.core.pdf.doc.XEasyPdfDocument;
import wiki.xsx.core.pdf.doc.XEasyPdfPositionStyle;
import wiki.xsx.core.pdf.handler.XEasyPdfHandler;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  PDF工具类
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022/3/13 15:39
 */
public class PdfUtils {
    /**
     * 生成试卷pdf
     */
    public static XEasyPdfDocument getPaperPdfDocument(XEasyPdfDocument xEasyPdfDocument,
                                                       ExamResultRespDTO resp, SysUser user, SysDepart depart) {
        List<XEasyPdfComponent> texts = new ArrayList<>();
        // 试卷名称
        texts.add(XEasyPdfHandler.Text.build(resp.getTitle())
                .setHorizontalStyle(XEasyPdfPositionStyle.CENTER)
                .setFontSize(16F));
        // 试卷信息
        texts.add(XEasyPdfHandler.Table.build(
                XEasyPdfHandler.Table.Row.build(
                        XEasyPdfHandler.Table.Row.Cell.build(135F, 30F).addContent(
                                XEasyPdfHandler.Text.build("考生姓名")
                        ),
                        XEasyPdfHandler.Table.Row.Cell.build(135F, 30F).addContent(
                                XEasyPdfHandler.Text.build(user.getRealName())
                        ),
                        XEasyPdfHandler.Table.Row.Cell.build(135F, 30F).addContent(
                                XEasyPdfHandler.Text.build("考生学号")
                        ),
                        XEasyPdfHandler.Table.Row.Cell.build(135F, 30F).addContent(
                                XEasyPdfHandler.Text.build(user.getId())
                        )
                ),
                XEasyPdfHandler.Table.Row.build(
                        XEasyPdfHandler.Table.Row.Cell.build(135F, 30F).addContent(
                                XEasyPdfHandler.Text.build("考场座位号")
                        ),
                        XEasyPdfHandler.Table.Row.Cell.build(135F, 30).addContent(
                                XEasyPdfHandler.Text.build(resp.getSeat())
                        ),
                        XEasyPdfHandler.Table.Row.Cell.build(135F, 30F).addContent(
                                XEasyPdfHandler.Text.build("考试时长")
                        ),
                        XEasyPdfHandler.Table.Row.Cell.build(135F, 30).addContent(
                                XEasyPdfHandler.Text.build(resp.getUserTime().toString() + "min")
                        )
                ),
                XEasyPdfHandler.Table.Row.build(
                        XEasyPdfHandler.Table.Row.Cell.build(135F, 30F).addContent(
                                XEasyPdfHandler.Text.build("考生选课号")
                        ),
                        XEasyPdfHandler.Table.Row.Cell.build(405F, 30F).addContent(
                                XEasyPdfHandler.Text.build(depart.getDeptName())
                        )
                ),
                XEasyPdfHandler.Table.Row.build(
                        XEasyPdfHandler.Table.Row.Cell.build(135F, 30F).addContent(
                                XEasyPdfHandler.Text.build("考试时间")
                        ),
                        XEasyPdfHandler.Table.Row.Cell.build(405F, 30F).addContent(
                                XEasyPdfHandler.Text.build(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(resp.getCreateTime())
                                        + "~" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(resp.getUpdateTime()))
                        )
                ),
                XEasyPdfHandler.Table.Row.build(
                        XEasyPdfHandler.Table.Row.Cell.build(135F, 30F).addContent(
                                XEasyPdfHandler.Text.build("总得分")
                        ),
                        XEasyPdfHandler.Table.Row.Cell.build(405F, 30F).addContent(
                                XEasyPdfHandler.Text.build(resp.getUserScore().toString())
                        ).setFontColor(Color.RED)
                )).enableCenterStyle().setMarginLeft(30F));  // 表格
        // 试卷题目
        for (int i = 0; i < resp.getQuList().size(); i++) {
            PaperQuDetailDTO qu = resp.getQuList().get(i);
            String filter = StringUtils.filterHtml(qu.getContent());
            String[] contents = filter.split("\n");
            texts.add(XEasyPdfHandler.Text.build(i + 1 + "." + contents[0]).setMarginLeft(30F).setMarginRight(30F));
            for (int j = 1; j < contents.length; j++) {
                texts.add(XEasyPdfHandler.Text.build(contents[j]).setMarginLeft(30F).setMarginRight(30F));
            }
            if (qu.getQuType().equals(QuType.RADIO) || qu.getQuType().equals(QuType.MULTI) || qu.getQuType().equals(QuType.JUDGE)) {
                StringBuilder userAnswer = new StringBuilder();
                for (PaperQuAnswerExtDTO answer : qu.getAnswerList()) {
                    texts.add(XEasyPdfHandler.Text.build(answer.getAbc() + "." + answer.getContent()).setMarginLeft(30F).setMarginRight(30F));
                    if (answer.getChecked()) userAnswer.append(answer.getAbc());
                }
                if (userAnswer.length() == 0) userAnswer.append("未作答");
                int score = qu.getIsRight() ? qu.getScore() : 0;
                texts.add(XEasyPdfHandler.Text.build("用户作答: " + userAnswer + "  得分: " + score).setFontColor(Color.RED).setMarginLeft(30F).setMarginRight(30F));
            }
            if (qu.getQuType().equals(QuType.BLANK) || qu.getQuType().equals(QuType.WORD) || qu.getQuType().equals(QuType.EXCEL) || qu.getQuType().equals(QuType.PPT)) {
                String userAnswer = StringUtils.isBlank(qu.getAnswer()) ? "未作答" : qu.getAnswer();
                int score;
                if (qu.getQuType().equals(QuType.BLANK)) {
                    score = qu.getIsRight() ? qu.getScore() : 0;
                } else {
                    score = qu.getActualScore();
                }
                texts.add(XEasyPdfHandler.Text.build("用户作答: " + userAnswer + "  得分: " + score).setFontColor(Color.RED).setMarginLeft(30F).setMarginRight(30F));
            }
            texts.add(XEasyPdfHandler.SplitLine.DottedLine.build().setMarginLeft(30F).setMarginRight(30F).setMarginTop(10F).setMarginBottom(10F));
        }
        return xEasyPdfDocument.addPage(XEasyPdfHandler.Page.build(texts));
    }
}
