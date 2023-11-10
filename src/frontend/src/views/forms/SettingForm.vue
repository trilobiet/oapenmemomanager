<template>

  <v-form
    ref="settingForm"
    v-model="isValidForm"
    lazy-validation
  >
    <v-card>

      <v-card-title class="white--text" :style="'background-color:'+headerColor"> 
        <span class="text-h5">
          <v-icon dark large>{{headerIcon}}</v-icon> {{ title }}
        </span>
      </v-card-title>

      <v-divider></v-divider>

      <v-card-text>

        <v-container>

          <v-row>
            <v-col cols="12" sm="6" md="4">
              <v-text-field v-model="setting.key" label="Key" :rules="validation.key"
               :disabled="!isNewSetting"></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-select v-model="setting.value" :items="roles" label="Value" :rules="validation.value" />
            </v-col>
          </v-row>  

        </v-container>

      </v-card-text>

      <v-divider></v-divider>

      <v-card-actions class="blue-grey lighten-5" >
        <v-container>
          <v-row >
            <v-col>
              <span class="text-left red--text caption" v-if="!isValidForm">
                <v-icon color="red" >mdi-alert-circle-outline</v-icon>
                Please fix validation issues before saving
              </span>
            </v-col>
            <v-col class="text-right">
                <v-btn color="blue darken-3" text @click="cancel">Cancel</v-btn>
                <v-btn color="blue darken-3" text @click="save" :disabled="!isValidForm">Save</v-btn>
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
  //      isOpen: {type: Boolean, default: null},
        insetting: {type: Object, default: null},
  //      title: {type: String, default: ''},
        takenKeys: {type: Array, default: ()=>[]},
        headerColor: {type: String, default: 'gray'},
        headerIcon: {type: String, default: 'mdi-account-edit'},
    },

    data() {
      return {
        isValidForm: false,
        //editedSetting: "",
        setting: this.insetting  // cannot edit props locally (unexpected mutation of prop)
      }      
    },

    watch: {
      isOpen: function(newVal,oldVal) {
        //if (newVal==true) {
          this.$refs.settingForm.resetValidation()
        //}
        console.log('Prop changed: ', newVal, ' | was: ', oldVal)
        console.log(JSON.stringify(this.setting))
        this.editedSetting = this.setting.key
      },
    },  

    computed: {

      /*setting: {
         get: function() { return this.insetting}, 
         set: function(value) {this.$emit(value)}
      },*/


      isNewSetting() {
         return this.setting.key == null || this.setting.key.length == 0
      },

      validation() {

        return {

          key: [
            v => !!v || 'Key is required',
            v => (v && v.length >= 4) || 'Key cannot be shorter than 4 characters',
            v => (v && v.length <= 32) || 'Key cannot be longer than 32 characters',
            v => (v && !/\s/g.test(v)) || 'Key must not contain whitespace',
            v => this.validateKeyFree(v) || 'Key is already in use. Choose another key.'
          ],
          value: [
            v => !!v || 'Value is required'
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

        if(this.$refs.settingForm.validate()) {
        // if (this.isValidForm) {
          console.log("VALIDATION PASSED! " + JSON.stringify(this.setting))
          this.$emit('save')
        }  
        else {
          console.log("VALIDATION ERRORS!") 
        }  
      },

      validateKeyFree(val) {

        if (this.isNewSetting) {

          if (!val) return false;
          const posInList = this.takenKeys.indexOf(val.trim())
          if ( posInList == -1 ) return true
          else return false  
        }
        else return true // existing keys cannot be renamed
      },

    },

  }

</script>