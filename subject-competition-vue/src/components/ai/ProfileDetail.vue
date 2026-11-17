<!-- 我的用户画像组件 -->
<template>
  <div class="profile-detail-container">
    <el-card class="profile-card">
      <div slot="header" class="clearfix">
        <span class="title">我的用户画像</span>
      </div>

      <!-- 基础信息 -->
      <el-tabs v-model="activeTab">
        <el-tab-pane label="用户印象" name="portrait">
          <div class="content-section">
            <div v-if="loadingPortrait" class="loading">
              <i class="el-icon-loading"></i> 加载中...
            </div>
            <div v-else-if="profileData.userPortrait" class="content-text">
              {{ profileData.userPortrait }}
            </div>
            <div v-else class="empty-tip">
              还未生成用户印象，请先填写问卷
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="学习计划" name="learning">
          <div class="content-section">
            <div v-if="loadingLearning" class="loading">
              <i class="el-icon-loading"></i> 加载中...
            </div>
            <div v-else-if="profileData.learningPlan" class="content-text">
              <div v-html="formatContent(profileData.learningPlan)"></div>
            </div>
            <div v-else class="empty-tip">
              还未生成学习计划，请先填写问卷
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="竞赛推荐" name="competition">
          <div class="content-section">
            <div v-if="loadingCompetition" class="loading">
              <i class="el-icon-loading"></i> 加载中...
            </div>
            <div v-else-if="profileData.competitionRecommendation" class="content-text">
              <div v-html="formatContent(profileData.competitionRecommendation)"></div>
            </div>
            <div v-else class="empty-tip">
              还未生成竞赛推荐，请先填写问卷
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="就业指导" name="employment">
          <div class="content-section">
            <div v-if="loadingEmployment" class="loading">
              <i class="el-icon-loading"></i> 加载中...
            </div>
            <div v-else-if="profileData.employmentGuidance" class="content-text">
              <div v-html="formatContent(profileData.employmentGuidance)"></div>
            </div>
            <div v-else class="empty-tip">
              还未生成就业指导，请先填写问卷
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="问卷信息" name="info">
          <div class="info-section">
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="info-item">
                  <span class="label">学校：</span>
                  <span class="value">{{ profileData.school || '-' }}</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="info-item">
                  <span class="label">学院：</span>
                  <span class="value">{{ profileData.college || '-' }}</span>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="info-item">
                  <span class="label">专业：</span>
                  <span class="value">{{ profileData.major || '-' }}</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="info-item">
                  <span class="label">年级：</span>
                  <span class="value">{{ profileData.grade || '-' }}</span>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="info-item">
                  <span class="label">性别：</span>
                  <span class="value">{{ profileData.gender || '-' }}</span>
                </div>
              </el-col>
            </el-row>

            <el-divider></el-divider>

            <div class="info-item">
              <span class="label">核心技能：</span>
              <div class="tags">
                <el-tag
                  v-for="skill in parseJson(profileData.skills)"
                  :key="skill"
                  size="small"
                >
                  {{ skill }}
                </el-tag>
              </div>
            </div>

            <div class="info-item">
              <span class="label">兴趣爱好：</span>
              <div class="tags">
                <el-tag
                  v-for="interest in parseJson(profileData.interests)"
                  :key="interest"
                  size="small"
                  type="success"
                >
                  {{ interest }}
                </el-tag>
              </div>
            </div>

            <div class="info-item">
              <span class="label">证书资质：</span>
              <div class="tags">
                <el-tag
                  v-for="cert in parseJson(profileData.certificates)"
                  :key="cert"
                  size="small"
                  type="info"
                >
                  {{ cert }}
                </el-tag>
              </div>
            </div>

            <div class="info-item">
              <span class="label">获奖经历：</span>
              <div class="tags">
                <el-tag
                  v-for="award in parseJson(profileData.awards)"
                  :key="award"
                  size="small"
                  type="warning"
                >
                  {{ award }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

      <div slot="footer" class="card-footer">
        <el-button @click="editProfile">编辑问卷</el-button>
        <el-button type="primary" @click="refreshProfile">刷新数据</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'ProfileDetail',
  data() {
    return {
      activeTab: 'portrait',
      profileData: {
        userId: null,
        school: '',
        college: '',
        major: '',
        grade: '',
        gender: '',
        certificates: '[]',
        awards: '[]',
        skills: '[]',
        interests: '[]',
        competitionPreference: '{}',
        userPortrait: '',
        learningPlan: '',
        competitionRecommendation: '',
        employmentGuidance: ''
      },
      loadingPortrait: false,
      loadingLearning: false,
      loadingCompetition: false,
      loadingEmployment: false
    }
  },
  computed: {
    ...mapState(['user'])
  },
  mounted() {
    this.loadProfileData()
  },
  methods: {
    loadProfileData() {
      const userId = (this.$store.state.user && this.$store.state.user.id) || localStorage.getItem('userId')
      if (!userId) {
        this.$message.error('用户ID不存在')
        return
      }

      this.$axios
        .get(`/ai/profile/${userId}`)
        .then((response) => {
          if (response.data.code === 200) {
            this.profileData = response.data.data.profile
          } else {
            this.$message.error(response.data.message || '获取数据失败')
          }
        })
        .catch((error) => {
          this.$message.error('加载数据失败：' + (error.message || '网络错误'))
        })
    },
    loadContent(type) {
      const userId = (this.$store.state.user && this.$store.state.user.id) || localStorage.getItem('userId')
      const loadingKey = `loading${type.charAt(0).toUpperCase()}${type.slice(1)}`

      this[loadingKey] = true

      this.$axios
        .get(`/ai/profile/content/${userId}`, { params: { type } })
        .then((response) => {
          if (response.data.code === 200) {
            const contentKey = this.getContentKey(type)
            this.profileData[contentKey] = response.data.data.content
          } else {
            this.$message.warning(response.data.message)
          }
        })
        .catch((error) => {
          this.$message.error('加载失败：' + (error.message || '网络错误'))
        })
        .finally(() => {
          this[loadingKey] = false
        })
    },
    getContentKey(type) {
      const mapping = {
        portrait: 'userPortrait',
        learning: 'learningPlan',
        competition: 'competitionRecommendation',
        employment: 'employmentGuidance'
      }
      return mapping[type] || ''
    },
    parseJson(jsonStr) {
      try {
        return JSON.parse(jsonStr || '[]')
      } catch (e) {
        return []
      }
    },
    formatContent(text) {
      if (!text) return ''
      // 简单的Markdown格式转换
      return text
        .replace(/\n/g, '<br/>')
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\*(.*?)\*/g, '<em>$1</em>')
    },
    editProfile() {
      this.$root.$emit('showProfileForm')
    },
    refreshProfile() {
      this.loadProfileData()
      this.$message.success('已刷新数据')
    }
  },
  watch: {
    activeTab(newVal) {
      // 当切换到某个标签时，如果数据为空，则加载
      const mapping = {
        portrait: 'userPortrait',
        learning: 'learningPlan',
        competition: 'competitionRecommendation',
        employment: 'employmentGuidance'
      }
      const contentKey = mapping[newVal]
      if (contentKey && !this.profileData[contentKey]) {
        this.loadContent(newVal)
      }
    }
  }
}
</script>

