package com.subject.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.subject.commons.result.Result;

import com.subject.domain.entity.Team;
import com.subject.domain.entity.TeamMember;
import com.subject.domain.vo.TeamVo;
import com.subject.service.TeamMemberService;
import com.subject.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sound.midi.Soundbank;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class TeamController {

    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamMemberService teamMemberService;

// 根据学号获取团队编号列表
    @GetMapping("team/member/student/{studentNo}")
    public Result getTeamNosByStudentNo(@PathVariable("studentNo") String studentNo) {
        // 查询该学号对应的所有团队成员记录
        QueryWrapper<TeamMember> wrapper = new QueryWrapper<>();
        wrapper.eq("student_no", studentNo);
        List<TeamMember> memberList = teamMemberService.list(wrapper);

        // 提取团队编号并去重
        List<String> teamNos = memberList.stream()
                .map(TeamMember::getTeamNo)
                .distinct()
                .collect(Collectors.toList());
        // 返回结果
        return Result.ok().data("teamNos", teamNos);
    }


    /**
     * 查询团队信息 构建分页显示
     */
    @PostMapping("team/{size}/{page}")
    public Result teamList(@PathVariable("size") int size, @PathVariable("page") int page,
                           @RequestBody Team team) {
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
//        根据标题以及审核状态查询
        wrapper.like(team.getTitle() != null && !"".equals(team.getTitle()), "team.title", team.getTitle());
        wrapper.eq(team.getStatus() != null && !"".equals(team.getStatus()), "team.status", team.getStatus());
//        页数
        Page<Team> teamPage = new Page<>(page, size);
        Page<TeamVo> teamPageBean = teamService.selectPageTeam(teamPage, wrapper);
//        返回的结果
        return Result.ok().data("list", teamPageBean.getRecords()).data("total", teamPageBean.getTotal());
    }

    /**
     * 插入团队以及团队成员信息
     *
     * @param teamVo
     * @return
     */
    @PostMapping("team/insert")
    public Result insertTeam(@RequestBody TeamVo teamVo) {
        boolean insertTeam = teamService.insertTeam(teamVo);
        if (insertTeam) {
            return Result.ok().message("添加成功");
        } else {
            return Result.error().message("添加失败");
        }
    }

    /**
     * 更新团队以及团队成员信息
     *竞赛报名页面 点击编辑 点击修改按钮使用
     * 新团队管理页面使用
     * @param teamVo
     * @return
     */
    @PutMapping("team/update")
    public Result updateTeam(@RequestBody TeamVo teamVo) {
        boolean updateTeam = teamService.updateTeam(teamVo);
        if (updateTeam) {
            return Result.ok().message("更新成功");
        } else {
            return Result.error().message("更新失败");
        }
    }

    //插入团队成员信息
    @PutMapping("team/member/insert")
    public Result insertTeamMember(@RequestBody TeamVo teamVo) {
        boolean insertTeamMember = teamService.insertTeamMember(teamVo);
        if (insertTeamMember) {
            return Result.ok().message("插入团队成员成功");
        } else {
            return Result.error().message("插入团队成员失败");
        }
    }


    // 根据团队编号查询成员列表
    @GetMapping("team/member/{teamNo}")
    public Result getTeamMemberList(@PathVariable("teamNo") String teamNo) {
        List<TeamMember> memberList = teamMemberService.getMemberListByTeamNo(teamNo);
        return Result.ok().data("memberList", memberList);
    }


    //删除单个团队成员
    @DeleteMapping("member/delete/{id}")
    public Result deleteTeamMember(@PathVariable("id") Integer id) {
        if (id == null || id <= 0) {
            return Result.error().message("无效的成员ID，无法删除");
        }
        boolean removeById = teamMemberService.removeById(id);
        if (removeById) {
            return Result.ok().message("删除成功");
        } else {
            return Result.error().message("删除失败");
        }
    }

    //删除团队
    @DeleteMapping("team/{id}")
    public Result deleteTeam(@PathVariable("id") String id) {
        boolean removeById = teamService.removeById(id);
        if (removeById) {
            return Result.ok().message("删除团队成功");
        } else {
            return Result.error().message("删除团队失败");
        }
    }

//    团队审核
    @PutMapping("team/examine")
    public Result updateTeamById(@RequestBody Team team) {
        boolean update = teamService.updateById(team);
        if (update) {
            return Result.ok().message("更新成功");
        } else {
            return Result.error().message("更新失败");
        }
    }

//    提交项目，结题提交
    @PutMapping("team/commit")
    public Result commitProjectById(@RequestBody Team team) {
        boolean update = teamService.commitProjectById(team);
        if (update) {
            return Result.ok().message("提交成功");
        } else {
            return Result.error().message("提交失败");
        }
    }


}
