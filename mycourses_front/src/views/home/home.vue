<template>
  <el-container>
    <el-header>
      <el-menu :default-active="activeIndex" class="" mode="horizontal" @select="handleSelect">

      </el-menu>
    </el-header>
  </el-container>
</template>

<script>
export default {
  data () {
    return {
      name: 'home'
    }
  },
  mounted () {
    /* 页面挂载获取保存的cookie值，渲染到页面上 */
    if (this.$cookies.isKey('email')) {
      this.name = this.$cookies.get('email')
    } else {
      /* 如果cookie不存在，则跳转到登录页 */
      this.$router.push('/login')
    }
  },
  methods: {
    logout () {
      /* 退出登录 */
      this.$cookies.remove('username')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>

</style>
