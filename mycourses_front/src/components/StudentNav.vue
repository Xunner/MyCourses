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
  data () {
    return {
      name: '游客',
      navList: [
        {name: '/MyCourses', navItem: '我的课程'},
        {name: '/TakeClasses', navItem: '选课'},
        {name: '/MyInformation', navItem: '个人信息'}
      ],
      userInfo: [
        {name: '名字', value: '未知'},
        {name: '身份', value: '未知'}
      ]
    }
  },
  methods: {
    logout () {
      /* 退出登录 */
      this.$cookies.remove('userId')
      this.$router.push('/login')
    },
    handleCommand (command) {
      this.$message('click on item ' + command)
    }
  }
}
</script>

<style scoped>
  .user {
    float: right;
    padding-top: 16px
  }
  .item {
    margin-bottom: 10px;
  }
  .button {
    display:block;
    margin:0 auto;
  }
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }
</style>
