<template>

  <div class="home">

      <!-- TODO toggle from axios on save if an error occurs -->  
      <v-alert v-if="dialogError" type="error" dismissible >
        <span @click="alertErrorDetail">A problem occurred when saving (click for details)</span>
      </v-alert>

      <v-container fluid>

      <v-row>

        <v-col>

          <v-card class="elevation-5">

            <v-toolbar density="normal" color="transparent" >  

                  <v-toolbar-title style="font-weight:bold">Vuetify</v-toolbar-title>
                  
                  <!-- Edit Client Dialog ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
                  <v-dialog v-model="dialog" width="1024" max-width="90%" scrollable>
                  
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn variant="tonal"  class="bg-primary" v-bind="attrs" v-on="on">New Setting</v-btn>
                    </template>

                    <setting-form :insetting="editedSetting" @cancel="cancel" @save="saveSetting" :isOpen="dialog"
                      :title="formSetting" :takenKeys="keys" />

                  </v-dialog>

              </v-toolbar>

              <v-card-text> 

                <v-text-field
                    v-model="tableSearch"
                    append-icon="mdi-magnify"
                    label="Search"
                    variant="underlined"></v-text-field>

                <v-data-table 
                  :sort-by="['key','value']" hover="true"
                  :loading="loading" :search="tableSearch" 
                  :headers="headers" :items="settings"   
                  :footer-props="{'items-per-page-options': [10, 25, 50, 100, -1]}"
                  calculate-widths>

                  <template v-slot:[`item.key`]="{ item }">
                    <span style="cursor:pointer" @click="editSetting(item)"
                     class="blue--text text--darken-4"
                    >{{item.key}}</span>
                  </template> 

                  <template v-slot:[`item.actions`]="{ setting }">
                    <v-hover v-slot="{ hover }">
                      <v-icon @click="deleteSetting(setting)"
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

        </v-col>
      </v-row>

    </v-container>

    <!-- DELETE Dialog ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <v-dialog v-model="dialogDelete" max-width="500px">

      <v-card>
        <v-card-title class="text-h5">Are you sure you want to delete this setting?</v-card-title>
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
import SettingForm from '../views/forms/SettingForm.vue';

export default {

  components: { SettingForm },
  
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
      settings:[], 
      editedIndex: -1,      
      editedSetting: {
        key: '',
        value: ''
      },
      defaultSetting: {
        key: '',
        value: ''
      },   
    }    
  },
  
  computed: {
    formTitle() {
      return this.editedIndex === -1 
        ? 'new setting'  
        : this.editedSetting.key
    },

    keys() {
      return this.settings.map(s => s.key)
    }
  },

  mounted() {
    this.loadSettings();
  },
  
  methods: {

    loadSettings() {

      this.loading = true; 
      axios.get(`/api/setting`)
      .then(resp => {
         this.settings=resp.data;
         this.headers=this.getHeaders(resp.data);
      })
      .catch(error => console.log(error))
      .finally(() => this.loading = false )
    },
    
    getHeaders() {

      let arr = [
        { title: "Key", key: "key" },
				{ title: "Value", key: "value" },
        { title: 'Remove', key: 'actions', sortable: false }
      ];

      return arr;
    },

    editSetting (setting) {
      this.editedIndex = this.settings.indexOf(setting)
      this.editedSetting = Object.assign({}, setting)
      this.dialog = true
    },

    deleteSetting (setting) {
      this.editedIndex = this.settings.indexOf(setting)
      this.editedSetting = Object.assign({}, setting)
      this.dialogDelete = true
    },

    confirmDelete () {

      // TODO
      axios.delete(`/api/setting`, this.editedSetting)
        .then( resp => {
          this.settings.splice(this.editedIndex, 1)
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

    saveSetting () {

      //if (this.editedItem.password.length==0) delete this.editedItem['password'];

      // Content-type: application/json
      axios.post(`/api/setting`, this.editedSetting)
        .then( resp => {
          console.log(resp)
          if (this.editedIndex > -1) {
            Object.assign(this.settings[this.editedIndex], this.editedSetting)
          }  
          else {
            this.settings.push(this.editedSetting)
            this.editedSetting.id = resp.data.id
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
      this.editedSetting = Object.assign({}, this.defaultItem)
      this.editedIndex = -1
    },

    alertErrorDetail() {
      alert(JSON.stringify(this.dialogErrorDetail))
    }

  }

}; 
</script>
