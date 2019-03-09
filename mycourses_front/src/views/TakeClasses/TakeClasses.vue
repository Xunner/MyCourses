<template>
  <el-container>
    <el-header>
      <student-nav></student-nav>
    </el-header>
    <el-main>
      <el-row>
        <el-col :span="24"><div class="grid-content bg-purple-dark">TakeClasses</div></el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'TakeClasses',
  mounted () {
    // TODO
    // if (this.$cookies.isKey('userId')) {
    /* HTTP请求 */
    this.$http.get('/MyCourses/home', {
      'params': {
        'userId': this.$cookies.get('userId'),
        'userType': this.$cookies.get('userType')
      }
    }).then((res) => {
      this.$message.success('success')
    }, () => {
      this.$message.error('网络错误，请刷新或稍后再试')
    })
    // } else {
    //   /* 如果cookie不存在，则跳转到登录页 */
    //   this.$router.push('/login')
    // }
  }
}
</script>

<style scoped>

</style>
