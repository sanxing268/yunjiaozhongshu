package com.subject.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 全局CORS跨域配置
 * 设置最高优先级，确保在Shiro过滤链之前处理CORS
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig {

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration();

    // 允许本地开发环境
    corsConfiguration.addAllowedOrigin("http://localhost:8080");
    corsConfiguration.addAllowedOrigin("http://localhost:8081");
    corsConfiguration.addAllowedOrigin("http://localhost:8443");
    corsConfiguration.addAllowedOrigin("http://127.0.0.1:8080");
    corsConfiguration.addAllowedOrigin("http://127.0.0.1:8081");
    corsConfiguration.addAllowedOrigin("http://127.0.0.1:8443");

    // 允许生产环境域名
    corsConfiguration.addAllowedOrigin("https://ilf.demoup.cn");
    corsConfiguration.addAllowedOrigin("http://ilf.demoup.cn");

    // 允许所有HTTP方法
    corsConfiguration.addAllowedMethod("*");

    // 允许所有请求头
    corsConfiguration.addAllowedHeader("*");

    // 是否发送Cookie信息
    corsConfiguration.setAllowCredentials(true);

    // 预检请求的缓存时间（秒）
    corsConfiguration.setMaxAge(3600L);

    // 添加映射路径
    source.registerCorsConfiguration("/**", corsConfiguration);

    return new CorsFilter(source);
  }
}
