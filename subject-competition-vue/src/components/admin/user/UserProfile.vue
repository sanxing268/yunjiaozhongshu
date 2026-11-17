<!-- 点击用户信息页面下的编辑按钮显示后显示的模态框的组件 以及用户信息列表页面-->
<template>
  <div>
    <el-dialog title="修改用户信息" :visible.sync="dialogFormVisible">
      <el-form v-model="selectedUser" style="text-align: left" ref="dataForm">
        <el-form-item label="用户名" label-width="120px" prop="username">
          <label>{{ selectedUser.username }}</label>
        </el-form-item>
        <el-form-item label="真实姓名" label-width="120px" prop="name">
          <el-input v-model="selectedUser.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="手机号" label-width="120px" prop="phone">
          <el-input v-model="selectedUser.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" label-width="120px" prop="email">
          <el-input v-model="selectedUser.email" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="密码" label-width="120px" prop="password">
          <el-button type="warning" @click="resetPassword(selectedUser.username)">重置密码</el-button>
        </el-form-item>
        <el-form-item label="角色分配" label-width="120px" prop="roles">
          <el-checkbox-group v-model="selectedRolesIds">
            <el-checkbox v-for="(role, i) in roles" :key="i" :label="role.id">{{ role.nameZh }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit(selectedUser)">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 导航条 -->
    <el-row style="margin: 18px 0px 0px 18px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>用户管理</el-breadcrumb-item>
        <el-breadcrumb-item>用户信息</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <!-- 查询用户条 -->
    <bulk-registration @onSubmit="listUsers()"></bulk-registration>
    <!--工具条-->
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px">
      <el-form :inline="true" :model="filters" @submit.native.prevent>
        <el-form-item>
          <el-input v-model="filters.username" placeholder="用户名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.phone" placeholder="手机号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-on:click="listUsers">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <!-- 用户信息列表页面 -->
    <el-card style="margin: 18px 2%; width: 95%">
      <el-table :data="users" stripe border ref="multipleTable" @selection-change="selsChange"
        :default-sort="{ prop: 'id', order: 'ascending' }" style="width: 100%" :cell-style="{ fontWeight: '700' }">
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column prop="id" label="id" sortable width="100">
        </el-table-column>
        <el-table-column prop="username" label="用户名" fit> </el-table-column>
        <el-table-column prop="name" label="真实姓名" fit> </el-table-column>
        <el-table-column prop="createTime" label="创建日期" fit>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" fit> </el-table-column>
        <el-table-column prop="email" label="邮箱" show-overflow-tooltip fit>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.enabled" active-color="#13ce66" inactive-color="#ff4949"
              @change="(value) => commitStatusChange(value, scope.row)">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button @click="editUser(scope.row)" type="text" size="small">编辑
            </el-button>
            <el-button @click.native.prevent="singleDel(value, scope.row)" type="text" size="small">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin: 20px 0 20px 0; float: left">
        <el-button @click="toggleSelection()">取消选择</el-button>
        <el-button type="danger" @click="batchRemove" :disabled="this.sels.length === 0">批量删除</el-button>
      </div>
      <!--分页工具-->
      <div style="margin: 20px 0 50px 0">
        <el-pagination background style="float: right" layout="total, prev, pager, next, jumper"
          @current-change="handleCurrentChange" :page-size="pageSize" :total="total">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import BulkRegistration from './BulkRegistration'

export default {
  name: 'UserProfile',
  components: { BulkRegistration },
  data() {
    return {
      filters: {
        username: '',
        phone: '',
        email: ''
      },
      total: 0,
      pageSize: 10, // 每页数量
      page: 1, // 当前页
      sels: [], // 列表选中列
      users: [],
      roles: [],
      dialogFormVisible: false,
      selectedUser: [],
      selectedRolesIds: []
    }
  },
  mounted() {
    this.listUsers()
    this.listRoles()
  },
  // 定义该页面操作请求的后端路径
  methods: {
    // 查询用户列表
    listUsers: function () {
      let user = {
        username: this.filters.username,
        phone: this.filters.phone,
        email: this.filters.email
      }
      var _this = this
      this.$axios
        .post('/admin/user/' + this.pageSize + '/1', user)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.users = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    // 处理页面切换
    handleCurrentChange(page) {
      let user = {
        username: this.filters.username,
        phone: this.filters.phone,
        email: this.filters.email
      }
      var _this = this
      this.$axios
        .post('/admin/user/' + this.pageSize + '/' + page, user)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.users = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    // 加载角色配置
    listRoles() {
      var _this = this
      this.$axios.get('/admin/rolelList').then((resp) => {
        if (resp && resp.data.code === 200) {
          _this.roles = resp.data.data.list
        }
      })
    },

    // 修改用户启用禁用功能
    commitStatusChange(value, user) {
      let requestUser = {
        enabled: value,
        username: user.username,
        id: user.id
      }
      if (user.username !== 'admin') {
        this.$axios.put('/admin/user/status', requestUser).then((resp) => {
          if (resp && resp.data.code === 200) {
            if (value) {
              this.$message('用户 [' + user.username + '] 已启用')
            } else {
              this.$message('用户 [' + user.username + '] 已禁用')
            }
          }
        })
      } else {
        user.enabled = true
        this.$alert('不能禁用管理员账户')
      }
    },
    // 提交用户编辑表单
    onSubmit(user) {
      console.log('提交用户编辑表单', user)
      let _this = this
      // 根据视图绑定的角色 id 向后端传送角色信息
      let roles = []
      for (let i = 0; i < _this.selectedRolesIds.length; i++) {
        for (let j = 0; j < _this.roles.length; j++) {
          if (_this.selectedRolesIds[i] === _this.roles[j].id) {
            roles.push(_this.roles[j].id)
          }
        }
      }
      let requestUser = {
        id: user.id,
        username: user.username,
        name: user.name,
        phone: user.phone,
        email: user.email,
        enabled: user.enabled,
        roleIds: roles
      }
      this.$axios.put('/admin/user/edit', requestUser).then((resp) => {
        if (resp && resp.data.code === 200) {
          this.$message.success(resp.data.message)
          this.dialogFormVisible = false
          // 修改角色后重新请求用户信息，实现视图更新
          this.listUsers()
        } else {
          this.$message.error(resp.data.message)
        }
      })
    },
    // 打开用户编辑弹窗，回显初始化表单数据
    editUser(user) {
      console.log("点击编辑按钮时", user)
      this.dialogFormVisible = true
      this.selectedUser = user
      let roleIds = []
      for (let i = 0; i < user.roles.length; i++) {
        roleIds.push(user.roles[i].id)
      }
      this.selectedRolesIds = roleIds
    },
    resetPassword(username) {
      this.$axios
        .put('/admin/user/password', {
          username: username,
          password: 123456
        })
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            this.$alert('密码已重置为 123456')
          }
        })
    },
    // 单个删除用户
    singleDel(value, user) {
      let _this = this
      _this
        .$confirm('确认移除编号为' + user.id + '记录吗?', '提示', {
          type: 'warning'
        })
        .then(() => {
          if (user.username !== 'admin') {
            _this.$axios.delete('/admin/user/' + user.id).then((resp) => {
              if (resp && resp.data.code === 200) {
                _this.$message(resp.data.message)
                _this.listUsers()
              } else {
                _this.$message(resp.data.message)
              }
            })
          } else {
            user.enabled = true
            this.$alert('不能删除管理员账户')
          }
        })
    },
    // 批量删除用户
    batchRemove: function () {
      var _this = this
      this.$confirm('确认删除选中记录吗？', '提示', {
        type: 'warning'
      })
        .then(() => {
          let ids = []
          _this.sels.forEach((obj) => {
            ids.push(obj.id)
          })
          if (this.sels.map((item) => item.id).toString() !== undefined) {
            _this.$axios
              .post('/admin/user/batchDelete', ids)
              .then(function (res) {
                if (res.data.code === 200) {
                  _this.listUsers()
                  _this.$message.success(res.data.message)
                } else {
                  _this.$message.error(res.data.message)
                }
              })
              .catch((error) => {
                console.log('error--->', error)
                _this.$message.error('删除失败')
              })
          }
        })
        .catch(() => {
          console.log('cancel')
        })
    },
    // 选择状态改变时的回调函数
    selsChange(sels) {
      this.sels = sels
    },
    // 手动控制选择状态
    toggleSelection(rows) {
      if (rows) {
        rows.forEach((row) => {
          this.$refs.multipleTable.toggleRowSelection(row)
        })
      } else {
        this.$refs.multipleTable.clearSelection()
      }
    }
  }
}
</script>

<style scoped></style>
