import Vue from 'vue'
import App from './App'
import router from './router'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import 'echarts/theme/macarons.js'
import store from './store'
import { DEFAULT_IMAGE_URL } from './utils'
import './styles/responsive.css'

import {
  Pagination,
  Dialog,
  Menu,
  Submenu,
  MenuItem,
  MenuItemGroup,
  Input,
  Checkbox,
  CheckboxButton,
  CheckboxGroup,
  Switch,
  Select,
  Option,
  Button,
  ButtonGroup,
  Table,
  TableColumn,
  Tooltip,
  Breadcrumb,
  BreadcrumbItem,
  Form,
  FormItem,
  Tabs,
  TabPane,
  Tag,
  Tree,
  Alert,
  Icon,
  Row,
  Col,
  Upload,
  Progress,
  Spinner,
  Badge,
  Card,
  Rate,
  Steps,
  Step,
  Carousel,
  CarouselItem,
  Container,
  Header,
  Aside,
  Main,
  Footer,
  Timeline,
  TimelineItem,
  Link,
  Divider,
  Image,
  Loading,
  MessageBox,
  Message,
  Notification,
  RadioGroup,
  DatePicker,
  Radio,
  Descriptions,
  DescriptionsItem
} from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(Pagination)
Vue.use(Dialog)
Vue.use(Menu)
Vue.use(Submenu)
Vue.use(MenuItem)
Vue.use(MenuItemGroup)
Vue.use(Input)
Vue.use(Checkbox)
Vue.use(CheckboxButton)
Vue.use(CheckboxGroup)
Vue.use(Switch)
Vue.use(Select)
Vue.use(Option)
Vue.use(Button)
Vue.use(ButtonGroup)
Vue.use(Table)
Vue.use(TableColumn)
Vue.use(Tooltip)
Vue.use(Breadcrumb)
Vue.use(BreadcrumbItem)
Vue.use(Form)
Vue.use(FormItem)
Vue.use(Tabs)
Vue.use(Radio)
Vue.use(RadioGroup)
Vue.use(TabPane)
Vue.use(Tag)
Vue.use(Tree)
Vue.use(Alert)
Vue.use(Icon)
Vue.use(Row)
Vue.use(Col)
Vue.use(Upload)
Vue.use(Progress)
Vue.use(Spinner)
Vue.use(Badge)
Vue.use(Card)
Vue.use(Rate)
Vue.use(Steps)
Vue.use(Step)
Vue.use(DatePicker)
Vue.use(Carousel)
Vue.use(CarouselItem)
Vue.use(Container)
Vue.use(Header)
Vue.use(Aside)
Vue.use(Main)
Vue.use(Footer)
Vue.use(Timeline)
Vue.use(TimelineItem)
Vue.use(Link)
Vue.use(Divider)
Vue.use(Image)
Vue.use(DescriptionsItem)
Vue.use(Descriptions)

Vue.use(Loading.directive)

Vue.prototype.$loading = Loading.service
Vue.prototype.$msgbox = MessageBox
Vue.prototype.$alert = MessageBox.alert
Vue.prototype.$confirm = MessageBox.confirm
Vue.prototype.$prompt = MessageBox.prompt
Vue.prototype.$notify = Notification
Vue.prototype.$message = Message

// 注册全局图片错误处理指令
Vue.directive('img-error', {
  inserted: function (el, binding) {
    const defaultImg = binding.value || DEFAULT_IMAGE_URL
    el.addEventListener('error', function () {
      // 防止无限循环：如果当前图片已经是默认图片，则不再替换
      if (el.src !== defaultImg) {
        el.src = defaultImg
      }
    })
    // 如果图片加载失败，立即替换
    if (el.complete && el.naturalWidth === 0) {
      el.src = defaultImg
    }
  },
  update: function (el, binding) {
    const defaultImg = binding.value || DEFAULT_IMAGE_URL
    // 当 src 更新时，重新绑定错误处理
    el.addEventListener('error', function () {
      if (el.src !== defaultImg) {
        el.src = defaultImg
      }
    })
  }
})

// 全局处理所有 img 标签的错误（自动应用）
Vue.directive('auto-img-error', {
  inserted: function (el) {
    el.addEventListener('error', function () {
      if (el.src !== DEFAULT_IMAGE_URL) {
        el.src = DEFAULT_IMAGE_URL
      }
    })
    // 如果图片已经加载失败，立即替换
    if (el.complete && el.naturalWidth === 0) {
      el.src = DEFAULT_IMAGE_URL
    }
  }
})

// 将默认图片地址挂载到 Vue 原型上，方便组件使用
Vue.prototype.$defaultImage = DEFAULT_IMAGE_URL

// 全局监听所有图片加载错误（包括动态插入的图片）
// 使用事件委托，监听 document 上的错误事件
document.addEventListener('error', function (e) {
  if (e.target.tagName === 'IMG' && e.target.src !== DEFAULT_IMAGE_URL) {
    e.target.src = DEFAULT_IMAGE_URL
  }
}, true)

var axios = require('axios')
//定义前端发给后端的请求路径前缀
axios.defaults.baseURL = '/api'
// 使请求带上凭证信息
axios.defaults.withCredentials = true

Vue.prototype.$axios = axios
Vue.config.productionTip = false
Vue.use(mavonEditor)

