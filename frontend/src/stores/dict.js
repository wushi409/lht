export const JobStatus = {
  OPEN: { value: 'OPEN', label: '招聘中', type: 'success' },
  CLOSED: { value: 'CLOSED', label: '已结束', type: 'info' },
  PAUSED: { value: 'PAUSED', label: '暂停', type: 'warning' }
}

export const ApplicationStatus = {
  PENDING: { value: 'PENDING', label: '已投递', type: 'info' },
  REVIEWING: { value: 'REVIEWING', label: '筛选中', type: 'warning' },
  INTERVIEW: { value: 'INTERVIEW', label: '面试中', type: 'primary' },
  OFFERED: { value: 'OFFERED', label: '已录用', type: 'success' },
  REJECTED: { value: 'REJECTED', label: '不合适', type: 'danger' },
  WITHDRAWN: { value: 'WITHDRAWN', label: '已撤回', type: 'info' }
}

// Add other enums as needed
