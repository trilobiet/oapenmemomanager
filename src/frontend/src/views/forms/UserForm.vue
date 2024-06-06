<template>

  <v-form
    ref="userForm"
    v-model="isValidForm"
    validate-on="lazy input"
  >
    <v-card>

      <v-card-title class="bg-primary"> 
        <span class="text-h5">
          <v-icon>mdi-square-edit-outline</v-icon> {{ title }}
        </span>
      </v-card-title>

      <v-divider></v-divider>

      <v-card-text>

        <v-container>

          <v-row>

            <v-col cols="12" sm="6" md="4">
              <v-text-field v-model="user.username" label="Username" :rules="isNew ? validation.username : []"/>
            </v-col>

            <v-col cols="12" sm="6" md="4">
              <v-text-field v-model="user.fullname" label="Full name" :rules="validation.fullname"/>
            </v-col>

          </v-row>  

          <v-row>

            <v-col v-if="isNew" lg="4" md="6" sm="8"> 
              <v-text-field label="password" v-model="user.password" :rules="validation.password"
                  append-inner-icon="mdi-auto-fix" @click:appendInner="user.password = $func.generatePassword()"/>
            </v-col>
            <v-col v-else lg="4" md="6" sm="8">
              <v-text-field v-if="setPassword" label="password" v-model="user.password" :rules="validation.password"
                append-inner-icon="mdi-auto-fix" @click:appendInner="user.password = $func.generatePassword()" />
              <v-text-field v-else label="set password" disabled><!-- just a 'label' --></v-text-field>
            </v-col>

            <v-col cols="4">
              <v-switch  v-if="!isNew" v-model="setPassword" inset></v-switch>
              <v-checkbox label="administrator" v-model="user.admin" :class="user.admin ? 'text-green' : 'text-orange-darken-3'" />
            </v-col>  

          </v-row>  

          <v-row v-if="!isNew">
            <v-col class="px-4 text-grey-darken-1 text-caption">
              Last edited by {{ user.updatedBy }} on {{ user.updatedAt }}
            </v-col>
          </v-row>
          
        </v-container>

      </v-card-text>

      <v-divider class="border-dark"></v-divider>

      <v-card-actions class="bg-actions">

        <v-container>
          <v-row >

            <v-col>
              <span v-if="isValidForm===false" class="text-red">
                <v-icon>mdi-alert-circle-outline</v-icon>
                Please fix validation issues before saving
              </span>
            </v-col>

            <v-col class="text-right">
              <v-btn @click="cancel" text="Cancel"/>
              <v-btn @click="save" text="Save" :disabled="!isValidForm"/>
            </v-col>

          </v-row>   

        </v-container>

      </v-card-actions>

    </v-card>

  </v-form>  

</template>


<script>

  export default {

    props: {
        inuser: {type: Object, default: null},
        title: {type: String, default: ''},
        takenUsernames: {type: Array, default: ()=>[]},
    },

    data() {
      return {
        isNew: false,
        isValidForm: true,
        setPassword: false,
        user: {},
      }      
    },

    mounted() {

      this.user = this.inuser  // copy from input -> cannot edit props locally (´unexpected mutation of prop´)

      console.log("MOUNTED")

      if (this.user.id == null || this.user.id.length == 0) {
        this.isNew = true;
      }

    },

    watch: {

      setPassword: function(newVal) {
        if (newVal==false) this.user.password = ''
      },
    },  

    computed: {

      validation() {

        return {

          username: [ 
            v => !!v || 'Username is required',
            v => (v && v.length >= 4) || 'Username cannot be shorter than 4 characters',
            v => (v && v.length <= 45) || 'Username cannot be longer than 45 characters',
            v => this.validateUsernameFree(v) || 'Username is already in use. Choose another username.',
            v => (v && this.$func.isValidUserName(v)) || 'Username may only contain A-Z, a-z, 0-9 and _',
          ],
          password: [
            v => !!v || "Value is required",
            v => (v && v.length >= 8) || "Value cannot be shorter than 8 characters"
          ],
          fullname: [
            v => !!v || 'Full name is required'
          ]
        }  
      }
    },

    methods: {

      showSpace(val) {
        return val.replace(/\s/gi,"_")
      },

      cancel() {
        this.$emit('cancel')
      },

      save() {

        if(this.$refs.userForm.validate()) {
        // if (this.isValidForm) {
          //console.log("VALIDATION PASSED! " + JSON.stringify(this.setting))
          this.$emit('save')
        }  
        else {
          //console.log("VALIDATION ERRORS!") 
        }  
      },

      validateUsernameFree(val) {

        if (this.isNew) {

          if (!val) return false;
          const posInList = this.takenUsernames.indexOf(val.trim())
          if ( posInList == -1 ) return true
          else return false  
        }
        else return true 
      },

    },

  }

</script>