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
              <label v-show="!teacherCourse.passReview"> （审核中）</label>
              <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-plus"
                         @click="publishClass(teacherCourse.courseId, teacherCourse.name)"
                         :disabled="!teacherCourse.passReview">发布新课程
              </el-button>
            </div>
            <el-button v-for="(teacherClass, i2) of teacherCourse.classes" :key="i2"
                       @click="clickClass(teacherClass.classId)" class="teacher-class" :disabled="!teacherClass.passReview">
              {{teacherClass.startTime}} 第{{teacherClass.term}}学期 {{teacherClass.classOrder}}班
              <label v-show="!teacherClass.passReview"> （审核中）</label>
            </el-button>
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
              <div class="left">开课时间：{{classInfo.startTime}}</div>
              <div class="left">结课时间：{{classInfo.endTime}}</div>
            </el-card>
            <el-card class="class-info-card">
              <div slot="header" style="text-align: left">课件</div>
              <div class="left" v-for="(courseware, i) of classInfo.coursewares" :key="i">
                <el-button type="text" @click="clickCourseware(courseware.id)">{{courseware.name}}</el-button>
              </div>
              <el-upload :data="userId" action="/MyCourses/uploadCourseware" :before-upload="beforeCoursewareUpload"
                         :on-success="uploadCoursewareSuccess" style="margin-top: 15px" multiple drag>
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                <div class="el-upload__tip" slot="tip">大小不超过500MB</div>
              </el-upload>
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
                  <div class="left">截止日期：{{homework.deadline}}</div>
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
  name: 'TeacherCourses',
  mounted () {
    // if (this.$cookies.isKey('userId')) {
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
    beforeCoursewareUpload (file) {
      const isValidSize = file.size / 1024 / 1024 < 500
      if (!isValidSize) {
        this.$message.error('上传文件大小不能超过500MB!')
      }
      return isValidSize
    },
    uploadCoursewareSuccess (res) {
      console.log(res.data)
      if (res.data.result === 'SUCCESS') {
        this.classInfo.coursewares.push(res.data.courseware)
        this.$message.success('上传课件成功！')
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
            if (res.data.result === 'SUCCESS') {
              this.newCourseDialogVisible = false
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
            if (res.data.result === 'SUCCESS') {
              this.newClassDialogVisible = false
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
    }
  },
  data () {
    return {
      name: this.$cookies.get('email'),
      userId: this.$cookies.get('userId'),
      isProfile: false,
      newCourseDialogVisible: false,
      newClassDialogVisible: false,
      activeHomeworkId: '',
      activePostId: '',
      postForm: {title: '', text: ''},
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
      newCourseRules: {name: [{required: true, message: '请输入课程名', trigger: 'blur'}]},
      newClassRules: {time: [{type: 'array', required: true, message: '请选择时间段', trigger: 'change'}]},
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
          classes: [
            {classId: 1, term: 1, classOrder: 1, startTime: '2019-03-01', endTime: '2019-06-28', passReview: true},
            {classId: 2, term: 1, classOrder: 2, startTime: '2019-03-01', endTime: '2019-06-28', passReview: true},
            {classId: 3, term: 1, classOrder: 3, startTime: '2019-03-01', endTime: '2019-06-28', passReview: false}]
        }, {
          courseId: 2,
          name: '课程2',
          grade: 1,
          passReview: true,
          classes: [{classId: 1, term: 1, classOrder: 1, startTime: '2019-03-01', endTime: '2019-06-28', passReview: false}]
        }, {
          courseId: 3,
          name: '课程3',
          grade: 2,
          passReview: false,
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
    margin-top: 40px;
  }
</style>