router.beforeEach((to, from, next) => {
  // 如果已登录且访问登录页，重定向到管理页面
  if (store.state.username && to.path.startsWith('/login')) {
    // // 先重定向到 /admin，让路由守卫处理菜单加载和认证
    // // 如果菜单已加载，直接跳转到目标页面，否则让路由守卫处理
    // if (store.state.adminMenus.length > 0) {
    //   next({
    //     path: '/admin/my/index'
    //   })
    // } else {
    //   // 菜单未加载，先加载菜单，然后跳转
    //   initAdminMenu(router, store).then(() => {
    //     next({
    //       path: '/admin/my/index'
    //     })
    //   }).catch(() => {
    //     // 菜单加载失败，跳转到my/index（会触发404，但至少会尝试）
    //     next({
    //       path: '/admin/my/index'
    //     })
    //   })
    // }
    // return
  }

  // 如果前端没有登录信息则直接拦截，如果有则判断后端是否正常登录（防止构造参数绕过）
  if (to.meta.requireAuth) {
    if (store.state.username) {
      // 访问admin路由时，需要先加载动态菜单
      if (to.path.startsWith('/admin')) {
        // 如果菜单还未加载，先加载菜单
        if (store.state.adminMenus.length === 0) {
          initAdminMenu(router, store).then(() => {
            // 菜单加载完成后，验证认证
            axios.get('/authentication').then(resp => {
              if (resp) {
                // 如果访问的是 /admin，重定向到 my/index
                if (to.path === '/admin') {
                  next({
                    path: '/admin/my/index',
                    replace: true
                  })
                } else {
                  // 路由已添加，使用 next(to) 重新匹配路由
                  next({ ...to, replace: true })
                }
              } else {
                // 认证失败，跳转登录页
                next({
                  path: '/login',
                  query: { redirect: to.fullPath }
                })
              }
            }).catch(error => {
              // 任何错误（302/401/500）都跳回登录页
              next({
                path: '/login',
                query: { redirect: to.fullPath }
              })
            })
          }).catch(error => {
            // 菜单加载失败，跳转登录页
            next({
              path: '/login',
              query: { redirect: to.fullPath }
            })
          })
        } else {
          // 菜单已加载，直接验证认证
          axios.get('/authentication').then(resp => {
            if (resp) {
              // 如果访问的是 /admin，重定向到 my/index
              if (to.path === '/admin') {
                next({
                  path: '/admin/my/index',
                  replace: true
                })
              } else {
                next()
              }
            } else {
              // 认证失败，跳转登录页
              next({
                path: '/login',
                query: { redirect: to.fullPath }
              })
            }
          }).catch(error => {
            // 任何错误（302/401/500）都跳回登录页
            next({
              path: '/login',
              query: { redirect: to.fullPath }
            })
          })
        }
      } else {
        // 非admin路由，直接验证认证
        axios.get('/authentication').then(resp => {
          if (resp) {
            next()
          } else {
            // 认证失败，跳转登录页
            next({
              path: '/login',
              query: { redirect: to.fullPath }
            })
          }
        }).catch(error => {
          // 任何错误（302/401/500）都跳回登录页
          next({
            path: '/login',
            query: { redirect: to.fullPath }
          })
        })
      }
    } else {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
  } else {
    next()
  }
}
)

// // 配置了全局http拦截器，当任何api请求失败时，都会返回登录页面
// axios.interceptors.response.use(
//   response => {
//     return response
//   },
//   error => {
//     if (error) {
//       store.commit('logout')
//       router.replace('/login')
//     }
//     // 返回接口返回的错误信息
//     return Promise.reject(error)
//   })

// 侧边栏的菜单数据通过调用后端的/admin/menu接口获取
const initAdminMenu = (router, store) => {
  // 防止重复触发加载菜单操作
  if (store.state.adminMenus.length > 0) {
    return Promise.resolve()
  }
  return axios.get('/admin/menu').then(resp => {
    if (resp && resp.data.code === 200) {
      var fmtRoutes = formatRoutes(resp.data.data.list)
      router.addRoutes(fmtRoutes)
      store.commit('initAdminMenu', fmtRoutes)
    }
  })
}

// 新增的组件的访问路径设置
const formatRoutes = (routes) => {
  let fmtRoutes = []
  routes.forEach(route => {
    if (route.children) {
      route.children = formatRoutes(route.children)
    }

    let fmtRoute = {
      path: route.path,
      component: resolve => {
        try {
          const req = require.context('./components/admin', true, /\.vue$/)
          const directKey = './' + route.component + '.vue'
          let mod = null
          if (req.keys().includes(directKey)) {
            mod = req(directKey)
          } else {
            const match = req.keys().find(k => k.endsWith('/' + route.component + '.vue'))
            if (match) mod = req(match)
          }
          if (mod) {
            resolve(mod.default || mod)
            return
          }
        } catch (e) {
          console.error('动态加载组件失败：', e)
        }
        // 回退到原有的动态加载方式（仍然可能抛出找不到模块的错误）
        require(['./components/admin/' + route.component + '.vue'], resolve)
      },
      name: route.name,
      nameZh: route.nameZh,
      iconCls: route.iconCls,
      meta: {
        requireAuth: true
      },
      children: route.children
    }
    fmtRoutes.push(fmtRoute)
  })
  return fmtRoutes
}

/* eslint-disable no-new */
new Vue({
  el: '#app',
  render: h => h(App),
  router,
  store,
  components: { App },
  template: '<App/>'
})
