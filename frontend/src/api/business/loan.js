import request from '@/utils/request'

export function page(query) {
  return request.get('/business/loan/page', { params: query })
}

export function get(id) {
  return request.get('/business/loan/' + id)
}

export function save(data) {
  return request.post('/business/loan', data)
}

export function update(data) {
  return request.put('/business/loan', data)
}

export function remove(id) {
  return request.delete('/business/loan/' + id)
}
