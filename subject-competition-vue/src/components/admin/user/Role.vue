<!-- 点击角色配置页面的编辑按钮后显示的模态框的组件 -->
<template>
  <div>
    <el-dialog title="修改角色信息" :visible.sync="dialogFormVisible">
      <el-form v-model="selectedRole" style="text-align: left" ref="dataForm">
        <el-form-item label="角色名" label-width="120px" prop="username">
          <el-input v-model="selectedRole.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="角色描述" label-width="120px" prop="name">
          <el-input v-model="selectedRole.nameZh" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="功能配置" label-width="120px" prop="perms">
          <el-checkbox-group v-model="selectedPermsIds">
            <el-checkbox v-for="(perm, i) in perms" :key="i" :label="perm.id">{{ perm.description }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="菜单配置" label-width="120px" prop="menus">
          <el-tree :data="menus" :props="props" show-checkbox :default-expand-all="isExpand" node-key="id" ref="tree">
          </el-tree>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit(selectedRole)">确 定</el-button>
      </div>
    </el-dialog>
    <el-row style="margin: 18px 0px 0px 18px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>用户管理</el-breadcrumb-item>
        <el-breadcrumb-item>角色配置</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <role-create @onSubmit="listRoles()"></role-create>
    <!--工具条-->
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px">
      <el-form :inline="true" :model="filters" @submit.native.prevent>
        <el-form-item>
          <el-input v-model="filters.name" placeholder="角色名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.nameZh" placeholder="角色描述"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-on:click="listRoles">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <el-card style="margin: 18px 2%; width: 95%">
      <el-table :data="roles" border ref="multipleTable" @selection-change="selsChange" style="width: 100%"
        :cell-style="{ fontWeight: '700' }">
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column prop="id" sortable label="id" width="100"> </el-table-column>
        <el-table-column prop="name" label="角色名" fit> </el-table-column>
        <el-table-column prop="nameZh" label="角色描述" fit> </el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.enabled" active-color="#13ce66" inactive-color="#ff4949"
              @change="(value) => commitStatusChange(value, scope.row)">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="editRole(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="removeRole(scope.row)">移除</el-button>
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
import RoleCreate from './RoleCreate'

export default {
  name: 'UserRole',
  components: { RoleCreate },
  data() {
    return {
      filters: {
        name: '',
        nameZh: ''
      },
      isExpand: true,
      sels: [], // 列表选中列
      total: 0,
      pageSize: 10, // 每页数量
      page: 1, // 当前页
      dialogFormVisible: false,
      roles: [],
      perms: [],
      menus: [],
      selectedRole: {},
      selectedPermsIds: [],
      selectedMenusIds: [],
      props: {
        id: 'id',
        label: 'nameZh',
        children: 'children'
      }
    }
  },
  mounted() {
    this.listRoles()
    this.listPerms()
    this.listMenus()
  },
  methods: {
    listRoles() {
      let params = {
        name: this.filters.name,
        nameZh: this.filters.nameZh
      }
      var _this = this
      this.$axios
        .post('/admin/role/' + this.pageSize + '/1', params)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.roles = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    // 单个删除角色
    removeRole(role) {
      this.$confirm('确认删除该角色吗？', '提示', {
        type: 'warning'
      })
        .then(() => {
          const ids = [role.id]
          this.$axios.post('/admin/role/batchDelete', ids)
            .then((res) => {
              if (res.data.code === 200) {
                this.$message.success(res.data.message)
                this.listRoles()
              } else {
                this.$message.error(res.data.message)
              }
            })
            .catch((error) => {
              console.log('error--->', error)
              this.$message.error('删除失败')
            })
        })
        .catch(() => {
          // 取消
        })
    },
    handleCurrentChange(page) {
      let params = {
        name: this.filters.name,
        nameZh: this.filters.nameZh
      }
      var _this = this
      this.$axios
        .post('/admin/role/' + this.pageSize + '/' + page, params)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.roles = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    listPerms() {
      var _this = this
      this.$axios.get('/admin/role/perm').then((resp) => {
        if (resp && resp.data.code === 200) {
          _this.perms = resp.data.data.list
        }
      })
    },
    listMenus() {
      var _this = this
      this.$axios.get('/admin/role/menu/list').then((resp) => {
        if (resp && resp.data.code === 200) {
          _this.menus = resp.data.data.list
        }
      })
    },
    commitStatusChange(value, role) {
      if (role.id !== 1) {
        this.$confirm('是否更改角色状态？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            this.$axios
              .put('/admin/role/status', {
                enabled: value,
                id: role.id
              })
              .then((resp) => {
                if (resp && resp.data.code === 200) {
                  if (value) {
                    this.$message('角色 [' + role.nameZh + '] 已启用')
                  } else {
                    this.$message('角色 [' + role.nameZh + '] 已禁用')
                  }
                }
              })
          })
          .catch(() => {
            role.enabled = !role.enabled
            this.$message({
              type: 'info',
              message: '已取消'
            })
          })
      } else {
        role.enabled = true
        this.$alert('无法禁用系统管理员！')
      }
    },
    editRole(role) {
      this.dialogFormVisible = true
      this.selectedRole = role
      let permIds = []
      // 获取角色对应的权限项id
      permIds = role.perms.map((item) => item.id)
      this.selectedPermsIds = permIds
      // 处理菜单项,父子节点
      this.$nextTick(() => {
        const arr = []
        role.menus.forEach((item) => {
          if (
            !this.$refs.tree.getNode(item.id).childNodes ||
            !this.$refs.tree.getNode(item.id).childNodes.length
          ) {
            arr.push(item.id)
          }
          item.children.forEach((item) => {
            if (
              !this.$refs.tree.getNode(item.id).childNodes ||
              !this.$refs.tree.getNode(item.id).childNodes.length
            ) {
              arr.push(item.id)
            }
          })
        })
        this.$refs.tree.setCheckedKeys(arr)
      })
    },
    onSubmit(role) {
      if (!role || !role.id) {
        this.$message.error('角色ID不能为空，请重新选择角色！')
        return // 终止请求
      }
      let _this = this
      // 根据视图绑定的角色 id 向后端传送角色信息
      let perms = []
      for (let i = 0; i < _this.selectedPermsIds.length; i++) {
        for (let j = 0; j < _this.perms.length; j++) {
          if (_this.selectedPermsIds[i] === _this.perms[j].id) {
            perms.push(_this.perms[j].id)
          }
        }
      }
      // 获取所有选中的节点
      let menusIds = this.$refs.tree.getCheckedKeys()
      let menusPIds = this.$refs.tree.getHalfCheckedKeys()
      // 合并父子节点 menusIds.concat(menusPIds)
      let roleInfo = {
        id: role.id,
        name: role.name,
        nameZh: role.nameZh,
        enabled: role.enabled,
        permsId: perms,
        menusIds: menusIds.concat(menusPIds)
      }
      this.$axios.put('/admin/role', roleInfo).then((resp) => {
        if (resp && resp.data.code === 200) {
          this.$message.success(resp.data.message)
          this.listRoles()
          this.listPerms()
          this.listMenus()
          this.dialogFormVisible = false
        }
      })
    },
    // 批量删除
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
              .post('/admin/role/batchDelete', ids)
              .then(function (res) {
                if (res.data.code === 200) {
                  _this.listRoles()
                  _this.$message(res.data.message)
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
    // 全选单选多选
    selsChange(sels) {
      this.sels = sels
    },
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

<style scoped>
.add-button {
  float: left;
  margin: 18px 0 18px 10px;
}
</style>
