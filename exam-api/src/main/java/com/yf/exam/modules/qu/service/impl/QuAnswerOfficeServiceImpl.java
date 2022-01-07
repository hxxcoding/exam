package com.yf.exam.modules.qu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.ability.Constant;
import com.yf.exam.ability.upload.service.UploadService;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.poi.WordUtils;
import com.yf.exam.modules.qu.dto.response.AnalyzeWordRespDTO;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;
import com.yf.exam.modules.qu.mapper.QuAnswerOfficeMapper;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuAnswerOfficeServiceImpl extends ServiceImpl<QuAnswerOfficeMapper, QuAnswerOffice> implements QuAnswerOfficeService {

    @Autowired
    private UploadService uploadService;

    @Override
    public List<AnalyzeWordRespDTO> officeAnalyze(String url) {
        String realPath = uploadService.getRealPath(url.substring(url.indexOf(Constant.FILE_PREFIX)));
        if (url.endsWith(".docx")) {
            return WordUtils.analyzeWord(realPath);
        } else {
            throw new ServiceException("文件不支持");
        }
    }
}
