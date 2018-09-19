package com.fc.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fc.common.interceptor.ApiInterceptor;

@Configuration
class WebConfigurer extends WebMvcConfigurerAdapter {
	
	
	@Bean
    public ApiInterceptor getLoginInterceptor() {
        return new ApiInterceptor();
    }
	
	@Override  
    public void addInterceptors(InterceptorRegistry registry) {  
        //注册自定义拦截器，添加拦截路径和排除拦截路径  
        registry.addInterceptor(getLoginInterceptor()).addPathPatterns("/game/aizq/**");  
        super.addInterceptors(registry);
	} 
	
	
	

}