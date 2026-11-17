package com.subject.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.subject.domain.entity.ProjectView;
import org.apache.ibatis.annotations.Param;

public interface ProjectViewMapper extends BaseMapper<ProjectView> {
     IPage<ProjectView>  selectProjectViewList(IPage<ProjectView> page, @Param(Constants.WRAPPER) Wrapper<ProjectView>  wrapper) ;



}

