import request from '@/utils/request'

export function page(query) {
  return request.get('/system/oper-log/page', { params: query })
}
