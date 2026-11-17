<!-- 后台头部显示栏的内容 -->
<template>
  <el-card class="admin-header" :body-style="{padding: '0 20px'}">
    <div class="header-content">
      <el-button
        type="text"
        icon="el-icon-s-unfold"
        class="mobile-admin-menu-btn"
        aria-label="打开后台菜单"
        @click="$emit('toggle-mobile-menu')">
      </el-button>
      <a href="/index" class="logo-link">
        <img src="../../assets/img/icon/icon2.png" alt="" width="70px" class="logo-img">
      </a>
      <span class="header-title">学科竞赛后台</span>
      <div class="header-actions">
        <el-button 
          type="text" 
          icon="el-icon-back" 
          class="action-btn"
          @click="goToFrontend">
          返回前台
        </el-button>
        <el-button 
          type="text" 
          icon="el-icon-chat-dot-round" 
          class="action-btn ai-btn"
          @click="goToAIChat">
          大模型助手
        </el-button>
        <el-button 
          type="text" 
          icon="el-icon-switch-button" 
          class="action-btn logout-btn"
          @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script>
  import {createRouter} from '../../router'

  export default {
    name: 'Header',
    methods: {
      // 返回前台
      goToFrontend() {
        this.$router.push('/index')
      },
      // 跳转大模型助手
      async goToAIChat() {
        // 检查登录状态
        try {
          const resp = await this.$axios.get('/authentication')
          if (resp && resp.data) {
            // 已登录，跳转到大模型聊天页面
            this.$router.push('/ai/chat')
          } else {
            this.$message.warning('请先登录后再使用大模型助手')
            this.$router.push({
              path: '/login',
              query: { redirect: '/ai/chat' }
            })
          }
        } catch (error) {
          this.$message.warning('请先登录后再使用大模型助手')
          this.$router.push({
            path: '/login',
            query: { redirect: '/ai/chat' }
          })
        }
      },
      // 处理退出登录（带确认对话框）
      handleLogout() {
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          center: true
        }).then(() => {
          this.logout()
        }).catch(() => {
          // 用户取消，不做任何操作
        })
      },
      // 执行退出登录
      logout () {
        var _this = this
        this.$axios.get('/logout').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.$store.commit('logout')
            _this.$message.success('已安全退出')
            _this.$router.replace('/index')
            // 清空路由，防止路由重复加载
            const newRouter = createRouter()
            _this.$router.matcher = newRouter.matcher
          }
        }).catch(failResponse => {
          _this.$message.error('退出登录失败，请重试')
        })
      }
    }
  }
</script>

<style scoped>
  .admin-header {
    height: auto;
    min-height: 80px;
    opacity: 0.85;
    line-height: 40px;
    min-width: 0;
    width: 100%;
    box-sizing: border-box;
    margin: 0;
    border-radius: 0;
  }

  .header-content {
    display: flex;
    align-items: center;
    min-height: 80px;
    width: 100%;
    gap: 16px;
  }

  .logo-link {
    float: none;
    display: flex;
    align-items: center;
    margin-right: 20px;
  }

  .logo-img {
    width: auto;
    height: 70px;
    margin-top: -5px;
  }

  .header-title {
    font-size: 25px;
    font-weight: bold;
    flex: 1;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .header-actions {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 8px;
    margin-left: auto;
    flex-shrink: 0;
  }

  .mobile-admin-menu-btn {
    display: none;
    padding: 6px;
    color: #409eff;
    font-size: 24px;
  }

  .action-btn {
    padding: 8px 16px;
    font-size: 14px;
    color: #606266;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    background-color: #fff;
    transition: all 0.3s ease;
    display: inline-flex;
    align-items: center;
    gap: 6px;
  }

  .action-btn:hover {
    border-color: #409eff;
    background-color: #ecf5ff;
  }

  .action-btn:active {
    transform: scale(0.98);
  }

  .action-btn i {
    font-size: 16px;
  }

  /* 返回前台按钮 */
  .action-btn:not(.ai-btn):not(.logout-btn):hover {
    color: #409eff;
  }

  /* 大模型助手按钮 */
  .action-btn.ai-btn:hover {
    color: #52c41a;
    border-color: #52c41a;
    background-color: #f6ffed;
  }

  /* 退出登录按钮 */
  .action-btn.logout-btn:hover {
    color: #f56c6c;
    border-color: #f56c6c;
    background-color: #fef0f0;
  }

  @media (max-width: 900px) {
    .header-content {
      min-height: 64px;
      flex-wrap: wrap;
      gap: 10px;
      padding: 8px 0;
    }

    .mobile-admin-menu-btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
    }

    .logo-link {
      margin-right: 0;
    }

    .logo-img {
      height: 48px;
      margin-top: 0;
    }

    .header-title {
      font-size: 20px;
    }

    .header-actions {
      width: 100%;
      justify-content: flex-start;
      flex-wrap: wrap;
      gap: 6px;
    }

    .action-btn {
      padding: 6px 10px;
      font-size: 12px;
    }
  }

  @media (max-width: 480px) {
    .header-title {
      font-size: 17px;
    }

    .header-actions {
      gap: 4px;
    }

    .action-btn {
      flex: 1;
      min-width: 0;
      padding: 6px 4px;
      font-size: 11px;
      white-space: normal;
    }
  }
</style>
