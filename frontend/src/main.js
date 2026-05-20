import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router/index.js'

// Tailwind CSS
import './assets/main.css'

// Vue Toastification — bildirim sistemi
import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'

// Font Awesome
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  faUser, faLock, faEnvelope, faSignOutAlt, faTrophy,
  faChartBar, faHistory, faCog, faPlus, faEdit, faTrash,
  faPlay, faCheck, faTimes, faClock, faMedal, faHome,
  faQuestionCircle, faList, faUsers, faChevronRight,
  faChevronLeft, faSpinner, faSearch, faStar, faBookOpen,
  faFolder
} from '@fortawesome/free-solid-svg-icons'

library.add(
  faUser, faLock, faEnvelope, faSignOutAlt, faTrophy,
  faChartBar, faHistory, faCog, faPlus, faEdit, faTrash,
  faPlay, faCheck, faTimes, faClock, faMedal, faHome,
  faQuestionCircle, faList, faUsers, faChevronRight,
  faChevronLeft, faSpinner, faSearch, faStar, faBookOpen,
  faFolder
)

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Toast, {
  position: 'top-right',
  timeout: 3000,
  closeOnClick: true,
  pauseOnHover: true,
  draggable: true
})

app.component('font-awesome-icon', FontAwesomeIcon)

app.mount('#app')
