package com.subject.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.domain.entity.AdminMenu;
import com.subject.domain.entity.User;
import com.subject.domain.vo.AdminMenuVo;
import com.subject.mapper.AdminMenuMapper;
import com.subject.mapper.UserMapper;
import com.subject.service.AdminMenuService;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdminMenuServiceImpl extends ServiceImpl<AdminMenuMapper, AdminMenu> implements AdminMenuService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private AdminMenuMapper adminMenuMapper;

    @Override
    public List<AdminMenuVo> getMenusByUserId() {
//        通过上下文获取username
        String username = SecurityUtils.getSubject().getPrincipal().toString();
//        创建查询语句
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getUsername, username);
//        查询用户
        User user = userMapper.selectOne(wrapper);
        // 获得当前用户对应的所有角色对应的菜单列表
        List<AdminMenuVo> menuVoList = adminMenuMapper.getMenusByUserId(Math.toIntExact(user.getId()));
//        输出查询出的侧边栏数据
//        System.out.println(menuVoList);
        return menuVoList;
    }

    @Override
    public List<AdminMenuVo> getMenusList() {
        return adminMenuMapper.getMenusList();
    }

}

