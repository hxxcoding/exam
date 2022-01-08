package com.yf.exam.modules.qu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yf.exam.modules.qu.dto.QuAnswerDTO;
import com.yf.exam.modules.qu.dto.QuAnswerOfficeDTO;
import com.yf.exam.modules.qu.dto.response.WordAnalyzeRespDTO;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;

import java.util.List;

public interface QuAnswerOfficeService extends IService<QuAnswerOffice> {

    WordAnalyzeRespDTO officeAnalyze(String url);

    String readWordFormat(String url, String method, Integer pos);

    List<QuAnswerOfficeDTO> listByQu(String quId);

    void saveAll(String quId, List<QuAnswerOfficeDTO> list);
}
