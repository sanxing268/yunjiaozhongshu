<!-- 点击每个竞赛显示的弹窗内容，包括赛事介绍，热点分析，赛事解读 -->
<template>
  <!-- 外层容器：适配竞赛动态区域的滚动，分离内容和分页 -->
  <div class="books-container">
    <!-- 竞赛卡片列表与分页：同一个白色容器 -->
    <div class="books-list-wrap">
      <div class="books-content">
        <div v-for="item in paginatedBooks" :key="item.id" class="book-card-item">
          <div class="card-header">
            <span class="level-badge" :class="'level-' + ((item.author || '未知')).toLowerCase()">{{ item.author || '未知' }}</span>
            <div class="card-title-clickable" @click="openDetailDialog(item)">{{ item.title }}</div>
          </div>
          <div class="card-meta">
            <div class="meta-row">
              <span class="meta-label">主办：</span>
              <span class="meta-value">{{ item.press || '未知' }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">日期：</span>
              <span class="meta-value-with-link">
                <span>{{ item.date || '未知' }}</span>
                <a href="javascript:void(0)" class="visit-link" @click="visitWebsite(item)">点击访问</a>
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页组件：在容器内，用分割线隔开 -->
      <div class="books-pagination">
        <el-pagination background small @current-change="handleCurrentChange" :current-page="currentPage"
          :page-size="pageSize" :total="books.length" layout="prev, pager, next, jumper, ->, total">
        </el-pagination>
      </div>
    </div>

    <!-- 详情弹窗：左图右文布局，左右分页 -->
    <el-dialog
      :title="currentItem ? currentItem.title : '竞赛详情'"
      :visible.sync="dialogVisible"
      width="750px"
      class="book-detail-dialog"
      :close-on-click-modal="true"
      :close-on-press-escape="true"
      :show-close="true"
      @open="onDialogOpen">
      <div class="dialog-content" v-if="currentItem">
        <!-- 顶部：封面图 -->
        <div class="dialog-cover-top">
          <img :src="currentItem.cover" :alt="currentItem.title" @error="handleImageError" class="cover-image cover-16-9">
        </div>
        <!-- 下方：详情信息 -->
        <div class="dialog-info-full">
          <h3 class="dialog-title">{{ currentItem.title }}</h3>
          <div class="dialog-meta">
            <div class="meta-item">
              <i class="el-icon-user"></i>
              <span>级别：{{ currentItem.author || '未知' }}</span>
            </div>
            <div class="meta-item">
              <i class="el-icon-office-building"></i>
              <span>主办：{{ currentItem.press || '未知' }}</span>
            </div>
            <div class="meta-item">
              <i class="el-icon-date"></i>
              <span>日期：{{ currentItem.date || '未知' }}</span>
            </div>
          </div>
          <div class="dialog-abstract" v-if="currentItem.abs">
            <h4 class="abstract-title">简介</h4>
            <p class="abstract-content">{{ currentItem.abs }}</p>
          </div>
          
          <!-- 大模型热点分析 -->
          <div class="ai-hotspot-section">
            <h4 class="ai-hotspot-title">
              <i class="el-icon-sunny"></i>
              大模型热点分析
            </h4>
            <div v-if="hotspotLoading" class="hotspot-loading">
              <i class="el-icon-loading"></i>
              <span v-if="hotspotLoadingStatus === 'searching'">正在检索大模型知识库...</span>
              <span v-else-if="hotspotLoadingStatus === 'generating'">暂未更新，大模型正在为您进行联网搜索分析，请稍后...</span>
            </div>
            <div v-else-if="hotspotContent" class="hotspot-content" v-html="formatHotspotContent(hotspotContent)"></div>
            <div v-else class="hotspot-error">
              <i class="el-icon-warning"></i>
              <span>暂无热点分析数据</span>
            </div>
          </div>
          
          <!-- 大模型解读赛事 -->
          <div class="ai-analysis-section">
            <h4 class="ai-analysis-title">
              <i class="el-icon-cpu"></i>
              大模型解读赛事
            </h4>
            <p class="ai-analysis-desc">点击下方问题，让大模型为您详细解答</p>
            <div class="ai-questions-grid">
              <div 
                v-for="(question, index) in getAIQuestions(currentItem)"
                :key="index"
                class="ai-question-card"
                @click="askAI(question)">
                <i class="el-icon-chat-dot-round"></i>
                <span>{{ question }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 对话框页脚 -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" class="close-btn">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Books',
  props: {
    books: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      currentItem: null,
      dialogCurrentPage: 1,
      hotspotLoading: false,
      hotspotContent: '',
      hotspotLoadingStatus: '' // 'searching': 查询数据库, 'generating': 调用AI生成
    }
  },
  computed: {
    // 计算属性：分页后的书籍列表
    paginatedBooks() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.books.slice(start, end)
    }
  },
  mounted() {
    // 数据由父组件通过 props 传入
  },
  watch: {
  },
  methods: {
    // 图片加载失败处理
    handleImageError (e) {
      e.target.src = this.$defaultImage
    },
    // 分页切换逻辑
    handleCurrentChange(currentPage) {
      this.currentPage = currentPage
      // 前端分页：计算属性自动更新列表，无需额外操作
    },
    // 访问网站
    visitWebsite(item) {
      if (item.website) {
        window.open(item.website, '_blank')
      } else {
        this.$message.info('暂无网址')
      }
    },
    // 弹窗打开事件
    onDialogOpen() {
      this.dialogCurrentPage = 1
      this.loadHotspotAnalysis()
    },
    
    // 打开详情弹窗
    openDetailDialog(item) {
      // 如果当前在首页的竞赛动态区域，跳转到竞赛一览页面并打开详情
      if (this.$route.path === '/index' || this.$route.path === '/home') {
        this.$router.push({
          path: '/library',
          query: { previewId: item.id }
        })
      } else {
        // 在竞赛一览页面，直接打开弹窗
        this.currentItem = item
        this.dialogVisible = true
        // 找出当前项在列表中的位置
        const index = this.books.findIndex(book => book.id === item.id)
        this.dialogCurrentPage = index + 1
      }
    },
    // 弹窗分页切换
    handleDialogPageChange(page) {
      this.dialogCurrentPage = page
      this.currentItem = this.books[page - 1]
    },
    // 生成AI问题列表
    getAIQuestions(item) {
      if (!item) return []
      const title = item.title || '该竞赛'
      return [
        `${title}的参赛资格和报名要求是什么？`,
        `${title}的评审标准和获奖机制是怎样的？`,
        `如何准备${title}，有什么技巧和建议？`,
        `${title}对个人发展和升学有什么帮助？`
      ]
    },
    // 加载AI热点分析
    async loadHotspotAnalysis() {
      if (!this.currentItem) return
      
      const competitionId = this.currentItem.id
      
      // 初始状态：显示查询数据库的提示
      this.hotspotLoading = true
      this.hotspotContent = ''
      this.hotspotLoadingStatus = 'searching'
      
      try {
        // 第一步：从数据库查询project_view表，检查ai字段是否有数据
        const resp = await this.$axios.get(`/projectView/${competitionId}`)
        
        if (resp && resp.data && resp.data.code === 200) {
          // Result.ok().data("data", projectView) 返回的结构是 { code: 200, data: { data: projectView } }
          const projectView = resp.data.data.data || resp.data.data
          
          // 调试：打印返回的数据
          console.log('完整响应数据:', resp.data)
          console.log('resp.data.data:', resp.data.data)
          console.log('查询到的projectView数据:', projectView)
          console.log('projectView.ai字段:', projectView ? projectView.ai : 'projectView为空')
          
          // 如果数据库中有ai字段数据，直接使用，不显示加载状态
          if (projectView && projectView.ai != null && projectView.ai !== '' && projectView.ai.trim() !== '') {
            console.log('数据库中有AI数据，直接使用')
            this.hotspotContent = projectView.ai
            this.hotspotLoading = false
            this.hotspotLoadingStatus = ''
            return
          } else {
            console.log('数据库中没有AI数据，需要生成')
          }
        }
        
        // 第二步：如果数据库中没有ai数据，调用AI生成
        // 切换状态为生成中，显示"暂未更新，AI正在为您进行联网搜索分析，请稍后..."
        this.hotspotLoadingStatus = 'generating'
        const question = `请用250字以内简要分析"${this.currentItem.title}"的参赛要求和热点，包括报名情况、政策变化、获奖趋势、具体的打法等方面。要求：1.控制在250字以内，末尾不要生成250字什么什么的字眼 2.简洁专业 3.重点突出。`
        
        const messageList = [
          {
            role: 'system',
            content: '你是一个专为学科竞赛提供信息的大模型助手，请提供简洁、专业的分析。'
          },
          {
            role: 'user',
            content: question
          }
        ]
        
        const apiUrl = this.$axios.defaults.baseURL + '/ai/chat'
        const response = await fetch(apiUrl, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(messageList),
          credentials: 'include'
        })
        
        if (!response.ok) {
          throw new Error('AI请求失败')
        }
        
        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''
        let generatedContent = ''
        
        const readChunk = async () => {
          const { done, value } = await reader.read()
          if (done) {
            this.hotspotLoading = false
            // AI生成完成后，保存到数据库
            if (generatedContent && generatedContent.trim() !== '') {
              await this.saveHotspotToDatabase(competitionId, generatedContent)
            }
            return
          }
          
          buffer += decoder.decode(value, { stream: true })
          const lines = buffer.split('\n')
          buffer = lines.pop() || ''
          
          lines.forEach(line => {
            if (line.startsWith('data: ')) {
              const data = line.substring(6).trim()
              if (data === '' || data === '[DONE]') {
                this.hotspotLoading = false
                // AI生成完成后，保存到数据库
                if (generatedContent && generatedContent.trim() !== '') {
                  this.saveHotspotToDatabase(competitionId, generatedContent)
                }
                return
              }
              
              try {
                const json = JSON.parse(data)
                if (json.type === 'content' && json.content) {
                  generatedContent += json.content
                  this.hotspotContent += json.content
                } else if (json.type === 'done') {
                  this.hotspotLoading = false
                  // AI生成完成后，保存到数据库
                  if (generatedContent && generatedContent.trim() !== '') {
                    this.saveHotspotToDatabase(competitionId, generatedContent)
                  }
                }
              } catch (e) {
                // 忽略解析错误
              }
            }
          })
          
          return readChunk()
        }
        
        await readChunk()
        
      } catch (error) {
        console.error('AI热点分析失败:', error)
        // 如果是查询数据库失败（可能API不存在），继续尝试AI生成
        if (error.response && error.response.status === 404) {
          console.log('数据库查询API不存在，直接调用AI生成')
          // 切换为生成状态
          this.hotspotLoadingStatus = 'generating'
          // 这里可以继续调用AI生成的逻辑，或者显示错误信息
        }
        // 如果其他错误，关闭加载状态
        if (error.response && error.response.status !== 404) {
          this.hotspotLoading = false
          this.hotspotContent = ''
          this.hotspotLoadingStatus = ''
        }
      }
    },
    
    // 保存热点分析到数据库
    async saveHotspotToDatabase(competitionId, aiContent) {
      try {
        // 更新project_view表的ai字段
        await this.$axios.put(`/projectView/${competitionId}/ai`, {
          ai: aiContent
        })
        console.log('AI热点分析已保存到数据库')
      } catch (error) {
        console.error('保存AI热点分析到数据库失败:', error)
        // 保存失败不影响显示，只记录错误
      }
    },
    
    // 格式化热点内容
    formatHotspotContent(text) {
      if (!text) return ''
      // 简单的markdown格式化
      text = text.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
      text = text.replace(/\n/g, '<br>')
      return text
    },
    
    // 跳转AI界面并发送问题
    async askAI(question) {
      // 检查登录状态
      try {
        const resp = await this.$axios.get('/authentication')
        if (resp && resp.data) {
          // 已登录，跳转到AI聊天页面，携带问题参数
          this.$router.push({
            path: '/ai/chat',
            query: { question: question }
          })
        }
      } catch (error) {
        // 未登录，提示并跳转到登录页
        this.$message.warning('请先登录后使用大模型功能')
        this.$router.push({
          path: '/login',
          query: { redirect: '/ai/chat', question: question }
        })
      }
    }
  }
}
</script>

