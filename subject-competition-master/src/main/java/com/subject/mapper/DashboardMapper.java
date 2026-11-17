package com.subject.mapper;

import org.apache.ibatis.annotations.Mapper;
//集中处理数据面板的数据，便于集中管理
@Mapper
public interface DashboardMapper {
    Long countUser();
    Long countTeam();
    Long countArticle();
    Long countSysProject();
    Long countProjectView();
}
