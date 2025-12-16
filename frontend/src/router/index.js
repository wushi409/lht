import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  // 登录注册
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register/student',
    name: 'RegisterStudent',
    component: () => import('@/views/auth/RegisterStudent.vue'),
    meta: { public: true }
  },
  {
    path: '/register/company',
    name: 'RegisterCompany',
    component: () => import('@/views/auth/RegisterCompany.vue'),
    meta: { public: true }
  },

  // 学生端
  {
    path: '/student',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/student/jobs',
    meta: { role: 'STUDENT' },
    children: [
      { path: 'profile', name: 'StudentProfile', component: () => import('@/views/student/Profile.vue') },
      { path: 'jobs', name: 'StudentJobs', component: () => import('@/views/student/Jobs.vue') },
      { path: 'companies', name: 'StudentCompanies', component: () => import('@/views/student/Companies.vue') },
      { path: 'applications', name: 'StudentApplications', component: () => import('@/views/student/Applications.vue') },
      { path: 'resumes', name: 'StudentResumes', component: () => import('@/views/student/Resumes.vue') },
      { path: 'interviews', name: 'StudentInterviews', component: () => import('@/views/student/Interviews.vue') },
      { path: 'events', name: 'StudentEvents', component: () => import('@/views/student/Events.vue') },
      { path: 'notifications', name: 'StudentNotifications', component: () => import('@/views/common/Notifications.vue') },
      { path: 'announcements', name: 'StudentAnnouncements', component: () => import('@/views/common/Announcements.vue') }
    ]
  },

  // 企业端
  {
    path: '/company',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/company/profile',
    meta: { role: 'COMPANY' },
    children: [
      { path: 'profile', name: 'CompanyProfile', component: () => import('@/views/company/Profile.vue') },
      { path: 'jobs', name: 'CompanyJobs', component: () => import('@/views/company/Jobs.vue') },
      { path: 'applications', name: 'CompanyApplications', component: () => import('@/views/company/Applications.vue') },
      { path: 'interviews', name: 'CompanyInterviews', component: () => import('@/views/company/Interviews.vue') },
      { path: 'notifications', name: 'CompanyNotifications', component: () => import('@/views/common/Notifications.vue') },
      { path: 'announcements', name: 'CompanyAnnouncements', component: () => import('@/views/common/Announcements.vue') }
    ]
  },

  // 管理员端
  {
    path: '/admin',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/admin/stats',
    meta: { role: 'ADMIN' },
    children: [
      { path: 'stats', name: 'AdminStats', component: () => import('@/views/admin/Stats.vue') },
      { path: 'companies', name: 'AdminCompanies', component: () => import('@/views/admin/CompanyAudit.vue') },
      { path: 'booths', name: 'AdminBooths', component: () => import('@/views/admin/Booths.vue') },
      { path: 'checkin', name: 'AdminCheckin', component: () => import('@/views/admin/Checkin.vue') },
      { path: 'announcements', name: 'AdminAnnouncements', component: () => import('@/views/admin/Announcements.vue') },
      { path: 'export', name: 'AdminExport', component: () => import('@/views/admin/Export.vue') },
      { path: 'audit-logs', name: 'AdminAuditLogs', component: () => import('@/views/admin/AuditLogs.vue') },
      { path: 'notifications', name: 'AdminNotifications', component: () => import('@/views/common/Notifications.vue') }
    ]
  },

  // 公共门户
  {
    path: '/',
    component: () => import('@/layout/PublicLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/Home.vue') },
      { path: 'public/jobs', name: 'PublicJobs', component: () => import('@/views/public/Jobs.vue') },
      { path: 'public/seminars', name: 'PublicSeminars', component: () => import('@/views/public/Seminars.vue') },
      { path: 'public/fairs', name: 'PublicFairs', component: () => import('@/views/public/Fairs.vue') },
      { path: 'public/fairs/:id', name: 'PublicFairDetail', component: () => import('@/views/public/FairDetail.vue') },
      { path: 'public/announcements', name: 'PublicAnnouncements', component: () => import('@/views/public/Announcements.vue') },
      { path: 'public/companies', name: 'PublicCompanies', component: () => import('@/views/public/Companies.vue') }
    ],
    meta: { public: true }
  },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('@/views/error/404.vue'), meta: { public: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  const isPublic = to.meta.public

  if (isPublic) {
    if (token && (to.path === '/login' || to.path.startsWith('/register'))) {
      const role = userStore.role
      if (role === 'COMPANY') return next('/company/profile')
      if (role === 'ADMIN') return next('/admin/stats')
      return next('/student/jobs')
    }
    return next()
  }

  if (!token) return next('/login')

  const requiredRole = to.matched.find(r => r.meta.role)?.meta.role
  if (requiredRole && userStore.role !== requiredRole) {
    const role = userStore.role
    if (role === 'STUDENT') return next('/student/jobs')
    if (role === 'COMPANY') return next('/company/profile')
    if (role === 'ADMIN') return next('/admin/stats')
    return next('/login')
  }

  next()
})

export default router
