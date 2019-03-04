<template>
  <el-container>
    <el-header height="200px">
      <img src="../../assets/logo.png">
    </el-header>
    <el-main>
      <div class="login-wrap" v-show="showLogin">
        <h3>登录</h3>
        <el-input class="input" type="text" placeholder="用户名" v-model="username"></el-input>
        <el-input class="input" type="input password" placeholder="密码" v-model="password"></el-input>
        <p class="prompt" v-show="showPrompt">{{prompt}}</p>
        <el-button class="button" type="primary" v-on:click="login">登录</el-button>
        <span class="span" v-on:click="toRegister">没有账号？马上注册</span>
      </div>

      <div class="register-wrap" v-show="showRegister">
        <h3>注册</h3>
        <el-input class="input" type="text" placeholder="NJU邮箱" v-model="email"></el-input>
        <el-input class="input" type="text" placeholder="姓名" v-model="newUsername"></el-input>
        <el-input class="input" type="password" placeholder="密码" v-model="newPassword"></el-input>
        <el-input class="input" type="password" placeholder="确认密码" v-model="newPasswordConfirm"></el-input>
        <p class="prompt" v-show="showPrompt">{{prompt}}</p>
        <el-button class="button" type="primary" v-on:click="register">注册</el-button>
        <span class="span" v-on:click="toLogin">已有账号？马上登录</span>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import {setCookie, getCookie} from '../../assets/js/cookie'

export default {
  name: 'login',
  mounted () {
    if (getCookie('username')) {
      this.$router.push('/')
    }
  },
  methods: {
    login () {
      if (this.username === '' || this.password === '') {
        alert('请输入用户名或密码')
      } else {
        let data = {'username': this.username, 'password': this.password}
        /* 接口请求 */
        this.$http.post('/MyCourses/login', data).then((res) => {
          console.log(res)
          /* 接口的传值是(-1,该用户不存在),(0,密码错误)，同时还会检测管理员账号的值 */
          if (res.data === -1) {
            this.prompt = '该用户不存在'
            this.showPrompt = true
          } else if (res.data === 0) {
            this.prompt = '密码输入错误'
            this.showPrompt = true
          } else if (res.data === 'admin') {
            /* 路由跳转this.$router.push */
            this.$router.push('/main')
          } else {
            this.prompt = '登录成功'
            this.showPrompt = true
            setCookie('username', this.username, 1000 * 60)
            setTimeout(function () {
              this.$router.push('/')
            }.bind(this), 1000)
          }
        })
      }
    },
    register () {
      if (this.newUsername === '' || this.newPassword === '') {
        alert('请输入用户名或密码')
      } else {
        let data = {'username': this.newUsername, 'password': this.newPassword}
        this.$http.post('/MyCourses/register', data).then((res) => {
          console.log(res)
          if (res.data === 'ok') {
            this.prompt = '注册成功'
            this.showPrompt = true
            this.username = ''
            this.password = ''
            /* 注册成功之后再跳回登录页 */
            setTimeout(function () {
              this.showRegister = false
              this.showLogin = true
              this.showPrompt = false
            }.bind(this), 1000)
          }
        })
      }
    },
    toRegister () {
      this.prompt = ''
      this.showRegister = true
      this.showLogin = false
    },
    toLogin () {
      this.prompt = ''
      this.showRegister = false
      this.showLogin = true
    }
  },
  data () {
    return {
      showLogin: true,
      showRegister: false,
      showPrompt: false,
      prompt: '',
      username: '',
      password: '',
      email: '',
      newUsername: '',
      newPassword: '',
      newPasswordConfirm: ''
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
  .input {
    margin-bottom: 15px;
  }
  .prompt {
    color: red;
  }
  .button {
    width: 250px;
    margin-bottom: 15px;
  }
  .span {
    cursor: pointer;
  }
  .span:hover {
    color: #409EFF;
  }
</style>
