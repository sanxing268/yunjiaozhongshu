package com.subject.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.subject.domain.entity.TeamMember;

import java.util.List;

public interface TeamMemberMapper extends BaseMapper<TeamMember> {
    boolean saveTeamMemberList(List<TeamMember> teamMembers);
}
