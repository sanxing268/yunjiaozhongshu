package com.subject.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.dto.ProjectViewUpdateDTO;
import com.subject.domain.entity.ProjectView;

import java.util.List;

public interface ProjectViewService extends IService<ProjectView> {
    IPage<ProjectView> selectProjectViewList(IPage<ProjectView> pagebean, Wrapper<ProjectView> wrapper );

    int updateProjectView(ProjectViewUpdateDTO view) ;

    int batchDeleteProjectView(List<Integer> ids);
}
