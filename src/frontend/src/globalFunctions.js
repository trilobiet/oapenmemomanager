
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

	generatePassword() {

		var length = 8,
			charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
			retVal = "";

		for (var i = 0, n = charset.length; i < length; ++i) {
			retVal += charset.charAt(Math.floor(Math.random() * n));
		}

		return(retVal)
	},

	normalizeName(str) {

		return str.toLowerCase()
			.normalize('NFD')	
			.replace(/[^\w\s]/gi, '')
			.replace(/ /g,"_");

	},

	getClientUrl(username) {

		return 'https://memo.oapen.org/clients/' + username
	}




}