<template>
   <div>
     <v-app-bar app color="primary" dark>
      <div class="d-flex align-center">
        <v-img
          alt="Vuetify Logo"
          class="shrink mr-2"
          contain
          src="https://cdn.vuetifyjs.com/images/logos/vuetify-logo-dark.png"
          transition="scale-transition"
          width="40" />
        <span class="mr-2">
          <span class="brand">Prothumia eTBN</span>
        </span>
      </div>

      <v-spacer></v-spacer>
      <v-btn href="" text>
        <v-icon class="mr-2">mdi-account</v-icon>
        <span class="mr-2">Login</span>
      </v-btn>
    </v-app-bar>
  </div>
</template>

<script>
export default {
  name: 'Header',
};
</script>
<style scoped>
.brand{
  font-size:24px;
  font-weight:500;
}
</style>
