import Vue from 'vue';
import VueRouter from 'vue-router';
import Home from '@/views/Home';
import User from '@/views/User';
import Dashboard from '@/views/Dashboard';
import Publications from '@/views/Publications';
import Purchases from '@/views/Purchases';
import Licenses from '@/views/Licenses';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home,
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: Dashboard,
  },
  {
    path: '/user',
    name: 'user',
    component: User,
  },
  {
    path: '/publications',
    name: 'publications',
    component: Publications,
  },
  {
    path: '/purchases',
    name: 'purchases',
    component: Purchases,
  },
  {
    path: '/licenses',
    name: 'licenses',
    component: Licenses,
  },

];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
});
export default router;
