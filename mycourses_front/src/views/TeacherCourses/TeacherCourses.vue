<template>
  <el-container>
    <el-header>
      <teacher-nav :name="name"></teacher-nav>
    </el-header>
    <el-main>
      <!--所有课程-->
      <el-row>
        <el-col>
          <el-card v-for="(teacherCourse, i) of teacherCourses" :key="i" class="box-card" v-show="isProfile">
            <div slot="header">{{teacherCourse.name}}
              <i v-show="!teacherCourse.passReview"> （审核中）</i>
              <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-plus"
                         @click="publishClass(teacherCourse.courseId, teacherCourse.name)"
                         :disabled="!teacherCourse.passReview">发布新课程
              </el-button>
            </div>
            <el-button v-for="(teacherClass, i2) of teacherCourse.classes" :key="i2"
                       @click="clickClass(teacherClass.classId)" class="teacher-class" :disabled="!teacherClass.passReview">
              {{teacherClass.startTime[0]}}年{{teacherClass.startTime[1]}}月{{teacherClass.startTime[2]}}日 第{{teacherClass.term}}学期 {{teacherClass.classOrder}}班
              <i v-show="!teacherClass.passReview"> （审核中）</i>
            </el-button>
            <el-row>
              <el-col :span="12">
                <el-card class="class-info-card">
                  <div slot="header" style="text-align: left">课件</div>
                  <div class="left" v-for="(courseware, i) of teacherCourse.coursewares" :key="i">
                    <el-button type="text" @click="clickCourseware(courseware.id, courseware.name)">{{courseware.name}}</el-button>
                  </div>
                  <el-upload name="coursewares" :data="{courseId: teacherCourse.courseId}" action="/MyCourses/uploadCourseware"
                             :before-upload="beforeCoursewareUpload" :on-success="uploadCoursewareSuccess"
                             style="margin-top: 15px; text-align: center" multiple>
                    <el-button size="small" type="primary" :disabled="!teacherCourse.passReview">点击上传</el-button>
                    <div class="el-upload__tip" slot="tip">大小不能超过500MB</div>
                  </el-upload>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card class="class-info-card" style="text-align: center">
                  <div slot="header" style="text-align: left">论坛</div>
                  <el-collapse v-model="activePostId">
                    <el-collapse-item v-for="(post, i) of teacherCourse.posts" :key="i" :name="post.id">
                      <template slot="title">标题：{{post.title}}&nbsp;&nbsp;({{post.time[0]}}年{{post.time[1]}}月{{post.time[2]}}日 {{post.time[3]}}:{{post.time[4]}}:{{post.time[5]}})</template>
                      <el-card style="margin: 5px 5px">
                        <el-button style="float: right; padding: 2px 0" type="text" icon="el-icon-plus"
                                   @click="newReplyDialogVisible = true
                                  replyForm.postId = post.id">回帖
                        </el-button>
                        <div class="left">由 {{post.poster}} 发帖：</div>
                        <div class="left">{{post.text}}</div>
                      </el-card>
                      <el-card v-for="(reply, i2) of post.replies" :key="i2" class="class-info-card">
                        <div class="left">由 {{reply.replies}} 于 {{reply.time[0]}}年{{reply.time[1]}}月{{reply.time[2]}}日 {{reply.time[3]}}:{{reply.time[4]}}:{{reply.time[5]}} 回帖：</div>
                        <div class="left">{{reply.text}}</div>
                      </el-card>
                    </el-collapse-item>
                  </el-collapse>
                  <el-button style="margin-top: 16px" @click="newPostDialogVisible = true"
                             :disabled="!teacherCourse.passReview">发布新帖子</el-button>
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
                        <el-button class="button" type="primary" @click="addPost('postForm', teacherCourse.courseId)">发帖</el-button>
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
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-button style="margin-top: 16px; margin-right: 40px" @click="newCourseDialogVisible = true"
                     v-show="isProfile">创建新课程
          </el-button>
        </el-col>
      </el-row>
      <!--弹窗：创建新课程-->
      <el-dialog title="新的课程" :visible.sync="newCourseDialogVisible" width="35%" center>
        <el-form :model="newCourseForm" label-width="80px" :rules="newCourseRules" ref="newCourseForm">
          <el-form-item label="课程名称" prop="name">
            <el-input v-model="newCourseForm.name" style="float: left"></el-input>
          </el-form-item>
          <el-form-item label="年级">
            <el-input-number v-model="newCourseForm.grade" :min="1" :max="4" style="float: left"></el-input-number>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="newCourseDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="addCourse('newCourseForm')">确 定</el-button>
        </div>
      </el-dialog>
      <!--弹窗：发布新课程-->
      <el-dialog :title="'为《' + this.newClassForm.name + '》开新的班次'" :visible.sync="newClassDialogVisible" width="35%" center>
        <el-form :model="newClassForm" label-width="80px" :rules="newClassRules" ref="newClassForm">
          <el-form-item label="学期">
            <el-input-number v-model="newClassForm.term" :min="1" :max="3" style="float: left"></el-input-number>
          </el-form-item>
          <el-form-item label="班级个数">
            <el-input-number v-model="newClassForm.classNumber" :min="1" :max="99" style="float: left"></el-input-number>
          </el-form-item>
          <el-form-item label="限选人数">
            <el-input-number v-model="newClassForm.maxNumber" :min="0" :max="9999" style="float: left"></el-input-number>
          </el-form-item>
          <el-form-item label="日期" prop="time">
              <el-date-picker v-model="newClassForm.time" type="daterange" range-separator="至"
                              start-placeholder="开始日期" end-placeholder="结束日期" style="float: left"></el-date-picker>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="newClassDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="addClass('newClassForm')">确 定</el-button>
        </div>
      </el-dialog>
      <!--某一课程详情-->
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
            </el-card>
            <el-button @click="toProfile">返回</el-button>
          </el-col>
          <el-col :span="12">
            <el-card class="class-info-card">
              <div slot="header" style="text-align: left">作业</div>
              <el-collapse v-model="activeHomeworkId" accordion>
                <el-collapse-item v-for="(homework, i) of classInfo.homework" :key="i" :name="homework.id">
                  <template slot="title">{{homework.name}}</template>
                  <div class="left">{{homework.description}}</div>
                  <div class="left">截止日期：{{homework.deadline[0]}}年{{homework.deadline[1]}}月{{homework.deadline[2]}}日 {{homework.deadline[3]}}:{{homework.deadline[4]}}:{{homework.deadline[5]}}</div>
                </el-collapse-item>
              </el-collapse>
              <el-button style="margin-top: 16px" @click="newHomeworkVisible = true">发布新作业</el-button>
            </el-card>
          </el-col>
        </el-row>
        <!--弹窗：发布作业-->
        <el-dialog title="发布作业" :visible.sync="newHomeworkVisible" width="40%" center>
          <el-form :model="newHomeworkForm" label-width="100px" :rules="newHomeworkRules" ref="newHomeworkForm">
            <el-form-item label="作业题目" prop="name">
              <el-input v-model="newHomeworkForm.name" style="float: left"></el-input>
            </el-form-item>
            <el-form-item label="作业内容">
              <el-input v-model="newHomeworkForm.description" style="float: left"></el-input>
            </el-form-item>
            <el-form-item label="截止时间" prop="deadline">
              <el-date-picker v-model="newHomeworkForm.deadline" type="datetime" placeholder="选择时间" style="float: left"></el-date-picker>
            </el-form-item>
            <el-form-item label="大小限制(MB)" prop="sizeLimit">
              <el-input-number v-model="newHomeworkForm.sizeLimit" :min="1" :max="100" style="float: left"></el-input-number>
            </el-form-item>
            <el-form-item label="类型限制">
              <el-select v-model="newHomeworkForm.typeRestriction" placeholder="不选则为无限制" multiple>
                <el-option v-for="type in validTypes" :key="type" :label="type" :value="type"></el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="newHomeworkVisible = false">取 消</el-button>
            <el-button type="primary" @click="publishHomework('newHomeworkForm')">发 布</el-button>
          </div>
        </el-dialog>
      </div>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'TeacherCourses',
  mounted () {
    if (this.$cookies.isKey('userId')) {
      /* HTTP请求 */
      this.$http.get('/MyCourses/TeacherCourses', {'params': {'teacherId': this.$cookies.get('userId')}
      }).then((res) => {
        if (res.data.result === 'SUCCESS') {
          this.teacherCourses = res.data.teacherCourses
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
    addPost (formName, courseId) {
      console.log(this.$refs[formName][0])
      this.$refs[formName][0].validate((valid) => {
        if (valid) {
          const data = {userId: this.userId, courseId: courseId, title: this.postForm.title, text: this.postForm.text}
          /* HTTP请求 */
          this.$http.post('/MyCourses/addPost', data).then((res) => {
            console.log(res.data)
            if (res.data.result === 'SUCCESS') {
              this.teacherCourses.forEach(teacherCourse => {
                if (teacherCourse.courseId === courseId) {
                  teacherCourse.posts.push(res.data.post)
                  this.newPostDialogVisible = false
                  this.$message.success('发帖成功')
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
    addReply (formName) {
      this.$refs[formName][0].validate((valid) => {
        if (valid) {
          const data = {userId: this.userId, postId: this.replyForm.postId, text: this.replyForm.text}
          /* HTTP请求 */
          this.$http.post('/MyCourses/addReply', data).then((res) => {
            console.log(res.data)
            if (res.data.result === 'SUCCESS') {
              this.teacherCourses.forEach((teacherCourse) => {
                teacherCourse.posts.forEach(post => {
                  if (post.id === this.replyForm.postId) {
                    post.replies.push(res.data.reply)
                    this.newReplyDialogVisible = false
                    this.$message.success('回帖成功')
                  }
                })
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
    beforeCoursewareUpload (file) {
      const isValidSize = file.size / 1024 / 1024 < 500
      if (!isValidSize) {
        this.$message.error('上传文件大小不能超过500MB!')
      }
      return isValidSize
    },
    uploadCoursewareSuccess (res) {
      console.log(res)
      if (res.result === 'SUCCESS') {
        this.teacherCourses.forEach(teacherCourse => {
          if (teacherCourse.courseId === res.courseId) {
            teacherCourse.coursewares.push({id: res.coursewareId, name: res.coursewareName})
            this.$message.success('上传课件成功！')
          }
        })
      } else {
        this.$message.error('网络错误，请刷新或稍后再试')
      }
    },
    publishClass (courseId, name) {
      this.newClassForm.courseId = courseId
      this.newClassForm.name = name
      this.newClassDialogVisible = true
    },
    addCourse (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$http.post('/MyCourses/addCourse', {
            teacherId: this.userId,
            name: this.newCourseForm.name,
            grade: this.newCourseForm.grade
          }).then(res => {
            if (res.data === 'SUCCESS') {
              this.newCourseDialogVisible = false
              this.teacherCourses.push({
                courseId: 0,
                name: this.newCourseForm.name,
                grade: this.newCourseForm.grade,
                passReview: false,
                coursewares: [],
                posts: [],
                classes: []
              })
              this.$message.success('创建新课程成功！请等待管理员审核')
            } else {
              this.$message.error('网络错误，请刷新或稍后再试')
            }
          }, () => {
            this.$message.error('网络错误，请刷新或稍后再试')
          })
        } else {
          return false
        }
      })
    },
    addClass (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          console.log(this.newClassForm.time)
          this.$http.post('/MyCourses/publishClasses', {
            courseId: this.newClassForm.courseId,
            startTime: this.newClassForm.time[0],
            endTime: this.newClassForm.time[1],
            classNumber: this.newClassForm.classNumber,
            term: this.newClassForm.term,
            maxNumber: this.newClassForm.maxNumber
          }).then(res => {
            if (res.data === 'SUCCESS') {
              this.newClassDialogVisible = false
              this.teacherCourses.forEach(item => {
                if (item.courseId === this.newClassForm.courseId) {
                  for (let i = 0; i < this.newClassForm.classNumber; i++) {
                    let startTime = [this.newClassForm.time[0].getFullYear(),
                      this.newClassForm.time[0].getMonth(), this.newClassForm.time[0].getDay()]
                    let endTime = [this.newClassForm.time[1].getFullYear(),
                      this.newClassForm.time[1].getMonth(), this.newClassForm.time[1].getDay()]
                    item.classes.push({
                      classId: 0,
                      term: this.newClassForm.term,
                      classOrder: i + 1,
                      startTime: startTime,
                      endTime: endTime,
                      passReview: false
                    })
                  }
                }
              })
              this.$message.success('发布新课程成功！请等待管理员审核')
            } else {
              this.$message.error('网络错误，请刷新或稍后再试')
            }
          }, () => {
            this.$message.error('网络错误，请刷新或稍后再试')
          })
        } else {
          return false
        }
      })
    },
    publishHomework (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$http.post('/MyCourses/publishHomework', {
            id: 0,
            submitted: false,
            classId: this.classInfo.id,
            name: this.newHomeworkForm.name,
            description: this.newHomeworkForm.description,
            deadline: this.newHomeworkForm.deadline,
            sizeLimit: this.newHomeworkForm.sizeLimit,
            typeRestriction: this.newHomeworkForm.typeRestriction
          }).then(res => {
            if (res.data.result === 'SUCCESS') {
              this.newHomeworkVisible = false
              this.classInfo.homework.push(res.data.homework)
              this.$message.success('发布作业成功')
            } else {
              this.$message.error('网络错误，请刷新或稍后再试')
            }
          }, () => {
            this.$message.error('网络错误，请刷新或稍后再试')
          })
        } else {
          return false
        }
      })
    }
  },
  data () {
    return {
      name: this.$cookies.get('email'),
      userId: this.$cookies.get('userId'),
      isProfile: false,
      newPostDialogVisible: false,
      newReplyDialogVisible: false,
      newCourseDialogVisible: false,
      newClassDialogVisible: false,
      newHomeworkVisible: false,
      activeHomeworkId: '',
      activePostId: '',
      validTypes: ['doc', 'docx', 'txt', 'rar', 'zip', 'md', 'ppt', 'pptx', 'xls', 'xlsx', 'c', 'cpp', 'java', 'py'],
      postForm: {title: '', text: ''},
      replyForm: {postId: 1, text: ''},
      newClassForm: {
        courseId: 1,
        name: '',
        term: 1,
        classNumber: 1,
        maxNumber: 0,
        time: []
      },
      newCourseForm: {
        name: '',
        grade: 1
      },
      newHomeworkForm: {
        classId: 1,
        name: '',
        description: '',
        deadline: '',
        sizeLimit: 20,
        typeRestriction: []
      },
      newCourseRules: {name: [{required: true, message: '请输入课程名', trigger: 'blur'}]},
      newClassRules: {time: [{type: 'array', required: true, message: '请选择时间段', trigger: 'change'}]},
      newHomeworkRules: {name: [{required: true, message: '请输入作业题目', trigger: 'blur'}],
        deadline: [{type: 'date', required: true, message: '请选择截止时间', trigger: 'change'}]},
      postRules: {
        title: [{required: true, message: '请输入标题', trigger: 'blur'}],
        text: [{required: true, message: '请输入正文', trigger: 'blur'}]
      },
      teacherCourses: [
        {
          courseId: 1,
          name: '课程1',
          grade: 1,
          passReview: true,
          coursewares: [{id: 1, name: '课件1'}, {id: 2, name: '课件2'}],
          posts: [{
            id: 1,
            poster: '发帖者1',
            title: '标题1',
            text: '正文',
            time: [],
            replies: [{id: 1, replies: '回帖者1', text: '正文', time: '2019-03-9'}, {
              id: 2,
              replies: '回帖者2',
              text: '正文',
              time: []
            }]
          }, {
            id: 2,
            poster: '发帖者2',
            title: '标题2',
            text: '正文',
            time: [],
            replies: [{id: 1, replies: '回帖者1', text: '正文', time: '2019-03-10'}]
          }],
          classes: [
            {classId: 1, term: 1, classOrder: 1, startTime: [], endTime: [], passReview: true},
            {classId: 2, term: 1, classOrder: 2, startTime: [], endTime: [], passReview: true},
            {classId: 3, term: 1, classOrder: 3, startTime: [], endTime: [], passReview: false}]
        }, {
          courseId: 2,
          name: '课程2',
          grade: 1,
          passReview: true,
          coursewares: [{id: 1, name: '课件1'}],
          posts: [{
            id: 1,
            poster: '发帖者1',
            title: '标题1',
            text: '正文',
            time: [],
            replies: [{id: 1, replies: '回帖者1', text: '正文', time: []}, {
              id: 2,
              replies: '回帖者2',
              text: '正文',
              time: []
            }]
          }],
          classes: [{classId: 1, term: 1, classOrder: 1, startTime: [], endTime: [], passReview: false}]
        }, {
          courseId: 3,
          name: '课程3',
          grade: 2,
          passReview: false,
          coursewares: [],
          posts: [],
          classes: []
        }
      ],
      classInfo: {
        id: 1,
        name: '课程1',
        teacherName: '教师1',
        grade: 1,
        term: 1,
        classOrder: 1,
        startTime: [],
        endTime: [],
        homework: [{
          id: 1,
          name: '作业1',
          description: '一段描述',
          deadline: '2019-03-9',
          submitted: true,
          sizeLimit: 2,
          typeRestriction: ['doc', 'docx']
        }, {
          id: 2,
          name: '作业2',
          description: '一段描述',
          deadline: [],
          submitted: false,
          sizeLimit: 2,
          typeRestriction: ['doc', 'docx']
        }]
      }
    }
  }
}
</script>

<style scoped>
  .box-card {
    width: 95%;
    margin: 15px 15px;
    float: left;
    text-align: left;
  }
  .class-info-card {
    width: 95%;
    margin: 15px 15px;
    float: left;
  }
  .teacher-class {
    margin-left: 10px;
    margin-right: 10px;
    margin-bottom: 16px;
  }
  .left {
    text-align: left;
  }
  .post-wrap {
    text-align: center;
  }
</style>
