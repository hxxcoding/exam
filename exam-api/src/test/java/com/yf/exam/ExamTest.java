package com.yf.exam;


import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.junit.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.FileInputStream;
import java.util.List;

public class ExamTest {

    @Test
    public void testOnline() throws Exception {
        XWPFDocument docx = new XWPFDocument(new FileInputStream("/Users/hxx/Desktop/old.docx"));
        CTDocument1 document = docx.getDocument();
        CTBody body = document.getBody();
        CTSectPr sectPr = body.getSectPr();
        CTPageMar pgMar = sectPr.getPgMar();
        List<CTP> pList = body.getPList();
        System.out.println(pList.size());
        // 页边距为3.5cm(1985)
        System.out.println(pgMar.getLeft().intValue() == 1985 && pgMar.getRight().intValue() == 1985);
        List<XWPFPictureData> allPictures = docx.getAllPictures();
    }
}
