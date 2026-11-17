package com.subject.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.entity.AdminPermission;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.Set;

@Service
public interface AdminPermissionService extends IService<AdminPermission> {
     Set<String> listPermissionURLsByUser(String username);
     boolean needFilter(String requestAPI);
}
