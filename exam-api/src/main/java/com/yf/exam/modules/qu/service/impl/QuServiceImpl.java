package com.yf.exam.modules.qu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.ability.upload.service.UploadService;
import com.yf.exam.ability.upload.utils.FileUtils;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.modules.qu.dto.QuAnswerDTO;
import com.yf.exam.modules.qu.dto.QuAnswerOfficeDTO;
import com.yf.exam.modules.qu.dto.QuDTO;
import com.yf.exam.modules.qu.dto.export.QuExportDTO;
import com.yf.exam.modules.qu.dto.ext.QuDetailDTO;
import com.yf.exam.modules.qu.dto.request.QuQueryReqDTO;
import com.yf.exam.modules.qu.entity.Qu;
import com.yf.exam.modules.qu.entity.QuAnswer;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;
import com.yf.exam.modules.qu.entity.QuRepo;
import com.yf.exam.modules.qu.enums.QuType;
import com.yf.exam.modules.qu.mapper.QuMapper;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import com.yf.exam.modules.qu.service.QuAnswerService;
import com.yf.exam.modules.qu.service.QuRepoService;
import com.yf.exam.modules.qu.service.QuService;
import com.yf.exam.modules.repo.service.RepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * <p>
 * ???????????? ???????????????
 * </p>
 *
 * @author ????????????
 * @since 2020-05-25 10:17
 */
@Service
@CacheConfig(cacheNames = "qu", keyGenerator = "keyGenerator")
public class QuServiceImpl extends ServiceImpl<QuMapper, Qu> implements QuService {

    @Autowired
    private QuAnswerService quAnswerService;

    @Autowired
    private QuAnswerOfficeService quAnswerOfficeService;

    @Autowired
    private QuRepoService quRepoService;

    @Autowired
    private RepoService repoService;

    @Autowired
    private UploadService uploadService;

    @Override
    public IPage<QuDTO> paging(PagingReqDTO<QuQueryReqDTO> reqDTO) {

        //??????????????????
        Page page = new Page<>(reqDTO.getCurrent(), reqDTO.getSize());

        //????????????
        IPage<QuDTO> pageData = baseMapper.paging(page, reqDTO.getParams());
        return pageData;
    }

    @Override
    public List<Qu> listByRandom(String repoId, Integer quType, Integer level, List<String> excludes, Integer size) {
        return baseMapper.listByRandom(repoId, quType, level, excludes, size);
    }

