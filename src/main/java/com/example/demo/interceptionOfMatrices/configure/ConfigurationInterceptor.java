package com.example.demo.interceptionOfMatrices.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.interceptionOfMatrices.interceptors.LogInterceptor;

/**
 * The Class InterceptorConfig.
 */
@Configuration
public class ConfigurationInterceptor implements WebMvcConfigurer {

    @Autowired
    LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(logInterceptor);
    }
}