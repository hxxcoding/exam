package com.yf.exam.modules.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.core.utils.StringUtils;
import com.yf.exam.modules.exam.dto.ExamDTO;
import com.yf.exam.modules.exam.dto.ExamRepoDTO;
import com.yf.exam.modules.exam.dto.request.ExamSaveReqDTO;
import com.yf.exam.modules.exam.dto.response.ExamOnlineRespDTO;
import com.yf.exam.modules.exam.dto.response.ExamPreviewRespDTO;
import com.yf.exam.modules.exam.dto.response.ExamReviewRespDTO;
import com.yf.exam.modules.exam.entity.Exam;
import com.yf.exam.modules.exam.enums.ExamState;
import com.yf.exam.modules.exam.enums.JoinType;
import com.yf.exam.modules.exam.enums.OpenType;
import com.yf.exam.modules.exam.mapper.ExamMapper;
import com.yf.exam.modules.exam.service.ExamDepartService;
import com.yf.exam.modules.exam.service.ExamRepoService;
import com.yf.exam.modules.exam.service.ExamService;
import com.yf.exam.modules.paper.entity.Paper;
import com.yf.exam.modules.paper.service.PaperService;
import com.yf.exam.modules.sys.depart.service.SysDepartService;
import com.yf.exam.modules.sys.user.service.SysUserService;
import com.yf.exam.modules.user.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
* <p>
* ?????????????????????
* </p>
*/
@Service
@CacheConfig(cacheNames = "exam", keyGenerator = "keyGenerator")
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamRepoService examRepoService;

    @Autowired
    private ExamDepartService examDepartService;

    @Autowired
    @Lazy
    private PaperService paperService;

    @Override
    @CacheEvict(allEntries = true) // ?????????/????????? ???????????????????????????
    public void save(ExamSaveReqDTO reqDTO) {

        // ID
        String id = reqDTO.getId();

        if(StringUtils.isBlank(id)){
            id = IdWorker.getIdStr();
        }

        //????????????
        Exam entity = new Exam();

        // ????????????
        this.calcScore(reqDTO);

        // ??????????????????
        BeanMapper.copy(reqDTO, entity);
        entity.setId(id);

        // ????????????
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

        // ????????????
        if(JoinType.REPO_JOIN.equals(reqDTO.getJoinType())){
            try {
                examRepoService.saveAll(id, reqDTO.getRepoList());
            }catch (DuplicateKeyException e){
                throw new ServiceException(1, "??????????????????????????????");
            }
        }

        // ???????????????
        if(OpenType.DEPT_OPEN.equals(reqDTO.getOpenType())){
            examDepartService.saveAll(id, reqDTO.getDepartIds());
        }

        this.saveOrUpdate(entity);

    }

    /**
     * ?????????????????? ??????detail
     * @param id examId
     */
    @Override
    public ExamSaveReqDTO findDetail(String id) {
        ExamSaveReqDTO respDTO = new ExamSaveReqDTO();
        Exam exam = this.getById(id);
        BeanMapper.copy(exam, respDTO);

        // ????????????
        List<String> departIds = examDepartService.listByExam(id);
        respDTO.setDepartIds(departIds);

        // ??????
        List<ExamRepoDTO> repos = examRepoService.listByExam(id);
        respDTO.setRepoList(repos);

        return respDTO;
    }

    /**
     * ?????? examId ????????????
     * @param id examId
     */
    @Override
    public ExamDTO findById(String id) {
        ExamDTO respDTO = new ExamDTO();
        Exam exam = examService.getById(id);
        BeanMapper.copy(exam, respDTO);
        return respDTO;
    }

    /**
     * `????????????`??????????????????
     */
    @Override
    public IPage<ExamDTO> paging(PagingReqDTO<ExamDTO> reqDTO) {

        //??????????????????
        Page page = new Page(reqDTO.getCurrent(), reqDTO.getSize());

        //????????????
        IPage<ExamDTO> pageData = baseMapper.paging(page, reqDTO.getParams());
        return pageData;
     }

    /**
     * ??????`?????????`????????????????????????
     * @param examId ??????ID
     */
    @Override
    public ExamPreviewRespDTO onlinePreview(String examId) {
        ExamPreviewRespDTO respDTO = new ExamPreviewRespDTO();
        Exam exam = examService.getById(examId);
        BeanMapper.copy(exam, respDTO);
        Paper paper = paperService.getOne(new QueryWrapper<Paper>()
                .lambda().eq(Paper::getUserId, UserUtils.getUserId())
                .eq(Paper::getExamId, examId)
                .eq(Paper::getState, 0));
        if (paper != null) {
            respDTO.setIsStart(true);
            respDTO.setSeat(paper.getSeat());
        } else {
            respDTO.setIsStart(false);
        }
        return respDTO;
    }

    /**
     * `????????????`?????????????????????
     */
    @Override
    @Cacheable(sync = true)
    public IPage<ExamOnlineRespDTO> onlinePaging(PagingReqDTO<ExamDTO> reqDTO) {

        // ??????????????????
        Page page = new Page(reqDTO.getCurrent(), reqDTO.getSize());

        // ????????????
        IPage<ExamOnlineRespDTO> pageData = baseMapper.online(page, reqDTO.getParams());

        return pageData;
    }

    @Override
    public IPage<ExamReviewRespDTO> reviewPaging(PagingReqDTO<ExamDTO> reqDTO) { // ????????????
        // ??????????????????
        Page page = new Page(reqDTO.getCurrent(), reqDTO.getSize());

        // ????????????
        IPage<ExamReviewRespDTO> pageData = baseMapper.reviewPaging(page, reqDTO.getParams());

        return pageData;
    }


    /**
     * ????????????
     * @param reqDTO
     */
    private void calcScore(ExamSaveReqDTO reqDTO){

        // ???????????????
        int objScore = 0;

        // ????????????
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
                if(item.getWordCount() != null
                        && item.getWordCount() > 0
                        && item.getWordScore() != null
                        && item.getWordScore() > 0){
                    objScore += item.getWordCount() * item.getWordScore();
                }
                if(item.getExcelCount() != null
                        && item.getExcelCount() > 0
                        && item.getExcelScore() != null
                        && item.getExcelScore() > 0){
                    objScore += item.getExcelCount() * item.getExcelScore();
                }
                if(item.getPptCount() != null
                        && item.getPptCount() > 0
                        && item.getPptScore() != null
                        && item.getPptScore() > 0){
                    objScore += item.getPptCount() * item.getPptScore();
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

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return super.removeByIds(idList);
    }

    @Override
    @Cacheable(sync = true)
    public Exam getById(Serializable id) {
        return super.getById(id);
    }
}
