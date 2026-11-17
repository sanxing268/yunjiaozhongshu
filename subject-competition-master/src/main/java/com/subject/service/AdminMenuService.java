package com.subject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.entity.AdminMenu;
import com.subject.domain.vo.AdminMenuVo;

import java.util.List;


public interface AdminMenuService extends IService<AdminMenu> {
     List<AdminMenuVo>  getMenusByUserId();


     List<AdminMenuVo> getMenusList();



}

