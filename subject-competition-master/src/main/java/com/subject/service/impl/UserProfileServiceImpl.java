package com.subject.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.domain.dto.UserProfileDTO;
import com.subject.domain.entity.UserProfile;
import com.subject.mapper.UserProfileMapper;
import com.subject.service.AIService;
import com.subject.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户画像Service实现
 */
@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

  @Autowired
  private UserProfileMapper userProfileMapper;

  @Autowired
  private AIService aiService;

  @Override
  public UserProfile getUserProfile(Long userId) {
    return userProfileMapper.selectByUserId(userId);
  }

  @Override
  public boolean saveUserProfile(UserProfileDTO profileDTO) {
    try {
      UserProfile profile = userProfileMapper.selectByUserId(profileDTO.getUserId());

      UserProfile userProfile = new UserProfile();
      userProfile.setUserId(profileDTO.getUserId());
      userProfile.setSchool(profileDTO.getSchool());
      userProfile.setCollege(profileDTO.getCollege());
      userProfile.setMajor(profileDTO.getMajor());
      userProfile.setGrade(profileDTO.getGrade());
      userProfile.setGender(profileDTO.getGender());

      // 将列表转换为JSON字符串存储
      userProfile.setCertificates(JSON.toJSONString(profileDTO.getCertificates()));
      userProfile.setAwards(JSON.toJSONString(profileDTO.getAwards()));
      userProfile.setSkills(JSON.toJSONString(profileDTO.getSkills()));
      userProfile.setInterests(JSON.toJSONString(profileDTO.getInterests()));
      userProfile.setCompetitionPreference(JSON.toJSONString(profileDTO.getCompetitionPreference()));

      userProfile.setProfileStatus(1); // 已填写
      userProfile.setRemindNextLogin(profileDTO.getRemindNextLogin() != null ? profileDTO.getRemindNextLogin() : 1);
      userProfile.setUpdatedAt(LocalDateTime.now());

      if (profile != null) {
        // 更新现有记录
        userProfile.setId(profile.getId());
        userProfile.setCreatedAt(profile.getCreatedAt());
        return updateById(userProfile);
      } else {
        // 创建新记录
        userProfile.setCreatedAt(LocalDateTime.now());
        return save(userProfile);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public String generateUserPortrait(Long userId) {
    UserProfile profile = getUserProfile(userId);
    if (profile == null || profile.getProfileStatus() == 0) {
      return null;
    }

    try {
      // 构建AI提示词
      StringBuilder prompt = new StringBuilder();
      prompt.append("基于以下用户信息，用中文生成用户的人物画像（200字以内）：\n");
      prompt.append("学校：").append(profile.getSchool()).append("\n");
      prompt.append("学院：").append(profile.getCollege()).append("\n");
      prompt.append("专业：").append(profile.getMajor()).append("\n");
      prompt.append("年级：").append(profile.getGrade()).append("\n");
      prompt.append("性别：").append(profile.getGender()).append("\n");

      if (profile.getCertificates() != null) {
        prompt.append("证书资质：").append(profile.getCertificates()).append("\n");
      }
      if (profile.getAwards() != null) {
        prompt.append("获奖经历：").append(profile.getAwards()).append("\n");
      }
      if (profile.getSkills() != null) {
        prompt.append("核心技能：").append(profile.getSkills()).append("\n");
      }
      if (profile.getInterests() != null) {
        prompt.append("兴趣爱好：").append(profile.getInterests()).append("\n");
      }

      // 调用AI生成内容（同步调用，获取结果）
      String portrait = aiService.generateContent(prompt.toString());

      // 保存到数据库
      UserProfile update = new UserProfile();
      update.setId(profile.getId());
      update.setUserPortrait(portrait);
      updateById(update);

      return portrait;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String generateLearningPlan(Long userId) {
    UserProfile profile = getUserProfile(userId);
    if (profile == null || profile.getProfileStatus() == 0) {
      return null;
    }

    try {
      StringBuilder prompt = new StringBuilder();
      prompt.append("基于用户的技能和兴趣爱好，用中文生成为期3个月的学习计划（500字以内）：\n");

      if (profile.getSkills() != null) {
        prompt.append("当前核心技能：").append(profile.getSkills()).append("\n");
      }
      if (profile.getInterests() != null) {
        prompt.append("兴趣爱好：").append(profile.getInterests()).append("\n");
      }
      if (profile.getMajor() != null) {
        prompt.append("专业：").append(profile.getMajor()).append("\n");
      }

      prompt.append("请提供具体的学习路线、推荐资源和预期目标。");

      String plan = aiService.generateContent(prompt.toString());

      UserProfile update = new UserProfile();
      update.setId(profile.getId());
      update.setLearningPlan(plan);
      updateById(update);

      return plan;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String generateCompetitionRecommendation(Long userId) {
    UserProfile profile = getUserProfile(userId);
    if (profile == null || profile.getProfileStatus() == 0) {
      return null;
    }

    try {
      StringBuilder prompt = new StringBuilder();
      prompt.append("根据用户的技能、兴趣和竞赛偏好，用中文推荐合适的竞赛（500字以内）：\n");

      if (profile.getSkills() != null) {
        prompt.append("核心技能：").append(profile.getSkills()).append("\n");
      }
      if (profile.getInterests() != null) {
        prompt.append("兴趣爱好：").append(profile.getInterests()).append("\n");
      }
      if (profile.getCompetitionPreference() != null) {
        prompt.append("竞赛偏好：").append(profile.getCompetitionPreference()).append("\n");
      }

      prompt.append("请推荐具体的竞赛项目、参赛建议和准备方向。");

      String recommendation = aiService.generateContent(prompt.toString());

      UserProfile update = new UserProfile();
      update.setId(profile.getId());
      update.setCompetitionRecommendation(recommendation);
      updateById(update);

      return recommendation;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String generateEmploymentGuidance(Long userId) {
    UserProfile profile = getUserProfile(userId);
    if (profile == null || profile.getProfileStatus() == 0) {
      return null;
    }

    try {
      StringBuilder prompt = new StringBuilder();
      prompt.append("基于用户的背景、技能和奖项，用中文提供就业指导建议（500字以内）：\n");

      if (profile.getSchool() != null) {
        prompt.append("学校：").append(profile.getSchool()).append("\n");
      }
      if (profile.getMajor() != null) {
        prompt.append("专业：").append(profile.getMajor()).append("\n");
      }
      if (profile.getSkills() != null) {
        prompt.append("核心技能：").append(profile.getSkills()).append("\n");
      }
      if (profile.getAwards() != null) {
        prompt.append("获奖经历：").append(profile.getAwards()).append("\n");
      }
      if (profile.getCertificates() != null) {
        prompt.append("证书资质：").append(profile.getCertificates()).append("\n");
      }

      prompt.append("请提供职业发展方向、求职建议和能力补充建议。");

      String guidance = aiService.generateContent(prompt.toString());

      UserProfile update = new UserProfile();
      update.setId(profile.getId());
      update.setEmploymentGuidance(guidance);
      updateById(update);

      return guidance;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public int updateRemindNextLogin(Long userId) {
    return userProfileMapper.updateRemindNextLogin(userId);
  }

  @Override
  public boolean needFillProfile(Long userId) {
    UserProfile profile = userProfileMapper.selectByUserId(userId);

    // 如果没有记录，需要填写
    if (profile == null) {
      // 创建初始记录
      UserProfile newProfile = new UserProfile();
      newProfile.setUserId(userId);
      newProfile.setProfileStatus(0);
      newProfile.setRemindNextLogin(1);
      newProfile.setCreatedAt(LocalDateTime.now());
      newProfile.setUpdatedAt(LocalDateTime.now());
      save(newProfile);
      return true;
    }

    // 如果状态为0（未填写）且需要提醒，则返回true
    if (profile.getProfileStatus() == 0 && profile.getRemindNextLogin() == 1) {
      return true;
    }

    return false;
  }
}
