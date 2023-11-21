<template>

    <div class="task-form">
  
      <v-container fluid>
  
        <v-form
          ref="taskForm"
          v-model="isValidForm"
          validate-on="lazy input" 
        >
  
        <v-card class="elevation-5" >
  
            <v-toolbar color="transparent" >  
              <v-toolbar-title class="font-weight-bold">
                <v-icon>mdi-table-edit</v-icon> {{ title }}
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
                  <v-text-field label="task name" v-model="task.fileName" :rules="validation.fileName" />
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12">TODO generate from filename
                  <v-text-field label="extension" v-model="task.fileName" :rules="validation.extension" />
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12">
                  <v-text-field label="description" v-model="task.description" />
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12">
                  <v-date-picker v-model="task.startDate" ></v-date-picker>
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12">
                  <v-select v-model="task.frequency" label="frequency" 
                   :items="frequencies" item-title="name" item-value="value"/>
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12">
                  <v-textarea label="notes" v-model="task.notes" rows="8"/>
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
          title: "New Task",
          task: {},
          isValidForm: false,
          id: null,
          dialogSaved: false,
          dialogError: false,
          dialogErrorDetail: "",
          frequencies: [
            { name: 'daily', value: 'D'},
            { name: 'weekly', value: 'W'},
            { name: 'monthly', value: 'M'},
            { name: 'yearly', value: 'Y'},
          ]
        }      
      },
  
      mounted() {
  
        console.log("MOUNTED: "+ this.$route.params.id)
        if (this.$route.params.id) {
          this.id = this.$route.params.id;
          this.loadTask();
        }  
        else this.isNew = true;
  
      },
  
      watch: {
      },  
  
      computed: {
  
        validation() {
  
          return {
  
            fileName: [ 
              v => !!v || "Value is required",
              v => (v && v.length >= 4) || "Value cannot be shorter than 3 characters"
            ],
            startDate: [
              v => !!v || "Value is required",
              v => (v && v.length >= 4) || "Value cannot be shorter than 4 characters"
            ],
          }  
        }
      },
  
      methods: {
  
        loadTask() {
  
          axios.get(`/api/task/`+this.id)
            .then(resp => {
              this.task = resp.data;
              this.title = this.task.fileName;
              console.log("CLIENT: " + this.task.fileName)
            })
            .catch(error => console.log(error))
            .finally(() => this.loading = false )
        },
  
        save() {
  
          if(this.$refs.taskForm.validate()) {
  
            axios.post(`/api/task`, this.task)
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