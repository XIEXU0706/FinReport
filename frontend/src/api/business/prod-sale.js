import request from '@/utils/request'

export function salePage(query) {
  return request.get('/business/prod-sale/page', { params: query })
}
