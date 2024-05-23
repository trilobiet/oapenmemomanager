<template>
  
  <div class="queries">

    <v-container fluid>

      <v-card class="elevation-5">

        <v-toolbar>  

          <v-toolbar-title class="font-weight-bold">
            <v-icon>mdi-database-search</v-icon> Query Library
          </v-toolbar-title>

          <v-btn variant="tonal" class="bg-primary" @click="newQuery()">New Query</v-btn>

        </v-toolbar>

        <v-card-text> 

          <v-text-field
              v-model="tableSearch"
              append-icon="mdi-magnify"
              label="Search"
              variant="underlined">
          </v-text-field>

          <!-- single-expand show-expand item-key="username" -->  
          <v-data-table  
            :sort-by="['name','references']" hover items-per-page="5"
            :loading="loading" :search="tableSearch" 
            :headers="headers" :items="queries"   
            calculate-widths>

            <template v-slot:[`item.name`]="{ item }">
              <span style="cursor:pointer" @click="editQuery(item)"
                class="text-blue-darken-4"
              >{{item.name}}</span>
            </template> 

            <template v-slot:no-data>
              No data available. Your session may have expired.
              <br/><a href="/login">Login again to start a new session</a>
            </template>                  

          </v-data-table>

        </v-card-text>

      </v-card>    

    </v-container>

    </div>

</template>


<script>

export default {

  data() {
    return {
      loading: true,
      tableSearch: '',
      headers: [],
      queries:[], 
    }    
  },
  
  computed: {

  },

  mounted() {
    this.loadQueries();
  },
  
  methods: {

    loadQueries() {

      this.loading = true; 
      this.$axios.get(`/api/query/library`)
      .then(resp => {
         this.queries=resp.data;
         console.log(resp.data);
         this.headers=this.getHeaders(resp.data);
      })
      .catch(error => console.log(error))
      .finally(() => this.loading = false )
    },
    
    getHeaders() {

      let arr = [
        { title: "queries", key: "name" },
				{ title: "# refering scripts", key: "references" },
      ];

      return arr;
    },

    editQuery(query) {
      console.log("QUERY: " + query.id)
      this.$router.push({ name: 'query', params: {id: query.id} })
    },

    newQuery() {
      this.$router.push({ name: 'queryNew' })
    },

  }

}; 
</script>
