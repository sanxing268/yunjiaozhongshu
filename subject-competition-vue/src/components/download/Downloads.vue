<!-- 文件下载列表页面 -->
<template>
  <div class="downloads-page">
    <div class="downloads-area">
      <el-card class="downloads-card">
        <div class="page-header">
          <h2>文件下载</h2>
        </div>

        <!-- 分类筛选 -->
        <div style="margin-bottom: 20px;">
          <div style="font-weight: 600; color: #333; font-size: 14px; margin-bottom: 8px;">分类：</div>
          <div style="display: flex; flex-wrap: wrap; gap: 8px;">
            <button 
              type="button" 
              :class="['filter-btn', { active: currentCategory === '' }]" 
              @click="currentCategory = ''"
              style="padding: 6px 14px; border: 1px solid #dcdfe6; border-radius: 4px; background: white; cursor: pointer; transition: all 0.3s;">
              全部
            </button>
            <button 
              type="button" 
              :class="['filter-btn', { active: currentCategory === '通知文件' }]" 
              @click="currentCategory = '通知文件'"
              style="padding: 6px 14px; border: 1px solid #dcdfe6; border-radius: 4px; background: white; cursor: pointer; transition: all 0.3s;">
              通知文件
            </button>
            <button 
              type="button" 
              :class="['filter-btn', { active: currentCategory === '报名表' }]" 
              @click="currentCategory = '报名表'"
              style="padding: 6px 14px; border: 1px solid #dcdfe6; border-radius: 4px; background: white; cursor: pointer; transition: all 0.3s;">
              报名表
            </button>
            <button 
              type="button" 
              :class="['filter-btn', { active: currentCategory === '比赛规则' }]" 
              @click="currentCategory = '比赛规则'"
              style="padding: 6px 14px; border: 1px solid #dcdfe6; border-radius: 4px; background: white; cursor: pointer; transition: all 0.3s;">
              比赛规则
            </button>
            <button 
              type="button" 
              :class="['filter-btn', { active: currentCategory === '其他' }]" 
              @click="currentCategory = '其他'"
              style="padding: 6px 14px; border: 1px solid #dcdfe6; border-radius: 4px; background: white; cursor: pointer; transition: all 0.3s;">
              其他
            </button>
          </div>
        </div>

        <div v-if="filteredList.length === 0" class="empty-tip">
          暂无文件
        </div>

        <div class="file-list">
          <div v-for="item in filteredList" :key="item.downloadId" class="file-item">
            <div class="file-icon">
              <i class="el-icon-document"></i>
            </div>
            <div class="file-info">
              <div class="file-title">{{ item.title }}</div>
              <div class="file-desc" v-if="item.description">{{ item.description }}</div>
              <div class="file-meta">
                <span v-if="item.category" class="file-category">{{ item.category }}</span>
                <span v-if="item.fileSize" class="file-size">{{ item.fileSize }}</span>
                <span class="file-count">下载 {{ item.downloadCount || 0 }} 次</span>
                <span v-if="item.createTime" class="file-time">{{ item.createTime }}</span>
              </div>
            </div>
            <div class="file-action">
              <el-button type="primary" size="small" icon="el-icon-download" @click="handleDownload(item)">下载</el-button>
            </div>
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
  name: 'Downloads',
  components: { About, FloatingBall },
  data() {
    return {
      downloads: [],
      currentCategory: ''
    }
  },
  computed: {
    filteredList() {
      if (!this.currentCategory) {
        return this.downloads
      }
      return this.downloads.filter(item => item.category === this.currentCategory)
    }
  },
  mounted() {
    this.loadDownloads()
  },
  methods: {
    loadDownloads() {
      var _this = this
      this.$axios.get('/download/list').then(resp => {
        if (resp && resp.data.code === 200) {
          _this.downloads = resp.data.data.list || []
        }
      }).catch(error => {
        console.log('加载文件列表失败:', error)
      })
    },
    filterByCategory() {
      // 筛选由 computed 属性自动完成
    },
// 处理文件下载标签下的文件点击下载功能
    handleDownload(item) {
      const fileUrl = item.fileUrl
      // 没有文件时
      if (!fileUrl || fileUrl === '' || fileUrl === '#') {
        this.$message.info('下载链接暂未配置')
        return
      }

      // 增加下载次数
      this.$axios.put('/download/count/' + item.downloadId).catch(() => {})
      item.downloadCount = (item.downloadCount || 0) + 1

      // 提取文件名（参考结题审核的下载逻辑）
      let fileName = item.title || 'download'
      if (fileUrl.lastIndexOf('/') !== -1) {
        const fullFileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1)
        // 如果文件名包含时间戳前缀（格式：时间戳_原文件名），提取原文件名
        const underscoreIndex = fullFileName.indexOf('_')
        if (underscoreIndex !== -1 && underscoreIndex > 0) {
          const prefix = fullFileName.substring(0, underscoreIndex)
          if (/^\d+$/.test(prefix)) {
            fileName = fullFileName.substring(underscoreIndex + 1)
          } else {
            fileName = fullFileName
          }
        } else {
          fileName = fullFileName
        }
      }

      // 如果文件URL是完整的HTTP/HTTPS URL（如COS地址），创建下载链接
      if (fileUrl.startsWith('http://') || fileUrl.startsWith('https://')) {
        const link = document.createElement('a')
        link.style.display = 'none'
        link.href = fileUrl
        link.setAttribute('download', fileName)
        link.setAttribute('target', '_blank')
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        // 实现文件的下载功能
      } else {
        // 非HTTP链接，尝试直接打开
        window.open(fileUrl, '_blank')
      }
    }
  }
}
</script>

<style scoped>
.downloads-page {
  min-height: calc(100vh - 200px);
}

.downloads-area {
  width: 990px;
  margin: 40px auto 20px;
  padding: 0 20px;
  max-width: 100%;
}

.downloads-card {
  text-align: left;
  padding: 20px;
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

.filter-bar {
  margin-bottom: 20px;
  display: block;
  visibility: visible;
  opacity: 1;
  width: 100%;
  overflow: visible;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 15px;
}

.file-list {
  display: flex;
  flex-direction: column;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.file-item:last-child {
  border-bottom: none;
}

.file-item:hover {
  background-color: #f8f9fa;
  padding-left: 8px;
  border-radius: 6px;
}

.file-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  background-color: #e6f7ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: #1890ff;
}

.file-info {
  flex: 1;
  min-width: 0;
  max-width: calc(100% - 120px);
}

.file-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
  line-height: 1.3;
}

.file-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
  line-height: 1.3;
}

.file-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #999;
  flex-wrap: wrap;
}

.file-category {
  background-color: #f0f0f0;
  padding: 1px 8px;
  border-radius: 4px;
  color: #666;
}

.file-action {
  flex-shrink: 0;
  margin-left: 16px;
}

.filter-btn {
  color: #333;
}

.filter-btn.active {
  background-color: #1890ff;
  color: #333;
  border-color: #1890ff;
}

.filter-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

/* 响应式适配 */
@media (max-width: 1024px) {
  .downloads-area {
    width: 95%;
  }
}

@media (max-width: 768px) {
  .downloads-area {
    width: 100%;
    padding: 0 10px;
    margin-top: 20px;
  }

  .file-item {
    flex-wrap: wrap;
    gap: 12px;
  }

  .file-action {
    margin-left: 64px;
  }

  .file-meta {
    gap: 8px;
  }
}
</style>
