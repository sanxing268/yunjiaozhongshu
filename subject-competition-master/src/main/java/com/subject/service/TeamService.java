package com.subject.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.dto.ProjectQueryDTO;
import com.subject.domain.entity.Team;
import com.subject.domain.entity.TeamMember;
import com.subject.domain.vo.TeamVo;

import java.util.List;


public interface TeamService extends IService<Team> {

    boolean insertTeam(TeamVo team);

    boolean updateTeam(TeamVo teamVo);

    Page<TeamVo> selectPageTeam(Page<Team> teamPage, Wrapper<Team> queryWrapper);

    boolean commitProjectById(Team team);

//    插入团队成员信息
    boolean insertTeamMember(TeamVo teamVo);
}
