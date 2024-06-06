
// use prefix this.$func. to call from vue files

export const globalfunctions = {

	/*
		Currently this function is not used. No downloadable data is directly streamed to vue. 
		Downloadable files are saved server side and can then be downloaded as streams.
	*/
	downloadResponse(response) {

		// console.log("RESP: " + response.headers['content-type'])
		const data = response.headers['content-type'].startsWith("application/json")? JSON.stringify(response.data) : response.data
		const url = window.URL.createObjectURL(new Blob([data]));
		const link = document.createElement('a');
		link.href = url;
		// Get filename from header like 'content-disposition: attachment; filename="SCELC_profile1_marc.xml"'
		const match = response.headers['content-disposition'].match(/filename="([^"]+)"/);
		link.setAttribute('download', match[1]);
		document.body.appendChild(link);
		link.click();    
		//link.remove();        
	},

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

	generateRandomNumber(length=8) {

		var charset = "0123456789",
			retVal = "";

		for (var i = 0, n = charset.length; i < length; ++i) {
			retVal += charset.charAt(Math.floor(Math.random() * n));
		}

		return(retVal)
	},

	normalizeName(str) {

		return str.toLowerCase()
			.normalize('NFD')	
			.replace(/ +/g,"_")
			.replace(/[^\w\s]+/gi, '_')
			
	},

	getClientUrl(username) {

		return 'https://memo.oapen.org/clients/' + username
	},

	getClientPath(username) {

		return '/clients/' + username
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
	},

	extensionIcon(ext) {

		if(ext) {

			switch(ext.toLowerCase()) {
				case "xml": 
				case "onix": 
				case "marcxml": return "mdi-xml"
				case "json": return "mdi-code-json"
				case "rss": return "mdi-rss"
				case "xls": 
				case "xlsm": return "mdi-microsoft-excel"
				case "tsv": 
				case "csv": return "mdi-file-delimited"
				case "htm":
				case "html": return "mdi-file-code"
				default: return "mdi-file"
			}
		} else return "mdi-file"  
	},

	truncate(input,maxsize) {

		if (input.length > maxsize) 
			return input.substring(0, maxsize) + ' \u2026';
		else 
			return input;
	},

	isValidFileName(s) {

		// Only A-Z, a-z, 0-9 (\w), _ and .
		//return s.match(/^[.\w]+$/)
		return /^[.\w]+$/.test(s)
	},

	isValidModuleName(s) {

		// Only a-z, 0-9 (\w) and _ and
		// return s.match(/^[a-z0-9_]+$/)
		return /^[a-z0-9_]+$/.test(s)
	},

	isValidUserName(s) {

		// Only A-Z, a-z, 0-9 (\w) and _
		//return s.match(/^[\w]+$/)
		return /^[\w]+$/.test(s)
	},

	isValidSettingKey(s) {

		// Only A-Z, a-z, 0-9 (\w), _ and .
		//return s.match(/^[.\w]+$/)
		return /^[.\w]+$/.test(s)
	},

	formatDateTime(dt) {

		if (dt) return dt.replace("T", "\xa0")
		else return "burp";
	},

	formatDate(dt) {

		return dt.replace(/T.*/, "")
	},

	uuid: function (separator) {
    /// <summary>
    ///    Creates a unique id for identification purposes.
    /// </summary>
    /// <param name="separator" type="String" optional="true">
    /// The optional separator for grouping the generated segmants: default "-".    
    /// </param>

    var delim = separator || "";

    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }

    return (S4() + S4() + delim + S4() + delim + S4() + delim + S4() + delim + S4() + S4() + S4());
	},

}