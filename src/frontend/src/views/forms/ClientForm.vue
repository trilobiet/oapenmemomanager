<template>

  <div class="client-form">

    <v-container fluid>

      <v-form
        ref="clientForm"
        v-model="isValidForm"
        validate-on="lazy input" 
      >

      <v-card class="elevation-5" >

          <!-- TODO toggle from axios on save if an error occurs -->  
          <v-alert v-if="dialogError" type="error" closable=true>
            <span @click="alertErrorDetail">A problem occurred when saving (click for details)</span>
          </v-alert>

          <v-toolbar color="transparent" >  
            <v-toolbar-title class="font-weight-bold">
              <v-icon>mdi-square-edit-outline</v-icon> {{ this.client.name }}
            </v-toolbar-title>
          </v-toolbar>

          <v-divider></v-divider>

          <v-card-text class="pa-16" >

            <div style="width: 1280px; max-width:90%; margin: auto">

            <v-row>
              <v-col cols="4">
                <v-text-field label="client name" />
              </v-col>
            </v-row>  

            <v-row>
              <v-col cols="4">
                <v-text-field label="username" />
              </v-col>
              <v-col cols="4">
                <v-btn text="generate from client name" />
              </v-col>
            </v-row>  

            <v-row>
              <v-col cols="4">
                <v-text-field label="password" />
              </v-col>
              <v-col cols="8">
                <v-btn text="generate new" />
                &nbsp;
                <v-btn text="copy login data" />
              </v-col>
            </v-row>  

            <v-row>
              <v-col cols="12">
                <v-textarea label="notes" rows="8" />
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
                    <v-btn @click="cancel" text="Cancel"/>
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

    props: {
        title: {type: String, default: ''}
    },

    data() {
      return {
        isNew: false,
        isValidForm: false,
        client: {},
        id: null,
      }      
    },

    mounted() {

      console.log("MOUNTED: "+ this.$route.params.id)
      this.id = this.$route.params.id
      this.loadClient();

    },

    computed: {

    validation() {

        console.log()

        return {

        todo1: [ 
            v => !!v || 'Key is required',
        ],
        todo2: [
            v => !!v || 'Value is required'
        ]
        }  
    }
    },

    methods: {

      loadClient() {

        axios.get(`/api/homedir/`+this.id)
          .then(resp => {
            this.client=resp.data;
            console.log(this.client.name)
          })
          .catch(error => console.log(error))
          .finally(() => this.loading = false )
      },

      cancel() {
        // TODO
      },

      save() {

        if(this.$refs.clientForm.validate()) {
        // TODO
        }  
        else {
        console.log("VALIDATION ERRORS!") 
        }  
      },

    },

  }

  </script>