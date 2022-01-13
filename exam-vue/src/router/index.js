import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path*',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/register'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '控制台', icon: 'dashboard', affix: true }
      }
    ]
  },

  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: '个人资料', icon: 'user', noCache: true }
      }
    ]
  }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [

  {
    path: '/start/exam',
    component: () => import('@/views/paper/exam/exam'),
    name: 'StartExam',
    meta: { title: '开始考试' },
    hidden: true
  },

  {
    path: '/my',
    component: Layout,
    name: 'Online',
    hidden: true,
    children: [

      {
        path: 'exam/prepare',
        component: () => import('@/views/paper/exam/preview'),
        name: 'ExamPrepare',
        meta: { title: '准备考试', noCache: true, activeMenu: '/my/exam/online' }
      },

      {
        path: 'exam/result',
        component: () => import('@/views/paper/exam/result'),
        name: 'ExamResult',
        meta: { title: '考试结果', noCache: true }
      }

    ]
  },

  {
    path: '/exam',
    component: Layout,
    name: 'Manage',
    hidden: true,
    children: [

      {
        path: 'save/repo',
        component: () => import('@/views/qu/repo/form'),
        name: 'SaveRepo',
        meta: { title: '保存题库', noCache: true, activeMenu: '/exam/list/repo' }
      },

      {
        path: 'save/qu',
        component: () => import('@/views/qu/qu/form'),
        name: 'SaveQu',
        meta: { title: '保存试题', noCache: true, activeMenu: '/exam/list/qu' }
      },

      {
        path: 'save/exam',
        component: () => import('@/views/exam/exam/form'),
        name: 'SaveExam',
        meta: { title: '保存考试', noCache: true, activeMenu: '/exam/list/exam' }
      },

      {
        path: 'list/exam/user',
        component: () => import('@/views/user/exam'),
        name: 'ListExamUser',
        meta: { title: '考试人员', noCache: true, activeMenu: '/exam/list/exam' }
      }
    ]
  }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
