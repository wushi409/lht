<template>
  <div class="student-events p-6">
    <el-card>
      <template #header>
        <h2 class="text-xl font-bold">招聘会活动</h2>
      </template>
      
      <div class="grid gap-6 grid-cols-1 md:grid-cols-2">
         <el-card v-for="fair in jobFairs" :key="fair.id" shadow="hover">
            <template #header>
               <h3 class="font-bold text-lg">{{ fair.name }}</h3>
            </template>
            <p class="text-gray-600 mb-2">时间：{{ new Date(fair.startTime).toLocaleString() }} - {{ new Date(fair.endTime).toLocaleString() }}</p>
            <p class="text-gray-600 mb-4">地点：{{ fair.location }}</p>
            <p class="mb-4">{{ fair.description }}</p>
            <el-button type="primary" @click="viewEvents(fair.id)">查看详情</el-button>
         </el-card>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="活动详情" width="60%">
      <el-table :data="events">
        <el-table-column prop="title" label="活动主题" />
        <el-table-column prop="startTime" label="时间">
           <template #default="scope">
             {{ new Date(scope.row.startTime).toLocaleString() }}
           </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const jobFairs = ref([])
const events = ref([])
const dialogVisible = ref(false)

const fetchJobFairs = async () => {
  try {
    const data = await request.get('/job-fairs')
    jobFairs.value = data || []
  } catch (error) {
    console.error(error)
  }
}

const viewEvents = async (fairId) => {
  try {
    const data = await request.get(`/job-fairs/${fairId}/events`)
    events.value = data || []
    dialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchJobFairs()
})
</script>
