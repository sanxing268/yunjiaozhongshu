package com.subject;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.subject.controller.*;
import com.subject.domain.dto.ProjectQueryDTO;

import com.subject.domain.vo.ConclusionVo;
import com.subject.mapper.AdminMenuMapper;
import com.subject.mapper.UserMapper;
import com.subject.service.*;
import com.subject.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class SubjectCompetitionMasterApplicationTests {
    @Autowired
    private UserService userService;
    @Autowired
    private AdminUserRoleService adminUserRoleService;
    @Autowired
    private AdminRoleService adminRoleService;
    @Autowired
    private AdminRoleMenuService adminRoleMenuService;
    @Autowired
    private AdminMenuService adminMenuService;

    @Autowired
    private RoleController roleController;
    @Autowired
    private ProjectController projectController;
    @Autowired
    private ProjectServiceImpl projectService;
    @Autowired
    private MenuController menuController;
    @Autowired
    private UserController userController;
    @Autowired
    private AdminPermissionService adminPermissionService;

    @Autowired
    private ConclusionController conclusionController;
    @Autowired
    private  ConclusionService conclusionService;

    @Autowired
    private CategoryController categoryController;
    @Autowired
    private AdminMenuMapper adminMenuMapper;
    @Autowired
    private UserMapper userMapper;
    @Test
    void contextLoads() {
        Page<ConclusionVo> pageBean = new Page<>(1,10);
        QueryWrapper<ProjectQueryDTO> wrapper = new QueryWrapper<>();
        wrapper.eq("team.status", 3);
        IPage<ConclusionVo> conclusionVoIPage = conclusionService.selectPageConclusionList(pageBean, wrapper);
        for (ConclusionVo record : conclusionVoIPage.getRecords()) {

            System.out.println(record);
        }
        System.out.println(conclusionVoIPage.getTotal());
    }
}
