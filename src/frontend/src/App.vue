<template>
  <v-app id="main" :style="{ background: $vuetify.theme.themes[theme].background }">

    <v-app-bar app color="#2c84bf" dark class="px-4">

      <div class="d-flex align-center">
        <v-img alt="Vuetify Logo" class="shrink mr-2" contain src="./assets/oapenlogo-white.png"
          transition="scale-transition" width="70" />
      </div>

      <h3 style="margin-left:1em;opacity:.85; cursor:pointer" 
        @click="this.$router.push({ name: 'home' })">MEMO Manager</h3>

      <v-spacer></v-spacer>

      <v-chip v-if="user" class="text-caption">
        {{ user.username }}
      </v-chip>

      <v-menu>

        <template v-slot:activator="{ props }">
          <v-btn icon="mdi-dots-vertical" v-bind="props"></v-btn>
        </template>

        <v-list>
          <v-list-item :to="{ name: 'home' }">
            <v-list-item-title>Home</v-list-item-title>
          </v-list-item>
          <v-list-item :to="{ name: 'library' }">
            <v-list-item-title>Library</v-list-item-title>
          </v-list-item>
          <v-list-item :to="{ name: 'alllogs' }">
            <v-list-item-title>Run Logs</v-list-item-title>
          </v-list-item>
          <v-list-item :to="{ name: 'settings' }">
            <v-list-item-title>Settings</v-list-item-title>
          </v-list-item>

          <v-divider></v-divider>

          <v-list-item v-if="user" href="/logout?logout">
            <!-- points to spring template! (so uses 'href' i.o 'to') -->
            <v-list-item-title>Log out</v-list-item-title>
          </v-list-item>

        </v-list>

      </v-menu>

    </v-app-bar>

    <v-main>

      <!-- Confirm Dialog -->
      <confirm ref="confirm"></confirm>

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
    user(){
      return this.$store.getters.getUser 
    }
  }
};
</script>


<style lang="scss">

  // font Roboto
  @import '../node_modules/@fontsource/roboto/400.css';
  @import '../node_modules/@fontsource/roboto/500.css';
  @import '../node_modules/@fontsource/roboto/700.css';

  .bg-actions {
    padding: 20px 0 !important;
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

    .v-data-table__td {
      overflow: hidden;
      white-space: wrap;
    }

  }

  #oapen-query-preview,
  #oapen-script-preview {
    position: relative;
    white-space: pre;
    overflow: auto;
    overflow-x: hidden;
    text-overflow: ellipsis;
    font-family: monospace;
    font-size: 85%;
    background: #fff;
    padding: 0 17px 17px;
    color: #666666;
    border-bottom: dotted 2px #fff;
    background: #555;
    color: #eee;
    height: 20em;

    &::before {
      position: sticky;
      content: 'preview';
      display: block;
      top: 0; 
      margin-left: -17px; 
      margin-right: -17px;
      margin-bottom: 1em;
      background: #F8F8FA;
      padding: 2px 17px;
      border-radius: 0px;
      color: #666;
      font-family: "Roboto", sans-serif;
      line-height: 1.8;
    }
  }

  .oapen-readonly-name input {
    cursor: pointer !important;
  }

  .ace_editor {
    font-size: 13px;
    line-height: 1.4;
    height: 100%;
  }

  a[target='memoweb'] {
    text-decoration: none;
    margin-right: 5px;
    vertical-align: inherit;
  }

  .oapen-script-refs-table .v-table__wrapper{
      overflow-x: hidden;

      code pre {
        display:block;
        padding: 10px;
        font-size:80%;
      }
  }




</style>

