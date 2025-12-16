<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '240px'" class="aside">
      <div class="logo">
        <el-icon v-if="isCollapse" class="logo-icon-small"><Promotion /></el-icon>
        <span v-else>双选会系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :collapse="isCollapse"
        router
        background-color="#1e293b"
        text-color="#94a3b8"
        active-text-color="#ffffff"
      >
        <!-- Student Menu (Normally Student has separate layout, but keeping for completeness if needed) -->
        <template v-if="role === 'STUDENT'">
          <el-menu-item index="/student/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item index="/student/profile">
            <el-icon><User /></el-icon>
            <template #title>个人信息</template>
          </el-menu-item>
          <el-menu-item index="/student/resumes">
            <el-icon><Document /></el-icon>
            <template #title>我的简历</template>
          </el-menu-item>
          <el-menu-item index="/student/jobs">
            <el-icon><Briefcase /></el-icon>
            <template #title>职位浏览</template>
          </el-menu-item>
          <el-menu-item index="/student/applications">
            <el-icon><Tickets /></el-icon>
            <template #title>我的投递</template>
          </el-menu-item>
          <el-menu-item index="/student/interviews">
            <el-icon><VideoCamera /></el-icon>
            <template #title>我的面试</template>
          </el-menu-item>
          <el-menu-item index="/student/events">
            <el-icon><Calendar /></el-icon>
            <template #title>双选会活动</template>
          </el-menu-item>
          <el-menu-item index="/student/notifications">
            <el-icon><Bell /></el-icon>
            <template #title>消息通知</template>
          </el-menu-item>
          <el-menu-item index="/student/announcements">
            <el-icon><Notification /></el-icon>
            <template #title>系统公告</template>
          </el-menu-item>
        </template>

        <!-- Company Menu -->
        <template v-if="role === 'COMPANY'">
          <el-menu-item index="/company/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item index="/company/profile">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>企业信息</template>
          </el-menu-item>
          <el-menu-item index="/company/jobs">
            <el-icon><Briefcase /></el-icon>
            <template #title>职位管理</template>
          </el-menu-item>
          <el-menu-item index="/company/applications">
            <el-icon><UserFilled /></el-icon>
            <template #title>简历处理</template>
          </el-menu-item>
          <el-menu-item index="/company/interviews">
            <el-icon><VideoCamera /></el-icon>
            <template #title>面试管理</template>
          </el-menu-item>
          <el-menu-item index="/company/notifications">
            <el-icon><Bell /></el-icon>
            <template #title>消息通知</template>
          </el-menu-item>
          <el-menu-item index="/company/announcements">
            <el-icon><Notification /></el-icon>
            <template #title>系统公告</template>
          </el-menu-item>
        </template>

        <!-- Admin Menu -->
        <template v-if="role === 'ADMIN'">
          <el-menu-item index="/admin/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item index="/admin/companies">
            <el-icon><School /></el-icon>
            <template #title>企业审核</template>
          </el-menu-item>
          <el-menu-item index="/admin/checkin">
            <el-icon><Checked /></el-icon>
            <template #title>现场签到</template>
          </el-menu-item>
          <el-menu-item index="/admin/stats">
            <el-icon><DataLine /></el-icon>
            <template #title>数据统计</template>
          </el-menu-item>
          <el-menu-item index="/admin/announcements">
            <el-icon><Notification /></el-icon>
            <template #title>公告管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/reviews">
            <el-icon><ChatDotRound /></el-icon>
            <template #title>评价管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/export">
            <el-icon><Download /></el-icon>
            <template #title>数据导出</template>
          </el-menu-item>
          <el-menu-item index="/admin/notifications">
            <el-icon><Bell /></el-icon>
            <template #title>消息通知</template>
          </el-menu-item>
        </template>

      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="user-info">
              <el-avatar :size="32" icon="UserFilled" :src="userStore.userInfo?.avatar" />
              <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>{{ role }}</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  HomeFilled, User, Briefcase, Document, OfficeBuilding, UserFilled, School, DataLine,
  Tickets, VideoCamera, Calendar, Bell, Notification, Checked, ChatDotRound, Download,
  Fold, Expand, CaretBottom, Promotion
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const role = computed(() => userStore.role)
const activeMenu = computed(() => route.path)
const currentRouteName = computed(() => route.meta.title || route.name || '当前页面')

const isCollapse = ref(false)
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: var(--bg-sidebar);
  color: white;
  transition: width 0.3s;
  overflow-x: hidden;
  box-shadow: 2px 0 6px rgba(0,0,0,0.1);
  z-index: 10;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  background-color: #0f172a; /* Darker shade */
  color: white;
  white-space: nowrap;
  overflow: hidden;
}

.logo-icon-small {
  font-size: 24px;
  vertical-align: middle;
}

.el-menu-vertical {
  border-right: none;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 240px;
}

/* Override menu colors for improved look */
:deep(.el-menu-item.is-active) {
  background-color: var(--primary-color) !important;
  color: white !important;
}

:deep(.el-menu-item:hover) {
  background-color: #334155 !important;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.03);
  z-index: 9;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: var(--text-regular);
  transition: color 0.2s;
}

.collapse-btn:hover {
  color: var(--primary-color);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.user-info:hover {
  background-color: #f1f5f9;
}

.username {
  font-size: 14px;
  color: var(--text-main);
}

.main {
  background-color: var(--bg-body);
  padding: 24px;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

/* Custom Scrollbar for Main */
.main::-webkit-scrollbar {
  width: 6px;
}
.main::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}
</style>
