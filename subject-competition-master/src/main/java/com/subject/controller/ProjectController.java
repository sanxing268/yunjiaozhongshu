package com.subject.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.subject.commons.result.Result;
import com.subject.domain.dto.ProjectQueryDTO;
import com.subject.domain.entity.Project;
import com.subject.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sound.midi.Soundbank;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

//    查询团队信息
    @PostMapping("project/{size}/{page}")
    public Result ProjectList(@PathVariable("size") int size, @PathVariable("page") int page,
                              @RequestBody ProjectQueryDTO queryDTO) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getStatus() != null && !"".equals(queryDTO.getStatus()), Project::getStatus, queryDTO.getStatus())
                .like(queryDTO.getName() != null && !"".equals(queryDTO.getName()), Project::getName, queryDTO.getName());
        Page<Project> pageBean = new Page<>(page, size);
        Page<Project> projectPage = projectService.page(pageBean, wrapper);
        return Result.ok().data("list", projectPage.getRecords()).data("total", projectPage.getTotal());
    }

//    查询比赛的信息
    @PostMapping("project/list")
    public Result ProjectList(@RequestBody ProjectQueryDTO queryDTO) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getDeleteFlag,0)
                .eq(queryDTO.getStatus() != null && !"".equals(queryDTO.getStatus()), Project::getStatus, queryDTO.getStatus())
                .like(queryDTO.getName() != null && !"".equals(queryDTO.getName()), Project::getName, queryDTO.getName());
        System.out.println(wrapper);
        List<Project> list = projectService.list(wrapper);
        return Result.ok().data("list", list);
    }

    @DeleteMapping("project/{id}")
    public Result deleteProjectById(@PathVariable("id") int id) {
        boolean remove = projectService.removeById(id);
        if (remove) {
            return Result.ok().message("删除成功");
        } else {
            return Result.error().message("删除失败");
        }
    }

    @PutMapping("project/examine")
    public Result updateProjectById(@RequestBody Project project) {
        boolean update = projectService.updateById(project);
        if (update) {
            return Result.ok().message("更新成功");
        } else {
            return Result.error().message("更新失败");
        }
    }

    @PutMapping("project/commit")
    public Result commitProjectById(@RequestBody Project project) {
        boolean update = projectService.updateById(project);
        if (update) {
            return Result.ok().message("提交成功");
        } else {
            return Result.error().message("提交失败");
        }
    }

    @PutMapping("project/update")
    public Result updateProject(@RequestBody Project project) {
        boolean update = projectService.updateById(project);
        if (update) {
            return Result.ok().message("更新信息成功");
        } else {
            return Result.error().message("更新信息失败");
        }
    }

    @PostMapping("project/insert")
    public Result insertProjectById(@RequestBody Project project) {
        boolean insert = projectService.save(project);
        if (insert) {
            return Result.ok().message("添加成功");
        } else {
            return Result.error().message("添加失败");
        }
    }

}
