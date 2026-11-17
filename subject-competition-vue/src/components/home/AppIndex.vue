<!-- 前台页面根布局组件 引入其他组件作为内容 -->
<template>
  <div class="app-index">
    <!-- 内容容器 -->
    <div class="content-container">

      <!-- 通知公告和轮播图区（灰色背景盒子） -->
      <div class="top-section">
        <div class="top-section-inner">
          <!-- 左侧：轮播图 -->
          <div class="carousel-wrapper">
            <carousel></carousel>
          </div>

          <!-- 右侧：通知公告 -->
          <div class="notice-wrapper">
            <div class="notice-section">
              <!-- 标签页 -->
              <div class="notice-tabs">
                <div
                  class="tab-item"
                  :class="{ active: activeTab === 'notice' }"
                  @click="switchTab('notice')">
                  选知公告
                </div>
                <div
                  class="tab-item"
                  :class="{ active: activeTab === 'dynamic' }"
                  @click="switchTab('dynamic')">
                  中心动态
                </div>
              </div>
              <!-- 自定义通知公告列表 -->
              <div class="articles-wrap" v-show="activeTab === 'notice'">
                <ul class="notice-list">
                  <li v-for="(notice, index) in notices" :key="index" class="notice-item">
                    <router-link :to="{ path: 'jotter/article', query: { id: notice.id } }" class="notice-link">
                      <span class="notice-title">{{ notice.title }}</span>
                      <span class="notice-time">{{ notice.createDate }}</span>
                    </router-link>
                  </li>
                </ul>
              </div>
              <!-- 中心动态列表 -->
              <div class="articles-wrap" v-show="activeTab === 'dynamic'">
                <ul class="notice-list">
                  <li v-for="(item, index) in dynamics" :key="index" class="notice-item">
                    <router-link :to="{ path: 'jotter/article', query: { id: item.id } }" class="notice-link">
                      <span class="notice-title">{{ item.title }}</span>
                      <span class="notice-time">{{ item.createDate }}</span>
                    </router-link>
                  </li>
                  <li v-if="dynamics.length === 0" class="notice-item" style="text-align: center; color: #999;">
                    暂无动态
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 4格布局：竞赛动态、报名通道、竞赛成果、数据一览 -->
      <div class="grid-4-layout">
        <!-- 左上：竞赛动态 -->
        <div class="grid-item grid-item-lt">
          <div class="card-block competition-section">
            <div class="section-header">
              <h3>
                <span class="section-title">竞赛动态</span>
                <span class="section-dot"></span>
              </h3>
              <a href="#" class="more" @click="showMoreCompetitions">更多 >></a>
            </div>
            <Books :books="limitedCompetitionBooks" />
          </div>
        </div>

        <!-- 右上：报名通道 -->
        <div class="grid-item grid-item-rt">
          <div class="register-section">
            <h3 class="register-title">报名通道</h3>
            <div class="register-content">
              <div class="register-card register-card-1" @click="applyCompetition">
                <h4>学科竞赛报名</h4>
                <p class="register-link">前往报名 >></p>
              </div>
              <div class="register-card register-card-2" @click="applyResearch">
                <h4>学科项目申报</h4>
                <p class="register-link">前往申报 >></p>
              </div>
            </div>
          </div>
        </div>

        <!-- 左下：竞赛成果 -->
        <div class="grid-item grid-item-lb">
          <div class="card-block achievement-section">
            <div class="section-header">
              <h3>
                <span class="section-title">竞赛成果</span>
                <span class="section-dot"></span>
              </h3>
              <a href="#" class="more" @click="showMoreAchievements">更多 >></a>
            </div>
            <ul class="achievement-list">
              <li v-for="(achievement, index) in achievements" :key="index" @click="goAchievementDetail(achievement.achievementId)" style="cursor: pointer;">
                <div class="achievement-info">
                  <div class="achievement-title">{{ achievement.title }}</div>
                  <div class="achievement-level">{{ achievement.level }}</div>
                </div>
                <div class="achievement-team">{{ achievement.team }}</div>
              </li>
            </ul>
          </div>
        </div>

        <!-- 右下：数据一览 -->
        <div class="grid-item grid-item-rb">
          <div class="statistics-section">
            <h3 class="statistics-title">数据一览</h3>
            <div class="statistics-grid">
              <!-- 左上 -->
              <div class="stat-item">
                <count-to :start-val="0" :end-val="stats.teams" :duration="2600" class="stat-value" />
                <div class="stat-label">国家级报奖<br/>（个）</div>
              </div>
              <!-- 左下 -->
              <div class="stat-item">
                <count-to :start-val="0" :end-val="stats.awards" :duration="2800" class="stat-value" />
                <div class="stat-label">市级奖励<br/>（个）</div>
              </div>
              <!-- 中间 -->
              <div class="stat-item">
                <count-to :start-val="0" :end-val="stats.projects" :duration="3000" class="stat-value main-stat" />
                <div class="stat-label">全国竞赛<br/>（个）</div>
              </div>
              <!-- 右上 -->
              <div class="stat-item">
                <count-to :start-val="0" :end-val="stats.competitions" :duration="2700" class="stat-value" />
                <div class="stat-label">竞赛项目<br/>（个）</div>
              </div>
              <!-- 右下 -->
              <div class="stat-item">
                <count-to :start-val="0" :end-val="stats.achievements" :duration="2900" class="stat-value" />
                <div class="stat-label">校级获奖<br/>（个）</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <about id="about"></about>

    <!-- 大模型浮动球 -->
    <floating-ball></floating-ball>
  </div>
