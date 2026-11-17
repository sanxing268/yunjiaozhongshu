<!-- 个人信息编辑组件 -->
<template>
  <div style="float: left">
    <el-button class="add-button" type="success" @click="dialogFormVisible = true">信息编辑</el-button>
    <el-dialog title="个人信息编辑" :visible.sync="dialogFormVisible">
      <el-form :model="editForm" label-width="100px" :rules="rules" ref="ruleForm">
        <el-form-item prop="id">
          <el-input type="hidden" v-model="editForm.id" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" autocomplete="off" disabled></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="name">
          <el-input v-model="editForm.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="editForm.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">提交</el-button>
          <el-button @click="dialogFormVisible = false">取 消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
  export default {
    inject: ['reload'],
    name: 'information',
    data () {
      return {
        dialogFormVisible: false,
        editForm: {
          id: '',
          username: '',
          name: '',
          phone: '',
          email: '',
          enabled: ''
        },
        rules: {
          username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
          name: [{required: true, message: '请输入真实姓名', trigger: 'blur'}],
          phone: [{required: true, message: '请输入手机号', trigger: 'blur'}],
          email: [{required: true, message: '请输入邮箱地址', trigger: 'blur'},
            {type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur'}]
        }
      }
    },
    methods: {
      onSubmit () {
        let userInfo = {
          id: this.editForm.id,
          username: this.editForm.username,
          name: this.editForm.name,
          phone: this.editForm.phone,
          email: this.editForm.email,
          enabled: this.editForm.enabled
        }
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            this.$axios.put('/admin/user/update', userInfo).then((resp) => {
                if (resp && resp.data.code === 200) {
                  this.dialogFormVisible = false
                  this.$notify.success({
                    title: '成功',
                    message: resp.data.message
                  })
                  this.reload() // 页面刷新
                } else {
                  this.$notify.error({
                    title: '错误',
                    message: resp.data.message
                  })
                }
              })
          } else {
            console.log('error submit!!')
            return false
          }
        })
      }
    }
  }
</script>

<style scoped>
  .add-button {
    margin: 18px 0 20px 10px;
  }
</style>