    @Override
    public QuDetailDTO detail(String id) {

        QuDetailDTO respDTO = new QuDetailDTO();
        Qu qu = this.getById(id);
        BeanMapper.copy(qu, respDTO);

        if (qu.getQuType().equals(QuType.RADIO) ||
                qu.getQuType().equals(QuType.MULTI) ||
                qu.getQuType().equals(QuType.JUDGE)) {
            List<QuAnswerDTO> answerList = quAnswerService.listDtoByQuId(id);
            respDTO.setAnswerList(answerList);
        }

        if (qu.getQuType().equals(QuType.WORD) ||
                qu.getQuType().equals(QuType.EXCEL) ||
                qu.getQuType().equals(QuType.PPT)) {
            List<QuAnswerOfficeDTO> officeAnswerList = quAnswerOfficeService.listDtoByQuId(id);
            respDTO.setOfficeAnswerList(officeAnswerList);
        }

        List<String> repoIds = quRepoService.listByQu(id);
        respDTO.setRepoIds(repoIds);

        return respDTO;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(allEntries = true)
    public void save(QuDetailDTO reqDTO) {


        // ????????????
        this.checkData(reqDTO, "");

        Qu qu = new Qu();
        BeanMapper.copy(reqDTO, qu);

        // ?????????????????????
        Qu notUpdatedQu = this.getById(qu.getId());
        if (notUpdatedQu != null && !notUpdatedQu.getImage().equals(qu.getImage())
                && !notUpdatedQu.getImage().equals("")) {
            uploadService.delete(notUpdatedQu.getImage());
        }

        // ??????
        this.saveOrUpdate(qu);

        // ????????????answerList
        if (qu.getQuType().equals(QuType.RADIO)
                || qu.getQuType().equals(QuType.MULTI)
                || qu.getQuType().equals(QuType.JUDGE)
                || qu.getQuType().equals(QuType.BLANK)
                || qu.getQuType().equals(QuType.SAQ)) {
            quAnswerService.saveAll(qu.getId(), reqDTO.getAnswerList());
        }

        if (qu.getQuType().equals(QuType.WORD)
                || qu.getQuType().equals(QuType.EXCEL)
                || qu.getQuType().equals(QuType.PPT)) {
            quAnswerOfficeService.saveAll(qu.getId(), reqDTO.getOfficeAnswerList());
        }

        // ???????????????
        quRepoService.saveAll(qu.getId(), qu.getQuType(), reqDTO.getRepoIds());


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(List<String> ids) {
        for (String quId : ids) {
            Qu qu = this.getById(quId);
            // ????????????????????????
            if (qu.getQuType().equals(QuType.RADIO) || qu.getQuType().equals(QuType.MULTI) || qu.getQuType().equals(QuType.JUDGE)) {
                quAnswerService.remove(new LambdaQueryWrapper<QuAnswer>().eq(QuAnswer::getQuId, quId));
            }
            if (qu.getQuType().equals(QuType.WORD) || qu.getQuType().equals(QuType.EXCEL) || qu.getQuType().equals(QuType.PPT)) {
                quAnswerOfficeService.remove(new LambdaQueryWrapper<QuAnswerOffice>().eq(QuAnswerOffice::getQuId, quId));
            }
            List<QuRepo> quRepoList = quRepoService.list(new LambdaQueryWrapper<QuRepo>().eq(QuRepo::getQuId, quId));
            if (quRepoList != null) {
                List<String> repoIds = quRepoList.stream().map(QuRepo::getRepoId).collect(Collectors.toList());
                // ??????????????????
                quRepoService.remove(new LambdaQueryWrapper<QuRepo>().eq(QuRepo::getQuId, quId));
                // ??????????????????
                repoIds.forEach(repoId -> repoService.refreshStat(repoId));
            }
            // ??????????????????
            this.removeById(quId);
        }
        return true;
    }

    @Override
    public List<QuExportDTO> listForExport(QuQueryReqDTO query) {
        return baseMapper.listForExport(query);
    }

    @Override
    public int importExcel(List<QuExportDTO> dtoList) {

        this.checkExcel(dtoList);
        //????????????????????????
        Map<Integer, List<QuExportDTO>> anMap = new HashMap<>(16);

        //??????????????????
        Map<Integer, QuExportDTO> quMap = new HashMap<>(16);

        //????????????
        for (QuExportDTO item : dtoList) {

            // ?????????ID
            if (StringUtils.isEmpty(item.getNo())) {
                continue;
            }

            Integer key;
            //??????
            try {
                key = Integer.parseInt(item.getNo());
            } catch (Exception e) {
                continue;
            }

            //?????????????????????????????????????????????
            if (anMap.containsKey(key)) {
                anMap.get(key).add(item);
            } else {
                //?????????????????????????????????????????????
                List<QuExportDTO> subList = new ArrayList<>();
                subList.add(item);
                anMap.put(key, subList);
                quMap.put(key, item);
            }
        }

        int count = 0;
        try {

            //??????????????????
            for (Integer key : quMap.keySet()) {

                QuExportDTO im = quMap.get(key);

                //??????????????????
                QuDetailDTO qu = new QuDetailDTO();
                qu.setContent(im.getQContent());
                qu.setAnalysis(im.getQAnalysis());
                qu.setQuType(Integer.parseInt(im.getQuType()));
                qu.setCreateTime(new Date());
                if (im.getQuType().equals(QuType.BLANK.toString())) { // ??????????????????
                    qu.setAnswer(im.getQAnswer());
                } else {
                    //??????????????????
                    List<QuAnswerDTO> answerList = this.processAnswerList(anMap.get(key));
                    //????????????
                    qu.setAnswerList(answerList);
                }
                //??????????????????
                qu.setRepoIds(im.getRepoList());
                // ????????????
                this.save(qu);
                count++;
            }

        } catch (ServiceException e) {
            e.printStackTrace();
            throw new ServiceException(1, "???????????????????????????" + count + "???" + e.getMsg());
        }

        return count;
    }

    /**
     * ??????????????????
     *
     * @param importList
     * @return
     */
    private List<QuAnswerDTO> processAnswerList(List<QuExportDTO> importList) {

        List<QuAnswerDTO> list = new ArrayList<>(16);
        for (QuExportDTO item : importList) {
            QuAnswerDTO a = new QuAnswerDTO();
            a.setIsRight(item.getAIsRight().equals("1"));
            a.setContent(item.getAContent());
            a.setAnalysis(item.getAAnalysis());
            a.setId("");
            list.add(a);
        }
        return list;
    }

    /**
     * ??????????????????
     *
     * @param qu
     * @param no
     * @throws Exception
     */
    public void checkData(QuDetailDTO qu, String no) {


        if (StringUtils.isEmpty(qu.getContent())) {
            throw new ServiceException(1, no + "???????????????????????????");
        }


        if (CollectionUtils.isEmpty(qu.getRepoIds())) {
            throw new ServiceException(1, no + "??????????????????????????????");
        } else {
            qu.getRepoIds().forEach(repoId -> {
                if (repoService.getById(repoId) == null) {
                    throw new ServiceException(1, no + "????????????????????????ID???");
                }
            });
        }

        List<QuAnswerDTO> answers = null;
        if (qu.getQuType().equals(QuType.RADIO) || qu.getQuType().equals(QuType.MULTI) || qu.getQuType().equals(QuType.JUDGE)) {
            answers = qu.getAnswerList();
            if (CollectionUtils.isEmpty(answers)) {
                throw new ServiceException(1, no + "?????????????????????????????????????????????");
            }
        }
        List<QuAnswerOfficeDTO> officeAnswers = null;
        if (qu.getQuType().equals(QuType.WORD) || qu.getQuType().equals(QuType.EXCEL) || qu.getQuType().equals(QuType.PPT)) {
            officeAnswers = qu.getOfficeAnswerList();
            if (CollectionUtils.isEmpty(officeAnswers)) {
                throw new ServiceException(1, no + "??????????????????????????????????????????");
            }
        }

        int trueCount = 0;
        if (answers != null) {
            for (QuAnswerDTO a : answers) {

                if (a.getIsRight() == null) {
                    throw new ServiceException(1, no + "????????????????????????????????????");
                }

                if (StringUtils.isEmpty(a.getContent())) {
                    throw new ServiceException(1, no + "????????????????????????");
                }

                if (a.getIsRight()) {
                    trueCount += 1;
                }
            }
        }
        if (officeAnswers != null) {
            for (QuAnswerOfficeDTO a : officeAnswers) {

                if (StringUtils.isEmpty(a.getMethod())) {
                    throw new ServiceException(1, no + "????????????????????????!");
                }
                if (StringUtils.isEmpty(a.getAnswer())) {
                    throw new ServiceException(1, no + "???????????????????????????!");
                }
                if (StringUtils.isEmpty(a.getScore())) {
                    throw new ServiceException(1, no + "???????????????????????????!");
                }
            }
        }

        if (trueCount == 0 && !qu.getQuType().equals(QuType.BLANK) && qu.getQuType() < QuType.WORD) {
            throw new ServiceException(1, no + "?????????????????????????????????");
        }


        //?????????
        if (qu.getQuType().equals(QuType.RADIO) && trueCount > 1) {
            throw new ServiceException(1, no + "???????????????????????????????????????");
        }

    }



    /**
     * ??????Excel
     *
     * @param list
     * @throws Exception
     */
    private void checkExcel(List<QuExportDTO> list) throws ServiceException {
        // ???????????????????????????
        int line = 3;
        StringBuffer sb = new StringBuffer();
        if (CollectionUtils.isEmpty(list)) {
            throw new ServiceException(1, "?????????????????????????????????????????????");
        }
        Integer quNo = null;
        for (QuExportDTO item : list) {
            if (org.apache.commons.lang3.StringUtils.isBlank(item.getNo())) {
                line++;
                continue;
            }

            int no;
            try {
                no = Integer.parseInt(item.getNo());
            } catch (Exception e) {
                line++;
                continue;
            }
            if (quNo == null || !quNo.equals(no)) {
                if (item.getQuType().equals("")) {
                    sb.append("???").append(line).append("???????????????????????????");
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(item.getQContent())) {
                    sb.append("???").append(line).append("???????????????????????????");
                }
                if (CollectionUtils.isEmpty(item.getRepoList())) {
                    sb.append("???").append(line).append("?????????????????????????????????");
                }
            }
            if (!item.getQuType().equals(QuType.BLANK.toString())) { // ???????????????
                if (org.apache.commons.lang3.StringUtils.isBlank(item.getAIsRight())) {
                    sb.append("???").append(line).append("?????????????????????????????????");
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(item.getAContent()) && org.apache.commons.lang3.StringUtils.isBlank(item.getAImage())) {
                    sb.append("???").append(line).append("??????????????????????????????????????????????????????");
                }
            }
            quNo = no;
            line++;
        }
        // ????????????
        if (!"".equals(sb.toString())) {
            throw new ServiceException(1, sb.toString());
        }
    }

    @Override
    @Cacheable(sync = true) // id instanceof String == true ????????????????????? ??????this??????????????????????????????
    public Qu getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return super.removeByIds(idList);
    }
}
