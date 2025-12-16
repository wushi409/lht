import request from './request'

// Profile
export function getProfile() {
  return request({
    url: '/students/me',
    method: 'get'
  })
}

export function updateProfile(data) {
  return request({
    url: '/students/me',
    method: 'put',
    data
  })
}

// Applications
export function getMyApplications() {
  return request({
    url: '/students/me/applications',
    method: 'get'
  })
}

export function applyJob(data) {
  return request({
    url: '/students/me/applications',
    method: 'post',
    data
  })
}

export function withdrawApplication(id) {
  return request({
    url: `/students/me/applications/${id}`,
    method: 'delete'
  })
}

// Favorites - Jobs
export function getFavoriteJobs() {
  return request({
    url: '/students/me/favorites/jobs',
    method: 'get'
  })
}

export function addFavoriteJob(jobId) {
  return request({
    url: `/students/me/favorites/jobs/${jobId}`,
    method: 'post'
  })
}

export function removeFavoriteJob(jobId) {
  return request({
    url: `/students/me/favorites/jobs/${jobId}`,
    method: 'delete'
  })
}
