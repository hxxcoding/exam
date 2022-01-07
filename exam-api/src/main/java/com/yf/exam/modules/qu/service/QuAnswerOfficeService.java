package com.yf.exam.modules.qu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yf.exam.modules.qu.dto.response.AnalyzeWordRespDTO;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;

import java.util.List;

public interface QuAnswerOfficeService extends IService<QuAnswerOffice> {
    List<AnalyzeWordRespDTO> officeAnalyze(String url);
}
