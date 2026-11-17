package com.subject.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.entity.AdminRoleMenu;

import java.util.List;


public interface AdminRoleMenuService extends IService<AdminRoleMenu> {

    Integer updateRoleMenu(int rid, List<Integer> menusIds);
}

