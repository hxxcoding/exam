package com.yf.exam.core.utils.poi;

import com.yf.exam.core.utils.Reflections;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

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

//    public static void main(String[] args) {
//        ExcelUtils xlsx = new ExcelUtils("/Users/hxx/Desktop/answer.xlsx");
//        System.out.println(xlsx.getFuncRefRange("H3"));
//        CellAddress address = new CellAddress("I3");
//        System.out.println(xlsx.xssfWorkbook.getSheetAt(0).getRow(address.getRow()).getCell(address.getColumn()).getCTCell());
//    }
}
