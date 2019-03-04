import Vue from 'vue'
import Router from 'vue-router'
// import HelloWorld from '@/components/HelloWorld'
import LogIn from '@/views/login/login.vue'
import Home from '@/views/home/home.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(Router)
Vue.use(ElementUI)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/login',
      name: 'LogIn',
      component: LogIn
    }
  ]
})
