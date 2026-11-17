package com.subject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.dto.UserProfileDTO;
import com.subject.domain.entity.UserProfile;

/**
 * 用户画像Service接口
 */
public interface UserProfileService extends IService<UserProfile> {

  /**
   * 获取用户画像状态
   */
  UserProfile getUserProfile(Long userId);

  /**
   * 保存用户问卷
   */
  boolean saveUserProfile(UserProfileDTO profileDTO);

  /**
   * 生成用户AI画像
   */
  String generateUserPortrait(Long userId);

  /**
   * 生成学习计划
   */
  String generateLearningPlan(Long userId);

  /**
   * 生成竞赛推荐
   */
  String generateCompetitionRecommendation(Long userId);

  /**
   * 生成就业指导
   */
  String generateEmploymentGuidance(Long userId);

  /**
   * 更新下次登录提醒状态
   */
  int updateRemindNextLogin(Long userId);

  /**
   * 检查用户是否需要填写问卷
   */
  boolean needFillProfile(Long userId);
}
