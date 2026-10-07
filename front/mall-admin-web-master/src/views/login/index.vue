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
        <h2 class="login-title color-main">{{ isClientLogin ? '球拍商城' : '球拍管理后台' }}</h2>

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
          <el-button v-if="isClientLogin" style="width: 45%; margin-left: 10px" @click.native.prevent="openRegister">
            注册
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <img :src="login_center_bg" class="login-center-layout" />

    <el-dialog title="用户注册" :visible.sync="registerDialogVisible" width="420px" @close="resetRegisterForm">
      <el-form ref="registerForm" :model="registerForm" :rules="registerRules" label-position="left" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码（至少6位）" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-input v-model="registerForm.code" placeholder="请输入验证码" style="width: 55%" />
          <el-button
            :disabled="codeBtnDisabled"
            style="margin-left: 8px; width: 38%"
            @click="sendCode"
          >{{ codeBtnText }}</el-button>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="registerDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="registerLoading"
          :disabled="!registerFormComplete"
          @click="submitRegister"
        >注册</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { isvalidUsername } from '@/utils/validate'
import { getCookie } from '@/utils/support'
import login_center_bg from '@/assets/images/login_center_bg.png'
import request from '@/utils/request'

const EMAIL_REGEX = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/

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
    const validateRegEmail = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入邮箱'))
      } else if (!EMAIL_REGEX.test(value)) {
        callback(new Error('邮箱格式不正确'))
      } else {
        callback()
      }
    }

    return {
      loginForm: { username: '', password: '' },
      registerForm: { username: '', password: '', email: '', code: '' },
      loginRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePass }]
      },
      registerRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }],
        email: [{ required: true, validator: validateRegEmail, trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      },
      loading: false,
      registerLoading: false,
      pwdType: 'password',
      login_center_bg,
      registerDialogVisible: false,
      codeBtnText: '获取验证码',
      codeBtnDisabled: false,
      codeCountdown: 0,
      countdownTimer: null
    }
  },
  computed: {
    isClientLogin() {
      return this.loginRole === 'USER'
    },
    loginRole() {
      return String((this.$route.meta && this.$route.meta.role) || 'USER').toUpperCase()
    },
    registerFormComplete() {
      const f = this.registerForm
      return !!(f.username && f.password && f.email && f.code)
    }
  },
  created() {
    this.loginForm.username = getCookie('username')
    this.loginForm.password = getCookie('password')
    if (!this.loginForm.password) this.loginForm.password = ''
  },
  beforeDestroy() {
    if (this.countdownTimer) clearInterval(this.countdownTimer)
  },
  methods: {
    showPwd() {
      this.pwdType = this.pwdType === 'password' ? '' : 'password'
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return false
        this.loading = true
        this.$store
          .dispatch('Login', { ...this.loginForm, role: this.loginRole })
          .then(res => {
            this.loading = false
            const data = res && res.data ? res.data : res
            const payload = data && data.data ? data.data : data
            const role = String(payload.role || '').toUpperCase()
            const dest = role === 'ADMIN' ? '/admin/pms/product' : '/client/products'
            this.$router.push({ path: dest })
          })
          .catch(() => { this.loading = false })
      })
    },
    openRegister() {
      this.registerDialogVisible = true
    },
    resetRegisterForm() {
      this.registerForm = { username: '', password: '', email: '', code: '' }
      if (this.$refs.registerForm) this.$refs.registerForm.clearValidate()
      if (this.countdownTimer) clearInterval(this.countdownTimer)
      this.codeBtnText = '获取验证码'
      this.codeBtnDisabled = false
    },
    sendCode() {
      const email = this.registerForm.email
      if (!EMAIL_REGEX.test(email)) {
        this.$message({ type: 'warning', message: '请输入正确的邮箱地址', duration: 2000 })
        return
      }
      request({ url: '/user/sendCode', method: 'post', data: { email } })
        .then(() => {
          this.$message({ type: 'success', message: '验证码已发送，请查看后端控制台', duration: 3000 })
          this.startCountdown()
        })
        .catch(() => {})
    },
    startCountdown() {
      this.codeCountdown = 60
      this.codeBtnDisabled = true
      this.codeBtnText = `${this.codeCountdown}s后重试`
      this.countdownTimer = setInterval(() => {
        this.codeCountdown--
        if (this.codeCountdown <= 0) {
          clearInterval(this.countdownTimer)
          this.codeBtnDisabled = false
          this.codeBtnText = '获取验证码'
        } else {
          this.codeBtnText = `${this.codeCountdown}s后重试`
        }
      }, 1000)
    },
    submitRegister() {
      this.$refs.registerForm.validate(valid => {
        if (!valid) return
        this.registerLoading = true
        request({ url: '/user/register', method: 'post', data: this.registerForm })
          .then(() => {
            this.registerLoading = false
            this.$message({ type: 'success', message: '注册成功，请登录', duration: 2000 })
            this.registerDialogVisible = false
            this.loginForm.username = this.registerForm.username
            this.loginForm.password = this.registerForm.password
          })
          .catch(() => { this.registerLoading = false })
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
