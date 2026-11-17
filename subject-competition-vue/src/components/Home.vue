<!-- 导入前台页面顶部导航栏组件 -->
<template>
  <div>
    <!-- 顶部空白盒子，右侧登录注册按钮和搜索栏（AI聊天页面不显示） -->
    <div v-if="!isAIChatPage" class="top-header">
      <div class="header-content">
        <div class="header-left">
          <div class="logo-container" @click="goToHome">
            <img
              src="../assets/img/icon/wenzi.png"
              alt="Logo"
              class="header-logo">
            <div class="logo-text-container">
              <span class="logo-text">基于大模型的学科竞赛智能化人机交互虚拟仿真实验</span>
              <span class="logo-subtitle">Large Model-Based Intelligent Human-Computer Interaction Virtual Simulation Experiment for Subject Competitions</span>
            </div>
          </div>
        </div>
        <div class="header-right">
          <div class="header-top-row">
            <!-- 未登录状态：显示登录和注册 -->
            <template v-if="!isLoggedIn">
              <router-link to="/login" class="header-link">登录</router-link>
            </template>
            <!-- 已登录状态：显示你好+用户名 -->
            <template v-else>
              <span class="user-greeting">
                <span class="greeting-text">你好，</span>
                <span class="username">{{ currentUsername }}</span>
              </span>
            </template>
          </div>
          <div class="header-search site-search">
            <input
              class="search-input"
              :placeholder="searchPlaceholder"
              v-model="keywords"
              @keyup.enter="handleSearch">
            <el-button
              type="primary"
              size="small"
              plain
              round
              class="search-button"
              @click="handleSearch">
              <i class="el-icon-search"></i>
              <span>搜索</span>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <nav-menu class="nav-menu"></nav-menu>
    <router-view/>
  </div>
</template>
<script>

  import NavMenu from '@/components/common/NavMenu'
  export default {
    components: {NavMenu},
    data () {
      return {
        keywords: '',
        isLoggedIn: false,
        currentUsername: ''
      }
    },
    computed: {
      // 根据当前页面返回搜索提示文字
      searchPlaceholder () {
        const path = this.$route.path
        if (path.startsWith('/jotter') || path === '/index') {
          return '搜索公告标题或内容...'
        } else if (path.startsWith('/library')) {
          return '搜索竞赛项目名称...'
        } else {
          return '请输入关键词进行搜索...'
        }
      },
      // 判断是否是AI聊天页面
      isAIChatPage () {
        return this.$route.path === '/ai/chat'
      }
    },
    mounted() {
      // 检查登录状态
      this.checkLoginStatus()
    },
    watch: {
      // 监听路由变化，更新登录状态
      '$route'() {
        this.checkLoginStatus()
      }
    },
    methods: {
      // 检查登录状态
      async checkLoginStatus() {
        try {
          const resp = await this.$axios.get('/currentUser')
          if (resp && resp.data && resp.data.code === 200) {
            this.isLoggedIn = true
            // 获取用户名，可能在不同字段中
            const data = resp.data.data || {}
            this.currentUsername = data.username ||
                                  data.name ||
                                  resp.data.username ||
                                  (this.$store && this.$store.state && this.$store.state.username) ||
                                  '用户'
          } else {
            this.isLoggedIn = false
            this.currentUsername = ''
          }
        } catch (error) {
          this.isLoggedIn = false
          this.currentUsername = ''
        }
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
      },
      // 点击logo跳转首页
      goToHome () {
        this.$router.push('/index')
      }
    }
  }
</script>

