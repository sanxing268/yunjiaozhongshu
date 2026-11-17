package com.subject.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.domain.dto.ProjectViewUpdateDTO;
import com.subject.mapper.ProjectViewMapper;
import com.subject.domain.entity.ProjectView;
import com.subject.service.ProjectViewService;
import com.subject.util.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProjectViewServiceImpl extends ServiceImpl<ProjectViewMapper, ProjectView> implements ProjectViewService {
    @Resource
    ProjectViewMapper projectViewMapper;

    @Override
    public IPage<ProjectView> selectProjectViewList(IPage<ProjectView> pagebean, Wrapper<ProjectView> wrapper) {

        IPage<ProjectView> projectViewIPage = projectViewMapper.selectProjectViewList(pagebean, wrapper);
        return projectViewIPage;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateProjectView(ProjectViewUpdateDTO view) {
        ProjectView projectView = BeanUtils.transformFrom(view, ProjectView.class);
        projectView.setCid(view.getCategory().getId());
        projectView.setCover("http://localhost:8443/image/"+projectView.getCover());
        int i = projectViewMapper.updateById(projectView);
        return i;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int batchDeleteProjectView(List<Integer> ids) {
        if (ids != null && ids.size() > 0) {
            int delete = projectViewMapper.delete(new LambdaQueryWrapper<ProjectView>().in(ProjectView::getId, ids));
            if (delete >= 0) {
                int batchDelete = projectViewMapper.deleteBatchIds(ids);
                return batchDelete;
            }
        }
        return -1;
    }
}
