import router from './router'
import store from './store'
import { getToken } from './utils/auth'

const whiteList = ['/login']

router.beforeEach(async (to, from, next) => {
  const hasToken = getToken()
  if (hasToken) {
    if (to.path === '/login') {
      next('/dashboard')
    } else {
      const hasRoles = store.state.user.roles && store.state.user.roles.length > 0
      if (hasRoles) {
        next()
      } else {
        try {
          await store.dispatch('user/getUserInfo')
          next({ ...to, replace: true })
        } catch {
          store.dispatch('user/logout')
          next('/login?redirect=' + to.path)
        }
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next('/login')
    }
  }
})
