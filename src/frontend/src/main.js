
import { createApp } from 'vue'
import MyApp from './App.vue'
import router from './router'
import axios from 'axios'

import '@/globalComponents'
import {globalfunctions} from './globalFunctions.js'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import * as labsComponents  from 'vuetify/labs/components'

import "@mdi/font/css/materialdesignicons.css";
import "@fortawesome/fontawesome-free/css/all.css";

const myLightTheme = {
	dark: false,
	colors: {
		background: 'rgb(232, 234, 246)',
		surface: '#FFFFFF',
		primary: '#9C27B0',
		'primary-darken-1': '#3700B3',
		secondary: '#ddd',
		'secondary-darken-1': '#018786',
		error: '#B00020',
		info: '#2196F3',
		success: '#4CAF50',
		warning: '#FB8C00',
		actions: '#eceff1' 
	},
}

const vuetify = createVuetify({

	components: {
		...components,
		...directives,
		...labsComponents
	},

    theme: {
		defaultTheme: 'myLightTheme',
		themes: {
			myLightTheme
		},
	},

	defaults: {
		VForm: {
			VTextField: { bgColor: '#f8f8fa', rounded: 0},
			VTextarea:  { bgColor: '#f8f8fa', rounded: 0}
		}
	}

}) 

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



