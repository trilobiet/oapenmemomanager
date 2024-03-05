<template>

  <div class="client-form">

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
                <a :href="$func.getClientUrl(client.username)" target="memoweb">{{$func.getClientUrl(client.username)}}</a>
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
                <v-data-table :headers="headers" :items="tasks" hover >

                  <template v-slot:[`item.fileName`]="{ item }">
                    <v-icon color="#999" :icon="$func.extensionIcon($func.getExtension(item.fileName))" size="small" class="mr-2"/>
                    <span style="cursor:pointer" @click="editTask(item)"
                      class="text-blue-darken-4"
                    >{{item.fileName}}</span>
                  </template> 

                  <template v-slot:[`item.hasScript`]="{ item }">
                    <v-icon v-if="item.hasScript" color="green" icon="mdi-circle" size="x-small"/>
                    <v-icon v-else color="orange" icon="mdi-circle-half-full" size="x-small"/>
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
                  </template>

                  <template v-slot:[`item.latestLog.date`]="{ item }">
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
                    <a v-if="item.latestLog.success" :href="'/download/' + item.id + '/' + item.fileName">
                      <v-icon class="text-blue-darken-1" icon="mdi-download">
                      </v-icon>  
                    </a>
                    <v-icon v-else class="text-grey-lighten-2" icon="mdi-download"/>
                  </template>

                  <!--
                  <template v-if=false v-slot:[`item.latestLog.date`]="{ item }">
                    {{ item.latestLog.date }}
                    <v-icon v-if="item.latestLog.success" color="green" icon="mdi-check-bold" size="small"/>
                    <v-icon v-else color="red" icon="mdi-alert-circle" size="small"/>
                  </template>-->

                  <template v-slot:[`item.runNow`]="{ item }">
                    <v-icon color="primary" icon="mdi-play-circle-outline" size="x-large" @click="runTask(item)"/>
                  </template>

                  <template v-slot:no-data>
                    No tasks available.
                  </template>                  

                </v-data-table>
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

    <v-dialog width="90%" height="90%" maxHeight="90%" v-model="isShowRunlog" scrollable>
      <run-log :taskId="runLogTaskId" :taskName="runLogTaskName" @closeRunlog="this.isShowRunlog=false;"/>
    </v-dialog>

  </div>
  
</template>
  
<script>

  import RunLog from './RunLog.vue';
  
  export default {

    components: { RunLog, },

    data() {
      return {
        client: {},
        tasks: [],
        headers: [
          { title: "File name", key: "fileName", width:"10em"},
          { title: "Completed", key: "hasScript", width: "1em", align: "center"},
          { title: "Active", key: "active", width: "1em", align: "center"},
          { title: "Frequency", key: "frequency", align: "center" },
          { title: "Last run (click for log)", key: "latestLog.date" },
          { title: "Download", key: "download", align: "center" },
          { title: "Run now", key: "runNow", align: "center" },
        ],
        isShowRunlog: false,
        runLogTaskId: null,
        runLogTaskName: null,
      }      
    },

    mounted() {

      console.log("MOUNTED: "+ this.$route.params.id)
      if (this.$route.params.id) {
        this.id = this.$route.params.id;
        this.loadClient();
      }  

    },

    computed: {

    },

    methods: {

      loadClient() {

        this.$axios.get(`/api/homedir/`+this.id)
          .then(resp => {
            this.client = resp.data;
            this.title = this.client.name;
            console.log("CLIENT: " + this.client.name)
          })
          .catch(error => console.log(error))
          .finally(() => {} )

          this.$axios.get(`/api/homedir/`+this.id+`/task`)
          .then(resp => {
            this.tasks = resp.data;
            console.log("TASKS: " + this.tasks)
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

      runTask(item) {
        console.log("Run Task " + item.id)
        // TODO implement
        alert("Feature to be implemented in a future version")
      }

    },

  }

  </script>