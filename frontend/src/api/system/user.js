import request from '@/utils/request'

export function page(query) {
  return request.get('/system/user/page', { params: query })
}

export function get(id) {
  return request.get('/system/user/' + id)
}

export function save(data) {
  return request.post('/system/user', data)
}

export function update(data) {
  return request.put('/system/user', data)
}

export function remove(id) {
  return request.delete('/system/user/' + id)
}
