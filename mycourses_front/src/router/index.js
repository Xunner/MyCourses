import Vue from 'vue'
import Router from 'vue-router'
// import HelloWorld from '@/components/HelloWorld'
import LogIn from '@/views/login/login.vue'
import Home from '@/views/home/home.vue'
import MyClasses from '@/views/MyClasses/MyClasses.vue'
import TakeClasses from '@/views/TakeClasses/TakeClasses.vue'
import MyInformation from '@/views/MyInformation/MyInformation.vue'
import TeacherCourses from '@/views/TeacherCourses/TeacherCourses.vue'
import TeacherInfo from '@/views/TeacherInfo/TeacherInfo.vue'
import Review from '@/views/review/review.vue'
import Statistics from '@/views/statistics/statistics.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(Router)
Vue.use(ElementUI)

export default new Router({
  mode: 'hash',
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
      path: '/MyClasses',
      name: 'MyClasses',
      component: MyClasses
    }, {
      path: '/TakeClasses',
      name: 'TakeClasses',
      component: TakeClasses
    }, {
      path: '/MyInformation',
      name: 'MyInformation',
      component: MyInformation
    }, {
      path: '/TeacherCourses',
      name: 'TeacherCourses',
      component: TeacherCourses
    }, {
      path: '/TeacherInfo',
      name: 'TeacherInfo',
      component: TeacherInfo
    }, {
      path: '/review',
      name: 'Review',
      component: Review
    }, {
      path: '/statistics',
      name: 'Statistics',
      component: Statistics
    }
  ]
})
