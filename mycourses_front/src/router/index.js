import Vue from 'vue'
import Router from 'vue-router'
// import HelloWorld from '@/components/HelloWorld'
import LogIn from '@/views/login.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: LogIn
    },
    {
      path: '/login',
      name: 'LogIn',
      component: LogIn
    }
  ]
})
