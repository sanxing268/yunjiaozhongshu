package com.subject.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.subject.commons.result.Result;
import com.subject.domain.entity.Achievement;
import com.subject.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievement")
public class AchievementController {

    @Autowired
    AchievementService achievementService;

    // 查询竞赛成果列表（公开接口，按创建时间倒序，仅查询未删除记录）
    @GetMapping("/list")
    public Result listAchievements() {
        try {
            QueryWrapper<Achievement> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0).orderByDesc("create_time");
            List<Achievement> list = achievementService.list(queryWrapper);
            return Result.ok().data("list", list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("查询竞赛成果失败: " + e.getMessage());
        }
    }

    // 新增竞赛成果
    @PostMapping("/add")
    public Result addAchievement(@RequestBody Achievement achievement) {
        try {
            boolean success = achievementService.addAchievement(achievement);
            if (success) {
                return Result.ok().message("新增竞赛成果成功");
            } else {
                return Result.error().message("新增竞赛成果失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("新增竞赛成果异常: " + e.getMessage());
        }
    }

    // 修改竞赛成果
    @PutMapping("/update")
    public Result updateAchievement(@RequestBody Achievement achievement) {
        try {
            boolean success = achievementService.updateAchievement(achievement);
            if (success) {
                return Result.ok().message("修改竞赛成果成功");
            } else {
                return Result.error().message("修改竞赛成果失败（可能不存在该记录）");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("修改竞赛成果异常: " + e.getMessage());
        }
    }

    // 删除竞赛成果
    @DeleteMapping("/delete/{id}")
    public Result deleteAchievement(@PathVariable("id") Integer id) {
        try {
            boolean success = achievementService.removeAchievement(id);
            if (success) {
                return Result.ok().message("删除竞赛成果成功");
            } else {
                return Result.error().message("删除竞赛成果失败（可能不存在该记录）");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("删除竞赛成果异常: " + e.getMessage());
        }
    }
}
