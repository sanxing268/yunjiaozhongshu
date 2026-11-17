package com.subject.controller;

import com.subject.commons.result.Result;
import com.subject.domain.entity.Pic;
import com.subject.service.PicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pic")
public class PicController {
    @Autowired
    PicService picService;

//    查询
    @GetMapping("/list")
    public Result listPics() {
        try {
            List<Pic> list = picService.list();
            return Result.ok().data("list", list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("查询失败: " + e.getMessage());
        }
    }

    // 新增轮播图
    @PostMapping("/add")
    public Result addPic(@RequestBody Pic pic) {
        try {
            boolean success = picService.addPic(pic);
            if (success) {
                return Result.ok().message("新增轮播图成功");
            } else {
                return Result.error().message("新增轮播图失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("新增轮播图异常: " + e.getMessage());
        }
    }

    // 修改轮播图
    @PutMapping("/update")
    public Result updatePic(@RequestBody Pic pic) {
        try {
            boolean success = picService.updatePic(pic);
            if (success) {
                return Result.ok().message("修改轮播图成功");
            } else {
                return Result.error().message("修改轮播图失败（可能不存在该轮播图）");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("修改轮播图异常: " + e.getMessage());
        }
    }

    // 删除轮播图
    @DeleteMapping("/delete/{id}")
    public Result deletePic(@PathVariable Integer id) {
        try {
            boolean success = picService.removePic(id);
            if (success) {
                return Result.ok().message("删除轮播图成功");
            } else {
                return Result.error().message("删除轮播图失败（可能不存在该轮播图）");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("删除轮播图异常: " + e.getMessage());
        }
    }
}

