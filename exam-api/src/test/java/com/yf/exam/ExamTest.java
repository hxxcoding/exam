package com.yf.exam;


import com.yf.exam.modules.exam.service.ExamService;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ExamTest {

    @Autowired
    private ExamService examService;

    @Test
    public void testOnline() throws IOException {
        InputStream is = new FileInputStream("/Users/hxx/Desktop/test.docx");
        XWPFDocument docx = new XWPFDocument(is);
        XWPFHeaderFooterPolicy headerFooterPolicy = docx.getHeaderFooterPolicy();
        System.out.println(headerFooterPolicy.getDefaultFooter().getText());
    }
}
