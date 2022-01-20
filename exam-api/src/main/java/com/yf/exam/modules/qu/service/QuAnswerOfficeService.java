package com.yf.exam.modules.qu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yf.exam.modules.qu.dto.PPTSlideDTO;
import com.yf.exam.modules.qu.dto.QuAnswerOfficeDTO;
import com.yf.exam.modules.qu.dto.WordParagraphDTO;
import com.yf.exam.modules.qu.dto.response.WordAnalyzeRespDTO;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;

import java.util.List;

public interface QuAnswerOfficeService extends IService<QuAnswerOffice> {

    List<WordParagraphDTO> wordParagraphAnalyze(String url);

    List<PPTSlideDTO> pptSlideAnalyze(String url);

    List<String> listQuOfficeMethods(Integer quType);

    String readFormat(String url, String method, String pos);

    List<QuAnswerOfficeDTO> listByQu(String quId);

    void saveAll(String quId, List<QuAnswerOfficeDTO> list);
}
