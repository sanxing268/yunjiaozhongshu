package com.subject.domain.dto;
import lombok.Data;

/**
 * 首页数据面板DTO
 * 封装5个核心统计指标
 */
@Data
public class DashboardDTO {
    /**
     * 用户总量
     */
    private Long userCount;

    /**
     * 团队总量（sys_team）
     */
    private Long teamCount;

    /**
     * 通知公告总量（article）
     */
    private Long articleCount;

    /**
     * 申请立项总量（sys_project）
     */
    private Long projectApplyCount;

    /**
     * 竞赛一览总量（project_view）
     */
    private Long projectViewCount;

}
