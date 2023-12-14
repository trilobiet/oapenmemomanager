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

              <my-form-header>{{ isNew ? "New library query" : "Edit library query " }}</my-form-header>

              <!-- update existing query -->
              <!--
              <input type="hidden" v-if="!isNew" v-model="query.id"/> -->

              <v-row>

                <v-col cols="6">
                  <v-text-field label="query name" v-model="query.name" :rules="validation.name" />
                </v-col>

                <v-col cols="6">

                  <v-text-field bg-color="transparent" class="text-primary oapen-readonly-name" @focus="showEditor()"
                    label="query (click to edit)" readonly v-model="query.name" variant="outlined" persistent-placeholder
                    prepend-inner-icon="mdi-database-search" />

                </v-col>

              </v-row>

              <v-row>

                <v-col>
                  <div id="oapen-query-preview">
                    {{ query.body ? query.body : '[no content]' }}
                  </div>
                </v-col>

              </v-row>

              <v-dialog fullscreen v-model="isEditor">

                <v-card>

                  <v-card-title>
                    <v-container fluid>
                      <v-row>
                        <v-col>
                          <v-icon icon="mdi-database-search" />
                          {{ 'SQL Editor: ' + (!query.name ? 'new' : query.name) }}
                        </v-col>
                      </v-row>
                    </v-container>
                  </v-card-title>

                  <v-card-text>

                    <v-ace-editor v-model:value="query.body" lang="mysql" theme="github_dark"/>

                  </v-card-text>

                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text="Close Editor" @click="closeEditor()"></v-btn>
                  </v-card-actions>

                </v-card>

              </v-dialog>

              <v-row>
                <v-col>
                  <v-textarea label="params" v-model="query.params" hint="one param per line" rows="4" auto-grow />
                </v-col>
                <v-col>
                  <v-textarea label="notes" v-model="query.notes" rows="4" auto-grow />
                </v-col>
              </v-row>

              <v-row>
                <v-col>
                  <v-data-table-virtual v-model:expanded="refscriptsExpanded" show-expand
                    hide-default-header class="bg-grey-lighten-5 oapen-query-refs-table"
                    :headers="refScriptsHeaders" 
                    :items="refscripts" item-value="name" density="compact"
                  >
                    <!--
                    <template v-slot:[`item.body`]="{ item }">
                      <code>{{$func.truncate(item.body,100)}}</code>
                    </template> -->

                    <template v-slot:expanded-row="{ columns, item }">
                      <tr>
                        <td :colspan="columns.length" class="bg-grey-darken-3">
                          <code><pre>{{item.body}}</pre></code>
                        </td>
                      </tr>
                    </template>                    
                
                  </v-data-table-virtual>
                </v-col>
              </v-row>

              <v-divider class="mt-4 mb-8" />

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
      isNew: false,
      isEditor: false,
      query: {},
      isValidForm: false,
      id: null,
      alert: this.$alert.NONE, // ERROR,INFO,SUCCESS,WARNING,NONE
      alertMsg: "",
      alertDetail: "",
      takenQueryNames: {},
      refscripts: [],
      refScriptsHeaders: [
        {title: 'usage in scripts', key: 'name'},
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
        ],
      }
    },

  },

  methods: {

    loadQuery() {

      this.$axios.get(`/api/query/` + this.id)
        .then(resp => {
          this.query = resp.data;
          this.loadReferingScripts(this.query.name)
        })
        .catch(error => console.log(error))
        .finally(() => this.loading = false)

    },

    loadReferingScripts(qname) {

      this.$axios.get(`/api/script/searchinbody?term=` + qname)
        .then(resp => {
          this.refscripts = resp.data;
          //console.log("REFSCRIPTS=" + this.refscripts[0].body)
        })
        .catch(error => console.log(error))
        .finally(() => this.loading = false)

    },

    loadTakenQueryNames(query) {

      // TODO  
      this.$axios.get(`/api/query/` + query.id)
        .then(resp => {
          this.takenQueryNames = resp.data
            .map(v => v.name)
            .filter(v => v !== this.name); // not it's own name!
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
            setTimeout(() => { router.push({ name: 'queries' }) }, 1000);
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

    deleteQuery() {

      if (confirm("This query will be deleted!\nAre you sure?")) {

        this.$axios.delete(`/api/query/` + this.query.id)
          .then(() => {
            this.alert = this.$alert.SUCCESS;
            this.alertMsg = "Query deleted";
            setTimeout(() => { router.push({ name: 'queries' }) }, 1000);
          })
          .catch(err => {
            this.alert = this.$alert.ERROR;
            this.alertMsg = err.message;
            this.alertDetail = err
          })
      }

    },

    validateQuerynameFree(queryname) {

      // console.log("FILENAME="+filename)
      // console.log("TAKEN="+this.takenFileNames)

      if (!queryname) return false;
      const posInList = this.takenQueryNames.indexOf(queryname.trim())
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

<style lang="scss">

  .oapen-query-refs-table .v-table__wrapper{
      overflow-x: hidden;

      code pre {
        display:block;
        padding: 10px;
        font-size:80%;
      }
  }

  .v-data-table__td {
    overflow: hidden;
    white-space: wrap;
  }

</style>