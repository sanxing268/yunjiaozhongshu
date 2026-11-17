package com.subject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.entity.Achievement;

public interface AchievementService extends IService<Achievement> {
    // 新增：竞赛成果
    boolean addAchievement(Achievement achievement);

    // 修改：竞赛成果
    boolean updateAchievement(Achievement achievement);

    // 删除：竞赛成果（根据 id）
    boolean removeAchievement(Integer achievementId);
}
