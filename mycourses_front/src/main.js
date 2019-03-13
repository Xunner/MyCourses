// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import VueResource from 'vue-resource'
import VueCookies from 'vue-cookies'
import StudentNav from './components/StudentNav'
import TeacherNav from './components/TeacherNav'
import AdminNav from './components/AdminNav'

Vue.use(VueCookies)
Vue.use(VueResource)

Vue.component('StudentNav', StudentNav)
Vue.component('TeacherNav', TeacherNav)
Vue.component('AdminNav', AdminNav)

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
