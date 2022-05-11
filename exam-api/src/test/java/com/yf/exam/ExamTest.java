package com.yf.exam;


import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGradientPaint;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.junit.Test;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.presentationml.x2006.main.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ExamTest {

    @Test
    public void testOnline() throws Exception {
        XWPFDocument docx = new XWPFDocument(new FileInputStream("/Users/hxx/Desktop/lianxi1answer.docx"));
        CTDocument1 document = docx.getDocument();
        System.out.println(docx.getParagraphArray(0).getAlignment());
        System.out.println(docx.getParagraphArray(0).getText());
    }

    @Test
    public void testWordUtils() throws IOException {
        XWPFDocument xwpfDocument = new XWPFDocument(new FileInputStream("/Users/hxx/Desktop/lianxi1answer.docx"));
        System.out.println(xwpfDocument.getParagraphArray(1).getSpacingBeforeLines());
        System.out.println(xwpfDocument.getParagraphArray(1).getSpacingAfterLines());
    }

    @Test
    public void testExcelUtils() throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream("/Users/hxx/Desktop/answer1.xlsx"));
        System.out.println(xssfWorkbook.getSheetAt(1).getSheetName()); // 获取第0个表的表名称
        System.out.println(xssfWorkbook.getNumberOfSheets()); // 获取表的数量
//        System.out.println(xssfWorkbook.getCalculationChain().getCTCalcChain()); // 获取计算链
        List<CellRangeAddress> mergedRegions = xssfWorkbook.getSheetAt(0).getMergedRegions();
        for (CellRangeAddress mergedRegion : mergedRegions) {
            System.out.println(mergedRegion.formatAsString()); // 获取合并的单元格
        }

        XSSFSheet sheet1 = xssfWorkbook.getSheetAt(0);
        CellAddress cellAddress = new CellAddress("A1");
        XSSFCell cell = xssfWorkbook.getSheetAt(0).getRow(cellAddress.getRow()).getCell(cellAddress.getColumn());
        RichTextString richTextString = xssfWorkbook.getSharedStringSource().getSharedStringItems().get(Integer.parseInt(cell.getRawValue()));
        // 获取单元格中的中文内容
        System.out.println(richTextString);

        short styleIndex = cell.getCellStyle().getIndex();
        CTXf cellXf = xssfWorkbook.getStylesSource().getCellXfAt(styleIndex);
//        long fontId = xssfWorkbook.getStylesSource().getCTStylesheet().getCellXfs().getXfArray(styleIndex).getFontId();
        XSSFFont font = xssfWorkbook.getStylesSource().getFontAt((int) cellXf.getFontId());
        // 获取某个单元格的字体
        System.out.println(font.getFontName()); // 字体名称
        System.out.println(font.getBold()); // 是否加粗
        System.out.println(font.getFontHeightInPoints()); // 获取字体榜数大小
        System.out.println(font.getThemeColor());

        System.out.println(cellXf.getAlignment().getHorizontal()); // 获取水平对齐方式
        System.out.println(cellXf.getAlignment().getVertical()); // 获取垂直对齐方式
        long fillId = cellXf.getFillId();
        XSSFCellFill fill = xssfWorkbook.getStylesSource().getFillAt((int) fillId);
        System.out.println(fill.getCTFill());
        StringBuilder foreColor = new StringBuilder();
        StringBuilder backColor = new StringBuilder();
        byte[] foreRGB = fill.getFillForegroundColor().getRGB();
        for (byte color : foreRGB) {
            foreColor.append(String.format("%02X", color));
        }
        System.out.println(foreColor); // 图案颜色

