<!-- 点击添加用户按钮显示的模态框的组件 -->
<template>
  <div style="text-align: left">
    <el-button class="add-button" type="success" @click="dialogFormVisible = true">添加用户</el-button>
    <el-dialog title="添加用户" :visible.sync="dialogFormVisible" @close="clear" width="25%">
      <el-form :model="loginForm" :rules="rules" label-position="left" label-width="80px" ref="editForm">
        <el-form-item prop="username" label="账号">
          <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="账号"></el-input>
        </el-form-item>
        <el-form-item prop="password" label="密码">
          <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="密码"></el-input>
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
        <el-form-item style="width: 100%">
          <el-button type="primary" style="width: 40%; background: #505458; border: none" v-on:click="register">添加
          </el-button>
          <el-button type="primary" style="width: 40%; background: #505458; border: none" v-on:click="clear">重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'BulkRegistration',
  data () {
    return {
      dialogFormVisible: false,
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
      loginForm: {
        username: '',
        password: '',
        name: '',
        rname: 'student',
        phone: '',
        email: ''
      }
    }
  },
  methods: {
    clear () {
      this.loginForm = {
        username: '',
        password: '',
        name: '',
        rname: 'student',
        phone: '',
        email: ''
      }
      this.$refs.editForm.resetFields()
    },
    register () {
      let requestUser = {
        username: this.loginForm.username,
        password: this.loginForm.password,
        name: this.loginForm.name,
        rname: this.loginForm.rname,
        phone: this.loginForm.phone,
        email: this.loginForm.email
      }
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          this.$axios
            .post('/register', requestUser)
            .then((resp) => {
              if (resp.data.code === 200) {
                this.$alert('注册成功', '提示', {
                  confirmButtonText: '确定'
                })
                this.clear()
                this.$emit('onSubmit')
              } else {
                this.$alert(resp.data.message, '提示', {
                  confirmButtonText: '确定'
                })
              }
            })
            .catch((failResponse) => {})
        }
      })
    }
  }
}
</script>

<style scoped>
.add-button {
  margin: 18px 0 0 10px;
}
</style>
