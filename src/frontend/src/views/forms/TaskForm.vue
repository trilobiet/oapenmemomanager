<template>
  <div class="task-form">

    <v-container fluid>

      <v-form ref="taskForm" v-model="isValidForm" validate-on="lazy input">

        <v-card class="elevation-5">

          <v-toolbar>
            <v-toolbar-title class="font-weight-bold">
              <!--<v-icon>mdi-table-edit</v-icon> Export task: {{ isNew? "new" : title }}-->
              <v-icon>mdi-home-account</v-icon> {{ client.name }}
            </v-toolbar-title>
          </v-toolbar>

          <v-divider></v-divider>

          <v-card-text class="pa-16">

            <my-wrapper>

              <my-form-header>{{ isNew ? "New Task" : "Edit task: " + taskName }}</my-form-header>

              <!-- update existing task -->
              <input type="hidden" v-if="!isNew" v-model="task.id" />

              <v-row>
                <v-col cols="12" sm="8">
                  <v-text-field label="task name" v-model="task.fileName" :rules="validation.fileName" clearable />
                  <div class="px-4 text-grey-darken-2 text-caption">export path: {{ client.username }}/{{ task.fileName }}</div>
                </v-col>
                <v-col cols="12" sm="4">
                  <v-text-field label="extension" v-model="task.extension" readonly bg-color="transparent"
                    class="text-blue-grey-lighten-1" :append-inner-icon="$func.extensionIcon(this.task.extension)" />
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12" sm="6">
                  <v-checkbox v-model="task.active"
                    :label="task.active ? 'Task is actived' : 'Task is currently suspended. Click to activate schedule.'"
                    :class="task.active ? 'text-green' : 'text-orange-darken-2'" />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-checkbox v-model="task.public"
                    :label="task.public ? 'Task is public. No passkey is needed!' : 'Task is private. Click to make public.'"
                    :class="task.public ? 'text-orange-darken-2' : 'text-green'" />
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field label="start date" v-model="task.startDate" :rules="validation.startDate"
                    append-inner-icon="mdi-hours-24" @click:appendInner="task.startDate = $func.tomorrowDate()"
                    hint="Format: yyyy-mm-dd (e.g. 2023-12-31)" />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-radio-group v-model="task.frequency" inline >
                    <v-radio label="daily" value="D" />
                    <v-radio label="weekly" value="W" />
                    <v-radio label="monthy" value="M" />
                    <v-radio label="yearly" value="Y" />
                  </v-radio-group>
                </v-col>
              </v-row>

              <v-row>
                <v-col>
                  <v-textarea label="description (shown to client)" v-model="task.description" rows="1" auto-grow />
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12" sm="6">

                  <div class="text-subtitle-2 text-grey-darken-2">
                    <v-icon icon="mdi-language-python" class="mr-1"/>
                    {{ task.script.name || 'new script'}}
                  </div>

                  <v-btn class="my-3" variant="tonal" width="100%" color="primary" prepend-icon="mdi-language-python" @focus="showEditorPython()">
                    open script editor
                  </v-btn>

                  <div id="oapen-script-preview">
                    {{ task.script.body || '[no content]' }}
                  </div>

                </v-col>
                <v-col cols="12" sm="6">

                  <div class="text-subtitle-2 text-grey-darken-2">
                    <v-icon icon="mdi-database-search" class="mr-1"/>
                    {{ task.script.query.name || 'new query' }}
                  </div>

                  <v-btn class="my-3" variant="tonal" width="100%" color="primary" prepend-icon="mdi-database-search" @focus="showEditorSql()">
                    open query editor
                  </v-btn>

                  <div id="oapen-query-preview">
                    {{ task.script.query.body || '[no content]' }}
                  </div>

                </v-col>
              </v-row>

              <v-dialog fullscreen v-model="isEditor">

                <v-card>

                  <v-card-title v-if="isEditorPython">
                    <v-container fluid class="pb-0">
                      <v-row>
                        <v-col>
                          <v-icon icon="mdi-language-python" />
                          {{ 'Python Editor: ' + (!task.script.name ? 'new' : task.script.name) }}
                        </v-col>
                        <v-col>
                          <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="showEditorSql()" class="bg-primary px-4">switch to SQL view</v-btn>
                            <v-btn variant="outlined" color="primary" @click="closeEditors()" title="exit fullscreen">
                              <v-icon size="25" icon="mdi-exit-to-app"/>
                            </v-btn>
                          </v-card-actions>    
                        </v-col>
                      </v-row>
                    </v-container>
                  </v-card-title>

                  <v-card-title v-if="isEditorSql">
                    <v-container fluid class="pb-0">
                      <v-row>
                        <v-col>
                          <v-icon icon="mdi-database-search" />
                          {{ 'SQL Editor: ' + (!task.script.query.name ? 'new' : task.script.query.name) }}
                        </v-col>
                        <v-col>
                          <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="showEditorPython()" class="bg-primary px-4">switch to Python view</v-btn>
                            <v-btn variant="outlined" color="primary" @click="closeEditors()" title="exit fullscreen">
                              <v-icon size="25" icon="mdi-exit-to-app"/>
                            </v-btn>
                          </v-card-actions>  
                        </v-col>
                      </v-row>
                    </v-container>
                  </v-card-title>

                  <v-card-text v-if="isEditorSql">
                    <v-ace-editor v-model:value="task.script.query.body" lang="mysql" theme="github_dark"/>
                  </v-card-text>

                  <v-card-text v-if="isEditorPython">
                    <v-ace-editor v-model:value="task.script.body" lang="python" theme="one_dark"/>
                  </v-card-text>

                </v-card>

              </v-dialog>

              <v-row>
                <v-col cols="12">
                  <v-textarea label="notes (only visible here)" v-model="task.notes" rows="1" auto-grow />
                </v-col>
              </v-row>

              <v-row v-if="!isNew">
                <v-col>
                  <my-danger-zone>
                    <v-btn @click="deleteTask()" text="Delete this task" prepend-icon="mdi-alert" variant="tonal" />
                  </my-danger-zone>
                </v-col>
              </v-row>

              <v-row v-if="alert !== this.$alert.NONE">
                <v-col>
                  <v-alert :type="alert" v-model="alert" closable @click:close="alert == this.$alert.NONE">
                    <span v-if="alert == this.$alert.ERROR" @click="showAlertDetail">{{ alertMsg }}</span>
                    <span v-else>{{ alertMsg }}</span>
                  </v-alert>
                </v-col>
              </v-row>

            </my-wrapper>

          </v-card-text>

          <v-divider class="border-dark"></v-divider>

          <v-card-actions class="bg-actions">

            <my-wrapper>

              <v-row>

                <v-col>
                  <v-btn @click="$router.go(-1)" text="Back" prepend-icon="mdi-menu-left" />
                </v-col>

                <v-col class="text-right">
                  <span v-if="isValidForm === false" class="text-red">
                    <v-icon>mdi-alert-circle-outline</v-icon>
                    Please fix validation issues before saving
                  </span>
                  <v-btn @click="saveTask" text="Save" :disabled="!isValidForm" />
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

