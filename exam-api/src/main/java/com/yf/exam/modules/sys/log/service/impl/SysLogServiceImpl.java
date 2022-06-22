package com.yf.exam.modules.sys.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.core.utils.DateUtils;
import com.yf.exam.modules.sys.log.config.SysLogConfig;
import com.yf.exam.modules.sys.log.dto.SysLogDTO;
import com.yf.exam.modules.sys.log.entity.SysLog;
import com.yf.exam.modules.sys.log.mapper.SysLogMapper;
import com.yf.exam.modules.sys.log.service.SysLogService;
import com.yf.exam.core.api.dto.PagingReqDTO;
import com.yf.exam.core.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* <p>
* 语言设置 服务实现类
* </p>
*
* @author 聪明笨狗
* @since 2020-04-28 10:23
*/
@Log4j2
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Autowired
    private SysLogService baseService;

    @Autowired
    private SysLogConfig sysLogConfig;

    @Override
    public IPage<SysLogDTO> paging(PagingReqDTO<SysLogDTO> reqDTO) {

        //创建分页对象
        IPage<SysLog> query = reqDTO.toPage();

        //查询条件
        QueryWrapper<SysLog> wrapper = new QueryWrapper<>();

        SysLogDTO params = reqDTO.getParams();

        if (params != null) {
            if (!StringUtils.isBlank(params.getTitle())) {
                wrapper.lambda().like(SysLog::getTitle, params.getTitle());
            }

            if (!StringUtils.isBlank(params.getUserName())) {
                wrapper.lambda().like(SysLog::getUserName, params.getUserName());
            }
        }

        wrapper.lambda().orderByDesc(SysLog::getCreateTime);

        //获得数据
        IPage<SysLog> page = this.page(query, wrapper);
        //转换结果
        IPage<SysLogDTO> pageData = JSON.parseObject(JSON.toJSONString(page), new TypeReference<Page<SysLogDTO>>(){});
        return pageData;
    }

    /**
     * 每日删除一个月之前的日志
     */
    @Override
    @Scheduled(cron = "0 0 0 1/1 * ? ")
    @Transactional(rollbackFor = Exception.class)
    public void removeOverdueLogs() {
        final List<SysLog> list = baseService.list();
        Set<String> removeIds = new HashSet<>();
        for (SysLog sysLog : list) {
            if (DateUtils.calcExpDays(sysLog.getCreateTime()) > sysLogConfig.getKeepDays()) {
                removeIds.add(sysLog.getId());
            }
        }
        baseService.removeByIds(removeIds);
        log.info("定时删除日志任务执行, 成功删除" + removeIds.size() + "条日志");
    }
}
