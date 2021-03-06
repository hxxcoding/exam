package com.yf.exam;

import com.yf.exam.core.api.utils.JsonConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
public class ExamApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ExamApplication.class, args);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		//保留原有converter,把新增fastConverter插入集合头,保证优先级
		converters.add(0, JsonConverter.fastConverter());
	}

}