<template>
  <v-app id="main" :style="{ background: $vuetify.theme.themes[theme].background }">

    <v-app-bar app color="#2c84bf" dark class="px-4">

      <div class="d-flex align-center">
        <v-img alt="Vuetify Logo" class="shrink mr-2" contain src="./assets/oapenlogo-white.png"
          transition="scale-transition" width="70" />
      </div>

      <v-spacer></v-spacer>

      <v-menu>

        <template v-slot:activator="{ props }">
          <v-btn icon="mdi-dots-vertical" v-bind="props"></v-btn>
        </template>

        <v-list>
          <v-list-item :to="{ name: 'home' }">
            <v-list-item-title>Home</v-list-item-title>
          </v-list-item>
          <v-list-item :to="{ name: 'queries' }">
            <v-list-item-title>Queries</v-list-item-title>
          </v-list-item>
          <v-list-item :to="{ name: 'settings' }">
            <v-list-item-title>Settings</v-list-item-title>
          </v-list-item>

          <v-divider></v-divider>

          <v-list-item v-if="isLoggedIn" href="/logout?logout">
            <!-- points to spring template! (so uses 'href' i.o 'to') -->
            <v-list-item-title>Log out</v-list-item-title>
          </v-list-item>

        </v-list>

      </v-menu>

    </v-app-bar>

    <v-main>

      <!-- Render current view -->
      <router-view />

    </v-main>

    <site-footer />

  </v-app>
</template>


<script>

import SiteFooter from '@/components/Footer.vue';


export default {
  components: { SiteFooter },
  name: 'App',
  data: () => ({ drawer: null }),
  computed: {
    theme() {
      return (this.$vuetify.theme.dark) ? 'dark' : 'light'
    },
    /*isLoggedIn(){ TODO
      return this.$store.getters.getUser 
    },
    user(){
      return this.$store.getters.getUser 
    }*/
  }
};
</script>


<style lang="scss">

  // font Roboto
  @import '../node_modules/@fontsource/roboto/400.css';
  @import '../node_modules/@fontsource/roboto/500.css';
  @import '../node_modules/@fontsource/roboto/700.css';

  .bg-actions {
    .v-btn { 
      color: #1565c0; 
    }  
  }

  // application wide v-data-table styling
  .v-data-table {

    th {
      background: #f4f2f6;
      white-space: nowrap;
      max-width: 25em;
      //overflow: hidden;
      border-bottom: none !important;

      span {
        text-overflow: ellipsis;
        font-weight: bold;
        font-size: 90%;
      }
    }

    td {
      @media (min-width:600px) {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;

        &.td-title {
          max-width: 25em;
        }
      }
    }

    .v-data-table-footer {
      padding: 5px;
      background: #f8f4fb;
    }

    .v-field {
      border-radius: 0;
    }

  }
  
</style>
