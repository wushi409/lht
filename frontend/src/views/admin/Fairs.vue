<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col :span="10">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>双选会管理</span>
              <el-button type="primary" size="small" @click="openFairDialog(null)">新建</el-button>
            </div>
          </template>
          <el-table :data="fairs" v-loading="loadingFairs" size="small" @row-click="selectFair">
            <el-table-column prop="name" label="名称" min-width="140" />
            <el-table-column label="时间" min-width="100">
              <template #default="{ row }">
                {{ formatDate(row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click.stop="openFairDialog(row)">
                  <el-icon><edit /></el-icon> 编辑
                </el-button>
                <el-divider direction="vertical" />
                <el-button type="danger" link size="small" @click.stop="deleteFair(row)">
                  <el-icon><delete /></el-icon> 删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="14">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>活动日程 - {{ currentFair ? currentFair.name : '请选择双选会' }}</span>
              <el-button type="primary" size="small" :disabled="!currentFair" @click="openEventDialog(null)">新增活动</el-button>
            </div>
          </template>
          <el-table :data="events" v-loading="loadingEvents" size="small">
            <el-table-column prop="name" label="活动名称" min-width="160" />
            <el-table-column label="类型" width="120">
              <template #default="{ row }">{{ formatType(row.type) }}</template>
            </el-table-column>
            <el-table-column label="时间" min-width="150">
              <template #default="{ row }">
                {{ formatDate(row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="openEventDialog(row)">
                  <el-icon><edit /></el-icon> 编辑
                </el-button>
                <el-divider direction="vertical" />
                <el-button type="danger" link size="small" @click="deleteEvent(row)">
                  <el-icon><delete /></el-icon> 删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="fairDialogVisible" :title="editingFair ? '编辑双选会' : '新建双选会'" width="520px">
      <el-form :model="fairForm" label-width="90px">
        <el-form-item label="名称" required>
          <el-input v-model="fairForm.name" />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="fairForm.location" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="fairForm.startTime" type="datetime" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="fairForm.endTime" type="datetime" style="width: 100%" />
        </el-form-item>
        <el-form-item label="容量">
          <el-input-number v-model="fairForm.capacity" :min="0" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="fairForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="fairDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingFair" @click="saveFair">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="eventDialogVisible" :title="editingEvent ? '编辑活动' : '新增活动'" width="520px">
      <el-form :model="eventForm" label-width="90px">
        <el-form-item label="活动名称" required>
          <el-input v-model="eventForm.name" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-select v-model="eventForm.type" style="width: 100%">
            <el-option label="双选会" value="FAIR" />
            <el-option label="宣讲会" value="PRESENTATION" />
            <el-option label="面试日" value="INTERVIEW_DAY" />
          </el-select>
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="eventForm.location" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="eventForm.startTime" type="datetime" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="eventForm.endTime" type="datetime" style="width: 100%" />
        </el-form-item>
        <el-form-item label="容量">
          <el-input-number v-model="eventForm.capacity" :min="0" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="eventForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="eventDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingEvent" @click="saveEvent">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'

const fairs = ref([])
const events = ref([])
const currentFair = ref(null)
const loadingFairs = ref(false)
const loadingEvents = ref(false)

const fairDialogVisible = ref(false)
const editingFair = ref(false)
const savingFair = ref(false)
const fairForm = ref({
  id: null,
  name: '',
  location: '',
  description: '',
  capacity: null,
  startTime: null,
  endTime: null
})

const eventDialogVisible = ref(false)
const editingEvent = ref(false)
const savingEvent = ref(false)
const eventForm = ref({
  id: null,
  name: '',
  type: 'FAIR',
  location: '',
  description: '',
  capacity: null,
  startTime: null,
  endTime: null
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatType = (type) => {
  const map = {
    'FAIR': '双选会',
    'PRESENTATION': '宣讲会',
    'INTERVIEW_DAY': '面试日'
  }
  return map[type] || type
}

const fetchFairs = async () => {
  loadingFairs.value = true
  try {
    const data = await request.get('/job-fairs')
    fairs.value = data || []
    if (fairs.value.length > 0 && !currentFair.value) {
      currentFair.value = fairs.value[0]
      fetchEvents()
    }
  } catch (e) {
    console.error(e)
  } finally {
    loadingFairs.value = false
  }
}

const fetchEvents = async () => {
  if (!currentFair.value) return
  loadingEvents.value = true
  try {
    const data = await request.get(`/job-fairs/${currentFair.value.id}/events`)
    events.value = data || []
  } catch (e) {
    console.error(e)
  } finally {
    loadingEvents.value = false
  }
}

const selectFair = (row) => {
  currentFair.value = row
  fetchEvents()
}

const openFairDialog = (row) => {
  if (row) {
    editingFair.value = true
    fairForm.value = {
      id: row.id,
      name: row.name,
      location: row.location,
      description: row.description,
      capacity: row.capacity,
      startTime: row.startTime ? new Date(row.startTime) : null,
      endTime: row.endTime ? new Date(row.endTime) : null
    }
  } else {
    editingFair.value = false
    fairForm.value = {
      id: null,
      name: '',
      location: '',
      description: '',
      capacity: null,
      startTime: null,
      endTime: null
    }
  }
  fairDialogVisible.value = true
}

const saveFair = async () => {
  if (!fairForm.value.name) {
    ElMessage.warning('请输入名称')
    return
  }
  
  savingFair.value = true
  try {
    const payload = {
      name: fairForm.value.name,
      location: fairForm.value.location,
      description: fairForm.value.description,
      capacity: fairForm.value.capacity,
      startTime: fairForm.value.startTime ? fairForm.value.startTime.toISOString() : null,
      endTime: fairForm.value.endTime ? fairForm.value.endTime.toISOString() : null
    }
    
    if (editingFair.value && fairForm.value.id) {
      await request.put(`/admin/job-fairs/${fairForm.value.id}`, payload)
    } else {
      await request.post('/admin/job-fairs', payload)
    }
    
    ElMessage.success('保存成功')
    fairDialogVisible.value = false
    fetchFairs()
  } catch (e) {
    console.error(e)
  } finally {
    savingFair.value = false
  }
}

const openEventDialog = (row) => {
  if (!currentFair.value) {
    ElMessage.warning('请先选择双选会')
    return
  }
  
  if (row) {
    editingEvent.value = true
    eventForm.value = {
      id: row.id,
      name: row.name,
      type: row.type,
      location: row.location,
      description: row.description,
      capacity: row.capacity,
      startTime: row.startTime ? new Date(row.startTime) : null,
      endTime: row.endTime ? new Date(row.endTime) : null
    }
  } else {
    editingEvent.value = false
    eventForm.value = {
      id: null,
      name: '',
      type: 'FAIR',
      location: '',
      description: '',
      capacity: null,
      startTime: null,
      endTime: null
    }
  }
  eventDialogVisible.value = true
}

const saveEvent = async () => {
  if (!currentFair.value) {
    ElMessage.warning('请先选择双选会')
    return
  }
  if (!eventForm.value.name) {
    ElMessage.warning('请输入活动名称')
    return
  }
  
  savingEvent.value = true
  try {
    const payload = {
      jobFairId: currentFair.value.id,
      name: eventForm.value.name,
      type: eventForm.value.type,
      location: eventForm.value.location,
      description: eventForm.value.description,
      capacity: eventForm.value.capacity,
      startTime: eventForm.value.startTime ? eventForm.value.startTime.toISOString() : null,
      endTime: eventForm.value.endTime ? eventForm.value.endTime.toISOString() : null
    }
    
    if (editingEvent.value && eventForm.value.id) {
      await request.put(`/admin/events/${eventForm.value.id}`, payload)
    } else {
      await request.post('/admin/events', payload)
    }
    
    ElMessage.success('保存成功')
    eventDialogVisible.value = false
    fetchEvents()
  } catch (e) {
    console.error(e)
  } finally {
    savingEvent.value = false
  }
}

const deleteFair = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该双选会吗？', '提示', { type: 'warning' })
    await request.delete(`/admin/job-fairs/${row.id}`)
    ElMessage.success('删除成功')
    if (currentFair.value && currentFair.value.id === row.id) {
      currentFair.value = null
      events.value = []
    }
    fetchFairs()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const deleteEvent = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该活动吗？', '提示', { type: 'warning' })
    await request.delete(`/admin/events/${row.id}`)
    ElMessage.success('删除成功')
    fetchEvents()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(() => {
  fetchFairs()
})
</script>

<style scoped>
.page-container {
  max-width: 1200px;
}
</style>
