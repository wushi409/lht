import request from './request'

export const companyApi = {
  getProfile() {
    return request({
      url: '/companies/me',
      method: 'get'
    })
  },

  updateProfile(data) {
    return request({
      url: '/companies/me',
      method: 'put',
      data
    })
  },

  createJob(data) {
    return request({
      url: '/companies/me/jobs',
      method: 'post',
      data
    })
  },

  updateJob(id, data) {
    return request({
      url: `/companies/me/jobs/${id}`,
      method: 'put',
      data
    })
  },

  changeJobStatus(id, status) {
    return request({
      url: `/companies/me/jobs/${id}/status`,
      method: 'post',
      params: { status }
    })
  },

  updateApplicationStatus(id, data) {
    return request({
      url: `/applications/${id}/status`,
      method: 'put',
      data
    })
  },

  listApplications(params) {
    return request({
      url: '/companies/me/applications',
      method: 'get',
      params
    })
  },

  getCompany(id) {
    return request({
      url: `/companies/${id}`,
      method: 'get'
    })
  },

  listApproved() {
    return request({
      url: '/companies',
      method: 'get'
    })
  }
}