// https://www.npmjs.com/package/vue3-ace-editor
// https://ace.c9.io/
import { VAceEditor } from 'vue3-ace-editor';
import 'ace-builds/src-noconflict/mode-python';
import 'ace-builds/src-noconflict/mode-mysql';
// https://ace.c9.io/build/kitchen-sink.html
import 'ace-builds/src-noconflict/theme-github_dark';
// import 'ace-builds/src-noconflict/theme-monokai';
import 'ace-builds/src-noconflict/theme-one_dark';
// eslint-disable-next-line no-unused-vars
import router from '@/router';

export default {

  components: {
    VAceEditor,
  },

  data() {
    return {
      isNew: false,
      task: {
        fileName: "",
        script: { query: {}, type: 'MAIN' },
      },
      isValidForm: false,
      id: null,
      client: { name: "", username: "" },
      clientid: null,
      frequencies: [
        { name: 'daily', value: 'D' },
        { name: 'weekly', value: 'W' },
        { name: 'monthly', value: 'M' },
        { name: 'yearly', value: 'Y' },
      ],
      taskName: "", // will hold task name as input before editing (for validation)
      takenFileNames: [],
      isEditor: false,
      isEditorSql: false,
      isEditorPython: false,
      alert: this.$alert.NONE, // ERROR,INFO,SUCCESS,WARNING,NONE
      alertMsg: "",
      alertDetail: "",
    }
  },

  mounted() {

    console.log("MOUNTED: " + this.$route.params.id)
    console.log(this.$func.tomorrowDate())

    if (this.$route.params.id) { // existing task
      this.id = this.$route.params.id;
      this.loadTask();
    }
    else { // add new task to existing client
      this.isNew = true;
      this.clientid = this.$route.params.clientid;
      this.loadClient(this.clientid)
      this.task.frequency = 'M' // default frequency
    }

  },

  watch: {

    'task.fileName': {
      handler(val) {
        // update extension field when filename changes
        this.task.extension = this.$func.getExtension(val);
      }
    },

    /*alert(new_val){ // auto close alert after 5 secs
      if(new_val){
        setTimeout(()=>{this.alert=""},5000)
      }
    },*/

  },

  computed: {

    validation() {

      return {

        fileName: [
          v => !!v || "Value is required",
          v => (v && v.length >= 4) || "Value cannot be shorter than 3 characters",
          v => this.hasExtension(v) || "Not a valid extension",
          v => this.validateFileNameFree(v) || 'File name is already in use for this client. Choose another name.',
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

      this.$axios.get(`/api/task/` + this.id)
        .then(resp => {
          this.task = resp.data;
          this.taskName = this.task.fileName;
          this.loadClient(this.task.homedir.id)
          if (this.task.script == null) this.task.script = { name: '', type: 'MAIN' }
          if (this.task.script.query == null) this.task.script.query = {}
        })
        .catch(error => console.log(error))
        .finally(() => this.loading = false)
    },

    loadClient(id) {

      this.$axios.get(`/api/homedir/` + id)
        .then(resp => {
          this.client = resp.data;
          this.takenFileNames = this.loadTakenFileNames(this.client)
        })
        .catch(error => console.log(error))
        .finally(() => this.loading = false)
    },

    loadTakenFileNames(client) {

      this.$axios.get(`/api/homedir/` + client.id + '/task')
        .then(resp => {
          this.takenFileNames = resp.data
            .map(v => v.fileName)
            .filter(v => v !== this.taskName); // not it's own name!
          console.log("TAKEN: " + this.takenFileNames)
        })
        .catch(error => console.log(error))
        .finally(() => this.loading = false)

    },

    setScriptAndQueryName() {

      let sqname = this.client.username + "_" + this.task.fileName.replace(".", "_")
      //+ "_" + this.$func.generateRandomNumber(4);
      this.task.script.name = sqname + ".py";
      this.task.script.query.name = sqname + ".sql"
    },

    saveTask() {

      this.setScriptAndQueryName();

      let taskToSave = JSON.parse(JSON.stringify(this.task))

      if (this.$refs.taskForm.validate()) {

        let postUrl = '/api/task';
        if (this.isNew) postUrl = '/api/homedir/' + this.clientid + '/task';

        this.$axios.post(postUrl, taskToSave)
          .then(resp => {
            console.log(resp)
            this.alert = this.$alert.SUCCESS;
            this.alertMsg = "Task saved";
            //setTimeout(() => { router.push({ name: 'client', params: { id: this.client.id } }) }, 1000);
            setTimeout(() => { router.go(-1) }, 1000);
          })
          .catch(err => {
            console.log(err)
            this.alert = this.$alert.ERROR;
            this.alertMsg = err.message;
            this.alertDetail = err
          })
          .finally(() => {
            console.log("Ready.")
          })

      }
      else {
        this.alert = this.$alert.ERROR;
        this.alertMsg = "There are validation errors"
      }

    },

    deleteTask() {

      if (confirm("This task will be deleted!\nAre you sure?")) {

        this.$axios.delete(`/api/task/` + this.task.id)
          .then(() => {
            this.alert = this.$alert.SUCCESS;
            this.alertMsg = "Task deleted";
            //setTimeout(() => { router.push({ name: 'client', params: { id: this.client.id } }) }, 1000);
            setTimeout(() => { router.go(-1) }, 1000);
          })
          .catch(err => {
            this.alert = this.$alert.ERROR;
            this.alertMsg = err.message;
            this.alertDetail = err
          })
      }

    },

    validateFileNameFree(fileName) {

      // console.log("FILENAME="+filename)
      // console.log("TAKEN="+this.takenFileNames)

      if (!fileName) return false;
      const posInList = this.takenFileNames.indexOf(fileName.trim())
      if (posInList == -1) return true
      else return false
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

    showAlertDetail() {
      alert(JSON.stringify(this.alertDetail))
    },

  }
}

</script>

