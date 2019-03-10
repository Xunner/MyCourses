<template>
  <el-container>
    <el-header>
      <student-nav></student-nav>
    </el-header>
    <el-main>
    </el-main>
  </el-container>
</template>

<script>
export default {
  data () {
    return {
      name: 'tourist'
    }
  },
  mounted () {
    if (this.$cookies.isKey('userId')) {
      switch (this.$cookies.get('userType')) {
        case 'Student':
          this.$router.push('/MyClasses')
          break
        case 'Teacher':
          this.$message.error('TODO Teacher')
          break
        case 'Admin':
          this.$message.error('TODO Admin')
          break
        default:
          this.$message.error('网络错误，请刷新或稍后再试')
          console.log('未知用户类型：' + this.$cookies.get('userType'))
      }
    } else {
      /* 如果cookie不存在，则跳转到登录页 */
      this.$router.push('/login')
    }
  },
  methods: {
  }
}
</script>

<style scoped>
</style>
