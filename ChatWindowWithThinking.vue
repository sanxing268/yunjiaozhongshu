<template>
  <!-- AI聊天窗口 - 支持思考过程显示 -->
  <div class="chat-window">
    <!-- 消息列表 -->
    <div class="message-list" ref="messageList">
      <div 
        v-for="msg in messages" 
        :key="msg.id" 
        :class="['message-item', msg.role]"
      >
        <!-- 用户消息 -->
        <div v-if="msg.role === 'user'" class="user-message">
          <div class="message-content">{{ msg.content }}</div>
          <div class="message-time">{{ formatTime(msg.createTime) }}</div>
        </div>

        <!-- AI消息（带思考过程支持） -->
        <div v-if="msg.role === 'assistant'" class="assistant-message">
          <!-- 思考过程部分 -->
          <div v-if="msg.thinkingProcess" class="thinking-wrapper">
            <div class="thinking-header" @click="toggleThinking(msg.id)">
              <span class="toggle-icon" :class="{ collapsed: msg.thinkingCollapsed }">
                ▼
              </span>
              <span class="thinking-label">🤔 思考过程</span>
              <span class="thinking-status">{{ msg.thinkingCollapsed ? '已折叠' : '展开中' }}</span>
            </div>
            
            <!-- 思考内容 -->
            <transition name="thinking-expand">
              <div v-show="!msg.thinkingCollapsed" class="thinking-content">
                <div class="thinking-text">
                  {{ msg.thinkingProcess }}
                </div>
                <div class="thinking-footer">
                  <span class="thinking-complete">✓ 思考完成</span>
                </div>
              </div>
            </transition>
          </div>

          <!-- 最终回复部分 -->
          <div class="response-wrapper">
            <div class="response-header">
              <span class="ai-badge">AI回复</span>
            </div>
            <div class="response-content">
              <div v-html="formatMessage(msg.content)"></div>
            </div>
            <div class="message-time">{{ formatTime(msg.createTime) }}</div>
          </div>

          <!-- 操作按钮 -->
          <div class="message-actions">
            <button class="action-btn" @click="copyMessage(msg.content)" title="复制回复">
              📋 复制
            </button>
            <button 
              v-if="msg.thinkingProcess" 
              class="action-btn" 
              @click="toggleThinking(msg.id)" 
              title="折叠/展开思考过程"
            >
              {{ msg.thinkingCollapsed ? '展开' : '折叠' }}思考
            </button>
            <button class="action-btn" @click="deleteMessage(msg.id)" title="删除消息">
              🗑️ 删除
            </button>
          </div>
        </div>
      </div>

      <!-- 加载指示器 -->
      <div v-if="isLoading" class="loading-indicator">
        <span class="spinner"></span>
        <span>AI思考中...</span>
      </div>
    </div>

    <!-- 输入框 -->
    <div class="input-area">
      <textarea 
        v-model="userInput"
        class="message-input"
        placeholder="输入您的问题..."
        @keyup.enter.ctrl="sendMessage"
        @keydown.tab.prevent="insertTab"
      ></textarea>
      <div class="input-footer">
        <span class="input-hint">Ctrl + Enter 发送消息</span>
        <button 
          class="send-btn" 
          @click="sendMessage"
          :disabled="!userInput.trim() || isLoading"
        >
          💬 发送
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatWindowWithThinking',
  data() {
    return {
      messages: [],
      userInput: '',
      isLoading: false,
      userId: null,
      messageIdCounter: 0
    }
  },
  computed: {
    conversationHistory() {
      return this.messages.map(msg => ({
        role: msg.role,
        content: msg.content,
        thinkingProcess: msg.thinkingProcess
      }))
    }
  },
  mounted() {
    this.userId = this.getCurrentUserId()
    this.loadChatHistory()
  },
  methods: {
    // 获取当前用户ID
    getCurrentUserId() {
      const user = JSON.parse(sessionStorage.getItem('user') || '{}')
      return user.id || 1
    },

    // 加载历史聊天记录
    async loadChatHistory() {
      try {
        const response = await this.$axios.get(`/ai/history/user/${this.userId}`, {
          params: { limit: 50 }
        })
        
        console.log('历史记录响应:', response.data)
        
        if (response.data.code === 200) {
          // 后端返回的数据结构是 { code: 200, data: { data: [...] } }
          let histories = response.data.data
          
          // 如果data是对象且包含data属性，则取data.data
          if (histories && typeof histories === 'object' && histories.data) {
            histories = histories.data
          }
          
          // 确保histories是数组
          if (!Array.isArray(histories)) {
            console.warn('历史记录不是数组:', histories)
            return
          }
          
          // 将历史记录转换为消息格式
          histories.forEach((record, index) => {
            // 添加用户消息
            this.messages.push({
              id: index * 2,
              role: 'user',
              content: record.userMessage,
              createTime: record.createTime
            })
            
            // 添加AI消息
            this.messages.push({
              id: index * 2 + 1,
              role: 'assistant',
              content: record.assistantMessage,
              thinkingProcess: record.thinkingProcess,
              thinkingCollapsed: true,  // 历史记录中的思考过程默认折叠
              createTime: record.createTime
            })
          })
          
          console.log('✓ 加载聊天历史成功，共', histories.length, '条记录')
          this.scrollToBottom()
        }
      } catch (error) {
        console.error('加载聊天历史失败:', error)
      }
    },

    // 发送消息
    async sendMessage() {
      if (!this.userInput.trim()) return

      const userMessage = this.userInput
      this.userInput = ''

      // 添加用户消息到列表
      this.messages.push({
        id: this.messageIdCounter++,
        role: 'user',
        content: userMessage,
        createTime: new Date().toISOString()
      })

      this.isLoading = true
      this.scrollToBottom()

      try {
        // 调用AI API获取回复
        const response = await this.$axios.post('/api/ai/chat', {
          message: userMessage,
          conversationHistory: this.conversationHistory,
          userId: this.userId
        })

        if (response.data.code === 200) {
          const aiMessage = response.data.data.message
          const thinkingProcess = response.data.data.thinkingProcess

          // 添加AI消息到列表
          this.messages.push({
            id: this.messageIdCounter++,
            role: 'assistant',
            content: aiMessage,
            thinkingProcess: thinkingProcess,
            thinkingCollapsed: false,  // 初始时展开思考过程
            createTime: new Date().toISOString()
          })

          // 保存聊天记录到数据库
          await this.saveChatHistory(userMessage, aiMessage, thinkingProcess)

          // 1秒后自动折叠思考过程（可选）
          setTimeout(() => {
            const lastMessage = this.messages[this.messages.length - 1]
            if (lastMessage.thinkingProcess) {
              lastMessage.thinkingCollapsed = true
            }
          }, 2000)
        } else {
          this.$message.error('AI回复失败：' + response.data.message)
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        this.$message.error('发送消息失败，请重试')
      } finally {
        this.isLoading = false
        this.scrollToBottom()
      }
    },

    // 保存聊天记录到数据库
    async saveChatHistory(userMessage, aiMessage, thinkingProcess) {
      try {
        const response = await this.$axios.post('/ai/history/save', {
          userId: this.userId,
          userMessage: userMessage,
          assistantMessage: aiMessage,
          thinkingProcess: thinkingProcess
        })

        if (response.data.code !== 200) {
          console.warn('保存聊天记录失败:', response.data.message)
        }
      } catch (error) {
        console.error('保存聊天记录失败:', error)
      }
    },

    // 折叠/展开思考过程
    toggleThinking(messageId) {
      const message = this.messages.find(m => m.id === messageId)
      if (message && message.thinkingProcess) {
        message.thinkingCollapsed = !message.thinkingCollapsed
      }
    },

    // 格式化消息内容（支持Markdown等）
    formatMessage(content) {
      // 这里可以添加Markdown解析、代码高亮等功能
      // 简单示例：替换换行符
      return content
        .replace(/\n/g, '<br>')
        .replace(/```(.*?)```/gs, '<pre><code>$1</code></pre>')
    },

    // 格式化时间
    formatTime(timeString) {
      if (!timeString) return ''
      const date = new Date(timeString)
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${hours}:${minutes}`
    },

    // 复制消息到剪贴板
    async copyMessage(content) {
      try {
        await navigator.clipboard.writeText(content)
        this.$message.success('已复制到剪贴板')
      } catch (error) {
        console.error('复制失败:', error)
        this.$message.error('复制失败')
      }
    },

    // 删除消息
    async deleteMessage(messageId) {
      this.$confirm('确定要删除该消息吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.messages = this.messages.filter(m => m.id !== messageId)
        this.$message.success('消息已删除')
      }).catch(() => {
        // 取消删除
      })
    },

    // 插入Tab键
    insertTab(event) {
      const textarea = event.target
      const start = textarea.selectionStart
      const end = textarea.selectionEnd
      const text = textarea.value
      
      this.userInput = text.substring(0, start) + '\t' + text.substring(end)
      
      this.$nextTick(() => {
        textarea.selectionStart = textarea.selectionEnd = start + 1
      })
    },

    // 滚动到底部
    scrollToBottom() {
      this.$nextTick(() => {
        const messageList = this.$refs.messageList
        if (messageList) {
          messageList.scrollTop = messageList.scrollHeight
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.chat-window {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .message-list {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 12px;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: transparent;
    }

    &::-webkit-scrollbar-thumb {
      background: rgba(0, 0, 0, 0.2);
      border-radius: 3px;

      &:hover {
        background: rgba(0, 0, 0, 0.3);
      }
    }
  }

  // 消息项
  .message-item {
    display: flex;
    animation: slideIn 0.3s ease;

    &.user {
      justify-content: flex-end;

      .user-message {
        max-width: 70%;
      }
    }

    &.assistant {
      justify-content: flex-start;

      .assistant-message {
        max-width: 85%;
      }
    }
  }

  // 用户消息
  .user-message {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 12px 16px;
    border-radius: 16px;
    word-wrap: break-word;

    .message-content {
      margin-bottom: 4px;
      line-height: 1.5;
    }

    .message-time {
      font-size: 12px;
      opacity: 0.7;
      text-align: right;
    }
  }

  // AI助手消息
  .assistant-message {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }

  // 思考过程包装
  .thinking-wrapper {
    border-bottom: 1px solid #f0f0f0;
  }

  // 思考过程头部
  .thinking-header {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background: #fafafa;
    cursor: pointer;
    user-select: none;
    transition: all 0.3s ease;

    &:hover {
      background: #f5f5f5;
    }

    .toggle-icon {
      display: inline-block;
      margin-right: 8px;
      transition: transform 0.3s ease;
      font-size: 12px;

      &.collapsed {
        transform: rotate(-90deg);
      }
    }

    .thinking-label {
      font-weight: 600;
      color: #666;
      flex: 1;
    }

    .thinking-status {
      font-size: 12px;
      color: #999;
      margin-left: 8px;
    }
  }

  // 思考内容
  .thinking-content {
    padding: 12px 16px;
    background: #f9f9f9;
    max-height: 300px;
    overflow-y: auto;
    border-bottom: 1px solid #f0f0f0;

    .thinking-text {
      font-size: 13px;
      line-height: 1.6;
      color: #555;
      white-space: pre-wrap;
      word-wrap: break-word;
      font-family: 'Monaco', 'Courier New', monospace;
    }

    .thinking-footer {
      display: flex;
      justify-content: flex-end;
      margin-top: 8px;
      padding-top: 8px;
      border-top: 1px solid #e8e8e8;

      .thinking-complete {
        font-size: 11px;
        color: #999;
      }
    }
  }

  // 回复内容
  .response-wrapper {
    padding: 12px 16px;

    .response-header {
      display: flex;
      align-items: center;
      margin-bottom: 8px;

      .ai-badge {
        display: inline-block;
        background: #667eea;
        color: white;
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 600;
      }
    }

    .response-content {
      margin-bottom: 8px;
      line-height: 1.6;
      color: #333;
      word-wrap: break-word;

      // Markdown代码块样式
      pre {
        background: #f5f5f5;
        padding: 12px;
        border-radius: 4px;
        overflow-x: auto;
        margin: 8px 0;

        code {
          font-family: 'Monaco', 'Courier New', monospace;
          font-size: 12px;
        }
      }

      // 代码块样式
      code {
        background: #f0f0f0;
        padding: 2px 6px;
        border-radius: 3px;
        font-family: 'Monaco', 'Courier New', monospace;
        font-size: 12px;
      }
    }

    .message-time {
      font-size: 12px;
      color: #999;
      text-align: right;
    }
  }

  // 消息操作按钮
  .message-actions {
    display: flex;
    gap: 8px;
    padding: 8px 16px;
    background: #f9f9f9;
    border-top: 1px solid #f0f0f0;

    .action-btn {
      flex: 1;
      padding: 6px 12px;
      background: white;
      border: 1px solid #ddd;
      border-radius: 4px;
      cursor: pointer;
      font-size: 12px;
      transition: all 0.3s ease;

      &:hover:not(:disabled) {
        background: #f5f5f5;
        border-color: #667eea;
        color: #667eea;
      }

      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
  }

  // 加载指示器
  .loading-indicator {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px;
    text-align: center;
    color: #666;

    .spinner {
      display: inline-block;
      width: 16px;
      height: 16px;
      border: 2px solid #f3f3f3;
      border-top: 2px solid #667eea;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }
  }

  // 输入区域
  .input-area {
    padding: 16px;
    background: white;
    border-top: 1px solid #e0e0e0;

    .message-input {
      width: 100%;
      height: 80px;
      padding: 12px;
      border: 1px solid #ddd;
      border-radius: 8px;
      resize: vertical;
      font-family: inherit;
      font-size: 14px;
      line-height: 1.5;
      transition: border-color 0.3s ease;

      &:focus {
        outline: none;
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
      }

      &::placeholder {
        color: #999;
      }
    }

    .input-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 12px;

      .input-hint {
        font-size: 12px;
        color: #999;
      }

      .send-btn {
        padding: 8px 24px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        font-size: 14px;
        font-weight: 600;
        transition: all 0.3s ease;

        &:hover:not(:disabled) {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }

        &:active:not(:disabled) {
          transform: translateY(0);
        }

        &:disabled {
          opacity: 0.5;
          cursor: not-allowed;
        }
      }
    }
  }
}

// 动画
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.thinking-expand-enter-active,
.thinking-expand-leave-active {
  transition: all 0.3s ease;
}

.thinking-expand-enter {
  opacity: 0;
  max-height: 0;
}

.thinking-expand-leave-to {
  opacity: 0;
  max-height: 0;
}

// 响应式设计
@media (max-width: 768px) {
  .message-item {
    &.user .user-message,
    &.assistant .assistant-message {
      max-width: 100%;
    }
  }

  .input-area {
    .message-input {
      height: 60px;
    }

    .input-footer {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;

      .send-btn {
        width: 100%;
      }
    }
  }
}
</style>
