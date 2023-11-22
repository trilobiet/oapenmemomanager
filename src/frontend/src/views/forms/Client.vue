<template>

  <div class="client-form">

    <v-container fluid>

      <v-card class="elevation-5" >

          <v-toolbar color="transparent" >  
            <v-toolbar-title class="font-weight-bold">
              <v-icon>mdi-account</v-icon> {{ client.name }}
            </v-toolbar-title>
          </v-toolbar>

          <v-divider></v-divider>

          <v-card-text class="pa-16">

            <div style="width: 1280px; max-width:90%; margin: auto">

              <v-toolbar class="mb-4">  

                <v-toolbar-title>
                  username: {{ client.username }}
                </v-toolbar-title>

                <v-btn variant="tonal" class="bg-primary" @click="$router.push({ name: 'clientEdit', params: {id: client.id} })">Edit Client</v-btn>

              </v-toolbar>

              <v-row>
                <v-col>
                  <strong>Client home:</strong><br/>
                  <a :href="$func.getClientUrl(client.username)" target="memoweb">{{$func.getClientUrl(client.username)}}</a>
                </v-col>
              </v-row>  

              <v-row v-if="client.notes">
                <v-col>
                  <strong>Notes:</strong><br/>
                  {{client.notes}}
                </v-col>
              </v-row>  

              <v-toolbar class="mt-8 mb-4">  

                <v-toolbar-title>
                  <v-icon>mdi-table-clock</v-icon> export tasks
                </v-toolbar-title>

                <v-btn variant="tonal" class="bg-primary" @click="newTask()">New Export Task</v-btn>

              </v-toolbar>

              <v-row>
                <v-col>
                  <v-data-table :headers="headers" :items="tasks" hover>

                    <template v-slot:[`item.fileName`]="{ item }">
                      <span style="cursor:pointer" @click="editTask(item)"
                       class="text-blue-darken-4"
                      >{{item.fileName}}</span>
                    </template> 
                  
                    <template v-slot:[`item.active`]="{ item }">
                      <v-icon v-if="item.active" color="green" icon="mdi-circle" size="x-small"/>
                      <v-icon v-else color="#ddd" icon="mdi-circle" size="x-small"/>
                    </template>

                    <template v-slot:[`item.latestLog.date`]="{ item }">
                      <span v-if="item.latestLog">
                        {{ item.latestLog.date }}
                        <v-icon v-if="item.latestLog.success" color="green" icon="mdi-check-bold" size="small"/>
                        <v-icon v-else color="red" icon="mdi-alert-circle" size="small"/>
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

                  </v-data-table>
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
                    <v-btn @click="$router.go(-1)" text="Back" />
                </v-col>

              </v-row>   
            </v-container>

          </v-card-actions>

        </v-card>
      
    </v-container>

  </div>
  
</template>
  
<script>

  import axios from 'axios';
  
  export default {

    data() {
      return {
        client: {},
        tasks: [],
        headers: [
          { title: "file name", key: "fileName" },
          { title: "start date", key: "startDate" },
          { title: "frequency", key: "frequency" },
          { title: "last run", key: "latestLog.date" },
          { title: "active", key: "active" },
        ],
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

        axios.get(`/api/homedir/`+this.id)
          .then(resp => {
            this.client = resp.data;
            this.title = this.client.name;
            console.log("CLIENT: " + this.client.name)
          })
          .catch(error => console.log(error))
          .finally(() => {} )

        axios.get(`/api/homedir/`+this.id+`/task`)
          .then(resp => {
            this.tasks = resp.data;
            console.log("TASKS: " + this.tasks)
          })
          .catch(error => console.log(error))
          .finally(() => {} )
      },

      editTask(task) {
        this.$router.push({ name: 'taskEdit', params: {id: task.id} })
      },

      newTask() {
        this.$router.push({ name: 'taskNew', params: {clientid: this.client.id}  })
      },

    },

  }

  </script>