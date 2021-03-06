package com.yf.exam.modules.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.ability.upload.config.UploadConfig;
import com.yf.exam.ability.upload.service.UploadService;
import com.yf.exam.ability.upload.utils.FileUtils;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.core.utils.StringUtils;
import com.yf.exam.core.utils.file.PdfUtils;
import com.yf.exam.core.utils.file.ZipUtils;
import com.yf.exam.core.utils.poi.ExcelUtils;
import com.yf.exam.core.utils.poi.PPTUtils;
import com.yf.exam.core.utils.poi.WordUtils;
import com.yf.exam.modules.Constant;
import com.yf.exam.modules.exam.dto.ExamDTO;
import com.yf.exam.modules.exam.dto.ExamRepoDTO;
import com.yf.exam.modules.exam.enums.ExamState;
import com.yf.exam.modules.exam.enums.ExamType;
import com.yf.exam.modules.exam.enums.JoinType;
import com.yf.exam.modules.exam.service.ExamRepoService;
import com.yf.exam.modules.exam.service.ExamService;
import com.yf.exam.modules.paper.dto.export.PaperExportDTO;
import com.yf.exam.modules.paper.dto.ext.OnlinePaperQuDTO;
import com.yf.exam.modules.paper.dto.ext.OnlinePaperQuDetailDTO;
import com.yf.exam.modules.paper.dto.ext.PaperQuAnswerExtDTO;
import com.yf.exam.modules.paper.dto.ext.PaperQuDetailDTO;
import com.yf.exam.modules.paper.dto.request.PaperAnswerDTO;
import com.yf.exam.modules.paper.dto.request.PaperQueryReqDTO;
import com.yf.exam.modules.paper.dto.response.ExamDetailRespDTO;
import com.yf.exam.modules.paper.dto.response.ExamResultRespDTO;
import com.yf.exam.modules.paper.dto.response.PaperDetailRespDTO;
import com.yf.exam.modules.paper.dto.response.PaperQuPointsRespDTO;
import com.yf.exam.modules.paper.entity.Paper;
import com.yf.exam.modules.paper.entity.PaperQu;
import com.yf.exam.modules.paper.entity.PaperQuAnswer;
import com.yf.exam.modules.paper.enums.PaperState;
import com.yf.exam.modules.paper.mapper.PaperMapper;
import com.yf.exam.modules.paper.service.PaperQuAnswerService;
import com.yf.exam.modules.paper.service.PaperQuService;
import com.yf.exam.modules.paper.service.PaperService;
import com.yf.exam.modules.paper.controller.PaperWebSocketServer;
import com.yf.exam.modules.qu.entity.Qu;
import com.yf.exam.modules.qu.entity.QuAnswer;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;
import com.yf.exam.modules.qu.enums.QuType;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import com.yf.exam.modules.qu.service.QuAnswerService;
import com.yf.exam.modules.qu.service.QuService;
import com.yf.exam.modules.sys.depart.entity.SysDepart;
import com.yf.exam.modules.sys.depart.service.SysDepartService;
import com.yf.exam.modules.sys.system.service.SysDictService;
import com.yf.exam.modules.sys.user.entity.SysUser;
import com.yf.exam.modules.sys.user.service.SysUserService;
import com.yf.exam.modules.user.exam.entity.UserExam;
import com.yf.exam.modules.user.exam.service.UserExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import wiki.xsx.core.pdf.doc.XEasyPdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
* <p>
* ???????????? ???????????????
* </p>
*
* @author ????????????
* @since 2020-05-25 16:33
*/
@Slf4j
@Service
@CacheConfig(cacheNames = "paper", keyGenerator = "keyGenerator") // TODO ???????????????????????????
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ExamService examService;

    @Autowired
    private QuService quService;

    @Autowired
    private QuAnswerService quAnswerService;

    @Autowired
    @Lazy
    private PaperService paperService;

    @Autowired
    private PaperQuService paperQuService;

    @Autowired
    private PaperQuAnswerService paperQuAnswerService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private ExamRepoService examRepoService;

    @Autowired
    private UserExamService userExamService;

    @Autowired
    private QuAnswerOfficeService quAnswerOfficeService;

    @Autowired
    private SysDepartService sysDepartService;

    @Autowired
    private SysDictService sysDictService;

    @Autowired
    private UploadConfig uploadConfig;

