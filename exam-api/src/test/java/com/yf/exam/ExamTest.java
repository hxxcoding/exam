package com.yf.exam;


import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.junit.Test;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExamTest {

    @Test
    public void testOnline() throws Exception {
        XWPFDocument docx = new XWPFDocument(new FileInputStream("/Users/hxx/Desktop/office/test.docx"));
        CTDocument1 document = docx.getDocument();
        System.out.println(document);
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
            foreColor.append(String.format("%02x", color));
        }
        System.out.println(foreColor); // 图案颜色

        byte[] backRGB = fill.getFillBackgroundColor().getRGB();
        for (byte color : backRGB) {
            backColor.append(String.format("%02x", color));
        }
        System.out.println(backColor); // 背景颜色

        System.out.println(fill.getPatternType()); // 获取图案类型
    }

    @Test
    public void testExcelUtilsCompare() throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream("/Users/hxx/Desktop/answer1.xlsx"));
        System.out.println(xssfWorkbook.getStylesSource().getCTStylesheet());
    }
}
