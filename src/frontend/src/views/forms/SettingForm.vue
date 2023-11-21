<template>

  <v-form
    ref="settingForm"
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
              <v-text-field :disabled="isNew===false" v-model="setting.key" label="Key" :rules="isNew ? validation.key : []"/>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-text-field v-model="setting.value" label="Value" :rules="validation.value"/>
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
        insetting: {type: Object, default: null},
        title: {type: String, default: ''},
        takenKeys: {type: Array, default: ()=>[]},
    },

    data() {
      return {
        isNew: false,
        isValidForm: false,
        setting: {},
      }      
    },

    mounted() {

      this.setting = this.insetting  // copy from input -> cannot edit props locally (´unexpected mutation of prop´)

      console.log("MOUNTED")

      if (this.setting.key == null || this.setting.key.length == 0) 
      this.isNew = true;

    },

    computed: {

      validation() {

        return {

          key: [ 
            v => !!v || 'Key is required',
            v => (v && v.length >= 4) || 'Key cannot be shorter than 4 characters',
            v => (v && v.length <= 32) || 'Key cannot be longer than 32 characters',
            v => this.validateKeyFree(v) || 'Key is already in use. Choose another key.',
            v => (v && !/\s/g.test(v)) || 'Key must not contain whitespace',
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

        console.log("VALUE="+val)
        console.log("TAKEN="+this.takenKeys)

        if (this.isNew) {

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