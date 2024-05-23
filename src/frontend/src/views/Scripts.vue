<template>
  
  <div class="scripts">

    <v-container fluid>

      <v-card class="elevation-5">

        <v-toolbar>  

          <v-toolbar-title class="font-weight-bold">
            <v-icon>mdi-language-python</v-icon> Script Library
          </v-toolbar-title>

          <v-btn variant="tonal" class="bg-primary" @click="newScript()">New Script</v-btn>

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
            :headers="headers" :items="scripts"   
            calculate-widths>

            <template v-slot:[`item.name`]="{ item }">
              <span style="cursor:pointer" @click="editScript(item)"
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
      scripts:[], 
    }    
  },
  
  computed: {

  },

  mounted() {
    this.loadScripts();
  },
  
  methods: {

    loadScripts() {

      this.loading = true; 
      this.$axios.get(`/api/script/snippets`)
      .then(resp => {
         this.scripts=resp.data;
         console.log(resp.data);
         this.headers=this.getHeaders(resp.data);
      })
      .catch(error => console.log(error))
      .finally(() => this.loading = false )
    },
    
    getHeaders() {

      let arr = [
        { title: "scripts", key: "name" },
				{ title: "# refering scripts", key: "references" },
      ];

      return arr;
    },

    editScript(script) {
      console.log("SCRIPT: " + script.id)
      this.$router.push({ name: 'script', params: {id: script.id} })
    },

    newScript() {
      this.$router.push({ name: 'scriptNew' })
    },

  }

}; 
</script>
