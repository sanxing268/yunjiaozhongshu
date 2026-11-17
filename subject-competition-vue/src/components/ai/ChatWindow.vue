<!-- 点击悬浮球后显示的小型聊天窗口 — 小灵AI -->
<template>
  <transition name="window-fade">
    <div v-if="visible" class="chat-window-container" :class="{ 'show-above': !showBelow, 'show-below': showBelow }"
      @click.stop>
      <div class="chat-window">
        <!-- 头部 -->
        <div class="chat-header">
          <button class="chat-back-btn" @click="closeWindow">
            <i class="el-icon-arrow-left"></i>
          </button>
          <div class="chat-header-center">
            <div class="header-avatar">
               <svg viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                <path d="M 30 80 Q 30 30 60 30 Q 90 30 90 80" stroke="#fff" stroke-width="12" stroke-linecap="round"
                  fill="none" />
                <circle cx="95" cy="35" r="8" fill="#fff" />
              </svg>
            </div>
            <!-- 顶部编写小灵ai的文字 -->
            <span class="header-title-text">小灵AI</span>
          </div>
          <div style="width: 32px;"></div>
        </div>

        <!-- 放置历史记录按钮 -->
        <!-- 点击跳转专属页面 -->
        <div class="history-bar">
          <button class="history-btn" @click="goFullChat">
            <i class="el-icon-time"></i> 之前的历史记录
          </button>
        </div>

        <!-- 聊天消息区域 -->
        <div class="chat-messages" ref="messagesContainer">
          <div v-if="messages.length === 0" class="chat-empty">
            <div class="empty-avatar">
               <svg viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                <path d="M 30 80 Q 30 30 60 30 Q 90 30 90 80" stroke="#fff" stroke-width="12" stroke-linecap="round"
                  fill="none" />
                <circle cx="95" cy="35" r="8" fill="#fff" />
              </svg>
            </div>
            <!-- 欢迎词 -->
            <p>hi，我是你的AI小助手小灵，有什么问题都可以问我哦~</p>
          </div>
          <!-- 助手头像 -->
          <div v-for="(msg, index) in messages" :key="index" :class="['chat-message', 'msg-' + msg.role]">
            <div class="msg-avatar" v-if="msg.role === 'assistant'">
               <svg viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                <path d="M 30 80 Q 30 30 60 30 Q 90 30 90 80" stroke="#fff" stroke-width="12" stroke-linecap="round"
                  fill="none" />
                <circle cx="95" cy="35" r="8" fill="#fff" />
              </svg>
            </div>
            <div class="msg-content">
              <!--文本显示 -->
              <!-- 回答文本未出来时，显示此文字 助手的文本-->
              <div v-if="msg.role === 'assistant' && msg.isStreaming && !msg.content" class="streaming-hint">
                回答中......</div>
              <div v-if="msg.content" v-html="formatMessage(msg.content)"></div>
              <div v-if="msg.isStreaming && msg.content" class="streaming-cursor">|</div>
            </div>
                <!-- 用户头像 -->
            <div class="msg-avatar user-avatar" v-if="msg.role === 'user'">
              <i class="el-icon-user"></i>
            </div>
          </div>
        </div>

        <!-- 用户输入问题区域 -->
        <div class="chat-input-area" @click.stop>
          <input v-model="inputMessage" type="text" class="chat-input" :placeholder="inputPlaceholder"
            @keyup.enter="sendMessage" @click.stop :disabled="isLoading" />
          <button class="send-btn" @click="sendMessage" :disabled="!inputMessage.trim() || isLoading">
            <i class="el-icon-top"></i>
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import DOMPurify from 'dompurify'