//    private static final Timer timer = new Timer();
//    private static final int DELAY_MINUTES = 3; // ?????????????????????3??????????????????????????????

    /**
     * ??????????????????ABC??????
     */
    private static final List<String> ABC = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z");


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPaper(String userId, String examId, String seat, String password) {

        // ????????????
        ExamDTO exam = examService.findById(examId);

        if(exam == null){
            throw new ServiceException(1, "??????????????????");
        }

        // ????????????????????????????????????
        if (exam.getTryLimit()) {
            UserExam userExam = userExamService.getOne(new QueryWrapper<UserExam>()
                    .lambda()
                    .eq(UserExam::getExamId, exam.getId())
                    .eq(UserExam::getUserId, userId));
            if (userExam != null && userExam.getTryCount() >= exam.getLimitTimes()) {
                throw new ServiceException(1, "???????????????????????????");
            }
        }

        // ?????????????????????????????????????????????????????????????????? ????????????????????????
        Paper paper = paperService.getOne(new QueryWrapper<Paper>()
                .lambda().eq(Paper::getUserId, userId)
                .eq(Paper::getExamId, examId)
                .eq(Paper::getState, 0));

        if (paper != null) { // ????????????????????????
            try {
                if (exam.getExamType().equals(ExamType.PRACTICE)) { // ??????????????????????????????
                    return paper.getId();
                }
                if (exam.getExamType().equals(ExamType.FINAL_EXAM) && !StringUtils.isBlank(password)) { // ????????????????????????????????????
                    if (exam.getPassword().equals(password)) {
                        return paper.getId();
                    } else {
                        throw new ServiceException("??????????????????!");
                    }
                }
                if (exam.getExamType().equals(ExamType.FINAL_EXAM) && StringUtils.isBlank(password)) { // ???????????????????????????????????????
                    throw new ServiceException("???????????????????????????????????????!");
                }
            } finally {
                if (!paper.getSeat().equals(seat)) {
                    paper.setSeat(seat);
                    paperService.updateById(paper);
                }
            }
        }

        if(!ExamState.ENABLE.equals(exam.getState())){
            throw new ServiceException(1, "?????????????????????!");
        }

        // ??????????????????
        List<PaperQu> quList = new ArrayList<>();

        // ??????????????????
        if(JoinType.REPO_JOIN.equals(exam.getJoinType())){
            //???????????????????????????
            quList = this.generateByRepo(examId, exam.getLevel());
        }
        quList.sort(Comparator.comparingInt(PaperQu::getQuType));

        if(CollectionUtils.isEmpty(quList)){
            throw new ServiceException(1, "???????????????????????????????????????");
        }

        //??????????????????
        Paper savedPaper = this.savePaper(userId, seat, exam, quList);

//        String paperId = savedPaper.getId();
//        // ???timer????????????????????????????????? ???executeTime??????
//        Calendar executeTime = Calendar.getInstance();
//        executeTime.setTime(savedPaper.getLimitTime());
//        executeTime.add(Calendar.MINUTE, DELAY_MINUTES);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    log.info("??????????????????[userId=" + savedPaper.getUserId() + ",paperId=" + paperId + "]");
//                    paperService.handExam(paperId);
//                } catch (Exception ignored) {}
//            }
//        }, executeTime.getTime());

        return savedPaper.getId();
    }

    @Override
    public ExamDetailRespDTO paperDetail(String paperId) {

        ExamDetailRespDTO respDTO = new ExamDetailRespDTO();

        // ??????????????????
        Paper paper = paperService.getById(paperId);
        BeanMapper.copy(paper, respDTO);

        // ??????????????????
        List<OnlinePaperQuDTO> list = paperQuService.listByPaper(paperId);

        List<OnlinePaperQuDTO> radioList = new ArrayList<>();
        List<OnlinePaperQuDTO> multiList = new ArrayList<>();
        List<OnlinePaperQuDTO> judgeList = new ArrayList<>();
        List<OnlinePaperQuDTO> saqList = new ArrayList<>();
        List<OnlinePaperQuDTO> blankList = new ArrayList<>();
        List<OnlinePaperQuDTO> wordList = new ArrayList<>();
        List<OnlinePaperQuDTO> excelList = new ArrayList<>();
        List<OnlinePaperQuDTO> pptList = new ArrayList<>();
        for (OnlinePaperQuDTO item: list) {
            if (QuType.RADIO.equals(item.getQuType())) {
                radioList.add(item);
            } else if (QuType.MULTI.equals(item.getQuType())) {
                multiList.add(item);
            } else if (QuType.JUDGE.equals(item.getQuType())) {
                judgeList.add(item);
            } else if (QuType.SAQ.equals(item.getQuType())) {
                saqList.add(item);
            } else if (QuType.BLANK.equals(item.getQuType())) {
                blankList.add(item);
            } else if (QuType.WORD.equals(item.getQuType())) {
                wordList.add(item);
            } else if (QuType.EXCEL.equals(item.getQuType())) {
                excelList.add(item);
            } else if (QuType.PPT.equals(item.getQuType())) {
                pptList.add(item);
            }
        }

        respDTO.setRadioList(radioList);
        respDTO.setMultiList(multiList);
        respDTO.setJudgeList(judgeList);
        respDTO.setSaqList(saqList);
        respDTO.setBlankList(blankList);
        respDTO.setWordList(wordList);
        respDTO.setExcelList(excelList);
        respDTO.setPptList(pptList);
        return respDTO;
    }

    @Override
    public IPage<PaperDetailRespDTO> onlinePaperPaging(PagingReqDTO<PaperDetailRespDTO> reqDTO) {
        // ??????????????????
        // ???????????????xml????????? sql ????????????, ?????? PaperMapper.paging
        IPage<Paper> query = new Page<>(reqDTO.getCurrent(), reqDTO.getSize());
        Set<String> paperIds = PaperWebSocketServer.getSessionPool().keySet();

        if (CollectionUtils.isEmpty(paperIds)) {
            return new Page<>(reqDTO.getCurrent(), reqDTO.getSize());
        }

        //????????????
        QueryWrapper<Paper> wrapper = new QueryWrapper<>();

        PaperDetailRespDTO params = reqDTO.getParams();

        if(params!=null){
            if(!StringUtils.isBlank(params.getSeat())){ // ?????????????????????
                wrapper.lambda().like(Paper::getSeat, params.getSeat());
            }

            if(!StringUtils.isBlank(params.getExamId())){ // ???????????????
                wrapper.lambda().eq(Paper::getExamId, params.getExamId());
            }

            if(!StringUtils.isBlank(params.getDepartId())){ // ???????????????
                List<String> ids = sysDepartService.listAllSubIds(params.getDepartId());
                wrapper.lambda().in(Paper::getDepartId, ids);
            }
        }
        wrapper.lambda().in(Paper::getId, paperIds);

        //????????????
        IPage<Paper> page = paperService.page(query, wrapper);
        IPage<PaperDetailRespDTO> pageData = page.convert(item -> {
            PaperDetailRespDTO paperDTO = new PaperDetailRespDTO();
            final SysUser user = sysUserService.getById(item.getUserId());
            final SysDepart depart = sysDepartService.getById(item.getDepartId());
            paperDTO.setRealName(user.getRealName());
            paperDTO.setUserName(user.getUserName());
            paperDTO.setDeptName(depart.getDeptName());
            BeanUtils.copyProperties(item, paperDTO);
            return paperDTO;
        });
        return pageData;
    }

    @Override
    public ExamResultRespDTO paperResult(String paperId) {

        ExamResultRespDTO respDTO = new ExamResultRespDTO();

        // ??????????????????
        Paper paper = paperService.getById(paperId);
        if (paper == null) {
            throw new ServiceException("???????????????!");
        }
        if (paper.getState().equals(PaperState.ING)) { // ???????????????
            throw new ServiceException("???????????????,??????????????????!");
        }
        BeanMapper.copy(paper, respDTO);
        final SysUser user = sysUserService.getById(paper.getUserId());
        respDTO.setUserName(user.getUserName());
        respDTO.setRealName(user.getRealName());
        final SysDepart depart = sysDepartService.getById(user.getDepartId());
        respDTO.setDeptName(depart.getDeptName());
        List<PaperQuDetailDTO> quList = paperQuService.listForPaperResult(paperId);
        respDTO.setQuList(quList);

        return respDTO;
    }

    @Override
    public OnlinePaperQuDetailDTO findQuDetail(String paperId, String quId) {

        OnlinePaperQuDetailDTO respDTO = new OnlinePaperQuDetailDTO();
        // ??????
        Qu qu = quService.getById(quId); // cached

        // ????????????
        PaperQu paperQu = paperQuService.findByKey(paperId, quId);
        BeanMapper.copy(paperQu, respDTO);
        respDTO.setContent(qu.getContent());
        respDTO.setImage(qu.getImage());

        // ?????????????????????
        if (qu.getQuType().equals(QuType.RADIO) || qu.getQuType().equals(QuType.MULTI) || qu.getQuType().equals(QuType.JUDGE)) {
            List<PaperQuAnswerExtDTO> list = paperQuAnswerService.listForExam(paperId, quId);
            respDTO.setAnswerList(list);
        }

        return respDTO;
    }


    /**
     * ????????????????????????????????????
     * @param examId
     * @return
     */
    private List<PaperQu> generateByRepo(String examId, Integer level){

        // ???????????????????????????
        List<ExamRepoDTO> list = examRepoService.listByExam(examId);

        //?????????????????????
        List<PaperQu> quList = new ArrayList<>();

        //??????ID?????????????????????
        List<String> excludes = new ArrayList<>();
        excludes.add("none");

        if (!CollectionUtils.isEmpty(list)) {
            for (ExamRepoDTO item : list) {

                // ?????????
                if(item.getRadioCount() > 0){
                    List<Qu> radioList = quService.listByRandom(item.getRepoId(), QuType.RADIO, level, excludes,
                            item.getRadioCount());
                    if (item.getRadioCount() > radioList.size()) { // ????????????
                        throw new ServiceException(1, "????????????`?????????`??????????????????,?????????????????????!");
                    }
                    for (Qu qu : radioList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                //?????????
                if(item.getMultiCount() > 0) {
                    List<Qu> multiList = quService.listByRandom(item.getRepoId(), QuType.MULTI, level, excludes,
                            item.getMultiCount());
                    if (item.getMultiCount() > multiList.size()) { // ????????????
                        throw new ServiceException(1, "????????????`?????????`??????????????????,?????????????????????!");
                    }
                    for (Qu qu : multiList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                // ?????????
                if(item.getJudgeCount() > 0) {
                    List<Qu> judgeList = quService.listByRandom(item.getRepoId(), QuType.JUDGE, level, excludes,
                            item.getJudgeCount());
                    if (item.getJudgeCount() > judgeList.size()) { // ????????????
                        throw new ServiceException(1, "????????????`?????????`??????????????????,?????????????????????!");
                    }
                    for (Qu qu : judgeList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                // ?????????
                if(item.getBlankCount() > 0) {
                    List<Qu> blankList = quService.listByRandom(item.getRepoId(), QuType.BLANK, level, excludes,
                            item.getBlankCount());
                    if (item.getBlankCount() > blankList.size()) { // ????????????
                        throw new ServiceException(1, "????????????`?????????`??????????????????,?????????????????????!");
                    }
                    for (Qu qu : blankList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                // ??????
                if(item.getSaqCount() > 0) {
                    List<Qu> saqList = quService.listByRandom(item.getRepoId(), QuType.SAQ, level, excludes,
                            item.getSaqCount());
                    if (item.getSaqCount() > saqList.size()) { // ????????????
                        throw new ServiceException(1, "????????????`?????????`??????????????????,?????????????????????!");
                    }
                    for (Qu qu : saqList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                // word
                if(item.getWordCount() > 0) {
                    List<Qu> wordList = quService.listByRandom(item.getRepoId(), QuType.WORD, level, excludes,
                            item.getWordCount());
                    if (item.getWordCount() > wordList.size()) { // ????????????
                        throw new ServiceException(1, "????????????`word???`??????????????????,?????????????????????!");
                    }
                    for (Qu qu : wordList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                // excel
                if(item.getExcelCount() > 0) {
                    List<Qu> excelList = quService.listByRandom(item.getRepoId(), QuType.EXCEL, level, excludes,
                            item.getExcelCount());
                    if (item.getExcelCount() > excelList.size()) { // ????????????
                        throw new ServiceException(1, "????????????`excel???`??????????????????,?????????????????????!");
                    }
                    for (Qu qu : excelList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                // ppt
                if(item.getPptCount() > 0) {
                    List<Qu> pptList = quService.listByRandom(item.getRepoId(), QuType.PPT, level, excludes,
                            item.getPptCount());
                    if (item.getPptCount() > pptList.size()) { // ????????????
                        throw new ServiceException(1, "????????????`ppt???`??????????????????,?????????????????????!");
                    }
                    for (Qu qu : pptList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }
            }
        }
        return quList;
    }


    /**
     * ????????????????????????
     * @param repo
     * @param qu
     * @return
     */
    private PaperQu processPaperQu(ExamRepoDTO repo, Qu qu) {

        //??????????????????
        PaperQu paperQu = new PaperQu();
        paperQu.setQuId(qu.getId());
        paperQu.setAnswered(false);
        paperQu.setIsRight(false);
        paperQu.setQuType(qu.getQuType());

        if (QuType.RADIO.equals(qu.getQuType())) {
            paperQu.setScore(repo.getRadioScore());
            paperQu.setActualScore(repo.getRadioScore());
        } else if (QuType.MULTI.equals(qu.getQuType())) {
            paperQu.setScore(repo.getMultiScore());
            paperQu.setActualScore(repo.getMultiScore());
        } else if (QuType.JUDGE.equals(qu.getQuType())) {
            paperQu.setScore(repo.getJudgeScore());
            paperQu.setActualScore(repo.getJudgeScore());
        } else if (QuType.SAQ.equals(qu.getQuType())) {
            paperQu.setScore(repo.getSaqScore());
            paperQu.setActualScore(repo.getSaqScore());
        } else if (QuType.BLANK.equals(qu.getQuType())) {
            paperQu.setScore(repo.getBlankScore());
            paperQu.setActualScore(repo.getBlankScore());
        } else if (QuType.WORD.equals(qu.getQuType())) {
            paperQu.setScore(repo.getWordScore());
            paperQu.setActualScore(0);
        } else if (QuType.EXCEL.equals(qu.getQuType())) {
            paperQu.setScore(repo.getExcelScore());
            paperQu.setActualScore(0);
        } else if (QuType.PPT.equals(qu.getQuType())) {
            paperQu.setScore(repo.getPptScore());
            paperQu.setActualScore(0);
        }

        return paperQu;
    }

    /**
     * ????????????
     * @param userId
     * @param exam
     * @param quList
     * @return
     */
    private Paper savePaper(String userId, String seat, ExamDTO exam, List<PaperQu> quList) {


        // ????????????
        SysUser user = sysUserService.getById(userId);
        //????????????????????????
        Paper paper = new Paper();
        paper.setDepartId(user.getDepartId());
        paper.setExamId(exam.getId());
        paper.setTitle(exam.getTitle());
        paper.setTotalScore(exam.getTotalScore());
        paper.setTotalTime(exam.getTotalTime());
        paper.setUserScore(0);
        paper.setUserId(userId);
        paper.setCreateTime(new Date());
        paper.setUpdateTime(new Date());
        paper.setQualifyScore(exam.getQualifyScore());
        paper.setState(PaperState.ING);
        paper.setHasSaq(false);
        paper.setSeat(seat);

        // ????????????
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(System.currentTimeMillis());
        cl.add(Calendar.MINUTE, exam.getTotalTime());
        if (exam.getTimeLimit()) {
            paper.setLimitTime(cl.getTime().after(exam.getEndTime()) ? exam.getEndTime() : cl.getTime());
        } else {
            paper.setLimitTime(cl.getTime());
        }

        paperService.save(paper);

        if (!CollectionUtils.isEmpty(quList)) {
            this.savePaperQu(paper.getId(), quList);
        }

        return paper;
    }


    /**
     * ????????????????????????
     * @param paperId
     * @param quList
     */
    private void savePaperQu(String paperId, List<PaperQu> quList){

        List<PaperQu> batchQuList = new ArrayList<>();
        List<PaperQuAnswer> batchAnswerList = new ArrayList<>();

        int sort = 0;
        for (PaperQu item : quList) {

            item.setPaperId(paperId);
            item.setSort(sort);
            item.setId(IdWorker.getIdStr());

            //????????????
            List<QuAnswer> answerList = quAnswerService.listAnswerByRandom(item.getQuId());

            if (!CollectionUtils.isEmpty(answerList)) {

                int ii = 0;
                for (QuAnswer answer : answerList) {
                    PaperQuAnswer paperQuAnswer = new PaperQuAnswer();
                    paperQuAnswer.setId(UUID.randomUUID().toString());
                    paperQuAnswer.setPaperId(paperId);
                    paperQuAnswer.setQuId(answer.getQuId());
                    paperQuAnswer.setAnswerId(answer.getId());
                    paperQuAnswer.setChecked(false);
                    paperQuAnswer.setSort(ii);
                    paperQuAnswer.setAbc(ABC.get(ii));
                    paperQuAnswer.setIsRight(answer.getIsRight());
                    ii++;
                    batchAnswerList.add(paperQuAnswer);
                }
            }

            batchQuList.add(item);
            sort++;
        }

        //????????????
        paperQuService.saveBatch(batchQuList);

        //????????????????????????
        paperQuAnswerService.saveBatch(batchAnswerList);
    }

    /**
     * ???????????????????????????
     * @param reqDTO
     */
    @Async
    @Override
    public void fillAnswerByAsync(PaperAnswerDTO reqDTO) {
        paperService.fillAnswer(reqDTO);
    }

    /**
     * ?????????????????????
     * @param reqDTO
     */
    @Override
    public void fillAnswer(PaperAnswerDTO reqDTO) {

        Paper paper = paperService.getById(reqDTO.getPaperId());
        if (!paper.getState().equals(PaperState.ING)) {
            return;
        }
        //????????????
        boolean right = false;

        PaperQu paperQu = paperQuService.getOne(new QueryWrapper<PaperQu>().lambda()
                .eq(PaperQu::getPaperId, reqDTO.getPaperId())
                .eq(PaperQu::getQuId, reqDTO.getQuId()));
        //??????????????????
        if (reqDTO.getQuType().equals(QuType.BLANK)) { // ???????????????
            if (!StringUtils.isBlank(reqDTO.getAnswer())) {
                Qu blankQu = quService.getOne(new QueryWrapper<Qu>()
                        .lambda().eq(Qu::getId, reqDTO.getQuId()));
                String[] answers = blankQu.getAnswer().split(";");
                for (String answer : answers) {
                    if (reqDTO.getAnswer().equals(answer)) {
                        right = true;
                        break;
                    }
                }
            }
        }

        if (reqDTO.getQuType().equals(QuType.RADIO)
                || reqDTO.getQuType().equals(QuType.MULTI)
                || reqDTO.getQuType().equals(QuType.JUDGE)) {
            //???????????????????????????
            List<PaperQuAnswer> list = paperQuAnswerService.listForFill(reqDTO.getPaperId(), reqDTO.getQuId());
            right = true;
            for (PaperQuAnswer item : list) {

                item.setChecked(reqDTO.getAnswers().contains(item.getId()));

                //??????????????????????????????
                if (item.getIsRight() != null && !item.getIsRight().equals(item.getChecked())) {
                    right = false;
                }
                paperQuAnswerService.updateById(item);
            }
        }

        if (reqDTO.getQuType().equals(QuType.WORD) ||
                reqDTO.getQuType().equals(QuType.EXCEL) ||
                reqDTO.getQuType().equals(QuType.PPT)) {
            // office???????????????
            String answerFileUrl = reqDTO.getAnswer();
            if (!StringUtils.isBlank(answerFileUrl)) {
                right = true; // ?????????true ???????????????actualScore ???is_right??????
                String realPath = uploadService.getRealPath(answerFileUrl.substring(answerFileUrl.indexOf(Constant.FILE_PREFIX)));
                List<PaperQuPointsRespDTO> points = this.quOfficePoints(reqDTO.getQuType(), reqDTO.getQuId(), realPath);
                int totalUserScore = points.stream().mapToInt(PaperQuPointsRespDTO::getUserScore).sum();
                int totalPointScore = points.stream().mapToInt(PaperQuPointsRespDTO::getPointScore).sum();
                int actualScore = Math.round(1.0F * totalUserScore / totalPointScore * paperQu.getScore());
                paperQu.setActualScore(actualScore);
            }
        }

        //??????????????????
        paperQu.setQuId(reqDTO.getQuId());
        paperQu.setPaperId(reqDTO.getPaperId());
        paperQu.setIsRight(right);
        paperQu.setAnswer(reqDTO.getAnswer());
        paperQu.setAnswered(true);


        // ?????????
        if(CollectionUtils.isEmpty(reqDTO.getAnswers())
                && StringUtils.isBlank(reqDTO.getAnswer())){
            paperQu.setAnswered(false);
        }

        paperQuService.updateById(paperQu);

    }

    /**
     * ???????????????????????????
     * @param paperId
     * @param quId
     * @return
     */
    @Override
    public List<PaperQuPointsRespDTO> quOfficePoints(String paperId, String quId) {
        PaperQu paperQu = paperQuService.findByKey(paperId, quId);
        String answerFile = paperQu.getAnswer();
        String realPath = uploadService.getRealPath(answerFile.substring(answerFile.indexOf(Constant.FILE_PREFIX)));
        return this.quOfficePoints(paperQu.getQuType(), paperQu.getQuId(), realPath);
    }

    /**
     * ??????????????????????????? ??????????????????
     * @param quType
     * @param quId
     * @param filePath
     * @return
     */
    private List<PaperQuPointsRespDTO> quOfficePoints(Integer quType, String quId, String filePath) {
        List<PaperQuPointsRespDTO> res = new ArrayList<>();
        List<QuAnswerOffice> officeAnswers = quAnswerOfficeService.listByQuId(quId);
        if (quType.equals(QuType.WORD) && filePath.endsWith(".docx")) {
            WordUtils docx = new WordUtils(filePath);
            for (QuAnswerOffice an : officeAnswers) {
                Integer position = StringUtils.isBlank(an.getPos()) ? null : Integer.parseInt(an.getPos());
                Object userAnswer = docx.executeMethod(an.getMethod(), position);
                PaperQuPointsRespDTO point = new PaperQuPointsRespDTO()
                        .setPoint(an.getMethod())
                        .setPointScore(an.getScore())
                        .setRemark(an.getRemark());
                if (userAnswer == null) point.setUserScore(0);
                if (userAnswer != null && an.getAnswer().equals(userAnswer.toString())) {
                    point.setUserScore(an.getScore());
                }
                if (userAnswer != null && !an.getAnswer().equals(userAnswer.toString())) {
                    point.setUserScore(0);
                }
                res.add(point);
            }
        } else if (quType.equals(QuType.EXCEL) && filePath.endsWith(".xlsx")) {
            ExcelUtils docx = new ExcelUtils(filePath);
            for (QuAnswerOffice an : officeAnswers) {
                Object userAnswer = docx.executeMethod(an.getMethod(), an.getPos());
                PaperQuPointsRespDTO point = new PaperQuPointsRespDTO()
                        .setPoint(an.getMethod())
                        .setPointScore(an.getScore())
                        .setRemark(an.getRemark());
                if (userAnswer == null) point.setUserScore(0);
                if (userAnswer != null && an.getAnswer().equals(userAnswer.toString())) {
                    point.setUserScore(an.getScore());
                }
                if (userAnswer != null && !an.getAnswer().equals(userAnswer.toString())) {
                    point.setUserScore(0);
                }
                res.add(point);
            }
        } else if (quType.equals(QuType.PPT) && filePath.endsWith(".pptx")) {
            PPTUtils pptx = new PPTUtils(filePath);
            for (QuAnswerOffice an : officeAnswers) {
                Integer position = StringUtils.isBlank(an.getPos()) ? null : Integer.parseInt(an.getPos());
                Object userAnswer = pptx.executeMethod(an.getMethod(), position);
                PaperQuPointsRespDTO point = new PaperQuPointsRespDTO()
                        .setPoint(an.getMethod())
                        .setPointScore(an.getScore())
                        .setRemark(an.getRemark());
                if (userAnswer == null) point.setUserScore(0);
                if (userAnswer != null && an.getAnswer().equals(userAnswer.toString())) {
                    point.setUserScore(an.getScore());
                }
                if (userAnswer != null && !an.getAnswer().equals(userAnswer.toString())) {
                    point.setUserScore(0);
                }
                res.add(point);
            }
        }
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handExam(String paperId) {
        //??????????????????
        Paper paper = paperService.getById(paperId);

        //????????????????????????????????????
        if(!PaperState.ING.equals(paper.getState())){
            throw new ServiceException(1, "?????????????????????");
        }

        // ?????????
        int objScore = paperQuService.sumObjective(paperId);
        paper.setObjScore(objScore);
        paper.setUserScore(objScore);

        // ???????????????????????????????????????0
        paper.setSubjScore(0);

        // ????????????????????????
        userExamService.joinResult(paper.getUserId(), paper.getExamId(), objScore, objScore>=paper.getQualifyScore());

        paper.setState(PaperState.FINISHED);

        // ?????????
//        if(paper.getHasSaq()) {
//            paper.setState(PaperState.WAIT_OPT);
//        }
        paper.setUpdateTime(new Date());

        //??????????????????
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(System.currentTimeMillis());
        int userTime = (int)((System.currentTimeMillis() - paper.getCreateTime().getTime()) / 1000 / 60);
        if(userTime == 0){
            userTime = 1;
        }
        paper.setUserTime(userTime);

        //????????????
        paperService.updateById(paper);
    }

    @Override
    public void backExam(String paperId) {
        Paper paper = paperService.getById(paperId);
        if (paper.getLimitTime().before(new Date())) {
            throw new ServiceException("?????????????????????????????????????????????????????????");
        }
        paper.setState(PaperState.ING); // ??????????????????????????????
        paperService.updateById(paper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reviewPaper(ExamResultRespDTO reqDTO) {

        List<PaperQuDetailDTO> quList = reqDTO.getQuList();
        List<PaperQu> list = new ArrayList<>();
        for(PaperQuDetailDTO item: quList){
            PaperQu qu = new PaperQu();
            qu.setId(item.getId());
            qu.setActualScore(item.getActualScore());
            qu.setIsRight(true);
            list.add(qu);
        }

        // ????????????
        paperQuService.updateBatchById(list);

        // ?????????
        int subjScore = paperQuService.sumSubjective(reqDTO.getId());

        // ??????????????????
        Paper paper = this.getById(reqDTO.getId());
        paper.setSubjScore(subjScore);
        paper.setUserScore(paper.getObjScore()+subjScore);
        paper.setState(PaperState.FINISHED);
        paper.setUpdateTime(new Date());
        this.updateById(paper);

        // ????????????????????????
        userExamService.joinResult(paper.getUserId(), paper.getExamId(), paper.getUserScore(), paper.getUserScore()>=paper.getQualifyScore());
    }

    @Override
    public IPage<PaperDetailRespDTO> paging(PagingReqDTO<PaperQueryReqDTO> reqDTO) {
        return baseMapper.paging(reqDTO.toPage(), reqDTO.getParams());
    }

    @Override
    public List<Paper> findDeadPapers() {

        // ????????????????????????????????????
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(System.currentTimeMillis());
        cl.add(Calendar.MINUTE, -5);

        QueryWrapper<Paper> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .le(Paper::getLimitTime, cl.getTime())
                .eq(Paper::getState, PaperState.ING);


        return this.list(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void breakExam(String paperId) {

        Paper paper = new Paper();
        paper.setId(paperId);
        paper.setState(PaperState.BREAK);
        paper.setUpdateTime(new Date());
        this.updateById(paper);
    }

    @Override
    public List<PaperExportDTO> listForExport(PaperQueryReqDTO reqDTO) {
        List<PaperExportDTO> res = new ArrayList<>();
        final List<PaperDetailRespDTO> paperList = baseMapper.export(reqDTO);
        int no = 1;
        // ????????????????????????
        for (PaperDetailRespDTO paper : paperList) {
            PaperExportDTO exportDTO = new PaperExportDTO();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // ???????????????????????????
            int radioScore = 0, multiScore = 0, judgeScore = 0, blankScore = 0, officeScore = 0;
            List<PaperQuDetailDTO> paperQuList = paperQuService.listForPaperResult(paper.getId());
            for (PaperQuDetailDTO paperQu : paperQuList) {
                if (paperQu.getQuType().equals(QuType.RADIO) && paperQu.getIsRight()) radioScore += paperQu.getScore();
                if (paperQu.getQuType().equals(QuType.MULTI) && paperQu.getIsRight()) multiScore += paperQu.getScore();
                if (paperQu.getQuType().equals(QuType.JUDGE) && paperQu.getIsRight()) judgeScore += paperQu.getScore();
                if (paperQu.getQuType().equals(QuType.BLANK) && paperQu.getIsRight()) blankScore += paperQu.getScore();
                if (paperQu.getQuType().equals(QuType.WORD) || paperQu.getQuType().equals(QuType.EXCEL) || paperQu.getQuType().equals(QuType.PPT)) {
                    officeScore += paperQu.getActualScore();
                }
            }
            exportDTO.setNo(no)
                    .setTitle(paper.getTitle())
                    .setRealName(paper.getRealName())
                    .setUserName(paper.getUserName())
                    .setDeptName(paper.getDeptName())
                    .setSeat(paper.getSeat())
                    .setTimeRange(formatter.format(paper.getCreateTime()) + " ~ " + formatter.format(paper.getUpdateTime()))
                    .setUserTime(paper.getUserTime())
                    .setRadioScore(radioScore)
                    .setMultiScore(multiScore)
                    .setJudgeScore(judgeScore)
                    .setBlankScore(blankScore)
                    .setOfficeScore(officeScore)
                    .setTitleScore(paper.getUserScore());
            res.add(exportDTO);
            no++;
        }
        return res;
    }

    @Override
    public String listPaperForExport(List<String> ids) {
        String time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(new Date());
        String folderName = "export_paper_" + time;
        String fileDir = uploadConfig.getDir() + folderName + File.separator;
        File dir = new File(fileDir);
        if (!dir.mkdir()) {
            throw new ServiceException("?????????????????????!");
        }
        for (String id : ids) {
            ExamResultRespDTO resp = paperService.paperResult(id); // ????????????
            SysUser user = sysUserService.getById(resp.getUserId()); // ????????????
            SysDepart depart = sysDepartService.getById(user.getDepartId()); // ????????????
            String fileName = fileDir + resp.getTitle() +
                    "_" + depart.getDeptName() +
                    "_" + user.getUserName() +
                    "_" + user.getRealName() +
                    "_" + resp.getSeat() +
                    "_" + resp.getUserScore() +
                    "_" + resp.getId() + ".pdf";
            try (
                    FileOutputStream out = new FileOutputStream(fileName)
                    ) {
                PdfUtils.getPaperPdfDocument(new XEasyPdfDocument(), resp, user, depart).save(out).close();
            } catch (Exception e) {
                FileUtils.deleteDir(dir);
                throw new ServiceException("????????????: " + e.getMessage());
            }
        }

        // ????????????
        String zipName = folderName + ".zip";
        ZipUtils.compress(fileDir, uploadConfig.getDir() + zipName);
        FileUtils.deleteDir(dir);
        return uploadConfig.getUrl() + zipName;
    }
}
