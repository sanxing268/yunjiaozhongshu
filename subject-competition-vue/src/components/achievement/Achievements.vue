<!-- 竞赛成果列表页面 -->
<template>
  <div class="achievements-page">
    <div class="achievements-area">
      <el-card class="achievements-card">
        <div class="page-header">
          <h2>竞赛成果</h2>
        </div>
        <div v-if="achievements.length === 0" class="empty-tip">
          暂无竞赛成果数据
        </div>
        <div v-for="(item, index) in achievements" :key="item.achievementId" class="achievement-item" @click="goDetail(item.achievementId)">
          <div class="item-left">
            <span class="level-tag" :class="levelClass(item.level)">{{ item.level }}</span>
          </div>
          <div class="item-center">
            <div class="item-title">{{ item.title }}</div>
            <div class="item-meta">
              <span class="item-team">{{ item.team }}</span>
              <span v-if="item.awardDate" class="item-date">{{ item.awardDate }}</span>
            </div>
          </div>
          <div class="item-right">
            <i class="el-icon-arrow-right"></i>
          </div>
          <el-divider v-if="index < achievements.length - 1" class="item-divider"></el-divider>
        </div>
      </el-card>
    </div>
    <!-- 页脚 -->
    <div style="margin-top: auto;">
      <about id="about"></about>
    </div>
    <!-- AI浮动球 -->
    <floating-ball></floating-ball>
  </div>
</template>

<script>
import About from '../common/About.vue'
import FloatingBall from '../ai/FloatingBall.vue'

export default {
  name: 'Achievements',
  components: { About, FloatingBall },
  data() {
    return {
      achievements: []
    }
  },
  mounted() {
    this.loadAchievements()
  },
  methods: {
    loadAchievements() {
      var _this = this
      this.$axios.get('/achievement/list').then(resp => {
        if (resp && resp.data.code === 200) {
          _this.achievements = resp.data.data.list || []
        }
      }).catch(error => {
        console.log('加载竞赛成果失败:', error)
      })
    },
    goDetail(id) {
      this.$router.push({ path: '/achievement/detail', query: { id: id } })
    },
    levelClass(level) {
      if (!level) return ''
      if (level.indexOf('国家') !== -1) return 'level-national'
      if (level.indexOf('省') !== -1) return 'level-province'
      if (level.indexOf('市') !== -1) return 'level-city'
      return 'level-school'
    }
  }
}
</script>

<style scoped>
.achievements-page {
  min-height: calc(100vh - 200px);
}

.achievements-area {
  width: 990px;
  margin: 40px auto 20px;
  padding: 0 20px;
  max-width: 100%;
}

.achievements-card {
  text-align: left;
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e8e8e8;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #333;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 15px;
}

.achievement-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}

.achievement-item:last-child {
  border-bottom: none;
}

.achievement-item:hover {
  background-color: #f8f9fa;
  padding-left: 8px;
  border-radius: 6px;
}

.item-left {
  flex-shrink: 0;
  margin-right: 16px;
}

.level-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  white-space: nowrap;
}

.level-national {
  background-color: #f5222d;
}

.level-province {
  background-color: #fa8c16;
}

.level-city {
  background-color: #1890ff;
}

.level-school {
  background-color: #52c41a;
}

.item-center {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  line-height: 1.4;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: #666;
}

.item-team {
  color: #666;
}

.item-date {
  color: #999;
}

.item-right {
  flex-shrink: 0;
  margin-left: 12px;
  color: #ccc;
  font-size: 16px;
}

.item-divider {
  display: none;
}

/* 响应式适配 */
@media (max-width: 1024px) {
  .achievements-area {
    width: 95%;
  }
}

@media (max-width: 768px) {
  .achievements-area {
    width: 100%;
    padding: 0 10px;
    margin-top: 20px;
  }

  .item-title {
    font-size: 15px;
  }

  .item-meta {
    flex-direction: column;
    gap: 4px;
    align-items: flex-start;
  }
}
</style>
