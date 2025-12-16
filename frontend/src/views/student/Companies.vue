<template>
  <div class="companies-container p-6">
    <div class="grid gap-6 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
      <el-card v-for="company in companies" :key="company.id" class="hover:shadow-lg transition-shadow">
        <template #header>
          <div class="flex items-center space-x-3">
             <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-500 font-bold">
               {{ company.companyName.charAt(0) }}
             </div>
             <h3 class="text-lg font-bold truncate">{{ company.companyName }}</h3>
          </div>
        </template>
        <div class="text-gray-600 space-y-2">
           <p><span class="font-semibold">行业：</span>{{ company.industry }}</p>
           <p><span class="font-semibold">联系人：</span>{{ company.contactPerson }}</p>
           <p class="line-clamp-3"><span class="font-semibold">简介：</span>{{ company.description }}</p>
        </div>
        <div class="mt-4 text-right">
           <el-button type="primary" size="small" @click="viewCompany(company)">查看详情</el-button>
        </div>
      </el-card>
    </div>

    <!-- Company Detail Dialog -->
    <el-dialog v-model="dialogVisible" title="企业详情" width="50%">
      <div v-if="currentCompany">
         <h2 class="text-2xl font-bold mb-4">{{ currentCompany.companyName }}</h2>
         <p class="text-gray-500 mb-2">行业：{{ currentCompany.industry }}</p>
         <p class="text-gray-500 mb-4">联系方式：{{ currentCompany.contactEmail }} | {{ currentCompany.contactPhone }}</p>
         <el-divider />
         <h3 class="text-lg font-bold mb-2">企业介绍</h3>
         <p class="whitespace-pre-wrap">{{ currentCompany.description }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listCompanies } from '@/api/student'

const companies = ref([])
const dialogVisible = ref(false)
const currentCompany = ref(null)

const fetchCompanies = async () => {
  try {
    const data = await listCompanies()
    companies.value = data || []
  } catch (error) {
    console.error(error)
  }
}

const viewCompany = (company) => {
  currentCompany.value = company
  dialogVisible.value = true
}

onMounted(() => {
  fetchCompanies()
})
</script>
