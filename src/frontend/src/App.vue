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