<style>
  /* 顶部盒子样式 */
  .top-header {
    background: transparent;
    padding: clamp(10px, 1.2vw, 16px) 0;
    border-bottom: 1px solid #e6e6e6;
    position: relative;
    min-height: clamp(112px, 9vw, 160px);
    display: flex;
    align-items: center;
    overflow: hidden;
  }

  /* 背景渐变模糊层，保证前景文字清晰 */
  .top-header::before {
    content: '';
    position: absolute;
    left: 50%;
    top: 0;
    transform: translateX(-50%);
    width: 100%;
    height: 100%;
    background: linear-gradient(180deg, #76C1FE 0%, #76C1FE 0%, rgba(91, 181, 255, 0.16) 100%);
    filter: blur(34px);
    z-index: 0;
  }

  .header-content {
    width: min(1200px, 100%);
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    gap: 16px;
    position: relative;
    z-index: 1;
  }


  .header-left {
    flex: 1;
    min-width: 0;
    display: flex;
    align-items: center;
    justify-content: flex-start;
  }

  .logo-container {
    display: flex;
    align-items: center;
    gap: 15px;
    cursor: pointer;
    transition: transform 0.3s ease;
    margin-left: clamp(-150px, -8vw, 0px);
    min-width: 0;
  }

  .logo-text-container {
    display: flex;
    flex-direction: column;
    gap: 5px;
    margin-top: clamp(10px, 2vw, 30px);
    min-width: 0;
  }

  .logo-container:hover {
    transform: translateX(2px);
  }

  .header-logo {
    height: 80px;
    width: auto;
    object-fit: contain;
    transition: transform 0.3s ease;
  }

  .logo-container:hover .header-logo {
    transform: scale(1.05);
    opacity: 0.9;
  }

  .logo-text {
    font-size: clamp(16px, 2vw, 28px);
    font-weight: bold;
    color: #000000;
    white-space: normal;
    overflow-wrap: anywhere;
    transition: color 0.3s ease;
    line-height: 1.2;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }

  .logo-subtitle {
    font-size: clamp(9px, 0.8vw, 12px);
    font-weight: normal;
    color: rgba(0, 0, 0, 0.9);
    white-space: normal;
    overflow-wrap: anywhere;
    transition: color 0.3s ease;
    line-height: 1.2;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  }

  .logo-container:hover .logo-text {
    color:rgb(234, 242, 247);
  }

  .logo-container:hover .logo-subtitle {
    color: rgb(234, 242, 247);
  }

  .header-right {
    display: flex;
    flex-direction: row-reverse;
    align-items: flex-start;
    gap: 8px;
    min-width: 0;
  }

  .header-top-row {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .header-link {
    color: rgb(64, 158, 255);
    text-decoration: none;
    font-size: 14px;
    font-weight: 500;
    padding: 4px 16px;
    border-radius: 20px;
    transition: all 0.3s ease;
    background-color: rgb(236, 245, 255);
    border: 1px solid rgba(255, 255, 255, 0.3);
    backdrop-filter: blur(10px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-top: 5px;
  }

  .header-link:hover {
    color: #667eea;
    background-color: #ffffff;
    border-color: #ffffff;
    box-shadow: 0 4px 8px rgba(255, 255, 255, 0.3);
    transform: translateY(-1px);
  }

  .header-top-row >>> .el-divider--vertical {
    height: 20px;
    margin: 0;
    background-color: rgba(255, 255, 255, 0.3);
  }

  /* 用户问候语样式 */
  .user-greeting {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 5px 6px;
    border-radius: 20px;
    background-color: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.3);
    backdrop-filter: blur(10px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    font-size: 14px;
    font-weight: 500;
  }

  .greeting-text {
    color: #000000;
  }

  .username {
    color: rgb(64, 158, 255);
    font-weight: 600;
  }

  .header-search.site-search {
    position: relative;
    display: inline-flex;
    align-items: center;
    transition: all 0.3s ease;
    width: auto;
  }

  /* 搜索输入框 */
  .header-search .search-input {
    background-color: rgba(255, 255, 255, 0.25);
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 20px;
    backdrop-filter: blur(10px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 0 100px 0 16px;
    height: 40px;
    line-height: 40px;
    font-size: 14px;
    color: rgb(64, 158, 255);
    outline: none;
    transition: all 0.3s ease;
    width: 0;
    opacity: 0;
    overflow: hidden;
  }

  .header-search .search-input::placeholder {
    color: rgb(64, 158, 255);
  }

  .header-search .search-input:focus {
    background-color: rgba(255, 255, 255, 0.35);
    border-color: rgba(255, 255, 255, 0.5);
    box-shadow: 0 4px 8px rgba(255, 255, 255, 0.2);
  }

  /* 悬停时展开输入框 */
  .header-search:hover .search-input {
    width: 240px;
    opacity: 1;
    padding: 0 100px 0 16px;
  }

  /* 搜索按钮 - 定位在输入框内部右侧 */
  .header-search .search-button {
    position: absolute;
    right: 4px;
    top: 50%;
    transform: translateY(-50%);
    height: 32px;
    padding: 0 16px;
    display: flex;
    align-items: center;
    gap: 6px;
    transition: all 0.3s ease;
    z-index: 10;
    margin: 0;
  }

  .header-search .search-button i {
    font-size: 14px;
  }

  .header-search .search-button span {
    font-size: 13px;
  }

  .nav-menu {
    /*margin-bottom: 40px;*/
    box-shadow: 0 2px 4px 0 rgba(0,0,0,.05);
    position: sticky;
    top: 0;
    z-index: 1000;
    background-color: white;
  }

  /* 响应式适配 */
  @media (max-width: 1200px) {
    .header-content {
      width: 100%;
      padding: 0 16px;
    }

    .logo-container {
      margin-left: 0;
    }

    .logo-text {
      max-width: 760px;
    }
  }

  @media (max-width: 768px) {
    .nav-menu {
      position: relative;
    }

    .header-content {
      width: 100%;
      padding: 0 14px;
      flex-wrap: wrap;
      align-items: flex-start;
      gap: 12px;
    }

    .header-left {
      width: 100%;
    }

    .logo-container {
      width: 100%;
      align-items: flex-start;
      margin-left: 0;
    }

    .logo-text-container {
      margin-top: 0;
      min-width: 0;
      flex: 1;
    }

    .header-right {
      width: 100%;
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
    }

    .header-top-row {
      flex-shrink: 0;
      margin-left: auto;
    }

    .header-search.site-search {
      flex: 1;
      min-width: 0;
    }

    .header-link {
      font-size: 13px;
      padding: 6px 14px;
    }

    .top-header {
      min-height: 0;
      padding: 12px 0;
    }

    .header-search:hover .search-input {
      width: 100%;
      padding: 0 82px 0 14px;
    }

    .header-search .search-input {
      width: 100%;
      height: 38px;
      line-height: 38px;
      padding: 0 82px 0 14px;
      opacity: 1;
    }

    .header-search .search-button {
      height: 30px;
      padding: 0 12px;
      right: 3px;
    }

    .header-logo {
      height: 60px;
      flex-shrink: 0;
    }

    .logo-text {
      font-size: 18px;
      line-height: 1.3;
    }

    .logo-subtitle {
      font-size: 12px;
      line-height: 1.35;
    }

    .logo-container {
      gap: 10px;
    }

    .logo-text-container {
      gap: 3px;
    }
  }

  @media (max-width: 480px) {
    .top-header {
      padding: 10px 0 12px;
    }

    .header-content {
      padding: 0 10px;
      gap: 10px;
    }

    .header-right {
      gap: 8px;
    }

    .header-link {
      font-size: 12px;
      padding: 5px 10px;
      margin-top: 0;
    }

    .header-top-row {
      gap: 8px;
    }

    .header-search:hover .search-input {
      width: 100%;
      padding: 0 80px 0 12px;
    }

    .header-search .search-input {
      height: 36px;
      line-height: 36px;
      font-size: 12px;
      padding: 0 80px 0 12px;
    }

    .header-search .search-button {
      height: 26px;
      padding: 0 10px;
      right: 3px;
    }

    .header-search .search-button span {
      font-size: 12px;
    }

    .header-search .search-button i {
      font-size: 12px;
    }

    .header-logo {
      height: 52px;
    }

    .logo-text {
      font-size: 16px;
    }

    .logo-subtitle {
      display: -webkit-box;
      overflow: hidden;
      font-size: 9px;
      line-height: 1.35;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
    }

    .logo-container {
      gap: 7px;
    }

    .logo-text-container {
      gap: 2px;
    }
  }
</style>
