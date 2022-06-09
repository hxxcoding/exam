package com.yf.exam.core.utils.poi;

import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.Reflections;
import com.yf.exam.core.utils.StringUtils;
import com.yf.exam.modules.qu.dto.WordParagraphDTO;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColumns;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFramePr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordUtils {

    private final XWPFDocument xwpfDocument;

    public WordUtils(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) { // 自动关闭
            xwpfDocument = new XWPFDocument(fis);
        } catch (IOException e) {
            throw new ServiceException("文件读取失败,请检查文件是否上传成功或文件格式是否正确");
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
            return Reflections.invokeMethod(this, methodName, classes, args);
        } catch (Exception e) { // 捕获所有异常,发生异常表示获取不到对应答案,判错
            return null;
        }
    }

    /**
     * 分析word文件段落
     * @return
     */
    public static List<WordParagraphDTO> analyzeWordParagraph(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            XWPFDocument xwpfDocument = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = xwpfDocument.getParagraphs();
            List<WordParagraphDTO> paragraphText = new ArrayList<>();
            for (int i = 0; i < paragraphs.size(); i++) {
                if (!StringUtils.isBlank(paragraphs.get(i).getText().trim())) {
                    paragraphText.add(new WordParagraphDTO(String.valueOf(i), paragraphs.get(i).getText()));
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
     * 获取上页边距
     * @return
     */
    public Integer getPgMarTop() {
        return xwpfDocument.getDocument().getBody().getSectPr().getPgMar().getTop().intValue();
    }

    /**
     * 获取下页边距
     * @return
     */
    public Integer getPgMarBottom() {
        return xwpfDocument.getDocument().getBody().getSectPr().getPgMar().getBottom().intValue();
    }

    /**
     * 获取装订线
     * @return
     */
    public Integer getPgMarGutter() {
        return xwpfDocument.getDocument().getBody().getSectPr().getPgMar().getGutter().intValue();
    }

    /**
     * 获取页眉距边缘
     * @return 页眉距边缘
     */
    public Integer getPgMarHeader() {
        return xwpfDocument.getDocument().getBody().getSectPr().getPgMar().getHeader().intValue();
    }

    /**
     * 获取页脚距边缘
     * @return 页脚距边缘
     */
    public Integer getPgMarFooter() {
        return xwpfDocument.getDocument().getBody().getSectPr().getPgMar().getFooter().intValue();
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
        return xwpfDocument.getDocument().getBody().getPList().get(pos + 1)
                .getPPr().getSectPr().getCols().getNum().intValue();
    }

    /**
     * 获取某个段落的是否存在分栏分隔线
     * 默认原始文件未分栏之后该段落的 前后各增加 一个paragraph
     * 原文字在段1, 分栏后文字在段2, 分栏数据在段3
     * @param pos
     * @return
     */
    public Boolean isColsLine(Integer pos) {
        CTColumns cols = xwpfDocument.getDocument().getBody().getPList().get(pos + 1)
                .getPPr().getSectPr().getCols();
        try {
            return cols.getSep().toString().equals("1");
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * 获取某个段落的首行缩进 未缩进返回-1
     * @param pos
     * @return
     */
    public Integer getIndentationFirstLine(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getIndentationFirstLine();
    }

    /**
     * 获取某个段落的悬挂缩进 未缩进返回-1
     * @param pos
     * @return
     */
    public Integer getIndentationHanging(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getIndentationHanging();
    }

    /**
     * 获取左侧缩进
     * @param pos
     * @return
     */
    public Integer getIndentationLeft(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getIndentationLeft();
    }

    /**
     * 获取右侧缩进
     * @param pos
     * @return
     */
    public Integer getIndentationRight(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getIndentationRight();
    }

    /**
     * 获取某个段落的行间距 默认-1.0
     * @param pos
     * @return
     */
    public Double getSpacingBetween(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getSpacingBetween();
    }

    /**
     * 获取某个段落的段前间距
     * @param pos
     * @return
     */
    public Integer getSpacingBeforeLines(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getSpacingBeforeLines();
    }

    /**
     * 获取某个段落的段后间距
     * @param pos
     * @return
     */
    public Integer getSpacingAfterLines(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getSpacingAfterLines();
    }

    /**
     * 获取某个段落的项目符号
     * @param pos
     * @return
     */
    public String getNumFmt(Integer pos) {
        String numFmt = xwpfDocument.getParagraphs().get(pos).getNumFmt();
        if (numFmt != null) {
            return numFmt;
        } else {
            throw new ServiceException("不存在`项目符号`数据");
        }
    }

    /**
     * 获取某个段落的字体大小 默认-1
     * @param pos 段落
     * @return 字体大小
     */
    public Integer getFontSize(Integer pos) {
        return pgetFirstRun(pos).getFontSize();
    }

    /**
     * 获取某个段落的字体
     * @param pos 段落
     * @return 字体
     */
    public String getFontFamily(Integer pos) {
        List<XWPFRun> runs = xwpfDocument.getParagraphs().get(pos).getRuns();
        String fontFamily = runs.get(0).getFontFamily();
        if (fontFamily != null) {
            return fontFamily;
        } else {
            throw new ServiceException("不存在`字体`数据");
        }
    }

    /**
     * 获取某个段落中的某一句话的字体
     * @param paraPos 段落
     * @param runPos 句子
     * @return 字体
     */
    public String getFontFamily(Integer paraPos, Integer runPos) {
        List<XWPFRun> runs = xwpfDocument.getParagraphs().get(paraPos).getRuns();
        String fontFamily = runs.get(runPos).getFontFamily();
        if (fontFamily != null) {
            return fontFamily;
        } else {
            throw new ServiceException("不存在`字体`数据");
        }
    }

    /**
     * 获取颜色
     * @param pos
     * @return
     */
    public String getColor(Integer pos) {
        return xwpfDocument.getParagraphArray(pos).getRuns().get(0).getColor();
    }

    /**
     * 获取默认中文字体
     * @return 字体
     */
    public String getDefaultChineseFontFamily() throws IOException, XmlException {
        String fontFamily = xwpfDocument.getStyle().getDocDefaults().getRPrDefault().getRPr().getRFonts().getEastAsia();
        if (fontFamily != null) {
            return fontFamily;
        } else {
            throw new ServiceException("不存在`字体`数据");
        }
    }

    /**
     * 获取默认英文字体
     * @return 字体
     */
    public String getDefaultAsciiFontFamily() throws IOException, XmlException {
        String fontFamily = xwpfDocument.getStyle().getDocDefaults().getRPrDefault().getRPr().getRFonts().getAscii();
        if (fontFamily != null) {
            return fontFamily;
        } else {
            throw new ServiceException("不存在`字体`数据");
        }
    }

    /**
     * 获取某个段落的下划线类型
     * @param pos 段落
     * @return
     */
    public String getUnderlineType(Integer pos) {
        UnderlinePatterns underline = pgetFirstRun(pos).getUnderline();
        if (underline != UnderlinePatterns.NONE) {
            return underline.toString();
        } else {
            throw new ServiceException("不存在`下划线`数据");
        }
    }

    /**
     * 获取某个段落是否加粗
     * @param pos 段落
     * @return
     */
    public Boolean isBold(Integer pos) {
        return pgetFirstRun(pos).isBold();
    }

    /**
     * 获取某个段落是否斜体
     * @param pos 段落
     * @return
     */
    public Boolean isItalic(Integer pos) {
        return pgetFirstRun(pos).isItalic();
    }

    /**
     * 获取文字环绕方式
     * @param pos 图片位于第三段到第四段之间 此时pos = 3
     * @return
     */
    public String getPicTextAround(Integer pos) {
        return pgetFirstRun(pos).getCTR()
                .getDrawingArray(0).getAnchorArray(0).getWrapSquare().getWrapText().toString();
    }

    /**
     * 获取对齐方式
     * @param pos
     * @return
     */
    public String getAlignment(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getAlignment().toString();
    }

    /**
     * 获取着重号样式
     * @param pos
     * @return
     */
    public String getEmphasisMark(Integer pos) {
        return pgetFirstRun(pos).getEmphasisMark().toString();
    }

    /**
     * 获取首字下沉格式和行数
     * @param pos
     * @return
     */
    public String getDropCapAndLines(Integer pos) {
        final CTFramePr framePr = xwpfDocument.getParagraphs().get(pos).getCTP().getPPr().getFramePr();
        return String.valueOf(framePr.getDropCap()) + framePr.getLines();
    }

    /**
     * 获取句子底纹填充色
     * @param pos
     * @return
     */
    public String getShadingFillWithRun(Integer pos) {
        byte[] colors = (byte[]) pgetFirstRun(pos).getCTR().getRPr().getShd().getFill();
        return StringUtils.bytesToHexString(colors);
    }

    /**
     * 获取段落底纹填充色
     * @param pos
     * @return
     */
    public String getShadingFillWithPara(Integer pos) {
        byte[] colors = (byte[]) xwpfDocument.getParagraphArray(pos).getCTP().getPPr().getShd().getFill();
        return StringUtils.bytesToHexString(colors);
    }

    private XWPFRun pgetFirstRun(Integer pos) {
        return xwpfDocument.getParagraphs().get(pos).getRuns().get(0);
    }

    /**
     * 获取表格行数
     * @return
     */
    public Integer getTableRowNum() {
        return xwpfDocument.getTableArray(0).getNumberOfRows();
    }

    /**
     * 获取表格列数
     * @return
     */
    public Integer getTableColNum() {
        return xwpfDocument.getTableArray(0).getRow(0).getTableCells().size();
    }

}
