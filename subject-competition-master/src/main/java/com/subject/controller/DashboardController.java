package com.subject.controller;

import com.subject.commons.result.Result;
import com.subject.commons.result.Result1;
import com.subject.domain.dto.DashboardDTO;
import com.subject.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard") // 接口前缀，可根据项目调整
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取首页数据面板统计信息
     * @return 统一响应结果（包含5个统计指标）
     */
    @GetMapping("/data")
    public Result1<DashboardDTO> getDashboardData() {
        try {
            DashboardDTO dashboardData = dashboardService.getDashboardData();
            return Result1.success(dashboardData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result1.error("获取数据失败");
        }
    }
}
