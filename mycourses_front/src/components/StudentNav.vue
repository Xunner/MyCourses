<template>
  <el-menu :default-active="this.$route.path" router mode="horizontal">
    <el-menu-item v-for="(item,i) in navList" :key="i" :index="item.name">
      {{ item.navItem }}
    </el-menu-item>
    <el-dropdown class="user" @command="handleCommand">
      <span class="el-dropdown-link">{{name}}<i class="el-icon-arrow-down el-icon--right"></i></span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item command="homepage">主页</el-dropdown-item>
        <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </el-menu>
</template>

<script>
export default {
  name: 'StudentNav',
  props: {
    name: String
  },
  data () {
    return {
      navList: [
        {name: '/MyClasses', navItem: '我的课程'},
        {name: '/TakeClasses', navItem: '选课'},
        {name: '/MyInformation', navItem: '个人信息'}
      ]
    }
  },
  methods: {
    handleCommand (command) {
      if (command === 'homepage') {
        this.$router.push('/')
      } else if (command === 'logout') {
        /* 退出登录 */
        this.$cookies.remove('userId')
        this.$cookies.remove('userType')
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
  .user {
    float: right;
    padding-top: 16px
  }
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }
</style>
