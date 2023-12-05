<template>

  <div class="task-form">

    <v-container fluid>

      <v-form
        ref="taskForm"
        v-model="isValidForm"
        validate-on="lazy input" 
      >

        <v-card class="elevation-5" >

          <v-toolbar>  
            <v-toolbar-title class="font-weight-bold">
              <!--<v-icon>mdi-table-edit</v-icon> Export task: {{ isNew? "new" : title }}-->
              <v-icon>mdi-home-account</v-icon> {{ client.name }}
            </v-toolbar-title>
          </v-toolbar>

          <v-divider></v-divider>

          <v-card-text class="pa-16">

            <my-wrapper>

              <my-form-header>{{ isNew? "New Task" : "Edit task: " + title }}</my-form-header>

              <!-- update existing task -->  
              <input type="hidden" v-if="!isNew" v-model="task.id"/>
  
              <v-row>
                <v-col cols="12" sm="8">
                  <v-text-field label="task name" v-model="task.fileName" :rules="validation.fileName" clearable/>
                  <div class="px-4 text-grey-darken-2 text-caption">export path: {{ client.username }}/{{ task.fileName }}</div>
                </v-col>
                <v-col cols="12" sm="4">
                  <v-text-field label="extension" v-model="task.extension" readonly bg-color="transparent" class="text-blue-grey-lighten-1" 
                  :append-inner-icon="$func.extensionIcon(this.task.extension)"/>
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12" sm="6" >
                  <v-text-field label="start date" v-model="task.startDate" :rules="validation.startDate" 
                  append-inner-icon="mdi-hours-24" @click:appendInner="task.startDate = $func.tomorrowDate()"
                  hint="Format: yyyy-mm-dd (e.g. 2023-12-31)" />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-checkbox v-model="task.active" :label="task.active? 'active':'click to activate (task is currently suspended)'" 
                    :class="task.active?'text-green':'text-deep-orange-darken-3'" />
                </v-col>
              </v-row>  

              <v-row>
                <v-col cols="12" sm="6">
                  <v-radio-group label="frequency" v-model="task.frequency" inline >
                    <v-radio label="daily" value="D"/>
                    <v-radio label="weekly" value="W"/>
                    <v-radio label="monthy" value="M"/>
                    <v-radio label="yearly" value="Y"/>
                  </v-radio-group>  
                </v-col>
              </v-row>  

              <v-row>
                <v-col>
                  <v-textarea label="description (shown to client)" v-model="task.description" rows="1" auto-grow/>
                </v-col>
              </v-row>  

              <v-divider class="mt-4 mb-8"/>

              <v-row>
                <v-col cols="12" sm="6">
                
                  <v-text-field bg-color="transparent" class="text-primary oapen-readonly-name" @focus="showEditorPython()"
                    label="script (click to edit)" readonly v-model="task.script.name"
                    variant="outlined" persistent-placeholder prepend-inner-icon="mdi-language-python" />

                  <div id="oapen-script-preview">
                    {{task.script.body? task.script.body : '[no content]' }}
                  </div>
                
                </v-col>  
                <v-col cols="12" sm="6">
                
                  <v-text-field bg-color="transparent" class="text-primary oapen-readonly-name" @focus="showEditorSql()"
                    label="query (click to edit)" readonly v-model="task.script.query.name" 
                    variant="outlined" persistent-placeholder prepend-inner-icon="mdi-database-search"/>

                  <div id="oapen-query-preview">
                    {{task.script.query.body? task.script.query.body : '[no content]'}}
                  </div>
              
                </v-col>  
              </v-row>  

              <v-dialog fullscreen v-model="isEditor">

                <v-card>

                  <v-card-title v-if="isEditorPython">
                    <v-container fluid >
                      <v-row>
                        <v-col>
                          <v-icon icon="mdi-language-python"/>
                          {{  'Python Editor: ' + (!task.script.name? 'new' : task.script.name) }}
                        </v-col>
                        <v-col class="text-right">
                          <v-btn @click="showEditorSql()" class="bg-primary">switch to SQL view</v-btn>
                        </v-col>
                      </v-row>
                    </v-container>
                  </v-card-title>

                  <v-card-title v-if="isEditorSql">
                    <v-container fluid >
                      <v-row>
                        <v-col>
                          <v-icon icon="mdi-database-search"/>
                          {{  'SQL Editor: ' + (!task.script.query.name? 'new' : task.script.query.name) }}
                        </v-col>
                        <v-col class="text-right">
                          <v-btn @click="showEditorPython()" class="bg-primary">switch to Python view</v-btn>
                        </v-col>
                      </v-row>
                    </v-container>
                  </v-card-title>

                  <v-card-text v-if="isEditorSql" >

                    <v-ace-editor
                      v-model:value="task.script.query.body"
                      lang="mysql"
                      theme="github_dark"
                      style="height: 100%; font-size: 100%;" />
                      
                  </v-card-text>

                  <v-card-text v-if="isEditorPython">

                    <v-ace-editor
                      v-model:value="task.script.body"
                      lang="python"
                      theme="one_dark"
                      style="height: 100%; font-size: 100%;" />

                  </v-card-text>

                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text="Close Editor" @click="closeEditors()"></v-btn>
                  </v-card-actions>                        

                </v-card>

              </v-dialog>  

              <v-divider class="mt-4 mb-8"/>

              <v-row>
                <v-col cols="12">
                  <v-textarea label="notes (only visible here)" v-model="task.notes" rows="1" auto-grow/>
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

                <v-row class="restricted">
  
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
                
            </my-wrapper>

          </v-card-actions>

        </v-card>

      </v-form>
      
    </v-container>

  </div>
  
  </template>
    
  <script>
  
    import axios from 'axios';

    // https://www.npmjs.com/package/vue3-ace-editor
    // https://ace.c9.io/
    import { VAceEditor } from 'vue3-ace-editor';
    import 'ace-builds/src-noconflict/mode-python';
    import 'ace-builds/src-noconflict/mode-mysql';
    // https://ace.c9.io/build/kitchen-sink.html
    import 'ace-builds/src-noconflict/theme-github_dark';
    // import 'ace-builds/src-noconflict/theme-monokai';
    import 'ace-builds/src-noconflict/theme-one_dark';
    
    export default {
  
      components: {
        VAceEditor,
      },

      data() {
        return {
          isNew: false,
          task: {
            fileName: "",
            script: { query: {}, type: 'MAIN'}
          },
          isValidForm: false,
          id: null,
          client: {name: "", username: ""},
          clientid: null, 
          frequencies: [
            { name: 'daily', value: 'D'},
            { name: 'weekly', value: 'W'},
            { name: 'monthly', value: 'M'},
            { name: 'yearly', value: 'Y'},
          ],
          takenFileNames: [],
          isEditor: false,
          isEditorSql: false,
          isEditorPython: false,
          alert: "",
          alertMsg: "",
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
          this.loadClient(this.clientid)
          this.task.frequency='M' // default frequency
        }  
  
      },
  
      watch: {

        'task.fileName': {
          handler(val) {
            // update extension field when filename changes
            this.task.extension = this.$func.getExtension(val);
          }
        },  
      },  
  
      computed: {
  
        validation() {
  
          return {
  
            fileName: [ 
              v => !!v || "Value is required",
              v => (v && v.length >= 4) || "Value cannot be shorter than 3 characters",
              v => this.hasExtension(v) || "Not a valid extension",
              v => this.validateFilenameFree(v) || 'File name is already in use for this client. Choose another name.',
            ],
            startDate: [
              v => !!v || "Value is required",
              v => this.$func.isValidDate(v) || "Not a valid date"
            ],
            queryName: [
              v => (v && v.length >= 2) || "Query name is required",
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
              this.loadClient(this.task.homedir.id)
              if (this.task.script==null) this.task.script = {name: '', type: 'MAIN'}
              if (this.task.script.query==null) this.task.script.query = {}
            })
            .catch(error => console.log(error))
            .finally(() => this.loading = false )
        },

        loadClient(id) {
  
          axios.get(`/api/homedir/`+id)
            .then(resp => {
              this.client = resp.data;
              this.takenFileNames = this.loadTakenFileNames(this.client)
            })
            .catch(error => console.log(error))
            .finally(() => this.loading = false )
        },

        loadTakenFileNames(client) {

          axios.get(`/api/homedir/`+ client.id + '/task')
          .then(resp => {
            this.takenFileNames = resp.data.map(v => v.fileName);
            console.log("TAKEN: " + this.takenFileNames)
          })
          .catch(error => console.log(error))
          .finally(() => this.loading = false )

        },

        setScriptAndQueryName() {

          let postfix = this.client.username + "_" + this.task.fileName;
          this.task.script.name = "script_" + postfix;
          this.task.script.query.name = "query_" + postfix;
        },

        save() {
          
          this.setScriptAndQueryName();

          let taskToSave = JSON.parse(JSON.stringify(this.task))

          // remove empty query
          if (!this.task.script.query.body) {
            taskToSave.script.query = null;
            if (this.task.script.query.id) this.deleteOrphanedQuery(this.task.script.query.id)
          }  

          if(this.$refs.taskForm.validate()) {

            let postUrl = '/api/task';
            if (this.isNew) postUrl = '/api/homedir/' + this.clientid + '/task';
  
            axios.post(postUrl, taskToSave)
            .then( resp => {
              console.log(resp)
              this.alert = "saved"
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

        validateFilenameFree(filename) {

          console.log("FILENAME="+filename)
          console.log("TAKEN="+this.takenFileNames)

          if (!filename) return false;
          const posInList = this.takenFileNames.indexOf(filename.trim())
          if ( posInList == -1 ) return true
          else return false  
        },

        deleteOrphanedQuery(id) {
          axios.delete('/api/query/'+id);
        },
  
        showEditorSql() {
          this.isEditorPython = false;
          this.isEditorSql = true;
          this.isEditor = true;
        },

        showEditorPython() {
          this.isEditorPython = true;
          this.isEditorSql = false;
          this.isEditor = true;
        },

        closeEditors() {
          this.isEditor = false;
        },

        showErrorDetail() {
          alert(JSON.stringify(this.alertMsg))
        },

      }  
    }
  
  </script>

  <!-- do not use 'scoped' here (cascading will fail) -->
  <style>

    #oapen-query-preview, #oapen-script-preview {

      white-space: nowrap; 
      overflow:hidden; 
      text-overflow: ellipsis; 
      font-family: monospace; 
      font-size: 85%; 
      background: #fff; 
      padding: 1em;
      margin-top: 10px;
      color: #666666;
      border-bottom: dotted 2px #fff;
      background: #555;
      color: #eee;
    }

    .oapen-readonly-name input {
      cursor: pointer !important;
    }
  
  
  </style>