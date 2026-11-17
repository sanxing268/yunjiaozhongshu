/*
 * 路由配置文件
 * 用于定义系统中所有的页面路由以及权限控制
 * 此页编写路由规则，负责浏览器的地址跳转
 */

// 导入Vue核心库
import Vue from 'vue'
// 导入Vue Router路由插件
import Router from 'vue-router'
// 导入Home组件作为主页面容器
import Home from '../components/Home'

// 注册Router插件到Vue
Vue.use(Router)

// 解决路由重复导航的报错问题
// 保存原始的push方法
const originalPush = Router.prototype.push
// 重写push方法，添加错误捕获
Router.prototype.push = function push(location, onResolve, onReject) {
  // 如果提供了成功或失败的回调，则调用原始push方法并传递这些回调
  if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
  // 否则，调用原始push方法并捕获可能的错误，避免控制台报错
  return originalPush.call(this, location).catch(err => err)
}

/*
 * 导出默认的路由实例
 * mode: 'history' - 使用HTML5 History模式，URL不带#符号
 * routes: 定义所有路由规则
 */
export default new Router({
  // 使用history模式，需要服务器端配置支持
  mode: 'history',
  // 路由规则数组
  routes: [
    {
      // 默认路由，当访问根路径时
      path: '/',
      name: 'Default',
      // 重定向到首页
      redirect: '/home',
      component: Home
    },
    {
      // home页面路由，作为其他组件的父组件容器
      path: '/home',
      name: 'Home',
      component: Home,
      // 默认重定向到主页内容
      redirect: '/index',
      // 子路由配置
      children: [
        {
          // 系统首页路由
          path: '/index',
          name: 'AppIndex',
          // 懒加载组件，按需加载，提高首屏加载速度
          component: () => import('../components/home/AppIndex')
        },
        {
          // 公告列表页路由
          path: '/jotter',
          name: 'Jotter',
          component: () => import('../components/jotter/Articles')
        },
        {
          // 公告详情页路由
          path: '/jotter/article',
          name: 'Article',
          component: () => import('../components/jotter/ArticleDetails')
        },
        {
          // 竞赛公告列表页路由
          path: '/competition-notice',
          name: 'CompetitionNotice',
          component: () => import('../components/jotter/CompetitionNotice')
        },
        {
          // 公告编辑器路由
          path: '/admin/content/editor',
          name: 'Editor',
          component: () => import('../components/admin/content/ArticleEditor'),
          // meta元信息，用于存储路由相关的自定义数据
          meta: {
            // requireAuth: true 表示该路由需要身份验证才能访问
            requireAuth: true
          }
        },
        {
          // 竞赛列表页面路由
          path: '/library',
          name: 'Library',
          component: () => import('../components/library/LibraryIndex')
        },
        {
          // AI聊天页面路由
          path: '/ai/chat',
          name: 'AIChat',
          component: () => import('../components/ai/Chat.vue')
        },
        {
          // 用户画像详情页面路由
          path: '/ai/profile',
          name: 'ProfileDetail',
          component: () => import('../components/ai/ProfileDetail.vue'),
          meta: {
            requireAuth: true
          }
        },
        {
          // 竞赛成果列表页面路由
          path: '/achievement',
          name: 'Achievements',
          component: () => import('../components/achievement/Achievements.vue')
        },
        {
          // 竞赛成果详情页面路由
          path: '/achievement/detail',
          name: 'AchievementDetail',
          component: () => import('../components/achievement/AchievementDetail.vue')
        },
        {
          // 文件下载页面路由
          path: '/downloads',
          name: 'Downloads',
          component: () => import('../components/download/Downloads.vue')
        },
        {
          // 搜索结果页面路由
          path: '/search',
          name: 'SearchResults',
          component: () => import('../components/search/SearchResults.vue')
        }
      ]
    },
    {
      // 登录页面路由
      path: '/login',
      name: 'Login',
      component: () => import('../components/Login')
    },
    {
      // 注册页面路由
      path: '/register',
      name: 'Register',
      component: () => import('../components/Register')
    },
    {
      // 管理员页面路由
      path: '/admin',
      name: 'Admin',
      component: () => import('../components/admin/AdminIndex'),
      // 需要身份验证
      meta: {
        requireAuth: true
      },
      // 默认重定向到个人资料页面
      redirect: '/admin/my/index',
      // 管理员子路由
      children: [
        {
          // 管理仪表盘路由（使用相对路径）
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('../components/admin/dashboard/admin/index'),
          // 需要身份验证
          meta: {
            requireAuth: true
          }
        },
        {
          // 个人资料页面路由（作为备选，确保路由存在）
          path: 'my/index',
          name: 'MyIndex',
          component: () => import('../components/admin/my/index'),
          // 需要身份验证
          meta: {
            requireAuth: true
          }
        }
      ]
    },
    {
      // 通配符路由，捕获所有未匹配的路径
      // 用于显示404错误页面
      path: '*',
      component: () => import('../components/pages/Error404')
    }
  ]
})

