<template>
  <div class="student-layout">
    <nav class="navbar" :class="{ 'scrolled': isScrolled }">
      <div class="container navbar-content">
        <div class="logo">
          <router-link to="/">
            <span class="logo-icon">🎓</span>
            <span class="logo-text">校园双选会</span>
          </router-link>
        </div>

        <div class="nav-links">
          <router-link to="/home" class="nav-item">首页</router-link>
          <router-link to="/student/jobs" class="nav-item">职位大厅</router-link>
          <router-link to="/student/events" class="nav-item">双选会日程</router-link>
          <router-link to="/student/companies" class="nav-item">参会企业</router-link>
        </div>

        <div class="auth-actions" v-if="!token">
          <router-link to="/login" class="login-btn">登录</router-link>
          <router-link to="/register/student" class="register-btn">注册</router-link>
        </div>

        <div class="user-actions" v-else>
          <el-dropdown trigger="click">
            <span class="user-avatar">
              <el-avatar :size="32" :src="userInfo?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              <span class="username">{{ userInfo?.name || userInfo?.username || '同学' }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/student/dashboard')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/student/profile')">我的简历</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/student/applications')">投递记录</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <main class="page-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-col">
            <h3>关于我们</h3>
            <p>致力为高校毕业生和企业搭建高效、便捷的求职招聘平台。</p>
          </div>
          <div class="footer-col">
            <h3>联系方式</h3>
            <p>电话：010-12345678</p>
            <p>邮箱：contact@jobfair.edu.cn</p>
          </div>
          <div class="footer-col">
            <h3>快速链接</h3>
            <p><router-link to="/student/jobs">找工作</router-link></p>
            <p><router-link to="/register/company">企业入驻</router-link></p>
          </div>
        </div>
        <div class="copyright">
          © 2025 高校毕业生双选会信息管理系统 All Rights Reserved.
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const token = computed(() => userStore.token)
const userInfo = computed(() => userStore.userInfo)

const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.student-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  z-index: 1000;
  border-bottom: 1px solid transparent;
  transition: all 0.3s ease;
}

.navbar.scrolled {
  background: rgba(255, 255, 255, 0.98);
  border-bottom: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
}

.navbar-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo a {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: var(--primary-color);
  font-weight: 700;
  font-size: 1.25rem;
}

.logo-icon {
  margin-right: 0.5rem;
  font-size: 1.5rem;
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-item {
  text-decoration: none;
  color: var(--text-regular);
  font-weight: 500;
  position: relative;
  padding: 0.5rem 0;
  transition: color 0.2s;
}

.nav-item:hover, .nav-item.router-link-active {
  color: var(--primary-color);
}

.nav-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--primary-color);
  transition: width 0.3s ease;
}

.nav-item:hover::after, .nav-item.router-link-active::after {
  width: 100%;
}

.auth-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.login-btn {
  text-decoration: none;
  color: var(--text-regular);
  font-weight: 500;
}

.register-btn {
  text-decoration: none;
  background: var(--primary-color);
  color: white;
  padding: 0.5rem 1.25rem;
  border-radius: var(--radius-lg);
  font-weight: 500;
  transition: background 0.2s;
}

.register-btn:hover {
  background: var(--primary-hover);
}

.user-avatar {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 0.5rem;
  color: var(--text-regular);
}

.page-content {
  padding-top: 64px;
  flex: 1;
}

.footer {
  background: #1f2937;
  color: #9ca3af;
  padding: 3rem 0 1.5rem;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.footer-col h3 {
  color: white;
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

.footer-col p {
  margin-bottom: 0.5rem;
}

.footer-col a {
  color: #9ca3af;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-col a:hover {
  color: white;
}

.copyright {
  border-top: 1px solid #374151;
  padding-top: 1.5rem;
  text-align: center;
  font-size: 0.875rem;
}
</style>
