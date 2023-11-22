
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
	},

	// https://stackoverflow.com/questions/18758772/how-do-i-validate-a-date-in-this-format-yyyy-mm-dd-using-jquery
	isValidDate(dateString) {

		// Calculates whether a date is possible (leap years included)
		var regEx = /^\d{4}-\d{2}-\d{2}$/;
		if(!dateString.match(regEx)) return false;  // Invalid format
		var d = new Date(dateString);
		var dNum = d.getTime();
		if(!dNum && dNum !== 0) return false; // NaN value, Invalid date
		return d.toISOString().slice(0,10) === dateString;
	},

	getExtension(s) {

		var re = /(?:\.([^.]+))?$/;
		return re.exec(s)[1];
	},

	tomorrowDate() {

		let today = new Date();
		const offset = today.getTimezoneOffset()
		let tomorrow = new Date(today.getTime() - offset*60*1000 + 24*60*60*1000)
		return tomorrow.toISOString().split('T')[0]
	}

}