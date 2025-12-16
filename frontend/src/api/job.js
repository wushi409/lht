import request from './request'

export function getJobs(params) {
  return request({
    url: '/jobs',
    method: 'get',
    params
  })
}

export function getJobDetail(id) {
  return request({
    url: `/jobs/${id}`,
    method: 'get'
  })
}
