package com.subject.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.subject.domain.entity.AdminUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminUserRoleMapper extends BaseMapper<AdminUserRole> {
    Integer UpdateUserRole(List<AdminUserRole> adminUserRoleList);
    /**
     * 插入用户-角色关联数据
     * @param adminUserRole 关联数据实体
     * @return 受影响行数
     */
    int insertAdminUserRole(@Param("adminUserRole")  AdminUserRole adminUserRole);

    /**
     * 删除用户-角色关联数据
     * @param uid 用户id
     * @return 受影响行数
     */
    int deleteByuid(@Param("uid") Long uid);
}
