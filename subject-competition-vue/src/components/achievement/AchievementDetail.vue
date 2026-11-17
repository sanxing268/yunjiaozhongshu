<!-- 竞赛成果详情页面 -->
<template>
  <div class="achievement-detail-page">
    <div class="detail-area">
      <el-card class="detail-card">
        <div v-if="!achievement" class="loading-tip">
          加载中...
        </div>
        <div v-else>
          <div class="detail-header">
            <h1 class="detail-title">{{ achievement.title }}</h1>
            <span class="level-tag" :class="levelClass(achievement.level)">{{ achievement.level }}</span>
          </div>
          <el-divider></el-divider>
          <div class="detail-info">
            <div class="info-row">
              <span class="info-label">获奖团队：</span>
              <span class="info-value">{{ achievement.team }}</span>
            </div>
            <div v-if="achievement.awardDate" class="info-row">
              <span class="info-label">获奖日期：</span>
              <span class="info-value">{{ achievement.awardDate }}</span>
            </div>
            <div v-if="achievement.createTime" class="info-row">
              <span class="info-label">发布时间：</span>
              <span class="info-value">{{ achievement.createTime }}</span>
            </div>
          </div>
          <div v-if="achievement.description" class="detail-description">
            <h3>成果描述</h3>
            <p>{{ achievement.description }}</p>
          </div>
          <div class="detail-actions">
            <el-button type="primary" plain size="small" @click="goBack">返回列表</el-button>
          </div>
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
  name: 'AchievementDetail',
  components: { About, FloatingBall },
  data() {
    return {
      achievement: null
    }
  },
  mounted() {
    this.loadDetail()
  },
  methods: {
    loadDetail() {
      var _this = this
      var id = this.$route.query.id
      if (!id) {
        this.$message.error('参数错误')
        this.$router.push('/achievement')
        return
      }
      this.$axios.get('/achievement/list').then(resp => {
        if (resp && resp.data.code === 200) {
          var list = resp.data.data.list || []
          var found = list.find(item => item.achievementId === parseInt(id))
          if (found) {
            _this.achievement = found
          } else {
            _this.$message.error('未找到该成果')
            _this.$router.push('/achievement')
          }
        }
      }).catch(error => {
        console.log('加载成果详情失败:', error)
        _this.$message.error('加载失败')
      })
    },
    goBack() {
      this.$router.push('/achievement')
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
.achievement-detail-page {
  min-height: calc(100vh - 200px);
}

.detail-area {
  width: 990px;
  margin: 40px auto 20px;
  padding: 0 20px;
  max-width: 100%;
}

.detail-card {
  text-align: left;
}

.loading-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 15px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.detail-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
  line-height: 1.4;
}

.level-tag {
  display: inline-block;
  padding: 4px 14px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  color: white;
  white-space: nowrap;
  flex-shrink: 0;
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

.detail-info {
  margin-bottom: 24px;
}

.info-row {
  padding: 8px 0;
  font-size: 15px;
  line-height: 1.6;
}

.info-label {
  color: #666;
  font-weight: 500;
}

.info-value {
  color: #333;
}

.detail-description {
  margin-bottom: 24px;
}

.detail-description h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
}

.detail-description p {
  font-size: 15px;
  color: #555;
  line-height: 1.8;
  margin: 0;
}

.detail-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式适配 */
@media (max-width: 1024px) {
  .detail-area {
    width: 95%;
  }
}

@media (max-width: 768px) {
  .detail-area {
    width: 100%;
    padding: 0 10px;
    margin-top: 20px;
  }

  .detail-title {
    font-size: 20px;
  }

  .detail-description p {
    font-size: 14px;
  }
}
</style>
