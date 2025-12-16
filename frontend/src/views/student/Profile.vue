<template>
  <div class="profile-container">
    <el-tabs v-model="activeTab">
      <!-- 个人信息 -->
      <el-tab-pane label="个人信息" name="info">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button type="primary" size="small" @click="editInfo = true" v-if="!editInfo">编辑</el-button>
              <div v-else>
                <el-button size="small" @click="cancelEdit">取消</el-button>
                <el-button type="primary" size="small" @click="saveInfo">保存</el-button>
              </div>
            </div>
          </template>
          <el-form :model="studentInfo" label-width="100px" :disabled="!editInfo">
            <el-form-item label="学号">
              <el-input v-model="studentInfo.studentNo" disabled />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="studentInfo.name" />
            </el-form-item>
            <el-form-item label="学院">
              <el-input v-model="studentInfo.college" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="studentInfo.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="studentInfo.email" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 简历管理 -->
      <el-tab-pane label="简历管理" name="resume">
        <el-button type="primary" @click="showResumeDialog = true" style="margin-bottom: 20px">
          <el-icon><Plus /></el-icon> 新建简历
        </el-button>
        
        <el-row :gutter="20" v-loading="resumeLoading">
          <el-col :xs="24" :sm="12" :md="8" v-for="resume in resumes" :key="resume.id">
            <el-card class="resume-card" shadow="hover">
              <template #header>
                <div class="resume-header">
                  <span>{{ resume.title }}</span>
                  <el-tag v-if="resume.isDefault" type="success" size="small">默认</el-tag>
                </div>
              </template>
              <div class="resume-content">
                <p class="resume-summary">{{ resume.summary || '暂无简介' }}</p>
                <div class="resume-meta">
                  <span>最后编辑：{{ formatDate(resume.lastEditedAt) }}</span>
                </div>
              </div>
              <template #footer>
                <div class="resume-actions">
                  <el-button size="small" @click="editResume(resume)">编辑</el-button>
                  <el-button size="small" @click="setDefault(resume)" v-if="!resume.isDefault">设为默认</el-button>
                  <el-button size="small" type="danger" @click="deleteResume(resume)">删除</el-button>
                </div>
              </template>
            </el-card>
          </el-col>
        </el-row>

        <el-empty v-if="resumes.length === 0 && !resumeLoading" description="还没有简历，快去创建一份吧！" />
      </el-tab-pane>
    </el-tabs>

    <!-- 简历编辑对话框 -->
    <el-dialog 
      v-model="showResumeDialog" 
      :title="currentResume.id ? '编辑简历' : '新建简历'"
      width="70%"
      @close="resetResumeForm"
    >
      <el-form :model="currentResume" label-width="120px">
        <el-form-item label="简历标题" required>
          <el-input v-model="currentResume.title" placeholder="例如：Java开发工程师简历" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input 
            v-model="currentResume.summary" 
            type="textarea" 
            :rows="3"
            placeholder="简要介绍自己的优势和特点"
          />
        </el-form-item>
        <el-form-item label="教育经历">
          <el-input 
            v-model="currentResume.education" 
            type="textarea" 
            :rows="4"
            placeholder="学校、专业、学历、时间等"
          />
        </el-form-item>
        <el-form-item label="工作/实习经历">
          <el-input 
            v-model="currentResume.experience" 
            type="textarea" 
            :rows="5"
            placeholder="公司、职位、时间、工作内容等"
          />
        </el-form-item>
        <el-form-item label="技能特长">
          <el-input 
            v-model="currentResume.skills" 
            type="textarea" 
            :rows="3"
            placeholder="掌握的技能、证书、获奖情况等"
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="currentResume.isDefault" />
          <span style="margin-left: 10px; color: #909399; font-size: 12px">默认简历将在投递时自动使用</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showResumeDialog = false">取消</el-button>
        <el-button type="primary" @click="saveResume" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getProfile, updateProfile } from '@/api/student'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/api/request'

const activeTab = ref('info')
const editInfo = ref(false)
const resumeLoading = ref(false)
const saving = ref(false)
const showResumeDialog = ref(false)

const studentInfo = reactive({
  studentNo: '',
  name: '',
  college: '',
  phone: '',
  email: ''
})

const originalInfo = reactive({})
const resumes = ref([])
const currentResume = reactive({
  id: null,
  title: '',
  summary: '',
  education: '',
  experience: '',
  skills: '',
  isDefault: false
})

const fetchProfile = async () => {
  try {
    const data = await getProfile()
    Object.assign(studentInfo, data)
    Object.assign(originalInfo, data)
  } catch (error) {
    console.error(error)
  }
}

const fetchResumes = async () => {
  resumeLoading.value = true
  try {
    const data = await request({ url: '/students/me/resumes', method: 'get' })
    resumes.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    resumeLoading.value = false
  }
}

const saveInfo = async () => {
  try {
    await updateProfile(studentInfo)
    ElMessage.success('保存成功')
    editInfo.value = false
    Object.assign(originalInfo, studentInfo)
  } catch (error) {
    console.error(error)
  }
}

const cancelEdit = () => {
  Object.assign(studentInfo, originalInfo)
  editInfo.value = false
}

const editResume = (resume) => {
  Object.assign(currentResume, resume)
  showResumeDialog.value = true
}

const saveResume = async () => {
  if (!currentResume.title) {
    ElMessage.warning('请填写简历标题')
    return
  }
  
  saving.value = true
  try {
    if (currentResume.id) {
      await request({
        url: `/students/me/resumes/${currentResume.id}`,
        method: 'put',
        data: currentResume
      })
      ElMessage.success('更新成功')
    } else {
      await request({
        url: '/students/me/resumes',
        method: 'post',
        data: currentResume
      })
      ElMessage.success('创建成功')
    }
    showResumeDialog.value = false
    fetchResumes()
  } catch (error) {
    console.error(error)
  } finally {
    saving.value = false
  }
}

const setDefault = async (resume) => {
  try {
    await request({
      url: `/students/me/resumes/${resume.id}/default`,
      method: 'put'
    })
    ElMessage.success('设置成功')
    fetchResumes()
  } catch (error) {
    console.error(error)
  }
}

const deleteResume = (resume) => {
  ElMessageBox.confirm('确定要删除这份简历吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request({
        url: `/students/me/resumes/${resume.id}`,
        method: 'delete'
      })
      ElMessage.success('删除成功')
      fetchResumes()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const resetResumeForm = () => {
  currentResume.id = null
  currentResume.title = ''
  currentResume.summary = ''
  currentResume.education = ''
  currentResume.experience = ''
  currentResume.skills = ''
  currentResume.isDefault = false
}

const formatDate = (date) => {
  if (!date) return '未编辑'
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchProfile()
  fetchResumes()
})
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resume-card {
  margin-bottom: 20px;
  height: 280px;
  display: flex;
  flex-direction: column;
}

.resume-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resume-content {
  flex: 1;
  overflow: hidden;
}

.resume-summary {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.resume-meta {
  color: #909399;
  font-size: 12px;
}

.resume-actions {
  display: flex;
  gap: 10px;
}
</style>
