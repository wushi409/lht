import request from './request'

export const adminApi = {
  getPendingCompanies() {
    return request.get('/admin/companies/pending')
  },

  reviewCompany(id, approved, reason) {
    return request.post(`/admin/companies/${id}/approve`, null, {
      params: { approved, reason }
    })
  },

  getStatsSummary() {
    return request.get('/admin/stats/summary')
  },

  getStatsOverview() {
    return request.get('/admin/stats/overview')
  },

  createBooth(data) {
    return request.post('/booths', data)
  },

  listBooths(fairId) {
    return request.get(`/job-fairs/${fairId}/booths`)
  },

  checkinBooth(id) {
    return request.post(`/booths/${id}/checkin`)
  }
}
