<template>

  <div class="client-form">

    <v-container fluid>

      <v-form
        ref="clientForm"
        v-model="isValidForm"
        validate-on="lazy input" 
      >

        <v-card class="elevation-5">

          <v-toolbar>  
            <v-toolbar-title class="font-weight-bold">
              <v-icon>mdi-home-account</v-icon> {{ title }}
            </v-toolbar-title>
          </v-toolbar>

          <v-divider></v-divider>

          <v-card-text class="pa-16">

            <my-wrapper>

              <my-form-header>{{ isNew? "New Client Home" : "Edit Client: " + title }}</my-form-header>

              <v-row>
                <v-col cols="12">
                  <v-text-field label="client name" v-model="client.name" :rules="validation.name" />
                </v-col>
              </v-row>  

              <v-row>

                <v-col cols="12" lg="4" md="6" sm="12">
                  <v-text-field label="username" v-model="client.username" :rules="validation.username" :disabled="!client.editable">
                    <template v-slot:append-inner v-if="client.name">
                      <v-icon icon="mdi-auto-fix" @click="client.username = $func.normalizeName(client.name)"/> 
                    </template>
                  </v-text-field>
                </v-col>

                <v-col cols="12" lg="4" md="6" sm="12">
                  <v-text-field label="accesskey" v-model="client.accessKey" readonly bg-color="transparent" class="text-blue-grey-lighten-1">
                    <template v-slot:append-inner>
                      <v-icon icon="mdi-auto-fix" @click="client.accessKey = $func.uuid()" color="black"/> 
                    </template>
                  </v-text-field>
                  <div class="text-caption mt-0 mb-5">
                    Only recreate when a previous version has been compromised.
                    When recreated external users must update links to private exports.
                  </div>
                </v-col>

              </v-row>  

              <v-row>

                <v-col v-if="isNew" lg="4" md="6" sm="8"> 
                  <v-text-field label="password" v-model="client.password" :rules="validation.password"
                  append-inner-icon="mdi-auto-fix" @click:appendInner="client.password = $func.generatePassword()"/>
                </v-col>
                <v-col v-else lg="4" md="6" sm="8">
                  <v-text-field v-if="setPassword" label="password" v-model="client.password" :rules="validation.password"
                    append-inner-icon="mdi-auto-fix" @click:appendInner="client.password = $func.generatePassword()" />
                  <v-text-field v-else label="set password" disabled><!-- just a 'label' --></v-text-field>
                </v-col>

                <v-col cols="4" v-if="!isNew">
                  <v-switch v-model="setPassword" inset></v-switch>
                </v-col>  

                <v-col class="text-lg-right"><!-- No way to retrieve the password from saved (encrypted) clients -->
                  <v-btn :disabled="!client.password" class="mt-2" color="blue" text="copy credentials to clipboard" @click="copyCredentials()"/>
                  <div class="text-caption mt-4">
                    <strong>Attn:</strong> Credentials can only be copied when the password is newly set. 
                    <br/>Once stored, passwords cannot be deciphered.
                  </div>
                </v-col>

              </v-row>  

              <v-row>
                <v-col cols="12">
                  <v-textarea label="notes" v-model="client.notes" rows="8"/>
                </v-col>
              </v-row>  

              <v-row v-if="!isNew && client.editable" >
                <v-col>
                  <my-danger-zone v-if="client.taskCount!==0" color="grey">
                    Delete client not available ({{client.taskCount}} tasks attached)
                    <v-btn disabled text="Delete this client" prepend-icon="mdi-alert" variant="tonal"/>
                  </my-danger-zone>
                  <my-danger-zone v-else>
                    <v-btn @click="confirmDeleteClient()" text="Delete this client" prepend-icon="mdi-alert" variant="tonal"/>
                  </my-danger-zone>
                </v-col>
              </v-row>  

              <v-row v-if="alert!==this.$alert.NONE">
                <v-col>
                  <v-alert :type="alert" closable @click:close="alert==this.$alert.NONE">
                    <span v-if="alert==this.$alert.ERROR" @click="showAlertDetail">{{alertMsg}}</span>
                    <span v-else>{{alertMsg}}</span>
                  </v-alert>
                </v-col>
              </v-row>

              <v-row v-if="!isNew">
                <v-col class="px-4 text-grey-darken-1 text-caption">
                  Last edited by {{ client.updatedBy }} on {{ client.updatedAt }}
                </v-col>
              </v-row>

            </my-wrapper>

          </v-card-text>

          <v-divider class="border-dark"></v-divider>

          <v-card-actions class="bg-actions">

            <my-wrapper>

              <v-row >

                <v-col class="text-left">
                  <v-btn @click="$router.go(-1)" text="Back" prepend-icon="mdi-menu-left" />
                </v-col>

                <v-col class="text-right">
                  <span class="text-red" v-if="isValidForm===false">
                    <v-icon>mdi-alert-circle-outline</v-icon>
                    Please fix validation issues before saving
                  </span>
                  <v-btn @click="saveClient" text="Save" :disabled="!isValidForm"/>
                </v-col>

              </v-row>   

            </my-wrapper>

          </v-card-actions>

        </v-card>

      </v-form>
      
    </v-container>

  </div>
  
