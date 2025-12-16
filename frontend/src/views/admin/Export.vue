<template>
  <div class="admin-export p-6">
    <el-card header="数据导出">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card shadow="hover" class="text-center">
             <h3 class="font-bold mb-4">学生数据导出</h3>
             <el-button type="primary" @click="exportData('students')">导出 Excel</el-button>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="text-center">
             <h3 class="font-bold mb-4">企业数据导出</h3>
             <el-button type="primary" @click="exportData('companies')">导出 Excel</el-button>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="text-center">
             <h3 class="font-bold mb-4">职位数据导出</h3>
             <el-button type="primary" @click="exportData('jobs')">导出 Excel</el-button>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const exportData = async (type) => {
  try {
    const response = await request({
      url: `/export/${type}`,
      method: 'get',
      responseType: 'blob'
    })
    
    // Create download link
    const url = window.URL.createObjectURL(new Blob([response]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${type}_export.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    ElMessage.success('导出开始')
  } catch (error) {
    console.error(error)
    ElMessage.error('导出失败')
  }
}
</script>