//        byte[] backRGB = fill.getFillBackgroundColor().getRGB();
//        for (byte color : backRGB) {
//            backColor.append(String.format("%02X", color));
//        }
//        System.out.println(backColor); // 背景颜色

        System.out.println(fill.getPatternType()); // 获取图案类型

        System.out.println(xssfWorkbook.getSheetAt(0).getRow(1).getHeightInPoints()); // 获取行高
        System.out.println(xssfWorkbook.getSheetAt(0).getColumnWidth(1)); // 获取列宽

        System.out.println(xssfWorkbook.getSheetAt(0).getPrintSetup().getPaperSizeEnum()); // 获取页面纸张类型
        System.out.println(xssfWorkbook.getSheetAt(0).getPrintSetup().getOrientation().name()); // LANDSCAPE - 横向 / PORTRAIT - 纵向
        System.out.println(xssfWorkbook.getSheetAt(0).getPrintSetup().getHeaderMargin()); // 页眉边距
        System.out.println(xssfWorkbook.getSheetAt(0).getPrintSetup().getFooterMargin()); // 页脚边距
        System.out.println(xssfWorkbook.getSheetAt(0).getPrintSetup().getTopMargin()); // 上边距
        System.out.println(xssfWorkbook.getSheetAt(0).getPrintSetup().getBottomMargin()); // 下边距
        System.out.println(xssfWorkbook.getSheetAt(0).getPrintSetup().getLeftMargin()); // 左页边距
        System.out.println(xssfWorkbook.getSheetAt(0).getPrintSetup().getRightMargin()); // 右页边距
        System.out.println(xssfWorkbook.getSheetAt(0).getHorizontallyCenter()); // 是否水平居中
        System.out.println(xssfWorkbook.getSheetAt(0).getVerticallyCenter()); // 是否垂直居中
        System.out.println(xssfWorkbook.getSheetAt(0).getCTWorksheet().getPrintOptions().getGridLines()); // 是否有网格线
        System.out.println(xssfWorkbook.getSheetAt(0).getCTWorksheet().getPrintOptions().getHeadings()); // 是否有行列号

        System.out.println(xssfWorkbook.getSheetAt(0).getHeaderFooterProperties().getHeaderFooter().getOddHeader()); // 获取页眉
        System.out.println(xssfWorkbook.getSheetAt(0).getHeaderFooterProperties().getHeaderFooter().getOddFooter()); // 获取页脚

        System.out.println(xssfWorkbook.getSheetAt(0).getSheetConditionalFormatting().getConditionalFormattingAt(0)
                .getRule(0).getConditionType().type);
        System.out.println(xssfWorkbook.getSheetAt(0).getSheetConditionalFormatting().getConditionalFormattingAt(1)
                .getRule(0).getPriority()); // 获取优先级
        System.out.println(xssfWorkbook.getSheetAt(0).getCTWorksheet().getConditionalFormattingArray(0)
                .getCfRuleArray(0).getOperator()); // 比较条件 例如 lessThan
        System.out.println(xssfWorkbook.getSheetAt(0).getCTWorksheet().getConditionalFormattingArray(0)
                .getCfRuleArray(0).getFormulaArray(0)); // 获取比较的公式
        final long dxfId = xssfWorkbook.getSheetAt(0).getCTWorksheet().getConditionalFormattingArray(0)
                .getCfRuleArray(0).getDxfId();
        final CTDxf dxf = xssfWorkbook.getStylesSource().getDxfAt((int) dxfId);
        final byte[] rgb = dxf.getFill().getPatternFill().getBgColor().getRgb();
    }

    @Test
    public void testExcelUtilsCompare() throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream("/Users/hxx/Desktop/answer1.xlsx"));
        System.out.println(xssfWorkbook.getSheetAt(0).getCTWorksheet());
//        System.out.println(xssfWorkbook.getStylesSource().getCTStylesheet());
    }

    @Test
    public void testExcel2() throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream("/Users/hxx/Desktop/answer2.xlsx"));
        final XSSFChart xssfChart = xssfWorkbook.getSheetAt(0).getDrawingPatriarch().getCharts().get(0);
        final CTBar3DChart bar3DChart = xssfChart.getCTChart().getPlotArea().getBar3DChartArray(0);
        System.out.println(xssfChart.getTitleText());
        final List<CTBarSer> serList = bar3DChart.getSerList();
        System.out.println(serList.get(0).getTx().getStrRef().getStrCache().getPtArray(0).getV()); // 列名称
        System.out.println(serList.get(0).getTx().getStrRef().getF()); // 列单元格
        System.out.println(serList.get(0).getVal().getNumRef().getF()); // 数据范围
        System.out.println(serList.get(0).getCat().getStrRef().getF()); // 列范围
        System.out.println(serList.get(1).getTx().getStrRef().getStrCache().getPtArray(0).getV());
        System.out.println(serList.get(1).getTx().getStrRef().getF());
        System.out.println(serList.get(1).getVal().getNumRef().getF());
        System.out.println(serList.get(1).getCat().getStrRef().getF());
        System.out.println(serList.get(2).getTx().getStrRef().getStrCache().getPtArray(0).getV());
        System.out.println(serList.get(2).getTx().getStrRef().getF());
        System.out.println(serList.get(2).getVal().getNumRef().getF());
        System.out.println(serList.get(2).getCat().getStrRef().getF());
        System.out.println(serList.get(0).getDLbls().getShowVal().getVal());
        System.out.println(xssfChart.getCTChart().getPlotArea().getCatAxArray(0)
                .getTitle().getTx().getRich().getPArray(0).getRArray(0).getT()); // 行标题
        System.out.println(xssfChart.getCTChart().getPlotArea().getValAxArray(0)
                .getTitle().getTx().getRich().getPArray(0).getRArray(0).getT()); // 值标题
        System.out.println(xssfChart.getCTChart().getPlotArea().getValAxArray(0)
                .getMajorUnit().getVal()); // 纵坐标轴间隔
    }

    @Test
    public void testExcel3() throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream("/Users/hxx/Desktop/answer2.xlsx"));
