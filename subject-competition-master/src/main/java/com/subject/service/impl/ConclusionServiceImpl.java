package com.subject.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.domain.dto.ConclusionUpdateDTO;
import com.subject.domain.dto.ProjectQueryDTO;
import com.subject.domain.entity.Conclusion;
import com.subject.domain.entity.Team;
import com.subject.domain.vo.ConclusionVo;
import com.subject.mapper.ConclusionMapper;
import com.subject.mapper.TeamMapper;
import com.subject.service.ConclusionService;

import com.subject.util.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class ConclusionServiceImpl extends ServiceImpl<ConclusionMapper, Conclusion> implements ConclusionService {
    @Resource
    private ConclusionMapper conclusionMapper;
    @Resource
    private TeamMapper teamMapper;

    @Override
    public IPage<ConclusionVo> selectPageConclusionList(IPage<ConclusionVo> page, Wrapper<ProjectQueryDTO> wrapper) {
        return conclusionMapper.selectPageConclusionList(page, wrapper);

    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public boolean updateConclusion(ConclusionUpdateDTO updateDTO) {
        Conclusion conclusion = BeanUtils.transformFrom(updateDTO, Conclusion.class);
        LambdaUpdateWrapper<Conclusion> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Conclusion::getConclusionId, conclusion.getConclusionId()).set(Conclusion::getOpinion, conclusion.getOpinion())
                .set(Conclusion::getPrizeId, conclusion.getPrizeId());
        int update = conclusionMapper.update(null, updateWrapper);
        int updateTeam = teamMapper.update(null, new LambdaUpdateWrapper<Team>().eq(Team::getTeamNo, updateDTO.getTeamNo()).set(Team::getConStatus, updateDTO.getConStatus()));
        if (update > 0 && updateTeam > 0) {
            return true;
        } else {
            return false;
        }
    }
}
