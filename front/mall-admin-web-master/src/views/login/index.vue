<template>
  <div>
    <el-card class="login-form-layout">
      <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        label-position="left"
        auto-complete="on"
      >
        <div style="text-align: center">
          <svg-icon icon-class="login-mall" style="width: 56px; height: 56px; color: #409eff" />
        </div>
        <h2 class="login-title color-main">mall-admin-web</h2>

        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            name="username"
            type="text"
            auto-complete="on"
            placeholder="请输入用户名"
          >
            <span slot="prefix">
              <svg-icon icon-class="user" class="color-main" />
            </span>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            name="password"
            :type="pwdType"
            auto-complete="on"
            placeholder="请输入密码"
            @keyup.enter.native="handleLogin"
          >
            <span slot="prefix">
              <svg-icon icon-class="password" class="color-main" />
            </span>
            <span slot="suffix" @click="showPwd">
              <svg-icon icon-class="eye" class="color-main" />
            </span>
          </el-input>
        </el-form-item>

        <el-form-item style="margin-bottom: 60px; text-align: center">
          <el-button style="width: 45%" type="primary" :loading="loading" @click.native.prevent="handleLogin">
            登录
          </el-button>
          <el-button style="width: 45%; margin-left: 10px" @click.native.prevent="openRegister">
            注册
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <img :src="login_center_bg" class="login-center-layout" />

    <el-dialog title="用户注册" :visible.sync="registerDialogVisible" width="400px">
      <el-form :model="registerForm" label-position="left" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="registerForm.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="registerForm.password" type="password" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="registerForm.email" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="registerDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="submitRegister">注册</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { isvalidUsername } from '@/utils/validate'
import { getCookie } from '@/utils/support'
import login_center_bg from '@/assets/images/login_center_bg.png'
import request from '@/utils/request'

export default {
  name: 'login',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!isvalidUsername(value)) {
        callback(new Error('请输入正确的用户名'))
      } else {
        callback()
      }
    }
    const validatePass = (rule, value, callback) => {
      if (!value || value.length < 3) {
        callback(new Error('密码不能小于3位'))
      } else {
        callback()
      }
    }

    return {
      loginForm: {
        username: '',
        password: ''
      },
      registerForm: {
        username: '',
        password: '',
        email: ''
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePass }]
      },
      loading: false,
      registerLoading: false,
      pwdType: 'password',
      login_center_bg,
      registerDialogVisible: false
    }
  },
  created() {
    this.loginForm.username = getCookie('username')
    this.loginForm.password = getCookie('password')

    if (this.loginForm.username === undefined || this.loginForm.username === null || this.loginForm.username === '') {
      this.loginForm.username = 'admin'
    }
    if (this.loginForm.password === undefined || this.loginForm.password === null) {
      this.loginForm.password = ''
    }
  },
  methods: {
    showPwd() {
      this.pwdType = this.pwdType === 'password' ? '' : 'password'
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) {
          return false
        }
        this.loading = true
        this.$store
          .dispatch('Login', this.loginForm)
          .then(res => {
            this.loading = false
            const data = res && res.data ? res.data : res
            const payload = data && data.data ? data.data : data
            const roleRaw = payload.role || payload.userRole || payload.authority || ''
            const role = typeof roleRaw === 'string' ? roleRaw.toLowerCase() : roleRaw

            let dest = '/'
            if (role === 'admin') {
              dest = '/pms/product'
            } else if (role === 'user') {
              dest = '/user'
            } else if (String(this.loginForm.username).toLowerCase() === 'admin') {
              dest = '/pms/product'
            } else {
              dest = '/user'
            }

            // 登录页只负责确定目标首页，动态路由统一由全局守卫按端身份注入
            this.$router.push({ path: dest })
          })
          .catch(() => {
            this.loading = false
          })
      })
    },
    openRegister() {
      this.registerDialogVisible = true
    },
    submitRegister() {
      if (!this.registerForm.username || !this.registerForm.password) {
        this.$message({ type: 'warning', message: '请输入用户名和密码', duration: 1500 })
        return
      }
      this.registerLoading = true
      request({ url: '/auth/register', method: 'post', data: this.registerForm })
        .then(() => {
          this.registerLoading = false
          this.$message({ type: 'success', message: '注册成功', duration: 1500 })
          this.registerDialogVisible = false
          this.loginForm.username = this.registerForm.username
          this.loginForm.password = this.registerForm.password
        })
        .catch(() => {
          this.registerLoading = false
        })
    }
  }
}
</script>

<style scoped>
.login-form-layout {
  position: absolute;
  left: 0;
  right: 0;
  width: 360px;
  margin: 140px auto;
  border-top: 10px solid #409eff;
}

.login-title {
  text-align: center;
}

.login-center-layout {
  background: #409eff;
  width: auto;
  height: auto;
  max-width: 100%;
  max-height: 100%;
  margin-top: 200px;
}
</style>
