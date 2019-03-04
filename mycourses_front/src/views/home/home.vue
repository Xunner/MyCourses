<template>
  <el-container>
  </el-container>
</template>

<script>
/* 引入公共方法 */
import {getCookie, deleteCookie} from '../../assets/js/cookie.js'

export default {
  data () {
    return {
      name: 'home'
    }
  },
  mounted () {
    /* 页面挂载获取保存的cookie值，渲染到页面上 */
    let userName = getCookie('username')
    this.name = userName
    /* 如果cookie不存在，则跳转到登录页 */
    if (userName === '') {
      this.$router.push('/login')
    }
  },
  methods: {
    quit () {
      /* 删除cookie */
      deleteCookie('username')
    }
  }
}
</script>

<style scoped>

</style>
