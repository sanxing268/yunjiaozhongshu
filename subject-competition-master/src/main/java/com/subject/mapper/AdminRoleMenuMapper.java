package com.subject.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.subject.domain.entity.AdminRoleMenu;

import java.util.List;

public interface AdminRoleMenuMapper extends BaseMapper<AdminRoleMenu> {

    Integer UpdateRoleMenu(List<AdminRoleMenu> adminRoleMenus);
}
