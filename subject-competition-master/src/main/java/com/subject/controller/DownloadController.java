package com.subject.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.subject.commons.result.Result;
import com.subject.domain.entity.Download;
import com.subject.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/download")
public class DownloadController {

    @Autowired
    DownloadService downloadService;

    // 查询文件列表（公开接口，按上传时间倒序，仅查询未删除记录）
    @GetMapping("/list")
    public Result listDownloads() {
        try {
            QueryWrapper<Download> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0).orderByDesc("create_time");
            List<Download> list = downloadService.list(queryWrapper);
            return Result.ok().data("list", list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("查询文件列表失败: " + e.getMessage());
        }
    }

    // 下载计数（点击下载时调用，增加下载次数）
    @PutMapping("/count/{id}")
    public Result incrementCount(@PathVariable("id") Integer id) {
        try {
            downloadService.incrementCount(id);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("更新下载次数失败");
        }
    }

    // 新增文件
    @PostMapping("/add")
    public Result addDownload(@RequestBody Download download) {
        try {
            boolean success = downloadService.addDownload(download);
            if (success) {
                return Result.ok().message("新增文件成功");
            } else {
                return Result.error().message("新增文件失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("新增文件异常: " + e.getMessage());
        }
    }

    // 修改文件
    @PutMapping("/update")
    public Result updateDownload(@RequestBody Download download) {
        try {
            boolean success = downloadService.updateDownload(download);
            if (success) {
                return Result.ok().message("修改文件成功");
            } else {
                return Result.error().message("修改文件失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("修改文件异常: " + e.getMessage());
        }
    }

    // 删除文件
    @DeleteMapping("/delete/{id}")
    public Result deleteDownload(@PathVariable("id") Integer id) {
        try {
            boolean success = downloadService.removeDownload(id);
            if (success) {
                return Result.ok().message("删除文件成功");
            } else {
                return Result.error().message("删除文件失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("删除文件异常: " + e.getMessage());
        }
    }
}
