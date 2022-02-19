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
 * 语言设置 服务实现类
 * </p>
 *
 * @author 聪明笨狗
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

        //创建分页对象
        Page page = new Page<>(reqDTO.getCurrent(), reqDTO.getSize());

        //转换结果
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

        if (qu.getQuType() < 10) {
            List<QuAnswerDTO> answerList = quAnswerService.listByQu(id);
            respDTO.setAnswerList(answerList);
        }

        if (qu.getQuType() >= 10) {
            List<QuAnswerOfficeDTO> officeAnswerList = quAnswerOfficeService.listByQu(id);
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


        // 校验数据
        this.checkData(reqDTO, "");

        Qu qu = new Qu();
        BeanMapper.copy(reqDTO, qu);

        // 如果更新了图片
        Qu notUpdatedQu = this.getById(qu.getId());
        if (notUpdatedQu != null && !notUpdatedQu.getImage().equals(qu.getImage())
                && !notUpdatedQu.getImage().equals("")) {
            uploadService.delete(notUpdatedQu.getImage());
        }

        // 更新
        this.saveOrUpdate(qu);

        // 保存全部answerList
        if (qu.getQuType() < 10) {
            quAnswerService.saveAll(qu.getId(), reqDTO.getAnswerList());
        }

        if (qu.getQuType() >= 10) {
            quAnswerOfficeService.saveAll(qu.getId(), reqDTO.getOfficeAnswerList());
        }

        // 保存到题库
        quRepoService.saveAll(qu.getId(), qu.getQuType(), reqDTO.getRepoIds());


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(List<String> ids) {
        for (String quId : ids) {
            Qu qu = this.getById(quId);
            // 同时删除题目答案
            if (qu.getQuType().equals(QuType.RADIO) || qu.getQuType().equals(QuType.MULTI) || qu.getQuType().equals(QuType.JUDGE)) {
                quAnswerService.remove(new LambdaQueryWrapper<QuAnswer>().eq(QuAnswer::getQuId, quId));
            }
            if (qu.getQuType().equals(QuType.WORD) || qu.getQuType().equals(QuType.EXCEL) || qu.getQuType().equals(QuType.PPT)) {
                quAnswerOfficeService.remove(new LambdaQueryWrapper<QuAnswerOffice>().eq(QuAnswerOffice::getQuId, quId));
            }
            List<QuRepo> quRepoList = quRepoService.list(new LambdaQueryWrapper<QuRepo>().eq(QuRepo::getQuId, quId));
            if (quRepoList != null) {
                List<String> repoIds = quRepoList.stream().map(QuRepo::getRepoId).collect(Collectors.toList());
                // 删除题库关联
                quRepoService.remove(new LambdaQueryWrapper<QuRepo>().eq(QuRepo::getQuId, quId));
                // 更新题库数量
                repoIds.forEach(repoId -> repoService.refreshStat(repoId));
            }
            // 最后删除题目
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
        //根据题目名称分组
        Map<Integer, List<QuExportDTO>> anMap = new HashMap<>(16);

        //题目本体信息
        Map<Integer, QuExportDTO> quMap = new HashMap<>(16);

        //数据分组
        for (QuExportDTO item : dtoList) {

            // 空白的ID
            if (StringUtils.isEmpty(item.getNo())) {
                continue;
            }

            Integer key;
            //序号
            try {
                key = Integer.parseInt(item.getNo());
            } catch (Exception e) {
                continue;
            }

            //如果已经有题目了，直接处理选项
            if (anMap.containsKey(key)) {
                anMap.get(key).add(item);
            } else {
                //如果没有，将题目内容和选项一起
                List<QuExportDTO> subList = new ArrayList<>();
                subList.add(item);
                anMap.put(key, subList);
                quMap.put(key, item);
            }
        }

        int count = 0;
        try {

            //循环题目插入
            for (Integer key : quMap.keySet()) {

                QuExportDTO im = quMap.get(key);

                //题目基本信息
                QuDetailDTO qu = new QuDetailDTO();
                qu.setContent(im.getQContent());
                qu.setAnalysis(im.getQAnalysis());
                qu.setQuType(Integer.parseInt(im.getQuType()));
                qu.setCreateTime(new Date());
                if (im.getQuType().equals(QuType.BLANK.toString())) { // 如果是填空题
                    qu.setAnswer(im.getQAnswer());
                } else {
                    //设置回答列表
                    List<QuAnswerDTO> answerList = this.processAnswerList(anMap.get(key));
                    //设置题目
                    qu.setAnswerList(answerList);
                }
                //设置引用题库
                qu.setRepoIds(im.getRepoList());
                // 保存答案
                this.save(qu);
                count++;
            }

        } catch (ServiceException e) {
            e.printStackTrace();
            throw new ServiceException(1, "导入出现问题，行：" + count + "，" + e.getMsg());
        }

        return count;
    }

    /**
     * 处理回答列表
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
     * 校验题目信息
     *
     * @param qu
     * @param no
     * @throws Exception
     */
    public void checkData(QuDetailDTO qu, String no) {


        if (StringUtils.isEmpty(qu.getContent())) {
            throw new ServiceException(1, no + "题目内容不能为空！");
        }


        if (CollectionUtils.isEmpty(qu.getRepoIds())) {
            throw new ServiceException(1, no + "至少要选择一个题库！");
        } else {
            qu.getRepoIds().forEach(repoId -> {
                if (repoService.getById(repoId) == null) {
                    throw new ServiceException(1, no + "不存在提供的题库ID！");
                }
            });
        }

        List<QuAnswerDTO> answers = null;
        if (qu.getQuType().equals(QuType.RADIO) || qu.getQuType().equals(QuType.MULTI) || qu.getQuType().equals(QuType.JUDGE)) {
            answers = qu.getAnswerList();
            if (CollectionUtils.isEmpty(answers)) {
                throw new ServiceException(1, no + "客观题至少要包含一个备选答案！");
            }
        }
        List<QuAnswerOfficeDTO> officeAnswers = null;
        if (qu.getQuType().equals(QuType.WORD) || qu.getQuType().equals(QuType.EXCEL) || qu.getQuType().equals(QuType.PPT)) {
            officeAnswers = qu.getOfficeAnswerList();
            if (CollectionUtils.isEmpty(officeAnswers)) {
                throw new ServiceException(1, no + "操作题至少要包含一个判分点！");
            }
        }

        int trueCount = 0;
        if (answers != null) {
            for (QuAnswerDTO a : answers) {

                if (a.getIsRight() == null) {
                    throw new ServiceException(1, no + "必须定义选项是否正确项！");
                }

                if (StringUtils.isEmpty(a.getContent())) {
                    throw new ServiceException(1, no + "选项内容不为空！");
                }

                if (a.getIsRight()) {
                    trueCount += 1;
                }
            }
        }
        if (officeAnswers != null) {
            for (QuAnswerOfficeDTO a : officeAnswers) {

                if (StringUtils.isEmpty(a.getMethod())) {
                    throw new ServiceException(1, no + "判分方法不能为空!");
                }
                if (StringUtils.isEmpty(a.getAnswer())) {
                    throw new ServiceException(1, no + "判分点答案不能为空!");
                }
                if (StringUtils.isEmpty(a.getScore())) {
                    throw new ServiceException(1, no + "判分点分数不能为空!");
                }
            }
        }

        if (trueCount == 0 && !qu.getQuType().equals(QuType.BLANK) && qu.getQuType() < QuType.WORD) {
            throw new ServiceException(1, no + "至少要包含一个正确项！");
        }


        //单选题
        if (qu.getQuType().equals(QuType.RADIO) && trueCount > 1) {
            throw new ServiceException(1, no + "单选题不能包含多个正确项！");
        }

    }



    /**
     * 校验Excel
     *
     * @param list
     * @throws Exception
     */
    private void checkExcel(List<QuExportDTO> list) throws ServiceException {
        // 约定第三行开始导入
        int line = 3;
        StringBuffer sb = new StringBuffer();
        if (CollectionUtils.isEmpty(list)) {
            throw new ServiceException(1, "您导入的数据似乎是一个空表格！");
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
                    sb.append("第").append(line).append("行题目类型不能为空");
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(item.getQContent())) {
                    sb.append("第").append(line).append("行题目内容不能为空");
                }
                if (CollectionUtils.isEmpty(item.getRepoList())) {
                    sb.append("第").append(line).append("行题目必须包含一个题库");
                }
            }
            if (!item.getQuType().equals(QuType.BLANK.toString())) { // 不是填空题
                if (org.apache.commons.lang3.StringUtils.isBlank(item.getAIsRight())) {
                    sb.append("第").append(line).append("行选项是否正确不能为空");
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(item.getAContent()) && org.apache.commons.lang3.StringUtils.isBlank(item.getAImage())) {
                    sb.append("第").append(line).append("行选项内容和选项图片必须有一个不为空");
                }
            }
            quNo = no;
            line++;
        }
        // 存在错误
        if (!"".equals(sb.toString())) {
            throw new ServiceException(1, sb.toString());
        }
    }

    @Override
    @Cacheable(sync = true) // id instanceof String == true
    public Qu getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return super.removeByIds(idList);
    }
}
