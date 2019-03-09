<template>
  <el-container>
    <el-header height="200px">
      <img src="../../assets/logo.png">
    </el-header>
    <el-main>
      <el-form class="login-wrap" :model="logInForm" :rules="logInRules" ref="logInForm" v-if="isLogIn">
        <h3>登录</h3>
        <el-form-item prop="email">
          <el-input class="input" type="text" placeholder="NJU邮箱" v-model="logInForm.email"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input class="input" type="password" placeholder="密码" v-model="logInForm.password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="button" type="primary" @click="login('logInForm')">登录</el-button>
        </el-form-item>
        <span class="span" v-on:click="toRegister">没有账号？马上注册</span>
      </el-form>
      <el-form class="register-wrap" :model="registerForm" :rules="registerRules" ref="registerForm" label-width="100px"
               v-else>
        <h3>注册</h3>
        <el-form-item label="邮箱" prop="email">
          <el-input class="input" type="text" placeholder="必须为NJU邮箱" v-model="registerForm.email"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input class="input" type="password" v-model="registerForm.password"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPassword">
          <el-input class="input" type="password" v-model="registerForm.checkPassword"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="newName">
          <el-input class="input" type="text" v-model="registerForm.newName"></el-input>
        </el-form-item>
        <el-form-item label="学号" prop="studentId">
          <el-input class="input" type="text" v-model="registerForm.studentId"></el-input>
        </el-form-item>
        <el-form-item label="身份" prop="userType">
          <el-radio-group v-model="registerForm.userType">
            <el-radio-button label="student">学生</el-radio-button>
            <el-radio-button label="teacher">教师</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button class="button" type="primary" @click="register('registerForm')">注册</el-button>
        </el-form-item>
        <span class="span" v-on:click="toLogin">已有账号？马上登录</span>
      </el-form>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'login',
  mounted () {
    if (this.$cookies.isKey('userId')) {
      this.$router.push('/')
    }
  },
  methods: {
    login (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let data = {'email': this.logInForm.email, 'password': this.logInForm.password}
          /* HTTP请求 */
          this.$http.post('/MyCourses/login', data).then((res) => {
            console.log(res.data)
            if (res.data.result === 'NOT_EXIST') {
              this.$message.error('该用户不存在或密码输入错误')
            } else if (res.data.result === 'SUCCESS') {
              this.$cookies.set('userId', this.data.userId, '2MIN')
              this.$cookies.set('userType', this.data.userType, '2MIN')
              this.$router.push('/')
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
    register (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let data = {
            'userType': this.registerForm.userType,
            'email': this.registerForm.email,
            'name': this.registerForm.newName,
            'password': this.registerForm.password,
            'studentId': this.registerForm.studentId
          }
          this.$http.post('/MyCourses/register', data).then((res) => {
            if (res.bodyText === 'EXIST') {
              this.$message.warning('该邮箱已被注册')
            } else if (res.bodyText === 'SUCCESS') {
              this.$message.success('注册成功')
              // ↓this可能出事！
              this.$refs[formName].resetFields()
              /* 注册成功之后再跳回登录页 */
              this.showRegister = false
              this.showLogin = true
            } else {
              this.$message.error('网络错误，请刷新或稍后再试')
              console.log('未知错误：' + res.bodyText)
            }
          }, () => {
            this.$message.error('网络错误，请刷新或稍后再试')
          })
        } else {
          return false
        }
      })
    },
    toRegister () {
      this.isLogIn = false
    },
    toLogin () {
      this.isLogIn = true
    }
  },
  data () {
    let validateEmail = (rule, value, callback) => {
      if (this.registerForm.email.match(/^[a-zA-Z0-9]+@smail.nju.edu.cn$/)) {
        callback()
      } else {
        callback(new Error('邮箱格式不符!'))
      }
    }
    let validatePassword = (rule, value, callback) => {
      if (this.registerForm.checkPassword !== '') {
        this.$refs.registerForm.validateField('checkPassword')
      }
      callback()
    }
    let validateCheckPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      isLogIn: true,
      logInForm: {
        email: '',
        password: ''
      },
      logInRules: {
        email: [{required: true, message: '请输入完整NJU邮箱', trigger: 'blur'}],
        password: [{required: true, message: '请输入密码', trigger: 'blur'}]
      },
      registerForm: {
        email: '',
        newName: '',
        password: '',
        checkPassword: '',
        userType: 'student',
        studentId: ''
      },
      registerRules: {
        email: [{required: true, message: '请输入完整NJU邮箱', trigger: 'blur'},
          {validator: validateEmail, trigger: 'blur'}],
        password: [{required: true, message: '请输入新密码', trigger: 'blur'},
          {validator: validatePassword, trigger: 'blur'}],
        checkPassword: [{required: true, message: '请再次输入密码', trigger: 'blur'},
          {validator: validateCheckPassword, trigger: 'blur'}],
        userType: [{required: true, message: '请选择您的身份', trigger: 'blur'}]
      }
    }
  }
}
</script>

<style>
  .login-wrap {
    text-align: center;
    margin:0 auto;
    width: 250px;
  }
  .register-wrap {
    text-align: center;
    margin:0 auto;
    width: 250px;
  }
  .button {
    width: 200px;
    margin-bottom: 15px;
  }
  .span {
    cursor: pointer;
  }
  .span:hover {
    color: #409EFF;
  }
</style>
