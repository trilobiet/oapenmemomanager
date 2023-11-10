
import { createApp } from 'vue'
import MyApp from './App.vue'
import router from './router'
import axios from 'axios'

import '@/globalComponents'
import {globalfunctions} from './globalFunctions.js'

// vuetify
import vuetify from "./plugins/vuetify";

// Preload some api lookup data
const promises = []

// Proceed when all promises have been fulfilled
Promise.all(promises).then( () => {
	
	const app = createApp(MyApp)
		.use(router)
		.use(vuetify)
		.use(globalfunctions)
		
	app.config.globalProperties.$axios = axios
	app.config.globalProperties.$func = globalfunctions
	
	app.mount('#app')	
}); 



