import axios from 'axios'
import { getToken, removeToken } from './auth'
import { Message } from 'element-ui'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(config => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 401) {
      removeToken()
      router.push('/login')
      return Promise.reject(new Error(res.msg))
    }
    if (res.code !== 200) {
      Message.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg))
    }
    return res
  },
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        removeToken()
        router.push('/login')
      }
      Message.error(error.response.data?.msg || '请求失败')
    } else {
      Message.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
