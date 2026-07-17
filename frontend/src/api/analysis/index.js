import request from '@/utils/request'

export function trade(params) {
  return request.get('/analysis/trade', { params })
}

export function customer() {
  return request.get('/analysis/customer')
}

export function loan() {
  return request.get('/analysis/loan')
}

export function product() {
  return request.get('/analysis/product')
}
