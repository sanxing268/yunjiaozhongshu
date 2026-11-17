package com.subject.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.domain.dto.RoleQueryDTO;
import com.subject.domain.entity.AdminRole;
import com.subject.domain.vo.AdminRoleVo;
import com.subject.mapper.AdminRoleMapper;
import com.subject.service.AdminRoleService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {

    @Resource
    private AdminRoleMapper adminRoleMapper;

    @Override
    public Page<AdminRoleVo> listWithPermsAndMenus(@Param("pageBean") Page<AdminRoleVo> pageBean, Wrapper<RoleQueryDTO> wrapper) {
        return adminRoleMapper.selectPageRoleList(pageBean, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteRole(List<Integer> ids) {
        if (ids != null && ids.size() > 0) {
            int batchDelete = adminRoleMapper.deleteBatchIds(ids);
            return batchDelete;
        }
        return -1;
    }
}