<style scoped>
/* 外层容器：垂直布局，适配父容器高度 */
.books-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

/* 卡片列表容器：可滚动，占满剩余高度 */
.books-list-wrap {
  flex: 0 1 auto;
  overflow-y: auto;
  margin: 0px 0;
  display: flex;
  flex-direction: column;
  width: 100%;
  background: white;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
}

/* 隐藏滚动条 */
.books-list-wrap::-webkit-scrollbar {
  display: none;
}

.books-list-wrap {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.books-content {
  flex: 0 1 auto;
  overflow-y: auto;
  padding: 0;
}

/* 竞赛卡片列表样式 */
.book-card-item {
  background: transparent;
  border: none;
  border-bottom: 1px solid #f0f0f0;
  border-radius: 0;
  padding: 12px 0;
  margin-bottom: 0;
  transition: all 0.2s ease;
}

.book-card-item:hover {
  box-shadow: none;
  background-color: transparent;
}

.book-card-item:last-child {
  border-bottom: none;
}

.card-header {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 8px;
}

.level-badge {
  display: inline-block;
  margin-left: 25px;
  padding: 4px 10px;
  /* border-radius: 12px; */
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
  flex-shrink: 0;
  color: white;
  letter-spacing: 0.3px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.level-badge::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.level-badge:hover::before {
  left: 100%;
}

.level-badge:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.level-badge.level-国际级 {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.level-badge.level-国家级 {
  background: linear-gradient(135deg, #4096ff 0%, #2e7ce8 100%);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.level-badge.level-省级 {
  background: linear-gradient(135deg, #95de64 0%, #73d13d 100%);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.level-badge.level-市级 {
  background: linear-gradient(135deg, #ffa940 0%, #fa8c16 100%);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.level-badge.level-校级 {
  background: linear-gradient(135deg, #b37feb 0%, #9254de 100%);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.level-badge.level-其他 {
  background: linear-gradient(135deg, #d9d9d9 0%, #bfbfbf 100%);
  color: #333;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.level-badge.level-未知 {
  background: linear-gradient(135deg, #d9d9d9 0%, #bfbfbf 100%);
  color: #333;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.card-title-clickable {
  font-size: 14px;
  font-weight: 600;
  color: #0066cc;
  line-height: 1.4;
  word-break: break-word;
  cursor: pointer;
  text-align: left;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-top: 5px;
  overflow: hidden;
  flex: 1;
}

.card-title-clickable:hover {
  text-decoration: underline;
}

.card-meta {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
  padding-left: 40px;
}

.meta-row {
  display: flex;
  font-size: 12px;
  margin-bottom: 6px;
  color: #333;
  text-align: left;
}

.meta-row:last-child {
  margin-bottom: 0;
}

.meta-label {
  font-weight: 500;
  color: #999;
  min-width: 50px;
}

.meta-value {
  color: #333;
  flex: 1;
  word-break: break-word;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta-value-with-link {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.meta-value-with-link span {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.visit-link {
  color: #0066cc;
  text-decoration: none;
  font-size: 12px;
  white-space: nowrap;
  cursor: pointer;
}

.visit-link:hover {
  text-decoration: underline;
}

/* 分页容器：右对齐，加分隔线 */
.books-pagination {
  padding: 20px 0;
  margin-top: 20px;
  border-top: 1px solid #f5f5f5;
  text-align: center;
  display: flex;
  justify-content: center;
}

/* 美化分页组件 */
.books-pagination >>> .el-pagination {
  padding: 0;
}

.books-pagination >>> .el-pager li {
  background-color: #fff;
  border: 1px solid #e4e7ed;
  color: #606266;
  font-weight: 500;
  transition: all 0.3s ease;
}

.books-pagination >>> .el-pager li:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.books-pagination >>> .el-pager li.active {
  background-color: #1890ff;
  border-color: #1890ff;
  color: #fff;
}

.books-pagination >>> .btn-prev,
.books-pagination >>> .btn-next {
  background-color: #fff;
  border: 1px solid #e4e7ed;
  color: #606266;
  transition: all 0.3s ease;
}

.books-pagination >>> .btn-prev:hover,
.books-pagination >>> .btn-next:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.books-pagination >>> .el-pagination__total {
  color: #606266;
  font-weight: 500;
}

.books-pagination >>> .el-pagination__jump {
  color: #606266;
}

.books-pagination >>> .el-pagination__editor {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.books-pagination >>> .el-pagination__editor:focus {
  border-color: #1890ff;
}

@media (max-width: 1024px) {
  .book-card-item {
    padding: 8px 0;
  }

  .card-title-clickable {
    font-size: 13px;
  }

  .meta-row {
    font-size: 11px;
  }

  .card-meta {
    padding-left: 35px;
  }
}

@media (max-width: 768px) {
  .books-list-wrap {
    padding: 0 5px;
  }

  .book-card-item {
    padding: 6px 0;
  }

  .card-title-clickable {
    font-size: 12px;
  }

  .meta-row {
    font-size: 10px;
  }

  .card-meta {
    padding-left: 30px;
  }
}

@media (max-width: 480px) {
  .book-card-item {
    padding: 6px 0;
  }

  .card-title-clickable {
    font-size: 11px;
  }

  .meta-row {
    font-size: 9px;
  }

  .level-badge {
    font-size: 10px;
    padding: 3px 8px;
    /* border-radius: 10px; */
  }

  .card-meta {
    padding-left: 25px;
  }
}

.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 卡片文字样式 */
.title {
  font-size: 14px;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.author {
  color: #333;
  width: 102px;
  font-size: 13px;
  margin-bottom: 6px;
  text-align: left;
}

.abstract {
  line-height: 17px;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 分页容器：在容器内，用分割线隔开 */
.books-pagination {
  padding: 20px 0;
  margin-top: 20px;
  border-top: 1px solid #f5f5f5;
  text-align: center;
  display: flex;
  justify-content: center;
  flex-shrink: 0;
}

/* 链接样式 */
a {
  text-decoration: none;
}

a:link,
a:visited,
a:focus {
  color: #3377aa;
}

/* 滚动条美化（和通知公告区域统一） */
.books-list-wrap::-webkit-scrollbar {
  width: 6px;
}

.books-list-wrap::-webkit-scrollbar-track {
  background-color: #f5f5f5;
  border-radius: 3px;
}

.books-list-wrap::-webkit-scrollbar-thumb {
  background-color: #dcdcdc;
  border-radius: 3px;
}

.books-list-wrap::-webkit-scrollbar-thumb:hover {
  background-color: #bfbfbf;
}

/* 详情弹窗样式 */
.book-detail-dialog >>> .el-dialog {
  border-radius: 12px;
  overflow: hidden;
  margin-top: 8vh !important;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
}

.book-detail-dialog >>> .el-dialog__header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.book-detail-dialog >>> .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.book-detail-dialog >>> .el-dialog__body {
  padding: 0;
  overflow-y: auto;
  flex: 1;
  min-height: 0;
}

.book-detail-dialog >>> .el-dialog__footer {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.book-dialog-body {
  padding: 0;
}

.dialog-content {
  padding: 24px;
}

/* 弹窗内容区域滚动条美化 */
.book-detail-dialog >>> .el-dialog__body::-webkit-scrollbar {
  width: 6px;
}

.book-detail-dialog >>> .el-dialog__body::-webkit-scrollbar-track {
  background-color: #f5f5f5;
  border-radius: 3px;
}

.book-detail-dialog >>> .el-dialog__body::-webkit-scrollbar-thumb {
  background-color: #dcdcdc;
  border-radius: 3px;
  transition: background-color 0.3s ease;
}

.book-detail-dialog >>> .el-dialog__body::-webkit-scrollbar-thumb:hover {
  background-color: #bfbfbf;
}

.dialog-cover-top {
  margin-bottom: 20px;
}

.dialog-layout {
  display: flex;
  gap: 24px;
  align-items: center;
}

.dialog-cover {
  flex-shrink: 0;
  width: 180px;
}

.cover-image {
  width: 100%;
  height: auto;
  object-fit: cover;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.cover-image.cover-16-9 {
  aspect-ratio: 16 / 9;
}

.dialog-info {
  flex: 1;
  min-width: 0;
}

.dialog-info-full {
  width: 100%;
}

.dialog-title {
  margin: 0 0 20px 0;
  font-size: 30px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.4;
}

.dialog-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #555;
}

.meta-item i {
  font-size: 16px;
  color: #409eff;
}

.dialog-abstract {
  margin-top: 20px;
}

.abstract-title {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.abstract-content {
  margin: 0;
  font-size: 14px;
  line-height: 1.8;
  color: #666;
  text-align: justify;
  white-space: pre-wrap;
  word-break: break-word;
}

/* AI热点分析样式 */
.ai-hotspot-section {
  margin-top: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
  border-radius: 12px;
  border: 1px solid #7dd3fc;
  box-shadow: 0 2px 8px rgba(56, 189, 248, 0.1);
}

.ai-hotspot-title {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: #0369a1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.ai-hotspot-title i {
  font-size: 20px;
  color: #0ea5e9;
}

.hotspot-loading {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  color: #0369a1;
  font-size: 14px;
}

.hotspot-loading i {
  font-size: 20px;
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.hotspot-content {
  padding: 16px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.8;
  color: #1e293b;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  text-align: left;
  text-indent: 2em;
}

.hotspot-content >>> strong {
  color: #0369a1;
  font-weight: 600;
}

.hotspot-error {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  color: #94a3b8;
  font-size: 14px;
}

.hotspot-error i {
  font-size: 18px;
}

/* AI解读赛事样式 */
.ai-analysis-section {
  margin-top: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border-radius: 12px;
  border: 1px solid #86efac;
  box-shadow: 0 2px 8px rgba(34, 197, 94, 0.1);
}

.ai-analysis-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #15803d;
  display: flex;
  align-items: center;
  gap: 8px;
}

.ai-analysis-title i {
  font-size: 20px;
  color: #22c55e;
}

.ai-analysis-desc {
  margin: 0 0 16px 0;
  font-size: 13px;
  color: #166534;
}

.ai-questions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.ai-question-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  padding: 12px 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #333;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(34, 197, 94, 0.2);
}

.ai-question-card i {
  font-size: 16px;
  color: #22c55e;
  flex-shrink: 0;
}

.ai-question-card span {
  flex: 1;
  line-height: 1.5;
}

.ai-question-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.15);
  background: #fff;
  border-color: #22c55e;
}

.ai-question-card:active {
  transform: translateY(0);
}

.dialog-footer {
  padding: 12px 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.close-btn {
  padding: 8px 20px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .book-detail-dialog >>> .el-dialog {
    width: 90% !important;
  }

  .dialog-layout {
    flex-direction: column;
    gap: 16px;
  }

  .dialog-cover {
    width: 100%;
  }

  .cover-image {
    height: 300px;
  }

  .dialog-footer-nav {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .close-btn {
    width: auto;
  }
  .dialog-content {
    padding: 20px;
  }

  .dialog-title {
    font-size: 18px;
  }

  .meta-item {
    font-size: 14px;
  }

  .abstract-content {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .book-detail-dialog >>> .el-dialog {
    width: 95% !important;
  }

  .dialog-content {
    padding: 16px;
  }
}
</style>