</template>

<script>
import About from '../common/About.vue'
import Books from '../library/Books.vue'
import FloatingBall from '../ai/FloatingBall.vue'
import Carousel from './Carousel.vue'
import CountTo from 'vue-count-to'

export default {
  name: 'AppIndex',
  components: { About, Books, FloatingBall, Carousel, CountTo },
  data() {
    return {
      achievements: [],
      activeTab: 'notice', // 默认显示通知公告
      // 通知公告相关数据
      notices: [],
      // 中心动态相关数据
      dynamics: [],
      noticePageSize: 5, // 显示最新5条
      // 竞赛动态相关数据
      competitionBooks: [],
      competitionDisplayCount: 3, // 竞赛动态显示数量
      // 统计数据
      stats: {
        teams: 20,
        projects: 270,
        awards: 60,
        competitions: 70,
        achievements: 80
      }
    }
  },
  computed: {
    // 限制竞赛动态显示数量
    limitedCompetitionBooks() {
      return this.competitionBooks.slice(0, this.competitionDisplayCount)
    }
  },
  mounted() {
    this.loadAchievements()
    this.loadNotices() // 加载通知公告
    this.loadDynamics() // 加载中心动态
    this.loadCompetitionBooks() // 加载竞赛动态
  },
  methods: {
    // 登录状态校验（基于Shiro会话）
    async checkLoginStatus() {
      try {
        const resp = await this.$axios.get('/currentUser')
        if (resp.data.code === 200) {
          return true
        } else {
          this.$message.warning('请先登录后再操作！')
          this.$router.push({
            path: '/login',
            query: { redirect: this.$route.fullPath }
          })
          return false
        }
      } catch (error) {
        this.$message.warning('请先登录后再操作！')
        this.$router.push({
          path: '/login',
          query: { redirect: this.$route.fullPath }
        })
        return false
      }
    },

    // 加载竞赛成果
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

    // 加载通知公告（只显示最新几条，不分页）
    loadNotices() {
      const _this = this
      this.$axios.post(`/content/article/${this.noticePageSize}/1`, { articleType: 'notice' }).then(resp => {
        if (resp && resp.data.code === 200) {
          const allNotices = resp.data.data.list || []
          _this.notices = allNotices.slice(0, this.noticePageSize)
        }
      }).catch(error => {
        console.log('加载通知公告失败:', error)
      })
    },

    // 加载中心动态
    loadDynamics() {
      const _this = this
      this.$axios.post(`/content/article/${this.noticePageSize}/1`, { articleType: 'dynamic' }).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.dynamics = (resp.data.data.list || []).slice(0, this.noticePageSize)
        }
      }).catch(error => {
        console.log('加载中心动态失败:', error)
      })
    },

    // 加载竞赛动态数据
    loadCompetitionBooks() {
      const _this = this
      this.$axios.get('/categories/0/books').then(resp => {
        if (resp && resp.data.code === 200) {
          _this.competitionBooks = resp.data.data.list || []
        }
      }).catch(error => {
        console.log('加载竞赛动态失败:', error)
        _this.competitionBooks = []
      })
    },

    // 学科竞赛报名
    async applyCompetition() {
      if (!await this.checkLoginStatus()) return
      this.$router.push('/admin/team/participate')
    },

    // 学科项目立项
    async applyResearch() {
      if (!await this.checkLoginStatus()) return
      this.$router.push('/admin/project/apply')
    },

    // 显示更多通知公告
    showMoreNotices() {
      this.$router.push('/jotter')
    },

    // 显示更多竞赛动态
    showMoreCompetitions() {
      this.$router.push('/library')
    },

    // 更多竞赛成果
    showMoreAchievements() {
      this.$router.push('/achievement')
    },

    // 跳转竞赛成果详情
    goAchievementDetail(id) {
      this.$router.push({ path: '/achievement/detail', query: { id: id } })
    },

    // 切换标签页
    switchTab(tab) {
      this.activeTab = tab
    }
  }
}
</script>

