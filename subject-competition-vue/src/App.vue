<!-- main.js操作完成后，挂载app.vue到index.html的挂载点-->
<template>
  <div id="app">
    <router-view v-if="isRouterAlive"/>
    <!-- 用户画像问卷表单 -->
    <UserProfileForm />
  </div>
</template>
<script>

  import NavMenu from '@/components/common/NavMenu'
  import UserProfileForm from '@/components/ai/UserProfileForm'

  export default {
    name: 'App',
    provide () {
      return {
        reload: this.reload
      }
    },
    data () {
      return {
        isRouterAlive: true
      }
    },
    methods: {
      reload () {
        this.isRouterAlive = false
        this.$nextTick(function () {
          this.isRouterAlive = true
        })
      }
    },
    components: {NavMenu, UserProfileForm}
  }
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    /*margin-top: 10px;*/
    min-height: 100vh;
    width: 100%;
    overflow-x: hidden;
  }

  /* 隐藏全局滚动条，但保持可滚动 */
  html {
    overflow-y: scroll;
    -ms-overflow-style: none;
    scrollbar-width: none;
  }

  html::-webkit-scrollbar {
    display: none;
  }

  body {
    overflow-y: scroll;
    -ms-overflow-style: none;
    scrollbar-width: none;
  }

  body::-webkit-scrollbar {
    display: none;
  }

  /* 响应式适配 */
  @media (max-width: 768px) {
    #app {
      text-align: left;
    }
  }
</style>