/*
 * 路由创建函数
 * 用于动态创建新的路由实例，可以传入自定义路由配置
 * 这个函数在需要动态修改路由表时会用到，例如在用户登录后根据权限动态添加路由
 */
export const createRouter = routes => new Router({
  mode: 'history',
  // 如果传入了自定义路由，则使用传入的路由，否则使用默认路由配置
  routes: routes || [
    {
      path: '/',
      name: 'Default',
      redirect: '/home',
      component: Home
    },
    {
      // home页面路由配置（同上）
      path: '/home',
      name: 'Home',
      component: Home,
      redirect: '/index',
      children: [
        {
          path: '/index',
          name: 'AppIndex',
          component: () => import('../components/home/AppIndex')
        },
        {
          path: '/jotter',
          name: 'Jotter',
          component: () => import('../components/jotter/Articles')
        },
        {
          path: '/jotter/article',
          name: 'Article',
          component: () => import('../components/jotter/ArticleDetails')
        },
        {
          path: '/competition-notice',
          name: 'CompetitionNotice2',
          component: () => import('../components/jotter/CompetitionNotice')
        },
        {
          path: '/admin/content/editor',
          name: 'Editor',
          component: () => import('../components/admin/content/ArticleEditor'),
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/library',
          name: 'Library',
          component: () => import('../components/library/LibraryIndex')
        },
        {
          path: '/ai/chat',
          name: 'AIChat',
          component: () => import('../components/ai/Chat.vue')
        },
        {
          path: '/achievement',
          name: 'Achievements2',
          component: () => import('../components/achievement/Achievements.vue')
        },
        {
          path: '/achievement/detail',
          name: 'AchievementDetail2',
          component: () => import('../components/achievement/AchievementDetail.vue')
        },
        {
          path: '/downloads',
          name: 'Downloads2',
          component: () => import('../components/download/Downloads.vue')
        }
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../components/Login')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../components/Register')
    },
    {
      path: '/admin',
      name: 'Admin',
      component: () => import('../components/admin/AdminIndex'),
      meta: {
        requireAuth: true
      },
      // 默认重定向到个人资料页面
      redirect: '/admin/my/index',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('../components/admin/dashboard/admin/index'),
          meta: {
            requireAuth: true
          }
        },
        {
          path: 'my/index',
          name: 'MyIndex',
          component: () => import('../components/admin/my/index'),
          meta: {
            requireAuth: true
          }
        },
        {
          path: 'user/studentManagement',
          name: 'StudentManagement',
          component: () => import('../components/admin/user/StudentManagement.vue'),
          meta: {
            requireAuth: true
          }
        },
        {
          path: 'user/teamManagement',
          name: 'TeamManagement',
          component: () => import('../components/admin/user/TeamManagement.vue'),
          meta: {
            requireAuth: true
          }
        },
        {
          path: 'content/pic',
          name: 'pic',
          component: () => import('../components/admin/content/pic.vue'),
          meta: {
            requireAuth: true
          }
        },
        {
          path: 'user/teacherManagement',
          name: 'TeacherManagement',
          component: () => import('../components/admin/user/TeacherManagement.vue'),
          meta: {
            requireAuth: true
          }
        },
        {
          path: 'user/teamManagementOfTeacherAndStudent',
          name: 'TeamManagementOfTeacherAndStudent',
          component: () => import('../components/admin/user/TeamManagementOfTeacherAndStudent.vue'),
          meta: {
            requireAuth: true
          }
        }
      ]
    },
    {
      path: '*',
      component: () => import('../components/pages/Error404')
    }
  ]
})