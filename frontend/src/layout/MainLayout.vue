<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo" @click="router.push('/')" style="cursor: pointer;">
        <el-icon v-if="isCollapse"><Promotion /></el-icon>
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
        <!-- 学生菜单 -->
        <template v-if="role === 'STUDENT'">
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
          <el-menu-item index="/student/companies">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>参会企业</template>
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
          <el-menu-item index="/student/announcements">
            <el-icon><Notification /></el-icon>
            <template #title>系统公告</template>
          </el-menu-item>
          <el-menu-item index="/student/notifications">
            <el-icon><Bell /></el-icon>
            <template #title>消息通知</template>
          </el-menu-item>
        </template>

        <!-- 企业菜单 -->
        <template v-if="role === 'COMPANY'">
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
          <el-menu-item index="/company/announcements">
            <el-icon><Notification /></el-icon>
            <template #title>系统公告</template>
          </el-menu-item>
          <el-menu-item index="/company/notifications">
            <el-icon><Bell /></el-icon>
            <template #title>消息通知</template>
          </el-menu-item>
        </template>

        <!-- 管理员菜单 -->
        <template v-if="role === 'ADMIN'">
          <el-menu-item index="/admin/stats">
            <el-icon><DataLine /></el-icon>
            <template #title>数据统计</template>
          </el-menu-item>
          <el-menu-item index="/admin/companies">
            <el-icon><School /></el-icon>
            <template #title>企业审核</template>
          </el-menu-item>
          <el-menu-item index="/admin/booths">
            <el-icon><Grid /></el-icon>
            <template #title>展位管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/checkin">
            <el-icon><Checked /></el-icon>
            <template #title>活动签到</template>
          </el-menu-item>
          <el-menu-item index="/admin/announcements">
            <el-icon><Notification /></el-icon>
            <template #title>公告管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/export">
            <el-icon><Download /></el-icon>
            <template #title>数据导出</template>
          </el-menu-item>
          <el-menu-item index="/admin/audit-logs">
            <el-icon><List /></el-icon>
            <template #title>审计日志</template>
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
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" /><Expand v-else />
          </el-icon>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>{{ roleLabel }}</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  User, Briefcase, Document, OfficeBuilding, UserFilled, School, DataLine,
  Tickets, VideoCamera, Calendar, Bell, Notification, Download,
  Fold, Expand, CaretBottom, Promotion, Grid, List, Checked
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const role = computed(() => userStore.role)
const roleLabel = computed(() => {
  const map = { STUDENT: '学生', COMPANY: '企业', ADMIN: '管理员' }
  return map[userStore.role] || userStore.role
})
const activeMenu = computed(() => route.path)
const isCollapse = ref(false)

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.aside {
  background-color: #1e293b;
  transition: width 0.3s;
  overflow-x: hidden;
}
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  background-color: #0f172a;
  color: white;
}
.el-menu-vertical { border-right: none; }
:deep(.el-menu-item.is-active) { background-color: #3b82f6 !important; }
:deep(.el-menu-item:hover) { background-color: #334155 !important; }
.header {
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 56px;
}
.header-left { display: flex; align-items: center; }
.collapse-btn { font-size: 20px; cursor: pointer; color: #64748b; }
.collapse-btn:hover { color: #3b82f6; }
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
}
.user-info:hover { background-color: #f1f5f9; }
.username { font-size: 14px; color: #1f2937; }
.main {
  background-color: #f8fafc;
  padding: 20px;
  height: calc(100vh - 56px);
  overflow-y: auto;
}
</style>
