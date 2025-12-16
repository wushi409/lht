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

// Resumes
export function listResumes() {
  return request({
    url: '/resumes',
    method: 'get'
  })
}

export function createResume(data) {
  return request({
    url: '/resumes',
    method: 'post',
    data
  })
}

export function updateResume(id, data) {
  return request({
    url: `/resumes/${id}`,
    method: 'put',
    data
  })
}

export function deleteResume(id) {
  return request({
    url: `/resumes/${id}`,
    method: 'delete'
  })
}

export function setDefaultResume(id) {
  return request({
    url: `/resumes/${id}/default`,
    method: 'post'
  })
}

export function uploadResumeFile(id, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: `/resumes/${id}/file`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// Interviews
export function listStudentInterviews() {
  return request({
    url: '/interviews/student/me',
    method: 'get'
  })
}

export function respondToInterview(id, status) {
  return request({
    url: `/interviews/${id}/status`,
    method: 'post',
    data: { status }
  })
}

// Companies
export function listCompanies() {
  return request({
    url: '/companies',
    method: 'get'
  })
}

