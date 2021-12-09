package com.yf.exam.modules.sys.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yf.exam.core.utils.BeanMapper;
import com.yf.exam.modules.sys.config.dto.SysConfigDTO;
import com.yf.exam.modules.sys.config.entity.SysConfig;
import com.yf.exam.modules.sys.config.mapper.SysConfigMapper;
import com.yf.exam.modules.sys.config.service.SysConfigService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
* <p>
* 语言设置 服务实现类
* </p>
*
* @author 聪明笨狗
* @since 2020-04-17 09:12
*/
@Service
@CacheConfig(cacheNames = "sysConfig", keyGenerator = "keyGenerator")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    @Cacheable(sync = true)
    public SysConfigDTO find() {

        QueryWrapper<SysConfig> wrapper = new QueryWrapper<>();
        wrapper.last(" LIMIT 1");

        SysConfig entity = this.getOne(wrapper, false);
        SysConfigDTO dto = new SysConfigDTO();
        BeanMapper.copy(entity, dto);
        return dto;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(SysConfig entity) {
        return super.saveOrUpdate(entity);
    }
}
