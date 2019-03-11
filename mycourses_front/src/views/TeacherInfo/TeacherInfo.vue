<template>
  <el-container>
    <el-header>
      <teacher-nav :name="name"></teacher-nav>
    </el-header>
    <el-main>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'TeacherInfo',
  data () {
    return {
      name: this.$cookies.get('email')
    }
  }
}
</script>

<style scoped>

</style>
