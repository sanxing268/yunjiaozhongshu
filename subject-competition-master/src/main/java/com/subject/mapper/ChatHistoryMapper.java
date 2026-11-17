package com.subject.mapper;

import com.subject.domain.entity.ChatHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 聊天历史记录数据访问层
 */
@Mapper
public interface ChatHistoryMapper {

  @Insert("INSERT INTO chat_history (user_id, user_message, assistant_message, thinking_process, create_time, del_flag) "
      +
      "VALUES (#{userId}, #{userMessage}, #{assistantMessage}, #{thinkingProcess}, #{createTime}, #{delFlag})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(ChatHistory chatHistory);

  @Select("SELECT * FROM chat_history WHERE id = #{id}")
  ChatHistory selectById(Long id);

  @Select("SELECT * FROM chat_history WHERE user_id = #{userId} AND del_flag = 0 ORDER BY create_time DESC LIMIT #{limit}")
  List<ChatHistory> selectByUserId(@Param("userId") Long userId, @Param("limit") int limit);

  @Select("SELECT COUNT(*) FROM chat_history WHERE user_id = #{userId} AND del_flag = 0")
  long countByUserId(Long userId);

  @Update("UPDATE chat_history SET del_flag = 1 WHERE id = #{id}")
  int deleteById(Long id);
}
