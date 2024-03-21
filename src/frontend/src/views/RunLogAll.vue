<template>

  <div class="home">

    <v-container fluid>

      <v-card class="elevation-5">

        <v-toolbar>  

          <v-toolbar-title class="font-weight-bold">
            <v-icon>mdi-list-box-outline</v-icon> Run Logs
          </v-toolbar-title>

        </v-toolbar>

        <v-card-text> 

          <!-- Does not search in expanded row, 
            For a fix see: https://stackoverflow.com/questions/51346831/how-to-make-content-in-an-expand-slot-of-a-data-table-in-vuetifyjs-searchable

          <v-text-field
              v-model="tableSearch"
              append-icon="mdi-magnify"
              label="Search"
              variant="underlined">
          </v-text-field>
          -->

          <!-- single-expand show-expand item-key="username" -->  
          <v-data-table id="oapen-logtable" :headers="headers" :items="loglines" :search="tableSearch"
           show-expand fixed-header expand-on-click height="100%" >

            <template v-slot:[`item.date`]="{ item }">
              <span v-if="!item.success" style="color:red">{{this.$func.formatDateTime(item.date)}}</span>
              <span v-else style="color:#999">{{this.$func.formatDateTime(item.date)}}</span>
            </template>           

            <template v-slot:[`item.client`]="{ item }">
              <div class="oapen-log-client-name" :title="item.client">{{item.client}}</div>
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
                <td>
                </td>
                <td colspan="5" style="height:100%;">
                  <pre style="background:#fcf8f8;font-size:.7rem;max-width: calc(90vw - 20em);height:100%;overflow:auto;padding:1em;">{{ item.message }}</pre>
                </td>
              </tr>
            </template>

          </v-data-table>

        </v-card-text>

      </v-card>    

    </v-container>

  </div>

</template>

<script>

export default {

  data() {
    return {
      headers: [
        { title: "date", key: "date", width: "10em" },
        { title: "success", key: "success", width: "3em" },
        { title: "client", key: "client", width: "15em"},
        { title: "task", key: "task", width: "10em"},
        { title: "message", key: "shortMessage", width: "auto" },
        { title: "", key: "data-table-expand" },
      ],
      loglines: [],
      tableSearch: "",
    }      
  },

  mounted() {
    this.loadRunlog();
  },

  methods: {

    loadRunlog() {

      this.$axios.get(`/api/runlog`)
        .then(resp => {
            this.loglines = resp.data;
            console.log("RUNLOG: " + this.loglines)
        })
        .catch(error => console.log(error))
        .finally(() => {} )
    },

  },

}; 
</script>

<style>

  #oapen-logtable .v-table__wrapper {
    overflow-x: hidden !important;
  }

  .oapen-log-client-name {
    max-width: 15em;
    white-space: nowrap; overflow:hidden; text-overflow: ellipsis;
  }


</style>
