<template>

  <v-card>

    <v-card-title class="bg-secondary">runlog</v-card-title>

      <v-card-text>
        <v-data-table :headers="headers" :items="loglines" density="compact" items-per-page="25"></v-data-table>
      </v-card-text>

    </v-card>
    
</template>
    
<script>

  import axios from 'axios';

  export default {


  props: {
    taskId: {type: String, default: ''},
  },  

  data() {
    return {
      headers: [
        { title: "date", key: "date" },
        { title: "success", key: "success" },
        { title: "message", key: "message" },
      ],
      loglines: []
    }      
  },

  mounted() {

    console.log("MOUNTED: "+ this.$props.taskId)

    if (this.$props.taskId) {
      this.loadRunlog();
    }  

  },

  computed: {
  },

  methods: {

    loadRunlog() {

      axios.get(`/api/task/`+this.taskId+`/runlog`)
        .then(resp => {
            this.loglines = resp.data;
            console.log("RUNLOG: " + this.loglines)
        })
        .catch(error => console.log(error))
        .finally(() => {} )
    },

  },

}

</script>