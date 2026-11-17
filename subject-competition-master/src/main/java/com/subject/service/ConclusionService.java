package com.subject.service;



import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.dto.ConclusionUpdateDTO;
import com.subject.domain.dto.ProjectQueryDTO;
import com.subject.domain.entity.Conclusion;
import com.subject.domain.vo.ConclusionVo;
import org.apache.ibatis.annotations.Param;

public interface ConclusionService extends IService<Conclusion> {
    IPage<ConclusionVo> selectPageConclusionList(IPage<ConclusionVo> page, @Param(Constants.WRAPPER) Wrapper<ProjectQueryDTO> wrapper) ;

    boolean updateConclusion(ConclusionUpdateDTO conclusion);
}
