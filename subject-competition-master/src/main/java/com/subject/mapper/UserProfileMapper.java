package com.subject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.subject.domain.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户画像Mapper
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {

  /**
   * 根据用户ID查询用户画像
   */
  @Select("SELECT * FROM user_profile WHERE user_id = #{userId}")
  UserProfile selectByUserId(Long userId);

  /**
   * 更新下次登录提醒状态
   */
  @Update("UPDATE user_profile SET remind_next_login = 0 WHERE user_id = #{userId}")
  int updateRemindNextLogin(Long userId);
}
