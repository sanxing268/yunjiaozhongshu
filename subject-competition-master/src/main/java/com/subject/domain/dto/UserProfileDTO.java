package com.subject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户问卷DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

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

  /** 证书资质 */
  private List<String> certificates;

  /** 获奖经历 */
  private List<String> awards;

  /** 核心技能 */
  private List<String> skills;

  /** 兴趣爱好 */
  private List<String> interests;

  /** 竞赛偏好 */
  private CompetitionPreferenceDTO competitionPreference;

  /** 是否提醒下次登录 */
  private Integer remindNextLogin;

  /**
   * 竞赛偏好DTO
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CompetitionPreferenceDTO {
    /** 意向竞赛类型 */
    private List<String> competitionTypes;

    /** 参与形式 */
    private String participationForm;

    /** 期望竞赛级别 */
    private List<String> expectedLevels;

    /** 可投入准备时间 */
    private String availableTime;
  }
}
