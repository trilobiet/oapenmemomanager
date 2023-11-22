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
                <v-icon>mdi-table-edit</v-icon> {{ isNew? "New Task" : title }}
              </v-toolbar-title>
            </v-toolbar>
  
            <v-divider></v-divider>
  
            <v-card-text class="pa-16">
  
              <div style="width: 1280px; max-width:90%; margin: auto">

              <!-- update existing task -->  
              <input type="hidden" v-if="!isNew" v-model="task.id"/>
  
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
                <v-col cols="12" sm="8">
                  <v-text-field label="task name" v-model="task.fileName" :rules="validation.fileName" clearable/>
                </v-col>
                <v-col cols="12" sm="4">
                  <v-text-field label="extension" v-model="task.extension" readonly bg-color="transparent" class="text-blue-grey-lighten-1" />
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12" sm="6" >
                  <v-text-field label="start date" v-model="task.startDate" :rules="validation.startDate" 
                   append-inner-icon="mdi-hours-24" @click:appendInner="task.startDate = $func.tomorrowDate()"
                   hint="Format: yyyy-mm-dd (e.g. 2023-12-31)" />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-select v-model="task.frequency" label="frequency" 
                   :items="frequencies" item-title="name" item-value="value"/>
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12">
                  <v-textarea label="description" v-model="task.description" rows="4"/>
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field label="script name" v-model="task.script.name"/>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field label="script type" v-model="task.script.type"/>
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12">
                  <v-textarea label="script" v-model="task.script.body" rows="4"/>
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12">
                  <v-textarea label="notes" v-model="task.notes" rows="4"/>
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
          task: {
            frequency: { name: 'weekly', value: 'W'},
            script: { }
          },
          isValidForm: false,
          id: null,
          clientid: null, 
          dialogSaved: false,
          dialogError: false,
          dialogErrorDetail: "",
          frequencies: [
            { name: 'daily', value: 'D'},
            { name: 'weekly', value: 'W'},
            { name: 'monthly', value: 'M'},
            { name: 'yearly', value: 'Y'},
          ],
          
        }      
      },
  
      mounted() {
  
        console.log("MOUNTED: "+ this.$route.params.id)
        console.log(this.$func.tomorrowDate())

        if (this.$route.params.id) {
          this.id = this.$route.params.id;
          this.loadTask();
        }  
        else {
          this.isNew = true;
          this.clientid = this.$route.params.clientid;
        }  
  
      },
  
      watch: {

        // update extension field when filename changes
        'task.fileName': {
          handler(val) {
            this.task.extension = this.$func.getExtension(val);
          }
        }  
      },  
  
      computed: {
  
        validation() {
  
          return {
  
            fileName: [ 
              v => !!v || "Value is required",
              v => (v && v.length >= 4) || "Value cannot be shorter than 3 characters",
              v => this.hasExtension(v) || "Not a valid extension"
            ],
            startDate: [
              v => !!v || "Value is required",
              v => this.$func.isValidDate(v) || "Not a valid date"
            ],
          }  
        },

      },
  
      methods: {

        hasExtension(s) {

          let ext = this.$func.getExtension(s)
          return ext && ext.length > 0;
        },
  
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

            let postUrl = '/api/task';
            if (this.isNew) postUrl = '/api/homedir/' + this.clientid + '/task';
  
            axios.post(postUrl, this.task)
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