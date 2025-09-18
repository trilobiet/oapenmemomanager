<template>

  <div class="client-form">

    <loading v-model:active="isLoading" is-full-page></loading>

    <v-container fluid>

      <v-card class="elevation-5" >

        <v-toolbar>  
          <v-toolbar-title class="font-weight-bold">
            <v-icon>mdi-home-account</v-icon> {{ client.name }}
          </v-toolbar-title>
        </v-toolbar>

        <v-divider></v-divider>

        <v-card-text class="pa-16">

          <my-wrapper>

            <v-toolbar class="mb-4">  

              <v-toolbar-title class="text-body-2">
                <strong>Username: {{ client.username }}</strong>
              </v-toolbar-title>

              <v-btn variant="tonal" class="bg-primary" @click="editClient()">Edit Client</v-btn>

            </v-toolbar>

            <v-row>
              <v-col class="px-7">
                <strong>Client home:</strong><br/>
                
                <a :href="clientConfig.clientsUrl + 'clients/' + client.username" 
                 target="memoweb">{{clientConfig.clientsUrl + 'clients/' + client.username}}</a>
                <v-icon icon="mdi-open-in-new" size="x-small"/>
              </v-col>
            </v-row>  

            <v-row v-if="client.notes">
              <v-col class="px-7">
                <strong>Notes:</strong><br/>
                {{client.notes}}
              </v-col>
            </v-row>  

            <v-toolbar class="mt-8 mb-4">  

              <v-toolbar-title class="text-body-2">
                <v-icon>mdi-table-clock</v-icon> <b>Export tasks</b>
              </v-toolbar-title>

              <v-btn variant="tonal" class="bg-primary" @click="newTask()">New Export Task</v-btn>

            </v-toolbar>

            <v-row>
              <v-col>
                <v-data-table :headers="headers" :items="tasks" hover>

                  <template v-slot:[`item.fileName`]="{ item }">
                    <span style="white-space: nowrap;cursor:pointer" @click="editTask(item)" :class="item.active?'text-blue-darken-4':'text-grey'">
                      <v-icon color="#999" :icon="$func.extensionIcon($func.getExtension(item.fileName))" size="small" class="mr-2"/>
                      {{item.fileName}}
                    </span>
                  </template> 

                  <template v-slot:[`item.hasScript`]="{ item }">
                    <v-icon v-if="item.hasScript" color="green" icon="mdi-circle" size="x-small"/>
                    <v-icon v-else color="orange" icon="mdi-circle-half-full" size="x-small" title="No script provided"/>
                  </template>
                
                  <template v-slot:[`item.active`]="{ item }">
                    <v-icon v-if="item.active" color="green" icon="mdi-circle" size="x-small"/>
                    <v-icon v-else color="#ddd" icon="mdi-circle" size="x-small"/>
                  </template>

                  <template v-slot:[`item.frequency`]="{ item }">
                    <v-chip v-if="item.frequency=='D'" style="font-weight:bold" size="small" variant="flat" color="blue-lighten-5"
                     title="day">D</v-chip>
                    <v-chip v-else-if="item.frequency=='W'" style="font-weight:bold" size="small" variant="flat" color="blue-lighten-3"
                     title="week">W</v-chip>
                    <v-chip v-else-if="item.frequency=='M'" style="font-weight:bold" size="small" variant="flat" color="blue-darken-1"
                     title="month">M</v-chip>
                    <v-chip v-else-if="item.frequency=='Y'" style="font-weight:bold" size="small" variant="flat" color="indigo-darken-4"
                     title="year">Y</v-chip>
                     <span class="text-caption ml-2" style="color:#aaa">/ {{ item.startDate }}</span>
                  </template>

                  <template v-slot:[`item.nextUpdate`]="{ item }">
                    <span :class="item.active?'text-blue-darken-4':'text-grey'" v-text="item.nextUpdate"/> 
                  </template>

                  <template v-slot:[`item.latestLog`]="{ item }">
                    <span v-if="item.latestLog" @click="showRunlog(item.id, item.fileName)" style="cursor:pointer" 
                      :class="item.latestLog.success? 'text-green-darken-3' : 'text-red-darken-2'">
                      {{ this.$func.formatDateTime(item.latestLog.date) }}
                      <v-icon v-if="!item.latestLog.success" color="red" icon="mdi-alert-circle" size="x-small" style="vertical-align: baseline;"/>
                    </span>    
                    <span v-else>
                      <v-icon icon="mdi-progress-clock" color="grey" size="small" title="waiting for first run"/>
                    </span>
                  </template>

                  <template v-slot:[`item.download`]="{ item }">
                    <a v-if="item.latestLog && item.latestLog.success" :href="'/download/' + client.username + '/' + item.fileName">
                      <v-icon class="text-blue-darken-1" icon="mdi-download">
                      </v-icon>  
                    </a>
                    <v-icon v-else class="text-grey-lighten-2" icon="mdi-download"/>
                  </template>

                  <template v-slot:[`item.runNow`]="{ item }">
                    <v-icon v-if="item.hasScript" color="primary" icon="mdi-play-circle-outline" size="x-large" @click="confirmRunTask(item)"/>
                    <v-icon v-else class="text-grey-lighten-2" icon="mdi-play-circle-outline" size="large"/>
                  </template>

                  <template v-slot:no-data>
                    No tasks available.
                  </template>                  

                </v-data-table>

                <div class="ma-4 text-caption">
                  * Running a task manually does not interfere with the next scheduled running date.
                </div>

              </v-col>
            </v-row>

          </my-wrapper>

        </v-card-text>

        <v-divider class="border-dark"></v-divider>

        <v-card-actions class="bg-actions">

          <my-wrapper>

            <v-row >

              <v-col>
                <v-btn @click="$router.push({name:'home' })" text="Home" prepend-icon="mdi-menu-left" />
              </v-col>

            </v-row>   

          </my-wrapper>

        </v-card-actions>

      </v-card>

    </v-container>

    <!-- Dialog showing runlog -->
    <v-dialog width="90%" height="90%" maxHeight="90%" v-model="isShowRunlog" scrollable>
      <run-log :taskId="runLogTaskId" :taskName="runLogTaskName" @closeRunlog="this.isShowRunlog=false;"/>
    </v-dialog>

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

  import RunLog from './RunLog.vue';
  import Loading from 'vue-loading-overlay';
  import 'vue-loading-overlay/dist/css/index.css'; 
  
  export default {

    components: { RunLog, Loading },

    data() {
      return {
        client: {},
        tasks: [],
        headers: [
          { title: "export name", key: "fileName", width:"10em"},
          { title: "active", key: "active", width: "1em", align: "center"},
          { title: "complete", key: "hasScript", width: "1em", align: "center"},
          { title: "frequency/start date", key: "frequency", align: "left" },
          { title: "next run *", key: "nextUpdate" },
          { title: "last run (click for log)", key: "latestLog" },
          { title: "download", key: "download", align: "center" },
          { title: "run now", key: "runNow", align: "center" },
        ],
        pageNum: 2,
        isShowRunlog: false,
        runLogTaskId: null,
        runLogTaskName: null,
        isLoading: false,
        isShowRunDialog: false,
        isRunDialogSuccess: false,
        runDialogText: "",
        runDialogTitle: "",
      }      
    },

    mounted() {

      //console.log("MOUNTED: "+ this.$route.params.id)
      if (this.$route.params.id) {
        this.id = this.$route.params.id;
        this.loadClient();
      }  

    },

    computed: {

      clientConfig(){
        return this.$store.getters.getClientConfig
      },

    },

    methods: {

      loadClient() {

        console.log("LOADING....")

        this.$axios.get(`/api/homedir/`+this.id)
          .then(resp => {
            this.client = resp.data;
            this.title = this.client.name;
            //console.log("CLIENT: " + this.client.name)
          })
          .catch(error => console.log(error))
          .finally(() => {} )

          this.$axios.get(`/api/homedir/`+this.id+`/task`)
          .then(resp => {
            this.tasks = resp.data;
            //console.log("TASKS: " + JSON.stringify(this.tasks))
          })
          .catch(error => console.log(error))
          .finally(() => {} )
      },

      editClient() {
        this.$router.push({ name: 'clientEdit', params: {id: this.client.id} })
      },

      editTask(task) {
        this.$router.push({ name: 'taskEdit', params: {id: task.id},  })
      },

      newTask() {
        this.$router.push({ name: 'taskNew', params: {clientid: this.client.id}  })
      },

      showRunlog(taskId, taskName) {

        this.runLogTaskId = taskId
        this.runLogTaskName = taskName
        this.isShowRunlog = true
      },

      confirmRunTask(item) {

        this.$root.$refs.confirm.open(
          'Run task', 
          'All previously generated data for this task will be overwritten! <br/><br/>Are you sure you want to continue?', 
          { color: 'orange-darken-2', width: 400 }
        ).then((confirm) => {
          if (confirm) this.runTask(item)
        })
      },

      runTask(item) {

        this.isLoading = true;
        const url = `/runproxy/run/` + item.id;
        //console.log("URL: " + url)

        this.$axios.get(url)
          .then((resp) => {
            console.log("RESP: " + resp.data)
            this.runDialog(true, "Task completed successfully. Resulting export is available through the download button.")
          })
          .catch(error => {
            var msg = JSON.stringify(error.response)
            if(error.response.data.message) msg = error.response.data.message
            this.runDialog(false, this.preformat(msg));
            //this.runDialog(false, this.preformat(JSON.stringify(error)));
          })
          .finally(() => {
            this.loadClient() // reload list
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

    },

  }

</script>

<style>

.pre-run-msg {
    overflow:auto;
    padding:1em;
    background:#f3f3f3;
    font-size:70%;
}

</style>