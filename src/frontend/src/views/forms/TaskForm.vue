<template>
  <div class="task-form">

    <loading v-model:active="isLoading" is-full-page></loading>

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

                  <div class="text-subtitle-2 text-grey-darken-2 mb-2">
                    <v-icon icon="mdi-language-python" class="mr-1"/>
                    {{ task.script.name || 'new script'}}
                  </div>

                  <div id="oapen-script-preview" @click="showEditorPython()">
                    {{ task.script.body || '[no content]' }}
                  </div>

                </v-col>
                <v-col cols="12" sm="6">

                  <div class="text-subtitle-2 text-grey-darken-2 mb-2">
                    <v-icon icon="mdi-database-search" class="mr-1"/>
                    {{ !task.script.query.name ? 'new' : 'queries.'+task.script.query.name }}
                  </div>

                  <div id="oapen-query-preview" @click="showEditorSql()">
                    {{ task.script.query.body || '[no content]' }}
                  </div>

                </v-col>
              </v-row>

              <v-row v-if="!isNew">
                <v-col>
                  <v-card variant="outlined" color="green-darken-2">
                    <v-card-item>
                      <v-btn @click="dryRunTask(task)" text="Dry run" prepend-icon="mdi-flask-outline" variant="tonal" class="mr-2"/>
                      <v-btn v-if="isDryRunSuccess" text="Download result" :href="'/download/tmp/' + client.username + '_' + task.fileName"
                        prepend-icon="mdi-download" variant="tonal"/>
                    </v-card-item> 
                  </v-card>                  
                </v-col>
              </v-row>    

              <!-- Dialog: Fullscreen Python/SQL editor -->
              <v-dialog fullscreen v-model="isEditor" >

                <v-card class="bg-grey-darken-3">

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
                            <v-btn @click="this.showLibrary=true" class="bg-primary px-4">
                              <v-icon icon="mdi-language-python" class="mr-1"/>browse libraries
                            </v-btn>
                            <v-btn @click="showEditorSql()" class="bg-primary px-4">
                              <v-icon icon="mdi-database-search" class="mr-1"/>switch to SQL view
                            </v-btn>
                            <v-btn @click="closeEditors()" title="exit fullscreen" class="bg-primary px-4">
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
                          {{ 'SQL Editor: ' + (!task.script.query.name ? 'new' : 'queries.'+task.script.query.name) }}
                        </v-col>
                        <v-col>
                          <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="showEditorPython()" class="bg-primary px-4">
                              <v-icon icon="mdi-language-python" class="mr-1"/>&nbsp;switch to Python view
                            </v-btn>
                            <v-btn @click="closeEditors()" title="exit fullscreen" class="bg-primary px-4">
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

                <!-- Dialog: Inspect libraries -->
                <v-dialog v-model="showLibrary" width="800" max-width="90%" max-height="80%" scrollable>
                  <library-quick @cancel="cancelLibraryDialog"/>
                </v-dialog>  

              </v-dialog>

              <v-row>
                <v-col cols="12">
                  <v-textarea label="notes (only visible here)" v-model="task.notes" rows="1" auto-grow />
                </v-col>
              </v-row>

              <v-row v-if="!isNew">
                <v-col>
                  <my-danger-zone>
                    <v-btn @click="confirmDeleteTask()" text="Delete this task" prepend-icon="mdi-alert" variant="tonal" />
                  </my-danger-zone>
                </v-col>
              </v-row>

              <!-- Alert showing SAVE success/failure -->
              <v-row v-if="alert !== this.$alert.NONE">
                <v-col>
                  <v-alert :type="alert" closable @click:close="alert == this.$alert.NONE">
                    <span v-if="alert == this.$alert.ERROR" @click="showAlertDetail">{{ alertMsg }}</span>
                    <span v-else>{{ alertMsg }}</span>
                  </v-alert>
                </v-col>
              </v-row>

              <v-row v-if="!isNew">
                <v-col class="px-4 text-grey-darken-1 text-caption">
                  Last edited by {{ task.updatedBy }} on {{ task.updatedAt }}
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

    <!-- Dialog showing run result -->
    <v-dialog v-model="isShowRunDialog">
      <v-card class="mx-auto" width="800" max-width="90%" min-width="400">
        <v-card-title :class="isRunDialogSuccess? 'bg-green':'bg-red'">
          <v-icon v-if="isRunDialogSuccess" icon="mdi-check-circle-outline" />
          <v-icon v-else icon="mdi-alert-circle-outline" />
          {{ runDialogTitle }}
        </v-card-title>
        <v-card-text v-html="runDialogText"></v-card-text>
        <v-card-actions>
          <v-spacer/>
          <v-btn variant="tonal" text="Ok" @click="isShowRunDialog = false"></v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

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
import LibraryQuick from '@/views/LibraryQuick.vue';
import Loading from 'vue-loading-overlay';

