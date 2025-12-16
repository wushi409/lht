import request from './request'

export const companyApi = {
  getProfile() {
    return request.get('/companies/me')
  },

  updateProfile(data) {
    return request.put('/companies/me', data)
  },

  createJob(data) {
    return request.post('/jobs', data)
  },

  updateJob(id, data) {
    return request.put(`/jobs/${id}`, data)
  },

  changeJobStatus(id, status) {
    return request.post(`/jobs/${id}/status`, null, { params: { status } })
  },

  updateApplicationStatus(id, data) {
    return request.post(`/applications/${id}/status`, data)
  },

  getCompany(id) {
    return request.get(`/companies/${id}`)
  },

  listApproved() {
    return request.get('/companies')
  }
}


