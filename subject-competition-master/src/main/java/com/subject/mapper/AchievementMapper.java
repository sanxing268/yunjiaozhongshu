package com.subject.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.subject.domain.entity.Achievement;
import org.apache.ibatis.annotations.Param;

public interface AchievementMapper extends BaseMapper<Achievement> {
    // 新增：竞赛成果
    int insertAchievement(Achievement achievement);

    // 修改：竞赛成果（根据 id 更新）
    int updateAchievementById(Achievement achievement);

    // 删除：竞赛成果（根据 id 软删除）
    int deleteAchievementById(@Param("achievementId") Integer achievementId);
}
