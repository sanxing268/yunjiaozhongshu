package com.subject.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.domain.entity.AdminRoleMenu;
import com.subject.mapper.AdminRoleMenuMapper;
import com.subject.service.AdminRoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminRoleMenuServiceImpl extends ServiceImpl<AdminRoleMenuMapper, AdminRoleMenu> implements AdminRoleMenuService {

    @Resource
    private AdminRoleMenuMapper adminRoleMenuMapper;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Integer updateRoleMenu(int rid, List<Integer> menusIds) {
        //查询当前数据库的 角色菜单项中间表数据
        List<Integer> midList = adminRoleMenuMapper.selectList(new LambdaQueryWrapper<AdminRoleMenu>().eq(AdminRoleMenu::getRid, rid))
                .stream().map(AdminRoleMenu::getMid).distinct().collect(Collectors.toList());
        ArrayList<AdminRoleMenu> adminRoleMenuList = new ArrayList<>();
        int delete = 0;
        for (Integer mid : midList) {
            if (!menusIds.contains(mid)) {
                //查询更新的mid,在原数据中,不存在,进行删除
                LambdaQueryWrapper<AdminRoleMenu> wrapper = new LambdaQueryWrapper<AdminRoleMenu>().eq(AdminRoleMenu::getRid, rid).eq(AdminRoleMenu::getMid, mid);
                delete = adminRoleMenuMapper.delete(wrapper);
            } else {
                //数据库中已存在的,集合进行移除
                menusIds.remove((Object) mid);
            }
        }
        for (Integer mid : menusIds) {
            AdminRoleMenu rm = new AdminRoleMenu();
            rm.setMid(mid);
            rm.setRid(rid);
            adminRoleMenuList.add(rm);
        }
        if (adminRoleMenuList != null && adminRoleMenuList.size() > 0) {
            Integer update = adminRoleMenuMapper.UpdateRoleMenu(adminRoleMenuList);
            return update;
        } else {
            return delete;
        }
    }

}
