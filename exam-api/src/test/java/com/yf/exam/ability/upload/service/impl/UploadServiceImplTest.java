package com.yf.exam.ability.upload.service.impl;

import com.yf.exam.ability.upload.service.UploadService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class UploadServiceImplTest {

    @Autowired
    public UploadService uploadService;

    @Ignore
    @Test
    public void testDelete() throws IOException {
        // 先创建 /Users/hxx/Desktop/upload/2021/12/04 目录
        String filePath = "http://localhost:8101/upload/file/2021/12/04/1467139182693482497.pdf";
        File file = new File("/Users/hxx/Desktop/upload" + filePath
                .substring(filePath.indexOf("/2021/12/04")));
        Assert.assertTrue(file.createNewFile());
        Assert.assertTrue(file.exists());
        uploadService.delete(filePath);
        Assert.assertFalse(file.exists());
    }
}