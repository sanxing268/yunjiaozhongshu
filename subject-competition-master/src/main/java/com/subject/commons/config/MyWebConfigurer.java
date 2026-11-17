package com.subject.commons.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
public class MyWebConfigurer implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许 8080 端口的前端跨域访问 8443 端口的后端接口
        registry.addMapping("/**") // 对所有接口生效
                .allowCredentials(true) // 允许携带 Cookie/Token 等凭证（如需）
                // 允许的源：前台页面的访问地址（8080端口）有时候前端会跑8081端口，此处设置便于开发
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080","http://localhost:8081","http://127.0.0.1:8081")
                // 兼容 Spring Boot 2.4+ 推荐写法（二选一，建议用这个）
                // .allowedOriginPatterns("http://localhost:8080", "http://127.0.0.1:8080")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE", "PATCH") // 允许的请求方法
                .allowedHeaders("*") // 允许所有请求头
                .maxAge(3600); // 预检请求缓存时间（秒），减少预检请求次数
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 腾讯云 COS 无需本地静态资源映射，留空即可
    }
}