<style scoped>
/* 全局基础样式 */
.app-index {
  font-family: 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  color: #333;
  width: 100%;
  margin: 0;
  padding: 0;
  background-color: #ffffff;
  min-height: calc(100vh - 200px);
}

/* 内容容器 */
.content-container {
  width: min(1400px, 100%);
  margin: 0 auto;
  padding: 20px clamp(12px, 2vw, 24px) 40px;
}

/* 0. 顶部通知公告和轮播图区（灰色背景盒子） */
.top-section {
  background-color: rgb(233, 238, 243);
  width: 100vw;
  margin-left: calc(-50vw + 50%);
  padding: 30px 0px 40px 0px;
  margin-bottom: 20px;
  margin-top: -20px;
}

.top-section-inner {
  width: min(1400px, 100%);
  padding: 0 clamp(12px, 2vw, 24px);
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: flex-start;
}

.carousel-wrapper {
  width: min(650px, 100%);
  height: 380px;
  flex-shrink: 0;
  margin-left: -10px;
}

.notice-wrapper {
  flex: 1;
  min-width: 0;
}

.carousel-wrapper >>> .card-carousel {
  margin: 0;
  width: 100%;
  height: 380px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e8e8e8;
}

.carousel-wrapper >>> .el-card {
  height: 100%;
}

.carousel-wrapper >>> .el-card__body {
  padding: 0;
  height: 100%;
}

.carousel-wrapper >>> .el-carousel {
  height: 100%;
}

/* 确保轮播图指示器显示 */
.carousel-wrapper >>> .el-carousel__indicators {
  display: block;
}

/* 1. 主要功能卡片区 */
.main-cards {
  display: flex;
  justify-content: space-between;
  margin: 20px 0;
  gap: 20px;
}