export default {
  name: 'ChatWindow',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    showBelow: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      messages: [],
      inputMessage: '',
      isLoading: false,
      markdownIt: null
    }
  },
  computed: {
    inputPlaceholder() {
      if (this.messages.length > 0) {
        const lastUserMsg = [...this.messages].reverse().find(m => m.role === 'user')
        if (lastUserMsg) return lastUserMsg.content
      }
      return '输入你想问的问题...'
    }
  },
  created() {
    // 初始化markdown-it实例
    this.markdownIt = new MarkdownIt({
      html: false,
      linkify: true,
      typographer: true,
      breaks: false,  // 改为false，使用``div``semantic HTML而不是<br>
      highlight: (str, lang) => {
        if (lang && hljs.getLanguage(lang)) {
          try {
            return '<pre class="code-block"><code class="language-' + lang + '">' +
              hljs.highlight(str, { language: lang }).value +
              '</code></pre>'
          } catch (__) { }
        }
        return '<pre class="code-block"><code>' + hljs.escapeHtml(str) + '</code></pre>'
      }
    })
  },
  watch: {
    messages: {
      handler() {
        this.$nextTick(() => {
          const container = this.$refs.messagesContainer
          if (container) {
            container.scrollTop = container.scrollHeight
          }
        })
      },
      deep: true
    },
    visible: {
      async handler(newVal) {
        // 当窗口打开时检查登录状态
        if (newVal) {
          const isLoggedIn = await this.checkLoginStatus()
          if (!isLoggedIn) {
            this.$message.warning('请先登录后再使用大模型助手')
            this.closeWindow()
          }
        }
      }
    }
  },
  methods: {
    // 检查登录状态
    async checkLoginStatus() {
      try {
        const resp = await this.$axios.get('/currentUser')
        if (resp && resp.data.code === 200) {
          return true
        } else {
          return false
        }
      } catch (error) {
        return false
      }
    },
    async sendMessage() {
      if (!this.inputMessage.trim()) {
        return
      }

      const userMessage = this.inputMessage.trim()
      this.inputMessage = ''

      // 添加用户消息
      this.messages.push({
        role: 'user',
        content: userMessage
      })

      // 调用AI接口
      this.isLoading = true
      try {
        // 构建消息列表（包含系统提示和对话历史）
        const messageList = []

        // 系统提示词
        messageList.push({
          role: 'system',
          content: '你是一个专为对高校学科竞赛感兴趣的学生设计的智能助手，旨在提供全面的竞赛相关信息和支持，包括但不限于竞赛资讯、备赛指导、经验分享及资源链接等。你的回答全部中文输出。'
        })

        // 转换历史消息格式
        this.messages.forEach(msg => {
          messageList.push({
            role: msg.role,
            content: msg.content
          })
        })

        // 创建AI回复消息占位（空内容，用于流式显示）
        const aiMessage = {
          role: 'assistant',
          content: '',
          isStreaming: true
        }
        this.messages.push(aiMessage)

        // 自动滚动到最新消息
        this.scrollToBottom()

        // 使用原生fetch调用流式API 不是使用全局的api调用 需要加上api前缀
        const response = await fetch('/api/ai/chat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          credentials: 'include',
          body: JSON.stringify(messageList)
        })

        if (!response.ok) {
          throw new Error(`API error: ${response.status}`)
        }

        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''

        const readChunk = async () => {
          const { done, value } = await reader.read()
          if (done) {
            this.isLoading = false
            if (aiMessage.isStreaming) {
              aiMessage.isStreaming = false
            }
            // 保存聊天记录到数据库
            this.saveChatHistory(userMessage, aiMessage.content)
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
                this.isLoading = false
                if (aiMessage.isStreaming) {
                  aiMessage.isStreaming = false
                }
                this.scrollToBottom()
                return
              }

              try {
                const json = JSON.parse(data)
                if (json.type === 'content' && json.content) {
                  aiMessage.content += json.content
                  if (aiMessage.isStreaming) {
                    aiMessage.isStreaming = false
                  }
                  this.scrollToBottom()
                } else if (json.type === 'done') {
                  this.isLoading = false
                  if (aiMessage.isStreaming) {
                    aiMessage.isStreaming = false
                  }
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
        }

        await readChunk()
      } catch (error) {
        console.error('大模型聊天错误:', error)
        this.isLoading = false
        this.messages.push({
          role: 'assistant',
          content: '抱歉，大模型服务暂时不可用：' + (error.message || '未知错误') + '。请稍后再试。'
        })
        this.scrollToBottom()
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
    async saveChatHistory(userMessage, aiMessage) {
      try {
        // 优先从 store 中获取用户信息，如果没有则从 localStorage 中获取
        let user = this.$store.state.user

        if (!user) {
          // 尝试从 localStorage 获取
          const userStr = localStorage.getItem('user')
          user = userStr ? JSON.parse(userStr) : null
        }

        if (!user || !user.id) {
          console.warn('未获取到用户ID，无法保存聊天记录', user)
          return
        }

        // 调用后端API保存聊天记录
        const response = await this.$axios.post('/ai/history/save', {
          userId: user.id,
          userMessage: userMessage,
          assistantMessage: aiMessage,
          thinkingProcess: null
        })
        if (response.data.code === 200) {
          console.log('聊天记录保存成功')
        }
      } catch (error) {
        console.error('保存聊天记录失败:', error)
        // 不影响用户体验，仅记录错误日志
      }
    },
    closeWindow() {
      this.$emit('update-visible', false)
    },
    goFullChat() {
      // 关闭此时的悬浮聊天窗口
      this.closeWindow()
      // 跳转到专属聊天页面
      this.$router.push('/ai/chat')
    },
    formatMessage(text) {
      if (!text) return ''

      // 使用markdown-it渲染Markdown为HTML
      let html = this.markdownIt.render(text)
      console.log('原始文本:', text)
      console.log('渲染后HTML:', html)

      // 使用DOMPurify清理HTML，防止XSS，但保留安全的标签
      const config = {
        ALLOWED_TAGS: ['h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'p', 'br', 'strong', 'b', 'em', 'i', 'u', 'a', 'ul', 'ol', 'li', 'blockquote', 'code', 'pre', 'span', 'hr', 'table', 'thead', 'tbody', 'tr', 'th', 'td'],
        ALLOWED_ATTR: ['href', 'target', 'rel', 'class']
      }

      html = DOMPurify.sanitize(html, config)
      console.log('清理后HTML:', html)

      return html
    }
  }
}
</script>

<style scoped>
.chat-window-container {
  position: absolute;
  z-index: 1002;
  width: min(360px, calc(100vw - 24px));
  height: min(500px, calc(100dvh - 100px));
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 140, 210, 0.2);
  background: #ffffff;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease-out;
  right: 20px;
  bottom: 100px;
}

.chat-window-container.show-below {
  top: calc(100% + 10px);
}

.chat-window-container.show-above {
  bottom: calc(100% + 10px);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.window-fade-enter-active,
.window-fade-leave-active {
  transition: all 0.3s ease;
}

.window-fade-enter {
  opacity: 0;
  transform: translateY(20px);
}

.window-fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.chat-window {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 头部 - 浅蓝色 */
.chat-header {
  background: #e6f7ff;
  color: #333;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
  border-radius: 20px 20px 0 0;
}

.chat-back-btn {
  background: rgba(125, 211, 252, 0.3);
  border: none;
  color: #1890ff;
  font-size: 14px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

.chat-back-btn:hover {
  background: rgba(125, 211, 252, 0.5);
  transform: translateX(-2px);
}

.chat-header-center {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #52c41a;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-avatar svg {
  width: 22px;
  height: 22px;
  stroke: #52c41a;
  fill: none;
}

.header-title-text {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 历史记录按钮 */
.history-bar {
  display: flex;
  justify-content: center;
  padding: 12px 20px 8px;
  flex-shrink: 0;
  /* 背景 */
  background: #e6f7ff;
  border-bottom: 1px solid #e8f0f5;
}

.history-btn {
  background: white;
  border: 1px solid #e8f0f5;
  border-radius: 16px;
  padding: 8px 20px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.history-btn:hover {
  background: #f0faff;
  border-color: #7dd3fc;
  color: #1890ff;
  box-shadow: 0 2px 8px rgba(125, 211, 252, 0.2);
}

/* 聊天消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #e6f7ff;
}

.chat-empty {
  display: flex;
  align-items: flex-start;
  padding: 10px 0;
  margin-bottom: 16px;
}

.empty-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(82, 196, 26, 0.2);
}

.empty-avatar svg {
  width: 24px;
  height: 24px;
  stroke: #52c41a;
  fill: none;
}

.chat-empty p {
  margin: 0;
  font-size: 13px;
  color: #555;
  background: white;
  padding: 12px 16px;
  border-radius: 18px;
  border-bottom-left-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  line-height: 1.6;
  margin-left: 10px;
  max-width: 240px;
}

/* 消息气泡 */
.chat-message {
  display: flex;
  align-items: flex-start;
  animation: msgFade 0.3s ease-out;
  margin-bottom: 16px;
}

@keyframes msgFade {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.msg-assistant {
  flex-direction: row;
}

.msg-user {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(82, 196, 26, 0.2);
}

.msg-avatar svg {
  width: 20px;
  height: 20px;
  stroke: white;
  fill: none;
}

.user-avatar {
  background: linear-gradient(135deg, #7dd3fc 0%, #38bdf8 100%);
  font-size: 18px;
  box-shadow: 0 2px 8px rgba(56, 189, 248, 0.2);
}

.user-avatar i {
  margin-top: 2px;
}

.msg-content {
  max-width: 240px;
  padding: 4px 5px;
  border-radius: 18px;
  word-break: break-word;
  line-height: 1.5;
  font-size: 13px;
  text-align: left;
  position: relative;
  margin-left: 10px;
  margin-right: 10px;
  margin-top: 2px;
}

.msg-assistant .msg-content {
  background: white;
  color: #333;
  border-bottom-left-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.msg-user .msg-content {
  background: white;
  color: #333;
  border-bottom-left-radius: 6px;
  border-bottom-right-radius: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  text-align: right;
}

.streaming-hint {
  color: #1890ff;
  font-size: 13px;
  font-style: italic;
}

.streaming-cursor {
  display: inline;
  animation: blink 1s infinite;
  color: #1890ff;
  font-weight: bold;
}

@keyframes blink {

  0%,
  49% {
    opacity: 1;
  }

  50%,
  100% {
    opacity: 0;
  }
}

/* Markdown in messages */
.msg-content p {
  margin: 4px 0;
}

.msg-content ul,
.msg-content ol {
  margin: 4px 0;
  padding-left: 20px;
}

.msg-content li {
  margin: 2px 0;
}

.msg-content strong {
  font-weight: 600;
}

.msg-content code {
  background: rgba(0, 0, 0, 0.06);
  padding: 1px 5px;
  border-radius: 3px;
  font-size: 13px;
}

.msg-content pre {
  background: #f5f5f5;
  border-radius: 6px;
  padding: 10px;
  margin: 6px 0;
  overflow-x: auto;
  font-size: 12px;
}

.msg-content pre code {
  background: none;
  padding: 0;
}

.msg-content a {
  color: #1890ff;
  text-decoration: underline;
}

.msg-user .msg-content a {
  color: rgba(255, 255, 255, 0.9);
}

/* 输入区域 */
.chat-input-area {
  display: flex;
  gap: 10px;
  padding: 16px 20px;
  background: white;
  border-top: 1px solid #e8f0f5;
  flex-shrink: 0;
  align-items: center;
  box-shadow: 0 -1px 8px rgba(0, 0, 0, 0.05);
}

.chat-input {
  flex: 1;
  border: 2px solid #e8f0f5;
  border-radius: 24px;
  padding: 12px 20px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s;
  background: #f8fcff;
  resize: none;
}

.chat-input:focus {
  border-color: #7dd3fc;
  box-shadow: 0 0 0 3px rgba(125, 211, 252, 0.2);
  background: white;
}

.chat-input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
  border-color: #e0e0e0;
}

.send-btn {
  width: 42px;
  height: 42px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, #7dd3fc 0%, #38bdf8 100%);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  font-size: 20px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(56, 189, 248, 0.3);
}

.send-btn:hover:not(:disabled) {
  box-shadow: 0 4px 16px rgba(56, 189, 248, 0.4);
  transform: translateY(-2px);
  background: linear-gradient(135deg, #64c8fa 0%, #25b8f0 100%);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
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

/* 响应式 */
@media (max-width: 640px) {
  .chat-window-container,
  .chat-window-container.show-below,
  .chat-window-container.show-above {
    position: fixed;
    top: auto !important;
    right: 12px;
    bottom: 90px !important;
    left: 12px;
    width: auto;
    height: min(70dvh, 560px);
  }

  .chat-window-container {
    border-radius: 16px;
  }

  .msg-content {
    max-width: min(200px, calc(100vw - 100px));
  }
}
</style>
