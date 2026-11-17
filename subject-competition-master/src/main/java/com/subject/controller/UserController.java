package com.subject.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.subject.commons.result.Result;
import com.subject.domain.dto.UserQueryDTO;
import com.subject.domain.dto.UserUpdateDTO;
import com.subject.domain.entity.AdminRole;
import com.subject.domain.entity.AdminUserRole;
import com.subject.domain.entity.User;
import com.subject.service.AdminRoleService;
import com.subject.service.AdminUserRoleService;
import com.subject.service.UserService;
import com.subject.domain.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AdminUserRoleService adminUserRoleService;

    @Autowired
    private AdminRoleService adminRoleService;
//    查询用户列表
    @PostMapping("user/{size}/{page}")
    public Result listUsers(@PathVariable("size") int size, @PathVariable("page") int page,
                            @RequestBody(required = false) UserQueryDTO user) {
        QueryWrapper<UserQueryDTO> wrapper = new QueryWrapper<>();
        wrapper.like(user.getUsername() != null && !"".equals(user.getUsername()), "user.username", user.getUsername());
        wrapper.like(user.getPhone() != null && !"".equals(user.getPhone()), "user.phone", user.getPhone());
        wrapper.like(user.getEmail() != null && !"".equals(user.getEmail()), "user.email", user.getEmail());
        Page<User> pageBean = new Page<>(page, size);
        Page<UserVo> userPage = userService.selectPageUserList(pageBean, wrapper);
        return Result.ok().data("list", userPage.getRecords()).data("total", userPage.getTotal());
    }

    @PutMapping("user/edit")
    public Result editUser(@RequestBody UserUpdateDTO requestUser) {
        Boolean editUser = userService.editUser(requestUser);
        if (editUser) {
            return Result.ok().message("修改用户信息成功");
        } else {
            return Result.error().message("修改用户信息失败");
        }
    }

    @PutMapping("user/update")
    public Result updateUser(@RequestBody User requestUser) {
        Boolean updateUser = userService.updateById(requestUser);
        if (updateUser) {
            return Result.ok().message("修改用户信息成功");
        } else {
            return Result.error().message("修改用户信息失败");
        }
    }

//    根据账户名查出用户的详细信息
    @PostMapping("user/select")
    public Result selectUser(@RequestBody User requestUser) {
//        根据账号名查出当前登录用户的信息，且该用户是可用的
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::isEnabled, 1).eq(User::getUsername, requestUser.getUsername());
        User user = userService.getOne(wrapper);
        return Result.ok().data("user", user);
    }

    @PutMapping("user/status")
    public Result updateUserStatus(@RequestBody User requestUser) {
        userService.updateById(requestUser);
        return Result.ok().message("用户状态更新成功");
    }

    @PutMapping("user/password")
    public Result resetPassword(@RequestBody User requestUser) {
        int reset = userService.resetPassword(requestUser);
        if (reset > 0) {
            return Result.ok().message("重置密码成功");
        } else {
            return Result.error().message("重置密码失败");
        }
    }

    @PutMapping("user/resetPassword")
    public Result resetUserPassword(@RequestBody UserUpdateDTO requestUser) {
        int reset = userService.resetUserPassword(requestUser);
        switch (reset) {
            case -1:
                return Result.error().message("密码错误");
            case 0:
                return Result.error().message("更新密码失败");
            case 1:
                return Result.ok().message("更新密码成功");
            case 2:
                return Result.error().message("该用户已被禁用或尚未通过审核,请联系管理员");
        }
        return Result.error().message("未知错误");
    }

    @DeleteMapping("user/{id}")
    @Transactional
    public Result updateUserStatus(@PathVariable("id") Integer id) {
        User user = userService.getById(id);
        boolean deleteFlag = userService.removeById(id);
        if (deleteFlag) {
            return Result.ok().message("用户:" + user.getUsername() + "删除成功");
        } else {
            return Result.error().message("用户:" + user.getUsername() + "删除失败");
        }
    }

    @PostMapping("user/batchDelete")
    public Result batchDeleteUser(@RequestBody List<Integer> ids) {
        int deleteUser = userService.batchDeleteUser(ids);
        if (deleteUser > 0) {
            return Result.ok().message("用户信息删除成功");
        } else {
            return Result.error().message("用户信息删除失败");
        }
    }

    // 根据用户ID(uid)查询角色ID(rid)
    @GetMapping("user/getRidByUid")
    public Result getRidByUid(@RequestParam("uid") Integer uid) {
        // 参数校验
        if (uid == null || uid <= 0) {
            return Result.error().message("无效的用户ID");
        }
        // 查询用户角色关联表
        LambdaQueryWrapper<AdminUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserRole::getUid, uid);
        AdminUserRole adminUserRole = adminUserRoleService.getOne(wrapper);

        if (adminUserRole == null || adminUserRole.getRid() == null) {
            return Result.error().message("该用户未分配角色");
        }
        // 返回角色ID
        return Result.ok().data("rid", adminUserRole.getRid());
    }

    // 根据角色ID(rid)查询角色名称(name)
    @GetMapping("user/getNameByRid")
    public Result getNameByRid(@RequestParam("rid") Integer rid) {
        // 参数校验
        if (rid == null || rid <= 0) {
            return Result.error().message("无效的角色ID");
        }
        // 查询角色表
        AdminRole role = adminRoleService.getById(rid);
        if (role == null || role.getName() == null) {
            return Result.error().message("角色信息不存在");
        }
        // 返回角色名称（如teacher/student/admin）
        return Result.ok().data("name", role.getName());
    }
}
