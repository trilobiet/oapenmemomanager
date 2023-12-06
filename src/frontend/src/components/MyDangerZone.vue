<template>

  <v-card variant="outlined" color="red-darken-2">
    <v-card-item class="text-overline mb-1">
      <v-row>
        <v-col cols="6">danger zone</v-col>
        <v-col cols="6" class="text-right">
          <slot></slot>  
        </v-col>
      </v-row>
    </v-card-item> 
  </v-card>

</template>