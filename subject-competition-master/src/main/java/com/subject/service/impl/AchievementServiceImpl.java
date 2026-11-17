package com.subject.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.mapper.AchievementMapper;
import com.subject.domain.entity.Achievement;
import com.subject.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AchievementServiceImpl extends ServiceImpl<AchievementMapper, Achievement> implements AchievementService {

    @Autowired
    private AchievementMapper achievementMapper;

    @Override
    public boolean addAchievement(Achievement achievement) {
        if (achievement == null || achievement.getTitle() == null) {
            return false;
        }
        int rows = achievementMapper.insertAchievement(achievement);
        return rows > 0;
    }

    @Override
    public boolean updateAchievement(Achievement achievement) {
        if (achievement == null || achievement.getAchievementId() == null) {
            return false;
        }
        int rows = achievementMapper.updateAchievementById(achievement);
        return rows > 0;
    }

    @Override
    public boolean removeAchievement(Integer achievementId) {
        if (achievementId == null || achievementId <= 0) {
            return false;
        }
        int rows = achievementMapper.deleteAchievementById(achievementId);
        return rows > 0;
    }
}
