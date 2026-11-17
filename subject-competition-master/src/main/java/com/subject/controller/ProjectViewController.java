package com.subject.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.subject.commons.result.Result;
import com.subject.domain.dto.ProjectQueryDTO;
import com.subject.domain.dto.ProjectViewUpdateDTO;
import com.subject.domain.entity.ProjectView;
import com.subject.service.CosService;
import com.subject.service.ProjectViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ProjectViewController {
    @Autowired
    private ProjectViewService projectViewService;

    @Autowired
    private CosService cosService;

    // 前台访问地址
    @PostMapping("/api/projectView/{size}/{page}")
    public Result listBooks(@PathVariable("size") int size, @PathVariable("page") int page,
            @RequestBody(required = false) ProjectQueryDTO queryDTO) {
        Page<ProjectView> pagebean = new Page<>(page, size);
        QueryWrapper<ProjectView> wrapper = new QueryWrapper<>();
        wrapper.likeRight(queryDTO.getName() != null && !"".equals(queryDTO.getName()), "project.title",
                queryDTO.getName());
        wrapper.eq(queryDTO.getCid() != null && !"".equals(queryDTO.getCid()), "c.id", queryDTO.getCid());
        IPage<ProjectView> projectViewIPage = projectViewService.selectProjectViewList(pagebean, wrapper);
        return Result.ok().data("list", projectViewIPage.getRecords()).data("total", projectViewIPage.getTotal());
    }

    // 后台访问地址
    @PostMapping("/api/admin/projectView/{size}/{page}")
    public Result AdminListBooks(@PathVariable("size") int size, @PathVariable("page") int page,
            @RequestBody(required = false) ProjectQueryDTO queryDTO) {
        Page<ProjectView> pagebean = new Page<>(page, size);
        QueryWrapper<ProjectView> wrapper = new QueryWrapper<>();
        wrapper.likeRight(queryDTO.getName() != null && !"".equals(queryDTO.getName()), "project.title",
                queryDTO.getName());
        wrapper.eq(queryDTO.getCid() != null && !"".equals(queryDTO.getCid()), "c.id", queryDTO.getCid());
        IPage<ProjectView> projectViewIPage = projectViewService.selectProjectViewList(pagebean, wrapper);
        return Result.ok().data("list", projectViewIPage.getRecords()).data("total", projectViewIPage.getTotal());
    }

    @GetMapping("/api/categories/{cid}/books")
    public Result listByCategory(@PathVariable("cid") int cid) {
        QueryWrapper<ProjectView> wrapper = new QueryWrapper<ProjectView>().eq("cid", cid);
        if (0 != cid) {
//            返回指定的
            return Result.ok().data("list", projectViewService.list(wrapper));
        } else {
//            返回全部的
            return Result.ok().data("list", projectViewService.list());
        }
    }

    /**
     * 根据author字段分类获取竞赛列表（用于首页竞赛一览分类）
     * author: 国家级、国际级、省级等
     */
    @GetMapping("/api/projectView/byAuthor/{author}")
    public Result listByAuthor(@PathVariable("author") String author) {
        QueryWrapper<ProjectView> wrapper = new QueryWrapper<>();
        if (author != null && !"".equals(author.trim())) {
            wrapper.eq("author", author);
        }
        List<ProjectView> projectViews = projectViewService.list(wrapper);
        return Result.ok().data("list", projectViews).data("author", author);
    }

    /**
     * 获取所有作者类别（用于竞赛分类标签）
     */
    @GetMapping("/api/projectView/authors")
    public Result listAllAuthors() {
        List<ProjectView> allProjects = projectViewService.list();
        // 提取所有不同的author值
        java.util.Set<String> authorSet = new java.util.LinkedHashSet<>();
        for (ProjectView pv : allProjects) {
            if (pv.getAuthor() != null && !"".equals(pv.getAuthor())) {
                authorSet.add(pv.getAuthor());
            }
        }
        // 按顺序排列：国际级 > 国家级 > 省级 > 其他
        java.util.List<String> authors = new java.util.ArrayList<>();
        if (authorSet.contains("国际级"))
            authors.add("国际级");
        if (authorSet.contains("国家级"))
            authors.add("国家级");
        if (authorSet.contains("省级"))
            authors.add("省级");
        for (String author : authorSet) {
            if (!author.equals("国际级") && !author.equals("国家级") && !author.equals("省级")) {
                authors.add(author);
            }
        }
        return Result.ok().data("authors", authors);
    }

    @GetMapping("/api/search")
    public Result searchResult(@RequestParam("keywords") String keywords) {
        // 关键词为空时查询出所有书籍
        if ("".equals(keywords)) {
            return Result.ok().data("list", projectViewService.list());
        } else {
            QueryWrapper<ProjectView> wrapper = new QueryWrapper<>();
            wrapper.like("title", keywords);
            return Result.ok().data("list", projectViewService.list(wrapper));
        }
    }

    // 根据id获取单个project_view记录（用于查询ai字段）
    @GetMapping("/api/projectView/{id}")
    public Result getProjectViewById(@PathVariable("id") Integer id) {
        try {
            ProjectView projectView = projectViewService.getById(id);
            if (projectView != null) {
                return Result.ok().data("data", projectView);
            } else {
                return Result.error().message("未找到对应的记录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("查询失败: " + e.getMessage());
        }
    }

    // 更新project_view的ai字段
    @PutMapping("/api/projectView/{id}/ai")
    public Result updateProjectViewAi(@PathVariable("id") Integer id, @RequestBody Map<String, String> request) {
        try {
            // 先查询记录是否存在
            ProjectView existingView = projectViewService.getById(id);
            if (existingView == null) {
                return Result.error().message("未找到对应的记录");
            }

            // 获取ai字段内容
            String aiContent = request.get("ai");
            if (aiContent == null) {
                return Result.error().message("ai字段不能为空");
            }

            // 只更新ai字段
            existingView.setAi(aiContent);
            boolean update = projectViewService.updateById(existingView);

            if (update) {
                return Result.ok().message("更新成功");
            } else {
                return Result.error().message("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("更新失败: " + e.getMessage());
        }
    }

    @PostMapping("/api/admin/content/projectView")
    public Result addOrUpdate(@RequestBody ProjectViewUpdateDTO projectView) {
        int updateBook = projectViewService.updateProjectView(projectView);
        if (updateBook > 0) {
            return Result.ok().message("修改成功");
        } else {
            return Result.error().message("修改失败");
        }
    }

    @GetMapping("/api/admin/content/projectView/delete/{id}")
    public Result delete(@PathVariable("id") Integer id) {

        // boolean remove = projectViewService.removeById(id);
        // if (remove) {
        // return Result.ok().message("删除成功");
        // } else {
        // return Result.error().message("删除失败");
        try {
            // 先查询记录，获取cover字段（图片URL）
            ProjectView projectView = projectViewService.getById(id);
            if (projectView != null && projectView.getCover() != null && !projectView.getCover().isEmpty()) {
                // 删除COS中的文件
                String coverUrl = projectView.getCover();
                System.out.println("准备删除项目封面图片: " + coverUrl);
                boolean cosDeleted = cosService.deleteFile(coverUrl);
                if (cosDeleted) {
                    System.out.println("COS文件删除成功");
                } else {
                    System.out.println("COS文件删除失败，但继续删除数据库记录");
                }
            }

            // 删除数据库记录
            boolean remove = projectViewService.removeById(id);
            if (remove) {
                return Result.ok().message("删除成功");
            } else {
                return Result.error().message("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("删除时发生错误: " + e.getMessage());
        }
    }

    @PostMapping("/api/admin/content/projectView/batchDelete")
    public Result batchDeleteProjectView(@RequestBody List<Integer> ids) {
        // int deleteUser = projectViewService.batchDeleteProjectView(ids);
        // if (deleteUser > 0) {
        // return Result.ok().message("批量删除成功");
        // } else {
        // return Result.error().message("批量删除失败");
        try {
            // 批量查询记录，获取所有cover字段
            List<ProjectView> projectViews = projectViewService.listByIds(ids);
            for (ProjectView projectView : projectViews) {
                if (projectView.getCover() != null && !projectView.getCover().isEmpty()) {
                    // 删除COS中的文件
                    String coverUrl = projectView.getCover();
                    System.out.println("准备删除项目封面图片: " + coverUrl);
                    cosService.deleteFile(coverUrl);
                }
            }

            // 批量删除数据库记录
            int deleteUser = projectViewService.batchDeleteProjectView(ids);
            if (deleteUser > 0) {
                return Result.ok().message("批量删除成功");
            } else {
                return Result.error().message("批量删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("批量删除时发生错误: " + e.getMessage());
        }
    }

    @PostMapping("/api/admin/content/view/covers")
    public String coversUpload(MultipartFile file) {
        // 1. 定义上传目录（和静态资源映射目录一致）
        // String folder = "E:\\file\\uploadImg";
        // File imageFolder = new File(folder);
        // if (!imageFolder.exists()) {
        // imageFolder.mkdirs(); // 确保目录存在
        // }
        //
        // // 2. 处理文件名：避免重复覆盖（原代码拼接逻辑无意义，优化为时间戳+原文件名）
        // String originalFileName = file.getOriginalFilename();
        // if (originalFileName == null || originalFileName.isEmpty()) {
        // return "";
        // }
        // // 生成唯一文件名：时间戳_原文件名（防止同名覆盖）
        // String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
        // File f = new File(imageFolder, uniqueFileName);
        //
        // try {
        // file.transferTo(f);
        // // 3. 返回完整可访问的URL（核心！和静态资源映射路径匹配）
        // // 假设后端启动端口是 8443（根据你代码中原有URL推测），请替换为实际端口
        // String fullImgUrl = "http://localhost:8443/api/file/" + uniqueFileName;
        // return fullImgUrl; // 前端直接用这个URL作为图片src
        // } catch (IOException e) {
        // e.printStackTrace();
        // return "";
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("上传文件为空");
            }
            // 上传到 COS，目录使用 images/projectView
            String url = cosService.uploadFile(file, "images/projectView");
            System.out.println("图片上传成功，URL: " + url);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }
}
