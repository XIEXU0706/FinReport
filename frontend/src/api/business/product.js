import request from '@/utils/request'

export function page(query) {
  return request.get('/business/product/page', { params: query })
}

export function get(id) {
  return request.get('/business/product/' + id)
}

export function save(data) {
  return request.post('/business/product', data)
}

export function update(data) {
  return request.put('/business/product', data)
}

export function remove(id) {
  return request.delete('/business/product/' + id)
}
