<!-- 用户登录页面 -->
<template>
  <div id="paper">
    <div class="login-shell" v-loading="loading">
      <section class="brand-panel" aria-label="平台标识">
        <div class="brand-decoration brand-decoration-top"></div>
        <div class="brand-decoration brand-decoration-bottom"></div>
        <img src="@/assets/img/icon/wenzi.png" class="brand-logo" alt="云教中枢" />
      </section>

      <section class="form-panel">
        <el-form ref="loginForm" :model="loginForm" :rules="rules" class="login-container" label-position="left"
          label-width="0px" @keyup.enter="login">
          <div class="login-heading">
            <h3 class="login_title">系统登录</h3>
            <p class="login-subtitle">欢迎回来，请登录您的账号</p>
          </div>
          <el-form-item prop="username">
            <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="请输入账号"
              prefix-icon="el-icon-user" clearable></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="请输入密码"
              prefix-icon="el-icon-lock" clearable show-password></el-input>
          </el-form-item>
          <el-checkbox class="login_remember" v-model="checked" label-position="left">
            <span>记住我</span>
          </el-checkbox>
          <el-form-item class="login-actions">
            <div class="login-button-row">
              <el-button type="primary" class="login-btn" @click="login" :disabled="loading">
                登录
              </el-button>
              <router-link to="register" class="register-link">
                <el-button class="register-btn">
                  注册
                </el-button>
              </router-link>
            </div>
          </el-form-item>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      rules: {
        username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
        password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
      },
      checked: false,
      loginForm: {
        username: '',
        password: ''
      },
      loading: false
    }
  },
  mounted() {
    // 读取记住的账号密码
    this.getRememberedUserInfo()
  },
  methods: {
    // 读取记住的账号密码
    getRememberedUserInfo() {
      const rememberedUser = localStorage.getItem('rememberedUser')
      if (rememberedUser) {
        const { username, password } = JSON.parse(rememberedUser)
        this.loginForm.username = username
        this.loginForm.password = password
        this.checked = true
      }
    },
    // 登录逻辑（适配Shiro会话 + 多表查询用户角色）
    async login() {
      // 1. 表单校验
      this.$refs.loginForm.validate(async (valid) => {
        if (!valid) return

        // 2. 开启加载状态
        this.loading = true

        try {
          // 3. 发起登录请求
          const loginResp = await this.$axios.post('/login', {
            username: this.loginForm.username,
            password: this.loginForm.password
          })

          if (loginResp.data.code !== 200) {
            this.$notify({
              title: '提示',
              message: this.$createElement('i', { style: 'color: teal' }, loginResp.data.message)
            })
            this.loading = false
            return
          }

          // 4. 登录成功：获取用户名，开始多表查询角色
          const username = loginResp.data.data.username
          this.$store.commit('login', username) // 存储用户名到Vuex，存储的键值为login,username
          localStorage.setItem('login', username)//存储账户名 存储键值对为login username


          // 5. 记住密码逻辑
          if (this.checked) {
            localStorage.setItem('rememberedUser', JSON.stringify({
              username: this.loginForm.username,
              password: this.loginForm.password
            }))
          } else {
            localStorage.removeItem('rememberedUser')
          }

          // 6. 登录成功跳转
          const redirectPath = this.$route.query.redirect || '/admin/my/index'
          const redirectQuery = {}
          if (this.$route.query.question) {
            redirectQuery.question = this.$route.query.question
          }

          this.$router.push({
            path: redirectPath,
            query: redirectQuery
          })

          // 7. 根据username账号查询用户ID
          const userResp = await this.$axios.post('/admin/user/select', {
            username: username // 后端@RequestBody接收User对象，传username字段
          })

          if (userResp.data.code !== 200 || !userResp.data.data || !userResp.data.data.user) {
            throw new Error('查询用户信息失败')
          }
          const userId = userResp.data.data.user.id // 从返回的user对象中取id
          const studentNo = userResp.data.data.user.studentNo // 从返回的user对象中取studentNo

          // 8.根据用户ID查询角色ID（admin_user_role表）
          const userRoleResp = await this.$axios.get(`/admin/user/getRidByUid?uid=${userId}`)
          if (userRoleResp.data.code !== 200 || !userRoleResp.data.data) {
            throw new Error('查询用户角色关联失败')
          }
          const roleId = userRoleResp.data.data.rid // 获取角色rid

          // 9. 根据角色ID查询角色名称（role表）
          const roleResp = await this.$axios.get(`/admin/user/getNameByRid?rid=${roleId}`)
          if (roleResp.data.code !== 200 || !roleResp.data.data) {
            throw new Error('查询角色信息失败')
          }
          const roleName = roleResp.data.data.name // 获取角色name（如teacher/student/admin）

          // 10. 存储角色信息到localStorage
          localStorage.setItem('userRole', roleName)//存储角色名 继续存储userRole roleName
          localStorage.setItem('userId', userId) // 存储用户ID 继续存储 userId userId
          localStorage.setItem('studentNo', studentNo) // 存储学号 studentNo

          // 11. 存储用户信息到Vuex，以供问卷检查使用
          this.$store.commit('setUser', {
            id: userId,
            username: username,
            name: userResp.data.data.user.name,
            role: roleName
          })

          // 12. 检查用户是否需要填写问卷（仅学生身份检查）
          if (roleName === 'student') {
            try {
              const profileCheckResp = await this.$axios.get(`/ai/profile/need-fill/${userId}`)
              if (profileCheckResp.data.code === 200 && profileCheckResp.data.data.needFill) {
                // 需要填写问卷，显示问卷弹窗
                this.$nextTick(() => {
                  this.$root.$emit('showProfileForm')
                })
              }
            } catch (error) {
              console.error('检查问卷状态失败：', error)
            }
          }

          this.$message.success(`登录成功！当前登录角色为：${roleName}`)

        } catch (error) {
          // 统一错误处理（无变化）
          this.loading = false
          console.error('登录/角色查询失败：', error)
          if (error.response) {
            this.$message.error(`操作失败：${(error.response && error.response.data && error.response.data.message) || '服务器异常'}`)
          } else {
            this.$message.error(error.message || '登录失败，请检查账号密码或网络！')
          }
        } finally {
          this.loading = false
        }
      })
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
  padding: 32px;
  box-sizing: border-box;
  overflow-y: auto;
}

.login-shell {
  width: 100%;
  max-width: 980px;
  min-height: 610px;
  display: grid;
  grid-template-columns: 48% 52%;
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
  padding: 48px;
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
  max-height: 430px;
  object-fit: contain;
  filter: drop-shadow(0 16px 24px rgba(56, 112, 164, 0.14));
}

.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 54px 58px;
  background: #ffffff;
}

