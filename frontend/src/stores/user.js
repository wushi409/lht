import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setRole(newRole) {
    role.value = newRole
    localStorage.setItem('role', newRole)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    role.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('userInfo')
  }

  async function loginAction(loginForm) {
    try {
      const data = await login(loginForm)
      // Assuming data contains { token, role, ...userFields } or similar
      // Adjust based on AuthResponse
      setToken(data.token)
      setRole(data.role) // e.g., 'STUDENT', 'COMPANY', 'ADMIN'
      setUserInfo(data)
      return data
    } catch (error) {
      throw error
    }
  }

  return { token, role, userInfo, loginAction, logout }
})
