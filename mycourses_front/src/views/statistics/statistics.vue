<template>
  <el-container>
    <el-header>
      <admin-nav :name="name"></admin-nav>
    </el-header>
  </el-container>
</template>

<script>
export default {
  name: 'statistics',
  mounted () {
    // if (this.$cookies.isKey('userId')) {
    /* HTTP请求 */
    this.$http.get('/MyCourses/statistics', {'params': {'adminId': this.$cookies.get('userId')}}).then((res) => {
      if (res.data.result === 'SUCCESS') {
        // TODO
      } else {
        this.$message.error('网络错误，请刷新或稍后再试')
      }
    }, () => {
      this.$message.error('网络错误，请刷新或稍后再试')
    })
    // } else {
    //   /* 如果cookie不存在，则跳转到登录页 */
    //   this.$router.push('/login')
    // }
  },
  data () {
    return {
      name: this.$cookies.get('email')
    }
  },
  methods: {
  }
}
</script>

<style scoped>

</style>
