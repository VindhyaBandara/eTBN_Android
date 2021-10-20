<template>
  <v-app>
    <Header/>
    <v-main class="main-container">
      <v-container class="container">
        <div class="row">
          <div class="col col-2">
            <div class="menu">
              <LeftMenu/>
            </div>
          </div>
          <div class="col">
            <router-view></router-view>
          </div>
        </div>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import Header from './components/Header';
import LeftMenu from './components/LeftMenu';


export default {
  name: 'App',

  components: {
    Header, LeftMenu,
  },

  data: () => ({
    //
  }),
};
</script>
<style scoped>
.main-container{
  background-color: #eee!important;
  border-color: #eee!important;
  padding-top:20px!important;
}
.container{
  padding-top:40px!important;
}
</style>
