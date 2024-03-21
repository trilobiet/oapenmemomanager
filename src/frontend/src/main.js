
import { createApp } from 'vue'
import MyApp from './App.vue'
import router from './router'
import axios from 'axios'
import store from './store'

import '@/globalComponents'
import {globalfunctions} from './globalFunctions.js'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import * as labsComponents  from 'vuetify/labs/components'

import "@mdi/font/css/materialdesignicons.css"
import "@fortawesome/fontawesome-free/css/all.css"

// global custom styles
import "@/styles/globals.scss"

// global custom tags
import MyFormHeader from "@/components/MyFormHeader.vue";
import MyWrapper from "@/components/MyWrapper.vue";
import MyDangerZone from "@/components/MyDangerZone.vue";
import Confirm from '@/components/Confirm.vue';

// Check session validity once every 5 minutes
setInterval( () => {
	axios.get('/api/session')
	.then(resp => {
		console.log("Periodically checking session expiration...")
		if(resp.data != 'OK') window.location.href = "/login"
	})  
	.catch(() => {})

}, 300000);
  

const myLightTheme = {
	dark: false,
	colors: {
		background: 'rgb(232, 234, 246)',
		surface: '#FFFFFF',
		primary: '#9C27B0',
		'primary-darken-1': '#3700B3',
		secondary: '#ddd',
		'secondary-darken-1': '#018786',
		'btn-editor': '#F6ECF8',
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
		...labsComponents,
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
const promises = [

	// Once we get here we already have a browser session (Spring Boot),
	// so we only need to ask the api who is the logged in user. 
	axios.get('/api/user')
		.then(resp => store.commit("setUser",resp.data))
		.catch(() => {}),

]

const alert = {
	ERROR: "error",
	INFO: "info",
	SUCCESS: "success",
	WARNING: "warning",
	NONE: "info"
}

// Proceed when all promises have been fulfilled
Promise.all(promises).then( () => {
	
	const app = createApp(MyApp)
		.use(router)
		.use(vuetify)
		.use(store)
		.use(globalfunctions)
		
	app.config.globalProperties.$axios = axios
	app.config.globalProperties.$func = globalfunctions
	app.config.globalProperties.$alert = alert

	app.component("my-form-header", MyFormHeader)
	app.component("my-wrapper", MyWrapper)
	app.component("my-danger-zone", MyDangerZone)
	app.component("confirm", Confirm)
	
	app.mount('#app')	
}); 



