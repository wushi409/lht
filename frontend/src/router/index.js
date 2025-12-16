import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import MainLayout from '@/layout/MainLayout.vue'


const routes = [
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { public: true }
  },
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
  {
    path: '/',
    component: MainLayout,
    children: [
      // Dashboard
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      // Student Routes
      {
        path: 'student',
        redirect: '/student/dashboard',
        meta: { role: 'STUDENT' },
        children: [
          {
            path: 'dashboard',
            name: 'StudentDashboard',
            component: () => import('@/views/Dashboard.vue')
          },
          {
            path: 'profile',
            name: 'StudentProfile',
            component: () => import('@/views/student/Profile.vue') // Lazy load
          },
          {
            path: 'jobs',
            name: 'StudentJobs',
            component: () => import('@/views/student/Jobs.vue')
          },
          {
            path: 'events',
            name: 'StudentEvents',
            component: () => import('@/views/student/Events.vue')
          },
          {
            path: 'applications',
            name: 'StudentApplications',
            component: () => import('@/views/student/Applications.vue')
          },
          {
            path: 'resumes',
            name: 'StudentResumes',
            component: () => import('@/views/student/Resumes.vue')
          },
          {
            path: 'interviews',
            name: 'StudentInterviews',
            component: () => import('@/views/student/Interviews.vue')
          },
          {
            path: 'notifications',
            name: 'StudentNotifications',
            component: () => import('@/views/common/Notifications.vue')
          },
          {
            path: 'announcements',
            name: 'StudentAnnouncements',
            component: () => import('@/views/common/Announcements.vue')
          }
        ]
      },
      // Company Routes
      {
        path: 'company',
        redirect: '/company/dashboard',
        meta: { role: 'COMPANY' },
        children: [
          {
            path: 'dashboard',
            name: 'CompanyDashboard',
            component: () => import('@/views/Dashboard.vue')
          },
          {
            path: 'profile',
            name: 'CompanyProfile',
            component: () => import('@/views/company/Profile.vue')
          },
          {
            path: 'jobs',
            name: 'CompanyJobs',
            component: () => import('@/views/company/Jobs.vue')
          },
          {
            path: 'applications',
            name: 'CompanyApplications',
            component: () => import('@/views/company/Applications.vue')
          },
          {
            path: 'interviews',
            name: 'CompanyInterviews',
            component: () => import('@/views/company/Interviews.vue')
          },
          {
            path: 'notifications',
            name: 'CompanyNotifications',
            component: () => import('@/views/common/Notifications.vue')
          },
          {
            path: 'announcements',
            name: 'CompanyAnnouncements',
            component: () => import('@/views/common/Announcements.vue')
          }
        ]
      },
      // Admin Routes
      {
        path: 'admin',
        redirect: '/admin/dashboard',
        meta: { role: 'ADMIN' },
        children: [
          {
            path: 'dashboard',
            name: 'AdminDashboard',
            component: () => import('@/views/Dashboard.vue')
          },
          {
            path: 'companies',
            name: 'AdminCompanies',
            component: () => import('@/views/admin/CompanyAudit.vue')
          },
          {
            path: 'stats',
            name: 'AdminStats',
            component: () => import('@/views/admin/Stats.vue')
          },
          {
            path: 'checkin',
            name: 'AdminCheckin',
            component: () => import('@/views/admin/Checkin.vue')
          },
          {
            path: 'announcements',
            name: 'AdminAnnouncements',
            component: () => import('@/views/admin/Announcements.vue')
          },
          {
            path: 'export',
            name: 'AdminExport',
            component: () => import('@/views/admin/Export.vue')
          },
          {
            path: 'reviews',
            name: 'AdminReviews',
            component: () => import('@/views/admin/Reviews.vue')
          },
          {
            path: 'notifications',
            name: 'AdminNotifications',
            component: () => import('@/views/common/Notifications.vue')
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { public: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  const isPublic = to.meta.public

  // 如果访问根路径，根据登录状态重定向
  if (to.path === '/') {
    if (!token) {
      return next('/home')
    } else {
      const role = userStore.role
      if (role === 'STUDENT') return next('/student/dashboard')
      else if (role === 'COMPANY') return next('/company/dashboard')
      else if (role === 'ADMIN') return next('/admin/dashboard')
      else return next('/home')
    }
  }

  // 未登录且访问非公共页面，跳转登录
  if (!token && !isPublic) {
    return next('/login')
  }

  // 已登录访问登录页，跳转到对应角色首页
  if (token && to.path === '/login') {
    const role = userStore.role
    if (role === 'STUDENT') return next('/student/dashboard')
    else if (role === 'COMPANY') return next('/company/dashboard')
    else if (role === 'ADMIN') return next('/admin/dashboard')
    else return next('/')
  }

  // 检查角色权限
  const requiredRole = to.matched.find(record => record.meta.role)?.meta.role
  if (requiredRole && userStore.role !== requiredRole) {
    const role = userStore.role
    if (role === 'STUDENT') return next('/student/dashboard')
    else if (role === 'COMPANY') return next('/company/dashboard')
    else if (role === 'ADMIN') return next('/admin/dashboard')
    else return next('/login')
  }

  next()
})

export default router
