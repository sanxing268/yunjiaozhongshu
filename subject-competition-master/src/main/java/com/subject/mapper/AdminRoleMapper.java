package com.subject.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.subject.domain.dto.RoleQueryDTO;
import com.subject.domain.entity.AdminRole;
import com.subject.domain.vo.AdminRoleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminRoleMapper extends BaseMapper<AdminRole> {
    Page<AdminRoleVo> selectPageRoleList(Page<AdminRoleVo> page, @Param(Constants.WRAPPER) Wrapper<RoleQueryDTO> queryWrapper);
}
