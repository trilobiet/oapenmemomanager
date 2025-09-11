import Vuex from 'vuex';

export default new Vuex.Store({

    state: {
        user: null,    
        clientConfig: null,
    },

    mutations: {

        clearUser(state) {
            state.user = null;
        },

        setUser(state,payload) {
            state.user = payload
            console.log("SET USER: " + JSON.stringify(state.user)) 
        },

        setClientConfig(state,payload) {
            state.clientConfig = payload
            console.log("SET CLIENT CONFIG: " + JSON.stringify(state.clientConfig)) 
        },    
    },

    //actions: { },

    getters: {

        getUser(state) {
            return state.user
        },

        getClientConfig(state) {
            return state.clientConfig
        },
    },

});