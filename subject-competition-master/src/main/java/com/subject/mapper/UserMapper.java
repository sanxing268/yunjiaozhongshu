package com.subject.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.subject.domain.dto.UserQueryDTO;
import com.subject.domain.entity.User;
import com.subject.domain.vo.UserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    Page<UserVo> selectPageUserList(Page<User> pageBean, @Param(Constants.WRAPPER) Wrapper<UserQueryDTO> queryWrapper);

    // 按角色英文名称查询用户
    List<User> selectUserByRoleName(@Param("roleName") String roleName);

    int saveStudent(@Param("user") User user);

    int updateStudentById(@Param("user") User user);

    int removeStudentById(@Param("id") Long id);

    int insertTeacher(@Param("user") User user); // 新增教师

    int updateTeacherById(@Param("user") User user); // 修改教师

    int deleteTeacherById(@Param("id") Long id); // 删除教师

    // 查询所有教师的username列表（用于生成下一个账号）
    List<String> selectTeacherUsernames();
}
