<!-- 前台页面顶部导航栏 -->
<template>
  <div class="nav-menu-container">
    <!-- 移动端：折叠菜单 -->
    <div class="mobile-nav">
      <div class="mobile-header">
        <span class="mobile-title">学科竞赛平台</span>
        <el-button 
          type="text" 
          icon="el-icon-menu" 
          class="mobile-menu-btn"
          @click="mobileMenuVisible = !mobileMenuVisible">
        </el-button>
      </div>
      <transition name="slide-fade">
        <div v-show="mobileMenuVisible" class="mobile-menu-content">
          <el-menu
            :default-active="currentPath"
            :router="false"
            mode="vertical"
            background-color="white"
            text-color="#222"
            active-text-color="red"
            @select="handleMenuSelect">
            <el-menu-item 
              v-for="(item,i) in navList" 
              :key="i" 
              :index="item.name || 'disabled-' + i"
              :class="{ 'nav-item-disabled': !item.name || item.name.trim() === '' }">
              <span class="nav-item-text">{{ item.navItem }}</span>
            </el-menu-item>
            <el-menu-item index="ai-chat" class="mobile-ai-menu-item">
              <i class="el-icon-chat-dot-round"></i>
              <span class="nav-item-text">大模型助手</span>
            </el-menu-item>
          </el-menu>
          <div class="mobile-search">
            <el-input
              :placeholder="searchPlaceholder"
              prefix-icon="el-icon-search"
              size="small"
              v-model="keywords"
              @keyup.enter.native="handleSearch">
              <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
            </el-input>
          </div>
        </div>
      </transition>
    </div>

    <!-- PC端：水平菜单 -->
    <el-menu
      :default-active="currentPath"
      :router="false"
      mode="horizontal"
      background-color="white"
      text-color="#222"
      active-text-color="#222"
      class="desktop-nav"
      @select="handleMenuSelect">
      <el-menu-item 
        v-for="(item,i) in navList" 
        :key="i" 
        :index="item.name || 'disabled-' + i"
        :class="{ 'nav-item-disabled': !item.name || item.name.trim() === '' }">
        <span class="nav-item-text">{{ item.navItem }}</span>
      </el-menu-item>
      <el-menu-item index="ai-chat" class="ai-menu-item" @click.native.prevent="goToAIChat">
        <el-button
          type="primary"
          size="small"
          class="ai-button-inline"
          @click.stop="goToAIChat">
          <span class="ai-logo-small-wrap">
            <svg class="ai-logo-small" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M6 17C6 11 8.5 8 12 8C15.5 8 18 11 18 17"
                stroke="#ffffff"
                stroke-width="2.2"
                stroke-linecap="round"
                fill="none"/>
              <circle cx="19.5" cy="8" r="1.6" fill="#ffffff"/>
            </svg>
          </span>
          <span class="ai-btn-text">大模型助手</span>
        </el-button>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script>
  export default {
    name: 'NavMenu',
    data () {
      return {
        navList: [
          {name: '/index', navItem: '首页'},
          {name: '/jotter', navItem: '通知公告'},
          {name: '/competition-notice', navItem: '竞赛公告'},
          {name: '/library', navItem: '竞赛一览'},
          {name: '/achievement', navItem: '竞赛成果'},
          {name: '/downloads', navItem: '文件下载'},
          {name: '/admin', navItem: '管理中心'}
        ],
        keywords: '',
        mobileMenuVisible: false
      }
    },
    computed: {
      hoverBackground () {
        return '#9FD3FD'
      },
      currentPath () {
        var x = this.$route.path.indexOf('/', 1)
        if (x !== -1) {
          return this.$route.path.substring(0, x)
        } else {
          return this.$route.path
        }
      },
      isAIChatPage () {
        return this.$route.path === '/ai/chat'
      },
      // 根据当前页面返回搜索提示文字（移动端使用）
      searchPlaceholder () {
        const path = this.$route.path
        if (path.startsWith('/jotter') || path === '/index') {
          return '搜索公告标题或内容...'
        } else if (path.startsWith('/library')) {
          return '搜索竞赛项目名F称...'
        } else {
          return '请输入关键词进行搜索...'
        }
      }
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
      async goToAIChat () {
        // 检查登录状态
        const isLoggedIn = await this.checkLoginStatus()
        if (!isLoggedIn) {
          this.$message.warning('请先登录后再使用大模型助手')
          this.$router.push({
            path: '/login',
            query: { redirect: '/ai/chat' }
          })
          return
        }
        this.$router.push('/ai/chat')
      },
      // 处理菜单项选择
      handleMenuSelect (index) {
        if (index === 'ai-chat') {
          this.mobileMenuVisible = false
          this.goToAIChat()
          return
        }

        // 找到对应的导航项
        const item = this.navList.find((nav, i) => {
          return (nav.name || 'disabled-' + i) === index
        })
        
        if (!item) return
        
        // 如果name为空，显示敬请期待提示
        if (!item.name || item.name.trim() === '') {
          this.$message.info('敬请期待')
          return
        }
        
        // name不为空时，手动跳转
        this.mobileMenuVisible = false
        this.$router.push(item.name)
      },
      // 处理搜索功能
      handleSearch () {
        if (!this.keywords || !this.keywords.trim()) {
          return
        }
        
        const keyword = this.keywords.trim()
        
        // 直接跳转到搜索结果页面
        this.$router.push({
          path: '/search',
          query: { keyword: keyword }
        })
      }
    }
  }
