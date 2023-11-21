<template>

  <div class="client-form">

    <v-container fluid>

      <v-form
        ref="clientForm"
        v-model="isValidForm"
        validate-on="lazy input" 
      >

      <v-card class="elevation-5" >

          <v-toolbar color="transparent" >  
            <v-toolbar-title class="font-weight-bold">
              <v-icon>mdi-square-edit-outline</v-icon> {{ title }}
            </v-toolbar-title>
          </v-toolbar>

          <v-divider></v-divider>

          <v-card-text class="pa-16">

            <div style="width: 1280px; max-width:90%; margin: auto">

            <v-row v-if="dialogSaved">
              <v-col>
                <v-alert type="success" closable v-model="dialogSaved">
                  Data saved!
                </v-alert>
              </v-col>
            </v-row>

            <v-row v-if="dialogError" >
              <v-col>
                <v-alert type="error" closable v-model="dialogError" >
                  <span @click="alertErrorDetail">A problem occurred when saving (click for details)</span>
                </v-alert>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="12">
                <v-text-field label="client name" v-model="client.name" :rules="validation.name" />
              </v-col>
            </v-row>  

            <v-row>
              <v-col cols="12" lg="4" md="6">
                <v-text-field label="username" v-model="client.username" :rules="validation.username">
                  <template v-slot:append-inner v-if="isNew">
                    <v-icon icon="mdi-auto-fix" @click="client.username = $func.normalizeName(client.name)"/> 
                  </template>
                </v-text-field>
              </v-col>
            </v-row>  

            <v-row>

              <v-col v-if="isNew" lg="4" md="6" sm="8"> 
                <v-text-field label="password" v-model="client.password" :rules="validation.password"
                 append-inner-icon="mdi-auto-fix" @click:appendInner="client.password = $func.generatePassword()"/>
              </v-col>

              <v-col v-if="!isNew" lg="4" md="6" sm="8">
                <v-text-field label="password" v-model="client.password" :rules="validation.password" v-if="setPassword"
                  append-inner-icon="mdi-auto-fix" @click:appendInner="client.password = $func.generatePassword()" />
                <v-text-field v-else label="set password" disabled><!-- just a 'label' --></v-text-field>
              </v-col>

              <v-col cols="4" v-if="!isNew">
                <v-switch v-model="setPassword" inset></v-switch>
              </v-col>  

              <v-col class="text-lg-right">
                <v-btn class="mt-2" color="blue" text="copy credentials to text" />
              </v-col>

            </v-row>  

            <v-row>
              <v-col cols="12">
                <v-textarea label="notes" v-model="client.notes" rows="8"/>
              </v-col>
            </v-row>  

          </div>

          </v-card-text>

          <v-divider class="border-dark"></v-divider>

          <v-card-actions class="bg-actions">

            <v-container>
              <v-row >

                <v-col>
                  <span v-if="isValidForm===false" class="text-red">
                    <v-icon>mdi-alert-circle-outline</v-icon>
                    Please fix validation issues before saving
                  </span>
                </v-col>

                <v-col class="text-right">
                    <v-btn @click="$router.go(-1)" text="Cancel" />
                    <v-btn @click="save" text="Save" :disabled="!isValidForm"/>
                </v-col>

              </v-row>   
            </v-container>

          </v-card-actions>

        </v-card>

      </v-form>
      
    </v-container>

  </div>
  
</template>
  
<script>

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
        dialogSaved: false,
        dialogError: false,
        dialogErrorDetail: "",
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
            this.dialogSaved = true;
          })
          .catch( err => {
            console.log(err)
            this.dialogErrorDetail = err
            this.dialogError = true
          })
          .finally(() => {
            console.log("Ready.") 
          })

        }  
        else {
          console.log("VALIDATION ERRORS!") 
        }  
      },

      alertErrorDetail() {
        alert(JSON.stringify(this.dialogErrorDetail))
      }

    },

  }

  </script>