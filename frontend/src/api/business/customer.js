import request from '@/utils/request'

export function page(query) {
  return request.get('/business/customer/page', { params: query })
}

export function get(id) {
  return request.get('/business/customer/' + id)
}

export function listAll() {
  return request.get('/business/customer/list')
}

export function save(data) {
  return request.post('/business/customer', data)
}

export function update(data) {
  return request.put('/business/customer', data)
}

export function remove(id) {
  return request.delete('/business/customer/' + id)
}
