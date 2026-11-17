package com.subject.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.domain.entity.AdminPermission;
import com.subject.mapper.AdminPermissionMapper;
import com.subject.service.AdminPermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service
public class AdminPermissionServiceImpl extends ServiceImpl<AdminPermissionMapper, AdminPermission> implements AdminPermissionService {

    @Resource
    private AdminPermissionMapper adminPermissionMapper;

    @Override
    public Set<String> listPermissionURLsByUser(String username) {
        Set<String> set = adminPermissionMapper.listPermissionURLsByUser(username);
        return set;
    }

    //判断用户请求接口的是否在权限列表中,在的话必须登录后才能访问
    @Override
    public boolean needFilter(String requestAPI) {
        List<AdminPermission> ps = adminPermissionMapper.selectList(null);
        for (AdminPermission p : ps) {
            if (p.getUrl().equals(requestAPI)) {
                return true;
            }
        }
        return false;
    }

}
