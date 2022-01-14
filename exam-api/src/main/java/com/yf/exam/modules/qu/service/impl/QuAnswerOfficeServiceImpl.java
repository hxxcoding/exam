package com.yf.exam.modules.qu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.ability.Constant;
import com.yf.exam.ability.upload.service.UploadService;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.core.utils.poi.WordUtils;
import com.yf.exam.modules.qu.dto.QuAnswerDTO;
import com.yf.exam.modules.qu.dto.QuAnswerOfficeDTO;
import com.yf.exam.modules.qu.dto.WordParagraphDTO;
import com.yf.exam.modules.qu.dto.response.WordAnalyzeRespDTO;
import com.yf.exam.modules.qu.entity.QuAnswer;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;
import com.yf.exam.modules.qu.mapper.QuAnswerOfficeMapper;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
            try {
                WordUtils word = new WordUtils(realPath);
                return word.executeMethod(method, pos).toString();
            } catch (RuntimeException e) {
                throw new ServiceException("无法获取到格式,请检查`段落`和`方法`是否对应");
            }
        } else {
            throw new ServiceException("文件不支持");
        }
    }

    @Override
    public List<QuAnswerOfficeDTO> listByQu(String quId) {
        List<QuAnswerOffice> list = this.list(new QueryWrapper<QuAnswerOffice>().lambda()
                .eq(QuAnswerOffice::getQuId, quId));
        if (!CollectionUtils.isEmpty(list)) {
            return BeanMapper.mapList(list, QuAnswerOfficeDTO.class);
        }
        return null;
    }

    @Override
    public void saveAll(String quId, List<QuAnswerOfficeDTO> list) {
        //最终要保存的列表
        List<QuAnswerOffice> saveList = new ArrayList<>();
        List<String> existIds = this.list(new QueryWrapper<QuAnswerOffice>().lambda()
                .eq(QuAnswerOffice::getQuId, quId)).stream()
                .map(QuAnswerOffice::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(list)) {
            for (QuAnswerOfficeDTO item : list) {
                QuAnswerOffice answer = new QuAnswerOffice();
                BeanMapper.copy(item, answer);
                answer.setQuId(quId);
                existIds.remove(item.getId());
                saveList.add(answer);
            }
            //保存标签列表
            if(!CollectionUtils.isEmpty(saveList)) {
                this.saveOrUpdateBatch(saveList);
            }
            //删除已移除
            if(!existIds.isEmpty()){
                this.removeByIds(existIds);
            }
        } else {
            this.remove(new QueryWrapper<QuAnswerOffice>().lambda()
                    .eq(QuAnswerOffice::getQuId, quId));
        }
    }
}
