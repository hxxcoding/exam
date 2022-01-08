package com.yf.exam.modules.qu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yf.exam.modules.qu.dto.response.WordAnalyzeRespDTO;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;

public interface QuAnswerOfficeService extends IService<QuAnswerOffice> {

    WordAnalyzeRespDTO officeAnalyze(String url);

}
