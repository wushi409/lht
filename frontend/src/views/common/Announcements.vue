<template>
  <div class="common-announcements p-6">
    <el-card>
      <template #header>
        <h2 class="text-xl font-bold">系统公告</h2>
      </template>
      
      <el-collapse accordion>
        <el-collapse-item v-for="item in announcements" :key="item.id" :name="item.id">
          <template #title>
            <div class="flex justify-between w-full pr-4">
              <span class="font-bold">{{ item.title }}</span>
              <span class="text-gray-500 text-sm">{{ new Date(item.createdAt).toLocaleDateString() }}</span>
            </div>
          </template>
          <div class="whitespace-pre-wrap">{{ item.content }}</div>
        </el-collapse-item>
      </el-collapse>
      
      <el-empty v-if="announcements.length === 0" description="暂无公告" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const announcements = ref([])

const fetchAnnouncements = async () => {
  try {
    const data = await request.get('/announcements')
    announcements.value = data || []
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchAnnouncements()
})
</script>
