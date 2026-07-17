import request from '@/utils/request'

export function page(query) {
  return request.get('/business/trans-log/page', { params: query })
}

export function get(id) {
  return request.get(`/business/trans-log/${id}`)
}
