<template>

  <div class="users">

      <v-alert v-if="alert!==this.$alert.NONE" :type="alert" v-model="alert" closable @click:close="alert==this.$alert.NONE">
        <span v-if="alert==this.$alert.ERROR" @click="showAlertDetail">{{alertMsg}}</span>
        <span v-else>{{alertMsg}}</span>
      </v-alert>

      <v-container fluid>

      <v-row>

        <v-col>

          <v-card class="elevation-5">

            <v-toolbar color="transparent" >  

              <v-toolbar-title class="font-weight-bold">Users</v-toolbar-title>
                  
                <!-- Edit Client Dialog ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
                <v-dialog v-model="dialog" width="1024" max-width="90%" scrollable @click:outside="cancelForm">
                
                  <template v-slot:activator>
                    <v-btn class="bg-primary" @click="editUser()">New User</v-btn>
                  </template>

                  <user-form :inuser="editedUser" @cancel="cancelForm" @save="saveUser" 
                    :title="formTitle" :takenUsernames="usernames" />

                </v-dialog>

              </v-toolbar>

              <v-card-text> 

                <v-text-field
                    v-model="tableSearch"
                    append-icon="mdi-magnify"
                    label="Search" single-line
                    variant="underlined">
                </v-text-field>

                <v-data-table 
                  :sort-by="['username','fullname']" :hover="true" items-per-page="5"
                  :loading="loading" :search="tableSearch" 
                  :headers="headers" :items="users">

                  <template v-slot:[`item.username`]="{ item }">
                    <span v-if="item.editable" style="cursor:pointer" @click="editUser(item)"
                     class="text-blue-darken-4">{{item.username}}</span>
                    <span v-else>{{item.username}}</span>
                  </template> 

                  <template v-slot:[`item.admin`]="{ item }">
                    <v-icon v-if="isAdmin(item)" color="orange-darken-3">mdi-account-tie</v-icon>
                  </template> 

                  <template v-slot:[`item.actions`]="{ item }">
                    <v-hover v-if="isEditable(item)" v-slot="{ isHovering, props }">
                      <v-icon @click="confirmDeleteUser(item)" v-bind="props"
                       :color="isHovering ? 'red': 'grey-darken-2'">mdi-close-circle-outline</v-icon>
                    </v-hover>  
                  </template>

                  <template v-slot:no-data>
                    No data available. Your session may have expired.
                    <br/><a href="/login">Login again to start a new session</a>
                  </template>                  

                </v-data-table>

              </v-card-text>
          </v-card>    

        </v-col>
      </v-row>

    </v-container>

  </div>

</template>

<script>

import UserForm from '../views/forms/UserForm.vue';

export default {

  components: { UserForm },
  
  data() {
    return {
      dialog: false,
      loading: true,
      tableSearch: '',
      headers: [],
      users:[], 
      editedIndex: -1,      
      editedUser: {
        //key: '',
        //value: ''
      },
      defaultUser: {
        //key: '',
        //value: ''
      },   
      alert: this.$alert.NONE, // ERROR,INFO,SUCCESS,WARNING,NONE
      alertMsg: "",
      alertDetail: "",
    }    
  },
  
  computed: {
    // Title of form
    formTitle() {
      return this.editedIndex === -1 
        ? 'new user'  
        : this.editedUser.fullname
    },

    // get all usernames
    usernames() {
      return this.users.map(u => u.username)
    }
  },

  mounted() {
    this.loadUsers();
  },
  
  watch: {

    // auto close alert after 5 secs
    alert(new_val){ 
      if(new_val){
        setTimeout(()=>{this.alert=this.$alert.NONE},5000)
      }
    }    
  },

  methods: {

    loadUsers() {

      this.loading = true; 
      this.$axios.get(`/api/user`)
        .then(resp => {
          this.users=resp.data;
          this.headers=this.getHeaders(resp.data);
          console.log("USERS " + this.users)
        })
        .catch(error => console.log(error))
        .finally(() => this.loading = false )
    },
    
    getHeaders() {

      let arr = [
        { title: "username", key: "username" },
				{ title: "name", key: "fullname" },
        { title: "admin", key: "admin" },
        { title: 'remove', key: 'actions', sortable: false }
      ];

      return arr;
    },

    editUser(user) {
      this.editedIndex = this.users.indexOf(user)
      this.editedUser = Object.assign({}, user)
      this.dialog = true
    },

    confirmDeleteUser(user) {
      this.$root.$refs.confirm.open(
        'Delete User', 
        'Are you sure you want to delete user \'' + user.username + '\'?', 
        { color: 'orange-darken-2', width: 400 }
      ).then((confirm) => {
         if (confirm) this.deleteUser(user)
      });
    },

    deleteUser(user) {

      console.log("DELETING ==== " + user.username)

      // TODO
      this.$axios.delete(`/api/user/${user.id}`)
        .then( resp => {
          this.users.splice(this.users.indexOf(user), 1)
          console.log(resp)
          this.alert = this.$alert.SUCCESS;
          this.alertMsg = "User deleted";
        })
        .catch( err => {
          this.alert = this.$alert.ERROR;
          this.alertMsg = err.message;
          this.alertDetail = err
        })
        .finally(() => {
          this.setDefault();
        })

      this.dialogDelete = false
    },

    cancelForm () {
      this.dialog = false
      this.setDefault();   
      //console.log("Dialog canceled")   
    },

    saveUser () {

      // Content-type: application/json
      this.$axios.post(`/api/user`, this.editedUser)
        .then( resp => {
          console.log(resp)
          if (this.editedIndex > -1) {
            Object.assign(this.users[this.editedIndex], this.editedUser)
          }  
          else {
            this.users.push(this.editedUser)
            this.editedUser.id = resp.data.id
          }  
          this.alert = this.$alert.SUCCESS;
          this.alertMsg = "User saved";
          this.loadUsers()
        })
        .catch( err => {
          this.alert = this.$alert.ERROR;
          this.alertMsg = err.message;
          this.alertDetail = err
        })
        .finally(() => {
          this.setDefault();
          console.log("Ready.") 
        })

      this.dialog = false
    },    

    setDefault() {
      this.editedUser = Object.assign({}, this.defaultUser)
      this.editedIndex = -1
    },

    showAlertDetail() {
      alert(JSON.stringify(this.alertDetail))
    },

    isAdmin(user) {
      return user.admin == true
    },

    isEditable(user) {
      return user.editable == true
    }



  }

}; 
</script>
