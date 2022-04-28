package com.yf.exam.core.utils.poi;

import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.Reflections;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    /**
     * 获取某个单元格顶部边框的类型
     * @param row
     * @param cell
     * @return
     */
    public String getBorderStyleTop(Integer row, Integer cell) {
        try {
            long s =  xssfWorkbook.getSheetAt(0).getRow(row).getCell(cell).getCTCell().getS();
            long borderId = xssfWorkbook.getStylesSource().getCellXfAt((int)s).getBorderId();
            XSSFCellBorder border = xssfWorkbook.getStylesSource().getBorderAt((int) borderId);
            return border.getBorderStyle(XSSFCellBorder.BorderSide.TOP).toString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getBorderStyleTop(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        return getBorderStyleTop(address.getRow(), address.getColumn());
    }

    /**
     * 获取某个单元格底部边框的类型
     * @param row
     * @param cell
     * @return
     */
    public String getBorderStyleBottom(Integer row, Integer cell) {
        try {
            long s =  xssfWorkbook.getSheetAt(0).getRow(row).getCell(cell).getCTCell().getS();
            long borderId = xssfWorkbook.getStylesSource().getCellXfAt((int)s).getBorderId();
            XSSFCellBorder border = xssfWorkbook.getStylesSource().getBorderAt((int) borderId);
            return border.getBorderStyle(XSSFCellBorder.BorderSide.BOTTOM).toString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getBorderStyleBottom(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        return getBorderStyleBottom(address.getRow(), address.getColumn());
    }

    /**
     * 获取某个单元格左边边框的类型
     * @param row
     * @param cell
     * @return
     */
    public String getBorderStyleLeft(Integer row, Integer cell) {
        try {
            long s =  xssfWorkbook.getSheetAt(0).getRow(row).getCell(cell).getCTCell().getS();
            long borderId = xssfWorkbook.getStylesSource().getCellXfAt((int)s).getBorderId();
            XSSFCellBorder border = xssfWorkbook.getStylesSource().getBorderAt((int) borderId);
            return border.getBorderStyle(XSSFCellBorder.BorderSide.LEFT).toString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getBorderStyleLeft(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        return getBorderStyleLeft(address.getRow(), address.getColumn());
    }

    /**
     * 获取某个单元格右边边框的类型
     * @param row
     * @param cell
     * @return
     */
    public String getBorderStyleRight(Integer row, Integer cell) {
        try {
            long s =  xssfWorkbook.getSheetAt(0).getRow(row).getCell(cell).getCTCell().getS();
            long borderId = xssfWorkbook.getStylesSource().getCellXfAt((int)s).getBorderId();
            XSSFCellBorder border = xssfWorkbook.getStylesSource().getBorderAt((int) borderId);
            return border.getBorderStyle(XSSFCellBorder.BorderSide.RIGHT).toString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getBorderStyleRight(String cellAddress) {
        CellAddress address = new CellAddress(cellAddress);
        return getBorderStyleRight(address.getRow(), address.getColumn());
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
        try {
            CellAddress address = new CellAddress(cellAddress);
            XSSFCell cell = xssfWorkbook.getSheetAt(0).getRow(address.getRow()).getCell(address.getColumn());
            RichTextString richTextString = xssfWorkbook.getSharedStringSource().getSharedStringItems().get(Integer.parseInt(cell.getRawValue()));
            // 获取单元格中的中文内容
            return richTextString.getString();
        } catch (NullPointerException e) {
            throw new ServiceException("");
        }
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
        try {
            CTXf cellXf = xssfWorkbook.getStylesSource().getCellXfAt(pgetStyleIndex(cellAddress));
            return xssfWorkbook.getStylesSource().getFontAt((int) cellXf.getFontId());
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * 获取某个单元格的字体名称
     * @param cellAddress
     * @return
     */
    public String getFontName(String cellAddress) {
        try {
            return Objects.requireNonNull(this.pgetFont(cellAddress)).getFontName();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * 获取某个单元格的字体大小
     * @param cellAddress
     * @return
     */
    public Short getFontSize(String cellAddress) {
        try {
            return Objects.requireNonNull(this.pgetFont(cellAddress)).getFontHeightInPoints();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * 获取某个单元格的字体是否加粗
     * @param cellAddress
     * @return
     */
    public Boolean isBold(String cellAddress) {
        try {
            return Objects.requireNonNull(this.pgetFont(cellAddress)).getBold();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * 获取水平和垂直对齐方式
     * @param cellAddress
     * @return
     */
    public String getAlignment(String cellAddress) {
        try {
            short styleIndex = pgetStyleIndex(cellAddress);
            CTXf cellXf = xssfWorkbook.getStylesSource().getCellXfAt(styleIndex);
            StringBuilder res = new StringBuilder();
            if (cellXf.getAlignment().getHorizontal() != null) {
                res.append(cellXf.getAlignment().getHorizontal()).append(",");
            } else {
                res.append("DEFAULT").append(",");
            }
            if (cellXf.getAlignment().getVertical() != null) {
                res.append(cellXf.getAlignment().getHorizontal()).append(",");
            } else {
                res.append("DEFAULT").append(",");
            }
            return res.toString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder res = new StringBuilder();
        for (byte b : bytes) {
            res.append(String.format("%02X", b));
        }
        return res.toString();
    }

    /**
     * 获取背景填充颜色
     * @param cellAddress
     * @return
     */
    public String getFillBackgroundColor(String cellAddress) {
        XSSFCellFill fill = pgetCellFill(cellAddress);
        byte[] backRGB = fill.getFillBackgroundColor().getRGB();
        return bytesToHexString(backRGB);
    }

    /**
     * 获取图案填充颜色
     * @param cellAddress
     * @return
     */
    public String getFillForegroundColor(String cellAddress) {
        XSSFCellFill fill = pgetCellFill(cellAddress);
        byte[] foreRGB = fill.getFillForegroundColor().getRGB();
        return bytesToHexString(foreRGB);
    }

    /**
     * 获取图案类型
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
                if (range.isInRange(address)) {
                    StringBuilder res = new StringBuilder();
                    for (int k = 0; k < formattingItem.getNumberOfRules(); k++) {
                        res.append(formattingItem.getRule(k).getConditionType().type).append(".");
                        res.append(formattingItem.getRule(k).getComparisonOperation()).append(".");
                        res.append(formattingItem.getRule(k).getFormula1()).append(".");
                    }
                    return res.substring(0, res.length() - 1);
                }
            }
        }
        return null;
    }

//    public static void main(String[] args) {
//        ExcelUtils xlsx = new ExcelUtils("/Users/hxx/Desktop/answer.xlsx");
//        System.out.println(xlsx.getFuncRefRange("H3"));
//        CellAddress address = new CellAddress("I3");
//        System.out.println(xlsx.xssfWorkbook.getSheetAt(0).getRow(address.getRow()).getCell(address.getColumn()).getCTCell());
//    }
}
