import request from '@/utils/request'

export function page(query) {
  return request.get('/alert/rule/page', { params: query })
}

export function save(data) {
  return request.post('/alert/rule', data)
}

export function update(data) {
  return request.put('/alert/rule', data)
}

export function remove(id) {
  return request.delete('/alert/rule/' + id)
}
