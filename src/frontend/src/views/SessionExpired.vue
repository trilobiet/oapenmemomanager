<template>

  <div class="home">

    <v-container fluid>

      <v-card class="elevation-5">

        <v-toolbar>  

          <v-toolbar-title class="font-weight-bold">
            <v-icon>mdi-account-group</v-icon> Session expired
          </v-toolbar-title>

        </v-toolbar>

        <v-card-text> 

            session expired

        </v-card-text>

      </v-card>    

    </v-container>

    <!-- DELETE Dialog ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <v-dialog v-model="dialogDelete" max-width="500px">

      <v-card>
        <v-card-title class="text-h5">Are you sure you want to delete this user?</v-card-title>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="cancelDelete">Cancel</v-btn>
          <v-btn color="blue darken-1" text @click="confirmDelete">OK</v-btn>
          <v-spacer></v-spacer>
        </v-card-actions>
      </v-card>

    </v-dialog>

  </div>

</template>

<script>

export default {
 
  data() {
    return {
    }    
  },

}; 
</script>
