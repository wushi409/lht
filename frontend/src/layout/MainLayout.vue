<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo">双选会系统</div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <!-- Common -->
        <el-menu-item index="/dashboard" v-if="false">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <!-- Student Menu -->
        <template v-if="role === 'STUDENT'">
          <el-menu-item index="/student/profile">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="/student/jobs">
            <el-icon><Briefcase /></el-icon>
            <span>职位浏览</span>
          </el-menu-item>
          <el-menu-item index="/student/applications">
            <el-icon><Document /></el-icon>
            <span>我的投递</span>
          </el-menu-item>
        </template>

        <!-- Company Menu -->
        <template v-if="role === 'COMPANY'">
          <el-menu-item index="/company/profile">
            <el-icon><OfficeBuilding /></el-icon>
            <span>企业信息</span>
          </el-menu-item>
          <el-menu-item index="/company/jobs">
            <el-icon><Briefcase /></el-icon>
            <span>职位管理</span>
          </el-menu-item>
          <el-menu-item index="/company/applications">
            <el-icon><UserFilled /></el-icon>
            <span>简历处理</span>
          </el-menu-item>
        </template>

        <!-- Admin Menu -->
        <template v-if="role === 'ADMIN'">
          <el-menu-item index="/admin/companies">
            <el-icon><School /></el-icon>
            <span>企业审核</span>
          </el-menu-item>
          <el-menu-item index="/admin/stats">
            <el-icon><DataLine /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
        </template>

      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <div class="breadcrumb">
            <!-- Breadcrumb placeholder -->
          </div>
          <div class="user-info">
            <span style="margin-right: 15px">欢迎, {{ userStore.userInfo?.username || '用户' }}（{{ role }}）</span>
            <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
          </div>
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { HomeFilled, User, Briefcase, Document, OfficeBuilding, UserFilled, School, DataLine } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const role = computed(() => userStore.role)
const activeMenu = computed(() => route.path)

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
  background-color: #304156;
  color: white;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b2f3a;
}
.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  padding: 0 20px;
}
.header-content {
  display: flex;
  justify-content: space-between;
  width: 100%;
  align-items: center;
}
.main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
