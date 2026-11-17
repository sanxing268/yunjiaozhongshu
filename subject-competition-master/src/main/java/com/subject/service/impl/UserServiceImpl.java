package com.subject.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.domain.dto.UserInsertDTO;
import com.subject.domain.dto.UserQueryDTO;
import com.subject.domain.dto.UserUpdateDTO;
import com.subject.domain.entity.AdminRole;
import com.subject.domain.entity.AdminUserRole;
import com.subject.domain.entity.User;
import com.subject.domain.vo.UserVo;
import com.subject.mapper.AdminUserRoleMapper;
import com.subject.mapper.UserMapper;
import com.subject.service.AdminRoleService;
import com.subject.service.UserService;
import com.subject.util.BeanUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private AdminUserRoleMapper adminUserRoleMapper;
    @Resource
    private AdminRoleService adminRoleService;
    @Autowired
    private AdminUserRoleMapper AdminUserRoleMapper;


    @Override

    public Page<UserVo> selectPageUserList(Page<User> pageBean, Wrapper<UserQueryDTO> wrapper) {
        return userMapper.selectPageUserList(pageBean, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean editUser(UserUpdateDTO requestUser) {
        User user = BeanUtils.transformFrom(requestUser, User.class);
        user.setId(Long.valueOf(requestUser.getId()));
        System.out.println(user);
        int update = userMapper.updateById(user);
        //更新user role中间表数据
        List<AdminUserRole> urs = new ArrayList<>();
        // 获得更新的用户的角色信息id
        List<Long> roleIds = requestUser.getRoleIds();
        //获取数据库表中关联的原数据
        LambdaQueryWrapper<AdminUserRole> wrapper = new LambdaQueryWrapper<AdminUserRole>().eq(AdminUserRole::getUid, user.getId());
        List<Long> RIdList = adminUserRoleMapper.selectList(wrapper).stream().map(AdminUserRole::getRid).collect(Collectors.toList());
        RIdList.forEach(id -> {
            if (!roleIds.contains(id)) {
                LambdaQueryWrapper<AdminUserRole> queryWrapper = new LambdaQueryWrapper<AdminUserRole>()
                        .eq(AdminUserRole::getUid, user.getId()).eq(AdminUserRole::getRid, id);
                adminUserRoleMapper.delete(queryWrapper);
            } else {
                roleIds.remove((Object) id);
            }
        });
        if (update > 0) {
            if (roleIds != null && roleIds.size() > 0) {
                for (Long roleId : roleIds) {
                    AdminUserRole userRole = new AdminUserRole();
                    userRole.setUid(user.getId());
                    userRole.setRid(roleId);
                    urs.add(userRole);
                }
                Integer integer = adminUserRoleMapper.UpdateUserRole(urs);
                if (integer > 0) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int register(UserInsertDTO requestUser) {
        User user = BeanUtils.transformFrom(requestUser, User.class);
        String username = user.getUsername();
        String password = user.getPassword();
        username = HtmlUtils.htmlEscape(username);
        user.setUsername(username);
        if (username.equals("") || password.equals("")) {
            return 0;
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getUsername, username);
        User isExistUser = userMapper.selectOne(wrapper);
        if (isExistUser != null) {
            return 2;
        }
        // 生成盐,默认长度 16 位
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        // 设置 hash 算法迭代次数
        int times = 2;
        // 得到 hash 后的密码
        String encodedPassword = new SimpleHash("md5", password, salt, times).toString();
        // 存储用户信息，包括 salt 与 hash 后的密码
        user.setSalt(salt);
        user.setPassword(encodedPassword);
        user.setCreateTime(new Date());
        userMapper.insert(user);
        //用户的角色名 student teacher
        String rname = requestUser.getRname();
        AdminRole adminRole = adminRoleService.getOne(new LambdaQueryWrapper<AdminRole>().eq(AdminRole::getName, rname));
        //创建用户角色表数据
        AdminUserRole adminUserRole = new AdminUserRole();
        adminUserRole.setRid(adminRole.getId());
        adminUserRole.setUid(user.getId());
        adminUserRoleMapper.insert(adminUserRole);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteUser(List<Integer> ids) {
        if (ids != null && ids.size() > 0) {
            int delete = adminUserRoleMapper.delete(new LambdaQueryWrapper<AdminUserRole>().in(AdminUserRole::getUid, ids));
            if (delete >= 0) {
                int batchDelete = userMapper.deleteBatchIds(ids);
                return batchDelete;
            }
        }
        return -1;
    }

    @Override
    public int resetPassword(User requestUser) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", requestUser.getUsername()));
        // 生成盐,默认长度 16 位
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        // 设置 hash 算法迭代次数
        int times = 2;
        // 得到 hash 后的密码
        String password = requestUser.getPassword();
        String encodedPassword = new SimpleHash("md5", password, salt, times).toString();
        // 存储用户信息，包括 salt 与 hash 后的密码
        user.setSalt(salt);
        user.setPassword(encodedPassword);
        int update = userMapper.updateById(user);
        return update;
    }

    @Override
    public int resetUserPassword(UserUpdateDTO requestUser) {
        Subject subject = SecurityUtils.getSubject();
        User user = BeanUtils.transformFrom(requestUser, User.class);
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        try {
            subject.login(usernamePasswordToken);
            //角色状态0:封禁 1:可用
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::isEnabled, 1).eq(User::getUsername, user.getUsername());
            User selectOne = userMapper.selectOne(wrapper);
            if (selectOne == null) {
                return 2;
            } else {
                // 生成盐,默认长度 16 位
                String salt = new SecureRandomNumberGenerator().nextBytes().toString();
                // 设置 hash 算法迭代次数
                int times = 2;
                // 得到 hash 后的密码
                String password = requestUser.getNewpwd();
                String encodedPassword = new SimpleHash("md5", password, salt, times).toString();
                User updateUser = new User();
                // 存储用户信息，包括 salt 与 hash 后的密码
                updateUser.setId(selectOne.getId());
                updateUser.setSalt(salt);
                updateUser.setPassword(encodedPassword);
                updateUser.setUsername(selectOne.getUsername());
                updateUser.setEnabled(selectOne.isEnabled());
                int update = userMapper.updateById(updateUser);
                if (update > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } catch (IncorrectCredentialsException e) {
            return -1;
        }
    }

    // 查询所有学生用户
    @Override
    public List<User> getStudentList() {
        return this.baseMapper.selectUserByRoleName("student");
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 异常时回滚，保证原子性
    public boolean saveStudent(User user) {
        // 生成盐,默认长度 16 位
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        // 设置 hash 算法迭代次数
        int times = 2;
        // 得到 hash 后的密码
        String encodedPassword = new SimpleHash("md5", user.getPassword(), salt, times).toString();
        // 存储用户信息，包括 salt 与 hash 后的密码
        user.setSalt(salt);
        user.setPassword(encodedPassword);
        int affectedRows = userMapper.saveStudent(user);
        if (affectedRows <= 0) {
            return false;
        }
        // 构建用户-角色关联数据
        AdminUserRole adminUserRole= new AdminUserRole();
        adminUserRole.setUid(user.getId()); // 获取新增用户的自增ID
        adminUserRole.setRid(4L);//学生角色的id为4
        // 插入关联表数据
        int roleAffectedRows = AdminUserRoleMapper.insertAdminUserRole(adminUserRole);
        return roleAffectedRows > 0;
    }

    @Override
    public boolean updateStudentById(User user) {
        // 生成盐,默认长度 16 位
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        // 设置 hash 算法迭代次数
        int times = 2;
        // 得到 hash 后的密码
        String encodedPassword = new SimpleHash("md5", user.getPassword(), salt, times).toString();
        // 存储用户信息，包括 salt 与 hash 后的密码
        user.setSalt(salt);
        user.setPassword(encodedPassword);
        int affectedRows = userMapper.updateStudentById(user);
        return affectedRows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeStudentById(Long id) {
        int affectedRows = userMapper.removeStudentById(id);
        return affectedRows > 0;
    }

    // 查询所有教授用户
    @Override
    public List<User> getTeacherList() {
        return this.baseMapper.selectUserByRoleName("teacher");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTeacher(User user) {
        // 生成盐,默认长度 16 位
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        // 设置 hash 算法迭代次数
        int times = 2;
        // 得到 hash 后的密码
        String encodedPassword = new SimpleHash("md5", user.getPassword(), salt, times).toString();
        // 存储用户信息，包括 salt 与 hash 后的密码
        user.setSalt(salt);
        user.setPassword(encodedPassword);
        // 新增教师到user表，回写自增ID
        int userRows = userMapper.insertTeacher(user);
        if (userRows <= 0) {
            return false;
        }
        // 插入用户-教师角色关联数据
        AdminUserRole adminUserRole= new AdminUserRole();
        adminUserRole.setUid(user.getId());
        adminUserRole.setRid(5L); // 绑定教师角色
        int roleRows = AdminUserRoleMapper.insertAdminUserRole(adminUserRole);
        return roleRows > 0;
    }

    @Override
    public boolean updateTeacher(User user) {
        // 生成盐,默认长度 16 位
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        // 设置 hash 算法迭代次数
        int times = 2;
        // 得到 hash 后的密码
        String encodedPassword = new SimpleHash("md5", user.getPassword(), salt, times).toString();
        // 存储用户信息，包括 salt 与 hash 后的密码
        user.setSalt(salt);
        user.setPassword(encodedPassword);
        int rows = userMapper.updateTeacherById(user);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeTeacher(Long uid) {
        // 删除用户-角色关联数据
        AdminUserRoleMapper.deleteByuid(uid);
        // 删除用户表数据
        int rows = userMapper.deleteTeacherById(uid);
        return rows > 0;
    }

}
