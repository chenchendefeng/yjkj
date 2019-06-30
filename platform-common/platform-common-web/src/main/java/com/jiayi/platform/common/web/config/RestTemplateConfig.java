package com.jiayi.platform.common.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @program: platform-common
 * @description:
 * @author: Mr.liang
 * @create: 2019-05-17 18:21
 **/
@Configuration
public class RestTemplateConfig {
	@Bean
	public RestTemplate restTemplate (ClientHttpRequestFactory factory) {
		return new RestTemplate(factory);
	}

	@Bean
	public ClientHttpRequestFactory simpleClientHttpRequestFactory () {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(15000);
		factory.setReadTimeout(5000);
		return factory;
	}
}
