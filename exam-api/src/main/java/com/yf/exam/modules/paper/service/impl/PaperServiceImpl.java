package com.yf.exam.modules.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.ability.Constant;
import com.yf.exam.ability.upload.service.UploadService;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.core.utils.StringUtils;
import com.yf.exam.core.utils.poi.WordUtils;
import com.yf.exam.modules.enums.JoinType;
import com.yf.exam.modules.exam.dto.ExamDTO;
import com.yf.exam.modules.exam.dto.ExamRepoDTO;
import com.yf.exam.modules.exam.dto.ext.ExamRepoExtDTO;
import com.yf.exam.modules.exam.service.ExamRepoService;
import com.yf.exam.modules.exam.service.ExamService;
import com.yf.exam.modules.paper.dto.PaperQuDTO;
import com.yf.exam.modules.paper.dto.ext.PaperQuAnswerExtDTO;
import com.yf.exam.modules.paper.dto.ext.PaperQuDetailDTO;
import com.yf.exam.modules.paper.dto.request.PaperAnswerDTO;
import com.yf.exam.modules.paper.dto.request.PaperListReqDTO;
import com.yf.exam.modules.paper.dto.response.ExamDetailRespDTO;
import com.yf.exam.modules.paper.dto.response.ExamResultRespDTO;
import com.yf.exam.modules.paper.dto.response.PaperListRespDTO;
import com.yf.exam.modules.paper.entity.Paper;
import com.yf.exam.modules.paper.entity.PaperQu;
import com.yf.exam.modules.paper.entity.PaperQuAnswer;
import com.yf.exam.modules.exam.enums.ExamState;
import com.yf.exam.modules.paper.enums.PaperState;
import com.yf.exam.modules.paper.mapper.PaperMapper;
import com.yf.exam.modules.paper.service.PaperQuAnswerService;
import com.yf.exam.modules.paper.service.PaperQuService;
import com.yf.exam.modules.paper.service.PaperService;
import com.yf.exam.modules.qu.entity.Qu;
import com.yf.exam.modules.qu.entity.QuAnswer;
import com.yf.exam.modules.qu.entity.QuAnswerOffice;
import com.yf.exam.modules.qu.enums.QuType;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import com.yf.exam.modules.qu.service.QuAnswerService;
import com.yf.exam.modules.qu.service.QuService;
import com.yf.exam.modules.sys.user.entity.SysUser;
import com.yf.exam.modules.sys.user.service.SysUserService;
import com.yf.exam.modules.user.book.service.UserBookService;
import com.yf.exam.modules.user.exam.entity.UserExam;
import com.yf.exam.modules.user.exam.service.UserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Timer;
import java.util.TimerTask;

