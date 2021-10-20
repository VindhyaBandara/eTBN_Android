<template>
  <v-card
    width="300"
    class="mx-auto"
  >
    <v-navigation-drawer permanent>
      <!--<v-list-item>
        <v-list-item-content>
          <v-list-item-title class="title">
            Application
          </v-list-item-title>
          <v-list-item-subtitle>
            subtext
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>-->

      <v-divider></v-divider>

      <v-list
        dense
        nav
      >
        <v-list-item
          v-for="item in items"
          :key="item.title"
          link
        >
          <v-list-item-icon>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-item-icon>

          <v-list-item-content @click="menuActionClick(item.url)">
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
  </v-card>
</template>

<script>
export default {
  name: 'LeftMenu',
  methods: {
    menuActionClick(action) {
      this.$router.push(action);
    },
  },
  data() {
    return {
      items: [
        { title: 'Dashboard', icon: 'mdi-view-dashboard', url: 'dashboard' },
        { title: 'Users', icon: 'mdi-account-multiple', url: 'user' },
        { title: 'Publications', icon: 'mdi-book', url: 'publications' },
        { title: 'Purchases', icon: 'mdi-cart-plus', url: 'purchases' },
        { title: 'Licenses', icon: 'mdi-license', url: 'licenses' },
      ],
      right: null,
    };
  },
};
</script>
<style scoped>
.v-list-item__icon{
  margin-right:10px!important;
}
</style>
