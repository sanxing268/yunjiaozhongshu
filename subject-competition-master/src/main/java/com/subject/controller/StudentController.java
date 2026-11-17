package com.subject.controller;

import com.subject.commons.result.Result;
import com.subject.domain.entity.User;
import com.subject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/student")
public class StudentController {

    @Autowired
    private UserService userService;

    // 查询所有学生用户
    @GetMapping("/list")
    public Result getStudentList() {
        List<User> studentList = userService.getStudentList();
        return Result.ok().data("studentList", studentList);
    }

    //  新增学生
    @PostMapping("/add")
    public Result addStudent(@RequestBody User user) {
        // 调用service新增学生（需确保UserService实现该方法）
        boolean save = userService.saveStudent(user);
        if (save) {
            return Result.ok().message("新增学生成功");
        } else {
            return Result.error().message("新增学生失败");
        }
    }

    //  修改学生
    @PutMapping("/update")
    public Result updateStudent(@RequestBody User user) {
        // 调用service修改学生（需确保UserService实现该方法）
        boolean update = userService.updateStudentById(user);
        if (update) {
            return Result.ok().message("修改学生成功");
        } else {
            return Result.error().message("修改学生失败");
        }
    }

    // 删除学生
    @DeleteMapping("/delete/{id}")
    public Result deleteStudent(@PathVariable("id") Long id) {
        // 调用service删除学生（需确保UserService实现该方法）
        boolean remove = userService.removeStudentById(id);
        if (remove) {
            return Result.ok().message("删除学生成功");
        } else {
            return Result.error().message("删除学生失败");
        }
    }

}
