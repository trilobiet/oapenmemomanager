<template>

    <v-card class="elevation-5">

      <v-toolbar>  
        <v-toolbar-title class="font-weight-bold">
          <v-icon>mdi-language-python</v-icon> Script & Query Libraries
        </v-toolbar-title>
      </v-toolbar>

      <v-container fluid style="overflow: auto;">

        <v-card-text> 

          Click <v-icon icon="mdi-content-duplicate" size="small"/> to copy import statements to the clipboard.
          <div class="bg-grey-lighten-2 pa-2 mt-2">  <code style="font-size:90%">from <b>queries</b> import <b>module_name</b></code>  
            <span style="font-size:90%" class="ml-2 text-grey-darken-1">query will be available in variable named 'query'</span>
            <br/>
            <code style="font-size:90%">  from <b>sniplets</b> import <b>sniplet_name</b></code>
          </div>

          <v-text-field bg-color="transparent"
              v-model="search"
              append-icon="mdi-magnify"
              label="Search"
              variant="underlined">
          </v-text-field>

          <!-- single-expand show-expand item-key="username" -->  
          <v-data-table  
            :sort-by="['name','references']" show-expand item-value="name"
            :loading="loading" :search="search" 
            :headers="queryHeaders" :items="queries">

            <template v-slot:[`item.copy`]="{ item }">
              <v-icon icon="mdi-content-duplicate" @click="copyImportStatement($event, 'queries', item.name)"/>
            </template>

            <template v-slot:expanded-row="{ item, columns }">
              <tr style="background:#bbb;">
                <td :colspan="columns.length" style="height:100%;">
                  <pre style="background:#fcf8f8;font-size:.7rem;height:100%;overflow:auto;padding:1em;">{{ item.body }}</pre>
                </td>
              </tr>
            </template>

            <template v-slot:no-data>
              No data available. Your session may have expired.
              <br/><a href="/login">Login again to start a new session</a>
            </template>                  

          </v-data-table>

          <br/>

          <v-data-table  
            :sort-by="['name','references']" show-expand  item-value="name"
            :loading="loading" :search="search" 
            :headers="scriptHeaders" :items="scripts">

            <template v-slot:[`item.copy`]="{ item }">
              <v-icon icon="mdi-content-duplicate" @click="copyImportStatement($event, 'sniplets', item.name)"/>
            </template>

            <template v-slot:expanded-row="{ item, columns }">
              <tr style="background:#bbb;">
                <td :colspan="columns.length" style="height:100%;">
                  <pre style="background:#fcf8f8;font-size:.7rem;height:100%;overflow:auto;padding:1em;">{{ item.body }}</pre>
                </td>
              </tr>
            </template>

            <template v-slot:no-data>
              No data available. Your session may have expired.
              <br/><a href="/login">Login again to start a new session</a>
            </template>                  

          </v-data-table>

        </v-card-text>

      </v-container>

    </v-card>    


</template>


<script>

  export default {

    data() {
      return {
        loading: false,
        search: '',
        queryHeaders: [],
        queries:[], 
        scriptHeaders: [],
        scripts:[], 
      }    
    },

    mounted() {
      this.loading = true
      this.loadQueries()
      this.loadScripts()
      this.loading = false
    },

    methods: {

      loadQueries() {

        this.$axios.get(`/api/query/library`)
        .then(resp => {
          this.queries=resp.data
          this.queryHeaders=this.getQueryHeaders(resp.data)
        })
        .catch(error => console.log(error))
      },
      
      loadScripts() {

        this.$axios.get(`/api/script/snippets`)
        .then(resp => {
          this.scripts=resp.data
          this.scriptHeaders=this.getScriptHeaders(resp.data)
        })
        .catch(error => console.log(error))
      },

      getQueryHeaders() {

        let arr = [
          { title: "queries", key: "name", width: "15em" },
          { title: "", key: "copy", width: "5em" },
          { title: "", key: "data-table-expand", width: "auto"},
        ];

        return arr;
      },

      getScriptHeaders() {

        let arr = [
          { title: "sniplets", key: "name", width: "15em"  },
          { title: "", key: "copy", width: "5em" },
          { title: "", key: "data-table-expand", width: "auto"  },
        ];

        return arr;
      },

      copyImportStatement(event,pakkage,module) { // reset previous animation
        event.target.classList.remove('animate-copy-icon')  
        navigator.clipboard.writeText("from " + pakkage + " import " + module); 
        setTimeout(() => { 
          event.target.classList.add('animate-copy-icon'); 
        }, 0); // add some animation feedback
        setTimeout(() => { 
          this.$emit('cancel')
        }, 1000); // add some animation feedback
        
      },

    }  
  }

</script>

<style>

  .animate-copy-icon {
    animation: color-fade 3s;
  }

  @keyframes color-fade {
    0% {
      color: lightgreen;
    }
    100% {
      color: none;
    }
  }

</style>
