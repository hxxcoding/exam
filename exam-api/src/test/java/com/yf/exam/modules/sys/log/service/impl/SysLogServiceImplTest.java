package com.yf.exam.modules.sys.log.service.impl;

import com.yf.exam.modules.sys.log.service.SysLogService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>
 *
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022/6/22 15:09
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@Rollback
public class SysLogServiceImplTest extends TestCase {

    @Autowired
    private SysLogService sysLogService;

    @Test
    public void testDeleteOverdueLogs() {
        System.out.println(sysLogService.count());
        sysLogService.removeOverdueLogs();
        System.out.println(sysLogService.count());
    }
}