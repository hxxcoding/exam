package com.yf.exam.modules.qu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.ability.upload.service.UploadService;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.core.utils.StringUtils;
import com.yf.exam.core.utils.poi.ExcelUtils;
import com.yf.exam.core.utils.poi.PPTUtils;
import com.yf.exam.core.utils.poi.WordUtils;
import com.yf.exam.modules.Constant;
import com.yf.exam.modules.qu.dto.PPTSlideDTO;
import com.yf.exam.modules.qu.dto.QuAnswerOfficeDTO;
import com.yf.exam.modules.qu.dto.WordParagraphDTO;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;
import com.yf.exam.modules.qu.enums.QuType;
import com.yf.exam.modules.qu.mapper.QuAnswerOfficeMapper;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "quAnswerOfficeService", keyGenerator = "keyGenerator")
public class QuAnswerOfficeServiceImpl extends ServiceImpl<QuAnswerOfficeMapper, QuAnswerOffice> implements QuAnswerOfficeService {

    @Autowired
    private UploadService uploadService;
    @Autowired
    private QuAnswerOfficeService quAnswerOfficeService;

    @Override
    public List<WordParagraphDTO> wordParagraphAnalyze(String url) {
        String realPath = uploadService.getRealPath(url.substring(url.indexOf(Constant.FILE_PREFIX)));
        if (url.endsWith(".docx")) {
            return WordUtils.analyzeWordParagraph(realPath);
        } else {
            throw new ServiceException("文件不支持");
        }
    }

    @Override
    public List<PPTSlideDTO> pptSlideAnalyze(String url) {
        String realPath = uploadService.getRealPath(url.substring(url.indexOf(Constant.FILE_PREFIX)));
        if (url.endsWith(".pptx")) {
            return PPTUtils.analyzePPTSlide(realPath);
        } else {
            throw new ServiceException("文件不支持");
        }
    }

    @Override
    public List<String> listQuOfficeMethods(Integer quType) {
        Method[] methods = null;
        if (quType.equals(QuType.WORD)) { // TODO getDeclaredMethods Or getMethods ？
            methods = WordUtils.class.getDeclaredMethods();
        } else if (quType.equals(QuType.EXCEL)) {
            methods = ExcelUtils.class.getDeclaredMethods();
        } else if (quType.equals(QuType.PPT)) {
            methods = PPTUtils.class.getDeclaredMethods();
        } else return null;
        return Arrays.stream(methods)
                .map(Method::getName)
                .filter(name -> name.startsWith("get") || name.startsWith("is"))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public String readFormat(String url, String method, String pos) {
        String realPath = uploadService.getRealPath(url.substring(url.indexOf(Constant.FILE_PREFIX)));
        if (url.endsWith(".docx")) {
            try {
                Integer position = StringUtils.isBlank(pos) ? null : Integer.parseInt(pos);
                WordUtils word = new WordUtils(realPath);
                return word.executeMethod(method, position).toString();
            } catch (RuntimeException e) {
                throw new ServiceException("无法获取到格式,请检查`段落`和`方法`是否对应");
            }
        } else if (url.endsWith(".xlsx")) {
            try {
                ExcelUtils excel = new ExcelUtils(realPath);
                return excel.executeMethod(method, pos).toString();
            } catch (RuntimeException e) {
                throw new ServiceException("无法获取到格式,请检查`目标单元格`和`方法`是否对应");
            }
        } else if (url.endsWith(".pptx")) {
            try {
                Integer position = StringUtils.isBlank(pos) ? null : Integer.parseInt(pos);
                PPTUtils ppt = new PPTUtils(realPath);
                return ppt.executeMethod(method, position).toString();
            } catch (RuntimeException e) {
                throw new ServiceException("无法获取到格式,请检查`幻灯片`和`方法`是否对应");
            }
        } else {
            throw new ServiceException("文件不支持");
        }
    }

    @Override
    public List<QuAnswerOfficeDTO> listDtoByQuId(String quId) {
        List<QuAnswerOffice> list = quAnswerOfficeService.listByQuId(quId);
        if (!CollectionUtils.isEmpty(list)) {
            return BeanMapper.mapList(list, QuAnswerOfficeDTO.class);
        }
        return null;
    }

    @Override
    @Cacheable(sync = true)
    public List<QuAnswerOffice> listByQuId(String quId) {
        return this.list(new LambdaQueryWrapper<QuAnswerOffice>()
                .eq(QuAnswerOffice::getQuId, quId));
    }

    /**
     * 保存Office题目内容
     * @param quId
     * @param list
     */
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
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
                existIds.remove(item.getId()); // 如果存在就移除
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
