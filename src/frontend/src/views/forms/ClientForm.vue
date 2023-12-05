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
                  <v-text-field label="username" v-model="client.username" :rules="validation.username">
                    <template v-slot:append-inner v-if="client.name">
                      <v-icon icon="mdi-auto-fix" @click="client.username = $func.normalizeName(client.name)"/> 
                    </template>
                  </v-text-field>
                  <div class="text-caption mx-4 text-grey-darken-1">export directory: /{{ client.username }}</div>
                </v-col>

              </v-row>  

              <v-row>

                <v-col v-if="isNew" lg="4" md="6" sm="8"> 
                  <v-text-field label="password" v-model="client.password" :rules="validation.password"
                  append-inner-icon="mdi-auto-fix" @click:appendInner="client.password = $func.generatePassword()"/>
                </v-col>
                <v-col v-else lg="4" md="6" sm="8">
                  <v-text-field label="password" v-model="client.password" :rules="validation.password" v-if="setPassword"
                    append-inner-icon="mdi-auto-fix" @click:appendInner="client.password = $func.generatePassword()" />
                  <v-text-field v-else label="set password" disabled><!-- just a 'label' --></v-text-field>
                </v-col>

                <v-col cols="4" v-if="!isNew">
                  <v-switch v-model="setPassword" inset></v-switch>
                </v-col>  

                <v-col v-if="setPassword" class="text-lg-right"><!-- No way to retrieve the password from saved (encrypted) clients -->
                  <v-btn class="mt-2" color="blue" text="copy credentials to clipboard" @click="copyCredentials()"/>
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

              <v-row v-if="alert=='saved'">
                <v-col>
                  <v-alert type="success" v-model="alert" closable @click:close="alert==''">
                    Data saved!
                  </v-alert>
                </v-col>
              </v-row>

              <v-row v-if="alert=='error'" >
                <v-col>
                  <v-alert type="error" v-model="alert" closable @click:close="alert==''">
                    <span @click="showErrorDetail">A problem occurred when saving (click for details)</span>
                  </v-alert>
                </v-col>
              </v-row>

            </my-wrapper>

          </v-card-text>

          <v-divider class="border-dark"></v-divider>

          <v-card-actions class="bg-actions">

            <my-wrapper>

              <v-row >

                <v-col v-if="!isNew" class="text-left">
                  <v-btn @click="deleteClient()" text="Delete this client" prepend-icon="mdi-alert"/>
                </v-col>

                <v-col>
                  <span v-if="isValidForm===false" class="text-red">
                    <v-icon>mdi-alert-circle-outline</v-icon>
                    Please fix validation issues before saving
                  </span>
                </v-col>

                <v-col class="text-right">
                    <v-btn @click="$router.go(-1)" text="Back" />
                    <v-btn @click="save" text="Save" :disabled="!isValidForm"/>
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
import axios from 'axios';
  
  export default {

    data() {
      return {
        isNew: false,
        title: "New Client Home",
        client: {},
        setPassword: false,
        isValidForm: false,
        id: null,
        alert: "",
        alertMsg: "",
      }      
    },

    mounted() {

      console.log("MOUNTED: "+ this.$route.params.id)
      if (this.$route.params.id) {
        this.id = this.$route.params.id;
        this.loadClient();
      }  
      else this.isNew = true;

    },

    watch: {

      setPassword: function(newVal, oldVal) {
        if (newVal==false) this.client.password = ''
        oldVal;
      },
      
      alert(new_val){ // auto close alert after 5 secs
        if(new_val){
          setTimeout(()=>{this.alert=""},5000)
        }
      }        

    },  

    computed: {

      validation() {

        return {

          name: [ 
            v => !!v || "Value is required",
            v => (v && v.length >= 4) || "Value cannot be shorter than 3 characters"
          ],
          username: [
            v => !!v || "Value is required",
            v => (v && v.length >= 4) || "Value cannot be shorter than 4 characters"
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

        axios.get(`/api/homedir/`+this.id)
          .then(resp => {
            this.client = resp.data;
            this.title = this.client.name;
            console.log("CLIENT: " + this.client.name)
          })
          .catch(error => console.log(error))
          .finally(() => this.loading = false )
      },

      save() {

        if(this.$refs.clientForm.validate()) {

          axios.post(`/api/homedir`, this.client)
          .then( resp => {
            console.log(resp)
            this.alert = "saved";
          })
          .catch( err => {
            console.log(err)
            this.alertMsg = err
            this.alert = "error"
          })
          .finally(() => {
            console.log("Ready.") 
          })

        }  
        else {
          console.log("VALIDATION ERRORS!") 
        }  
      },

      deleteClient() {

        if (confirm("This client and all its tasks will be deleted!\nAre you sure?")) {

          axios.delete(`/api/homedir/` + this.client.id)
          .then( () => {
            alert("deleted");
            router.push({name: 'home'})    
          })
          .catch( err => {
            this.alertMsg = err
            this.alert = "error"
          })
        }

      },

      // a button to copy username and password to clipboard
      copyCredentials() { 
        
        if(this.isValidForm) {
          navigator.clipboard.writeText("username: " + this.client.username + "\npassword: " + this.client.password);
          alert("Username/password copied to clipboard!");  
        }
        else alert("Nothing copied - fix validation issues first!");  
      },

      showErrorDetail() {
        alert(JSON.stringify(this.alertMsg))
      }

    },

  }

  </script>