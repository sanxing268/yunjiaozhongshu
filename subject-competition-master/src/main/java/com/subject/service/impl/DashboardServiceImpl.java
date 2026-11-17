package com.subject.service.impl;

import com.subject.domain.dto.DashboardDTO;
import com.subject.mapper.DashboardMapper;
import com.subject.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;
    @Override
    public DashboardDTO getDashboardData() {
        DashboardDTO dashboardDTO = new DashboardDTO();
        // ========== 方式1：使用DashboardMapper（原生MyBatis/注解SQL） ==========
        dashboardDTO.setUserCount(dashboardMapper.countUser());
        dashboardDTO.setTeamCount(dashboardMapper.countTeam());
        dashboardDTO.setArticleCount(dashboardMapper.countArticle());
        dashboardDTO.setProjectApplyCount(dashboardMapper.countSysProject());
        dashboardDTO.setProjectViewCount(dashboardMapper.countProjectView());
        return dashboardDTO;
    }
}

