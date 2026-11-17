<!-- 学生管理组件 用于管理员与教师账号 -->
<template>
  <div class="student-manage-wrap">
<!-- 面包屑导航 -->
      <el-row style="margin: 10px 0px 0px 3px">
            <el-breadcrumb separator-class="el-icon-arrow-right">
                <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
                <el-breadcrumb-item>用户管理</el-breadcrumb-item>
                <el-breadcrumb-item>学生管理</el-breadcrumb-item>
            </el-breadcrumb>
        </el-row>

    <!-- 页面标题 -->
    <div class="page-header">
      <el-button type="success" @click="openStudentDialog(true)">新增学生</el-button>
    </div>

    <!-- 搜索框 -->
    <el-form :model="searchForm" inline class="search-form" @submit.prevent="getStudentList">
      <el-form-item label="学生姓名">
        <el-input v-model="searchForm.name" placeholder="请输入学生姓名" clearable @keyup.enter.native="getStudentList" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable @keyup.enter.native="getStudentList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getStudentList">查询</el-button>
        <el-button @click="resetSearchForm">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 学生列表表格 -->
    <el-table v-loading="loading" :data="studentList" border stripe :fit="true" style="width: 100%; margin-top: 10px">
      <el-table-column prop="id" label="用户ID" align="center" width="120" />
      <el-table-column prop="username" label="账号" align="center" width="150" />
      <el-table-column prop="name" label="学生姓名" align="center" width="120" />
      <el-table-column prop="phone" label="手机号" align="center" width="130" />
      <el-table-column prop="email" label="邮箱" align="center" />
      <el-table-column prop="createTime" label="创建时间" align="center" width="180">
        <template slot-scope="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="enabled" label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag
            :type="(scope.row.enabled === true || scope.row.enabled === 'true' || scope.row.enabled === 1) ? 'success' : 'danger'">
            {{ (scope.row.enabled === true || scope.row.enabled === 'true' || scope.row.enabled === 1) ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180">
        <template slot-scope="scope">
          <el-button type="text" @click="openStudentDialog(false, scope.row)">编辑</el-button>
          <el-button type="text" style="color: #f56c6c" @click="deleteStudent(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页组件 -->
    <div class="pagination-wrap" style="margin-top: 20px; text-align: right;">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="pagination.currentPage" :page-sizes="[1,10, 20, 50, 100]" :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="pagination.total">
      </el-pagination>
    </div>

    <!-- 新增编辑复用的学生弹窗 -->
    <el-dialog :visible.sync="studentDialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form :model="studentForm" label-width="80px" :rules="studentRules" ref="studentFormRef">
        <el-form-item label="账号" prop="username">
          <!-- 新增时账号可编辑，编辑时账号禁用 -->
          <el-input v-model="studentForm.username" placeholder="请输入登录账号" :disabled="!isAdd" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="studentForm.name" placeholder="请输入学生姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="studentForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="studentForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="studentForm.password" type="password" placeholder="请输入密码（编辑可不填）" />
        </el-form-item>
        <el-form-item label="状态" prop="enabled">
          <el-select v-model="studentForm.enabled" placeholder="请选择学生状态">
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="studentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStudent">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from '../../../utils/axios'
import { Message, MessageBox } from 'element-ui'

export default {
  name: 'StudentManage',
  data() {
    return {
      loading: false,
      searchForm: {
        name: '',
        phone: ''
      },
      studentList: [],
      // 原始完整列表（用于分页截取，不直接渲染）
      totalStudentList: [],
      // 分页核心变量
      pagination: {
        currentPage: 1, // 当前页码
        pageSize: 10,   // 设置默认每页显示条数
        total: 0        // 总记录数
      },
      //原始数据
      studentDialogVisible: false,
      isAdd: true,
      studentForm: {
        id: '',
        username: '',
        name: '',
        phone: '',
        email: '',
        password: '',
        enabled: '1'
      },
      // 统一表单规则
      studentRules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        name: [{ required: true, message: '请输入学生姓名', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入有效的手机号', trigger: 'blur' },
          {
            len: 11,
            message: '手机号必须为11位数字',
            trigger: 'blur'
          },
          //手机号格式正则校验
          {
            pattern: /^1[3-9]\d{9}$/,
            message: '手机号格式不正确',
            trigger: 'blur'
          }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        enabled: [{ required: true, message: '请选择学生状态', trigger: 'change' }]
      }
    }
  },
  computed: {
    // 动态计算弹窗标题
    dialogTitle() {
      return this.isAdd ? '新增学生' : '编辑学生'
    }
  },
  mounted() {
    this.getStudentList()
  },
  methods: {

    // 切换每页条数
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.pagination.currentPage = 1 // 切换每页条数时重置为第1页
      this.getStudentList() // 重新查询并分页
    },

    // 切换当前页码
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      // 直接分页截取
      this.studentList = this.totalStudentList.slice(
        (val - 1) * this.pagination.pageSize,
        val * this.pagination.pageSize
      )
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      try {
        const date = new Date(dateStr)
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
      } catch (error) {
        return dateStr
      }
    },

    // 查询学生列表
    async getStudentList() {
      this.loading = true
      try {
        const res = await axios.get('/api/admin/student/list')
        if (res.data.code === 200) {
          let list = res.data.data.studentList
          // 1. 前端筛选
          if (this.searchForm.name) {
            list = list.filter(item => item.name && item.name.includes(this.searchForm.name))
          }
          if (this.searchForm.phone) {
            list = list.filter(item => item.phone && item.phone.includes(this.searchForm.phone))
          }
          // 2. 保存完整筛选后的列表
          this.totalStudentList = JSON.parse(JSON.stringify(list))
          // 3. 更新总条数
          this.pagination.total = this.totalStudentList.length
          // 4. 分页截取数据
          this.studentList = this.totalStudentList.slice(
            (this.pagination.currentPage - 1) * this.pagination.pageSize,
            this.pagination.currentPage * this.pagination.pageSize
          )
        } else {
          Message.error('获取学生列表失败：' + res.data.message)
        }
      } catch (error) {
        console.error('获取学生列表异常：', error)
        Message.error('获取学生列表失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },

    // 重置搜索表单
    resetSearchForm() {
      this.searchForm.name = ''
      this.searchForm.phone = ''
      this.pagination.currentPage = 1 // 重置页码为1
      this.getStudentList()
    },

    // isAdd（是否新增），student（编辑时的学生数据）
    openStudentDialog(isAdd, student = null) {
      this.isAdd = isAdd // 设置模式
      this.studentDialogVisible = true // 打开弹窗

      // 重置表单数据和校验状态
      this.$nextTick(() => {
        if (this.$refs.studentFormRef) {
          this.$refs.studentFormRef.clearValidate() // 清除校验提示
        }

        // 初始化表单
        this.studentForm = {
          id: '',
          username: '',
          name: '',
          phone: '',
          email: '',
          password: '',
          enabled: '1' // 默认启用
        }

        // 编辑模式：赋值学生数据
        if (!isAdd && student) {
          this.studentForm = {
            id: student.id || '',
            username: student.username || '',
            name: student.name || '',
            phone: student.phone || '',
            email: student.email || '',
            password: '', // 编辑时密码默认留空
            enabled: (student.enabled === true || student.enabled === 'true' || student.enabled === 1) ? '1' : '0'
          }

          // 编辑模式：修改密码规则为非必填
          this.studentRules.password = []
        } else {
          // 新增模式：恢复密码必填规则
          this.studentRules.password = [{ required: true, message: '新增时必须输入密码，请输入', trigger: 'blur' }]
        }
      })
    },

    // 统一提交表单
    async submitStudent() {
      try {
        // 表单校验
        await this.$refs.studentFormRef.validate()
        this.loading = true

        let res
        // 新增模式：调用新增接口
        if (this.isAdd) {
          res = await axios.post('/api/admin/student/add', this.studentForm)
        } else {
          // 编辑模式：调用修改接口
          res = await axios.put('/api/admin/student/update', this.studentForm)
        }

        if (res.data.code === 200) {
          Message.success(res.data.message)
          this.studentDialogVisible = false // 关闭弹窗
          this.pagination.currentPage = 1 // 新增/编辑成功后重置页码为1
          this.getStudentList() // 刷新列表
        } else {
          Message.error(this.isAdd ? '新增学生失败：' : '修改学生失败：' + res.data.message)
        }
      } catch (error) {
        if (error.name !== 'ValidationError') {
          console.error('提交学生信息异常：', error)
          Message.error(this.isAdd ? '新增学生失败,必填项不合法' : '修改学生失败，必填项不合法')
        }
      } finally {
        this.loading = false
      }
    },

    // 删除学生
    async deleteStudent(student) {
      if (!student.id) {
        Message.warning('学生ID不能为空')
        return
      }
      try {
        await MessageBox.confirm(
          '确定要删除该学生吗？删除后无法恢复！',
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        this.loading = true
        const res = await axios.delete(`/api/admin/student/delete/${student.id}`)
        if (res.data.code === 200) {
          Message.success(res.data.message)
          this.pagination.currentPage = 1 // 删除成功后重置页码为1
          this.getStudentList()
        } else {
          Message.error('删除学生失败：' + res.data.message)
        }
      } catch (error) {
        if (error.type !== 'cancel') {

        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.student-manage-wrap {
  width: 100%;
  box-sizing: border-box;
  padding: 20px;
  background-color: #fff;
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
  margin-top: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
  font-weight: 600;
}

.search-form {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
  margin-bottom: 10px;
}
</style>