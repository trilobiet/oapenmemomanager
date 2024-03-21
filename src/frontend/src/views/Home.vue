<template>

  <div class="home">

    <v-container fluid>

      <v-card class="elevation-5">

        <v-toolbar>  

          <v-toolbar-title class="font-weight-bold">
            <v-icon>mdi-home</v-icon> Clients
          </v-toolbar-title>

          <v-btn variant="tonal" class="bg-primary" @click="newClient()">New Client</v-btn>

        </v-toolbar>

        <v-card-text> 

          <v-text-field
              v-model="tableSearch"
              append-icon="mdi-magnify"
              label="Search"
              variant="underlined">
          </v-text-field>

          <!-- single-expand show-expand item-key="username" -->  
          <v-data-table  
            :sort-by="['name','username']" hover 
            :loading="loading" :search="tableSearch" 
            :headers="headers" :items="clients"   
            calculate-widths>

            <template v-slot:[`item.name`]="{ item }">
              <div style="cursor:pointer" @click="editClient(item)" class="oapen-home-client-name text-blue-darken-4">
                {{item.name}}
              </div>
            </template> 

            <template v-slot:[`item.emptyScriptsCount`]="{ item }">
              <v-icon v-if="item.emptyScriptsCount > 0" icon="mdi-circle-half-full" color="orange" size="x-small" title="Some scripts need implementation"/>
              <v-icon v-else icon="mdi-circle" color="green" size="x-small"/>  
            </template>

            <template v-slot:[`item.url`]="{ item }">
              <div class="oapen-home-client-home">
                <a :href="$func.getClientUrl(item.username)" target="memoweb">
                  {{$func.getClientPath(item.username)}}
                </a><v-icon icon="mdi-open-in-new" size="x-small"/>
              </div>
              
            </template> 

            <template v-slot:[`item.passedTaskCount`]="{ item }">
              <v-chip v-if="item.passedTaskCount > 0" variant="flat" color="green" size="small">
                <strong>{{item.passedTaskCount}}</strong>
              </v-chip>
            </template>

            <template v-slot:[`item.failedTaskCount`]="{ item }">
              <v-chip v-if="item.failedTaskCount > 0" variant="flat" color="red" size="small">
                <strong>{{item.failedTaskCount}}</strong>
              </v-chip>
            </template>

            <template v-slot:[`item.actions`]="{ item }">
              <v-hover v-slot="{ hover }" v-if="item.username!='administrator'">
                <v-icon @click="deleteClient(item)"
                :class="hover?'red--text text--darken-3':'gray--text'">mdi-close-circle-outline</v-icon>
              </v-hover>  
            </template>

            <template v-slot:no-data>
              No data available. Your session may have expired.
              <br/><a href="/login">Login again to start a new session</a>
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
      loading: true,
      tableSearch: '',
      headers: [],
      clients:[], 
      defaultClient: {
        name: '',
        username: '',
        password: '',
      },   
    }    
  },
  
  computed: {

    userNames() {
      return this.clients.map(i => i.username)
    }
  },

  mounted() {
    this.loadClients();
  },
  
  methods: {

    loadClients() {

      this.loading = true; 
      this.$axios.get(`/api/homedir`)
      .then(resp => {
         this.clients=resp.data;
         console.log(resp.data);
         this.headers=this.getHeaders(resp.data);
      })
      .catch(error => console.log(error))
      .finally(() => this.loading = false )
    },
    
    getHeaders() {

      let arr = [
        { title: "name", key: "name", width: "25em"},
        { title: "complete", key: "emptyScriptsCount" , align: "center"},
        { title: "client home", key: "url" },
				{ title: "user name", key: "username" },
        { title: "tasks", key: "taskCount", align: "end" },
        { title: "success", key: "passedTaskCount", align: "center" },
        { title: "fail", key: "failedTaskCount", align: "center" },
      ];

      return arr;
    },

    editClient(client) {
      console.log("CLIENT: " + client.id)
      this.$router.push({ name: 'client', params: {id: client.id} })
    },

    newClient() {
      this.$router.push({ name: 'clientNew' })
    },

  }

}; 
</script>

<style>

  .oapen-home-client-name {
    max-width: 30em;
    white-space: nowrap; overflow:hidden; text-overflow: ellipsis;
  }

  .oapen-home-client-home {
    white-space: nowrap; overflow:hidden; text-overflow: ellipsis;
  }

</style>