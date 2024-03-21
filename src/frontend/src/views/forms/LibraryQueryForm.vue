<template>
  <div class="query-form">

    <v-container fluid>

      <v-form ref="queryForm" v-model="isValidForm" validate-on="lazy input">

        <v-card class="elevation-5">

          <v-toolbar>
            <v-toolbar-title class="font-weight-bold">
              <v-icon>mdi-database-search</v-icon> Query Library
            </v-toolbar-title>
          </v-toolbar>

          <v-divider></v-divider>

          <v-card-text class="pa-16">

            <my-wrapper>

              <my-form-header>{{ isNew ? "New query" : "Edit " + curName }}</my-form-header>

              <v-row>

                <v-col>
                  <v-text-field v-if="!refscripts.length" label="query name" v-model="query.name" :rules="validation.queryName" />
                  <v-text-field v-else label="query name" v-model="query.name" readonly disabled 
                   hint="To edit name, remove references first" persistent-hint />
                </v-col>

              </v-row>

              <v-row>

                <v-col>

                  <v-btn class="my-3" variant="tonal" width="100%" color="primary" prepend-icon="mdi-database-search" @focus="showEditor()">
                    open query editor
                  </v-btn>

                  <div id="oapen-query-preview">
                    {{ query.body ? query.body : '[no content]' }}
                  </div>

                </v-col>

              </v-row>

              <v-dialog fullscreen v-model="isEditor">

                <v-card>

                  <v-card-title>
                    <v-container fluid class="pb-0">
                      <v-row>
                        <v-col>
                          <v-icon icon="mdi-database-search" />
                          {{ 'SQL Editor: ' + (!query.name ? 'new' : query.name) }}
                        </v-col>
                        <v-col>
                          <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn variant="outlined" color="primary" @click="closeEditor()" title="exit fullscreen">
                              <v-icon size="25" icon="mdi-exit-to-app"/>
                            </v-btn>
                          </v-card-actions>  
                        </v-col>
                      </v-row>
                    </v-container>
                  </v-card-title>

                  <v-card-text>
                    <v-ace-editor v-model:value="query.body" lang="mysql" theme="github_dark"/>
                  </v-card-text>

                </v-card>

              </v-dialog>

              <!--
              <v-row>
                <v-col>
                  <v-textarea label="params" v-model="query.params" hint="one param per line" rows="4" auto-grow />
                </v-col>
                <v-col>
                  <v-textarea label="notes" v-model="query.notes" rows="4" auto-grow />
                </v-col>
              </v-row>
              -->

              <v-row v-if="!isNew">
                <v-col>
                  <v-data-table v-model:expanded="refscriptsExpanded" show-expand
                    hide-default-header class="bg-grey-lighten-5 oapen-script-refs-table"
                    :headers="refScriptsHeaders" no-data-text="no references" 
                    :items="refscripts" item-value="name" density="comfortable" items-per-page="5"
                  >
                    <template v-slot:[`item.taskOutline.fileName`]="{ item }">
                      <span @click="this.$router.push({ name: 'taskEdit', params: {id: item.taskOutline.id},  })">
                        {{item.taskOutline.fileName}}
                      </span>  
                    </template>

                    <template v-slot:expanded-row="{ columns, item }">
                      <tr>
                        <td :colspan="columns.length" class="bg-grey-darken-3">
                          <code><pre>{{item.body}}</pre></code>
                        </td>
                      </tr>
                    </template>                    
                
                  </v-data-table>
                </v-col>
              </v-row>

              <v-row v-if="!isNew" >
                <v-col>
                  <my-danger-zone v-if="refscripts.length" color="grey">
                    Delete query not available ({{refscripts.length}} references)
                    <v-btn disabled text="Delete this query" prepend-icon="mdi-alert" variant="tonal"/>
                  </my-danger-zone>
                  <my-danger-zone v-else>
                    <v-btn @click="confirmDeleteQuery()" text="Delete this query" prepend-icon="mdi-alert" variant="tonal"/>
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
                  <v-btn @click="saveQuery" text="Save" :disabled="!isValidForm" />
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
      curName: "",
      isNew: false,
      isEditor: false,
      query: {library: true},
      isValidForm: false,
      id: null,
      alert: this.$alert.NONE, // ERROR,INFO,SUCCESS,WARNING,NONE
      alertMsg: "",
      alertDetail: "",
      takenQueryNames: [],
      refscripts: [],
      refScriptsHeaders: [
        {title: 'references in scripts', key: 'name'},
        {title: 'task', key: 'taskOutline.fileName'},
        {title: 'client', key: 'taskOutline.client'},
        {title: '', key: 'data-table-expand' }, 
      ],
      refscriptsExpanded: []
    }
  },

  mounted() {

    console.log("MOUNTED: " + this.$route.params.id)
    console.log(this.$func.tomorrowDate())

    if (this.$route.params.id) {
      this.id = this.$route.params.id;
      this.loadQuery();
    }
    else {
      this.isNew = true;
    }

  },

  watch: {

  },

  computed: {

    validation() {

      return {

        queryName: [
          v => (v && v.length >= 2) || "Query name is required",
          v => (v && this.$func.isValidModuleName(v)) || "Query name can only contain lower case a-z, 0-9 and _",
          v => this.validateQueryNameFree(v) || 'Query name is already in use. Choose another name.',
        ],
      }
    },

  },

  methods: {

    loadQuery() {

      this.$axios.get(`/api/query/` + this.id)
        .then(resp => {
          this.query = resp.data
          this.loadReferingScripts(this.query)
          this.loadTakenQueryNames()
          this.curName = this.query.name
        })
        .catch(error => console.log(error))
        .finally(() => this.loading = false)

    },

    loadReferingScripts(query) {

      this.$axios.get(`/api/script/searchimport?term=` + query.name)
        .then(resp => {
          this.refscripts = resp.data;
          //console.log("REFSCRIPTS=" + this.refscripts[0].body)
        })
        .catch(error => console.log(error))
        .finally(() => this.loading = false)

    },

    loadTakenQueryNames() { 

      this.$axios.get(`/api/query/`)
        .then(resp => {
          this.takenQueryNames = resp.data
            .map(v => v.name)
            .filter(v => v !== this.query.name); // not it's own name!
          console.log("TAKEN: " + this.takenQueryNames)
        })
        .catch(error => console.log(error))
        .finally(() => this.loading = false)

    },

    saveQuery() {

      if (this.$refs.queryForm.validate()) {

        this.$axios.post(`/api/query`, this.query)
          .then(resp => {
            console.log(resp)
            this.alert = this.$alert.SUCCESS;
            this.alertMsg = "Query saved";
            setTimeout(() => { router.push({ name: 'library' }) }, 1000);
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

    confirmDeleteQuery() {

      this.$root.$refs.confirm.open(
          'Delete query', 
          'This library query will be deleted irreversibly! <br/><br/>Are you sure you want to continue?', 
          { color: 'orange-darken-2', width: 400 }
        ).then((confirm) => {
          if (confirm) this.deleteQuery()
        })

    },

    deleteQuery() {

      this.$axios.delete(`/api/query/` + this.query.id)
        .then(() => {
          this.alert = this.$alert.SUCCESS;
          this.alertMsg = "Query deleted";
          setTimeout(() => { router.push({ name: 'library' }) }, 1000);
        })
        .catch(err => {
          this.alert = this.$alert.ERROR;
          this.alertMsg = err.message;
          this.alertDetail = err
        })

    },

    validateQueryNameFree(queryName) {

      // console.log("FILENAME="+filename)
      // console.log("TAKEN="+this.takenFileNames)

      if (!queryName) return false;
      const posInList = this.takenQueryNames.indexOf(queryName.trim())
      if (posInList == -1) return true
      else return false
    },

    showEditor() {
      this.isEditor = true;
    },

    closeEditor() {
      this.isEditor = false;
    },

    showAlertDetail() {
      alert(JSON.stringify(this.alertDetail))
    },

  }
}

</script>

