<template>
  <div class="min-h-screen flex">

    <!-- ── Sol Panel: İllüstrasyon + Marka ──────────────────────────────────── -->
    <div class="hidden lg:flex flex-col flex-1 relative overflow-hidden bg-gradient-to-br from-primary-900 via-surface-900 to-surface-800 px-16 py-14">

      <!-- Dekoratif halka arka plan -->
      <div class="absolute -top-32 -left-32 w-[500px] h-[500px] rounded-full bg-primary-500/10 blur-3xl pointer-events-none" />
      <div class="absolute -bottom-24 -right-24 w-[400px] h-[400px] rounded-full bg-primary-600/10 blur-3xl pointer-events-none" />

      <!-- Logo -->
      <div class="relative z-10 flex items-center gap-2.5">
        <div class="w-9 h-9 rounded-xl bg-primary-500 flex items-center justify-center shadow-lg shadow-primary-500/30">
          <font-awesome-icon icon="book-open" class="text-white text-sm" />
        </div>
        <span class="text-white font-bold text-xl tracking-tight">QuizApp</span>
      </div>

      <!-- İllüstrasyon -->
      <div class="relative z-10 flex-1 flex items-center justify-center">
        <div class="w-full max-w-md">
          <!-- SVG'nin şeffaf arka planı temizlenir, ince bir glow efekti eklenir -->
          <div class="relative">
            <div class="absolute inset-0 bg-primary-500/10 rounded-3xl blur-2xl scale-95" />
            <img
              src="@/assets/quiz.svg"
              alt="Online Quiz"
              class="relative w-full drop-shadow-2xl select-none"
              draggable="false"
            />
          </div>
        </div>
      </div>

      <!-- Alt metin -->
      <div class="relative z-10 space-y-4">
        <h2 class="text-3xl font-bold text-white leading-snug">
          Bilgini test et,<br />
          <span class="text-primary-400">sınırlarını keşfet.</span>
        </h2>
        <p class="text-gray-400 text-sm leading-relaxed max-w-sm">
          Yüzlerce soru, kategori bazlı quizler ve anlık sonuçlarla
          öğrenme deneyimini yeni bir seviyeye taşı.
        </p>

        <!-- Özellik rozetleri -->
        <div class="flex flex-wrap gap-2 pt-2">
          <span v-for="tag in features" :key="tag"
            class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/5 border border-white/10 text-xs text-gray-300">
            <font-awesome-icon icon="check" class="text-primary-400 text-[10px]" />
            {{ tag }}
          </span>
        </div>
      </div>

    </div>

    <!-- ── Sağ Panel: Form ────────────────────────────────────────────────────── -->
    <div class="flex flex-col justify-center w-full lg:w-[420px] xl:w-[480px] flex-shrink-0 bg-surface-900 px-8 sm:px-14 py-12">

      <!-- Mobilde görünen logo -->
      <div class="flex items-center gap-2 mb-10 lg:hidden">
        <div class="w-8 h-8 rounded-lg bg-primary-500 flex items-center justify-center">
          <font-awesome-icon icon="book-open" class="text-white text-xs" />
        </div>
        <span class="text-white font-bold text-lg">QuizApp</span>
      </div>

      <!-- Başlık -->
      <div class="mb-8">
        <h1 class="text-2xl font-bold text-white">Tekrar hoş geldiniz</h1>
        <p class="text-gray-500 text-sm mt-1.5">Hesabınıza giriş yapın</p>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin" class="space-y-5">

        <div class="space-y-1.5">
          <label class="block text-xs font-medium text-gray-400">Kullanıcı Adı</label>
          <div class="relative">
            <font-awesome-icon icon="user" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-500 text-sm" />
            <input
              v-model="form.username"
              type="text"
              class="input !pl-10"
              placeholder=""
              autocomplete="username"
              required
            />
          </div>
        </div>

        <div class="space-y-1.5">
          <label class="block text-xs font-medium text-gray-400">Parola</label>
          <div class="relative">
            <font-awesome-icon icon="lock" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-500 text-sm" />
            <input
              v-model="form.password"
              type="password"
              class="input !pl-10"
              placeholder=""
              autocomplete="current-password"
              required
            />
          </div>
        </div>

        <button
          type="submit"
          class="btn-primary w-full !py-3 text-sm font-semibold shadow-lg shadow-primary-500/20 mt-1"
          :disabled="loading"
        >
          <font-awesome-icon v-if="loading" icon="spinner" spin class="mr-2" />
          {{ loading ? 'Giriş yapılıyor...' : 'Giriş Yap' }}
        </button>

      </form>

      <!-- Ayırıcı -->
      <div class="flex items-center gap-3 my-6">
        <div class="flex-1 h-px bg-white/5" />
        <span class="text-xs text-gray-600">veya</span>
        <div class="flex-1 h-px bg-white/5" />
      </div>

      <!-- Demo hesaplar -->
      <div class="space-y-2">
        <p class="text-xs text-gray-600 mb-2">Hızlı giriş için demo hesaplar:</p>
        <button
          v-for="demo in demoAccounts"
          :key="demo.username"
          type="button"
          @click="fillDemo(demo)"
          class="w-full flex items-center justify-between px-4 py-2.5 rounded-lg border border-white/5 bg-transparent hover:bg-white/5 hover:border-white/10 transition-colors duration-150 group"
        >
          <div class="flex items-center gap-2.5">
            <div class="w-7 h-7 rounded-full flex items-center justify-center text-xs"
                 :class="demo.role === 'Admin' ? 'bg-primary-500/20 text-primary-400' : 'bg-white/5 text-gray-400'">
              <font-awesome-icon :icon="demo.role === 'Admin' ? 'cog' : 'user'" />
            </div>
            <div class="text-left">
              <p class="text-xs font-medium text-gray-300">{{ demo.username }}</p>
              <p class="text-[10px] text-gray-600">{{ demo.role }}</p>
            </div>
          </div>
          <font-awesome-icon icon="chevron-right" class="text-gray-600 text-xs group-hover:text-gray-400 transition-colors" />
        </button>
      </div>

      <!-- Kayıt linki -->
      <p class="text-center text-sm text-gray-600 mt-8">
        Hesabınız yok mu?
        <RouterLink to="/register" class="text-primary-400 hover:text-primary-300 ml-1 font-medium transition-colors">
          Kayıt olun
        </RouterLink>
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
const form    = ref({ username: '', password: '' })

const features = ['Geri sayım sayacı', 'ECharts grafik', 'Liderlik tablosu', 'Kategori bazlı quizler']

const demoAccounts = [
  { username: 'admin', password: 'admin123', role: 'Admin' },
  { username: 'user',  password: 'user123',  role: 'Kullanıcı' }
]

function fillDemo(demo) {
  form.value.username = demo.username
  form.value.password = demo.password
}

async function handleLogin() {
  loading.value = true
  try {
    await authStore.login(form.value)
    toast.success(`Hoş geldiniz, ${authStore.user.username}!`)
    router.push('/quizzes')
  } catch (err) {
    toast.error(err.response?.data?.message || 'Giriş başarısız. Bilgilerinizi kontrol edin.')
  } finally {
    loading.value = false
  }
}
</script>
