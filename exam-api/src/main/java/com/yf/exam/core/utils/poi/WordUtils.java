package com.yf.exam.core.utils.poi;

import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.StringUtils;
import com.yf.exam.modules.qu.dto.response.AnalyzeWordRespDTO;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordUtils {

    private XWPFDocument xwpfDocument;

    public WordUtils(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            xwpfDocument = new XWPFDocument(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过方法名和参数获取方法并执行
     * @param methodName
     * @param args
     * @return
     */
    public Object executeMethod(String methodName, Object... args) {
        try {
            Class<?>[] classes = null;
            if (args[0] == null) {
                args = null;
            } else {
                classes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
            }
            return WordUtils.class.getMethod(methodName, classes).invoke(this, args);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 分析word文件段落
     * @param filePath
     * @return
     */
    public static List<AnalyzeWordRespDTO> analyzeWord(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            XWPFDocument xwpfDocument = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = xwpfDocument.getParagraphs();
            List<AnalyzeWordRespDTO> paragraphText = new ArrayList<>();
            for (int i = 0; i < paragraphs.size(); i++) {
                if (!StringUtils.isBlank(paragraphs.get(i).getText().trim())) {
                    paragraphText.add(new AnalyzeWordRespDTO(i, paragraphs.get(i).getText()));
                }
            }
            return paragraphText;
        } catch (FileNotFoundException e) {
            throw new ServiceException("文件不存在！");
        } catch (IOException e) {
            throw new ServiceException("文件解析失败！");
        }
    }

    /**
     * 获取左页边距
     * @return
     */
    public Integer getPgMarLeft() {
        return xwpfDocument.getDocument().getBody().getSectPr().getPgMar().getLeft().intValue();
    }

    /**
     * 获取右页边距
     * @return
     */
    public Integer getPgMarRight() {
        return xwpfDocument.getDocument().getBody().getSectPr().getPgMar().getRight().intValue();
    }

    /**
     * 获取右页边距
     * @return
     */
    public Integer getPgMarTop() {
        return xwpfDocument.getDocument().getBody().getSectPr().getPgMar().getTop().intValue();
    }

    /**
     * 获取右页边距
     * @return
     */
    public Integer getPgMarBottom() {
        return xwpfDocument.getDocument().getBody().getSectPr().getPgMar().getBottom().intValue();
    }

    /**
     * 纸张高度 A4:  16839 x 11907
     * @return
     */
    public Integer getPgSzH() {
        return xwpfDocument.getDocument().getBody().getSectPr().getPgSz().getH().intValue();
    }

    /**
     * 纸张宽度 A4:  16839 x 11907
     * @return
     */
    public Integer getPgSzW() {
        return xwpfDocument.getDocument().getBody().getSectPr().getPgSz().getW().intValue();
    }

    /**
     * 获取某个段落的分栏数
     * 分栏之后该段落的 前后各增加 一个paragraph
     * 原文字在段1, 分栏后文字在段2, 分栏数据在段3
     * @param pos
     * @return
     */
    public Integer getColsNum(Integer pos) {
        try {
            return xwpfDocument.getDocument().getBody().getPList().get(pos)
                    .getPPr().getSectPr().getCols().getNum().intValue();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * 获取某个段落的是否存在分栏分隔符
     * 默认原始文件未分栏之后该段落的 前后各增加 一个paragraph
     * 原文字在段1, 分栏后文字在段2, 分栏数据在段3
     * @param pos
     * @return
     */
    public Boolean isColsLine(Integer pos) {
        try {
            return xwpfDocument.getDocument().getBody().getPList().get(pos)
                    .getPPr().getSectPr().getCols().getSep().toString().equals("1");
        } catch (NullPointerException e){
            return false;
        }
    }

    /**
     * 获取某个段落的首行缩进
     * @param pos
     * @return
     */
    public Integer getIndentationFirstLine(Integer pos) {
        try {
            return xwpfDocument.getParagraphs().get(pos).getIndentationFirstLine();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * 获取某个段落的悬挂缩进
     * @param pos
     * @return
     */
    public Integer getIndentationHanging(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getIndentationHanging();
    }

    /**
     * 获取某个段落的行间距
     * @param pos
     * @return
     */
    public double getSpacingBetween(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getSpacingBetween();
    }

    /**
     * 获取某个段落的项目符号
     * @param pos
     * @return
     */
    public String getNumFmt(Integer pos) {
        try {
            return xwpfDocument.getParagraphs().get(pos).getNumFmt();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * 获取某个段落的字体大小
     * @param pos 段落
     * @return 字体大小
     */
    public Integer getFontSize(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getRuns().get(0).getFontSize();
    }

    /**
     * 获取某个段落的字体
     * @param pos 段落
     * @return 字体
     */
    public String getFontFamily(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getRuns().get(0).getFontFamily();
    }

    /**
     * 获取某个段落的下划线类型
     * @param pos 段落
     * @return
     */
    public Integer getUnderlineType(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getRuns().get(0).getUnderline().getValue();
    }

    /**
     * 获取某个段落是否有下划线
     * @param pos 段落
     * @return
     */
    public Boolean isBold(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getRuns().get(0).isBold();
    }

    /**
     * 获取某个段落是否斜体
     * @param pos 段落
     * @return
     */
    public Boolean isItalic(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getRuns().get(0).isItalic();
    }

    /**
     * 获取文字环绕方式
     * @param pos 图片位于第三段到第四段之间 此时pos = 3
     * @return
     */
    public String getPicTextAround(Integer pos) {
        try {
            return xwpfDocument.getParagraphArray(pos).getRuns().get(0).getCTR()
                    .getDrawingArray(0).getAnchorArray(0).getWrapSquare().getWrapText().toString();
        } catch (Exception e) {
            return null;
        }
    }

    public XWPFDocument getXwpfDocument() {
        return xwpfDocument;
    }

}
