package com.subject.domain.dto;

import lombok.Data;
import java.util.List;

/**
 * AI聊天请求DTO
 */
@Data
public class ChatRequestDTO {
    /**
     * 消息列表
     */
    private List<ChatMessageDTO> messages;
    
    /**
     * 模型名称，默认qwen-plus-latest
     */
    private String model = "qwen-plus-latest";
    
    /**
     * 温度参数，默认0.7
     */
    private Double temperature = 0.7;
    
    /**
     * top_p参数，默认0.8
     */
    private Double topP = 0.8;
    
    /**
     * 是否启用搜索，默认false
     */
    private Boolean enableSearch = false;
    
    /**
     * 是否启用思考过程，默认true
     */
    private Boolean enableThinking = true;
    
    /**
     * 思考预算，默认4000
     */
    private Integer thinkingBudget = 4000;
    
    /**
     * 是否流式输出，默认true
     */
    private Boolean stream = true;
}

