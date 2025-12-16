import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import MainLayout from '@/layout/MainLayout.vue'
import Login from '@/views/auth/Login.vue'
import RegisterStudent from '@/views/auth/RegisterStudent.vue'
import RegisterCompany from '@/views/auth/RegisterCompany.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { public: true }
  },
  {
    path: '/register/student',
    name: 'RegisterStudent',
    component: RegisterStudent,
    meta: { public: true }
  },
  {
    path: '/register/company',
    name: 'RegisterCompany',
    component: RegisterCompany,
    meta: { public: true }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/login', // Redirect to login if root is accessed without auth, or determine based on role
    children: [
      // Student Routes
      {
        path: 'student',
        redirect: '/student/jobs',
        meta: { role: 'STUDENT' },
        children: [
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
            path: 'applications',
            name: 'StudentApplications',
            component: () => import('@/views/student/Applications.vue')
          }
        ]
      },
      // Company Routes
      {
        path: 'company',
        redirect: '/company/profile',
        meta: { role: 'COMPANY' },
        children: [
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
          }
        ]
      },
      // Admin Routes
      {
        path: 'admin',
        redirect: '/admin/companies',
        meta: { role: 'ADMIN' },
        children: [
          {
            path: 'companies',
            name: 'AdminCompanies',
            component: () => import('@/views/admin/CompanyAudit.vue')
          },
          {
            path: 'stats',
            name: 'AdminStats',
            component: () => import('@/views/admin/Stats.vue')
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

  if (!token && !isPublic) {
    next('/login')
  } else if (token && to.path === '/login') {
    // If already logged in, redirect to dashboard/home based on role
    const role = userStore.role
    if (role === 'STUDENT') next('/student')
    else if (role === 'COMPANY') next('/company')
    else if (role === 'ADMIN') next('/admin')
    else next('/')
  } else {
    // Check Role
    // This part assumes the parent route has the role meta, or we check matched
    const requiredRole = to.matched.find(record => record.meta.role)?.meta.role
    if (requiredRole && userStore.role !== requiredRole) {
      // Permission denied
      // Could redirect to 403 or home
      // next('/403') or just:
      next(false) // Cancel navigation? or redirect to their home
      // Better: redirect to their role home
      const role = userStore.role
      if (role === 'STUDENT') next('/student')
      else if (role === 'COMPANY') next('/company')
      else if (role === 'ADMIN') next('/admin')
      else next('/') // Fallback
    } else {
      next()
    }
  }
})

export default router
