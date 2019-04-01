<template>
  <el-container>
    <el-header>
      <student-nav :name="name"></student-nav>
    </el-header>
    <el-main>
      <!--所有选课-->
      <div v-if="myClasses.length === 0">您还没有选上任何课程，可以前往“选课”页面进行选课</div>
      <el-card v-for="(myClass, i) of myClasses" :key="i" class="box-card" v-show="isProfile">
        <div slot="header">
          <el-button type="text" @click="clickClass(myClass.classId)">{{myClass.name}}</el-button>
        </div>
        <div v-for="(homework, i2) in myClass.homework" :key="i2" class="text"><i class="el-icon-edit-outline"></i>
          <span class="item">尚未提交的作业：{{homework.name}}</span>
          <span class="item">&nbsp;&nbsp;&nbsp;&nbsp;截止日期：{{dateTime(homework.deadline)}}</span>
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
              <div class="left">开课时间：{{classInfo.startTime[0]}}年{{classInfo.startTime[1]}}月{{classInfo.startTime[2]}}日</div>
              <div class="left">结课时间：{{classInfo.endTime[0]}}年{{classInfo.endTime[1]}}月{{classInfo.endTime[2]}}日</div>
              <div class="left">学生人数：{{classInfo.number}}</div>
            </el-card>
            <el-card class="class-info-card">
              <div slot="header" style="text-align: left">论坛</div>
              <el-collapse v-model="activePostId">
                <el-collapse-item v-for="(post, i) of classInfo.posts" :key="i" :name="post.id">
                  <template slot="title">标题：{{post.title}}&nbsp;&nbsp;&nbsp;{{dateTime(post.time)}}</template>
                  <el-card style="margin: 5px 5px">
                    <el-button style="float: right; padding: 2px 0" type="text" icon="el-icon-plus"
                               @click="newReplyDialogVisible = true
                                  replyForm.postId = post.id">回帖
                    </el-button>
                    <div class="left">由 {{post.poster}} 发帖：</div>
                    <div class="left">{{post.text}}</div>
                  </el-card>
                  <el-card v-for="(reply, i2) of post.replies" :key="i2" class="class-info-card">
                    <div class="left">由 {{reply.replies}} 于 {{dateTime(reply.time)}} 回帖：</div>
                    <div class="left">{{reply.text}}</div>
                  </el-card>
                </el-collapse-item>
              </el-collapse>
              <el-button style="margin-top: 16px" @click="newPostDialogVisible = true">发布新帖子</el-button>
              <!--弹窗：发布新帖子-->
              <el-dialog title="新的帖子" :visible.sync="newPostDialogVisible" width="40%" center>
                <el-form class="post-wrap" :model="postForm" :rules="postRules" ref="postForm">
                  <el-form-item prop="title">
                    <el-input class="input" type="text" placeholder="标题" v-model="postForm.title"></el-input>
                  </el-form-item>
                  <el-form-item prop="text">
                    <el-input type="textarea" :autosize="{minRows: 4}" placeholder="请输入正文" v-model="postForm.text"></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button class="button" @click="newPostDialogVisible = false">取消</el-button>
                    <el-button class="button" type="primary" @click="addPost('postForm', classInfo.id)">发帖</el-button>
                  </el-form-item>
                </el-form>
              </el-dialog>
              <!--弹窗：发布回帖-->
              <el-dialog title="新的回帖" :visible.sync="newReplyDialogVisible" width="40%" center>
                <el-form class="post-wrap" :model="replyForm" ref="replyForm">
                  <el-form-item prop="text" :rules="{required: true, message: '回复内容不能为空', trigger: 'blur'}">
                    <el-input type="textarea" :autosize="{minRows: 4}" placeholder="请输入回复内容" v-model="replyForm.text"></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button class="button" @click="newReplyDialogVisible = false">取消</el-button>
                    <el-button class="button" type="primary" @click="addReply('replyForm')">回帖</el-button>
                  </el-form-item>
                </el-form>
              </el-dialog>
            </el-card>
            <el-button @click="toProfile">返回</el-button>
            <el-button @click="quitClass(classInfo.id)">退课</el-button>
          </el-col>
          <el-col :span="12">
            <el-card class="class-info-card">
              <div slot="header" style="text-align: left">课件</div>
              <div class="left" v-for="(courseware, i) of classInfo.coursewares" :key="i">
                <el-button type="text" @click="clickCourseware(courseware.id, courseware.name)">{{courseware.name}}</el-button>
              </div>
            </el-card>
            <el-card class="class-info-card">
              <div slot="header" style="text-align: left">作业</div>
              <el-collapse v-model="activeHomeworkId" accordion>
                <el-collapse-item v-for="(homework, i) of classInfo.homework" :key="i" :name="homework.id">
                  <template slot="title">
                    {{homework.name}}&nbsp;&nbsp;&nbsp;
                    <i v-if="homework.submissionId === 0" class="el-icon-warning">未提交</i>
                    <i v-else class="el-icon-check">已提交</i>
                  </template>
                  <div class="left">{{homework.description}}</div>
                  <div class="left">截止日期：{{dateTime(homework.deadline)}}</div>
                  <el-button style="margin-top: 16px" @click="downloadSubmission(homework.submissionId)"
                             v-show="homework.submissionId !== 0">下载作业</el-button>
                  <el-upload :data="{studentId: userId, homeworkId: homework.id}" action="/MyCourses/submitHomework"
                             :before-upload="beforeHomeworkUpload" :on-success="uploadHomeworkSuccess" limit="1"
                             v-show="homework.submissionId === 0" style="margin-top: 15px" drag>
                    <i class="el-icon-upload"></i>
                    <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                    <div class="el-upload__tip" slot="tip">只能上传一个
                      <div v-if="homework.typeRestriction.length > 0 && homework.typeRestriction[0].length > 0">
                        {{homework.typeRestriction.join('/')}}</div>文件，且不超过{{homework.sizeLimit}}MB
                    </div>
                  </el-upload>
                </el-collapse-item>
              </el-collapse>
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
    if (this.$cookies.isKey('userId')) {
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
    } else {
      /* 如果cookie不存在，则跳转到登录页 */
      this.$router.push('/login')
    }
  },
  methods: {
    pre2 (num) {
      return (Array(2).join('0') + num).slice(-2)
    },
    dateTime (dateTime) {
      let sec = dateTime.length >= 6 ? dateTime[5] : 0
      return dateTime[0] + '年' + dateTime[1] + '月' + dateTime[2] + '日' +
        ' ' + dateTime[3] + ':' + this.pre2(dateTime[4]) + ':' + this.pre2(sec)
    },
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
    clickCourseware (coursewareId, name) {
      this.$http.get('/MyCourses/downloadCourseware', {'params': {coursewareId: coursewareId}, 'responseType': 'blob'}).then(data => {
        // 由于是ajax调用下载方法，下载数据不会直接下载到本地，所以再创建一个a标签，给它一个 download 属性（HTML5新属性）
        console.log(data)
        if (!data) {
          return
        }
        let url = window.URL.createObjectURL(data.data)
        let link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        // download 属性定义了下载链接的地址而不是跳转路径
        link.setAttribute('download', name)
        document.body.appendChild(link)
        link.click()
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
      for (let item of this.classInfo.homework) {
        if (item.id === this.activeHomeworkId) {
          console.log(item.typeRestriction)
          console.log(file.name)
          const isValidType = item.typeRestriction.length === 0 || item.typeRestriction[0].length === 0 ||
            item.typeRestriction.includes(file.name.substring(file.name.indexOf('.') + 1))
          const isValidSize = file.size / 1024 / 1024 < item.sizeLimit
          if (!isValidType) {
            this.$message.error('上传文件类型只能为' + item.typeRestriction.join('、') + '!')
          }
          if (!isValidSize) {
            this.$message.error('上传文件大小不能超过' + item.sizeLimit + 'MB!')
          }
          return isValidType && isValidSize
        }
      }
    },
    addPost (formName, classId) {
      console.log(this.$refs[formName])
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const data = {userId: this.userId, classId: classId, title: this.postForm.title, text: this.postForm.text}
          /* HTTP请求 */
          this.$http.post('/MyCourses/addPost', data).then((res) => {
            console.log(res.data)
            if (res.data.result === 'SUCCESS') {
              this.classInfo.posts.push(res.data.post)
              this.newPostDialogVisible = false
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
    addReply (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const data = {userId: this.userId, postId: this.replyForm.postId, text: this.replyForm.text}
          /* HTTP请求 */
          this.$http.post('/MyCourses/addReply', data).then((res) => {
            console.log(res.data)
            if (res.data.result === 'SUCCESS') {
              this.classInfo.posts.forEach(post => {
                if (post.id === this.replyForm.postId) {
                  post.replies.push(res.data.reply)
                  this.newReplyDialogVisible = false
                  this.$message.success('回帖成功')
                }
              })
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
      if (res.result === 'SUCCESS') {
        this.classInfo.homework.forEach(homework => {
          if (homework.id === res.homeworkId) {
            homework.submissionId = res.submissionId
          }
        })
        this.$message.success('提交作业成功！')
      } else {
        this.$message.error('网络错误，请刷新或稍后再试')
      }
    },
    downloadSubmission (submissionId) {
      /* HTTP请求 */
      this.$http.get('/MyCourses/downloadSubmission', {params: {submissionId: submissionId}, 'responseType': 'blob'}).then((data) => {
        // 由于是ajax调用下载方法，下载数据不会直接下载到本地，所以再创建一个a标签，给它一个 download 属性（HTML5新属性）
        console.log(data)
        if (!data) {
          return
        }
        let url = window.URL.createObjectURL(data.data)
        let link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        // download 属性定义了下载链接的地址而不是跳转路径
        let filename = data.headers.map['content-disposition'][0]
        filename = filename.substring(filename.indexOf('filename=') + 9)
        link.setAttribute('download', filename)
        document.body.appendChild(link)
        link.click()
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
      })
    }
  },
  data () {
    return {
      name: this.$cookies.get('email'),
      userId: this.$cookies.get('userId'),
      isProfile: true,
      newPostDialogVisible: false,
      newReplyDialogVisible: false,
      activeHomeworkId: '',
      activePostId: '',
      postForm: {title: '', text: ''},
      replyForm: {postId: 1, text: ''},
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
          submissionId: 1,
          sizeLimit: 2,
          typeRestriction: ['doc', 'docx']
        }, {
          id: 2,
          name: '作业2',
          description: '一段描述',
          deadline: '2019-03-10',
          submissionId: 0,
          sizeLimit: 2,
          typeRestriction: ['doc', 'docx']
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
