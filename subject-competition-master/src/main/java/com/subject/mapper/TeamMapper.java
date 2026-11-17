package com.subject.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.subject.domain.dto.ProjectQueryDTO;
import com.subject.domain.entity.Team;

import com.subject.domain.entity.TeamMember;
import com.subject.domain.vo.TeamVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;



public interface TeamMapper extends BaseMapper<Team> {

    int updateTeamForCommit(@Param("team") Team team);

    Boolean saveTeam(Team insertTeam);

    Page<TeamVo>  selectPageTeam(Page<Team> teamPage, @Param(Constants.WRAPPER) Wrapper<Team> queryWrapper);
}
