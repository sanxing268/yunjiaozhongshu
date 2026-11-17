package com.subject.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.subject.domain.dto.ProjectQueryDTO;
import com.subject.domain.entity.Conclusion;

import com.subject.domain.vo.ConclusionVo;
import org.apache.ibatis.annotations.Param;

public interface ConclusionMapper extends BaseMapper<Conclusion> {
    IPage<ConclusionVo> selectPageConclusionList(IPage<ConclusionVo> page, @Param(Constants.WRAPPER) Wrapper<ProjectQueryDTO> wrapper) ;
}
