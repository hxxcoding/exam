package com.yf.exam.modules.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.ability.upload.service.UploadService;
import com.yf.exam.ability.upload.utils.FileUtils;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.core.utils.StringUtils;
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
import com.yf.exam.modules.paper.dto.request.PaperQuQueryDTO;
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
import com.yf.exam.modules.paper.service.PaperWebSocketServer;
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
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
* <p>
* 语言设置 服务实现类
* </p>
*
* @author 聪明笨狗
* @since 2020-05-25 16:33
*/
@Slf4j
@Service
@CacheConfig(cacheNames = "paper", keyGenerator = "keyGenerator") // TODO 答题页面的缓存操作
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

    @Value("${conf.upload.dir}")
    private String uploadDir;

    @Value("${conf.upload.url}")
    private String uploadUrl;

    private static final Timer timer = new Timer();
    private static final int DELAY_MINUTES = 3; // 默认考试结束后3分钟提交未交卷的试卷

    /**
     * 展示的选项，ABC这样
     */
    private static final List<String> ABC = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z");


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPaper(String userId, String examId, String seat, String password) {

        // 查找考试
        ExamDTO exam = examService.findById(examId);

        if(exam == null){
            throw new ServiceException(1, "考试不存在！");
        }

        // 查找该用户是否已经创建过该考试的试卷且未交卷 返回未完成的试卷
        Paper paper = paperService.getOne(new QueryWrapper<Paper>()
                .lambda().eq(Paper::getUserId, userId)
                .eq(Paper::getExamId, examId)
                .eq(Paper::getState, 0));

        // 判断考试次数是否达到上限
        if (exam.getTryLimit()) {
            UserExam userExam = userExamService.getOne(new QueryWrapper<UserExam>()
                    .lambda()
                    .eq(UserExam::getExamId, exam.getId())
                    .eq(UserExam::getUserId, userId));
            if (userExam != null && userExam.getTryCount() >= exam.getLimitTimes()) {
                throw new ServiceException(1, "考试次数达到上限！");
            }
        }
        if (paper != null) { // 存在未交卷的试卷
            try {
                if (exam.getExamType().equals(ExamType.PRACTICE)) { // 模拟练习直接返回数据
                    return paper.getId();
                }
                if (exam.getExamType().equals(ExamType.FINAL_EXAM) && !StringUtils.isBlank(password)) { // 正式考试重新进入输入密码
                    if (exam.getPassword().equals(password)) {
                        return paper.getId();
                    } else {
                        throw new ServiceException("考试密码错误!");
                    }
                }
                if (exam.getExamType().equals(ExamType.FINAL_EXAM) && StringUtils.isBlank(password)) { // 正式考试重新进入未输入密码
                    throw new ServiceException("请联系监考老师输入考试密码!");
                }
            } finally {
                if (!paper.getSeat().equals(seat)) {
                    paper.setSeat(seat);
                    paperService.updateById(paper);
                }
            }
        }

        if(!ExamState.ENABLE.equals(exam.getState())){
            throw new ServiceException(1, "考试状态不正确!");
        }

        // 考试题目列表
        List<PaperQu> quList = new ArrayList<>();

        // 按题库组卷的
        if(JoinType.REPO_JOIN.equals(exam.getJoinType())){
            //查找规则选定的题库
            quList = this.generateByRepo(examId, exam.getLevel());
        }
        quList.sort(Comparator.comparingInt(PaperQu::getQuType));

        if(CollectionUtils.isEmpty(quList)){
            throw new ServiceException(1, "规则不正确，无对应的考题！");
        }

        //保存试卷内容
        Paper savedPaper = this.savePaper(userId, seat, exam, quList);
        String paperId = savedPaper.getId();
        Calendar executeTime = Calendar.getInstance();
        executeTime.setTime(savedPaper.getLimitTime());
        executeTime.add(Calendar.MINUTE, DELAY_MINUTES);

        // 向timer队列中添加一个交卷任务 在executeTime执行
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    log.info("自动交卷执行[userId=" + savedPaper.getUserId() + ",paperId=" + paperId + "]");
                    paperService.handExam(paperId);
                } catch (Exception ignored) {}
            }
        }, executeTime.getTime());

        return savedPaper.getId();
    }

    @Override
    public ExamDetailRespDTO paperDetail(String paperId) {

        ExamDetailRespDTO respDTO = new ExamDetailRespDTO();

        // 试题基本信息
        Paper paper = paperService.getById(paperId);
        BeanMapper.copy(paper, respDTO);

        // 查找题目列表
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
        // 创建分页对象
        // 也可以使用xml直接写 sql 语句实现, 参考 PaperMapper.paging
        IPage<Paper> query = new Page<>(reqDTO.getCurrent(), reqDTO.getSize());
        Set<String> paperIds = PaperWebSocketServer.getSessionPool().keySet();

        if (CollectionUtils.isEmpty(paperIds)) {
            return new Page<>(reqDTO.getCurrent(), reqDTO.getSize());
        }

        //查询条件
        QueryWrapper<Paper> wrapper = new QueryWrapper<>();

        PaperDetailRespDTO params = reqDTO.getParams();

        if(params!=null){
            if(!StringUtils.isBlank(params.getSeat())){ // 按考场座位搜索
                wrapper.lambda().like(Paper::getSeat, params.getSeat());
            }

            if(!StringUtils.isBlank(params.getExamId())){ // 按考试搜索
                wrapper.lambda().eq(Paper::getExamId, params.getExamId());
            }

            if(!StringUtils.isBlank(params.getDepartId())){ // 按部门搜索
                List<String> ids = sysDepartService.listAllSubIds(params.getDepartId());
                wrapper.lambda().in(Paper::getDepartId, ids);
            }
        }
        wrapper.lambda().in(Paper::getId, paperIds);

        //获得数据
        IPage<Paper> page = paperService.page(query, wrapper);
        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        IPage<PaperDetailRespDTO> pageData = page.convert(item -> {
            PaperDetailRespDTO paperDTO = new PaperDetailRespDTO();
            BeanUtils.copyProperties(item, paperDTO);
            return paperDTO;
        });
        return pageData;
    }

    @Override
    public ExamResultRespDTO paperResult(String paperId) {

        ExamResultRespDTO respDTO = new ExamResultRespDTO();

        // 试题基本信息
        Paper paper = paperService.getById(paperId);
        if (paper == null) {
            throw new ServiceException("试卷不存在!");
        }
        if (paper.getState().equals(PaperState.ING)) { // 试卷未提交
            throw new ServiceException("试卷未提交,无法查看结果!");
        }
        BeanMapper.copy(paper, respDTO);

        List<PaperQuDetailDTO> quList = paperQuService.listForPaperResult(paperId);
        respDTO.setQuList(quList);

        return respDTO;
    }

    @Override
    public OnlinePaperQuDetailDTO findQuDetail(String paperId, String quId) {

        OnlinePaperQuDetailDTO respDTO = new OnlinePaperQuDetailDTO();
        // 问题
        Qu qu = quService.getById(quId); // cached

        // 基本信息
        PaperQu paperQu = paperQuService.findByKey(paperId, quId);
        BeanMapper.copy(paperQu, respDTO);
        respDTO.setContent(qu.getContent());
        respDTO.setImage(qu.getImage());

        // 选择题答案列表
        if (qu.getQuType().equals(QuType.RADIO) || qu.getQuType().equals(QuType.MULTI) || qu.getQuType().equals(QuType.JUDGE)) {
            List<PaperQuAnswerExtDTO> list = paperQuAnswerService.listForExam(paperId, quId);
            respDTO.setAnswerList(list);
        }

        return respDTO;
    }


    /**
     * 题库组题方式产生题目列表
     * @param examId
     * @return
     */
    private List<PaperQu> generateByRepo(String examId, Integer level){

        // 查找规则指定的题库
        List<ExamRepoDTO> list = examRepoService.listByExam(examId);

        //最终的题目列表
        List<PaperQu> quList = new ArrayList<>();

        //排除ID，避免题目重复
        List<String> excludes = new ArrayList<>();
        excludes.add("none");

        if (!CollectionUtils.isEmpty(list)) {
            for (ExamRepoDTO item : list) {

                // 单选题
                if(item.getRadioCount() > 0){
                    List<Qu> radioList = quService.listByRandom(item.getRepoId(), QuType.RADIO, level, excludes,
                            item.getRadioCount());
                    if (item.getRadioCount() > radioList.size()) { // 题目不足
                        throw new ServiceException(1, "考试题库`单选题`题目数量不足,请联系监考老师!");
                    }
                    for (Qu qu : radioList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                //多选题
                if(item.getMultiCount() > 0) {
                    List<Qu> multiList = quService.listByRandom(item.getRepoId(), QuType.MULTI, level, excludes,
                            item.getMultiCount());
                    if (item.getMultiCount() > multiList.size()) { // 题目不足
                        throw new ServiceException(1, "考试题库`多选题`题目数量不足,请联系监考老师!");
                    }
                    for (Qu qu : multiList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                // 判断题
                if(item.getJudgeCount() > 0) {
                    List<Qu> judgeList = quService.listByRandom(item.getRepoId(), QuType.JUDGE, level, excludes,
                            item.getJudgeCount());
                    if (item.getJudgeCount() > judgeList.size()) { // 题目不足
                        throw new ServiceException(1, "考试题库`判断题`题目数量不足,请联系监考老师!");
                    }
                    for (Qu qu : judgeList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                // 填空题
                if(item.getBlankCount() > 0) {
                    List<Qu> blankList = quService.listByRandom(item.getRepoId(), QuType.BLANK, level, excludes,
                            item.getBlankCount());
                    if (item.getBlankCount() > blankList.size()) { // 题目不足
                        throw new ServiceException(1, "考试题库`填空题`题目数量不足,请联系监考老师!");
                    }
                    for (Qu qu : blankList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                // 简答
                if(item.getSaqCount() > 0) {
                    List<Qu> saqList = quService.listByRandom(item.getRepoId(), QuType.SAQ, level, excludes,
                            item.getSaqCount());
                    if (item.getSaqCount() > saqList.size()) { // 题目不足
                        throw new ServiceException(1, "考试题库`简答题`题目数量不足,请联系监考老师!");
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
                    if (item.getWordCount() > wordList.size()) { // 题目不足
                        throw new ServiceException(1, "考试题库`word题`题目数量不足,请联系监考老师!");
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
                    if (item.getExcelCount() > excelList.size()) { // 题目不足
                        throw new ServiceException(1, "考试题库`excel题`题目数量不足,请联系监考老师!");
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
                    if (item.getPptCount() > pptList.size()) { // 题目不足
                        throw new ServiceException(1, "考试题库`ppt题`题目数量不足,请联系监考老师!");
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
     * 填充试题题目信息
     * @param repo
     * @param qu
     * @return
     */
    private PaperQu processPaperQu(ExamRepoDTO repo, Qu qu) {

        //保存试题信息
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
     * 保存试卷
     * @param userId
     * @param exam
     * @param quList
     * @return
     */
    private Paper savePaper(String userId, String seat, ExamDTO exam, List<PaperQu> quList) {


        // 查找用户
        SysUser user = sysUserService.getById(userId);
        //保存试卷基本信息
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

        // 截止时间
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
     * 保存试卷试题列表
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

            //回答列表
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

        //添加问题
        paperQuService.saveBatch(batchQuList);

        //批量添加问题答案
        paperQuAnswerService.saveBatch(batchAnswerList);
    }

    /**
     * 填充答案并判分
     * @param reqDTO
     */
    @Async
    @Override
    public void fillAnswer(PaperAnswerDTO reqDTO) {

        //查找答案列表
        List<PaperQuAnswer> list = paperQuAnswerService.listForFill(reqDTO.getPaperId(), reqDTO.getQuId());

        //是否正确
        boolean right = false;

        PaperQu paperQu = paperQuService.getOne(new QueryWrapper<PaperQu>().lambda()
                .eq(PaperQu::getPaperId, reqDTO.getPaperId())
                .eq(PaperQu::getQuId, reqDTO.getQuId()));
        //更新正确答案
        if (reqDTO.getQuType().equals(QuType.BLANK)) { // 填空题判题
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
                || reqDTO.getQuType().equals(QuType.JUDGE)){
            right = true;
            for (PaperQuAnswer item : list) {

                item.setChecked(reqDTO.getAnswers().contains(item.getId()));

                //有一个对不上就是错的
                if (item.getIsRight() != null && !item.getIsRight().equals(item.getChecked())) {
                    right = false;
                } // TODO 少选得一半 错选不得分
                paperQuAnswerService.updateById(item);
            }
        }

        if (reqDTO.getQuType().equals(QuType.WORD) ||
                reqDTO.getQuType().equals(QuType.EXCEL) ||
                reqDTO.getQuType().equals(QuType.PPT)) {
            // office操作题判分
            String answerFileUrl = reqDTO.getAnswer();
            if (!StringUtils.isBlank(answerFileUrl)) {
                right = true; // 设置为true 实际的分看actualScore 与is_right无关
                String realPath = uploadService.getRealPath(answerFileUrl.substring(answerFileUrl.indexOf(Constant.FILE_PREFIX)));
                List<PaperQuPointsRespDTO> points = this.quOfficePoints(reqDTO.getQuType(), reqDTO.getQuId(), realPath);
                int totalScore = points.stream().mapToInt(PaperQuPointsRespDTO::getUserScore).sum();
                paperQu.setActualScore(totalScore);
            }
        }

        //修改为已回答
        paperQu.setQuId(reqDTO.getQuId());
        paperQu.setPaperId(reqDTO.getPaperId());
        paperQu.setIsRight(right);
        paperQu.setAnswer(reqDTO.getAnswer());
        paperQu.setAnswered(true);


        // 未作答
        if(CollectionUtils.isEmpty(reqDTO.getAnswers())
                && StringUtils.isBlank(reqDTO.getAnswer())){
            paperQu.setAnswered(false);
        }

        paperQuService.updateById(paperQu);

    }

    /**
     * 获取题目的得分详情 上面判分其实也可以用这个函数，但是 参数问题,判分效率问题 可能会受到影响
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
     * 同时做判分用
     * @param quType
     * @param quId
     * @param filePath
     * @return
     */
    private List<PaperQuPointsRespDTO> quOfficePoints(Integer quType, String quId, String filePath) {
        List<PaperQuPointsRespDTO> res = new ArrayList<>();
        List<QuAnswerOffice> officeAnswers = quAnswerOfficeService.list(new LambdaQueryWrapper<QuAnswerOffice>()
                .eq(QuAnswerOffice::getQuId, quId));
        if (quType.equals(QuType.WORD) && filePath.endsWith(".docx")) {
            WordUtils docx = new WordUtils(filePath);
            for (QuAnswerOffice an : officeAnswers) {
                Integer position = an.getPos() != null ? Integer.parseInt(an.getPos()) : null;
                Object userAnswer = docx.executeMethod(an.getMethod(), position);
                PaperQuPointsRespDTO point = new PaperQuPointsRespDTO()
                        .setPoint(an.getMethod())
                        .setPointScore(an.getScore());
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
                        .setPointScore(an.getScore());
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
                Integer position = an.getPos() != null ? Integer.parseInt(an.getPos()) : null;
                Object userAnswer = pptx.executeMethod(an.getMethod(), position);
                PaperQuPointsRespDTO point = new PaperQuPointsRespDTO()
                        .setPoint(an.getMethod())
                        .setPointScore(an.getScore());
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

        //获取试卷信息
        Paper paper = paperService.getById(paperId);

        //如果不是正常的，抛出异常
        if(!PaperState.ING.equals(paper.getState())){
            throw new ServiceException(1, "试卷状态错误！");
        }

        // 客观分
        int objScore = paperQuService.sumObjective(paperId);
        paper.setObjScore(objScore);
        paper.setUserScore(objScore);

        // 主观分，因为要阅卷，所以给0
        paper.setSubjScore(0);

        // 同步保存考试成绩
        userExamService.joinResult(paper.getUserId(), paper.getExamId(), objScore, objScore>=paper.getQualifyScore());

        paper.setState(PaperState.FINISHED);

        // 待阅卷
//        if(paper.getHasSaq()) {
//            paper.setState(PaperState.WAIT_OPT);
//        }
        paper.setUpdateTime(new Date());

        //计算考试时长
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(System.currentTimeMillis());
        int userTime = (int)((System.currentTimeMillis() - paper.getCreateTime().getTime()) / 1000 / 60);
        if(userTime == 0){
            userTime = 1;
        }
        paper.setUserTime(userTime);

        //更新试卷
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

        // 批量修改
        paperQuService.updateBatchById(list);

        // 客观分
        int subjScore = paperQuService.sumSubjective(reqDTO.getId());

        // 修改试卷状态
        Paper paper = this.getById(reqDTO.getId());
        paper.setSubjScore(subjScore);
        paper.setUserScore(paper.getObjScore()+subjScore);
        paper.setState(PaperState.FINISHED);
        paper.setUpdateTime(new Date());
        this.updateById(paper);

        // 同步保存考试成绩
        userExamService.joinResult(paper.getUserId(), paper.getExamId(), paper.getUserScore(), paper.getUserScore()>=paper.getQualifyScore());
    }

    @Override
    public IPage<PaperDetailRespDTO> paging(PagingReqDTO<PaperQueryReqDTO> reqDTO) {
        return baseMapper.paging(reqDTO.toPage(), reqDTO.getParams());
    }

    @Override
    public List<Paper> findDeadPapers() {

        // 结束后两分钟，非正常交卷
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
        // 设置导出条件
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isBlank(reqDTO.getExamId())) {
            wrapper.eq(Paper::getExamId, reqDTO.getExamId());
        }
        if (!StringUtils.isBlank(reqDTO.getDepartId())) {
            List<String> departIds = sysDepartService.listAllSubIds(reqDTO.getDepartId());
            wrapper.in(Paper::getDepartId, departIds);
        }
        if (reqDTO.getState() != null) {
            wrapper.eq(Paper::getState, reqDTO.getState());
        }
        if (!StringUtils.isBlank(reqDTO.getUserName())) {
            List<String> userIds = sysUserService.list(new LambdaQueryWrapper<SysUser>().like(SysUser::getUserName, reqDTO.getUserName()))
                    .stream().map(SysUser::getId).collect(Collectors.toList());
            wrapper.in(Paper::getUserId, userIds);
        }
        List<Paper> paperList = paperService.list(wrapper);
        int no = 1;
        // 计算各个题型分数
        for (Paper paper : paperList) {
            PaperExportDTO exportDTO = new PaperExportDTO();
            SysUser user = sysUserService.getById(paper.getUserId());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 查询每种题目的得分
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
                    .setRealName(user.getRealName())
                    .setUserName(user.getUserName())
                    .setDeptName(sysDictService.findDict("sys_depart", "dept_name", "id", paper.getDepartId()))
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
        String fileDir = this.uploadDir + dirName + File.separator;
        File dir = new File(fileDir);
        if (!dir.mkdir()) {
            throw new ServiceException("文件夹创建失败!");
        }
        for (String id : ids) {
            ExamResultRespDTO resp = paperService.paperResult(id); // 试卷内容
            SysUser user = sysUserService.getById(resp.getUserId()); // 用户数据
            SysDepart depart = sysDepartService.getById(user.getDepartId()); // 部门数据
            XWPFDocument xwpfDocument = new XWPFDocument();
            String fileName = fileDir + resp.getTitle() +
                    "_" + depart.getDeptName() +
                    "_" + user.getUserName() +
                    "_" + user.getRealName() +
                    "_" + resp.getSeat() +
                    "_" + resp.getUserScore() +
                    "_" + resp.getId() + ".docx";
            try (
                    FileOutputStream out = new FileOutputStream(fileName)
                    ) {
                this.getPaperDocument(xwpfDocument, resp, user, depart).write(out);
            } catch (Exception e) {
                throw new ServiceException("导出失败!");
            }
        }

        // 打包文件
        String zipName = folderName + ".zip";
        ZipUtils.compress(fileDir, this.uploadDir + zipName);
        FileUtils.deleteDir(dir);
        return uploadUrl + zipName;
    }

    private XWPFDocument getPaperDocument(XWPFDocument xwpfDocument, ExamResultRespDTO resp, SysUser user, SysDepart depart) {
        // 标题
        XWPFParagraph title = xwpfDocument.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleParagraphRun = title.createRun();
        titleParagraphRun.setText(resp.getTitle());
        titleParagraphRun.setFontSize(16);

        // 考试信息
        XWPFTable infoTable = xwpfDocument.createTable(4, 4);
        CTTblWidth infoTableWidth = infoTable.getCTTbl().addNewTblPr().addNewTblW();
        infoTableWidth.setType(STTblWidth.DXA);
        infoTableWidth.setW(BigInteger.valueOf(8500));

        XWPFTableRow infoTableRowOne = infoTable.getRow(0);
        infoTableRowOne.getCell(0).setText("考生姓名");
        infoTableRowOne.getCell(1).setText(user.getRealName());
        infoTableRowOne.getCell(2).setText("考生学号");
        infoTableRowOne.getCell(3).setText(resp.getUserId());
        XWPFTableRow infoTableRowTwo = infoTable.getRow(1);
        infoTableRowTwo.getCell(0).setText("考生班级");
        infoTableRowTwo.getCell(1).setText(depart.getDeptName());
        infoTableRowTwo.getCell(2).setText("考场座位号");
        infoTableRowTwo.getCell(3).setText(resp.getSeat());

        XWPFTableRow infoTableRowThree = infoTable.getRow(2);
        infoTableRowThree.getCell(0).setText("考试时间");
        infoTableRowThree.getCell(1).setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(resp.getCreateTime())
                + "~" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(resp.getUpdateTime()));
        infoTableRowThree.getCell(2).setText("考试用时");
        infoTableRowThree.getCell(3).setText(resp.getUserTime() + "分钟");
        XWPFTableRow infoTableRowFour = infoTable.getRow(3);
        infoTableRowFour.getCell(0).setText("总得分");
        infoTableRowFour.getCell(1).setText(resp.getUserScore().toString());

        // 题目信息
        int quNum = 1;
        for (PaperQuDetailDTO qu : resp.getQuList()) {
            XWPFParagraph quPara = xwpfDocument.createParagraph();
            XWPFRun quRun = quPara.createRun();
            String filter = StringUtils.filterHtml(qu.getContent());
            String[] contents = filter.split("\n");
            quRun.setText(quNum + ".");
            for (String content : contents) {
                quRun.setText(content);
                quRun.addCarriageReturn();
            }
            quRun.setFontSize(10);
            if (qu.getQuType().equals(QuType.RADIO) || qu.getQuType().equals(QuType.MULTI) || qu.getQuType().equals(QuType.JUDGE)) {
                StringBuilder userAnswer = new StringBuilder();
                for (PaperQuAnswerExtDTO quAnswer : qu.getAnswerList()) {
                    XWPFRun quAnswerRun = quPara.createRun();
                    quAnswerRun.setText(quAnswer.getAbc() + "." +quAnswer.getContent());
                    quAnswerRun.addCarriageReturn();
                    if (quAnswer.getChecked()) {
                        userAnswer.append(quAnswer.getAbc());
                    }
                }
                XWPFRun userAnswerRun = quPara.createRun();
                if (userAnswer.length() == 0) userAnswer.append("未作答");
                int score = qu.getIsRight() ? qu.getScore() : 0;
                userAnswerRun.setText("用户回答：" + userAnswer + "    " + "用户得分：" + score);
                userAnswerRun.setColor("EE0000");
                userAnswerRun.addCarriageReturn();
            }
            if (qu.getQuType().equals(QuType.BLANK) || qu.getQuType().equals(QuType.WORD) || qu.getQuType().equals(QuType.EXCEL) || qu.getQuType().equals(QuType.PPT)) {
                XWPFRun userAnswerRun = quPara.createRun();
                String answer = StringUtils.isBlank(qu.getAnswer()) ? "未作答" : qu.getAnswer();
                int score;
                if (qu.getQuType().equals(QuType.BLANK)) {
                    score = qu.getIsRight() ? qu.getScore() : 0;
                } else {
                    score = qu.getActualScore();
                }
                userAnswerRun.setText("用户回答：" + answer + "    " + "用户得分：" + score);
                userAnswerRun.setColor("EE0000");
                userAnswerRun.addCarriageReturn();
            }
            quNum++;
        }
        return xwpfDocument;
    }
}
