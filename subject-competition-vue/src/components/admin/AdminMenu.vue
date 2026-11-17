<!-- 后台侧边导航栏组件 -->
<template>
  <el-menu :default-active="currentPath" class="el-menu-admin" router mode="vertical" background-color="#545c64"
    text-color="#fff" active-text-color="#ffd04b" :collapse="isCollapse">
    <!--index 没有用但是必需字段-->
    <el-submenu v-for="(item, i) in adminMenus" :key="i" :index="(i).toString()" style="text-align: left">
      <span slot="title" style="font-size: 18px;">
        <i :class="item.iconCls"></i>
        {{ item.nameZh }}
      </span>
      <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
        <i :class="child.icon"></i>
        {{ child.nameZh }}
      </el-menu-item>
    </el-submenu>
  </el-menu>
</template>

<script>
export default {
  name: 'AdminMenu',
  data() {
    return {
      isCollapse: false
    }
  },
  computed: {
    // 从store的index.js中获取菜单数据
    adminMenus() {
      return this.$store.state.adminMenus
    },
    currentPath() {
      return this.$route.path
    }
  }
}
</script>

<style scoped>
.el-menu-admin {
  border-radius: 5px;
  height: calc(100% - 20px);
  margin-top: 20px;
  padding-top: 0;
  overflow: visible;
}

/* 确保菜单列表对齐，调整内边距适配窄宽度 */
.el-menu-admin>>>ul {
  margin-top: 0;
  padding-top: 0;
}

/* 保持滚动功能，适配窄宽度 */
.el-menu-admin>>>.el-menu {
  overflow-y: auto;
  overflow-x: hidden;
  height: 100%;
}

/* 可选：调整菜单标题字体大小，适配窄侧边栏 */
.el-menu-admin>>>.el-submenu__title {
  font-size: 16px !important;
  /* 原18px，缩小后更适配窄宽度 */
  padding: 0 15px !important;
  /* 调整左右内边距，避免文字溢出 */
}

/* 可选：调整子菜单字体大小和内边距 */
.el-menu-admin>>>.el-menu-item {
  font-size: 14px !important;
  padding: 0 20px !important;
  /* 优化子菜单文字对齐 */
}

/* 可选：美化滚动条（保持原有样式，不影响功能） */
.el-menu-admin>>>.el-menu::-webkit-scrollbar {
  width: 6px;
}

.el-menu-admin>>>.el-menu::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 3px;
}

.el-menu-admin>>>.el-menu::-webkit-scrollbar-track {
  background-color: transparent;
}
</style>