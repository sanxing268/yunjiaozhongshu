package com.subject.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.subject.domain.entity.AdminMenu;
import com.subject.domain.vo.AdminMenuVo;

import java.util.List;

public interface AdminMenuMapper extends BaseMapper<AdminMenu> {

     List<AdminMenuVo> getMenusByUserId(Integer uid);

    List<AdminMenuVo> getMenusList();
}
