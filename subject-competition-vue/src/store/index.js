// 侧边栏数据由后端接口返回
import Vue from 'vue'
import Vuex from 'vuex'
Vue.use(Vuex)
export default new Vuex.Store({
  state: {
    username: window.localStorage.getItem('username') == null ? '' : JSON.parse(window.localStorage.getItem('username' || '[]')),
    user: window.localStorage.getItem('user') == null ? null : JSON.parse(window.localStorage.getItem('user')),
    adminMenus: []
  },
  mutations: {
    initAdminMenu(state, menus) {
      state.adminMenus = menus
    },
    login(state, data) {
      state.username = data
      window.localStorage.setItem('username', JSON.stringify(data))
    },
    setUser(state, user) {
      state.user = user
      window.localStorage.setItem('user', JSON.stringify(user))
    },
    logout(state) {
      // 注意不能用 null 清除，否则将无法判断 user 里具体的内容
      state.username = ''
      window.localStorage.removeItem('username')
      state.user = null
      window.localStorage.removeItem('user')
      state.adminMenus = []
    }
  },
  actions: {
  }
})
