<template>
  <el-container>
    <el-header>
      <student-nav></student-nav>
    </el-header>
    <el-main>
      <el-card v-for="(myClass, i) of myClasses" :key="i" class="box-card">
        <div slot="header">
          <span>{{myClass.name}}</span>
        </div>
        <div v-for="(homework, i2) in myClass.homework" :key="i2" class="text"><i class="el-icon-edit-outline"></i>
          <span class="item">尚未提交的作业：{{homework.name}}</span>
          <span class="item">&nbsp;&nbsp;&nbsp;&nbsp;截止日期：{{homework.deadline}}</span>
        </div>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'MyCourses',
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
  },
  data () {
    return {
      myClasses: [
        {
          name: '课程1',
          homework: [{name: '作业1', deadline: '2019-03-08 23:59:59'}, {name: '作业2', deadline: '2019-03-09 23:59:59'}]
        },
        {name: '课程2'},
        {name: '课程3'}
      ]
    }
  }
}
</script>

<style scoped>
  .box-card {
    width: 47%;
    margin: 15px 15px;
    float: left;
  }
  .item {
    margin-bottom: 16px;
  }
  .text {
    margin: 20px 0;
    text-align: left;
  }
</style>
