<template>
  <el-container>
    <el-header>
      <admin-nav :name="name"></admin-nav>
    </el-header>
    <el-main>
      <el-card class="box-card">
        <div slot="header" class="text">创建新课程审核</div>
        <el-table :data="checkNewCourse" :default-sort = "{prop: 'name', order: 'ascending'}" max-height="450px" stripe>
          <el-table-column prop="name" label="课名" sortable></el-table-column>
          <el-table-column prop="teacher" label="教师" sortable></el-table-column>
          <el-table-column prop="grade" label="年级" sortable></el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button size="mini" type="success" @click="clickPassCourse(scope.$index, scope.row)">通过</el-button>
              <el-button size="mini" type="danger" @click="clickRejectCourse(scope.$index, scope.row)">不通过</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      <el-card class="box-card">
        <div slot="header" class="text">发布新课程审核</div>
        <el-table :data="checkNewClass" :default-sort = "{prop: 'name', order: 'ascending'}" max-height="450px" stripe>
          <el-table-column prop="name" label="课名" sortable></el-table-column>
          <el-table-column prop="teacher" label="教师" sortable></el-table-column>
          <el-table-column prop="term" label="学期" sortable></el-table-column>
          <el-table-column prop="classOrder" label="班级" sortable></el-table-column>
          <el-table-column prop="startTime" label="开课时间" :formatter="startDateFormatter" sortable></el-table-column>
          <el-table-column prop="endTime" label="结课时间" :formatter="endDateFormatter" sortable></el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button size="mini" type="success" @click="clickPassClass(scope.$index, scope.row)">通过</el-button>
              <el-button size="mini" type="danger" @click="clickRejectClass(scope.$index, scope.row)">不通过</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'admin',
  mounted () {
    if (this.$cookies.isKey('userId')) {
      /* HTTP请求 */
      this.$http.get('/MyCourses/review', {'params': {'adminId': this.$cookies.get('userId')}}).then((res) => {
        if (res.data.result === 'SUCCESS') {
          this.checkNewCourse = res.data.checkNewCourse
          this.checkNewClass = res.data.checkNewClass
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
      })
    } else {
      /* 如果cookie不存在，则跳转到登录页 */
      this.$router.push('/login')
    }
  },
  data () {
    return {
      name: this.$cookies.get('email'),
      checkNewCourse: [{courseId: 1, grade: 1, name: '课程1', teacher: '教师1'},
        {courseId: 3, grade: 1, name: '课程2', teacher: '教师1'},
        {courseId: 2, grade: 3, name: '课程3', teacher: '教师2'}],
      checkNewClass: [
        {classId: 1, name: '课程1', teacher: '教师1', term: 3, classOrder: 1, startTime: [2019, 3, 1], endTime: [2019, 6, 30]},
        {classId: 2, name: '课程2', teacher: '教师2', term: 1, classOrder: 1, startTime: [2019, 3, 1], endTime: [2019, 6, 30]},
        {classId: 4, name: '课程2', teacher: '教师2', term: 1, classOrder: 2, startTime: [2019, 3, 1], endTime: [2019, 7, 7]}]
    }
  },
  methods: {
    clickPassCourse (index, row) {
      this.$http.post('/MyCourses/reviewCourse', {courseId: row.courseId, pass: true}).then(res => {
        if (res.data === 'SUCCESS') {
          this.$message.success('审批成功！')
          this.checkNewCourse.splice(index, 1)
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
      })
    },
    clickRejectCourse (index, row) {
      this.$http.post('/MyCourses/reviewCourse', {courseId: row.courseId, pass: false}).then(res => {
        if (res.data === 'SUCCESS') {
          this.$message.success('拒绝成功！')
          this.checkNewCourse.splice(index, 1)
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
      })
    },
    clickPassClass (index, row) {
      this.$http.post('/MyCourses/reviewClass', {classId: row.classId, pass: true}).then(res => {
        if (res.data === 'SUCCESS') {
          this.$message.success('审批成功！')
          this.checkNewClass.splice(index, 1)
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
      })
    },
    clickRejectClass (index, row) {
      this.$http.post('/MyCourses/reviewClass', {classId: row.classId, pass: false}).then(res => {
        if (res.data === 'SUCCESS') {
          this.$message.success('拒绝成功！')
          this.checkNewClass.splice(index, 1)
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
      })
    },
    startDateFormatter (row) {
      console.log(row.startTime)
      console.log(row.startTime.slice(0, 3).join('-'))
      return row.startTime.slice(0, 3).join('-')
    },
    endDateFormatter (row) {
      return row.endTime.slice(0, 3).join('-')
    }
  }
}
</script>

<style scoped>
  .box-card {
    width: 95%;
    margin: 15px 15px;
    float: left;
  }
  .text {
    margin: 20px 0;
    text-align: left;
  }
</style>
