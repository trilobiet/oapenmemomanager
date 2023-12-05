import Vuex from 'vuex';

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

    //actions: { },

    getters: {

        getUser(state) {
            return state.user
        },
    },

});