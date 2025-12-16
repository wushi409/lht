import request from './request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function registerStudent(data) {
  return request({
    url: '/auth/register/student',
    method: 'post',
    data
  })
}

export function registerCompany(data) {
  return request({
    url: '/auth/register/company',
    method: 'post',
    data
  })
}

export function resetPassword(data) {
  return request({
    url: '/auth/reset-password',
    method: 'post',
    data
  })
}
