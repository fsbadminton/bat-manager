<template>
  <div>
    <el-card class="login-form-layout">
      <el-form autoComplete="on"
               :model="loginForm"
               :rules="loginRules"
               ref="loginForm"
               label-position="left">
        <div style="text-align: center">
          <svg-icon icon-class="login-mall" style="width: 56px;height: 56px;color: #409EFF"></svg-icon>
        </div>
        <h2 class="login-title color-main">mall-admin-web</h2>
        <el-form-item prop="username">
          <el-input name="username"
                    type="text"
                    v-model="loginForm.username"
                    autoComplete="on"
                    placeholder="请输入用户名">
          <span slot="prefix">
            <svg-icon icon-class="user" class="color-main"></svg-icon>
          </span>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input name="password"
                    :type="pwdType"
                    @keyup.enter.native="handleLogin"
                    v-model="loginForm.password"
                    autoComplete="on"
                    placeholder="请输入密码">
          <span slot="prefix">
            <svg-icon icon-class="password" class="color-main"></svg-icon>
          </span>
            <span slot="suffix" @click="showPwd">
            <svg-icon icon-class="eye" class="color-main"></svg-icon>
          </span>
          </el-input>
        </el-form-item>
        <el-form-item style="margin-bottom: 60px;text-align: center">
          <el-button style="width: 45%" type="primary" :loading="loading" @click.native.prevent="handleLogin">
            登录
          </el-button>
          <el-button style="width: 45%; margin-left: 10px" @click.native.prevent="openRegister">
            注册
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <img :src="login_center_bg" class="login-center-layout">
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
        <el-button @click="registerDialogVisible=false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="submitRegister">注册</el-button>
      </span>
    </el-dialog>
    
  </div>
</template>

<script>
  import {isvalidUsername} from '@/utils/validate';
  import {setSupport,getSupport,setCookie,getCookie} from '@/utils/support';
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
      };
      const validatePass = (rule, value, callback) => {
        if (value.length < 3) {
          callback(new Error('密码不能小于3位'))
        } else {
          callback()
        }
      };
      return {
        loginForm: {
          username: '',
          password: '',
        },
        registerForm: {
          username: '',
          password: '',
          email: ''
        },
        loginRules: {
          username: [{required: true, trigger: 'blur', validator: validateUsername}],
          password: [{required: true, trigger: 'blur', validator: validatePass}]
        },
        loading: false,
        registerLoading: false,
        pwdType: 'password',
        login_center_bg,
        dialogVisible:false,
        supportDialogVisible:false,
        registerDialogVisible:false
      }
    },
    created() {
      this.loginForm.username = getCookie("username");
      this.loginForm.password = getCookie("password");
      if(this.loginForm.username === undefined||this.loginForm.username==null||this.loginForm.username===''){
        this.loginForm.username = 'admin';
      }
      if(this.loginForm.password === undefined||this.loginForm.password==null){
        this.loginForm.password = '';
      }
    },
    methods: {
      showPwd() {
        if (this.pwdType === 'password') {
          this.pwdType = ''
        } else {
          this.pwdType = 'password'
        }
      },
      handleLogin() {
        this.$refs.loginForm.validate(valid => {
          if (valid) {
            // let isSupport = getSupport();
            // if(isSupport===undefined||isSupport==null){
            //   this.dialogVisible =true;
            //   return;
            // }
            this.loading = true;
            this.$store.dispatch('Login', this.loginForm).then((res) => {
              this.loading = false;
              const data = res && res.data ? res.data : res;
              const payload = (data && data.data) ? data.data : data;
              const roleRaw = payload.role || payload.userRole || payload.authority || '';
              const role = typeof roleRaw === 'string' ? roleRaw.toLowerCase() : roleRaw;
              const token = payload.token;
              let dest = '/';
              // 管理员直接跳转到球拍列表页（管理端常用入口）
              if (role === 'admin') dest = '/pms/product';
              else if (role === 'user') dest = '/user';
              else if (token === 'admin-token') dest = '/pms/product';
              else if (token === 'user-token') dest = '/user';
              else if (this.loginForm.username === 'admin') dest = '/pms/product';
              else dest = '/user';

              const roleUpper = (role && typeof role==='string') ? role.toUpperCase() : (this.loginForm.username.toLowerCase()==='admin' ? 'ADMIN' : 'USER');
              this.$store.dispatch('GenerateRoutes', { menus: [], username: this.loginForm.username, roles: [roleUpper] }).then(() => {
                this.$router.addRoutes(this.$store.getters.addRouters);
                this.$router.push({ path: dest })
              });
              // setCookie("username",this.loginForm.username,15);
              // setCookie("password",this.loginForm.password,15);
              // this.$router.push({path: '/'})
            }).catch(() => {
              this.loading = false
            })
          } else {
            console.log('参数验证不合法！');
            return false
          }
        })
      },
      openRegister(){
        this.registerDialogVisible = true
      },
      submitRegister(){
        if(!this.registerForm.username||!this.registerForm.password){
          this.$message({type:'warning',message:'请输入用户名和密码',duration:1500})
          return
        }
        this.registerLoading = true
        request({url:'/auth/register',method:'post',data:this.registerForm}).then(()=>{
          this.registerLoading = false
          this.$message({type:'success',message:'注册成功',duration:1500})
          this.registerDialogVisible = false
          this.loginForm.username = this.registerForm.username
          this.loginForm.password = this.registerForm.password
        }).catch(()=>{
          this.registerLoading = false
        })
      },
      handleTry(){
        this.dialogVisible =true
      },
      dialogConfirm(){
        this.dialogVisible =false;
        setSupport(true);
      },
      dialogCancel(){
        this.dialogVisible = false;
        setSupport(false);
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
    border-top: 10px solid #409EFF;
  }

  .login-title {
    text-align: center;
  }

  .login-center-layout {
    background: #409EFF;
    width: auto;
    height: auto;
    max-width: 100%;
    max-height: 100%;
    margin-top: 200px;
  }
</style>
