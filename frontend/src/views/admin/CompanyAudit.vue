<template>
  <div class="page-container">
    <el-card>
      <template #header>企业审核</template>
      
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待审核" name="pending">
          <el-table :data="companies" v-loading="loading" stripe>
            <el-table-column label="Logo" width="80">
              <template #default="{ row }">
                <div style="width: 48px; height: 48px; border-radius: 8px; overflow: hidden; display: flex; align-items: center; justify-content: center; background: #f5f7fa;">
                  <img v-if="row.logoUrl" :src="getLogoUrl(row.logoUrl)" :alt="row.name" style="max-width: 100%; max-height: 100%; object-fit: contain;" />
                  <span v-else style="color: #999; font-size: 20px;">{{ (row.name || '企').charAt(0) }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="企业名称" prop="name" min-width="150" />
            <el-table-column label="行业" prop="industry" width="100" />
            <el-table-column label="规模" prop="scale" width="100" />
            <el-table-column label="联系人" prop="contactName" width="100" />
            <el-table-column label="联系电话" prop="contactPhone" width="130" />
            <el-table-column label="企业资质" width="100">
              <template #default="{ row }">
                <el-link v-if="row.licenseUrl" :href="getLogoUrl(row.licenseUrl)" target="_blank" type="primary">查看</el-link>
                <span v-else style="color: #999;">未上传</span>
              </template>
            </el-table-column>
            <el-table-column label="申请时间" min-width="110">
              <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="viewDetail(row)">
                  详情
                </el-button>
                <el-divider direction="vertical" />
                <el-button type="success" link size="small" @click="handleReview(row, true)">
                  通过
                </el-button>
                <el-divider direction="vertical" />
                <el-button type="danger" link size="small" @click="handleReview(row, false)">
                  拒绝
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && companies.length === 0" description="暂无待审核企业" />
        </el-tab-pane>

        <el-tab-pane label="已审核" name="reviewed">
          <el-table :data="companies" v-loading="loading" stripe>
            <el-table-column label="Logo" width="80">
              <template #default="{ row }">
                <div style="width: 48px; height: 48px; border-radius: 8px; overflow: hidden; display: flex; align-items: center; justify-content: center; background: #f5f7fa;">
                  <img v-if="row.logoUrl" :src="getLogoUrl(row.logoUrl)" :alt="row.name" style="max-width: 100%; max-height: 100%; object-fit: contain;" />
                  <span v-else style="color: #999; font-size: 20px;">{{ (row.name || '企').charAt(0) }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="企业名称" prop="name" min-width="150" />
            <el-table-column label="行业" prop="industry" width="100" />
            <el-table-column label="规模" prop="scale" width="100" />
            <el-table-column label="联系人" prop="contactName" width="100" />
            <el-table-column label="联系电话" prop="contactPhone" width="130" />
            <el-table-column label="企业资质" width="100">
              <template #default="{ row }">
                <el-link v-if="row.licenseUrl" :href="getLogoUrl(row.licenseUrl)" target="_blank" type="primary">查看</el-link>
                <span v-else style="color: #999;">未上传</span>
              </template>
            </el-table-column>
            <el-table-column label="审核时间" min-width="110">
              <template #default="{ row }">{{ formatDate(row.updatedAt) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'APPROVED' ? 'success' : 'danger'">
                  {{ row.status === 'APPROVED' ? '已通过' : '已拒绝' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="viewDetail(row)">
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && companies.length === 0" description="暂无已审核企业" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dialogVisible" title="企业详情" width="550px">
      <div v-if="current">
        <div style="display: flex; gap: 16px; align-items: flex-start; margin-bottom: 16px;">
          <div v-if="current.logoUrl" style="width: 80px; height: 80px; flex-shrink: 0; border: 1px solid #eee; border-radius: 8px; overflow: hidden;">
            <img :src="getLogoUrl(current.logoUrl)" :alt="current.name" style="width: 100%; height: 100%; object-fit: contain;" />
          </div>
          <div style="flex: 1;">
            <h2 style="margin:0 0 8px">{{ current.name }}</h2>
            <p style="margin: 4px 0;"><strong>行业：</strong>{{ current.industry || '未分类' }}</p>
            <p style="margin: 4px 0;"><strong>规模：</strong>{{ current.scale || '未知' }}</p>
          </div>
        </div>
        <el-divider />
        <p><strong>联系人：</strong>{{ current.contactName }}</p>
        <p><strong>联系电话：</strong>{{ current.contactPhone }}</p>
        <p><strong>联系邮箱：</strong>{{ current.contactEmail }}</p>
        <p v-if="current.licenseUrl">
          <strong>企业资质：</strong>
          <el-link :href="getLogoUrl(current.licenseUrl)" target="_blank" type="primary">点击查看</el-link>
        </p>
        <p v-else>
          <strong>企业资质：</strong><span style="color: #999;">未上传</span>
        </p>
        <el-divider />
        <p><strong>企业简介：</strong></p>
        <p>{{ current.description || '暂无' }}</p>
      </div>
    </el-dialog>

    <el-dialog v-model="rejectVisible" title="拒绝原因" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因（可选）" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="submitting" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'

const activeTab = ref('pending')
const companies = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const rejectVisible = ref(false)
const current = ref(null)
const rejectReason = ref('')
const rejectTarget = ref(null)

const fetchCompanies = async () => {
  loading.value = true
  try {
    if (activeTab.value === 'pending') {
      companies.value = await adminApi.getPendingCompanies() || []
    } else {
      // 获取已审核的企业（已通过和已拒绝）
      const all = await adminApi.getAllCompanies() || []
      companies.value = all.filter(c => c.status === 'APPROVED' || c.status === 'REJECTED')
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  fetchCompanies()
}

const viewDetail = (row) => {
  current.value = row
  dialogVisible.value = true
}

const handleReview = async (row, approved) => {
  if (approved) {
    submitting.value = true
    try {
      await adminApi.reviewCompany(row.id, true)
      ElMessage.success('已通过')
      fetchCompanies()
    } catch (e) {
      console.error(e)
    } finally {
      submitting.value = false
    }
  } else {
    rejectTarget.value = row
    rejectReason.value = ''
    rejectVisible.value = true
  }
}

const confirmReject = async () => {
  submitting.value = true
  try {
    await adminApi.reviewCompany(rejectTarget.value.id, false, rejectReason.value)
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    fetchCompanies()
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}

const formatDate = (date) => date ? new Date(date).toLocaleDateString('zh-CN') : ''

const getLogoUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return '/api' + url
}

onMounted(fetchCompanies)
</script>
