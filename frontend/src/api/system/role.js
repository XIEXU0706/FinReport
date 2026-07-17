import request from '@/utils/request'

export function page(query) {
  return request.get('/system/role/page', { params: query })
}

export function save(data) {
  return request.post('/system/role', data)
}

export function update(data) {
  return request.put('/system/role', data)
}

export function remove(id) {
  return request.delete('/system/role/' + id)
}
