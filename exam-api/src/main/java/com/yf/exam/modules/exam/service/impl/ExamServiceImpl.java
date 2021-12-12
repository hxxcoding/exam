package com.yf.exam.modules.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.core.utils.StringUtils;
import com.yf.exam.modules.enums.JoinType;
import com.yf.exam.modules.enums.OpenType;
import com.yf.exam.modules.exam.dto.ExamDTO;
import com.yf.exam.modules.exam.dto.ExamRepoDTO;
import com.yf.exam.modules.exam.dto.request.ExamSaveReqDTO;
import com.yf.exam.modules.exam.dto.response.ExamOnlineRespDTO;
import com.yf.exam.modules.exam.dto.response.ExamReviewRespDTO;
import com.yf.exam.modules.exam.entity.Exam;
import com.yf.exam.modules.exam.enums.ExamState;
import com.yf.exam.modules.exam.mapper.ExamMapper;
import com.yf.exam.modules.exam.service.ExamDepartService;
import com.yf.exam.modules.exam.service.ExamRepoService;
import com.yf.exam.modules.exam.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* <p>
* 考试业务实现类
* </p>
*
* @author 聪明笨狗
* @since 2020-07-25 16:18
*/
@Service
@CacheConfig(cacheNames = "exam", keyGenerator = "keyGenerator")
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {


    @Autowired
    private ExamRepoService examRepoService;

    @Autowired
    private ExamDepartService examDepartService;

    @Override
    @CacheEvict(allEntries = true) // 当保存/修改时 删除所有的缓存记录
    public void save(ExamSaveReqDTO reqDTO) {

        // ID
        String id = reqDTO.getId();

        if(StringUtils.isBlank(id)){
            id = IdWorker.getIdStr();
        }

        //复制参数
        Exam entity = new Exam();

        // 计算分值
        this.calcScore(reqDTO);

        // 复制基本数据
        BeanMapper.copy(reqDTO, entity);
        entity.setId(id);

        // 修复状态
        Integer actualState = reqDTO.getState();
        if (reqDTO.getTimeLimit() != null
                && !reqDTO.getTimeLimit()
                && actualState != null
                && (actualState.equals(ExamState.READY_START)
                    || actualState.equals(ExamState.OVERDUE))) {
            entity.setState(ExamState.ENABLE);
        } else {
            entity.setState(actualState);
        }

        // 题库组卷
        if(JoinType.REPO_JOIN.equals(reqDTO.getJoinType())){
            try {
                examRepoService.saveAll(id, reqDTO.getRepoList());
            }catch (DuplicateKeyException e){
                throw new ServiceException(1, "不能选择重复的题库！");
            }
        }

        // 开放的部门
        if(OpenType.DEPT_OPEN.equals(reqDTO.getOpenType())){
            examDepartService.saveAll(id, reqDTO.getDepartIds());
        }

        this.saveOrUpdate(entity);

    }

    @Override
    @Cacheable(sync = true)
    public ExamSaveReqDTO findDetail(String id) {
        ExamSaveReqDTO respDTO = new ExamSaveReqDTO();
        Exam exam = this.getById(id);
        BeanMapper.copy(exam, respDTO);

        // 考试部门
        List<String> departIds = examDepartService.listByExam(id);
        respDTO.setDepartIds(departIds);

        // 题库
        List<ExamRepoDTO> repos = examRepoService.listByExam(id);
        respDTO.setRepoList(repos);

        return respDTO;
    }

    @Override
    @Cacheable(sync = true)
    public ExamDTO findById(String id) {
        ExamDTO respDTO = new ExamDTO();
        Exam exam = this.getById(id);
        BeanMapper.copy(exam, respDTO);
        return respDTO;
    }

    @Override
    @Cacheable(sync = true)
    public IPage<ExamDTO> paging(PagingReqDTO<ExamDTO> reqDTO) {

        //创建分页对象
        Page page = new Page(reqDTO.getCurrent(), reqDTO.getSize());

        //转换结果
        IPage<ExamDTO> pageData = baseMapper.paging(page, reqDTO.getParams());
        return pageData;
     }

    @Override
    @Cacheable
    public IPage<ExamOnlineRespDTO> onlinePaging(PagingReqDTO<ExamDTO> reqDTO) {

        // 创建分页对象
        Page page = new Page(reqDTO.getCurrent(), reqDTO.getSize());

        // 查找分页
        IPage<ExamOnlineRespDTO> pageData = baseMapper.online(page, reqDTO.getParams());

        return pageData;
    }

    @Override
    public IPage<ExamReviewRespDTO> reviewPaging(PagingReqDTO<ExamDTO> reqDTO) { // 待阅试卷
        // 创建分页对象
        Page page = new Page(reqDTO.getCurrent(), reqDTO.getSize());

        // 查找分页
        IPage<ExamReviewRespDTO> pageData = baseMapper.reviewPaging(page, reqDTO.getParams());

        return pageData;
    }


    /**
     * 计算分值
     * @param reqDTO
     */
    private void calcScore(ExamSaveReqDTO reqDTO){

        // 主观题分数
        int objScore = 0;

        // 题库组卷
        if(JoinType.REPO_JOIN.equals(reqDTO.getJoinType())){
            List<ExamRepoDTO> repoList = reqDTO.getRepoList();

            for(ExamRepoDTO item: repoList){
                if(item.getRadioCount() != null
                        && item.getRadioCount() > 0
                        && item.getRadioScore() != null
                        && item.getRadioScore() > 0) {
                    objScore += item.getRadioCount() * item.getRadioScore();
                }
                if(item.getMultiCount() != null
                        && item.getMultiCount() > 0
                        && item.getMultiScore() != null
                        && item.getMultiScore() > 0){
                    objScore += item.getMultiCount() * item.getMultiScore();
                }
                if(item.getJudgeCount() != null
                        && item.getJudgeCount() > 0
                        && item.getJudgeScore() != null
                        && item.getJudgeScore() > 0){
                    objScore += item.getJudgeCount() * item.getJudgeScore();
                }
                if(item.getSaqCount() != null
                        && item.getSaqCount() > 0
                        && item.getSaqScore() != null
                        && item.getSaqScore() > 0){
                    objScore += item.getSaqCount() * item.getSaqScore();
                }
                if(item.getBlankCount() != null
                        && item.getBlankCount() > 0
                        && item.getBlankScore() != null
                        && item.getBlankScore() > 0){
                    objScore += item.getBlankCount() * item.getBlankScore();
                }
            }
        }


        reqDTO.setTotalScore(objScore);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean update(Exam entity, Wrapper<Exam> updateWrapper) {
        return super.update(entity, updateWrapper);
    }

}
