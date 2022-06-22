package com.yf.exam;

import com.yf.exam.modules.paper.entity.PaperQu;
import com.yf.exam.modules.paper.service.PaperQuService;
import com.yf.exam.modules.paper.service.PaperService;
import com.yf.exam.modules.qu.entity.Qu;
import com.yf.exam.modules.qu.service.QuAnswerOfficeService;
import com.yf.exam.modules.qu.service.QuAnswerService;
import com.yf.exam.modules.qu.service.QuService;
import com.yf.exam.modules.sys.config.entity.SysConfig;
import com.yf.exam.modules.sys.config.service.SysConfigService;
import com.yf.exam.modules.sys.depart.service.SysDepartService;
import com.yf.exam.modules.sys.user.service.SysUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  迁移服务器时，更改数据库中的附件IP访问地址，也可以直接导出SQL然后查询替换文本内容
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022/6/18 14:16
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class MigrateServer {
    @Autowired
    private PaperService paperService;
    @Autowired
    private PaperQuService paperQuService;
    @Autowired
    private QuAnswerOfficeService quAnswerOfficeService;
    @Autowired
    private QuAnswerService quAnswerService;
    @Autowired
    private QuService quService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysDepartService sysDepartService;
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 服务器迁移时使用
     * 使用nginx反向代理ip 下载和上传文件使用前端端口
     */
    @Test
//    @Transactional(rollbackFor = Exception.class)
    public void migrateDB() {
        String oldIpAdd = "localhost:8101/";
        String newIpAdd = "localhost:9527/";

        List<Qu> quList = quService.list();
        quList.forEach(item -> {
            if (item.getImage().contains(oldIpAdd)) { // 更改附件ip
                item.setImage(item.getImage().replace(oldIpAdd, newIpAdd));
            }
            if (item.getAnswer().contains(oldIpAdd)) { // 更改答案ip
                item.setAnswer(item.getAnswer().replace(oldIpAdd, newIpAdd));
            }
            if (item.getContent().contains(oldIpAdd)) { // 更改题目ip
                item.setContent(item.getContent().replace(oldIpAdd, newIpAdd));
            }
        });
        quService.updateBatchById(quList);

        List<PaperQu> paperQuList = paperQuService.list();
        paperQuList.forEach(item -> {
            if (item.getAnswer().contains(oldIpAdd)) { // 更改用户上传答案的ip
                item.setAnswer(item.getAnswer().replace(oldIpAdd, newIpAdd));
            }
        });
        paperQuService.updateBatchById(paperQuList);

        // 更改系统设置
        final SysConfig sysConfig = sysConfigService.list().get(0);
        sysConfig.setBackLogo(sysConfig.getBackLogo().replace(oldIpAdd, newIpAdd));
        sysConfig.setSiteNotice(sysConfig.getSiteNotice().replace(oldIpAdd, newIpAdd));
        sysConfigService.updateById(sysConfig);
    }
}
