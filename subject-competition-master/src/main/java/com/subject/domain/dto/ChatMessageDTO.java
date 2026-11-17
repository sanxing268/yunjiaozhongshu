package com.subject.domain.dto;

import lombok.Data;
import java.util.List;

/**
 * AI聊天消息DTO
 */
@Data
public class ChatMessageDTO {
    /**
     * 消息角色：system, user, assistant
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 多模态内容（可选）
     */
    private List<ContentItem> contentList;
    
    @Data
    public static class ContentItem {
        private String type; // text, image等
        private String text;
        private String imageUrl;
    }
}