<style scoped lang="scss">
.profile-detail-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;

  .profile-card {
    max-width: 1000px;
    margin: 0 auto;

    .title {
      font-size: 24px;
      font-weight: bold;
      color: #333;
    }

    .content-section {
      padding: 20px 0;
      min-height: 300px;

      .loading {
        text-align: center;
        padding: 50px 0;
        color: #909399;
        font-size: 16px;

        i {
          margin-right: 10px;
          animation: spin 1s infinite linear;
        }
      }

      @keyframes spin {
        0% { transform: rotate(0); }
        100% { transform: rotate(360deg); }
      }

      .content-text {
        line-height: 1.8;
        color: #333;
        font-size: 14px;
        white-space: pre-wrap;
        word-break: break-word;
      }

      .empty-tip {
        text-align: center;
        padding: 50px 0;
        color: #909399;
        font-size: 14px;
      }
    }

    .info-section {
      padding: 20px 0;

      .info-item {
        margin-bottom: 20px;

        .label {
          font-weight: bold;
          color: #333;
          min-width: 100px;
          display: inline-block;
        }

        .value {
          color: #666;
        }

        .tags {
          display: flex;
          flex-wrap: wrap;
          gap: 10px;
          margin-top: 10px;

          :deep(.el-tag) {
            margin: 5px 0;
          }
        }
      }

      :deep(.el-divider) {
        margin: 20px 0;
      }
    }

    .card-footer {
      display: flex;
      justify-content: flex-end;
      gap: 10px;
    }
  }
}
</style>
