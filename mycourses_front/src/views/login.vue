<template>
  <div>
    <div class="login-wrap" v-show="showLogin">
      <h3>登录</h3>
      <p v-show="showPrompt">{{prompt}}</p>
      <input type="text" placeholder="用户名" v-model="username">
      <input type="password" placeholder="密码" v-model="password">
      <button v-on:click="login">登录</button>
      <span v-on:click="ToRegister">没有账号？马上注册</span>
    </div>

    <div class="register-wrap" v-show="showRegister">
      <h3>注册</h3>
      <p v-show="showPrompt">{{prompt}}</p>
      <input type="text" placeholder="NJU邮箱" v-model="email">
      <input type="text" placeholder="姓名" v-model="newUsername">
      <input type="password" placeholder="密码" v-model="newPassword">
      <input type="password" placeholder="确认密码" v-model="newPasswordConfirm">
      <button v-on:click="register">注册</button>
      <span v-on:click="ToLogin">已有账号？马上登录</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'login.vue',
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
  }

  input {
    display: block;
    width: 250px;
    height: 40px;
    line-height: 40px;
    margin: 0 auto;
    margin-bottom: 10px;
    outline: none;
    border: 1px solid #888;
    padding: 10px;
    box-sizing: border-box;
  }

  p {
    color: red;
  }

  button {
    display: block;
    width: 250px;
    height: 40px;
    line-height: 40px;
    margin: 0 auto;
    border: none;
    background-color: #41b883;
    color: #fff;
    font-size: 16px;
    margin-bottom: 5px;
  }

  span {
    cursor: pointer;
  }

  span:hover {
    color: #41b883;
  }
</style>
