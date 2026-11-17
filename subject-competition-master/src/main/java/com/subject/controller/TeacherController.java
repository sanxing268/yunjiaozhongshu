package com.subject.controller;

import com.subject.commons.result.Result;
import com.subject.domain.entity.User;
import com.subject.service.AIRecognitionService;
import com.subject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/teacher") // 统一前缀：教师接口
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private AIRecognitionService aiRecognitionService;

    // 查询所有教师用户
    @GetMapping("/list")
    public Result getProfessorList() {
        List<User> teacherList = userService.getTeacherList();
        return Result.ok().data("teacherList", teacherList);
    }

    // 新增教师
    @PostMapping("/add")
    public Result addTeacher(@RequestBody User user) {
        boolean success = userService.saveTeacher(user);
        if (success) {
            return Result.ok().message("新增教师成功");
        }
        return Result.error().message("新增教师失败");
    }

    // 修改教师
    @PutMapping("/update")
    public Result updateTeacher(@RequestBody User user) {
        boolean success = userService.updateTeacher(user);
        if (success) {
            return Result.ok().message("修改教师成功");
        }
        return Result.error().message("修改教师失败");
    }

    // 删除教师（根据ID）
    @DeleteMapping("/delete/{id}")
    public Result deleteTeacher(@PathVariable Long id) {
        boolean success = userService.removeTeacher(id);
        if (success) {
            return Result.ok().message("删除教师成功");
        }
        return Result.error().message("删除教师失败");
    }

    // 批量添加教师
    @PostMapping("/batchAdd")
    public Result batchAddTeachers(@RequestBody List<User> teachers) {
        try {
            if (teachers == null || teachers.isEmpty()) {
                return Result.error().message("教师列表不能为空");
            }

            int successCount = 0;
            int failureCount = 0;
            StringBuilder errorMessages = new StringBuilder();

            for (int i = 0; i < teachers.size(); i++) {
                User teacher = teachers.get(i);
                try {
                    // 验证必填字段
                    if (teacher.getUsername() == null || teacher.getUsername().trim().isEmpty()) {
                        failureCount++;
                        errorMessages.append("第").append(i + 1).append("条：账号不能为空; ");
                        continue;
                    }
                    if (teacher.getName() == null || teacher.getName().trim().isEmpty()) {
                        failureCount++;
                        errorMessages.append("第").append(i + 1).append("条：姓名不能为空; ");
                        continue;
                    }
                    if (teacher.getPassword() == null || teacher.getPassword().trim().isEmpty()) {
                        failureCount++;
                        errorMessages.append("第").append(i + 1).append("条：密码不能为空; ");
                        continue;
                    }

                    if (userService.saveTeacher(teacher)) {
                        successCount++;
                    } else {
                        failureCount++;
                        errorMessages.append("第").append(i + 1).append("条：保存失败（可能用户名已存在）; ");
                    }
                } catch (Exception e) {
                    failureCount++;
                    errorMessages.append("第").append(i + 1).append("条：").append(e.getMessage()).append("; ");
                    e.printStackTrace();
                }
            }

            return Result.ok()
                    .message("批量添加完成")
                    .data("successCount", successCount)
                    .data("failureCount", failureCount)
                    .data("total", teachers.size())
                    .data("errorDetails", errorMessages.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("批量添加失败：" + e.getMessage());
        }
    }

    // AI图像识别接口
    @PostMapping("/recognize")
    public Result recognizeTeacherInfo(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error().message("文件不能为空");
            }

            // 获取图片数据
            byte[] imageData = file.getBytes();

            // 调用AI识别服务
            List<Map<String, String>> results = aiRecognitionService.recognizeTeacherInfo(imageData);

            return Result.ok()
                    .message("识别成功")
                    .data("results", results)
                    .data("count", results.size());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("识别失败：" + e.getMessage());
        }
    }
}
