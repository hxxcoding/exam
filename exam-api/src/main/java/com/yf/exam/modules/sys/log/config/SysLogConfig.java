package com.yf.exam.modules.sys.log.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022/6/22 14:55
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "sys.log")
public class SysLogConfig {

    /**
     * 日志保存天数
     */
    private Integer keepDays;
}
