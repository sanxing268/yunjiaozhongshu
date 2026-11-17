package com.subject.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.subject.commons.result.Result;
import com.subject.domain.dto.UserInsertDTO;
import com.subject.domain.entity.User;
import com.subject.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
public class LoginController {
    @Autowired
    private UserService userService;


    @PostMapping(value = "/api/login")
    public Result login(@RequestBody User requestUser) {
        String username = requestUser.getUsername();
        // 对 html 标签进行转义，防止 XSS 攻击
        username = HtmlUtils.htmlEscape(username);
        Subject subject = SecurityUtils.getSubject();

        //  subject.getSession().setTimeout(10000);
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, requestUser.getPassword());
        //记住我
        usernamePasswordToken.setRememberMe(true);
        try {
            subject.login(usernamePasswordToken);
            //角色状态0:封禁 1:可用
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::isEnabled, 1).eq(User::getUsername, username);
            User user = userService.getOne(wrapper);
            if (user == null) {
                return Result.error().message("该用户已被禁用或尚未通过审核,请联系管理员");
            }
//           返回的是登录用户的账户名
            return Result.ok().data("username", username);
        } catch (IncorrectCredentialsException e) {
            return Result.error().message("密码错误");
        } catch (UnknownAccountException e) {
            return Result.error().message("账号不存在");
        }
    }

    @GetMapping("/api/authentication")
    public String authentication() {
        return "身份认证成功";
    }

    /**
     * 获取当前登录用户（用于前端校验是否登录）
     * @return 登录用户信息/未登录返回错误
     */
    @GetMapping("/api/currentUser")
    public Result getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        // 判断是否登录（Shiro 会话是否有效）
        if (subject.isAuthenticated()) {
            String username = (String) subject.getPrincipal(); // 获取登录用户名
            return Result.ok().data("username", username);
        } else {
            return Result.error().code(401).message("未登录");
        }
    }

    @PostMapping("/api/register")
    public Result register(@RequestBody UserInsertDTO user) {

        int status = userService.register(user);
        switch (status) {
            case 0:
                return Result.error().message("用户名和密码不能为空");
            case 1:
                return Result.ok().message("注册成功");
            case 2:
                return Result.error().message("用户已存在");
        }
        return Result.error().message("未知错误");
    }


    @GetMapping("/api/logout")
    public Result logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return Result.ok().message("成功登出");
    }
}

