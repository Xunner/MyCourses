<template>
  <el-container>
    <el-header>
      <student-nav :name="name"></student-nav>
    </el-header>
    <el-main>
      <el-row>
        <el-col :span="24">
          <el-card class="box-card">
            <div slot="header" class="text">选课</div>
            <el-table :data="unselectedClass" :default-sort = "{prop: 'name', order: 'ascending'}" max-height="450px" stripe>
              <el-table-column prop="name" label="课名" sortable></el-table-column>
              <el-table-column prop="teacher" label="教师" sortable></el-table-column>
              <el-table-column prop="grade" label="年级" sortable></el-table-column>
              <el-table-column prop="term" label="学期" sortable></el-table-column>
              <el-table-column prop="classOrder" label="班级" sortable></el-table-column>
              <el-table-column prop="startTime" label="开课时间" sortable></el-table-column>
              <el-table-column prop="endTime" label="结课时间" sortable></el-table-column>
              <el-table-column prop="number" label="已选/限额" sortable :formatter="numberFormatter"></el-table-column>
              <el-table-column label="操作">
                <template slot-scope="scope">
                  <el-button size="mini" type="primary" @click.native.prevent="clickSelectClass(scope.row)">选课</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-card class="box-card">
            <div slot="header" class="text">已选课程</div>
            <el-table :data="selectedClass" :default-sort = "{prop: 'name', order: 'ascending'}" max-height="450px" stripe>
              <el-table-column prop="name" label="课名" sortable></el-table-column>
              <el-table-column prop="teacher" label="教师" sortable></el-table-column>
              <el-table-column prop="grade" label="年级" sortable></el-table-column>
              <el-table-column prop="term" label="学期" sortable></el-table-column>
              <el-table-column prop="classOrder" label="班级" sortable></el-table-column>
              <el-table-column prop="startTime" label="开课时间" sortable></el-table-column>
              <el-table-column prop="endTime" label="结课时间" sortable></el-table-column>
              <el-table-column prop="number" label="已选/限额" sortable :formatter="numberFormatter"></el-table-column>
              <el-table-column label="操作">
                <template slot-scope="scope">
                  <el-button size="mini" type="primary" @click.native.prevent="clickCancelClassSelection(scope.row)">退选</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
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
    this.$http.get('/MyCourses/TakeClasses', {'params': {'studentId': this.$cookies.get('userId')}
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
  methods: {
    clickSelectClass (row) {
      this.$http.post('/MyCourses/selectClass', {
        classId: row.classId,
        studentId: this.$cookies.get('userId')
      }).then(res => {
        if (res.bodyText === 'SUCCESS') {
          this.$message.success('申请选课成功！开课时间到后会自动公布选课结果')
          row.number.current += 1
          this.selectedClass.push(row)
          for (let i = 0; i < this.unselectedClass.length; i++) {
            if (this.unselectedClass[i].classId === row.classId) {
              this.unselectedClass.splice(i, 1)
              break
            }
          }
        } else if (res.bodyText === 'FAILED') {
          this.$message.error('申请选课失败，该课程已经开课且人数已满')
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')

        // console.log(row)
        // row.number.current += 1
        // this.selectedClass.push(row)
        // for (let i = 0; i < this.unselectedClass.length; i++) {
        //   if (this.unselectedClass[i].classId === row.classId) {
        //     this.unselectedClass.splice(i, 1)
        //     break
        //   }
        // }
      })
    },
    clickCancelClassSelection (row) {
      this.$http.post('/MyCourses/cancelClassSelection', {
        classId: row.classId,
        studentId: this.$cookies.get('userId')
      }).then(res => {
        if (res.bodyText === 'SUCCESS') {
          this.$message.success('取消申请选课成功！')
          row.number.current -= 1
          this.unselectedClass.push(row)
          for (let i = 0; i < this.selectedClass.length; i++) {
            if (this.selectedClass[i].classId === row.classId) {
              this.selectedClass.splice(i, 1)
              break
            }
          }
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')

        // console.log(row)
        // row.number.current -= 1
        // this.unselectedClass.push(row)
        // for (let i = 0; i < this.selectedClass.length; i++) {
        //   if (this.selectedClass[i].classId === row.classId) {
        //     this.selectedClass.splice(i, 1)
        //     break
        //   }
        // }
      })
    },
    numberFormatter (row) {
      return row.number.current + ' / ' + row.number.max
    }
  },
  data () {
    return {
      name: this.$cookies.get('email'),
      unselectedClass: [{
        classId: 1,
        name: '课程1',
        teacher: '教师1',
        grade: 1,
        term: 1,
        classOrder: 1,
        startTime: '2019-03-01',
        endTime: '2019-06-30',
        number: {current: 88, max: 90}
      }, {
        classId: 2,
        name: '课程2',
        teacher: '教师2',
        grade: 2,
        term: 1,
        classOrder: 1,
        startTime: '2019-03-01',
        endTime: '2019-06-30',
        number: {current: 37, max: 100}
      }],
      selectedClass: [{
        classId: 3,
        name: '课程3',
        teacher: '教师3',
        grade: 2,
        term: 1,
        classOrder: 1,
        startTime: '2019-03-01',
        endTime: '2019-06-30',
        number: {current: 201, max: 250}
      }]
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
