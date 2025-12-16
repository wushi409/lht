import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import MainLayout from '@/layout/MainLayout.vue'
import StudentLayout from '@/layout/StudentLayout.vue'

const routes = [
  // Public & Student Routes (Website Layout)
  {
    path: '/',
    component: StudentLayout,
    children: [
      {
        path: '',
        redirect: '/home'
      },
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { public: true }
      },
      {
        path: 'login',
        name: 'Login',
        component: () => import('@/views/auth/Login.vue'),
        meta: { public: true }
      },
      {
        path: 'register/student',
        name: 'RegisterStudent',
        component: () => import('@/views/auth/RegisterStudent.vue'),
        meta: { public: true }
      },
      {
        path: 'register/company',
        name: 'RegisterCompany',
        component: () => import('@/views/auth/RegisterCompany.vue'),
        meta: { public: true }
      },
      // Student Authenticated Routes
      {
        path: 'student',
        meta: { role: 'STUDENT' },
        children: [
          {
            path: 'dashboard',
            redirect: '/student/profile' // Redirect dashboard to profile for students in new layout
          },
          {
            path: 'profile',
            name: 'StudentProfile',
            component: () => import('@/views/student/Profile.vue')
          },
          {
            path: 'jobs',
            name: 'StudentJobs',
            component: () => import('@/views/student/Jobs.vue'),
            meta: { public: true } // Allow public viewing of jobs? Maybe. Let's keep it restricted for consistency with previous, or public? Let's make it public to attract users.
          },
          {
            path: 'events',
            name: 'StudentEvents',
            component: () => import('@/views/student/Events.vue'),
            meta: { public: true }
          },
          {
            path: 'companies', // New route for company list
            name: 'StudentCompanies',
            component: () => import('@/views/student/Companies.vue'), // Need to create or alias
            meta: { public: true }
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
      }
    ]
  },

  // Company Routes (Admin Layout)
  {
    path: '/company',
    component: MainLayout,
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

  // Admin Routes (Admin Layout)
  {
    path: '/admin',
    component: MainLayout,
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

  // Allow public access
  if (isPublic) {
    // If logged in student accessing home/login, it's fine.
    // If logged in company/admin accessing public pages, they might want to see them too, 
    // but usually we redirect them to dashboard. 
    // Let's allow public pages for everyone, BUT Login/Register should redirect if already logged in.
    if (token && (to.path === '/login' || to.path.startsWith('/register'))) {
      const role = userStore.role
      if (role === 'COMPANY') return next('/company/dashboard')
      else if (role === 'ADMIN') return next('/admin/dashboard')
      else return next('/student/profile') // Student stays in student layout
    }
    return next()
  }

  // Protected Routes
  if (!token) {
    return next('/login')
  }

  // Role Check
  // Note: Student routes are now under '/' which might be mixed with public. 
  // We need to check if the route requires a specific role.
  // The 'children' routes inherit meta from parent? No, not automatically in Vue Router unless customized.
  // But we put meta on the parent route '/student', '/company', '/admin'.
  // We need to check matched routes.
  
  const requiredRole = to.matched.find(record => record.meta.role)?.meta.role
  if (requiredRole && userStore.role !== requiredRole) {
    const role = userStore.role
     if (role === 'STUDENT') return next('/student/profile')
     else if (role === 'COMPANY') return next('/company/dashboard')
     else if (role === 'ADMIN') return next('/admin/dashboard')
     else return next('/login')
  }

  next()
})

export default router
