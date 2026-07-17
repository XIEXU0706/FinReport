import request from '@/utils/request'

export function page(query) {
  return request.get('/business/branch/page', { params: query })
}

export function get(id) {
  return request.get('/business/branch/' + id)
}

export function save(data) {
  return request.post('/business/branch', data)
}

export function update(data) {
  return request.put('/business/branch', data)
}

export function remove(id) {
  return request.delete('/business/branch/' + id)
}
