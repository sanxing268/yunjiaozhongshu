package com.subject.commons.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class StringBooleanDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String value = p.getText();

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // 处理字符串转布尔的各种情况
        String lowerValue = value.toLowerCase().trim();
        if ("true".equals(lowerValue) || "1".equals(lowerValue) || "yes".equals(lowerValue) || "on".equals(lowerValue)) {
            return true;
        } else if ("false".equals(lowerValue) || "0".equals(lowerValue) || "no".equals(lowerValue) || "off".equals(lowerValue)) {
            return false;
        } else {
            // 如果无法识别，抛出异常或返回默认值
            throw new IllegalArgumentException("无法将字符串 '" + value + "' 转换为布尔类型");
        }
    }
}