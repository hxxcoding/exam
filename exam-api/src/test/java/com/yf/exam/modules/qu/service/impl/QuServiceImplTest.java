package com.yf.exam.modules.qu.service.impl;

import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import com.yf.exam.modules.qu.service.QuAnswerService;
import com.yf.exam.modules.qu.service.QuRepoService;
import com.yf.exam.modules.qu.service.QuService;
import com.yf.exam.modules.repo.service.RepoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022/1/29 14:03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Rollback
public class QuServiceImplTest {

    @Autowired
    private QuService quService;
    @Autowired
    private QuRepoService quRepoService;
    @Autowired
    private QuAnswerService quAnswerService;
    @Autowired
    private QuAnswerOfficeService quAnswerOfficeService;
    @Autowired
    private RepoService repoService;

    @Test
    public void testDeleteJudge() { // 判断题测试
        List<String> ids = new ArrayList<>();
        ids.add("1461506028981055490"); // 咖啡非洲判断题
        int beforeQuCount = quService.count();
        int beforeQuAnswerCount = quAnswerService.count();
        int beforeQuRepoCount = quRepoService.count();
        System.out.println(repoService.list());
        quService.deleteBatch(ids);
        int afterQuCount = quService.count();
        int afterQuAnswerCount = quAnswerService.count();
        int afterQuRepoCount = quRepoService.count();
        Assert.assertEquals(1, beforeQuCount - afterQuCount); // 删除一个题
        Assert.assertEquals(2, beforeQuRepoCount - afterQuRepoCount); // 删除两个题库关联
        Assert.assertEquals(2, beforeQuAnswerCount - afterQuAnswerCount); // 删除两个答案
        System.out.println(repoService.list());
    }

    @Test
    public void testDeleteRadio() { // 单选题测试
        List<String> ids = new ArrayList<>();
        ids.add("1286859710305226754"); // 咖啡非洲判断题
        int beforeQuCount = quService.count();
        int beforeQuAnswerCount = quAnswerService.count();
        int beforeQuRepoCount = quRepoService.count();
        quService.deleteBatch(ids);
        int afterQuCount = quService.count();
        int afterQuAnswerCount = quAnswerService.count();
        int afterQuRepoCount = quRepoService.count();
        Assert.assertEquals(1, beforeQuCount - afterQuCount); // 删除1个题
        Assert.assertEquals(0, beforeQuRepoCount - afterQuRepoCount); // 删除0个题库关联
        Assert.assertEquals(5, beforeQuAnswerCount - afterQuAnswerCount); // 删除5个答案
    }

    @Test
    public void testDeleteWord() { // word题测试
        List<String> ids = new ArrayList<>();
        ids.add("1467139788766216193"); // 咖啡非洲判断题
        int beforeQuCount = quService.count();
        int beforeQuAnswerCount = quAnswerOfficeService.count();
        int beforeQuRepoCount = quRepoService.count();
        quService.deleteBatch(ids);
        int afterQuCount = quService.count();
        int afterQuAnswerCount = quAnswerOfficeService.count();
        int afterQuRepoCount = quRepoService.count();
        Assert.assertEquals(1, beforeQuCount - afterQuCount); // 删除1个题
        Assert.assertEquals(0, beforeQuRepoCount - afterQuRepoCount); // 删除0个题库关联
        Assert.assertEquals(7, beforeQuAnswerCount - afterQuAnswerCount); // 删除5个答案
    }
}