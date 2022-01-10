package com.yf.exam.config;

import com.yf.exam.aspect.mybatis.QueryInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis过滤器配置
 * 注意：必须按顺序进行配置，否则容易出现业务异常
 * @author bool
 */
@Configuration
@MapperScan("com.yf.exam.modules.**.mapper")
public class MybatisConfig {

    /**
     * 数据查询过滤器
     */
    @Bean
    public QueryInterceptor queryInterceptor() {
        // setLimit(500L) 设置最大单页限制数量，默认 500 条，-1 不受限制
        return new QueryInterceptor();
    }


}