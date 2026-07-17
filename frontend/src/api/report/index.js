import request from '@/utils/request'

// ===== 我的报表 =====
export function page(query) {
  return request.get('/report/page', { params: query })
}

export function generate(data) {
  return request.post('/report/generate', data)
}

export function approve(id, approvedBy) {
  return request.put('/report/approve/' + id, null, { params: { approvedBy } })
}

export function remove(id) {
  return request.delete('/report/' + id)
}

export function exportUrl(reportType) {
  return '/api/report/export' + (reportType ? '?reportType=' + reportType : '')
}

export function downloadUrl(id) {
  return '/api/report/download/' + id
}

// ===== 自动报表任务 =====
export function taskPage(query) {
  return request.get('/report/task/page', { params: query })
}

export function taskGet(id) {
  return request.get('/report/task/' + id)
}

export function taskSave(data) {
  return request.post('/report/task', data)
}

export function taskUpdate(data) {
  return request.put('/report/task', data)
}

export function taskRemove(id) {
  return request.delete('/report/task/' + id)
}

export function taskEnable(id) {
  return request.put('/report/task/enable/' + id)
}

export function taskDisable(id) {
  return request.put('/report/task/disable/' + id)
}

export function taskRunOnce(id) {
  return request.post('/report/task/run/' + id)
}