export default {

  components: {
    VAceEditor, LibraryQuick, Loading
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
      isLoading: false,
      isDryRunSuccess: false,
      alert: this.$alert.NONE, // ERROR,INFO,SUCCESS,WARNING,NONE
      alertMsg: "",
      alertDetail: "",
      showLibrary: false,
      isShowRunDialog: false,
      isRunDialogSuccess: false,
      runDialogText: "",
      runDialogTitle: "",
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

    alert(new_val){ // auto close alert after 5 secs
      if(new_val){
        setTimeout(()=>{this.alert=this.$alert.NONE},5000)
      }
    }

  },

  computed: {

    validation() {

      return {

        fileName: [
          v => !!v || "Value is required",
          v => (v && v.length >= 4) || "Value cannot be shorter than 3 characters",
          v => this.hasExtension(v) || "Not a valid extension",
          v => this.$func.isValidFileName(v) || "Task name can only contain A-Z, a-z, 0-9, _ and .",
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

      let sqname = this.client.username + ":" + this.task.fileName.replace(".", "_")
      //+ "_" + this.$func.generateRandomNumber(4);
      this.task.script.name = sqname.toLowerCase() //+ ".py";
      this.task.script.query.name = sqname.toLowerCase() //+ ".sql"
    },

    saveTask() {

      this.setScriptAndQueryName();

      let taskToSave = JSON.parse(JSON.stringify(this.task))

      if (this.$refs.taskForm.validate()) {

        let postUrl = '/api/task';
        if (this.isNew) postUrl = '/api/homedir/' + this.clientid + '/task';

        this.$axios.post(postUrl, taskToSave)
          .then((resp) => {
            console.log("SAVE: " + resp.data.id)
            this.alert = this.$alert.SUCCESS;
            this.alertMsg = "Task saved";
            this.id = resp.data.id
            this.isNew = false
            this.loadTask();
            router.replace({ name: 'taskEdit', params: { id: resp.data.id } })
          })
          .catch(err => {
            // console.log(err)
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

    confirmDeleteTask() {

      this.$root.$refs.confirm.open(
          'Delete task', 
          'This task will be deleted irreversibly! <br/><br/>Are you sure you want to continue?', 
          { color: 'orange-darken-2', width: 400 }
        ).then((confirm) => {
          if (confirm) this.deleteTask()
        })

    },

    deleteTask() {

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

    cancelLibraryDialog() {
      this.showLibrary = false;
    },

    dryRunTask(item) {

      this.isLoading = true;
      const url = `/runproxy/run/` + item.id + "?dry=true";
      console.log("URL: " + url)

      this.$axios.get(url)
        .then((resp) => {
          console.log("RESP: " + resp.data)
          this.isDryRunSuccess = true
          this.runDialog(true, "Dry run completed successfully. Resulting export is available through the download button.")
        })
        .catch(error => {
          var msg = JSON.stringify(error.response)
          if(error.response.data.message) msg = error.response.data.message
          this.runDialog(false, this.preformat(msg));
          //this.runDialog(false, this.preformat(JSON.stringify(error)));
        })
        .finally(() => {
          this.isLoading = false
        })
    },

    runDialog(isSuccess, text) {

      this.runDialogText = text
      this.runDialogTitle = isSuccess? "success" : "fail"
      this.isRunDialogSuccess = isSuccess
      this.isShowRunDialog = true
    },

    preformat(string) {
      return `Some things didn´t work out too well:<br/><br/><pre class="pre-run-msg">${string}</pre>`
    },    

  }
}

</script>

