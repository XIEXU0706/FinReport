import request from '@/utils/request'

export function page(query) {
  return request.get('/business/account/page', { params: query })
}

export function get(id) {
  return request.get('/business/account/' + id)
}

export function save(data) {
  return request.post('/business/account', data)
}

export function update(data) {
  return request.put('/business/account', data)
}

export function remove(id) {
  return request.delete('/business/account/' + id)
}

export function listAll() {
  return request.get('/business/account/list')
}
