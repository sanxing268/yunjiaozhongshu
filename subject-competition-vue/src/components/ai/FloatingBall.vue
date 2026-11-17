<!--大模型助手悬浮球  -->
<template>
  <div class="floating-ball" :style="{ top: ballTop + 'px' }" @click="toggleChat">
    <div class="floating-ball-inner">
      <!-- 绿色大模型小球图标 -->
      <svg class="floating-ball-logo" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
        <path
          d="M 30 80 Q 30 30 60 30 Q 90 30 90 80"
          stroke="#ffffff"
          stroke-width="12"
          stroke-linecap="round"
          fill="none"/>
        <circle cx="95" cy="35" r="8" fill="#ffffff"/>
      </svg>
    </div>
    <!-- 一些悬浮的提示词 -->
    <transition name="ball-fade">
      <div v-if="tipVisible" class="floating-ball-tip">
        {{ currentTip }}
      </div>
    </transition>
    <!-- 点击出现小型聊天窗口 -->
    <chat-window :visible="chatWindowVisible" :show-below="shouldShowBelow" @update-visible="updateChatWindowVisible" />
  </div>
</template>

<script>
import ChatWindow from './ChatWindow.vue'

export default {
  name: 'FloatingBall',
  components: {
    ChatWindow
  },
  data () {
    return {
      tipVisible: false,
      currentTip: '',
      tipTimer: null,
      hideTimer: null,
      ballTop: null,
      targetTop: null,
      lastScrollY: 0,
      scrollDirection: 'down',
      animationFrame: null,
      chatWindowVisible: false
    }
  },
  computed: {
    shouldShowBelow () {
      // 当浮动球距离顶部超过300px时（有足够空间放窗口在上方），显示在上方
      // 否则显示在下方
      return this.ballTop < 300
    }
  },
  mounted () {
    this.showTip()
    this.tipTimer = setInterval(this.showTip, 15000)
    this.lastScrollY = window.scrollY || window.pageYOffset
    const viewportHeight = window.innerHeight
    this.ballTop = viewportHeight - 160 - 72
    this.targetTop = this.ballTop
    window.addEventListener('scroll', this.handleScroll, { passive: true })
    this.animatePosition()
  },
  beforeDestroy () {
    if (this.tipTimer) {
      clearInterval(this.tipTimer)
    }
    if (this.hideTimer) {
      clearTimeout(this.hideTimer)
    }
    if (this.animationFrame) {
      cancelAnimationFrame(this.animationFrame)
    }
    window.removeEventListener('scroll', this.handleScroll)
  },
  methods: {
    // 检查登录状态
    async checkLoginStatus () {
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
    async toggleChat () {
      // 检查登录状态
      const isLoggedIn = await this.checkLoginStatus()
      if (!isLoggedIn) {
        this.$message.warning('请先登录后再使用大模型助手')
        this.$router.push({
          path: '/login',
          query: { redirect: this.$route.fullPath }
        })
        return
      }
      this.chatWindowVisible = !this.chatWindowVisible
    },
    updateChatWindowVisible (visible) {
      this.chatWindowVisible = visible
    },
    handleScroll () {
      this.updateBallPosition()
      if (!this.animationFrame) {
        this.animatePosition()
      }
    },
    updateBallPosition () {
      const currentScrollY = window.scrollY || window.pageYOffset
      const scrollDelta = currentScrollY - this.lastScrollY
      const viewportHeight = window.innerHeight

      if (scrollDelta > 5) {
        this.scrollDirection = 'down'
        this.targetTop = viewportHeight - 160 - 72
      } else if (scrollDelta < -5) {
        this.scrollDirection = 'up'
        this.targetTop = 120
      }

      this.lastScrollY = currentScrollY
    },
    animatePosition () {
      const diff = this.targetTop - this.ballTop
      if (Math.abs(diff) > 0.5) {
        this.ballTop += diff * 0.06
        this.animationFrame = requestAnimationFrame(() => {
          this.animatePosition()
        })
      } else {
        this.ballTop = this.targetTop
        this.animationFrame = null
      }
    },
    getTimeBasedTips () {
      const hour = new Date().getHours()

      const commonTips = [
        '要不要让我告诉你最近有哪些值得参加的比赛吗？',
        '需要我帮你找一些有用的竞赛准备资料吗？',
        '要不要我帮你分析一下哪些竞赛适合你？',
        '需要我帮你整理一下竞赛时间表吗？',
        '要不要我帮你找一些编程竞赛的题目？',
        '需要我帮你了解数学建模竞赛的流程吗？',
        '要不要我帮你找一些竞赛的获奖经验分享？',
        '需要我帮你分析一下如何选择竞赛项目吗？',
        '要不要我帮你找一些竞赛的组队信息？',
        '需要我帮你制定竞赛准备计划吗？',
        '要不要我帮你了解各类竞赛的报名要求？',
        '需要我帮你找一些竞赛的真题和解析吗？',
        '要不要我帮你分析一下竞赛的评分标准？',
        '你知道吗？数学建模竞赛通常需要3人组队，考察建模、编程和论文写作能力',
        'ACM程序设计竞赛是个人或团队参加的算法竞赛，需要扎实的编程基础',
        '全国大学生数学竞赛分为数学专业组和非数学专业组，考察数学基础',
        '互联网+大学生创新创业大赛注重创新性和商业价值，适合有创业想法的同学',
        '挑战杯是全国大学生课外学术科技作品竞赛，涵盖多个学科领域',
        '蓝桥杯是面向大学生的IT类竞赛，包括软件、硬件和设计等多个方向',
        '全国大学生电子设计竞赛需要团队合作，考察电子系统设计能力',
        '中国大学生计算机设计大赛涵盖多个计算机相关领域，适合不同专业的同学',
        '全国大学生英语竞赛分为A、B、C、D四个类别，适合不同英语水平的同学',
        '全国大学生物理实验竞赛注重实验设计和操作能力，需要团队协作',
        '全国大学生化学实验邀请赛考察化学实验技能和理论知识',
        '全国大学生机械创新设计大赛需要创新思维和机械设计能力',
        '全国大学生结构设计竞赛考察结构设计和模型制作能力',
        '全国大学生广告艺术大赛适合有创意和设计能力的同学',
        '有什么竞赛相关的问题随时可以问我哦',
        '遇到困难不要怕，我们一起解决',
        '保持好心情，竞赛路上我陪你一起进步',
        '记得劳逸结合，效率更高哦',
        '有什么想法可以和我分享，我随时准备为你答疑解惑'
      ]

      if (hour >= 6 && hour < 11) {
        const morningTips = [
          '早上好，新的一天开始了！',
          '早上好，今天有什么竞赛计划吗？',
          '早上好，吃早餐了吗？记得补充能量哦~',
          '早上好，要不要让我帮你看看今天的竞赛安排？'
        ]
        return [...morningTips, ...commonTips]
      } else if (hour >= 11 && hour < 14) {
        const noonTips = [
          '中午好，记得好好吃饭哦~',
          '中午好，午饭吃了什么好吃的？',
          '中午好，要不要休息一下？',
          '中午好，需要我帮你整理下午的竞赛计划吗？'
        ]
        return [...noonTips, ...commonTips]
      } else if (hour >= 14 && hour < 18) {
        const afternoonTips = [
          '下午好，学习/工作还顺利吗？',
          '下午好，今天的任务完成得怎么样？',
          '下午好，需要我帮你查点什么吗？',
          '下午好，要不要我帮你分析一下竞赛准备进度？'
        ]
        return [...afternoonTips, ...commonTips]
      } else if (hour >= 18 && hour < 23) {
        const eveningTips = [
          '晚上好，今天过得怎么样？',
          '晚上好，该适当放松一下啦~',
          '晚上好，今天有什么收获吗？',
          '晚上好，需要我帮你规划明天的竞赛安排吗？'
        ]
        return [...eveningTips, ...commonTips]
      } else {
        const nightTips = [
          '这么晚还在学习，要注意休息哦',
          '这么晚还没休息？要注意身体哦',
          '夜深了，有困惑的话可以来和我聊聊',
          '这么晚还在努力，记得早点休息哦'
        ]
        return [...nightTips, ...commonTips]
      }
    },
    showTip () {
      const tips = this.getTimeBasedTips()
      const index = Math.floor(Math.random() * tips.length)
      this.currentTip = tips[index]
      this.tipVisible = true
      if (this.hideTimer) {
        clearTimeout(this.hideTimer)
      }
      this.hideTimer = setTimeout(() => {
        this.tipVisible = false
      }, 5000)
    }
  }
}
</script>

<style scoped>
.floating-ball {
  position: fixed;
  right: 60px;
  z-index: 1001;
  cursor: pointer;
  will-change: top;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.floating-ball-inner {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  box-shadow: 0 8px 24px rgba(82, 196, 26, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  animation: floatY 3s ease-in-out infinite;
}

.floating-ball-logo {
  width: 36px;
  height: 36px;
}

.floating-ball-tip {
  position: absolute;
  right: 75px;
  top: 50%;
  transform: translateY(-50%);
  display: inline-block;
  min-width: 150px;
  max-width: 260px;
  padding: 10px 14px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  font-size: 13px;
  font-weight: 600;
  color: #555;
  line-height: 1.5;
  text-align: left;
  white-space: normal;
  word-break: break-word;
}

.ball-fade-enter-active,
.ball-fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.ball-fade-enter {
  opacity: 0;
  transform: translateY(-50%) translateX(10px);
}

.ball-fade-leave-to {
  opacity: 0;
  transform: translateY(-50%) translateX(10px);
}

@keyframes floatY {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@media (max-width: 640px) {
  .floating-ball {
    right: 12px;
  }

  .floating-ball-inner {
    width: 56px;
    height: 56px;
  }

  .floating-ball-logo {
    width: 34px;
    height: 34px;
  }

  .floating-ball-tip {
    top: auto;
    right: 0;
    bottom: 72px;
    min-width: 0;
    max-width: calc(100vw - 32px);
    transform: none;
  }
}
</style>

