package com.subject.service;

import com.subject.domain.dto.DashboardDTO;

public interface DashboardService {
    /**
     * 获取首页数据面板统计信息
     * @return DashboardDTO
     */
    DashboardDTO getDashboardData();
}
