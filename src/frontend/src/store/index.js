

// CURRENTLY NOT USED!

import Vue from 'vue';
import Vuex from 'vuex';
import createPersistedState from 'vuex-persistedstate';

// CURRENTLY NOT USED!

Vue.use(Vuex);

export default new Vuex.Store({

    state: {
        user: null,    
    },
    mutations: {

        clearUser(state) {
            state.user = null;
        },

        setUser(state,payload) {
            state.user = payload
            console.log("SET USER: " + JSON.stringify(state.user)) 
        },
    },
    actions: {
    },
    getters: {

        getUser(state) {
            return state.user
        },
    },

    plugins: [createPersistedState()]

});