<template>

  <div class="home">

      <!-- TODO toggle from axios on save if an error occurs -->  
      <v-alert v-if="dialogError" type="error" closable=true >
        <span @click="alertErrorDetail">A problem occurred when saving (click for details)</span>
      </v-alert>

      <v-container fluid>

      <v-row>

        <v-col>

          <v-card class="elevation-5">

            <v-toolbar color="transparent" >  

              <v-toolbar-title class="font-weight-bold">Settings</v-toolbar-title>
                  
                <!-- Edit Client Dialog ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
                <v-dialog v-model="dialog" width="1024" max-width="90%" scrollable @click:outside="cancelForm">
                
                  <template v-slot:activator="{ props }">
                    <v-btn class="bg-primary" v-bind="props">New Setting</v-btn>
                  </template>

                  <setting-form :insetting="editedSetting" @cancel="cancelForm" @save="saveSetting" 
                    :title="formTitle" :takenKeys="keys" />

                </v-dialog>

              </v-toolbar>

              <v-card-text> 

                <v-text-field
                    v-model="tableSearch"
                    append-icon="mdi-magnify"
                    label="Search" single-line
                    variant="underlined">
                </v-text-field>

                <v-data-table 
                  :sort-by="['key','value']" :hover="true"
                  :loading="loading" :search="tableSearch" 
                  :headers="headers" :items="settings">

                  <template v-slot:[`item.key`]="{ item }">
                    <span style="cursor:pointer" @click="editSetting(item)"
                     class="text-blue-darken-4">{{item.key}}</span>
                  </template> 

                  <template v-slot:[`item.actions`]="{ item }">
                    <v-hover v-slot="{ isHovering, props }">
                      <v-icon @click="deleteSetting(item)" v-bind="props"
                       :color="isHovering ? 'red': 'grey-darken-2'">mdi-close-circle-outline</v-icon>
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

      <v-card class="mx-auto" title="Delete setting" subtitle="Are you sure you want to delete this setting?">
        <template v-slot:prepend>
          <v-icon icon="mdi-alert-circle-outline" color="red" size="x-large"></v-icon>
        </template>
        <v-divider></v-divider>
        <v-card-actions class="justify-center px-6 py-3">
          <v-btn class="flex-grow-1" @click="cancelDelete">Cancel</v-btn>
          <v-btn class="flex-grow-1" @click="confirmDelete">OK</v-btn>
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
    // Title of form
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

      console.log("DELETING ==== " + this.editedSetting.key)

      // TODO
      axios.delete(`/api/setting/${this.editedSetting.key}`)
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

    cancelForm () {
      this.dialog = false
      this.setDefault();   
      console.log("Dialog canceled")   
    },

    cancelDelete () {
      this.dialogDelete = false
      this.setDefault();      
    },

    saveSetting () {

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
          console.log(err)
          // Show error on alert
          // TODO show logout message on session expiration
          console.log("ERROR: " + err)
          this.dialogErrorDetail = err.message
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
