<template>

  <div class="home">

    <!-- TODO toggle from axios on save if an error occurs -->  
    <v-alert v-if="dialogError" type="error" closable=true>
      <span @click="alertErrorDetail">A problem occurred when saving (click for details)</span>
    </v-alert>

    <v-container fluid>

      <v-card class="elevation-5">

        <v-toolbar>  

          <v-toolbar-title class="font-weight-bold">
            <v-icon>mdi-account-group</v-icon> Clients
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
              <span style="cursor:pointer" @click="editClient(item)"
                class="text-blue-darken-4"
              >{{item.name}}</span>
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

    <!-- DELETE Dialog ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <v-dialog v-model="dialogDelete" max-width="500px">

      <v-card>
        <v-card-title class="text-h5">Are you sure you want to delete this user?</v-card-title>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="cancelDelete">Cancel</v-btn>
          <v-btn color="blue darken-1" text @click="confirmDelete">OK</v-btn>
          <v-spacer></v-spacer>
        </v-card-actions>
      </v-card>

    </v-dialog>

  </div>

</template>

<script>
import axios from 'axios';
//import UserForm from '../views/UserForm.vue';

export default {

  //components: { UserForm },
  
  data() {
    return {
      loading: true,
      tableSearch: '',
      dialog: false,
      dialogDelete: false,      
      dialogError: false, 
      dialogSaved: false, 
      dialogErrorDetail: "",     
      headers: [],
      clients:[], 
      editedIndex: -1,      
      editedClient: {
        name: '',
        username: '',
        password: '',
        id: '',
      },
      defaultClient: {
        name: '',
        username: '',
        password: '',
      },   
    }    
  },
  
  computed: {
    formTitle() {
      return this.editedIndex === -1 
        ? 'new client'  
        : this.editedClient.username 
    },

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
      axios.get(`/api/homedir`)
      .then(resp => {
         this.clients=resp.data;
         this.headers=this.getHeaders(resp.data);
      })
      .catch(error => console.log(error))
      .finally(() => this.loading = false )
    },
    
    getHeaders() {

      let arr = [
        { title: "Name", key: "name" },
				{ title: "User name", key: "username" },
      ];

      return arr;
    },

    editClient(client) {
      /*this.editedIndex = this.clients.indexOf(client)
      this.editedClient = Object.assign({}, client)
      this.dialog = true*/
      console.log("CLIENT: " + client.id)
      this.$router.push({ name: 'client', params: {id: client.id} })
    },

    newClient() {
      this.$router.push({ name: 'clientNew' })
    },

    deleteItem (client) {
      this.editedIndex = this.clients.indexOf(client)
      this.editedClient = Object.assign({}, client)
      this.dialogDelete = true
    },

    confirmDelete () {

      // TODO
      axios.post(`/api/TODOdelete-user`, this.editedClient)
        .then( resp => {
          this.clients.splice(this.editedIndex, 1)
          console.log(resp)
        })
        .catch( err => {
          // Show error on alert
          this.dialogError = true
          console.log(err) 
        })
        .finally(() => {
          this.setDefault();
        })

      this.dialogDelete = false
    },

    cancel () {
      this.dialog = false
      this.setDefault();      
    },

    cancelDelete () {
      this.dialogDelete = false
      this.setDefault();      
    },

    saveClient () {

      //if (this.editedItem.password.length==0) delete this.editedItem['password'];

      // Content-type: application/json
      axios.post(`/api/homedir`, this.editedItem)
        .then( resp => {
          console.log(resp)
          if (this.editedIndex > -1) {
            Object.assign(this.clients[this.editedIndex], this.editedClient)
          }  
          else {
            this.clients.push(this.editedClient)
            this.editedClient.id = resp.data.id
          }  
          this.dialogSaved = true;
        })
        .catch( err => {
          console.log(err.response)
          // Show error on alert
          // TODO show logout message on session expiration
          this.dialogErrorDetail = err.response
          this.dialogError = true
        })
        .finally(() => {
          this.setDefault();
          console.log("Ready.") 
        })

      this.dialog = false
    },    

    setDefault() {
      this.editedClient = Object.assign({}, this.defaultClient)
      this.editedIndex = -1
    },

    alertErrorDetail() {
      alert(JSON.stringify(this.dialogErrorDetail))
    }

  }

}; 
</script>
