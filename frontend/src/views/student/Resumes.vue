<template>
  <div class="resumes-container p-6">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <h2 class="text-xl font-bold">我的简历</h2>
          <el-button type="primary" @click="showCreateDialog">创建简历</el-button>
        </div>
      </template>

      <el-table :data="resumes" style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="简历名称" />
        <el-table-column prop="fileUrl" label="附件">
          <template #default="scope">
            <a v-if="scope.row.fileUrl" :href="scope.row.fileUrl" target="_blank" class="text-blue-500 hover:underline">下载</a>
            <span v-else>未上传</span>
          </template>
        </el-table-column>
        <el-table-column prop="isDefault" label="默认">
          <template #default="scope">
            <el-tag v-if="scope.row.isDefault" type="success">默认</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="success" @click="handleUpload(scope.row)">上传附件</el-button>
            <el-button size="small" type="warning" @click="handleSetDefault(scope.row.id)" v-if="!scope.row.isDefault">设为默认</el-button>
            <el-popconfirm title="确定删除吗？" @confirm="handleDelete(scope.row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑简历' : '创建简历'" width="40%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="简历名称">
          <el-input v-model="form.title" placeholder="例如：Java开发工程师简历" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.summary" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="技能">
          <el-input v-model="form.skills" type="textarea" :rows="3" placeholder="例如：Java, Vue, Spring Boot" />
        </el-form-item>
        <el-form-item label="经验">
          <el-input v-model="form.experience" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="教育">
          <el-input v-model="form.education" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Upload Dialog -->
    <el-dialog v-model="uploadDialogVisible" title="上传附件" width="30%">
      <el-upload
        class="upload-demo"
        drag
        action="#"
        :http-request="uploadFile"
        :limit="1"
        :on-exceed="handleExceed"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listResumes, createResume, updateResume, deleteResume, setDefaultResume, uploadResumeFile } from '@/api/student'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'

const resumes = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const uploadDialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)

const form = reactive({
  title: '',
  summary: '',
  skills: '',
  experience: '',
  education: ''
})

const fetchResumes = async () => {
  loading.value = true
  try {
    const data = await listResumes()
    resumes.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  form.title = ''
  form.summary = ''
  form.skills = ''
  form.experience = ''
  form.education = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    if (isEdit.value) {
      await updateResume(currentId.value, form)
      ElMessage.success('更新成功')
    } else {
      await createResume(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchResumes()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await deleteResume(id)
    ElMessage.success('删除成功')
    fetchResumes()
  } catch (error) {
    console.error(error)
  }
}

const handleSetDefault = async (id) => {
  try {
    await setDefaultResume(id)
    ElMessage.success('设置成功')
    fetchResumes()
  } catch (error) {
    console.error(error)
  }
}

const handleUpload = (row) => {
  currentId.value = row.id
  uploadDialogVisible.value = true
}

const uploadFile = async (param) => {
  try {
    await uploadResumeFile(currentId.value, param.file)
    ElMessage.success('上传成功')
    uploadDialogVisible.value = false
    fetchResumes()
  } catch (error) {
    console.error(error)
    ElMessage.error('上传失败')
  }
}
const handleExceed = () => {
    ElMessage.warning('只能上传一个文件，请删除后重新上传')
}

onMounted(() => {
  fetchResumes()
})
</script>
