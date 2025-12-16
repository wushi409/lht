<template>
  <div class="not-found">
    <h1>404</h1>
    <p>抱歉，您访问的页面不存在。</p>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<style scoped>
.not-found {
  text-align: center;
  margin-top: 100px;
}
h1 {
  font-size: 72px;
  color: #909399;
}
</style>
