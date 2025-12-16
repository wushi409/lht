<template>
  <div class="home-container min-h-screen flex flex-col">
    <!-- Hero Section -->
    <div class="hero bg-blue-600 text-white py-20">
      <div class="container mx-auto px-6 text-center">
        <h1 class="text-5xl font-bold mb-4">校园招聘会管理系统</h1>
        <p class="text-xl mb-8">连接高校人才与优质企业，开启您的职业生涯</p>
        <div class="space-x-4">
          <router-link to="/login">
            <el-button type="warning" size="large" round>立即登录</el-button>
          </router-link>
          <router-link to="/register/student">
            <el-button type="primary" size="large" round plain>学生注册</el-button>
          </router-link>
          <router-link to="/register/company">
            <el-button type="success" size="large" round plain>企业入驻</el-button>
          </router-link>
        </div>
      </div>
    </div>

    <!-- Features Section -->
    <div class="features py-16 bg-white">
      <div class="container mx-auto px-6">
        <h2 class="text-3xl font-bold text-center mb-12">平台优势</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div class="feature-card text-center p-6 border rounded hover:shadow-lg transition">
            <el-icon :size="50" class="text-blue-500 mb-4"><User /></el-icon>
            <h3 class="text-xl font-bold mb-2">精准匹配</h3>
            <p class="text-gray-600">根据专业和技能，为您推荐最合适的职位和人才</p>
          </div>
          <div class="feature-card text-center p-6 border rounded hover:shadow-lg transition">
            <el-icon :size="50" class="text-green-500 mb-4"><OfficeBuilding /></el-icon>
            <h3 class="text-xl font-bold mb-2">名企入驻</h3>
            <p class="text-gray-600">汇聚各行业领军企业，提供海量优质就业机会</p>
          </div>
          <div class="feature-card text-center p-6 border rounded hover:shadow-lg transition">
            <el-icon :size="50" class="text-orange-500 mb-4"><DataLine /></el-icon>
            <h3 class="text-xl font-bold mb-2">高效流程</h3>
            <p class="text-gray-600">简历投递、面试安排、结果反馈，全流程在线化</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Footer -->
    <footer class="bg-gray-800 text-white py-8 mt-auto">
      <div class="container mx-auto px-6 text-center">
        <p>&copy; 2024 校园招聘会管理系统. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { User, OfficeBuilding, DataLine } from '@element-plus/icons-vue'
</script>

<style scoped>
.hero {
  background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
</style>
