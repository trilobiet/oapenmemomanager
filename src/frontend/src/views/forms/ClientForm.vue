<template>

    <v-form
      ref="clientForm"
      v-model="isValidForm"
      validate-on="lazy input"
    >
      <v-card>
  
        <v-card-title class="bg-primary"> 
          <span class="text-h5">
            <v-icon>mdi-square-edit-outline</v-icon> {{ title }}
          </span>
        </v-card-title>
  
        <v-divider></v-divider>
  
        <v-card-text>
  
          <v-container>
  
            <v-row>
              <v-col cols="12" sm="6" md="4">
                <v-text-field todo />
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <v-text-field todo />
              </v-col>
            </v-row>  
  
          </v-container>
  
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
            //this.headers=this.getHeaders(resp.data);
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