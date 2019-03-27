<template>
  <el-container>
    <el-header>
      <student-nav :name="name"></student-nav>
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
                <time class="time">{{item.time}}</time>
              </div>
            </el-card>
          </el-card>
        </el-col>
        <el-col>
          <el-button @click="deleteAccount">注销账户</el-button>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'TeacherInfo',
  mounted () {
    // if (this.$cookies.isKey('userId')) {
    /* HTTP请求 */
    this.$http.get('/MyCourses/information', {'params': {'userId': this.$cookies.get('userId')}
    }).then((res) => {
      if (res.data.result === 'SUCCESS') {
        this.userInfo = res.data.userInfo
        // TODO
        this.messages = res.data.messages
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
    // } else {
    //   /* 如果cookie不存在，则跳转到登录页 */
    //   this.$router.push('/login')
    // }
  },
  data () {
    return {
      name: this.$cookies.get('email'),
      userInfo: [{name: '姓名', value: '未知'}, {name: '身份', value: '未知'}, {name: '邮箱', value: '未知'}],
      isEditing: false,
      editUserForm: {name: '未知'},
      messages: [
        {messageId: '1', title: '标题1', sender: '教师1', time: '2019-03-07 21:18:00', message: '正文'},
        {messageId: '2', title: '标题2', sender: '教师1', time: '2019-03-08 21:18:00', message: '正文2'},
        {messageId: '3', title: '标题3', sender: '教师2', time: '2019-03-09 21:18:00', message: '正文正文正文正文正文正文正文正文正文正文正文正文正文'}
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
        this.$http.post('/MyCourses/deleteMessages', {
          userIds: userIds
        }).then((res) => {
          if (res.bodyText === 'SUCCESS') {
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
      this.$http.post('/MyCourses/deleteMessages', {
        messageIds: [messageId]
      }).then((res) => {
        if (res.bodyText === 'SUCCESS') {
          this.messages.filter(value => value.messageId === messageId)
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
        if (res.bodyText === 'SUCCESS') {
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
          if (res.bodyText === 'SUCCESS') {
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
