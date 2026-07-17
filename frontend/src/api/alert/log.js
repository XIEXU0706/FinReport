import request from '@/utils/request'

export function page(query) {
  return request.get('/alert/log/page', { params: query })
}

export function count() {
  return request.get('/alert/log/count')
}

export function ack(id) {
  return request.put('/alert/log/ack/' + id)
}

export function resolve(id) {
  return request.put('/alert/log/resolve/' + id)
}
