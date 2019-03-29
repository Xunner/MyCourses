<template>
  <el-container>
    <el-header>
      <teacher-nav :name="name"></teacher-nav>
    </el-header>
    <el-main>
      <el-row>
        <el-col>
          <el-card class="box-card" style="width: 25%">
            <div slot="header" class="text">账户信息
              <el-button v-if="!isEditing" style="float: right; padding: 3px 0" type="text" icon="el-icon-edit"
                         @click="editUser">编辑</el-button>
              <el-button v-else style="float: right; padding: 3px 0" type="text" icon="el-icon-edit"
                         @click="cancelEditUser">取消编辑</el-button>
            </div>
            <div v-for="(item, i) of userInfo" :key=i class="text">
              <label>{{item.name}}:&nbsp;&nbsp;</label>
              <el-input v-if="isEditing && item.name === '姓名'" v-model="editUserForm.name"
                        size="mini" class="input"></el-input>
              <span v-else>{{item.value}}</span>
            </div>
            <el-button v-if="isEditing" class="button" type="primary" @click="updateUserInfo()">保存修改</el-button>
          </el-card>
          <!--TODO 教师统计信息card-->
          <el-card class="box-card">
            <div slot="header" class="text">消息
              <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-delete" @click="deleteAllMessages">全部删除</el-button>
            </div>
            <el-card :body-style="{ padding: '0px' }" class="message-card" v-for="(item, i) of messages" :key=i>
              <div slot="header" style="text-align: left">
                {{item.title}}
                <i class="el-icon-error deleteMessageIcon" style="float: right" @click="deleteMessage(item.messageId)"></i>
                <span class="message-sender">发送者：{{item.sender}}</span>
              </div>
              <div class="message-message">{{item.message}}</div>
              <div class="bottom">
                <time class="time" :datetime="item.time">{{item.time[0]}}年{{item.time[1]}}月{{item.time[2]}}日 {{item.time[3]}}:{{item.time[4]}}:{{item.time[5]}}</time>
              </div>
            </el-card>
            <el-button @click="newMessageDialogVisible = true">写新消息</el-button>
          </el-card>
        </el-col>
        <el-col>
          <el-button @click="deleteAccount">注销账户</el-button>
        </el-col>
      </el-row>
      <!--弹窗：写新消息-->
      <el-dialog title="新的消息" :visible.sync="newMessageDialogVisible" width="40%" center>
        <el-form :model="newMessageForm" label-width="75px" ref="newMessageForm">
          <el-form-item label="标题" prop="title" :rules="{required: true, message: '标题不能为空', trigger: 'blur'}">
            <el-input v-model="newMessageForm.title" style="float: left" placeholder="请输入标题"></el-input>
          </el-form-item>
          <el-form-item label="群发班级" prop="classes">
            <el-select v-model="newMessageForm.classes" multiple placeholder="请选择群发班级">
              <el-option v-for="item in classes" :key="item.classId" :value="item.classId"
                         :label="item.courseName+' '+item.startTime[0]+'年'+item.startTime[1]+'月'+item.startTime[2]+'日'+' '+item.classOrder+'班'"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-for="(user, index) in newMessageForm.users" :label="'收件人' + (index+1)" :key="user.key"
                        :prop="'users.' + index + '.email'" :rules="{required: true, message: '邮箱不能为空', trigger: 'blur'}">
            <el-input v-model="user.email" style="width: 90%; margin-right: 10px" placeholder="请输入收件人邮箱"></el-input>
            <el-button type="danger" icon="el-icon-delete" @click.prevent="removeUser(user)" size="mini" circle></el-button>
          </el-form-item>
          <el-form-item label="正文" prop="message" :rules="{required: true, message: '正文不能为空', trigger: 'blur'}">
            <el-input type="textarea" :autosize="{minRows: 4}" placeholder="请输入正文" v-model="newMessageForm.message"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="newMessageDialogVisible = false">取 消</el-button>
          <el-button @click="addReceiver">新增收件人</el-button>
          <el-button type="primary" @click="addMessages('newMessageForm')">确 定</el-button>
        </div>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'TeacherInfo',
  mounted () {
    if (this.$cookies.isKey('userId')) {
      /* HTTP请求 */
      this.$http.get('/MyCourses/information', {'params': {'userId': this.$cookies.get('userId')}
      }).then((res) => {
        if (res.data.result === 'SUCCESS') {
          this.userInfo = res.data.userInfo
          // TODO
          this.messages = res.data.messages
          this.classes = res.data.classes
          res.data.userInfo.forEach(item => {
            if (item.name === '姓名') {
              this.editUserForm.name = item.value
            }
          })
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
      userId: this.$cookies.get('userId'),
      userInfo: [{name: '姓名', value: '未知'}, {name: '身份', value: '未知'}, {name: '邮箱', value: '未知'}],
      isEditing: false,
      editUserForm: {name: '未知'},
      newMessageDialogVisible: false,
      newMessageForm: {
        title: '',
        message: '',
        classes: [],
        users: []
      },
      classes: [{
        classId: 1,
        courseName: '课程1',
        startTime: '2019-03-01',
        classOrder: 1
      }, {
        classId: 2,
        courseName: '课程1',
        startTime: '2019-03-01',
        classOrder: 2
      }],
      messages: [
        {messageId: 1, title: '标题1', sender: '教师1', time: '2019-03-07 21:18:00', message: '正文'},
        {messageId: 2, title: '标题2', sender: '教师1', time: '2019-03-08 21:18:00', message: '正文2'},
        {messageId: 3, title: '标题3', sender: '教师2', time: '2019-03-09 21:18:00', message: '正文正文正文正文正文正文正文正文正文正文正文正文正文'}
      ]
    }
  },
  methods: {
    editUser () {
      this.isEditing = true
    },
    cancelEditUser () {
      this.isEditing = false
    },
    deleteAllMessages () {
      this.$confirm('是否删除全部消息？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let userIds = []
        this.messages.forEach(value => {
          userIds.push(value.messageId)
        })
        this.$http.post('/MyCourses/deleteMessages', userIds).then((res) => {
          if (res.data === 'SUCCESS') {
            this.$message.success('成功删除全部消息!')
            this.messages = []
          } else {
            this.$message.error('网络错误，请刷新或稍后再试')
          }
        }, () => {
          this.$message.error('网络错误，请刷新或稍后再试')
        })
      }).catch(() => {
      })
    },
    deleteMessage (messageId) {
      this.$http.post('/MyCourses/deleteMessages', [messageId]).then((res) => {
        if (res.data === 'SUCCESS') {
          console.log(this.messages.length)
          this.messages.filter(value => value.messageId !== messageId)
          console.log(this.messages.length)
          this.$message.success('成功删除消息')
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
      })
    },
    updateUserInfo () {
      this.$http.post('/MyCourses/updateUserInfo', {
        userId: this.$cookies.get('userId'),
        name: this.editUserForm.name,
        studentId: ''
      }).then((res) => {
        if (res.data === 'SUCCESS') {
          this.userInfo.map(item => {
            if (item.name === '姓名') {
              item.value = this.editUserForm.name
            }
            return item
          })
          this.isEditing = false
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
      })
    },
    deleteAccount () {
      this.$confirm('是否永久注销你的账号？此举动不会真正删除该账号的信息，而你将无法再使用该账号！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post('/MyCourses/deleteAccount', {
          userId: this.$cookies.get('userId')
        }).then((res) => {
          if (res.data === 'SUCCESS') {
            this.$message.success('成功永久注销你的账号!')
            /* 退出登录 */
            this.$cookies.remove('userId')
            this.$cookies.remove('userType')
            this.$router.push('/login')
          } else {
            this.$message.error('网络错误，请刷新或稍后再试')
          }
        }, () => {
          this.$message.error('网络错误，请刷新或稍后再试')
        })
      }).catch(() => {
      })
    },
    addReceiver () {
      this.newMessageForm.users.push({email: ''})
    },
    addMessages (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.newMessageForm.classes.length === 0 && this.newMessageForm.users.length === 0) {
            this.$message.error('发送消息至少要选择一个班级或收件人！')
          } else {
            this.$http.post('/MyCourses/addMessages', {
              senderId: this.userId,
              title: this.newMessageForm.title,
              message: this.newMessageForm.message,
              classes: this.newMessageForm.classes,
              users: this.newMessageForm.users
            }).then(res => {
              if (res.data.result === 'SUCCESS') {
                this.newMessageDialogVisible = false
                this.$message.success('发送消息成功！')
              } else if (res.data.result === 'NOT_EXIST') {
                this.newMessageDialogVisible = false
                let emails = ''
                res.data.failedEmails.forEach((email) => {
                  emails += email + '<br />'
                })
                this.$notify({
                  title: '部分消息发送失败',
                  message: '以下收件人邮箱不存在，对他们的消息未能成功发出，请再次检查：<br />' + emails,
                  type: 'warning',
                  duration: 0
                })
              } else {
                this.$message.error('网络错误，请刷新或稍后再试')
              }
            }, () => {
              this.$message.error('网络错误，请刷新或稍后再试')
            })
          }
        } else {
          return false
        }
      })
    },
    removeUser (item) {
      const index = this.newMessageForm.users.indexOf(item)
      if (index !== -1) {
        this.newMessageForm.users.splice(index, 1)
      }
    }
  }
}
</script>

<style scoped>
  .box-card {
    width: 31%;
    margin: 15px 15px;
    float: left;
  }
  .message-card {
    width: 92%;
    margin: 15px 15px;
    float: left;
  }
  .text {
    margin: 20px 0;
    text-align: left;
  }
  .input {
    width: 200px;
  }
  .bottom {
    margin-top: 13px;
    line-height: 12px;
    padding: 10px;
  }
  .deleteMessageIcon {
    margin-top: 3px;
  }
  .deleteMessageIcon:hover {
    color: red;
  }
  .message-sender {
    float: right;
    padding-right: 10px;
  }
  .message-message {
    text-align: left;
    padding: 10px 16px;
  }
</style>
