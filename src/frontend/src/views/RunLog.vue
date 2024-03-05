<template>

  <div class="home">

    <v-card height="100%">

      <v-toolbar>  

        <v-toolbar-title class="font-weight-bold">
          <v-icon>mdi-list-box-outline</v-icon> Run Log for {{ taskName }}
        </v-toolbar-title>

      </v-toolbar>

      <v-card-text >
        <v-data-table id="oapen-logtable" :headers="headers" :items="loglines" density="compact"
        show-expand fixed-header expand-on-click height="100%" >

          <template v-slot:[`item.date`]="{ item }">
            <span v-if="!item.success" style="color:red">{{this.$func.formatDateTime(item.date)}}</span>
            <span v-else style="color:#999">{{this.$func.formatDateTime(item.date)}}</span>
          </template>           

          <template v-slot:[`item.shortMessage`]="{ item }">
            <code style="font-size:.8rem">{{item.shortMessage}}</code>
          </template>           
        
          <template v-slot:[`item.success`]="{ item }">
            <v-chip v-if="item.success" color="green" density="compact" class="text-caption">success</v-chip>
            <v-chip v-else color="red" density="compact" class="text-caption">failure</v-chip>
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
    
  </div>
    
</template>
    
<script>

  export default {

  props: {
    taskId: {type: String, default: ''},
    taskName: {type: String, default: ''},
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
