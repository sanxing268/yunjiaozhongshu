package com.subject.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.domain.entity.AdminPermission;
import com.subject.domain.entity.AdminRolePermission;
import com.subject.mapper.AdminRolePermissionMapper;
import com.subject.service.AdminRolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminRolePermissionServiceImpl extends ServiceImpl<AdminRolePermissionMapper, AdminRolePermission> implements AdminRolePermissionService {
    @Resource
    AdminRolePermissionMapper adminRolePermissionMapper;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Integer savePermChanges(int rid, List<Integer> permsId) {
        LambdaQueryWrapper<AdminRolePermission> queryWrapper = new LambdaQueryWrapper<AdminRolePermission>().eq(AdminRolePermission::getRid, rid);
        List<Integer> PIdList = adminRolePermissionMapper.selectList(queryWrapper).stream().map(AdminRolePermission::getPid).distinct().collect(Collectors.toList());
        ArrayList<AdminRolePermission> rolePermissions = new ArrayList<>();
        Integer delete = 0;
        for (Integer pid : PIdList) {
            if (!permsId.contains(pid)) {
                LambdaQueryWrapper<AdminRolePermission> wrapper = new LambdaQueryWrapper<AdminRolePermission>().eq(AdminRolePermission::getRid, rid).eq(AdminRolePermission::getPid, pid);
                delete = adminRolePermissionMapper.delete(wrapper);
            } else {
                //数据库中已存在的,集合进行移除
                permsId.remove((Object) pid);
            }
        }
        for (Integer pid : permsId) {
            AdminRolePermission rp = new AdminRolePermission();
            rp.setRid(rid);
            rp.setPid(pid);
            rolePermissions.add(rp);
        }
        if (rolePermissions.size() > 0 && rolePermissions != null) {
            Integer updateRolePermission = adminRolePermissionMapper.UpdateRolePermission(rolePermissions);
            return updateRolePermission;
        } else {
            return delete;
        }
    }
}
