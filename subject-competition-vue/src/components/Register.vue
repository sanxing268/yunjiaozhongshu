<!-- 用户注册页面 -->
<template>
  <div id="paper">
    <div class="register-shell" v-loading="loading">
      <section class="brand-panel" aria-label="平台标识">
        <div class="brand-decoration brand-decoration-top"></div>
        <div class="brand-decoration brand-decoration-bottom"></div>
        <img src="@/assets/img/icon/wenzi.png" class="brand-logo" alt="云教中枢" />
      </section>

      <section class="form-panel">
        <el-form :model="loginForm" :rules="rules" class="login-container" label-position="left" ref="ruleForm"
          label-width="80px">
          <h3 class="login_title">用户注册</h3>
          <el-form-item prop="username" label="账号">
            <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="账号"></el-input>
          </el-form-item>
          <el-form-item prop="password" label="密码">
            <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="密码"
              prefix-icon="el-icon-lock" clearable show-password></el-input>
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input type="text" v-model="loginForm.name" auto-complete="off" placeholder="真实姓名"></el-input>
          </el-form-item>
          <el-form-item label="用户角色" prop="rname">
            <el-radio-group v-model="loginForm.rname" size="medium">
              <el-radio border label="student"></el-radio>
              <el-radio border label="teacher"></el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="电话号码" prop="phone">
            <el-input type="text" v-model="loginForm.phone" auto-complete="off" placeholder="电话号码"></el-input>
          </el-form-item>
          <el-form-item label="E-Mail" prop="email">
            <el-input type="text" v-model="loginForm.email" auto-complete="off" placeholder="E-Mail"></el-input>
          </el-form-item>
          <el-form-item class="register-actions">
            <el-button type="primary" class="register-btn" v-on:click="register">注册</el-button>
            <el-button type="primary" class="reset-btn" v-on:click="clear">重置</el-button>
          </el-form-item>
        </el-form>
      </section>
    </div>
  </div>
</template>
<script>
export default {
  data () {
    return {
      rules: {
        username: [
          { required: true, message: '用户名不能为空', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '密码不能为空', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'change' },
          { min: 7, max: 11, message: '长度在7~11个字符', trigger: 'blur' },
          {
            pattern: /^1[3|5|7|8|9]\d{9}$/,
            message: '请输入正确的号码格式',

            trigger: 'change'
          }
        ],
        email: [
         { required: true, message: '请输入邮箱', trigger: 'blur' },
          { pattern: /^([0-9A-Za-z\-_\\.]+)@([0-9a-z]+\.[a-z]{2,3}(\.[a-z]{2})?)$/g,
            message: '请输入正确的邮箱',
            trigger: 'blur'}
        ]
      },
      checked: true,
      loginForm: {
        username: '',
        password: '',
        name: '',
        rname: 'student',
        phone: '',
        email: ''
      },
      loading: false
    }
  },
  methods: {
    register () {
      var _this = this
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.$axios
            .post('/register', {
              username: this.loginForm.username,
              password: this.loginForm.password,
              name: this.loginForm.name,
              rname: this.loginForm.rname,
              phone: this.loginForm.phone,
              email: this.loginForm.email
            })
            .then((resp) => {
              if (resp.data.code === 200) {
                this.$alert('注册成功，请联系审核员进行审核，审核通过后即可登录。', '注册成功', {
                  confirmButtonText: '我知道了',
                  type: 'success'
                }).then(() => {
                  _this.$router.replace('/login')
                }).catch(() => {
                  _this.$router.replace('/login')
                })
              } else {
                this.$alert(resp.data.message, '提示', {
                  confirmButtonText: '确定'
                })
              }
            })
            .catch((failResponse) => {})
        }
      })
    },
    clear () {
      this.loginForm = {
        username: '',
        password: '',
        name: '',
        rname: 'student',
        phone: '',
        email: ''
      }
    }
  }
}
</script>
<style scoped>
#paper {
  background:
    radial-gradient(circle at 15% 15%, rgba(255, 255, 255, 0.9) 0, rgba(255, 255, 255, 0) 32%),
    linear-gradient(135deg, #d9edff 0%, #eef8ff 48%, #cfe7ff 100%);
  height: 100vh;
  width: 100%;
  position: fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28px;
  box-sizing: border-box;
  overflow-y: auto;
}

.register-shell {
  width: 100%;
  max-width: 1060px;
  min-height: 690px;
  display: grid;
  grid-template-columns: 42% 58%;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.85);
  border-radius: 28px;
  background: #ffffff;
  box-shadow: 0 24px 70px rgba(46, 104, 158, 0.2);
}

.brand-panel {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  padding: 46px;
  background: linear-gradient(145deg, #c4e3ff 0%, #e6f4ff 55%, #b8dcff 100%);
}

.brand-panel::after {
  content: "";
  position: absolute;
  inset: 24px;
  border: 1px solid rgba(255, 255, 255, 0.65);
  border-radius: 22px;
}

.brand-decoration {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.38);
}

.brand-decoration-top {
  top: -90px;
  right: -100px;
  width: 280px;
  height: 280px;
}

