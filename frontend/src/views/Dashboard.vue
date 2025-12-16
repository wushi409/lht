<template>
  <div class="dashboard-container p-6">
    <h1 class="text-2xl font-bold mb-6">欢迎回来, {{ userStore.userInfo.username }}</h1>
    
    <div v-if="role === 'COMPANY'" class="company-dashboard">
       <el-row :gutter="20">
         <el-col :span="8">
           <el-card shadow="hover">
             <template #header>发布职位</template>
             <div class="text-3xl font-bold text-blue-500">12</div>
           </el-card>
         </el-col>
         <el-col :span="8">
           <el-card shadow="hover">
             <template #header>收到简历</template>
             <div class="text-3xl font-bold text-green-500">45</div>
           </el-card>
         </el-col>
         <el-col :span="8">
           <el-card shadow="hover">
             <template #header>待处理面试</template>
             <div class="text-3xl font-bold text-orange-500">3</div>
           </el-card>
         </el-col>
       </el-row>
       <div class="mt-8">
         <h2 class="text-lg font-bold mb-4">快捷操作</h2>
         <el-button type="primary" @click="$router.push('/company/jobs')">发布新职位</el-button>
         <el-button type="success" @click="$router.push('/company/applications')">处理简历</el-button>
       </div>
    </div>

    <div v-else-if="role === 'ADMIN'" class="admin-dashboard">
       <el-row :gutter="20">
         <el-col :span="8">
           <el-card shadow="hover">
             <template #header>待审核企业</template>
             <div class="text-3xl font-bold text-red-500">5</div>
           </el-card>
         </el-col>
         <el-col :span="8">
           <el-card shadow="hover">
             <template #header>注册学生</template>
             <div class="text-3xl font-bold text-blue-500">1,203</div>
           </el-card>
         </el-col>
         <el-col :span="8">
           <el-card shadow="hover">
             <template #header>总职位数</template>
             <div class="text-3xl font-bold text-purple-500">340</div>
           </el-card>
         </el-col>
       </el-row>
        <div class="mt-8">
         <h2 class="text-lg font-bold mb-4">系统管理</h2>
         <el-button type="danger" @click="$router.push('/admin/companies')">审核企业</el-button>
         <el-button type="primary" @click="$router.push('/admin/stats')">查看统计</el-button>
       </div>
    </div>

    <div v-else class="student-dashboard">
      <p>正在跳转...</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const role = computed(() => userStore.role)
</script>
