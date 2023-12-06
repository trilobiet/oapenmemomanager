<template>

  <v-card height="100%">

    <v-card-title  class="bg-secondary">runlog</v-card-title>

      <v-card-text >
        <v-data-table id="oapen-logtable" :headers="headers" :items="loglines" density="compact"
         show-expand fixed-header="true" expand-on-click height="100%" >

          <template v-slot:[`item.shortMessage`]="{ item }">
            <code style="font-size:.8rem">{{item.shortMessage}}</code>
          </template>           
        
          <template v-slot:expanded-row="{ item }">
            <tr v-if="item.message" style="background:#bbb;">
              <td colspan="2">
              </td>
              <td style="height:100%;">
                <pre style="background:#fcf8f8;font-size:.7rem;max-width: calc(75vw - 20em);height:100%;overflow:auto;padding:1em;">{{ item.message }}</pre>
              </td>
              <td></td>
            </tr>
          </template>

        </v-data-table>

      </v-card-text>

      <v-divider class="border-dark"></v-divider>
  
      <v-card-actions class="bg-actions">

        <v-container>
          <v-row >
            <v-col class="text-right">
              <v-btn text="Cancel" @click="this.$emit('closeRunlog')" />
            </v-col>
          </v-row>   
        </v-container>

      </v-card-actions>

    </v-card>
    
</template>
    
<script>

  export default {

  props: {
    taskId: {type: String, default: ''},
  },  

  data() {
    return {
      headers: [
        { title: "date", key: "date", width: "10em" },
        { title: "success", key: "success", width: "3em" },
        { title: "message", key: "shortMessage", width: "auto" },
        { title: "", key: "data-table-expand" },
      ],
      loglines: [],
      isExpanded: false
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

      this.$axios.get(`/api/task/`+this.taskId+`/runlog`)
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

<style>

  #oapen-logtable .v-table__wrapper {
    overflow-x: hidden !important;
  }

</style>
