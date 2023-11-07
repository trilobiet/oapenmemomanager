
// use prefix this.$func. to call from vue files

export const globalfunctions = {

	flattenJSON: (obj={}, res={}, extraKey='') => {
	
		for(const key in obj) {
		
			if(typeof obj[key] !== 'object') {
				res[extraKey + key] = obj[key]
			}
			else {
				globalfunctions.flattenJSON(obj[key], res, `${extraKey}${key}.`)
			}
		}
		return res; 
	},
	
	flattenJsonArray: data => {
		return data.map(r => globalfunctions.flattenJSON(r))
	},
}