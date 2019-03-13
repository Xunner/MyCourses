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
