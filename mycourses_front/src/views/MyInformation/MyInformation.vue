<template>
  <el-container>
    <el-header>
      <student-nav></student-nav>
    </el-header>
    <el-main>
      <el-card class="box-card">
        <div slot="header" class="text">账户信息
          <el-button v-if="!isEditing" style="float: right; padding: 3px 0" type="text" icon="el-icon-edit"
                     @click="editUser">编辑</el-button>
          <el-button v-else style="float: right; padding: 3px 0" type="text" icon="el-icon-edit"
                     @click="cancelEditUser">取消编辑</el-button>
        </div>
        <div v-for="(item, i) of userInfo" :key=i class="text">
          <label>{{item.name}}:&nbsp;&nbsp;</label>
          <el-input v-if="isEditing && (item.name === '姓名' || item.name === '学号')" v-model="editUserForm[item.name]"
                    size="mini" class="input"></el-input>
          <span v-else>{{item.value}}</span>
        </div>
        <el-button v-if="isEditing" class="button" type="primary" @click="updateUserInfo()">保存修改</el-button>
      </el-card>
      <el-card class="box-card">
        <div slot="header" class="text">用户信息统计</div>
        <el-collapse v-model="activeNames">
          <el-collapse-item title="选课" name="1">
            <el-tree :data="classesTaken" :props="defaultProps" default-expand-all></el-tree>
          </el-collapse-item>
          <el-collapse-item title="退课" name="2">
            <el-tree :data="classesQuit" :props="defaultProps"></el-tree>
          </el-collapse-item>
          <el-collapse-item title="成绩" name="3">
            <el-tree :data="scores" :props="defaultProps"></el-tree>
          </el-collapse-item>
        </el-collapse>
      </el-card>
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
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'MyInformation',
  mounted () {
    if (this.$cookies.isKey('userId')) {
      /* HTTP请求 */
      this.$http.get('/MyCourses/MyInformation', {
        'params': {
          'userId': this.$cookies.get('userId'),
          'userType': this.$cookies.get('userType')
        }
      }).then((res) => {
        if (res.data.result === 'SUCCESS') {
          this.userInfo = res.data.userInfo
          this.classesTaken = res.data.classesTaken
          this.classesQuit = res.data.classesQuit
          this.scores = res.data.scores
          this.messages = res.data.messages
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
      userInfo: [{name: '姓名', value: '未知'}, {name: '身份', value: '未知'}, {name: '邮箱', value: '未知'}, {name: '学号', value: '未知'}],
      activeNames: [],
      isEditing: false,
      editUserForm: {
        name: '未知',
        studentId: '未知'
      },
      classesTaken: [{
        label: '大一',
        children: [{
          label: '第一学期',
          children: [{
            label: '课程1'
          }]
        }, {
          label: '第二学期',
          children: [{
            label: '课程2'
          }]
        }]
      }, {
        label: '大二',
        children: [{
          label: '第一学期',
          children: [{
            label: '课程3'
          }]
        }, {
          label: '第二学期',
          children: [{
            label: '课程4'
          }]
        }]
      }, {
        label: '大三',
        children: [{
          label: '第一学期',
          children: [{
            label: '课程5'
          }]
        }]
      }],
      classesQuit: [{
        label: '大一',
        children: [{
          label: '第一学期',
          children: [{
            label: '课程1'
          }]
        }, {
          label: '第二学期',
          children: [{
            label: '课程2'
          }]
        }]
      }, {
        label: '大二',
        children: [{
          label: '第二学期',
          children: [{
            label: '课程4'
          }]
        }]
      }],
      scores: [{
        label: '大一',
        children: [{
          label: '第一学期',
          children: [{
            label: '课程1: 100'
          }]
        }, {
          label: '第二学期',
          children: [{
            label: '课程2: 100'
          }]
        }]
      }, {
        label: '大二',
        children: [{
          label: '第一学期',
          children: [{
            label: '课程3: 100'
          }]
        }, {
          label: '第二学期',
          children: [{
            label: '课程4: 100'
          }]
        }]
      }, {
        label: '大三',
        children: [{
          label: '第一学期',
          children: [{
            label: '课程5: 100'
          }]
        }]
      }],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      messages: [
        {messageId: '1', title: '标题1', sender: '教师1', time: '2019-03-07 21:18:00', message: '正文'},
        {messageId: '2', title: '标题2', sender: '教师1', time: '2019-03-08 21:18:00', message: '正文2'},
        {messageId: '3', title: '标题3', sender: '教师2', time: '2019-03-09 21:18:00', message: '正文正文正文正文正文正文正文正文正文正文正文'}
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
        this.$http.post('/MyCourses/deleteAllMessage', {
          userId: this.$cookies.get('userId')
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
    updateUserInfo () {
      this.$http.post('/MyCourses/updateUser', {
        userId: this.$cookies.get('userId'),
        name: this.editUserForm.name,
        studentId: this.editUserForm.studentId
      }).then((res) => {
        this.userInfo.map(item => {
          if (item.name === '姓名') {
            item.value = res.data.name
          } else if (item.name === '学号') {
            item.value = res.data.studentId
          }
          return item
        })
        this.isEditing = false
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
      })
    },
    deleteMessage (messageId) {
      this.$http.post('/MyCourses/deleteMessage', {
        messageId: messageId
      }).then((res) => {
        if (res.bodyText === 'SUCCESS') {
          this.messages.filter(value => value.messageId === messageId)
        } else {
          this.$message.error('网络错误，请刷新或稍后再试')
        }
      }, () => {
        this.$message.error('网络错误，请刷新或稍后再试')
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