//        System.out.println(xssfWorkbook.getSheetAt(0).getCTWorksheet());
        System.out.println(xssfWorkbook.getSheetAt(0).getCTWorksheet()
                .getSortState().getRef()); // 排序影响范围
        System.out.println(xssfWorkbook.getSheetAt(0).getCTWorksheet()
                .getSortState().getSortConditionArray(0).getRef()); // 排序条件1
        System.out.println(xssfWorkbook.getSheetAt(0).getCTWorksheet()
                .getSortState().getSortConditionArray(0).getDescending()); // 排序条件1降序
        System.out.println(xssfWorkbook.getSheetAt(0).getCTWorksheet().getAutoFilter().getRef()); // 筛选范围
        final List<CTFilterColumn> filterColumnList = xssfWorkbook.getSheetAt(0).getCTWorksheet().getAutoFilter().getFilterColumnList();
        System.out.println(filterColumnList.get(0).getFilters().getFilterArray(0).getVal()); // 过滤选定值
        System.out.println(filterColumnList.get(0).getFilters().getFilterList().stream().map(CTFilter::getVal).collect(Collectors.joining(","))); // 过滤选定值
        final List<CTCustomFilter> customFilterList = filterColumnList.get(1).getCustomFilters().getCustomFilterList();
        StringBuilder sb = new StringBuilder();
        for (CTCustomFilter item : customFilterList) {
            sb.append(item.getOperator()).append(item.getVal()).append(".");
        }
        System.out.println(sb);
    }

    @Test
    public void testPPT () throws IOException {
        XMLSlideShow xmlSlideShow = new XMLSlideShow(new FileInputStream("/Users/hxx/Desktop/answer.pptx"));
        final List<XSLFSlide> slides = xmlSlideShow.getSlides();
        final XSLFSlide slide = slides.get(4);
        System.out.println(slide.getSlideLayout().getName()); // 获取版式

        final List<CTShape> spList = slide.getXmlObject().getCSld().getSpTree().getSpList();
        final CTShape shape = spList.get(0);
        System.out.println(shape.getTxBody().getPArray(0).getRArray(0).getRPr().getSz()); // 获取标题文字

//        final CTGraphicalObjectFrame graphic = slides.get(1).getXmlObject().getCSld().getSpTree().getGraphicFrameArray(0);
//        System.out.println(graphic.getGraphic().getGraphicData()); // 无法深入获取
//        System.out.println(slides.get(3).getXmlObject().getCSld().getSpTree().getSpList().get(2).getSpPr().getPrstGeom().getPrst());

        final PaintStyle paint = slides.get(0).getBackground().getFillStyle().getPaint();
        System.out.println(paint.getClass().getSimpleName()); // 背景填充类型
        System.out.println(xmlSlideShow.getSlideMasters().size());

        XSLFSlide xslfSlide = xmlSlideShow.getSlides().get(3);
        System.out.println(xslfSlide.getXmlObject());
//        CTTLCommonTimeNodeData mainSeq = xslfSlide.getXmlObject().getTiming().getTnLst().getParArray(0).getCTn()
//                .getChildTnLst().getSeqArray(0).getCTn();
//        CTTLTimeNodeParallel timeNodeParallel = mainSeq.getChildTnLst().getParArray(0);// 第actionPos个动画
//        CTTimeNodeList childTnLst = timeNodeParallel.getCTn().getChildTnLst().getParArray(0)
//                .getCTn().getChildTnLst().getParArray(0).getCTn().getChildTnLst();
//        System.out.println(childTnLst);
    }
}
