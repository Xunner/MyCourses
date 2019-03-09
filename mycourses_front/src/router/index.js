import Vue from 'vue'
import Router from 'vue-router'
// import HelloWorld from '@/components/HelloWorld'
import LogIn from '@/views/login/login.vue'
import Home from '@/views/home/home.vue'
import MyCourses from '@/views/MyCourses/MyCourses.vue'
import TakeClasses from '@/views/TakeClasses/TakeClasses.vue'
import MyInformation from '@/views/MyInformation/MyInformation.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(Router)
Vue.use(ElementUI)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    }, {
      path: '/login',
      name: 'LogIn',
      component: LogIn
    }, {
      path: '/MyCourses',
      name: 'MyCourses',
      component: MyCourses
    }, {
      path: '/TakeClasses',
      name: 'TakeClasses',
      component: TakeClasses
    }, {
      path: '/MyInformation',
      name: 'MyInformation',
      component: MyInformation
    }
  ]
})
