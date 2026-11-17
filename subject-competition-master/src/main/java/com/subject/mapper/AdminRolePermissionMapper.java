package com.subject.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.subject.domain.entity.AdminRolePermission;

import java.util.List;

public interface AdminRolePermissionMapper extends BaseMapper<AdminRolePermission> {
    Integer UpdateRolePermission(List<AdminRolePermission> adminRolePermissions);

}
