package com.subject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.subject.domain.dto.RoleQueryDTO;
import com.subject.domain.dto.RoleUpdateDTO;
import com.subject.domain.entity.AdminPermission;
import com.subject.domain.entity.AdminRole;
import com.subject.commons.result.Result;
import com.subject.domain.entity.AdminRolePermission;
import com.subject.domain.vo.AdminRoleVo;
import com.subject.service.AdminPermissionService;
import com.subject.service.AdminRoleMenuService;
import com.subject.service.AdminRolePermissionService;
import com.subject.service.AdminRoleService;
import com.subject.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class RoleController {
    @Autowired
    private AdminRoleMenuService adminRoleMenuService;
    @Autowired
    private AdminRoleService adminRoleService;
    @Autowired
    private AdminPermissionService adminPermissionService;
    @Autowired
    private AdminRolePermissionService adminRolePermissionService;

//    查询角色列表
    @GetMapping("/rolelList")
    public Result listRoleList() {
        List<AdminRole> roleList = adminRoleService.list();
        return Result.ok().data("list", roleList);
    }

    @PostMapping("role/{size}/{page}")
    public Result listRoles(@PathVariable("size") int size, @PathVariable("page") int page,
                            @RequestBody(required = false) RoleQueryDTO adminRole) {
        LambdaQueryWrapper<RoleQueryDTO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(adminRole.getName() != null && !"".equals(adminRole.getName()), RoleQueryDTO::getName, adminRole.getName())
                .like(adminRole.getNameZh() != null && !"".equals(adminRole.getNameZh()), RoleQueryDTO::getNameZh, adminRole.getNameZh());
        Page<AdminRoleVo> pageBean = new Page<>(page, size);
        Page<AdminRoleVo> adminRoles = adminRoleService.listWithPermsAndMenus(pageBean, wrapper);
        return Result.ok().data("list", adminRoles.getRecords()).data("total", adminRoles.getTotal());
    }

    @PutMapping("role/status")
    public Result updateRoleStatus(@RequestBody AdminRole requestRole) {
        adminRoleService.updateById(requestRole);
        String message = "用户" + requestRole.getNameZh() + "状态更新成功";
        return Result.ok().message(message);
    }

    @PostMapping("role")
    public Result addRole(@RequestBody AdminRole requestRole) {
        boolean save = adminRoleService.save(requestRole);
        AdminRolePermission rolePermission = new AdminRolePermission();
        rolePermission.setRid(Math.toIntExact(requestRole.getId()));
        rolePermission.setPid(7);//个人信息
        adminRolePermissionService.save(rolePermission);
        if (save) {
            return Result.ok().message("添加角色成功");
        } else {
            return Result.error().message("添加角色失败");
        }
    }

    @GetMapping("role/perm")
    public Result listPerms() {
        return Result.ok().data("list", adminPermissionService.list());
    }

    @PutMapping("role")
    public Result editRole(@RequestBody RoleUpdateDTO requestRole) {
//        id字段的数据类型需要一致
//        System.out.println("接收的角色ID：" + requestRole.getId());
        AdminRole adminRole = BeanUtils.transformFrom(requestRole, AdminRole.class);
//        System.out.println("转换后的角色ID：" + adminRole.getId());
        boolean updateById = adminRoleService.updateById(adminRole);
        Integer savePermChanges = adminRolePermissionService.savePermChanges(Math.toIntExact(requestRole.getId()), requestRole.getPermsId());
        Integer update = adminRoleMenuService.updateRoleMenu(Math.toIntExact(adminRole.getId()), requestRole.getMenusIds());
        if (updateById && savePermChanges >= 0 && update >= 0) {
            return Result.ok().message("修改角色信息成功");
        } else {
            return Result.error().message("修改角色信息失败");
        }
    }

    @PostMapping("role/batchDelete")
    public Result batchDeleteRole(@RequestBody List<Integer> ids) {
        int deleteUser = adminRoleService.batchDeleteRole(ids);
        if (deleteUser > 0) {
            return Result.ok().message("角色信息删除成功");
        } else {
            return Result.error().message("角色信息删除失败");
        }
    }

}
