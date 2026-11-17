package com.subject.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.subject.domain.dto.UserInsertDTO;
import com.subject.domain.dto.UserQueryDTO;
import com.subject.domain.dto.UserUpdateDTO;
import com.subject.domain.entity.User;
import com.subject.domain.vo.UserVo;

import java.util.List;

public interface UserService extends IService<User> {

    Page<UserVo> selectPageUserList(Page<User> pageBean, Wrapper<UserQueryDTO> wrapper);

    Boolean editUser(UserUpdateDTO requestUser);

    int register(UserInsertDTO user);

    int batchDeleteUser(List<Integer> ids);

    int resetPassword(User requestUser);

    int resetUserPassword(UserUpdateDTO requestUser);
    // 查询所有学生用户（角色=STUDENT）
    List<User> getStudentList();

    boolean saveStudent(User user);

    boolean updateStudentById(User user);

    boolean removeStudentById(Long id);

    // 查询所有教授用户（角色=PROFESSOR）
    List<User> getTeacherList();
    boolean saveTeacher(User user); // 新增教师
    boolean updateTeacher(User user); // 修改教师
    boolean removeTeacher(Long id); // 删除教师
}
