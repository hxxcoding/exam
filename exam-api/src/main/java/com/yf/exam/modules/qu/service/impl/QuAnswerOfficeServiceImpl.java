package com.yf.exam.modules.qu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.ability.Constant;
import com.yf.exam.ability.upload.service.UploadService;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.poi.WordUtils;
import com.yf.exam.modules.qu.dto.WordParagraphDTO;
import com.yf.exam.modules.qu.dto.response.WordAnalyzeRespDTO;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;
import com.yf.exam.modules.qu.mapper.QuAnswerOfficeMapper;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuAnswerOfficeServiceImpl extends ServiceImpl<QuAnswerOfficeMapper, QuAnswerOffice> implements QuAnswerOfficeService {

    @Autowired
    private UploadService uploadService;

    @Override
    public WordAnalyzeRespDTO officeAnalyze(String url) {
        String realPath = uploadService.getRealPath(url.substring(url.indexOf(Constant.FILE_PREFIX)));
        if (url.endsWith(".docx")) {
            WordAnalyzeRespDTO word = new WordAnalyzeRespDTO();
            word.setParagraphs(WordUtils.analyzeWord(realPath));
            word.setMethods(Arrays.stream(WordUtils.class.getDeclaredMethods())
                    .map(Method::getName)
                    .filter(name -> name.startsWith("get") || name.startsWith("is"))
                    .collect(Collectors.toList()));
            return word;
        } else {
            throw new ServiceException("文件不支持");
        }
    }

    @Override
    public String readWordFormat(String url, String method, Integer pos) {
        String realPath = uploadService.getRealPath(url.substring(url.indexOf(Constant.FILE_PREFIX)));
        if (url.endsWith(".docx")) {
            WordUtils word = new WordUtils(realPath);
            return word.executeMethod(method, pos).toString();
        } else {
            throw new ServiceException("文件不支持");
        }
    }
}