.card {
  flex: 1;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.card-content {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  padding: 20px;
  text-align: center;
}

.competition-card {
  background: linear-gradient(135deg, #ff9800, #ff5722);
}

.research-card {
  background: linear-gradient(135deg, #2196f3, #03a9f4);
}

.card h3 {
  font-size: 24px;
  margin-bottom: 16px;
  font-weight: 600;
}

.apply-btn {
  background-color: rgba(255, 255, 255, 0.95);
  color: #1890ff;
  border: 1px solid rgba(255, 255, 255, 0.8);
  padding: 10px 28px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.apply-btn:hover {
  background-color: white;
  color: #40a9ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 2. 核心内容区*/
.info-section {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 40px;
}

/* 左侧区域（50%宽度） */
.left-content {
  width: 50%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 右侧区域（50%宽度） */
.right-content {
  width: 50%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 4格布局 */
.grid-4-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 40px;
  margin-top: 20px;
}

.grid-item {
  min-height: 500px;
}

.grid-item-lt {
  min-height: 350px;
}

.grid-item-rt {
  min-height: 350px;
}
.grid-item-rt,
.grid-item-lb,
.grid-item-rb {
  display: flex;
  flex-direction: column;
}

/* 统一卡片样式 */
.card-block {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 报名通道卡片样式 */
.register-section {
  background-color: white;
  border-radius: 8px;
  padding: 50px 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}

.register-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px 0;
  text-align: center;
  border-bottom: 2px solid #e8e8e8;
  padding-bottom: 12px;
}

.register-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
  justify-content: center;
}

.register-card {
  width: 100%;
  max-width: 486px;
  height: 70px;
  background: #FFEEC7;
  border-radius: 10px;
  padding: 24px;
  box-sizing: border-box;
  color: #333;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0px 4px 20px 0px rgba(201, 117, 40, 0.3);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.register-card-2 {
  background: #E0F7FF;
  box-shadow: 0px 4px 20px 0px rgba(93, 160, 187, 0.3);
  color: #333;
}

.register-card:hover {
  transform: translateY(-5px);
  box-shadow: 0px 6px 24px 0px rgba(201, 117, 40, 0.4);
}

.register-card-2:hover {
  box-shadow: 0px 6px 24px 0px rgba(93, 160, 187, 0.4);
}

.register-card h4 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 12px 0;
  line-height: 1.4;
  color: #333;
}

.register-card .register-link {
  font-size: 14px;
  font-weight: 500;
  margin: 0;
  opacity: 0.9;
  transition: opacity 0.3s ease;
}

.register-card:hover .register-link {
  opacity: 1;
}

/* 通知公告区域特殊样式 - 无边框，直接显示在大盒子里 */
.notice-section {
  border: none;
  border-radius: 0;
  box-shadow: none;
  padding: 0;
  height: 380px;
  display: flex;
  flex-direction: column;
  background-color: transparent;
}

.notice-section:hover {
  box-shadow: none;
  transform: none;
}

/* 标签页样式 */
.notice-tabs {
  display: flex;
  justify-content: flex-start;
  border-bottom: 2px solid #e8e8e8;
  padding: 0 20px;
  background-color: transparent;
}

.tab-item {
  padding: 12px 20px;
  font-size: 15px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  position: relative;
  transition: color 0.3s ease;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
}

.tab-item:hover {
  color: #1890ff;
}

.tab-item.active {
  color: #1890ff;
  border-bottom-color: #1890ff;
}

.articles-wrap {
  flex: 1;
  padding: 0 20px 20px;
  overflow: hidden;
}

.card-block:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.notice-section:hover {
  box-shadow: none;
  transform: none;
}

/* 区块头部样式 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e8e8e8;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title {
  color: #1a1a1a;
  font-weight: 600;
}

.section-dot {
  display: inline-block;
  width: 4px;
  height: 4px;
  background-color: #1890ff;
  border-radius: 50%;
}

.more {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
}

.more:hover {
  color: #1890ff;
  background-color: #f0f7ff;
}

/* 3. 竞赛成果样式 */
.achievement-section {
  flex: 1;
}

.achievement-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.achievement-list li {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.achievement-list li:last-child {
  border-bottom: none;
}

.achievement-list li:hover {
  background-color: #f8f9fa;
  padding-left: 8px;
  border-radius: 6px;
}

.achievement-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 6px;
}

.achievement-title {
  color: #333;
  font-size: 15px;
  font-weight: 600;
  flex: 1;
  line-height: 1.4;
}

.achievement-level {
  background-color: #4caf50;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  margin-left: 8px;
  white-space: nowrap;
}

.achievement-team {
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}

/* 数据一览统计样式 */
.statistics-section {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.statistics-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 30px 0;
  text-align: center;
}

.statistics-grid {
  position: relative;
  width: min(520px, 100%);
  height: 360px;
  margin: 60px auto 0;
  flex: 1;
}

.stat-item {
  width: 140px;
  height: 140px;
  background: #E7F8FF;
  box-shadow: 0px 4px 10px 0px rgba(0, 0, 0, 0.2);
  border: 1px solid;
  border-image: radial-gradient(circle, rgba(131, 218, 254, 1), rgba(131, 218, 254, 0)) 1 1;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  position: absolute;
}

/* 左上、左下、右上、右下四个小圆 */
.stat-item:nth-child(1) { left: 5%; top: -10%; }
.stat-item:nth-child(2) { left: 5%; top: 44%; }
.stat-item:nth-child(4) { left: 68%; top: -10%; }
.stat-item:nth-child(5) { left: 68%; top: 44%; }

/* 中间主圆 */
.stat-item:nth-child(3) {
  width: 178px;
  height: 178px;
  left: 50%;
  top: 35%;
  transform: translate(-50%, -50%);
  background: #E7F8FF;
  box-shadow: 0px 4px 10px 0px rgba(0, 0, 0, 0.2);
  border: 1px solid;
  border-image: radial-gradient(circle, rgba(131, 218, 254, 1), rgba(255, 255, 255, 1)) 1 1;
  z-index: 1;
}

.stat-item:hover {
  transform: scale(1.05);
  box-shadow: 0px 6px 16px 0px rgba(0, 0, 0, 0.25);
}

.stat-item:nth-child(3):hover {
  transform: translate(-50%, -50%) scale(1.05);
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #333;
  line-height: 1;
}

.stat-value >>> span {
  display: inline;
  font-size: 36px;
  font-weight: 700;
  color: #333;
  line-height: 1;
}

.stat-item:nth-child(3) .stat-value {
  font-size: 48px;
}

.stat-item:nth-child(3) .stat-value >>> span {
  font-size: 48px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  text-align: center;
  line-height: 1.4;
  font-weight: 500;
}

/* 响应式适配 */
@media (max-width: 1400px) {
  .content-container {
    width: min(1400px, 95%);
    padding: 20px 20px 40px;
  }

  .top-section-inner {
    width: min(1400px, 90%);
    height: auto;
  }
}

@media (max-width: 1200px) {
  .content-container {
    width: 95%;
    padding: 20px 10px 40px;
  }

  .top-section-inner {
    width: min(1400px, 95%);
    padding: 0 12px;
  }
}

@media (max-width: 768px) {
  .content-container {
    width: 100%;
    padding: 20px 10px 40px;
  }

  .top-section-inner {
    width: 100%;
    padding: 0;
    flex-direction: column;
    gap: 15px;
  }

  .top-section {
    width: 100%;
    margin-left: 0;
    padding: 15px 0 18px;
    margin-bottom: 15px;
  }

  .notice-wrapper,
  .carousel-wrapper {
    width: 100%;
  }

  .carousel-wrapper {
    height: auto;
    margin-left: 0;
  }

  .carousel-wrapper >>> .card-carousel {
    height: 200px;
  }

  .notice-section {
    height: auto;
    min-height: 300px;
  }

  .articles-wrap {
    overflow-y: visible;
  }

  .grid-4-layout {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .grid-item {
    min-height: auto;
  }

  .info-section {
    flex-direction: column;
    gap: 15px;
  }

  .left-content,
  .right-content {
    width: 100%;
  }

  .main-cards {
    flex-direction: column;
    gap: 15px;
    margin: 20px 0;
  }

  .card {
    height: 180px;
    margin-bottom: 0;
  }

  .card h3 {
    font-size: 22px;
    margin-bottom: 15px;
  }

  .apply-btn {
    padding: 8px 24px;
    font-size: 14px;
  }

  .card-block {
    padding: 16px;
  }

  .section-header h3 {
    font-size: 18px;
  }

  .section-title {
    font-size: 16px;
  }

  .notice-title {
    font-size: 14px;
  }

  .notice-time {
    font-size: 12px;
  }

  .achievement-title {
    font-size: 14px;
  }

  .achievement-team {
    font-size: 13px;
  }

  .register-section {
    width: 100%;
    height: auto;
    padding: 28px 16px 30px;
  }

  .register-content {
    width: 100%;
  }

  .register-card {
    width: 100%;
    max-width: 100%;
    min-height: 70px;
    height: auto;
  }

  .statistics-section {
    height: auto;
    flex: none;
    padding: 16px;
  }

  .statistics-title {
    margin-bottom: 18px;
  }

  .statistics-grid {
    width: 100%;
    max-width: 440px;
    height: 320px;
    margin: 18px auto 0;
    flex: none;
  }

  .stat-item {
    width: 112px;
    height: 112px;
  }

  .stat-item:nth-child(1),
  .stat-item:nth-child(2) {
    left: 2%;
  }

  .stat-item:nth-child(4),
  .stat-item:nth-child(5) {
    left: auto;
    right: 2%;
  }

  .stat-item:nth-child(2),
  .stat-item:nth-child(5) {
    top: 51%;
  }

  .stat-item:nth-child(3) {
    width: 142px;
    height: 142px;
    top: 40%;
  }

  .stat-value,
  .stat-value >>> span {
    font-size: 29px;
  }

  .stat-item:nth-child(3) .stat-value,
  .stat-item:nth-child(3) .stat-value >>> span {
    font-size: 38px;
  }

  .stat-label {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .app-index {
    padding: 0 8px;
  }

  .top-section {
    margin: 15px 0;
    gap: 12px;
  }

  .carousel-wrapper >>> .card-carousel {
    height: 160px;
  }

  .notice-section {
    min-height: 270px;
  }

  .card {
    height: 150px;
  }

  .card h3 {
    font-size: 18px;
    margin-bottom: 12px;
  }

  .apply-btn {
    padding: 6px 20px;
    font-size: 13px;
  }

  .card-block {
    padding: 12px;
  }

  .section-header h3 {
    font-size: 16px;
  }

  .main-cards {
    margin: 15px 0;
    gap: 12px;
  }

  .info-section {
    gap: 12px;
    margin-bottom: 30px;
  }

  .register-section {
    padding: 22px 12px 24px;
  }

  .register-title {
    font-size: 17px;
    margin-bottom: 16px;
  }

  .statistics-section {
    padding: 14px 10px;
  }

  .statistics-grid {
    max-width: 330px;
    height: 280px;
    margin-top: 14px;
  }

  .stat-item {
    width: 94px;
    height: 94px;
  }

  .stat-item:nth-child(2),
  .stat-item:nth-child(5) {
    top: 52%;
  }

  .stat-item:nth-child(3) {
    width: 122px;
    height: 122px;
    top: 41%;
  }

  .stat-value,
  .stat-value >>> span {
    font-size: 25px;
  }

  .stat-item:nth-child(3) .stat-value,
  .stat-item:nth-child(3) .stat-value >>> span {
    font-size: 33px;
  }
}

/* 通知公告容器样式 */
.notice-section {
  padding-bottom: 12px;
}

/* 通知公告内容容器 */
.articles-wrap {
  width: 100%;
  padding: 16px 20px 20px;
  flex: 1;
  overflow-y: auto;
}

/* 自定义通知公告列表样式 */
.notice-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.notice-item {
  padding: 16px 0;
  border-bottom: 1px solid #e8e8e8;
  transition: all 0.3s ease;
  position: relative;
  margin: 0;
}


.notice-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.notice-item:hover {
  background-color: #f5f7fa;
  padding-left: 4px;
  border-radius: 4px;
}

.notice-link {
  display: flex;
  justify-content: space-between;
  align-items: center;
  text-decoration: none;
  color: #333;
  width: 100%;
  padding: 0;
  gap: 16px;
}

.notice-title {
  font-size: 14px;
  font-weight: 400;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  line-height: 1.6;
  color: #333;
  transition: color 0.3s ease;
  padding: 0;
  text-align: left;
}

.notice-time {
  font-size: 13px;
  color: #8c8c8c;
  white-space: nowrap;
  font-weight: 400;
  flex-shrink: 0;
  padding: 0;
}

.notice-link:hover .notice-title {
  color: #1890ff;
}

/* 竞赛动态容器样式 */
.competition-section {
  padding-bottom: 12px;
}

/* 隐藏竞赛动态的分页和滚动 */
.competition-section >>> .books-pagination {
  display: none;
}

.competition-section >>> .books-list-wrap {
  overflow: visible;
  max-height: none;
}
</style>
