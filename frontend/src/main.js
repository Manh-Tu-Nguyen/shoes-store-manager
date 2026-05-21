import { createApp } from 'vue'
import { createPinia } from 'pinia' 
import router from './router'      
import './style.css'
import App from './App.vue'
import { Icon } from "@iconify/vue"

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)  
app.use(router) 
app.mount('#app')
app.component("Icon", Icon)