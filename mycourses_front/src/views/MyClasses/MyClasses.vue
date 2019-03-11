<template>
  <el-container>
    <el-header>
      <student-nav :name="name"></student-nav>
    </el-header>
    <el-main>
      <!--所有选课-->
      <el-card v-for="(myClass, i) of myClasses" :key="i" class="box-card" v-show="isProfile">
        <div slot="header">
          <el-button type="text" @click="clickClass(myClass.classId)">{{myClass.name}}</el-button>
        </div>
        <div v-for="(homework, i2) in myClass.homework" :key="i2" class="text"><i class="el-icon-edit-outline"></i>
          <span class="item">尚未提交的作业：{{homework.name}}</span>
          <span class="item">&nbsp;&nbsp;&nbsp;&nbsp;截止日期：{{homework.deadline}}</span>
        </div>
      </el-card>
      <!--某一选课详情-->
      <div v-show="!isProfile">
        <el-row>
          <el-col :span="12">
            <el-card class="class-info-card">
              <div slot="header" style="text-align: left">{{classInfo.name}}</div>
              <div class="left">任课教师：{{classInfo.teacherName}}</div>
              <div class="left">时期：大学{{classInfo.grade}}年级第{{classInfo.term}}学期</div>
              <div class="left">班级：{{classInfo.classOrder}}班</div>
              <div class="left">开课时间：{{classInfo.startTime}}</div>
              <div class="left">结课时间：{{classInfo.endTime}}</div>
            </el-card>
            <el-card class="class-info-card">
              <div slot="header" style="text-align: left">课件</div>
              <div class="left" v-for="(courseware, i) of classInfo.coursewares" :key="i">
                <el-button type="text" @click="clickCourseware(courseware.id)">{{courseware.name}}</el-button>
              </div>
            </el-card>
            <el-button @click="toProfile">返回</el-button>
            <el-button @click="quitClass(classInfo.id)">退课</el-button>
          </el-col>
          <el-col :span="12">
            <el-card class="class-info-card">
              <div slot="header" style="text-align: left">作业</div>
              <el-collapse v-model="activeHomeworkId" accordion>
                <el-collapse-item v-for="(homework, i) of classInfo.homework" :key="i" :name="homework.id">
                  <template slot="title">
                    {{homework.name}}&nbsp;&nbsp;&nbsp;
                    <i v-if="!homework.submitted" class="el-icon-warning">未提交</i>
                    <i v-else class="el-icon-check">已提交</i>
                  </template>
                  <div class="left">{{homework.description}}</div>
                  <div class="left">截止日期：{{homework.deadline}}</div>
                  <el-upload :data="userId" action="/MyCourses/submitHomework" :before-upload="beforeHomeworkUpload"
                             :on-success="uploadHomeworkSuccess" v-show="!homework.submitted" style="margin-top: 15px" drag>
                    <i class="el-icon-upload"></i>
                    <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                    <div class="el-upload__tip" slot="tip">
                      只能上传{{homework.typeRestriction}}文件，且不超过{{homework.sizeLimit}}MB
                    </div>
                  </el-upload>
                </el-collapse-item>
              </el-collapse>
            </el-card>
            <el-card class="class-info-card">
              <div slot="header" style="text-align: left">论坛</div>
              <el-collapse v-model="activePostId">
                <el-collapse-item v-for="(post, i) of classInfo.posts" :key="i" :name="post.id">
                  <template slot="title">{{post.title}}&nbsp;&nbsp;&nbsp;{{post.time}}</template>
                  <div class="left">由 {{post.poster}} 发表</div>
                  <div class="left">{{post.text}}</div>
                  <el-card v-for="(reply, i2) of post.replies" :key="i2" class="class-info-card">
                    <div class="left">由 {{reply.replies}} 发表于 {{reply.time}}</div>
                    <div class="left">{{reply.text}}</div>
                  </el-card>
                </el-collapse-item>
              </el-collapse>
              <el-form class="post-wrap" :model="postForm" :rules="postRules" ref="postForm">
                <h3>发布新帖子</h3>
                <el-form-item prop="title">
                  <el-input class="input" type="text" placeholder="标题" v-model="postForm.title"></el-input>
                </el-form-item>
                <el-form-item prop="text">
                  <el-input type="textarea" :autosize="{minRows: 4}" placeholder="请输入正文" v-model="postForm.text"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button class="button" type="primary" @click="addPost('postForm')">发帖</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'MyClasses',
  mounted () {
    // if (this.$cookies.isKey('userId')) {
    /* HTTP请求 */
    this.$http.get('/MyCourses/MyClasses', {'params': {'userId': this.$cookies.get('userId')}
    }).then((res) => {
      if (res.data.result === 'SUCCESS') {
        this.myClasses = res.data.myClasses
      } else {
        this.$message.error('网络错误，请刷新或稍后再试')
      }
    }, () => {
      this.$message.error('网络错误，请刷新或稍后再试')
    })
    // } else {
    //   /* 如果cookie不存在，则跳转到登录页 */
    //   this.$router.push('/login')
    // }
  },
  methods: {
    clickClass (classId) {
      this.$http.get('/MyCourses/class', {'params': {userId: this.userId, classId: classId}}).then(res => {
        console.log(res.data)
        if (res.data.result === 'SUCCESS') {
          this.classInfo = res.data.classInfo
          this.isProfile = false
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
      })
    },
    toProfile () {
      this.isProfile = true
    },
    clickCourseware (coursewareId) {
      this.$http.post('/MyCourses/downloadCourseware', {coursewareId: coursewareId}).then(res => {
        if (res.data.result === 'SUCCESS') {
          this.$message.success('成功！')
          // TODO
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
      })
    },
    quitClass (classId) {
      this.$confirm('是否确定退选该课程?', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post('/MyCourses/quitClass', {classId: classId, studentId: this.$cookies.get('userId')}).then(res => {
          if (res.data.bodyText === 'SUCCESS') {
            this.$message.success('退课成功！')
            this.isProfile = true
          } else {
            this.$message.error('网络错误，请刷新或稍后再试')
          }
        }, () => {
          this.$message.error('网络错误，请刷新或稍后再试')
        })
      })
    },
    beforeHomeworkUpload (file) {
      for (let item of this.posts) {
        if (item.id === this.activeHomeworkId) {
          const isValidType = file.type === item.typeRestriction
          const isValidSize = file.size / 1024 / 1024 < item.sizeLimit
          if (!isValidType) {
            this.$message.error('上传文件类型只能为' + item.typeRestriction + '!')
          }
          if (!isValidSize) {
            this.$message.error('上传文件大小不能超过' + item.sizeLimit + 'MB!')
          }
          return isValidType && isValidSize
        }
      }
    },
    addPost (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const data = {userId: this.userId, title: this.postForm.title, text: this.postForm.text}
          /* HTTP请求 */
          this.$http.post('/MyCourses/addPost', data).then((res) => {
            console.log(res.data)
            if (res.data.result === 'SUCCESS') {
              this.classInfo.posts.push(res.data.post)
              this.$message.success('发帖成功')
            } else {
              this.$message.error('网络错误，请刷新或稍后再试')
              console.log('未知错误：' + res.data.result)
            }
          }, () => {
            this.$message.error('网络错误，请刷新或稍后再试')
          })
        } else {
          return false
        }
      })
    },
    uploadHomeworkSuccess (res) {
      console.log(res.data)
      if (res.data.result === 'SUCCESS') {
        // this.classInfo.h.push(res.data.courseware)
        this.$message.success('上传作业成功！')
      } else {
        this.$message.error('网络错误，请刷新或稍后再试')
      }
    }
  },
  data () {
    return {
      name: this.$cookies.get('email'),
      userId: this.$cookies.get('userId'),
      isProfile: false,
      activeHomeworkId: '',
      activePostId: '',
      postForm: {title: '', text: ''},
      postRules: {
        title: [{required: true, message: '请输入标题', trigger: 'blur'}],
        text: [{required: true, message: '请输入正文', trigger: 'blur'}]
      },
      myClasses: [
        {
          classId: '1',
          name: '课程1',
          homework: [{name: '作业1', deadline: '2019-03-08 23:59:59'}, {name: '作业2', deadline: '2019-03-09 23:59:59'}]
        },
        {classId: '2', name: '课程2', homework: []},
        {classId: '3', name: '课程3'}
      ],
      classInfo: {
        id: 1,
        name: '课程1',
        teacherName: '教师1',
        grade: 1,
        term: 1,
        classOrder: 1,
        startTime: '2019-03-1',
        endTime: '2019-06-30',
        coursewares: [{id: 1, name: '课件1'}, {id: 2, name: '课件2'}],
        homework: [{
          id: 1,
          name: '作业1',
          description: '一段描述',
          deadline: '2019-03-9',
          submitted: true,
          sizeLimit: 2,
          typeRestriction: 'doc/docx'
        }, {
          id: 2,
          name: '作业2',
          description: '一段描述',
          deadline: '2019-03-10',
          submitted: false,
          sizeLimit: 2,
          typeRestriction: 'doc/docx'
        }],
        posts: [{
          id: 1,
          poster: '发帖者1',
          title: '标题1',
          text: '正文',
          time: '2019-03-9',
          replies: [{id: 1, replies: '回帖者1', text: '正文', time: '2019-03-9'}, {
            id: 2,
            replies: '回帖者2',
            text: '正文',
            time: '2019-03-10'
          }]
        }, {
          id: 2,
          poster: '发帖者2',
          title: '标题2',
          text: '正文',
          time: '2019-03-10',
          replies: [{id: 1, replies: '回帖者1', text: '正文', time: '2019-03-10'}]
        }]
      }
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
  .class-info-card {
    width: 95%;
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
  .left {
    text-align: left;
  }
  .post-wrap {
    text-align: center;
    margin-top: 40px;
  }
</style>
