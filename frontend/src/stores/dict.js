export const JobStatus = {
  PUBLISHED: { value: 'PUBLISHED', label: '已发布', type: 'success' },
  UNPUBLISHED: { value: 'UNPUBLISHED', label: '未发布', type: 'info' }
}

export const ApplicationStatus = {
  SUBMITTED: { value: 'SUBMITTED', label: '已投递', type: 'info' },
  VIEWED: { value: 'VIEWED', label: '查看', type: '' },
  INTERVIEW: { value: 'INTERVIEW', label: '已面试', type: 'primary' },
  HIRED: { value: 'HIRED', label: '已录用', type: 'success' },
  REJECTED: { value: 'REJECTED', label: '不合适', type: 'danger' },
  WITHDRAWN: { value: 'WITHDRAWN', label: '已撤回', type: 'info' }
}
