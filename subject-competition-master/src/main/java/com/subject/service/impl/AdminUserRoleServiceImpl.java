package com.subject.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.mapper.AdminUserRoleMapper;
import com.subject.domain.entity.AdminUserRole;
import com.subject.service.AdminUserRoleService;
import org.springframework.stereotype.Service;

@Service
public class AdminUserRoleServiceImpl extends ServiceImpl<AdminUserRoleMapper, AdminUserRole> implements AdminUserRoleService {

}