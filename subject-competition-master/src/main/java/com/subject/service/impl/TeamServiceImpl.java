package com.subject.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.commons.result.Result;
import com.subject.domain.dto.ProjectQueryDTO;
import com.subject.domain.entity.Conclusion;
import com.subject.domain.entity.TeamMember;
import com.subject.domain.vo.TeamVo;
import com.subject.mapper.ConclusionMapper;
import com.subject.mapper.TeamMapper;
import com.subject.domain.entity.Team;
import com.subject.mapper.TeamMemberMapper;
import com.subject.service.ConclusionService;
import com.subject.service.TeamService;

import com.subject.util.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.NativeWebRequest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    @Resource
    private TeamMapper teamMapper;
    @Resource
    private TeamMemberMapper teamMemberMapper;
    @Resource
    private ConclusionMapper conclusionMapper;


//    项目报名处 点击报名按钮 添加团队信息后点击添加按钮实现新增团队
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public boolean insertTeam(TeamVo teamVo) {
        Team insertTeam = BeanUtils.transformFrom(teamVo, Team.class);
//        设置创建时间
        insertTeam.setEnrollTime(new Date());
//        进一步设置，防止错误
        insertTeam.setCreateUserId(teamVo.getCreateUserId());
        insertTeam.setTeacherId(teamVo.getTeacherId());
        // 生成团队编号
        long currentTimeMillis = System.currentTimeMillis();
//        设置团队编号
        insertTeam.setTeamNo(currentTimeMillis + "");
        List<TeamMember> memberList = teamVo.getMemberList();
        if (memberList != null && memberList.size() > 0) {
            for (TeamMember member : memberList) {
//                设置每个团队成员的团队编号
                member.setTeamNo(currentTimeMillis + "");
            }
        } else {
            return false;
        }
//        先插入团队成员表
        boolean flag = teamMemberMapper.saveTeamMemberList(memberList);
        //创建结题信息
        Conclusion conclusion = new Conclusion();
//设置结题申请的申请id为比赛的赛事id
        conclusion.setApplyId(insertTeam.getProjectId());
//        设置结题申请的团队编号
        conclusion.setTeamNo(insertTeam.getTeamNo());
//        插入结题表
        int insert = conclusionMapper.insert(conclusion);
//        插入团队表
        Boolean saveTeam = teamMapper.saveTeam(insertTeam);
        if (flag && saveTeam && insert > 0) {
            return true;
        } else {
            return false;
        }
    }

    // 更新团队信息
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public boolean updateTeam(TeamVo teamVo) {
        System.out.println("实体类之间的属性赋值前" + teamVo.getTeamNo());
        Team updateTeam = BeanUtils.transformFrom(teamVo, Team.class);
        System.out.println("实体类之间的属性赋值后" + updateTeam.getTeamNo());

        // 获取转换后的团队编号
        String targetTeamNo = updateTeam.getTeamNo();
        // 获取团队成员列表
        List<TeamMember> teamVoTeamMembers = teamVo.getMemberList();

        // 遍历成员列表，为每个TeamMember对象赋值teamNo
        if (teamVoTeamMembers != null && !teamVoTeamMembers.isEmpty()) {
            teamVoTeamMembers.forEach(member -> {
                // 为当前成员对象设置转换后的团队编号
                member.setTeamNo(targetTeamNo);
            });
        }
        //待更新的队伍成员编号
        List<Integer> updateTeamMemberNo = teamVoTeamMembers.stream().map(TeamMember::getMemberNo).collect(Collectors.toList());
        //原数据库,队伍成员信息
        List<TeamMember> teamMembers = teamMemberMapper.selectList(new LambdaQueryWrapper<TeamMember>().eq(TeamMember::getTeamNo, updateTeam.getTeamNo()));
        //原数据库,队伍成员信息编号
        List<Integer> teamMemberNo = teamMembers.stream().map(TeamMember::getMemberNo).collect(Collectors.toList());
        //数据比对
        if (teamVoTeamMembers != null && teamVoTeamMembers.size() >= 0) {
            teamMemberNo.forEach(no -> {
                if (!updateTeamMemberNo.contains(no)) {
                    teamMemberMapper.deleteById(no);
                }
                ;
            });
        }
        boolean flag = teamMemberMapper.saveTeamMemberList(teamVo.getMemberList());
        LambdaUpdateWrapper<Team> updateWrapper = new LambdaUpdateWrapper<Team>().eq(Team::getTeamNo, updateTeam.getTeamNo()).set(Team::getTitle, updateTeam.getTitle())
                .set(Team::getTeamName, updateTeam.getTeamName()).set(Team::getStatus, updateTeam.getStatus());
        conclusionMapper.update(null, new LambdaUpdateWrapper<Conclusion>().eq(Conclusion::getTeamNo, updateTeam.getTeamNo()).set(Conclusion::getApplyId, updateTeam.getProjectId()));
        int update = teamMapper.update(null, updateWrapper);
        if (flag && update >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Page<TeamVo> selectPageTeam(Page<Team> teamPage, Wrapper<Team> queryWrapper) {
        return teamMapper.selectPageTeam(teamPage, queryWrapper);
    }

    @Override
    public boolean commitProjectById(Team team) {
        // 调用Mapper的自定义更新方法，返回受影响行数
        int affectedRows = teamMapper.updateTeamForCommit(team);
        // 受影响行数>0则更新成功，返回true；否则返回false
        return affectedRows > 0;
    }

//    插入团队成员信息
    @Override
    public boolean insertTeamMember(TeamVo teamVo) {
        // 获取团队成员列表
        List<TeamMember> teamVoTeamMembers = teamVo.getMemberList();
        // 遍历成员列表，为每个TeamMember对象赋值teamNo
        if (teamVoTeamMembers != null && !teamVoTeamMembers.isEmpty()) {
            teamVoTeamMembers.forEach(member -> {
                // 为当前成员对象设置转换后的团队编号
                member.setTeamNo(teamVo.getTeamNo());
            });
        }
       return  teamMemberMapper.saveTeamMemberList(teamVoTeamMembers);
    }

}
