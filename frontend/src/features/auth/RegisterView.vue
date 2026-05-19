<template>
  <div class="min-h-screen flex items-center justify-center p-4">
    <div class="w-full max-w-sm">

      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-primary-500/20 mb-4">
          <font-awesome-icon icon="user" class="text-2xl text-primary-400" />
        </div>
        <h1 class="text-2xl font-semibold text-white">Kayıt Ol</h1>
        <p class="text-gray-400 text-sm mt-1">Ücretsiz hesap oluşturun</p>
      </div>

      <form @submit.prevent="handleRegister" class="card space-y-4">
        <div>
          <label class="block text-xs font-medium text-gray-400 mb-1.5">Kullanıcı Adı</label>
          <input v-model="form.username" type="text" class="input" placeholder="" required />
        </div>

        <div>
          <label class="block text-xs font-medium text-gray-400 mb-1.5">E-posta</label>
          <input v-model="form.email" type="email" class="input" placeholder="" required />
        </div>

        <div>
          <label class="block text-xs font-medium text-gray-400 mb-1.5">Parola</label>
          <input v-model="form.password" type="password" class="input" placeholder="En az 6 karakter" required />
        </div>

        <button type="submit" class="btn-primary w-full mt-2" :disabled="loading">
          <font-awesome-icon v-if="loading" icon="spinner" spin class="mr-2" />
          {{ loading ? 'Kayıt yapılıyor...' : 'Kayıt Ol' }}
        </button>
      </form>

      <p class="text-center text-sm text-gray-500 mt-4">
        Zaten hesabınız var mı?
        <RouterLink to="/login" class="text-primary-400 hover:text-primary-300 ml-1">Giriş yapın</RouterLink>
      </p>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'
import { useToast } from 'vue-toastification'

const authStore = useAuthStore()
const router    = useRouter()
const toast     = useToast()

const loading = ref(false)
const form    = ref({ username: '', email: '', password: '' })

async function handleRegister() {
  loading.value = true
  try {
    await authStore.register(form.value)
    toast.success('Hesabınız oluşturuldu!')
    router.push('/quizzes')
  } catch (err) {
    const errors = err.response?.data?.errors
    if (errors) {
      toast.error(Object.values(errors).join(', '))
    } else {
      toast.error(err.response?.data?.message || 'Kayıt başarısız.')
    }
  } finally {
    loading.value = false
  }
}
</script>
