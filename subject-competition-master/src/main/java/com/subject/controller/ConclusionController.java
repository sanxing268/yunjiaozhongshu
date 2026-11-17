package com.subject.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.subject.commons.result.Result;
import com.subject.domain.dto.ConclusionUpdateDTO;
import com.subject.domain.dto.ProjectQueryDTO;
import com.subject.domain.entity.*;
import com.subject.domain.vo.ConclusionVo;
import com.subject.domain.vo.TeamVo;
import com.subject.service.ConclusionService;
import com.subject.service.PrizeService;
import com.subject.service.TeamMemberService;
import com.subject.service.TeamService;
import com.subject.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class ConclusionController {

    @Autowired
    private ConclusionService conclusionService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private PrizeService prizeService;

    @PostMapping("conclusion/{size}/{page}")
    public Result conclusionList(@PathVariable("size") int size, @PathVariable("page") int page,
                                 @RequestBody ProjectQueryDTO queryDTO) {
        QueryWrapper<ProjectQueryDTO> wrapper = new QueryWrapper<>();
        wrapper.like(queryDTO.getName() != null && !"".equals(queryDTO.getName()), "project.name", queryDTO.getName());
//        wrapper.eq("project.status", 1);//只显示sys_project表中待审核的项目
        Page<ConclusionVo> pageBean = new Page<>(page, size);
        IPage<ConclusionVo> conclusionVoIPage = conclusionService.selectPageConclusionList(pageBean, wrapper);
        ArrayList<TeamVo> teamVos = new ArrayList<>();
        for (ConclusionVo record : conclusionVoIPage.getRecords()) {
            teamVos.add(record.getTeamVo());
        }
        return Result.ok().data("list", conclusionVoIPage.getRecords()).data("teamVo", teamVos).data("total", conclusionVoIPage.getTotal());
    }

    //更新信息
    @PutMapping("conclusion/update")
    public Result updateConclusion(@RequestBody ConclusionUpdateDTO conclusion) {
        Conclusion updateConclusion = BeanUtils.transformFrom(conclusion, Conclusion.class);
        LambdaUpdateWrapper<Team> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Team::getTeamNo, conclusion.getTeamNo()).set(Team::getConStatus, conclusion.getStatus());
        boolean update = teamService.update(updateWrapper);
        boolean save = conclusionService.saveOrUpdate(updateConclusion);
        if (save && update) {
            return Result.ok().message("更新信息成功");
        } else {
            return Result.error().message("更新信息失败");
        }
    }

    //提交结题附件
    @PutMapping("conclusion/commit")
    public Result commitConclusionById(@RequestBody Conclusion conclusion) {
        boolean update = conclusionService.updateById(conclusion);
        if (update) {
            return Result.ok().message("提交成功");
        } else {
            return Result.error().message("提交失败");
        }
    }

    //审核 通过或不通过 评奖
    @PutMapping("conclusion/examine")
    public Result updateConclusionById(@RequestBody ConclusionUpdateDTO updateDTO) {
        boolean update = conclusionService.updateConclusion(updateDTO);
        if (update) {
            return Result.ok().message("更新成功");
        } else {
            return Result.error().message("更新失败");
        }
    }

    @GetMapping("conclusion/prize")
    public Result getPrize() {
        List<Prize> list = prizeService.list();
        return Result.ok().data("list", list);
    }
}
