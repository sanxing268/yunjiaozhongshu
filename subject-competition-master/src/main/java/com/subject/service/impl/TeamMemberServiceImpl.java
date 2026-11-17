package com.subject.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.subject.mapper.TeamMemberMapper;
import com.subject.domain.entity.TeamMember;
import com.subject.service.TeamMemberService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class TeamMemberServiceImpl extends ServiceImpl<TeamMemberMapper, TeamMember> implements TeamMemberService {
    // 根据团队编号查询成员列表
    @Override
    public List<TeamMember> getMemberListByTeamNo(String teamNo) {
        QueryWrapper<TeamMember> wrapper = new QueryWrapper<>();
        // 绑定团队编号，且删除标识为0（未删除）
        wrapper.eq("team_no", teamNo)
                .eq("delete_flag", 0);
        return this.baseMapper.selectList(wrapper);
    }
}