</template>
  
<script>

import router from '@/router';
 
  export default {

    data() {
      return {
        isNew: false,
        title: "New Client Home",
        client: {editable:true},
        setPassword: false,
        isValidForm: false,
        id: null,
        alert: this.$alert.NONE, // ERROR,INFO,SUCCESS,WARNING,NONE
        alertMsg: "",
        alertDetail: "",
      }      
    },

    mounted() {

      console.log("MOUNTED: "+ this.$route.params.id)
      if (this.$route.params.id) {
        this.id = this.$route.params.id;
        this.loadClient();
      }  
      else {
        this.isNew = true;
        this.client.accessKey = this.$func.uuid();
      }  

    },

    watch: {

      setPassword: function(newVal) {
        if (newVal==false) this.client.password = ''
      },
      
      alert(new_val){ // auto close alert after 5 secs
        if(new_val){
          setTimeout(()=>{this.alert=this.$alert.NONE},5000)
        }
      }       

    },  

    computed: {

      validation() {

        return {

          name: [ 
            v => !!v || "Value is required",
            v => (v && v.length >= 3) || "Value cannot be shorter than 3 characters"
          ],
          username: [
            v => !!v || "Value is required",
            v => (v && this.$func.isValidUserName(v)) || "Value can only contain A-Z, a-z, 0-9 and _",
            v => (v && v.length >= 3) || "Value cannot be shorter than 3 characters",
            v => (v && v != 'tmp') || "Value cannot be 'tmp'",
          ],
          password: [
            v => !!v || "Value is required",
            v => (v && v.length >= 8) || "Value cannot be shorter than 8 characters"
          ]
        }  
      }
    },

    methods: {

      loadClient() {

        this.$axios.get(`/api/homedir/`+this.id)
          .then(resp => {
            this.client = resp.data;
            this.title = this.client.name;
            console.log("CLIENT: " + this.client.name)
          })
          .catch(error => console.log(error))
          .finally(() => this.loading = false )
      },

      saveClient() {

        if(this.$refs.clientForm.validate()) {

          this.$axios.post(`/api/homedir`, this.client)
          .then( () => {
            this.alert = this.$alert.SUCCESS;
            this.alertMsg = "Client saved";
            //setTimeout(() => {router.push({ name: 'client', params: {id: resp.data.id} })}, 1000);
          })
          .catch( err => {
            this.alert = this.$alert.ERROR;
            this.alertMsg = err.message;
            this.alertDetail = err
          })
          .finally(() => {})

        }  
        else {
          this.alert = this.$alert.ERROR;
          this.alertMsg = "There are validation errors" 
        }  
      },

      confirmDeleteClient() {

        this.$root.$refs.confirm.open(
            'Delete client', 
            'This client will be deleted irreversibly! <br/><br/>Are you sure you want to continue?', 
            { color: 'orange-darken-2', width: 400 }
          ).then((confirm) => {
            if (confirm) this.deleteClient()
          })
      },
      
      deleteClient() {

        this.$axios.delete(`/api/homedir/` + this.client.id)
          .then( () => {
            this.alert = this.$alert.SUCCESS;
            this.alertMsg = "Client deleted";
            setTimeout(() => {router.push({ name: 'home', params: {id: this.client.id} })}, 1000);
          })
          .catch( err => {
            this.alert = this.$alert.ERROR
            this.alertMsg = "Error deleting client"
            this.alertDetail = err
          })

      },

      // a button to copy username and password to clipboard
      copyCredentials() { 

        // validate form first
        this.$refs.clientForm.validate()

        if(this.isValidForm) {
          navigator.clipboard.writeText("username: " + this.client.username + "\npassword: " + this.client.password);
          alert("Username + password copied to clipboard!");  
        }
        else alert("Nothing copied - fix validation issues first!");  
      },

      showAlertDetail() {
        alert(JSON.stringify(this.alertDetail))
      }

    },

  }

  </script>