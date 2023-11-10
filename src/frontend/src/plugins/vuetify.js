
//import 'vuetify/styles'
import '@/styles/main.scss'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import * as labsComponents  from 'vuetify/labs/components'

import "@mdi/font/css/materialdesignicons.css";
import "@fortawesome/fontawesome-free/css/all.css";

const myLightTheme = {
	dark: false,
	colors: {
		background: '#FFFFFF',
		surface: '#FFFFFF',
		primary: '#9C27B0',
		'primary-darken-1': '#3700B3',
		secondary: '#03DAC6',
		'secondary-darken-1': '#018786',
		error: '#B00020',
		info: '#2196F3',
		success: '#4CAF50',
		warning: '#FB8C00',
	},
}

export default createVuetify({

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
	}
}) 


