<!-- 前台竞赛一览的侧边框组件 -->
<template>
  <el-menu class="categories" default-active="0" @select="handleSelect" active-text-color="red">
    <el-menu-item index="0">
      <i class="el-icon-menu"></i>
      <span slot="title">全部</span>
    </el-menu-item>
    <el-menu-item v-for="(item, i) in categorylist" :key="i + 1" :index="(i + 1).toString()">
      <i class="el-icon-menu"></i>
      <span slot="title">{{ item.name }}</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
export default {
  name: 'SideMenu',
  data() {
    return {
      categorylist: [],
      cid: ''
    }
  },
  mounted() {
    this.loadCategorys()
    this.handleSelect(0)
  },
  methods: {
    loadCategorys() {
      var _this = this
      this.$axios.get('/categoryList').then(resp => {
        if (resp && resp.data.code === 200) {
          _this.categorylist = resp.data.data.list
        }
      })
    },
    handleSelect(key, keyPath) {
      this.cid = key
      this.$emit('indexSelect')
    }
  }
}
</script>

<style scoped>
.categories {
  /* margin-left: 50%; */
  width: 100%;
  /* 填满父容器（el-aside）的宽度 */
  margin-top: 20px;
  /* 保留合适的上边距，和原布局协调 */
  overflow: hidden !important;
}

.categories::-webkit-scrollbar {
  width: 0 !important;
  display: none !important;
}
</style>
