
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import * as labsComponents  from 'vuetify/labs/components'
import 'vuetify/styles'
//import colors from 'vuetify/lib/util/colors'

export default createVuetify({

	components: {
		...components,
		...directives,
		...labsComponents,
	},

/*
    theme: {
		themes: {
			light: {
				primary: colors.purple,
				secondary: colors.grey.darken1,
				accent: colors.shades.black,
				error: colors.red.accent3,
				background: colors.indigo.lighten5, // Not automatically applied
				//    ...
			},
			dark: {
				primary: colors.blue.lighten3, 
				background: colors.indigo.base, // If not using lighten/darken, use base to return hex
				//   ...
			},
		},
	}
	*/
}) 