/**
* <p>
* 语言设置 服务实现类
* </p>
*
* @author 聪明笨狗
* @since 2020-05-25 16:33
*/
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

    private static final Timer timer = new Timer();
    private static final int delayMinutes = 1; // 默认考试结束后3分钟提交未交卷的试卷

    /**
     * 展示的选项，ABC这样
     */
    private static final List<String> ABC = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z");


    @Override
    public String createPaper(String userId, String examId) {

        // 查找考试
        ExamDTO exam = examService.findById(examId);

        if(exam == null){
            throw new ServiceException(1, "考试不存在！");
        }

        // 查找该用户是否已经创建过试卷且未交卷 返回未完成的试卷
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
        if (paper != null) {
            return paper.getId();
        }

        if(!ExamState.ENABLE.equals(exam.getState())){
            throw new ServiceException(1, "考试状态不正确！");
        }

        // 考试题目列表
        List<PaperQu> quList = new ArrayList<>();

        // 按题库组卷的
        if(JoinType.REPO_JOIN.equals(exam.getJoinType())){
            //查找规则选定的题库
            quList = this.generateByRepo(examId, exam.getLevel());
        }

        if(CollectionUtils.isEmpty(quList)){
            throw new ServiceException(1, "规则不正确，无对应的考题！");
        }

        //保存试卷内容
        Paper savedPaper = this.savePaper(userId, exam, quList);
        String paperId = savedPaper.getId();
        Calendar executeTime = Calendar.getInstance();
        executeTime.setTime(savedPaper.getLimitTime());
        executeTime.add(Calendar.MINUTE, delayMinutes);

        // 向timer队列中添加一个交卷任务 在executeTime执行
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    paperService.handExam(paperId);
                } catch (ServiceException ignored) {}
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
        List<PaperQuDTO> list = paperQuService.listByPaper(paperId);

        List<PaperQuDTO> radioList = new ArrayList<>();
        List<PaperQuDTO> multiList = new ArrayList<>();
        List<PaperQuDTO> judgeList = new ArrayList<>();
        List<PaperQuDTO> saqList = new ArrayList<>();
        List<PaperQuDTO> blankList = new ArrayList<>();
        for (PaperQuDTO item: list) {
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
            }
        }

        respDTO.setRadioList(radioList);
        respDTO.setMultiList(multiList);
        respDTO.setJudgeList(judgeList);
        respDTO.setSaqList(saqList);
        respDTO.setBlankList(blankList);
        return respDTO;
    }

    @Override
    public ExamResultRespDTO paperResult(String paperId) {

        ExamResultRespDTO respDTO = new ExamResultRespDTO();

        // 试题基本信息
        Paper paper = paperService.getById(paperId);
        if (paper.getState().equals(PaperState.ING)) { // 试卷未提交
            throw new ServiceException("试卷未提交,无法查看结果!");
        }
        BeanMapper.copy(paper, respDTO);

        List<PaperQuDetailDTO> quList = paperQuService.listForPaperResult(paperId);
        respDTO.setQuList(quList);

        return respDTO;
    }

    @Override
    public PaperQuDetailDTO findQuDetail(String paperId, String quId) {

        PaperQuDetailDTO respDTO = new PaperQuDetailDTO();
        // 问题
        Qu qu = quService.getById(quId); // cached

        // 基本信息
        PaperQu paperQu = paperQuService.findByKey(paperId, quId);
        BeanMapper.copy(paperQu, respDTO);
        respDTO.setContent(qu.getContent());
        respDTO.setImage(qu.getImage());

        // 答案列表
        List<PaperQuAnswerExtDTO> list = paperQuAnswerService.listForExam(paperId, quId);
        respDTO.setAnswerList(list);

        return respDTO;
    }


    /**
     * 题库组题方式产生题目列表
     * @param examId
     * @return
     */
    private List<PaperQu> generateByRepo(String examId, Integer level){

        // 查找规则指定的题库
        List<ExamRepoExtDTO> list = examRepoService.listByExam(examId);

        //最终的题目列表
        List<PaperQu> quList = new ArrayList<>();

        //排除ID，避免题目重复
        List<String> excludes = new ArrayList<>();
        excludes.add("none");

        if (!CollectionUtils.isEmpty(list)) {
            for (ExamRepoExtDTO item : list) {

                // 单选题
                if(item.getRadioCount() > 0){
                    List<Qu> radioList = quService.listByRandom(item.getRepoId(), QuType.RADIO, level, excludes,
                            item.getRadioCount());
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
                    for (Qu qu : judgeList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                // 填空题
                if(item.getBlankCount() > 0) {
                    List<Qu> blankList = quService.listByRandom(item.getRepoId(), QuType.BLANK, level, excludes,
                            item.getSaqCount());
                    for (Qu qu : blankList) {
                        PaperQu paperQu = this.processPaperQu(item, qu);
                        quList.add(paperQu);
                        excludes.add(qu.getId());
                    }
                }

                // 操作题
                if(item.getSaqCount() > 0) {
                    List<Qu> saqList = quService.listByRandom(item.getRepoId(), QuType.SAQ, level, excludes,
                            item.getSaqCount());
                    for (Qu qu : saqList) {
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
    private Paper savePaper(String userId, ExamDTO exam, List<PaperQu> quList) {


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
            QuAnswer quAnswer = quAnswerService.getOne(new QueryWrapper<QuAnswer>()
                    .lambda().eq(QuAnswer::getQuId, reqDTO.getQuId()));
            String[] answers = quAnswer.getContent().split(";");
            for (String answer : answers) {
                if (reqDTO.getAnswer().equals(answer)) {
                    right = true;
                    break;
                }
            }
        } else if (reqDTO.getQuType().equals(QuType.SAQ)) {
            // 操作题判分
            right = true;
            String answerFileUrl = reqDTO.getAnswer();
            String realPath = uploadService.getRealPath(answerFileUrl.substring(answerFileUrl.indexOf(Constant.FILE_PREFIX)));
            WordUtils docx = new WordUtils(realPath);
            List<QuAnswerOffice> officeAnswers = quAnswerOfficeService.list();
            int totalScore = 0;
            for (QuAnswerOffice officeAnswer : officeAnswers) {
                Object userAnswer = docx.executeMethod(officeAnswer.getMethod(), officeAnswer.getPos());
                if (userAnswer != null && officeAnswer.getAnswer().equals(userAnswer.toString())) {
                    totalScore += officeAnswer.getScore();
                }
            }
            paperQu.setActualScore(totalScore);
        } else {
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

        //把打错的问题加入错题本
//        List<PaperQuDTO> list = paperQuService.listByPaper(paperId);
//        for(PaperQuDTO qu: list){
//            // 主观题和对的都不加入错题库
//            if(qu.getIsRight()){
//                continue;
//            }
//            //加入错题本
//            userBookService.addBook(paper.getExamId(), qu.getQuId());
//        }
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
    public IPage<PaperListRespDTO> paging(PagingReqDTO<PaperListReqDTO> reqDTO) {
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
}
