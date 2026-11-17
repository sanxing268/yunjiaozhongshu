package com.subject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.entity.TeamMember;

import java.util.List;


public interface TeamMemberService extends IService<TeamMember> {
    // 根据团队编号查询成员列表
    List<TeamMember> getMemberListByTeamNo(String teamNo);

}
