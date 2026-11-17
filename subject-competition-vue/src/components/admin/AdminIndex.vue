<!-- 系统后台布局根组件 引入其他组件的内容-->
<template>
  <el-container id="admin-body">
    <!-- 头部 -->
    <el-header class="admin-header-wrapper">
      <Header class="header-component" @toggle-mobile-menu="toggleMobileMenu"></Header>
    </el-header>
    <div v-if="mobileMenuVisible" class="admin-mobile-mask" @click="closeMobileMenu"></div>
    <el-container class="admin-content-wrapper">
      <!-- 侧边栏 -->
      <el-aside class="admin-aside" :class="{ 'is-mobile-open': mobileMenuVisible }">
        <admin-menu></admin-menu>
      </el-aside>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import AdminMenu from './AdminMenu'
import Header from './Header'

export default {
  name: 'AdminIndex',
  components: { AdminMenu, Header },
  data() {
    return {
      dialogVisible: false,
      mobileMenuVisible: false
    }
  },
  watch: {
    '$route.path'() {
      this.mobileMenuVisible = false
    }
  },
  methods: {
    toggleMobileMenu() {
      this.mobileMenuVisible = !this.mobileMenuVisible
    },
    closeMobileMenu() {
      this.mobileMenuVisible = false
    }
  },
  mounted() {
    // this.$router.replace('/admin/dashboard')
  }
}
</script>

<style scoped>
#admin-body {
  height: 95vh;
  display: flex;
  flex-direction: column;
}

.admin-content-wrapper {
  flex: 1;
  overflow: hidden;
  height: calc(100% - 80px);
  /* 固定高度，支撑侧边栏高度继承 */
}

.admin-aside {
  width:  220px !important; 
  flex-shrink: 0;
  height: 100%;
  /* 继承父容器高度，给 AdminMenu 提供高度支撑 */
}

.header-component {
  position: absolute;
  width: 100%;
  left: 0;
  right: 0;
}

.admin-content-wrapper {
  flex: 1;
  margin-top: 0;
  overflow: hidden;
  height: calc(100% - 80px);
}

.admin-main {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
}

.admin-mobile-mask {
  display: none;
}

@media (max-width: 900px) {
  #admin-body {
    height: 100dvh;
    min-height: 100dvh;
  }

  .admin-header-wrapper {
    height: auto !important;
    padding: 0 !important;
  }

  .header-component {
    position: relative;
    width: 100%;
  }

  .admin-content-wrapper {
    height: auto;
    min-height: 0;
    overflow: hidden;
  }

  .admin-aside {
    position: fixed;
    top: 0;
    bottom: 0;
    left: -250px;
    z-index: 2100;
    width: 240px !important;
    height: 100dvh;
    padding-top: 18px;
    overflow-y: auto;
    background: #545c64;
    transition: left 0.25s ease;
  }

  .admin-aside.is-mobile-open {
    left: 0;
  }

  .admin-mobile-mask {
    display: block;
    position: fixed;
    inset: 0;
    z-index: 2090;
    background: rgba(0, 0, 0, 0.42);
  }

  .admin-main {
    width: 100%;
    padding: 10px;
    overflow-x: auto;
  }
}
</style>
