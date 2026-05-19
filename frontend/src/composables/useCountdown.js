/**
 * Geri sayım sayacı composable.
 *
 * Kullanımı:
 *   const { timeLeft, formatted, isExpired, start, stop } = useCountdown(300)
 *
 * timeLeft   : kalan saniye (reaktif)
 * formatted  : "MM:SS" formatında string (ör: "04:57")
 * isExpired  : süre doldu mu?
 * start()    : sayacı başlat
 * stop()     : sayacı durdur (quiz tamamlandığında çağrılır)
 * onExpired  : süre dolduğunda çağrılacak callback
 */

import { ref, computed, onUnmounted } from 'vue'

export function useCountdown(initialSeconds, onExpired = null) {
  const timeLeft  = ref(initialSeconds)
  const isExpired = ref(false)
  let   intervalId = null

  const formatted = computed(() => {
    const m = Math.floor(timeLeft.value / 60).toString().padStart(2, '0')
    const s = (timeLeft.value % 60).toString().padStart(2, '0')
    return `${m}:${s}`
  })

  function start() {
    if (intervalId) return   // zaten çalışıyorsa tekrar başlatma
    intervalId = setInterval(() => {
      if (timeLeft.value <= 0) {
        isExpired.value = true
        stop()
        onExpired?.()
        return
      }
      timeLeft.value--
    }, 1000)
  }

  function stop() {
    clearInterval(intervalId)
    intervalId = null
  }

  // Bileşen unmount olduğunda interval'i temizle (bellek sızıntısı önleme)
  onUnmounted(stop)

  return { timeLeft, formatted, isExpired, start, stop }
}