</script>

<style scoped>
  a{
    text-decoration: none;
  }

  .nav-menu-container {
    position: relative;
    display: flex;
    justify-content: center;
  }

  /* PC端样式 */
  .desktop-nav {
    display: flex;
    justify-content: center;
    width: 100%;
    border-bottom: none;
  }

  /* 确保菜单项居中 */
  .desktop-nav >>> .el-menu {
    display: flex;
    justify-content: center;
    border-bottom: 1px solid #e6e6e6;
  }

  /* 导航菜单项通用样式 */
  .desktop-nav >>> .el-menu-item {
    padding: 0 24px;
    height: 60px;
    line-height: 60px;
    font-size: 15px;
    font-weight: 500;
    color: #333;
    transition: all 0.3s ease;
    border-bottom: 3px solid transparent;
    position: relative;
  }

  .desktop-nav >>> .el-menu-item:hover {
    color: #333 !important;
    background-color: transparent !important;
    border-bottom-color: #1890ff !important;
  }

  .desktop-nav >>> .el-menu-item.is-active {
    color: #333 !important;
    border-bottom: none !important;
    background-color: #9FD3FD !important;
  }

  /* 禁用状态的导航项 */
  .desktop-nav >>> .nav-item-disabled {
    color: #999;
    cursor: not-allowed;
    opacity: 0.7;
  }

  .desktop-nav >>> .nav-item-disabled:hover {
    color: #999;
    background-color: rgba(0, 0, 0, 0.03);
    border-bottom-color: transparent;
  }

  /* 导航项文本 */
  .nav-item-text {
    display: inline-block;
    vertical-align: middle;
  }


  /* 移动端样式 */
  .mobile-nav {
    display: none;
  }

  .mobile-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background-color: white;
    box-shadow: 0 2px 8px 0 rgba(0,0,0,.08);
  }

  .mobile-title {
    font-size: 18px;
    font-weight: 600;
    color: #222;
    letter-spacing: 0.5px;
  }

  .mobile-menu-btn {
    font-size: 24px;
    color: #222;
    padding: 4px;
    transition: transform 0.3s;
  }

  .mobile-menu-btn:hover {
    transform: scale(1.1);
  }

  .mobile-menu-content {
    background-color: white;
    border-top: 1px solid #e6e6e6;
    max-height: calc(100dvh - 110px);
    overflow-x: hidden;
    overflow-y: auto;
    box-shadow: 0 4px 12px rgba(0,0,0,.1);
  }

  /* 移动端菜单项样式 */
  .mobile-menu-content >>> .el-menu-item {
    height: 48px;
    line-height: 48px;
    font-size: 15px;
    color: #333;
    transition: all 0.2s;
  }

  .mobile-menu-content >>> .el-menu-item:hover {
    background-color: #f5f5f5;
    color: #1890ff;
  }

  .mobile-menu-content >>> .nav-item-disabled {
    color: #999;
    opacity: 0.7;
  }

  .mobile-menu-content >>> .mobile-ai-menu-item {
    color: #2f8f46;
    font-weight: 600;
  }

  .mobile-menu-content >>> .mobile-ai-menu-item i {
    margin-right: 8px;
    color: #52c41a;
  }

  .mobile-search {
    padding: 16px;
    border-top: 1px solid #e6e6e6;
    background-color: #fafafa;
  }

  /* 过渡动画 */
  .slide-fade-enter-active {
    transition: all 0.3s ease;
  }

  .slide-fade-leave-active {
    transition: all 0.3s ease;
  }

  .slide-fade-enter, .slide-fade-leave-to {
    transform: translateY(-10px);
    opacity: 0;
  }

  /* 响应式适配 */
  @media (max-width: 1024px) {
    .nav-menu-container,
    .mobile-nav {
      width: 100%;
    }

    .desktop-nav {
      display: none;
    }

    .mobile-nav {
      display: block;
    }

    .nav-title {
      display: none;
    }
  }

  @media (max-width: 768px) {
    .mobile-title {
      font-size: 16px;
    }
  }

  /* 大模型助手按钮样式 */
  .ai-menu-item {
    padding: 0 16px !important;
    border-bottom: none !important;
    margin-left: 8px;
    height: 60px !important;
    line-height: 60px !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
  }

  .ai-menu-item:hover {
    background-color: transparent !important;
  }

  .ai-button-inline {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
    border: none;
    margin: 0;
    height: 40px;
    padding: 0 20px;
    border-radius: 20px;
    box-shadow: 0 2px 8px rgba(82, 196, 26, 0.25);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    vertical-align: middle;
  }

  .ai-button-inline:hover {
    background: linear-gradient(135deg, #73d13d 0%, #52c41a 100%);
    box-shadow: 0 4px 16px rgba(82, 196, 26, 0.4);
    transform: translateY(-2px);
  }

  .ai-button-inline:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(82, 196, 26, 0.3);
  }

  .ai-logo-small-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .ai-logo-small {
    width: 20px;
    height: 20px;
  }

  .ai-btn-text {
    font-size: 14px;
  }
</style>
