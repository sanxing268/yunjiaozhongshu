package com.subject.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.dto.RoleQueryDTO;
import com.subject.domain.entity.AdminRole;
import com.subject.domain.vo.AdminRoleVo;

import java.util.List;


public interface AdminRoleService extends IService<AdminRole> {

     Page<AdminRoleVo> listWithPermsAndMenus(Page<AdminRoleVo> pageBean, Wrapper<RoleQueryDTO> wrapper);


    int batchDeleteRole(List<Integer> ids);
}
