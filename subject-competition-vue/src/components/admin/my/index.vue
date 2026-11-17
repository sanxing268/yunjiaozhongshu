<!-- 账号资料组件 -->
<template>
  <div>
    <el-row style="margin: 18px 0px 0px 18px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>设置</el-breadcrumb-item>
        <el-breadcrumb-item>账号资料</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <password @onSubmit="Repassword()" ref="edit"></password>
    <information @click.native.prevent="updateUser(userlist)"  @onSubmit="getUser()" ref="editupdate"></information>
    <div style="float: left; margin: 20px; width: 100%">
      <el-form :model="userlist">
        <el-col :span="8" hidden>
          <el-input v-model="userlist.id" hidden></el-input>
<!--          <el-input v-model="userlist.enabled" hidden></el-input>-->
        </el-col>
        <el-form-item label="用户名" prop="username" width="100">
          <el-col :span="8">
            <el-input v-model="userlist.username" disabled></el-input
          ></el-col>
        </el-form-item>
        <el-form-item label="真实姓名" prop="name" width="100">
          <el-col :span="8">
            <el-input v-model="userlist.name" disabled></el-input
          ></el-col>
        </el-form-item>
        <el-form-item label="电话" prop="phone" width="100">
          <el-col :span="8">
            <el-input v-model="userlist.phone" disabled></el-input
          ></el-col>
        </el-form-item>
        <el-form-item label="邮箱" prop="email" width="100">
          <el-col :span="8">
            <el-input v-model="userlist.email" disabled></el-input
          ></el-col>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import Password from './password'
import Information from './information'
export default {
  name: 'index',
  components: { Password, Information },
  data () {
    return {
      userlist: {}, // 用户信息表单
      user: {
        id: '',
        username: '',
        name: '',
        phone: '',
        email: '',
        enabled: ''
      }
    }
  },
  mounted () {
    this.getUser()
  },

  methods: {
    // 获取个人用户的信息
    getUser () {
      var _this = this
      let username = window.localStorage.getItem('username')
      let userInfo = {
        username: JSON.parse(username)
      }
      this.$axios.post('/admin/user/select', userInfo).then((resp) => {
        if (resp && resp.data.code === 200) {
          _this.userlist = resp.data.data.user
        }
      })
    },
    updateUser (user) {
      this.$refs.editupdate.editForm = {
        id: user.id,
        username: user.username,
        name: user.name,
        phone: user.phone,
        email: user.email,
        enabled: user.enabled
      }
    }
  }
}
</script>
