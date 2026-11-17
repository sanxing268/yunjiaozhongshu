package com.subject.commons.config;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        // 1. 创建 ObjectMapper 实例
        ObjectMapper objectMapper = new ObjectMapper();
        // 2. 创建自定义模块，注册字符串转 boolean 反序列化器
        SimpleModule simpleModule = new SimpleModule();
        // 为 boolean 类型注册反序列化器
        simpleModule.addDeserializer(boolean.class, new StringBooleanDeserializer());
        // 为 Boolean 包装类注册反序列化器（兼容可空类型）
        simpleModule.addDeserializer(Boolean.class, new StringBooleanDeserializer());
        // 3. 注册模块并配置其他可选特性
        objectMapper.registerModule(simpleModule);
        // 忽略未知字段（可选，避免前端传多余字段报错）
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 4. 创建并返回消息转换器
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}
