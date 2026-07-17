import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

const routes = [
  {
    path: '/login',
    component: () => import('@/views/login/Login'),
    hidden: true
  },
  {
    path: '/',
    component: () => import('@/layout/Layout'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Index'),
        meta: { title: '首页', icon: 'el-icon-s-home' }
      }
    ]
  },
  {
    path: '/business',
    component: () => import('@/layout/Layout'),
    meta: { title: '业务数据', icon: 'el-icon-s-data' },
    children: [
      { path: 'customer', name: 'Customer', component: () => import('@/views/business/Customer'), meta: { title: '客户管理' } },
      { path: 'account', name: 'Account', component: () => import('@/views/business/Account'), meta: { title: '账户管理' } },
      { path: 'trans-log', name: 'TransLog', component: () => import('@/views/business/TransLog'), meta: { title: '交易流水' } },
      { path: 'loan', name: 'Loan', component: () => import('@/views/business/Loan'), meta: { title: '贷款业务' } },
      { path: 'product', name: 'Product', component: () => import('@/views/business/Product'), meta: { title: '产品管理' } },
      { path: 'branch', name: 'Branch', component: () => import('@/views/business/Branch'), meta: { title: '支行信息' } },
      { path: 'prod-sale', name: 'ProductSale', component: () => import('@/views/business/ProductSale'), meta: { title: '产品销售记录' } }
    ]
  },
  {
    path: '/analysis',
    component: () => import('@/layout/Layout'),
    meta: { title: '经营分析', icon: 'el-icon-s-marketing' },
    children: [
      { path: 'center', name: 'AnalysisCenter', component: () => import('@/views/analysis/Center'), meta: { title: '分析中心' } }
    ]
  },
  {
    path: '/alert',
    component: () => import('@/layout/Layout'),
    meta: { title: '预警中心', icon: 'el-icon-warning' },
    children: [
      { path: 'log', name: 'AlertLog', component: () => import('@/views/alert/AlertLog'), meta: { title: '预警列表' } },
      { path: 'rule', name: 'AlertRule', component: () => import('@/views/alert/AlertRule'), meta: { title: '规则配置' } }
    ]
  },
  {
    path: '/report',
    component: () => import('@/layout/Layout'),
    meta: { title: '报表中心', icon: 'el-icon-document' },
    children: [
      { path: 'generate', name: 'ReportGenerate', component: () => import('@/views/report/Generate'), meta: { title: '报表生成' } },
      { path: 'history', name: 'ReportHistory', component: () => import('@/views/report/History'), meta: { title: '我的报表' } },
      { path: 'task', name: 'ReportTask', component: () => import('@/views/report/Task'), meta: { title: '自动报表任务' } }
    ]
  },
  {
    path: '/system',
    component: () => import('@/layout/Layout'),
    meta: { title: '系统管理', icon: 'el-icon-setting' },
    children: [
      { path: 'user', name: 'SysUser', component: () => import('@/views/system/User'), meta: { title: '用户管理' } },
      { path: 'role', name: 'SysRole', component: () => import('@/views/system/Role'), meta: { title: '角色管理' } },
      { path: 'oper-log', name: 'OperLog', component: () => import('@/views/system/OperLog'), meta: { title: '操作日志' } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/system/Profile'), meta: { title: '个人中心' } }
    ]
  }
]

const router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
