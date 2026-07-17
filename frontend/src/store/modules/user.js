import { getToken, setToken, removeToken } from '@/utils/auth'
import { login as loginApi, getUserInfo } from '@/api/auth'

export default {
  namespaced: true,
  state: {
    token: getToken() || '',
    userId: null,
    username: '',
    roles: []
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
    },
    SET_USER_INFO(state, { userId, username, roles }) {
      state.userId = userId
      state.username = username
      state.roles = roles
    },
    RESET(state) {
      state.token = ''
      state.userId = null
      state.username = ''
      state.roles = []
    }
  },
  actions: {
    async login({ commit }, { username, password }) {
      const res = await loginApi({ username, password })
      const { token, userId, roles } = res.data
      setToken(token)
      commit('SET_TOKEN', token)
      commit('SET_USER_INFO', { userId, username, roles })
      return res.data
    },
    async getUserInfo({ commit, state }) {
      const res = await getUserInfo()
      const { userId, username, roles } = res.data
      commit('SET_USER_INFO', { userId, username, roles })
      return res.data
    },
    logout({ commit }) {
      removeToken()
      commit('RESET')
    }
  }
}