.brand-decoration-bottom {
  bottom: -120px;
  left: -105px;
  width: 320px;
  height: 320px;
  background: rgba(137, 195, 249, 0.22);
}

.brand-logo {
  position: relative;
  z-index: 1;
  display: block;
  width: auto;
  max-width: 78%;
  max-height: 450px;
  object-fit: contain;
  filter: drop-shadow(0 16px 24px rgba(56, 112, 164, 0.14));
}

.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 42px 56px;
  background: #ffffff;
}

.login-container {
  width: 100%;
  max-width: 470px;
  padding: 0;
  background: transparent;
  box-sizing: border-box;
}

.login_title {
  margin: 0 0 28px;
  text-align: left;
  color: #1e3a55;
  font-size: 30px;
  font-weight: 600;
  line-height: 1.3;
  letter-spacing: 2px;
}

.login-container >>> .el-form-item {
  margin-bottom: 18px;
}

.login-container >>> .el-form-item__label {
  color: #536f87;
  font-weight: 500;
  line-height: 44px;
}

.login-container >>> .el-input__inner {
  height: 44px;
  border-color: #d8e7f4;
  border-radius: 9px;
  background: #f8fbfe;
  color: #1e3a55;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.login-container >>> .el-input__inner:focus {
  border-color: #72b5f3;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(96, 169, 235, 0.13);
}

.login-container >>> .el-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.login-container >>> .el-radio {
  margin-right: 0;
  border-color: #d8e7f4;
  border-radius: 9px;
  background: #f8fbfe;
}

.login-container >>> .el-radio.is-bordered.is-checked {
  border-color: #72b5f3;
  background: #eef7ff;
}

.register-actions {
  margin-top: 6px;
  margin-bottom: 0 !important;
}

.register-actions >>> .el-form-item__content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
}

.register-btn {
  flex: 0 0 120px;
  width: 120px;
  height: 46px;
  border: none !important;
  border-radius: 10px;
  background: linear-gradient(135deg, #4a9fee, #2f80ed) !important;
  box-shadow: 0 10px 22px rgba(47, 128, 237, 0.22);
  font-size: 16px;
  letter-spacing: 3px;
}

.reset-btn {
  flex: 0 0 120px;
  width: 120px;
  height: 46px;
  margin-left: 0 !important;
  color: #2f80ed !important;
  border: 1px solid #9dcbf6 !important;
  border-radius: 10px;
  background: #f7fbff !important;
  font-size: 16px;
  letter-spacing: 3px;
}

.reset-btn:hover,
.reset-btn:focus {
  color: #1f6fd1 !important;
  border-color: #72b5f3 !important;
  background: #edf7ff !important;
}

/* 响应式适配 */
@media (max-width: 768px) {
  #paper {
    align-items: flex-start;
    padding: 18px;
  }

  .register-shell {
    grid-template-columns: 1fr;
    min-height: auto;
    max-width: 500px;
    border-radius: 22px;
  }

  .brand-panel {
    min-height: 170px;
    padding: 22px;
  }

  .brand-panel::after {
    inset: 14px;
    border-radius: 16px;
  }

  .brand-logo {
    max-width: 165px;
    max-height: 120px;
  }

  .form-panel {
    padding: 34px 34px 38px;
  }

  .login_title {
    font-size: 26px;
    margin-bottom: 24px;
  }

  .login-container >>> .el-form-item {
    margin-bottom: 15px;
  }

  .login-container >>> .el-radio {
    margin-bottom: 0;
  }
}

@media (max-width: 480px) {
  #paper {
    padding: 12px;
  }

  .register-shell {
    border-radius: 18px;
  }

  .brand-panel {
    min-height: 145px;
    padding: 18px;
  }

  .brand-logo {
    max-width: 140px;
    max-height: 102px;
  }

  .form-panel {
    padding: 28px 20px 30px;
  }

  .login_title {
    font-size: 24px;
    margin-bottom: 20px;
  }

  .register-btn,
  .reset-btn {
    font-size: 14px;
  }

  .login-container >>> .el-form-item {
    margin-bottom: 13px;
  }

  .login-container >>> .el-form-item__label {
    font-size: 13px;
    padding-right: 8px;
    width: 72px !important;
  }

  .login-container >>> .el-input__inner {
    font-size: 14px;
  }

  .login-container >>> .el-radio {
    font-size: 13px;
  }
}

@media (max-width: 360px) {
  .form-panel {
    padding: 24px 14px 26px;
  }

  .login_title {
    font-size: 22px;
    margin-bottom: 18px;
  }

  .login-container >>> .el-form-item__label {
    font-size: 12px;
    width: 68px !important;
  }

  .register-btn,
  .reset-btn {
    flex-basis: 112px;
    width: 112px;
  }
}

@media (max-width: 320px) {
  .register-actions >>> .el-form-item__content {
    flex-direction: column;
    gap: 10px;
  }

  .register-btn,
  .reset-btn {
    flex: 0 0 112px;
    width: 112px !important;
    max-width: 100%;
    margin-left: 0 !important;
  }
}
</style>
