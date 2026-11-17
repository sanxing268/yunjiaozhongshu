package com.subject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.entity.AdminPermission;
import com.subject.domain.entity.AdminRolePermission;

import java.util.List;

public interface AdminRolePermissionService extends IService<AdminRolePermission> {
    Integer savePermChanges(int id, List<Integer> permsId);
}
