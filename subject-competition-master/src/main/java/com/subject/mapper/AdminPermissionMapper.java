package com.subject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.subject.domain.entity.AdminPermission;

import java.util.Set;

public interface AdminPermissionMapper extends BaseMapper<AdminPermission> {

     Set<String> listPermissionURLsByUser(String username);

}
