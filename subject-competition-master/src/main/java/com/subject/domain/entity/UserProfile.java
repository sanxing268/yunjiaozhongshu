package com.subject.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户画像实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_profile")
public class UserProfile implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId
  private Long id;

  /** 用户ID */
  private Long userId;

  /** 学校名称 */
  private String school;

  /** 学院名称 */
  private String college;

  /** 专业名称 */
  private String major;

  /** 年级 */
  private String grade;

  /** 性别 */
  private String gender;

  /** 证书资质（JSON格式） */
  private String certificates;

  /** 获奖经历（JSON格式） */
  private String awards;

  /** 核心技能（JSON格式） */
  private String skills;

  /** 兴趣爱好（JSON格式） */
  private String interests;

  /** 竞赛偏好（JSON格式） */
  private String competitionPreference;

  /** AI生成的用户印象 */
  private String userPortrait;

  /** AI生成的学习计划 */
  private String learningPlan;

  /** AI生成的竞赛推荐 */
  private String competitionRecommendation;

  /** AI生成的就业指导 */
  private String employmentGuidance;

  /** 画像状态：0未填写 1已填写 */
  private Integer profileStatus;

  /** 下次登录是否提醒：0否 1是 */
  private Integer remindNextLogin;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 更新时间 */
  private LocalDateTime updatedAt;
}