.login-container {
  width: 100%;
  max-width: 390px;
  padding: 0;
  background: transparent;
  box-sizing: border-box;
}

.login-heading {
  margin-bottom: 34px;
}

.login_title {
  margin: 0 0 10px;
  color: #1e3a55;
  font-size: 30px;
  font-weight: 600;
  line-height: 1.3;
  letter-spacing: 2px;
}

.login-subtitle {
  margin: 0;
  color: #7b94aa;
  font-size: 14px;
  line-height: 1.6;
}

.login-container >>> .el-input__inner {
  height: 48px;
  padding-left: 42px;
  border-color: #d8e7f4;
  border-radius: 10px;
  background: #f8fbfe;
  color: #1e3a55;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.login-container >>> .el-input__prefix {
  left: 13px;
  color: #8aa8c1;
}

.login-container >>> .el-input__inner:focus {
  border-color: #72b5f3;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(96, 169, 235, 0.13);
}

.login-container >>> .el-form-item {
  margin-bottom: 22px;
}

.login_remember {
  margin: 0 0 30px;
  text-align: left;
  color: #607b91;
}

.login-actions {
  width: 100%;
  margin-bottom: 0;
}

.login-actions >>> .el-form-item__content {
  width: 100%;
}

.login-button-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  width: 100%;
}

.login-btn {
  flex: 0 0 120px;
  width: 120px;
  height: 46px;
  margin: 0;
  border: none !important;
  border-radius: 10px;
  background: linear-gradient(135deg, #4a9fee, #2f80ed) !important;
  box-shadow: 0 10px 22px rgba(47, 128, 237, 0.22);
  font-size: 16px;
  letter-spacing: 3px;
}

.register-link {
  flex: 0 0 120px;
  width: 120px;
  text-decoration: none;
  display: inline-block;
}

.register-btn {
  width: 100%;
  height: 46px;
  color: #2f80ed !important;
  border: 1px solid #9dcbf6 !important;
  border-radius: 10px;
  background: #f7fbff !important;
  font-size: 16px;
  letter-spacing: 3px;
}

.register-btn:hover,
.register-btn:focus {
  color: #1f6fd1 !important;
  border-color: #72b5f3 !important;
  background: #edf7ff !important;
}

/* 响应式适配 */
@media (max-width: 768px) {
  #paper {
    align-items: center;
    padding: 18px;
  }

  .login-shell {
    grid-template-columns: 1fr;
    min-height: auto;
    max-width: 480px;
    border-radius: 22px;
  }

  .brand-panel {
    min-height: 180px;
    padding: 24px;
  }

  .brand-panel::after {
    inset: 14px;
    border-radius: 16px;
  }

  .brand-logo {
    max-width: 180px;
    max-height: 132px;
  }

  .form-panel {
    padding: 36px 34px 40px;
  }

  .login-heading {
    margin-bottom: 28px;
  }

  .login_title {
    font-size: 26px;
  }

}

@media (max-width: 480px) {
  #paper {
    padding: 12px;
  }

  .login-shell {
    border-radius: 18px;
  }

  .brand-panel {
    min-height: 150px;
    padding: 20px;
  }

  .brand-logo {
    max-width: 150px;
    max-height: 108px;
  }

  .form-panel {
    padding: 30px 22px 32px;
  }

  .login_title {
    font-size: 24px;
  }

  .login_remember {
    margin-bottom: 24px;
    font-size: 14px;
  }

  .login-btn,
  .register-btn {
    font-size: 14px;
  }

}

@media (max-width: 360px) {
  .login-btn,
  .register-link {
    flex-basis: 112px;
    width: 112px;
  }
}

@media (max-width: 320px) {
  .login-button-row {
    flex-direction: column;
  }

  .login-btn,
  .register-link {
    width: 112px !important;
    flex-basis: auto !important;
  }

  .register-btn {
    width: 112px !important;
  }
}
</style>
