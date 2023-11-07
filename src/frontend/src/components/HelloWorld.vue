<template>
	<!-- <div>{{ items }}</div> -->
	
	<div>
		<my-data-table :headers="headers" :items="items" :loading="loading" :report-title="reportTitle" />
	</div>
</template>

<script>

import MyDataTable from '@/components/MyDataTable.vue';

export default {
	components: { MyDataTable },
	data() {
		return {
			headers: [
				{ title: "OAPEN id", key: "id" },
				{ title: "Filename", key: "fileName" },
				{ title: "Extension", key: "extension" }
			],
			items: [],
			loading: true,
			reportTitle: 'Ladieda',
		}
	},
	mounted() {
		console.log("mounted!")
		this.$axios
			.get('/api/task')
			.then((response) => { 
				//this.items = this.$func.flattenJsonArray(response.data); 
				this.items = response.data;
				console.log(response.data)
			})  
			.catch(error => console.log(error))
			.finally(() => this.loading = false )
			
	}
} 
</script>