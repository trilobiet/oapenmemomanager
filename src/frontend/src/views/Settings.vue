<template>

  <div class="settings">

      <v-alert v-if="alert!==this.$alert.NONE" :type="alert" v-model="alert" closable @click:close="alert==this.$alert.NONE">
        <span v-if="alert==this.$alert.ERROR" @click="showAlertDetail">{{alertMsg}}</span>
        <span v-else>{{alertMsg}}</span>
      </v-alert>

      <v-container fluid>

      <v-row>

        <v-col>

          <v-card class="elevation-5">

            <v-toolbar color="transparent" >  

              <v-toolbar-title class="font-weight-bold">Settings</v-toolbar-title>
                  
                <!-- Edit Client Dialog ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
                <v-dialog v-model="dialog" width="1024" max-width="90%" scrollable @click:outside="cancelForm">
                
                  <template v-slot:activator>
                    <v-btn class="bg-primary" @click="editSetting()">New Setting</v-btn>
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
                    <span v-if="isHiddenKey(item.key)"
                     class="text-grey">{{item.key}}</span>
                    <span v-else style="cursor:pointer" @click="editSetting(item)"
                     class="text-blue-darken-4">{{item.key}}</span>
                  </template> 

                  <template v-slot:[`item.value`]="{ item }">
                    <span v-if="isHiddenKey(item.key)" class="text-grey">{{item.value}}</span>
                    <span v-else>{{item.value}}</span>
                  </template> 

                  <template v-slot:[`item.actions`]="{ item }">
                    <v-hover v-if="!isHiddenKey(item.key)" v-slot="{ isHovering, props }">
                      <v-icon @click="confirmDeleteSetting(item)" v-bind="props"
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

  </div>

</template>

<script>

import SettingForm from '../views/forms/SettingForm.vue';

export default {

  components: { SettingForm },
  
  data() {
    return {
      dialog: false,
      loading: true,
      tableSearch: '',
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
      alert: this.$alert.NONE, // ERROR,INFO,SUCCESS,WARNING,NONE
      alertMsg: "",
      alertDetail: "",
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
  
  watch: {

    // auto close alert after 5 secs
    alert(new_val){ 
      if(new_val){
        setTimeout(()=>{this.alert=this.$alert.NONE},5000)
      }
    }    
  },

  methods: {

    loadSettings() {

      this.loading = true; 
      this.$axios.get(`/api/setting`)
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

    editSetting(setting) {
      this.editedIndex = this.settings.indexOf(setting)
      this.editedSetting = Object.assign({}, setting)
      this.dialog = true
    },

    confirmDeleteSetting(setting) {
      this.$root.$refs.confirm.open(
        'Delete Setting', 
        'Are you sure you want to delete setting \'' + setting.key + '\'?', 
        { color: 'orange-darken-2', width: 400 }
      ).then((confirm) => {
         if (confirm) this.deleteSetting(setting)
      })

      if(confirm("Delete this setting?")) this.confirmDelete();
    },

    deleteSetting(setting) {

      console.log("DELETING ==== " + setting.key)

      // TODO
      this.$axios.delete(`/api/setting/${setting.key}`)
        .then( resp => {
          this.settings.splice(this.settings.indexOf(setting), 1)
          console.log(resp)
          this.alert = this.$alert.SUCCESS;
          this.alertMsg = "Setting deleted";
        })
        .catch( err => {
          this.alert = this.$alert.ERROR;
          this.alertMsg = err.message;
          this.alertDetail = err
        })
        .finally(() => {
          this.setDefault();
        })

      this.dialogDelete = false
    },

    cancelForm () {
      this.dialog = false
      this.setDefault();   
      //console.log("Dialog canceled")   
    },

    saveSetting () {

      // Content-type: application/json
      this.$axios.post(`/api/setting`, this.editedSetting)
        .then( resp => {
          console.log(resp)
          if (this.editedIndex > -1) {
            Object.assign(this.settings[this.editedIndex], this.editedSetting)
          }  
          else {
            this.settings.push(this.editedSetting)
            this.editedSetting.id = resp.data.id
          }  
          this.alert = this.$alert.SUCCESS;
          this.alertMsg = "Setting saved";
        })
        .catch( err => {
          this.alert = this.$alert.ERROR;
          this.alertMsg = err.message;
          this.alertDetail = err
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

    showAlertDetail() {
      alert(JSON.stringify(this.alertDetail))
    },

    isHiddenKey(key) {
      return key.charAt(0) == "."
    }

  }

}; 
</script>
