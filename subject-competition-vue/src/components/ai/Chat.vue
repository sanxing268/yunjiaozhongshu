<!-- 小灵智能助手专属页面 -->
<template>
  <div class="ai-page-layout">
    <!-- 左侧边栏 -->
    <div class="ai-sidebar">
      <div class="sidebar-top">
        <h2 class="sidebar-title">小灵</h2>
        <button class="new-chat-btn" @click="createNewChat">创建新对话</button>
        <div class="sidebar-menu">
          <div class="menu-item" :class="{ active: currentTab === 'history' }" @click="currentTab = 'history'">
            <i class="el-icon-time"></i>
            <span>历史对话</span>
          </div>
          <!-- <div class="menu-item" :class="{ active: currentTab === 'daily' }" @click="currentTab = 'daily'">
            <i class="el-icon-message"></i>
            <span>每日推送</span>
          </div> -->
        </div>
        <!-- 历史对话列表 -->
        <div v-if="currentTab === 'history'" class="history-list">
          <div v-for="(item, index) in chatSessions" :key="index" class="history-item"
            :class="{ active: currentSessionIndex === index }" @click="switchSession(index)">
            <span class="history-text">{{ item.title || '新对话' }}</span>
          </div>
          <div v-if="chatSessions.length === 0" class="history-empty">暂无历史对话</div>
        </div>
        <!-- 每日推送 -->
        <!-- <div v-if="currentTab === 'daily'" class="daily-section">
          <div class="daily-empty">暂无推送内容</div>
        </div> -->
      </div>
      <!-- <div class="sidebar-bottom">
      
        <span class="setting-link" @click="$message.info('设置功能开发中')">设置</span>
      </div> -->
    </div>

    <!-- 主聊天区域 -->
    <div class="ai-main">
      <!-- 空状态：品牌展示 -->
      <div class="brand-section" v-if="messages.length === 0 && !isLoadingHistory">
        <div class="brand-icon">
          <!-- 空对话时，默认设置此ai的图标 -->
          <svg class="ai-logo" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
            <path d="M 30 80 Q 30 30 60 30 Q 90 30 90 80" stroke="url(#greenGradient)" stroke-width="12"
              stroke-linecap="round" fill="none" />
            <circle cx="95" cy="35" r="8" fill="url(#greenGradient)" />
            <defs>
              <linearGradient id="greenGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#52c41a;stop-opacity:1" />
                <stop offset="100%" style="stop-color:#73d13d;stop-opacity:1" />
              </linearGradient>
            </defs>
          </svg>
        </div>
        <!-- 下面编写一些文字 -->
        <h1 class="brand-title">小灵AI</h1>
        <p class="brand-subtitle">智能助手，为您提供专业的竞赛咨询服务</p>
      </div>

      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messagesContainer" v-if="messages.length > 0">
        <div v-for="(message, index) in messages" :key="index" :class="['message-item', message.type]">
          <div class="message-avatar">
            <i v-if="message.type === 'user'" class="el-icon-user"></i>
            <svg v-else class="ai-avatar-logo" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
              <path d="M 30 80 Q 30 30 60 30 Q 90 30 90 80" stroke="#ffffff" stroke-width="12" stroke-linecap="round"
                fill="none" />
              <circle cx="95" cy="35" r="8" fill="#ffffff" />
            </svg>
          </div>
          <div class="message-content">
            <div class="message-meta" :class="message.type">
              <span class="message-time">{{ message.time }}</span>
            </div>
            <!-- 思考过程 -->
            <div v-if="message.type === 'ai' && message.thinkingProcess" class="thinking-section">
              <div class="thinking-header" @click="message.thinkingVisible = !message.thinkingVisible">
                <span class="thinking-toggle">{{ message.thinkingVisible ? '▼' : '▶' }}</span>
                <span class="thinking-label">思考过程</span>
              </div>
              <transition name="thinking-expand">
                <div v-show="message.thinkingVisible" class="thinking-content">
                  <pre>{{ message.thinkingProcess }}</pre>
                </div>
              </transition>
            </div>
            <!-- 最终内容 -->
            <div class="message-text" v-if="message.content && message.content.trim()">
              <div v-html="formatMessage(message.content)"></div>
            </div>
            <div v-else-if="message.type === 'ai' && isTyping && index === messages.length - 1"
              class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部输入区域 -->
      <div class="chat-input-wrapper">
        <div class="input-container">
          <div class="input-wrapper">
            <!-- 深度思考开关 -->
            <div class="thinking-toggle-wrapper">
              <el-switch v-model="enableThinking" active-text="思考" inactive-text="快速" :disabled="isTyping"></el-switch>
            </div>
            <el-input v-model="inputMessage" type="textarea" :rows="3" :autosize="{ minRows: 3, maxRows: 8 }"
              placeholder="输入你的问题, Enter发送, Shift + Enter 换行" @keydown.native="handleKeyDown" :disabled="isTyping"
              class="chat-input">
            </el-input>
            <!-- 点击发送 -->
            <button class="send-circle" :class="{ disabled: !inputMessage.trim() || isTyping, sending: sendAnimating }"
              @click="onSendClick">
              <i class="el-icon-top"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AIChat',
  data() {
    return {
      messages: [],
      inputMessage: '',
      isTyping: false,
      sendAnimating: false,
      enableThinking: true,
      chatHistory: [],
      userId: null,
      isLoadingHistory: false,
      currentTab: 'history',
      chatSessions: [],
      currentSessionIndex: -1
    }
  },
  mounted() {
    document.body.classList.add('ai-chat-page')
    document.documentElement.classList.add('ai-chat-page')
    this.initializeUser()
    this.scrollToBottom()
    this.checkAndSendQuestion()
  },
  beforeDestroy() {
    document.body.classList.remove('ai-chat-page')
    document.documentElement.classList.remove('ai-chat-page')
  },
  methods: {
    createNewChat() {
      // 只有当当前是新会话（未保存到chatSessions）且有消息时，才添加到chatSessions
      if (this.currentSessionIndex === -1 && this.messages.length > 0) {
        const firstUserMsg = this.messages.find(m => m.type === 'user')
        this.chatSessions.unshift({
          title: firstUserMsg ? firstUserMsg.content.substring(0, 20) : '新对话',
          messages: [...this.messages]
        })
      }
      this.messages = []
      this.inputMessage = ''
      this.isTyping = false
      this.currentSessionIndex = -1
    },
    // 点击历史对话列表下的内容，跳转此条聊天的内容
    switchSession(index) {
      if (this.currentSessionIndex >= 0 && this.messages.length > 0) {
        // 有内容
        this.chatSessions[this.currentSessionIndex].messages = [...this.messages]
      } else if (this.currentSessionIndex === -1 && this.messages.length > 0) {
        // 无内容
        const firstUserMsg = this.messages.find(m => m.type === 'user')
        this.chatSessions.unshift({
          title: firstUserMsg ? firstUserMsg.content.substring(0, 20) : '新对话',
          messages: [...this.messages]
        })
        index += 1
      }
      this.currentSessionIndex = index
      this.messages = [...this.chatSessions[index].messages]
      this.scrollToBottom()
    },
    // 初始化用户
    initializeUser() {
      try {
        let user = this.$store.state.user
        if (!user) {
          const userStr = localStorage.getItem('user')
          if (userStr) {
            try {
              user = JSON.parse(userStr)
            } catch (e) {
              console.warn('解析localStorage中的user失败:', e)
            }
          }
        }
        if (user && user.id) {
          this.userId = user.id
          this.loadChatHistory()
        }
      } catch (error) {
        console.error('初始化用户失败:', error)
      }
    },
    // 加载对话历史
    loadChatHistory() {
      if (!this.userId) return
      this.isLoadingHistory = true
      this.$axios.get(`/ai/history/user/${this.userId}`, {
        // 写定默认获取50条历史记录
        // 对于后端，若前端没有写定，设置为50
        params: { limit: 50 }
      }).then(response => {
        if (response.data.code === 200) {
          let histories = response.data.data
          if (histories && typeof histories === 'object' && histories.data) {
            histories = histories.data
          }
          if (!Array.isArray(histories)) {
            this.isLoadingHistory = false
            return
          }
          // 清空当前的会话列表
          this.chatSessions = []
          // 按时间倒序排列，最新的在前面
          histories.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
          // 处理历史记录，每一条记录作为一个会话
          histories.forEach(record => {
            const session = {
              title: record.userMessage.substring(0, 20) || '新对话',
              messages: [
                {
                  type: 'user',
                  content: record.userMessage,
                  time: this.formatTime(new Date(record.createTime))
                },
                {
                  type: 'ai',
                  content: record.assistantMessage,
                  thinkingProcess: record.thinkingProcess || '',
                  thinkingVisible: false,
                  time: this.formatTime(new Date(record.createTime)),
                  isStreaming: false
                }
              ]
            }
            this.chatSessions.push(session)
          })
        }
      }).catch(error => {
        console.error('加载聊天历史失败:', error)
      }).finally(() => {
        this.isLoadingHistory = false
      })
    },
    // 检查并发送问题
    checkAndSendQuestion() {
      const question = this.$route.query.question
      if (question) {
        this.$router.replace({
          path: this.$route.path,
          query: {}
        })
        this.$nextTick(() => {
          setTimeout(() => {
            this.inputMessage = question
            this.sendMessage()
          }, 500)
        })
      }
    },
    // 点击发送按钮
    onSendClick() {
      if (!this.inputMessage.trim() || this.isTyping || this.sendAnimating) return
      this.sendAnimating = true
      this.sendMessage()
      setTimeout(() => { this.sendAnimating = false }, 500)
    },
    // 执行发送问题逻辑
    sendMessage() {
      if (!this.inputMessage.trim() || this.isTyping) return
      const userMessage = {
        type: 'user',
        content: this.inputMessage.trim(),
        time: this.formatTime(new Date())
      }
      this.messages.push(userMessage)
      const userInput = this.inputMessage.trim()
      this.inputMessage = ''
      this.scrollToBottom()
      this.isTyping = true
      const messageList = []
      this.messages.forEach(msg => {
        if (msg.type === 'user') {
          messageList.push({ role: 'user', content: msg.content })
        } else if (msg.type === 'ai') {
          messageList.push({ role: 'assistant', content: msg.content })
        }
      })
      messageList.push({ role: 'user', content: userInput })
      const aiMessage = {
        type: 'ai',
        content: '',
        thinkingProcess: '',
        thinkingVisible: true,
        time: this.formatTime(new Date()),
        isStreaming: true
      }
      this.messages.push(aiMessage)
      // 添加一个标志，防止重复保存聊天记录
      let hasSaved = false
      // 调用后端的流式聊天接口
      const apiUrl = this.$axios.defaults.baseURL + '/ai/chat/stream'
      // 请求数据的参数设置
      const requestData = {
        messages: messageList,
        model: 'qwen-plus-latest',
        temperature: 0.7,
        topP: 0.8,
        enableSearch: false,
        enableThinking: this.enableThinking,
        thinkingBudget: 4000,
        stream: true
      }
      fetch(apiUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestData),
        credentials: 'include'
      })
        .then(response => {
          const contentType = response.headers.get('content-type')
          if (!response.ok) {
            if (contentType && contentType.includes('application/json')) {
              return response.json().then(errorData => {
                throw new Error(errorData.message || errorData.error || `网络请求失败: ${response.status}`)
              })
            } else {
              return response.text().then(text => {
                throw new Error(`网络请求失败: ${response.status} ${response.statusText}`)
              })
            }
          }
          if (!contentType || !contentType.includes('text/event-stream')) {
            return response.text().then(text => {
              try {
                const errorData = JSON.parse(text)
                if (errorData.code !== 200) throw new Error(errorData.message || '请求失败')
                throw new Error('响应格式错误: ' + text)
              } catch (e) {
                throw new Error('响应格式错误: ' + text)
              }
            })
          }
          // 拿到响应
          const reader = response.body.getReader()
          const decoder = new TextDecoder()
          let buffer = ''
          // 解析
          const readChunk = () => {
            return reader.read().then(({ done, value }) => {
              if (done) {
                this.isTyping = false
                aiMessage.isStreaming = false
                this.scrollToBottom()
                return
              }
              buffer += decoder.decode(value, { stream: true })
              const lines = buffer.split('\n')
              buffer = lines.pop() || ''
              lines.forEach(line => {
                if (line.startsWith('data: ')) {
                  const data = line.substring(6).trim()
                  if (data === '' || data === '[DONE]') {
                    this.isTyping = false
                    aiMessage.isStreaming = false
                    this.scrollToBottom()
                    return
                  }
                  try {
                    const json = JSON.parse(data)
                    if (json.type === 'content' && json.content) {
                      aiMessage.content += json.content
                      // 保持流式状态为true，直到完成
                      aiMessage.isStreaming = true
                      this.scrollToBottom()
                    } else if (json.type === 'thinking' && json.content) {
                      aiMessage.thinkingProcess += json.content
                      this.scrollToBottom()
                    } else if (json.type === 'done' && !hasSaved) {
                      this.isTyping = false
                      aiMessage.isStreaming = false
                      if (aiMessage.thinkingProcess) {
                        setTimeout(() => { aiMessage.thinkingVisible = false }, 2000)
                      }
                      // 保存聊天信息 此时的逻辑是发送问题就拿到响应就保存到数据库
                      this.saveChatHistory(userInput, aiMessage.content, aiMessage.thinkingProcess)
                      hasSaved = true
                      this.scrollToBottom()
                    } else if (json.error) {
                      throw new Error(json.error)
                    }
                  } catch (e) {
                    if (data && !data.startsWith('{')) {
                      console.warn('无法解析的SSE数据:', data)
                    }
                  }
                }
              })
              return readChunk()
            })
          }
          return readChunk()
        })
        .catch(error => {
          console.error('AI请求失败:', error)
          this.isTyping = false
          aiMessage.content = '抱歉，AI服务暂时不可用：' + (error.message || '未知错误') + '。请稍后再试。'
          this.scrollToBottom()
        })
    },
    handleKeyDown(event) {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        this.onSendClick()
      }
    },
    formatTime(date) {
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      const hours = date.getHours().toString().padStart(2, '0')
      const minutes = date.getMinutes().toString().padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    // 保存聊天记录
    saveChatHistory(userMessage, assistantMessage, thinkingProcess) {
      try {
        let user = this.$store.state.user
        if (!user) {
          const userStr = localStorage.getItem('user')
          user = userStr ? JSON.parse(userStr) : null
        }
        if (!user || !user.id) return
        // 保存双方的聊天信息
        this.$axios.post('/ai/history/save', {
          userId: user.id,
          userMessage: userMessage,
          assistantMessage: assistantMessage,
          thinkingProcess: thinkingProcess || ''
        }).then(response => {
          if (response.data.code === 200) {
            console.log('聊天记录保存成功')
            // 检查当前是否是新会话（未保存到chatSessions）
            if (this.currentSessionIndex === -1 && this.messages.length > 0) {
              // 创建新的会话对象
              const session = {
                title: userMessage.substring(0, 20) || '新对话',
                messages: [...this.messages]
              }
              // 将新会话添加到chatSessions数组的开头
              this.chatSessions.unshift(session)
              // 更新当前会话索引
              this.currentSessionIndex = 0
            }
          }
        }).catch(error => {
          console.error('保存聊天记录失败:', error)
        })
      } catch (error) {
        console.error('保存聊天记录异常:', error)
      }
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },
    formatMessage(text) {
      if (!text) return ''
      const codeBlocks = []
      let codeBlockIndex = 0
      let formatted = text.replace(/```([\s\S]*?)```/g, (match, code) => {
        const placeholder = `__CODE_BLOCK_${codeBlockIndex}__`
        codeBlocks[codeBlockIndex] = code
        codeBlockIndex++
        return placeholder
      })
      formatted = formatted
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
      formatted = formatted.replace(/`([^`\n]+)`/g, '<code class="inline-code">$1</code>')
      formatted = formatted.replace(/\n/g, '<br>')
      formatted = formatted.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
      // eslint-disable-next-line no-useless-escape
      formatted = formatted.replace(/(https?:\/\/[a-zA-Z0-9\-._~:/?#\[\]@!$&'()*+,;=%.]+)/g, '<a href="$1" target="_blank" rel="noopener">$1</a>')
      codeBlocks.forEach((code, index) => {
        const escapedCode = code
          .replace(/&/g, '&amp;')
          .replace(/</g, '&lt;')
          .replace(/>/g, '&gt;')
        formatted = formatted.replace(`__CODE_BLOCK_${index}__`, `<pre class="code-block"><code>${escapedCode}</code></pre>`)
      })
      return formatted
    }
  }
}
</script>

<style>
/* 全局样式：隐藏AI聊天页面的body滚动条 */
body.ai-chat-page,
html.ai-chat-page {
  overflow: hidden !important;
  height: 100%;
}

body.ai-chat-page::-webkit-scrollbar,
html.ai-chat-page::-webkit-scrollbar {
  width: 0 !important;
  display: none !important;
}

body.ai-chat-page {
  scrollbar-width: none !important;
  -ms-overflow-style: none !important;
}
</style>

<style scoped>
/* 整体布局：左侧栏 + 右侧主区域 */
.ai-page-layout {
  display: flex;
  height: calc(100dvh - 60px);
  overflow: hidden;
}

/* ========== 左侧边栏 ========== */
.ai-sidebar {
  width: clamp(260px, 28vw, 350px);
  background: linear-gradient(180deg, #e8f4fc 0%, #f0f8ff 100%);
  border-right: 1px solid #dceaf5;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-top {
  flex: 1;
  padding: 24px 16px 16px;
  overflow-y: auto;
}

.sidebar-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0 0 20px 0;
  text-align: center;
}

.new-chat-btn {
  width: 100%;
  padding: 10px 0;
  border: none;
  border-radius: 20px;
  background: #ffffff;
  color: #333;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  margin-bottom: 20px;
}

.new-chat-btn:hover {
  background: #f0f8ff;
  box-shadow: 0 4px 12px rgba(0, 140, 210, 0.12);
}

.sidebar-menu {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 16px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 8px;
  font-size: 14px;
  color: #555;
  cursor: pointer;
  transition: all 0.2s;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.6);
  color: #1890ff;
}

.menu-item.active {
  background: rgba(255, 255, 255, 0.8);
  color: #1890ff;
  font-weight: 500;
}

/* 历史对话列表 */
.history-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.history-item {
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.history-item:hover {
  background: rgba(255, 255, 255, 0.6);
  color: #333;
}

.history-item.active {
  background: rgba(255, 255, 255, 0.8);
  color: #1890ff;
}

.history-empty,
.daily-empty {
  text-align: center;
  color: #999;
  font-size: 13px;
  padding: 20px 0;
}

.sidebar-bottom {
  padding: 16px;
  border-top: 1px solid #dceaf5;
}

.setting-link {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

.setting-link:hover {
  color: #1890ff;
}

/* ========== 主聊天区域 ========== */
.ai-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #dceefb 0%, #eaf5ff 40%, #f5fbff 100%);
  overflow: hidden;
  position: relative;
}

/* 品牌区域 */
.brand-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  padding: 80px 20px;
}

.brand-icon {
  margin-bottom: 24px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-10px);
  }
}

.ai-logo {
  width: 100px;
  height: 100px;
  filter: drop-shadow(0 4px 12px rgba(82, 196, 26, 0.2));
}

.brand-title {
  font-size: 32px;
  font-weight: 600;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 12px 0;
}

.brand-subtitle {
  font-size: 15px;
  color: #8c8c8c;
  margin: 0;
}

/* 聊天消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 30px 40px 20px;
  display: flex;
  flex-direction: column;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
  animation: fadeIn 0.3s ease-in;
  max-width: 75%;
}

.message-item.user {
  flex-direction: row-reverse;
  align-self: flex-end;
}

.message-item.ai {
  align-self: flex-start;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.ai-avatar-logo {
  width: 20px;
  height: 20px;
}

.message-item.user .message-avatar {
  background: linear-gradient(135deg, #7dd3fc 0%, #38bdf8 100%);
  color: white;
  margin-left: 10px;
}

.message-item.ai .message-avatar {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
  margin-right: 10px;
}

.message-content {
  max-width: 100%;
  display: flex;
  flex-direction: column;
}

.message-item.ai .message-content {
  align-items: flex-start;
}

.message-item.user .message-content {
  align-items: flex-end;
}

.message-text {
  padding: 12px 16px;
  border-radius: 16px;
  word-wrap: break-word;
  word-break: break-word;
  line-height: 1.7;
  font-size: 14px;
  white-space: pre-wrap;
  text-align: left;
}

.message-item.user .message-text {
  background: linear-gradient(135deg, #7dd3fc 0%, #38bdf8 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-item.ai .message-text {
  background: white;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.message-meta {
  display: flex;
  margin-bottom: 4px;
  font-size: 12px;
  color: #999;
  padding: 0 4px;
}

.message-meta.ai {
  justify-content: flex-start;
}

.message-meta.user {
  justify-content: flex-end;
}

/* 思考过程 */
.thinking-section {
  margin-bottom: 8px;
  background: #fafafa;
  border-left: 3px solid #faad14;
  border-radius: 4px;
  overflow: hidden;
}

.thinking-header {
  padding: 8px 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: #666;
  user-select: none;
  font-size: 13px;
}

.thinking-header:hover {
  background: #f5f5f5;
}

.thinking-toggle {
  font-size: 11px;
}

.thinking-label {
  font-size: 13px;
}

.thinking-content {
  padding: 8px 12px;
  background: white;
  border-top: 1px solid #f0f0f0;
}

.thinking-content pre {
  margin: 0;
  font-size: 12px;
  line-height: 1.6;
  color: #555;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Monaco', 'Courier New', monospace;
  text-align: left;
}

.thinking-expand-enter-active,
.thinking-expand-leave-active {
  transition: all 0.3s ease;
}

.thinking-expand-enter,
.thinking-expand-leave-to {
  opacity: 0;
  max-height: 0;
}

/* Markdown样式 */
.message-text>>>p {
  margin: 0.4em 0;
}

.message-text>>>p:first-child {
  margin-top: 0;
}

.message-text>>>p:last-child {
  margin-bottom: 0;
}

.message-text>>>.code-block {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 6px;
  padding: 10px;
  margin: 6px 0;
  overflow-x: auto;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
}

.message-item.ai .message-text>>>.code-block {
  background: #f5f5f5;
}

.message-text>>>.inline-code {
  background: rgba(0, 0, 0, 0.06);
  padding: 2px 5px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
}

.message-text>>>strong {
  font-weight: 600;
}

.message-text>>>a {
  color: #1890ff;
  text-decoration: none;
}

.message-text>>>a:hover {
  text-decoration: underline;
}

.message-time {
  line-height: 1.2;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  align-items: center;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #7dd3fc;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {

  0%,
  60%,
  100% {
    transform: translateY(0);
    opacity: 0.7;
  }

  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

/* ========== 输入区域 ========== */
.chat-input-wrapper {
  padding: 20px 40px 30px;
  display: flex;
  justify-content: center;
}

.input-container {
  max-width: 700px;
  width: 100%;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: flex-end;
  gap: 10px;
}

.thinking-toggle-wrapper {
  display: flex;
  align-items: center;
  padding-bottom: 14px;
  flex-shrink: 0;
}

.chat-input {
  flex: 1;
}

.chat-input>>>.el-textarea__inner {
  border: 1.5px solid #d4edff;
  border-radius: 16px;
  padding: 14px 18px;
  font-size: 14px;
  resize: none;
  background: #ffffff;
  transition: all 0.3s;
}

.chat-input>>>.el-textarea__inner:focus {
  border-color: #7dd3fc;
  box-shadow: 0 0 0 2px rgba(125, 211, 252, 0.2);
}

.send-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #7dd3fc 0%, #38bdf8 100%);
  color: white;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s;
  margin-bottom: 4px;
}

.send-circle:hover:not(.disabled) {
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.4);
  transform: translateY(-1px);
}

.send-circle.disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.send-circle.sending {
  animation: plane-fly 0.5s ease-out;
}

@keyframes plane-fly {
  0% {
    transform: translate(0, 0) scale(1);
    opacity: 1;
  }

  60% {
    transform: translate(10px, -10px) scale(1.05);
    opacity: 0.8;
  }

  100% {
    transform: translate(16px, -16px) scale(0.4);
    opacity: 0;
  }
}

/* 滚动条 */
.chat-messages::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #d0e8f5;
  border-radius: 2px;
}

.sidebar-top::-webkit-scrollbar {
  width: 4px;
}

.sidebar-top::-webkit-scrollbar-thumb {
  background: #d0e8f5;
  border-radius: 2px;
}

/* 响应式 */
@media (max-width: 768px) {
  .ai-sidebar {
    width: 160px;
  }

  .chat-messages {
    padding: 20px 16px;
  }

  .chat-input-wrapper {
    padding: 16px 16px 20px;
  }

  .message-item {
    max-width: 90%;
  }
}

@media (max-width: 640px) {
  .ai-sidebar {
    display: none;
  }

  .chat-input-wrapper {
    padding: 12px 10px 16px;
  }
}
</style>
