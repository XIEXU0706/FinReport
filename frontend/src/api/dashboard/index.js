import request from '@/utils/request'

export function full() {
  return request.get('/dashboard/full')
}
