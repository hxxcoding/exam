package com.yf.exam.core.utils.poi;

import com.yf.exam.core.utils.Reflections;
import com.yf.exam.core.utils.StringUtils;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFilterColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSortCondition;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * ExcelUtils
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022-01-11
 */
public class ExcelUtils {

    private XSSFWorkbook xssfWorkbook;

    public ExcelUtils(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            xssfWorkbook = new XSSFWorkbook(fis);
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
            if (StringUtils.isBlank(args[0].toString())) {
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
     * 获取单元格的数字的格式
     * @param row
     * @param cell
     * @return
     */
    public String getNumFormatCode(Integer row, Integer cell) {
        long s =  xssfWorkbook.getSheetAt(0).getRow(row).getCell(cell).getCTCell().getS();
        long numFmtId = xssfWorkbook.getStylesSource().getCellXfAt((int)s).getNumFmtId();
        Map<Short, String> numberFormats = xssfWorkbook.getStylesSource().getNumberFormats();
        return numberFormats.get((short) numFmtId);
    }

    public String getNumFormatCode(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        return getNumFormatCode(address.getRow(), address.getColumn());
    }

    /**
     * 获取工作表名称
     */
    public String getSheetName() {
        return xssfWorkbook.getSheetAt(0).getSheetName();
    }

    /**
     * 获取cell的函数
     * @param row
     * @param cell
     * @return
     */
    public String getFunction(Integer row, Integer cell) {
        return xssfWorkbook.getSheetAt(0).getRow(row).getCell(cell)
                .getCTCell().getF().getStringValue();
    }

    public String getFunction(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        return getFunction(address.getRow(), address.getColumn());
    }

    /**
     * 获取受到该单元格函数影响的范围
     * @param row
     * @param cell
     * @return
     */
    public String getFuncRefRange(Integer row, Integer cell) {
        return xssfWorkbook.getSheetAt(0).getRow(row).getCell(cell)
                .getCTCell().getF().getRef();
    }

    public String getFuncRefRange(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        return getFuncRefRange(address.getRow(), address.getColumn());
    }

    /**
     * 获取数字单元格的值
     * @param row
     * @param cell
     * @return
     */
    public String getNumValue(Integer row, Integer cell) {
        return xssfWorkbook.getSheetAt(0).getRow(row).getCell(cell)
                .getCTCell().getV();
    }

    public String getNumValue(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        return xssfWorkbook.getSheetAt(0).getRow(address.getRow()).getCell(address.getColumn())
                .getCTCell().getV();
    }

    private XSSFCellBorder pgetBorder(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        long s = xssfWorkbook.getSheetAt(0)
                .getRow(address.getRow()).getCell(address.getColumn()).getCTCell().getS();
        long borderId = xssfWorkbook.getStylesSource().getCellXfAt((int) s).getBorderId();
        return xssfWorkbook.getStylesSource().getBorderAt((int) borderId);
    }

    /**
     * 获取某个单元格顶部边框的类型
     */
    public String getBorderStyleTop(String cellAddress) {
        XSSFCellBorder border = pgetBorder(cellAddress);
        return border.getBorderStyle(XSSFCellBorder.BorderSide.TOP).toString();
    }

    /**
     * 获取某个单元格底部边框的类型
     */
    public String getBorderStyleBottom(String cellAddress) {
        XSSFCellBorder border = pgetBorder(cellAddress);
        return border.getBorderStyle(XSSFCellBorder.BorderSide.BOTTOM).toString();
    }

    /**
     * 获取某个单元格左边边框的类型
     */
    public String getBorderStyleLeft(String cellAddress) {
        XSSFCellBorder border = pgetBorder(cellAddress);
        return border.getBorderStyle(XSSFCellBorder.BorderSide.LEFT).toString();
    }

    /**
     * 获取某个单元格右边边框的类型
     */
    public String getBorderStyleRight(String cellAddress) {
        XSSFCellBorder border = pgetBorder(cellAddress);
        return border.getBorderStyle(XSSFCellBorder.BorderSide.RIGHT).toString();
    }

    /**
     * 获取某个单元格顶部边框的颜色
     */
    public String getBorderColorTop(String cellAddress) {
        XSSFCellBorder border = pgetBorder(cellAddress);
        final byte[] rgb = border.getBorderColor(XSSFCellBorder.BorderSide.TOP).getRGB();
        return StringUtils.bytesToHexString(rgb);
    }

    /**
     * 获取某个单元格底部边框的颜色
     */
    public String getBorderColorBottom(String cellAddress) {
        XSSFCellBorder border = pgetBorder(cellAddress);
        final byte[] rgb = border.getBorderColor(XSSFCellBorder.BorderSide.BOTTOM).getRGB();
        return StringUtils.bytesToHexString(rgb);
    }

    /**
     * 获取某个单元格左边边框的颜色
     */
    public String getBorderColorLeft(String cellAddress) {
        XSSFCellBorder border = pgetBorder(cellAddress);
        final byte[] rgb = border.getBorderColor(XSSFCellBorder.BorderSide.LEFT).getRGB();
        return StringUtils.bytesToHexString(rgb);
    }

    /**
     * 获取某个单元格右边边框的颜色
     * @return
     */
    public String getBorderColorRight(String cellAddress) {
        XSSFCellBorder border = pgetBorder(cellAddress);
        final byte[] rgb = border.getBorderColor(XSSFCellBorder.BorderSide.RIGHT).getRGB();
        return StringUtils.bytesToHexString(rgb);
    }

    /**
     * 获取合并的单元格
     * @param cellAddress 合并开始的坐标
     * @return
     */
    public String getMergedRegion(String cellAddress) {
        List<CellRangeAddress> mergedRegions = xssfWorkbook.getSheetAt(0).getMergedRegions();
        for (CellRangeAddress mergedRegion : mergedRegions) {
            if (mergedRegion.formatAsString().contains(cellAddress)) {
                return mergedRegion.formatAsString();
            }
        }
        return null;
    }

    /**
     * 获取某个单元格的文字内容
     * @param cellAddress
     * @return
     */
    public String getRichTextString(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        XSSFCell cell = xssfWorkbook.getSheetAt(0).getRow(address.getRow()).getCell(address.getColumn());
        RichTextString richTextString = xssfWorkbook.getSharedStringSource().getSharedStringItems().get(Integer.parseInt(cell.getRawValue()));
        // 获取单元格中的中文内容
        return richTextString.getString();
    }

    /**
     * 获取单元格样式索引
     * @param cellAddress
     * @return
     */
    private short pgetStyleIndex(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        XSSFCell cell = xssfWorkbook.getSheetAt(0).getRow(address.getRow()).getCell(address.getColumn());
        return cell.getCellStyle().getIndex();
    }

    /**
     * 获取某个单元格的字体
     * @param cellAddress
     * @return
     */
    private XSSFFont pgetFont(String cellAddress) {
        CTXf cellXf = xssfWorkbook.getStylesSource().getCellXfAt(pgetStyleIndex(cellAddress));
        return xssfWorkbook.getStylesSource().getFontAt((int) cellXf.getFontId());
    }

    /**
     * 获取某个单元格的字体名称
     * @param cellAddress
     * @return
     */
    public String getFontName(String cellAddress) {
        return Objects.requireNonNull(this.pgetFont(cellAddress)).getFontName();
    }

    /**
     * 获取某个单元格的字体大小
     * @param cellAddress
     * @return
     */
    public Short getFontSize(String cellAddress) {
        return Objects.requireNonNull(this.pgetFont(cellAddress)).getFontHeightInPoints();
    }

    /**
     * 获取某个单元格的字体是否加粗
     * @param cellAddress
     * @return
     */
    public Boolean isBold(String cellAddress) {
        return Objects.requireNonNull(this.pgetFont(cellAddress)).getBold();
    }

    /**
     * 获取水平对齐方式
     * @param cellAddress
     * @return
     */
    public String getHorizontalAlignment(String cellAddress) {
        short styleIndex = pgetStyleIndex(cellAddress);
        CTXf cellXf = xssfWorkbook.getStylesSource().getCellXfAt(styleIndex);
        if (cellXf.getAlignment().getHorizontal() != null) {
            return cellXf.getAlignment().getHorizontal().toString();
        }
        return "default";
    }
    /**
     * 获取垂直对齐方式
     * @param cellAddress
     * @return
     */
    public String getVerticalAlignment(String cellAddress) {
        short styleIndex = pgetStyleIndex(cellAddress);
        CTXf cellXf = xssfWorkbook.getStylesSource().getCellXfAt(styleIndex);
        if (cellXf.getAlignment().getVertical() != null) {
            return cellXf.getAlignment().getVertical().toString();
        }
        return "default";
    }

    /**
     * 获取背景填充颜色
     * @param cellAddress
     * @return
     */
    public String getFillBackgroundColor(String cellAddress) {
        XSSFCellFill fill = pgetCellFill(cellAddress);
        byte[] backRGB = fill.getFillBackgroundColor().getRGB();
        return StringUtils.bytesToHexString(backRGB);
    }

    /**
     * 获取图案填充颜色
     * @param cellAddress
     * @return
     */
    public String getFillForegroundColor(String cellAddress) {
        XSSFCellFill fill = pgetCellFill(cellAddress);
        byte[] foreRGB = fill.getFillForegroundColor().getRGB();
        return StringUtils.bytesToHexString(foreRGB);
    }

    /**
     * 获取图案填充类型
     * @param cellAddress
     * @return
     */
    public String getForegroundPatternType(String cellAddress) {
        XSSFCellFill fill = pgetCellFill(cellAddress);
        return fill.getPatternType().toString();
    }

    private XSSFCellFill pgetCellFill(String cellAddress) {
        short styleIndex = pgetStyleIndex(cellAddress);
        CTXf cellXf = xssfWorkbook.getStylesSource().getCellXfAt(styleIndex);
        long fillId = cellXf.getFillId();
        return xssfWorkbook.getStylesSource().getFillAt((int) fillId);
    }

    /**
     * 获取行高
     * @param cellAddress
     * @return
     */
    public Float getRowHeight(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        return xssfWorkbook.getSheetAt(0).getRow(address.getRow()).getHeightInPoints();
    }

    /**
     * 获取列宽
     * @param cellAddress
     * @return
     */
    public Integer getColWidth(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        return xssfWorkbook.getSheetAt(0).getColumnWidth(address.getColumn());
    }

    /**
     * 获取打印纸张类型
     * @return
     */
    public String getPrintPaperSize() {
        return xssfWorkbook.getSheetAt(0).getPrintSetup().getPaperSizeEnum().name();
    }

    /**
     * 获取打印方向
     * @return LANDSCAPE - 横向 / PORTRAIT - 纵向
     */
    public String getPrintOrientation() {
        return xssfWorkbook.getSheetAt(0).getPrintSetup().getOrientation().name();
    }

    /**
     * 获取打印页眉边距
     * @return
     */
    public Double getPrintHeaderMargin() {
        return xssfWorkbook.getSheetAt(0).getPrintSetup().getHeaderMargin();
    }

    /**
     * 获取打印页脚边距
     * @return
     */
    public Double getPrintFooterMargin() {
        return xssfWorkbook.getSheetAt(0).getPrintSetup().getFooterMargin();
    }

    /**
     * 获取打印上页边距
     * @return
     */
    public Double getPrintTopMargin() {
        return xssfWorkbook.getSheetAt(0).getPrintSetup().getTopMargin();
    }

    /**
     * 获取打印下页边距
     * @return
     */
    public Double getPrintBottomMargin() {
        return xssfWorkbook.getSheetAt(0).getPrintSetup().getBottomMargin();
    }

    /**
     * 获取打印左页边距
     * @return
     */
    public Double getPrintLeftMargin() {
        return xssfWorkbook.getSheetAt(0).getPrintSetup().getLeftMargin();
    }

    /**
     * 获取打印右页边距
     * @return
     */
    public Double getPrintRightMargin() {
        return xssfWorkbook.getSheetAt(0).getPrintSetup().getRightMargin();
    }

    /**
     * 打印页面是否水平居中
     * @return
     */
    public Boolean isPrintHorizontallyCenter() {
        return xssfWorkbook.getSheetAt(0).getHorizontallyCenter();
    }

    /**
     * 打印页面是否垂直居中
     * @return
     */
    public Boolean isPrintVerticallyCenter() {
        return xssfWorkbook.getSheetAt(0).getVerticallyCenter();
    }

    /**
     * 是否打印网格线
     * @return
     */
    public Boolean isPrintGridLines() {
        return xssfWorkbook.getSheetAt(0).getCTWorksheet().getPrintOptions().getGridLines();
    }

    /**
     * 是否打印行列号
     * @return
     */
    public Boolean isPrintHeadings() {
        return xssfWorkbook.getSheetAt(0).getCTWorksheet().getPrintOptions().getHeadings();
    }

    /**
     * 获取页眉内容和格式
     * @return
     */
    public String getHeaderContent() {
        return xssfWorkbook.getSheetAt(0).getHeaderFooterProperties().getHeaderFooter().getOddHeader();
    }

    /**
     * 获取页脚内容和格式
     * @return
     */
    public String getFooterContent() {
        return xssfWorkbook.getSheetAt(0).getHeaderFooterProperties().getHeaderFooter().getOddFooter();
    }

    /**
     * 获取条件格式
     * @return
     */
    public String getConditionalFormatting(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        final XSSFSheetConditionalFormatting cf = xssfWorkbook.getSheetAt(0).getSheetConditionalFormatting();
        final int count = cf.getNumConditionalFormattings();
        for (int i = 0; i < count; i++) {
            final XSSFConditionalFormatting formattingItem = cf.getConditionalFormattingAt(i);
            final CellRangeAddress[] ranges = formattingItem.getFormattingRanges();
            for (CellRangeAddress range : ranges) {
                if (range.isInRange(address)) { // cellAddress在range之中
                    StringBuilder res = new StringBuilder();
                    for (int k = 0; k < formattingItem.getNumberOfRules(); k++) {
                        final XSSFConditionalFormattingRule rule = formattingItem.getRule(k);
                        res.append(rule.getConditionType().type).append("&");
                        res.append(rule.getComparisonOperation()).append("&");
                        res.append(rule.getFormula1()).append("&");
                    }
                    return res.substring(0, res.length() - 1);
                }
            }
        }
        return null;
    }

    private XSSFChart pgetXSSFChart() {
        return xssfWorkbook.getSheetAt(0).getDrawingPatriarch().getCharts().get(0);
    }

    private CTBarChart pgetBarChart() {
        final CTPlotArea plotArea = pgetXSSFChart().getCTChart().getPlotArea();
        if (plotArea.getBarChartList().size() != 0) { // 二维柱状图
            return plotArea.getBarChartArray(0);
        }
        return null;
    }

    private CTBar3DChart pget3DBarChart() {
        final CTPlotArea plotArea = pgetXSSFChart().getCTChart().getPlotArea();
        if (plotArea.getBar3DChartList().size() != 0) { // 二维柱状图
            return plotArea.getBar3DChartArray(0);
        }
        return null;
    }

    /**
     * 获取图表名称
     * @return
     */
    public String getChartTitle() {
        return pgetXSSFChart().getTitleText().getString();
    }

    /**
     * 获取cat坐标(横坐标)标题
     * xssfChart.getCTChart().getPlotArea().getCatAxArray(0).getTitle().getTx()
     *          .getRich().getPArray(0).getRArray(0).getRPr().getSz(); // 行标题大小
     * xssfChart.getCTChart().getPlotArea().getCatAxArray(0).getTitle().getTx()
     *          .getRich().getPArray(0).getRArray(0).getRPr().getEa(); // 中文字体名称
     * @return
     */
    public String getChartCatTitle() {
        return pgetXSSFChart().getCTChart().getPlotArea().getCatAxArray(0)
                .getTitle().getTx().getRich().getPArray(0).getRArray(0).getT();
    }

    /**
     * 获取val坐标(纵坐标)标题
     * @return
     */
    public String getChartValTitle() {
        return pgetXSSFChart().getCTChart().getPlotArea().getValAxArray(0)
                .getTitle().getTx().getRich().getPArray(0).getRArray(0).getT();
    }

    private List<CTBarSer> pgetBarSerList() {
        final List<CTBarSer> serList;
        if (pgetBarChart() != null) {
            serList = Objects.requireNonNull(pgetBarChart()).getSerList();
        } else if (pget3DBarChart() != null) {
            serList =  Objects.requireNonNull(pget3DBarChart()).getSerList();
        } else serList = null;
        return serList;
    }

    /**
     * 获取图表的数据范围
     */
    public String getChartDataRef() {
        final List<CTBarSer> serList = pgetBarSerList();
        StringBuilder sb = new StringBuilder();
        for (CTBarSer ser : serList) {
            final String f0 = ser.getTx().getStrRef().getF(); // 数据标题
            final String f1 = ser.getCat().getStrRef().getF(); // cat横坐标范围
            final String f2 = ser.getVal().getNumRef().getF(); // 数据范围
            sb.append(f0).append("&");
            sb.append(f1).append("&");
            sb.append(f2).append("&");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 三维柱状/条形图 bar3dChart 二位柱状/条形图 barChart
     * barDir 条形为bar柱状为col
     * 获取图表的类型
     * 暂时只能获取柱状图类型
     */
    public String getChartType() {
        StringBuilder sb = new StringBuilder();
        if (pgetBarChart() != null) {
            sb.append("BarChart").append("&");
            final CTBarChart barChart = pgetBarChart();
            sb.append(barChart.getBarDir().getVal()).append("&");
            sb.append(barChart.getGrouping().getVal());
        } else if (pget3DBarChart() != null) {
            sb.append("3DBarChart").append("&");
            final CTBar3DChart bar3DChart = pget3DBarChart();
            sb.append(bar3DChart.getBarDir().getVal()).append("&");
            sb.append(bar3DChart.getGrouping().getVal());
        }
        return sb.toString();
    }

    /**
     * 是否图表旁显示值
     * @return
     */
    public Boolean isChartShowVal() {
        return pgetBarSerList().get(0).getDLbls().getShowVal().getVal();
    }

    /**
     * 获取图表坐标的数据间隔大小
     * @return
     */
    public Double getChartMajorUnit() {
        return pgetXSSFChart().getCTChart().getPlotArea().getValAxArray(0)
                .getMajorUnit().getVal();
    }

    /**
     * 是否显示图例
     * @return
     */
    public Boolean isChartDisplayLegend() {
        return pgetXSSFChart().getCTChart().getLegend() != null;
    }

    /**
     * 获取图例位置
     * @return
     */
    public String getChartLegendPos() {
        return pgetXSSFChart().getCTChart().getLegend().getLegendPos().getVal().toString();
    }

    /**
     * 获取排序影响的范围
     * @return
     */
    public String getSortStateRef() {
        return xssfWorkbook.getSheetAt(0).getCTWorksheet()
                .getSortState().getRef();
    }

    /**
     * 获取排序条件
     * @return
     */
    public String getSortConditionRef() {
        final List<CTSortCondition> sortConditionList = xssfWorkbook.getSheetAt(0).getCTWorksheet()
                .getSortState().getSortConditionList();
        StringBuilder res = new StringBuilder();
        for (CTSortCondition condition : sortConditionList) {
            if (condition.getDescending()) {
                res.append("r"); // 逆序
            }
            res.append(condition.getRef()).append("&");
        }
        return res.substring(0, res.length() - 1);
    }

    /**
     * 获取自动筛选的范围
     * @return
     */
    public String getAutoFilterRef() {
        return xssfWorkbook.getSheetAt(0).getCTWorksheet().getAutoFilter().getRef();
    }

    /**
     * 获取筛选条件
     * @return
     */
    public String getFilterVal() {
        final List<CTFilterColumn> filterColumnList = xssfWorkbook.getSheetAt(0)
                .getCTWorksheet().getAutoFilter().getFilterColumnList();
        StringBuilder res = new StringBuilder();
        for (CTFilterColumn column : filterColumnList) {
            if (column.getFilters() != null) {
                res.append(column.getFilters().getFilterList()
                        .stream().map(CTFilter::getVal).collect(Collectors.joining("&")));
            }
            if (column.getCustomFilters() != null) {
                res.append(column.getCustomFilters().getCustomFilterList()
                        .stream().map(item -> item.getOperator().toString() + item.getVal())
                        .collect(Collectors.joining("&")));
            }
            res.append("&");
        }
        return res.substring(0, res.length() - 1);
    }

//    public static void main(String[] args) {
//        ExcelUtils xlsx = new ExcelUtils("/Users/hxx/Desktop/answer.xlsx");
//        System.out.println(xlsx.getFuncRefRange("H3"));
//        CellAddress address = new CellAddress("I3");
//        System.out.println(xlsx.xssfWorkbook.getSheetAt(0).getRow(address.getRow()).getCell(address.getColumn()).getCTCell());
//    }
}
