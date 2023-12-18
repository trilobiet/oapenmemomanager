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
                
                  <template v-slot:[`item.active`]="{ item }">
                    <v-icon v-if="item.active" color="green" icon="mdi-circle" size="x-small"/>
                    <v-icon v-else color="#ddd" icon="mdi-circle" size="x-small"/>
                  </template>

                  <template v-slot:[`item.latestLog.date`]="{ item }">
                    <span v-if="item.latestLog" @click="showRunlog(item.id)" style="cursor:pointer" 
                      :class="item.latestLog.success? 'text-green-darken-3' : 'text-red-darken-2'">
                      {{ item.latestLog.date }}
                      <v-icon v-if="!item.latestLog.success" color="red" icon="mdi-alert-circle" size="small"/>
                    </span>    
                    <span v-else>
                      <v-icon icon="mdi-progress-clock" color="grey" size="small" title="waiting for first run"/>
                    </span>
                  </template>

                  <!--
                  <template v-if=false v-slot:[`item.latestLog.date`]="{ item }">
                    {{ item.latestLog.date }}
                    <v-icon v-if="item.latestLog.success" color="green" icon="mdi-check-bold" size="small"/>
                    <v-icon v-else color="red" icon="mdi-alert-circle" size="small"/>
                  </template>-->

                  <template v-slot:[`item.frequency`]="{ item }">
                    <v-chip v-if="item.frequency=='D'" size="small" variant="flat" color="yellow">daily</v-chip>
                    <v-chip v-else-if="item.frequency=='W'" size="small" variant="flat" color="amber">weekly</v-chip>
                    <v-chip v-else-if="item.frequency=='M'" size="small" variant="flat" color="orange">monthly</v-chip>
                    <v-chip v-else-if="item.frequency=='Y'" size="small" variant="flat" color="deep-orange">yearly</v-chip>
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
      <run-log :taskId="runlogId" @closeRunlog="this.isShowRunlog=false;"/>
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
          { title: "File name", key: "fileName"},
          { title: "Active", key: "active", width: "1em"},
          { title: "Last run", key: "latestLog.date" },
          { title: "Frequency", key: "frequency" },
          { title: "Start date", key: "startDate" },
        ],
        isShowRunlog: false,
        runlogId: null,
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

      showRunlog(id) {

        this.runlogId = id
        this.isShowRunlog = true;
      },

    },

  }

  </script>