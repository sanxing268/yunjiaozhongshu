<!-- 教师管理组件 用于管理员账号 -->
<template>
  <div class="professor-manage-wrap">
    <!-- 面包屑导航 -->
      <el-row style="margin: 10px 0px 0px 3px">
            <el-breadcrumb separator-class="el-icon-arrow-right">
                <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
                <el-breadcrumb-item>用户管理</el-breadcrumb-item>
                <el-breadcrumb-item>教师管理</el-breadcrumb-item>
            </el-breadcrumb>
        </el-row>
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-buttons">
        <el-button type="success" @click="openTeacherDialog(true)">新增教师</el-button>
        <el-button type="success" @click="openAIRecognitionDialog">大模型自动识别</el-button>
      </div>
    </div>

    <!-- 搜索框 -->
    <el-form :model="searchForm" inline class="search-form" @submit.prevent="getProfessorList">
      <el-form-item label="教师姓名">
        <el-input v-model="searchForm.name" placeholder="请输入教师姓名" clearable @keyup.enter.native="getProfessorList" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable @keyup.enter.native="getProfessorList"
          oninput="value = value.replace(/[^0-9]/g, '')" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getProfessorList">查询</el-button>
        <el-button @click="resetSearchForm">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 教师列表表格 -->
    <el-table v-loading="loading" :data="professorList" border stripe :fit="true" style="width: 100%; margin-top: 10px">
      <el-table-column prop="id" label="用户ID" align="center" width="120" />
      <el-table-column prop="username" label="账号" align="center" width="150" />
      <el-table-column prop="name" label="教师姓名" align="center" width="120" />
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
          <el-button type="text" @click="openTeacherDialog(false, scope.row)">编辑</el-button>
          <el-button type="text" style="color: #f56c6c" @click="deleteProfessor(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <div class="pagination-wrap" style="margin-top: 20px; text-align: right;">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="pagination.currentPage" :page-sizes="[1,10, 20, 50, 100]" :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="pagination.total">
      </el-pagination>
    </div>

    <!-- 新增/编辑教师弹窗 -->
    <el-dialog :visible.sync="teacherDialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form :model="teacherForm" label-width="80px" :rules="teacherRules" ref="teacherFormRef">
        <el-form-item label="账号" prop="username">
          <el-input v-model="teacherForm.username" placeholder="请输入登录账号" :disabled="!isAdd" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="teacherForm.name" placeholder="请输入教师姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="teacherForm.phone" placeholder="请输入11位有效手机号"
            oninput="value = value.replace(/[^0-9]/g, '')" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="teacherForm.email" placeholder="请输入有效的邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="teacherForm.password" type="password" placeholder="请输入密码（编辑可不填）" />
        </el-form-item>
        <el-form-item label="状态" prop="enabled">
          <el-select v-model="teacherForm.enabled" placeholder="请选择教师状态">
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="teacherDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTeacher">提交</el-button>
      </div>
    </el-dialog>

    <!-- 大模型图像识别弹窗 -->
    <el-dialog :visible.sync="aiRecognitionVisible" title="大模型自动识别教师信息" width="900px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="上传图片">
          <el-upload
            ref="aiUpload"
            action="/api/admin/teacher/recognize"
            :auto-upload="false"
            :on-success="handleRecognitionSuccess"
            :on-error="handleRecognitionError"
            :limit="1"
            accept="image/*">
            <el-button slot="trigger" type="primary">选择图片</el-button>
            <span slot="tip" class="el-upload__tip">支持jpg、jpeg、png、gif等图片格式</span>
          </el-upload>
        </el-form-item>
        <el-form-item label="识别结果">
          <el-table :data="recognitionResults" border v-if="recognitionResults.length > 0">
            <el-table-column prop="name" label="教师姓名" width="120">
              <template slot-scope="scope">
                <el-input v-model="scope.row.name" size="small" placeholder="姓名" />
              </template>
            </el-table-column>
            <el-table-column prop="phone" label="手机号" width="120">
              <template slot-scope="scope">
                <el-input v-model="scope.row.phone" size="small" placeholder="手机号" />
              </template>
            </el-table-column>
            <el-table-column prop="email" label="邮箱" min-width="150">
              <template slot-scope="scope">
                <el-input v-model="scope.row.email" size="small" placeholder="邮箱" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button type="text" @click="addRecognizedTeacher(scope.row)">单独添加</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-else style="text-align: center; padding: 20px; color: #ccc;">
            还未识别信息
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="aiRecognitionVisible = false">关闭</el-button>
        <el-button type="primary" @click="submitRecognition" v-if="recognitionResults.length === 0">开始识别</el-button>
        <el-button type="success" @click="batchAddRecognizedTeachers" v-if="recognitionResults.length > 0" :loading="batchLoading">
          一键批量添加 ({{ recognitionResults.length }})
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from '../../../utils/axios'
import { Message, MessageBox } from 'element-ui'

export default {
  name: 'ProfessorManage',
  data() {
    return {
      loading: false,
      batchLoading: false,
      searchForm: {
        name: '',
        phone: ''
      },
      professorList: [],
      // 统一弹窗变量
      teacherDialogVisible: false,
      // 大模型识别弹窗
      aiRecognitionVisible: false,
      recognitionResults: [],
      // 原始完整列表（用于分页截取）
      totalProfessorList: [],
      // 分页核心变量
      pagination: {
        currentPage: 1, // 当前页码
        pageSize: 10,   // 设置默认每页显示条数
        total: 0        // 总记录数
      },
      isAdd: true, // true=新增，false=编辑
      teacherForm: {
        id: '',
        username: '',
        name: '',
        phone: '',
        email: '',
        password: '',
        enabled: '1' // 默认启用
      },
      // 表单校验规则
      teacherRules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        name: [{ required: true, message: '请输入教师姓名', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { len: 11, message: '手机号必须为11位数字', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        enabled: [{ required: true, message: '请选择教师状态', trigger: 'change' }]
      }
    }
  },
  computed: {
    // 动态弹窗标题
    dialogTitle() {
      return this.isAdd ? '新增教师' : '编辑教师'
    }
  },
  mounted() {
    this.getProfessorList()
  },
  methods: {

    // 切换每页条数
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.pagination.currentPage = 1 // 重置为第1页
      this.getProfessorList()
    },

    // 切换当前页码
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      // 直接分页截取
      this.professorList = this.totalProfessorList.slice(
        (val - 1) * this.pagination.pageSize,
        val * this.pagination.pageSize
      )
    },
    // 格式化时间
    formatDate(dateStr) {
      if (!dateStr) return ''
      try {
        const date = new Date(dateStr)
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
      } catch (error) {
        return dateStr
      }
    },

    // 1. 查询教师列表
    async getProfessorList() {
      this.loading = true
      try {
        // 对接后端接口
        const res = await axios.get('/api/admin/teacher/list')
        if (res.data.code === 200) {
          let list = res.data.data.teacherList
          // 前端筛选
          if (this.searchForm.name) {
            list = list.filter(item => item.name && item.name.includes(this.searchForm.name))
          }
          if (this.searchForm.phone) {
            list = list.filter(item => item.phone && item.phone.includes(this.searchForm.phone))
          }
          // 保存完整筛选后的列表
          this.totalProfessorList = JSON.parse(JSON.stringify(list))
          // 更新总条数
          this.pagination.total = this.totalProfessorList.length
          // 分页截取数据
          this.professorList = this.totalProfessorList.slice(
            (this.pagination.currentPage - 1) * this.pagination.pageSize,
            this.pagination.currentPage * this.pagination.pageSize
          )
        } else {
          Message.error('获取教师列表失败：' + res.data.message)
        }
      } catch (error) {
        console.error('获取教师列表异常：', error)
        Message.error('获取教师列表失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },

    // 重置搜索表单
    resetSearchForm() {
      this.searchForm.name = ''
      this.searchForm.phone = ''
      this.pagination.currentPage = 1
      this.getProfessorList()
    },

    // 2. 打开新增/编辑弹窗
    openTeacherDialog(isAdd, teacher = null) {
      this.isAdd = isAdd
      this.teacherDialogVisible = true

      this.$nextTick(() => {
        // 重置表单校验
        if (this.$refs.teacherFormRef) {
          this.$refs.teacherFormRef.clearValidate()
        }

        // 初始化表单
        this.teacherForm = {
          id: '',
          username: '',
          name: '',
          phone: '',
          email: '',
          password: '',
          enabled: '1'
        }

        // 编辑模式：回显数据
        if (!isAdd && teacher) {
          this.teacherForm = {
            id: teacher.id || '',
            username: teacher.username || '',
            name: teacher.name || '',
            phone: teacher.phone || '',
            email: teacher.email || '',
            password: '', // 编辑时密码可选填
            enabled: (teacher.enabled === true || teacher.enabled === 'true' || teacher.enabled === 1) ? '1' : '0'
          }
          // 编辑模式：密码非必填
          this.teacherRules.password = []
        } else {
          // 新增模式：密码必填
          this.teacherRules.password = [{ required: true, message: '新增时必须输入密码，请输入', trigger: 'blur' }]
        }
      })
    },

    // 3. 提交新增/编辑教师
    async submitTeacher() {
      try {
        await this.$refs.teacherFormRef.validate()
        this.loading = true

        let res
        // 新增教师：调用POST接口
        if (this.isAdd) {
          res = await axios.post('/api/admin/teacher/add', this.teacherForm)
        } else {
          // 编辑教师：调用PUT接口
          res = await axios.put('/api/admin/teacher/update', this.teacherForm)
        }

        if (res.data.code === 200) {
          Message.success(res.data.message)
          this.teacherDialogVisible = false
          this.pagination.currentPage = 1 // 重置页码为1
          this.getProfessorList() // 刷新列表
        } else {
          Message.error(this.isAdd ? '新增教师失败：' : '修改教师失败：' + res.data.message)
        }
      } catch (error) {
        if (error.name !== 'ValidationError') {
          console.error('提交教师信息异常：', error)
          Message.error(this.isAdd ? '新增教师失败' : '修改教师失败')
        }
      } finally {
        this.loading = false
      }
    },

    // 4. 删除教师
    async deleteProfessor(teacher) {
      if (!teacher.id) {
        Message.warning('教师ID不能为空')
        return
      }
      try {
        await MessageBox.confirm(
          '确定要删除该教师吗？删除后无法恢复！',
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        this.loading = true
        // 调用删除接口
        const res = await axios.delete(`/api/admin/teacher/delete/${teacher.id}`)
        if (res.data.code === 200) {
          Message.success(res.data.message)
          this.pagination.currentPage = 1 // 重置页码为1
          this.getProfessorList() // 刷新列表
        } else {
          Message.error('删除教师失败：' + res.data.message)
        }
      } catch (error) {
        if (error.type !== 'cancel') {

        }
      } finally {
        this.loading = false
      }
    },

    // 5. 打开大模型识别弹窗
    openAIRecognitionDialog() {
      this.aiRecognitionVisible = true
      this.recognitionResults = []
    },

    // 6. 提交识别请求
    submitRecognition() {
      const files = this.$refs.aiUpload.uploadFiles
      if (!files || files.length === 0) {
        Message.warning('请先选择图片')
        return
      }
      this.loading = true
      this.$refs.aiUpload.submit()
    },

    // 7. 识别成功回调
    handleRecognitionSuccess(response, file, fileList) {
      this.loading = false
      if (response.code === 200 && response.data) {
        this.recognitionResults = response.data.results || []
        if (this.recognitionResults.length > 0) {
          Message.success('识别成功，识别到 ' + this.recognitionResults.length + ' 条教师信息')
        } else {
          Message.warning('未识别到教师信息，请确保图片清晰')
        }
      } else {
        Message.error('识别失败：' + (response.message || '未知错误'))
      }
    },

    // 8. 识别失败回调
    handleRecognitionError(err) {
      this.loading = false
      console.error('识别错误：', err)
      Message.error('识别失败，请检查网络连接或重试')
    },

    // 9. 添加识别到的教师
    addRecognizedTeacher(teacher) {
      // 填充表单
      this.teacherForm = {
        id: '',
        username: teacher.username || '',
        name: teacher.name || '',
        phone: teacher.phone || '',
        email: teacher.email || '',
        password: '123456', // 默认密码
        enabled: '1'
      }
      // 关闭AI弹窗，打开编辑弹窗
      this.aiRecognitionVisible = false
      this.isAdd = true
      this.teacherDialogVisible = true
      
      // 重置表单校验
      this.$nextTick(() => {
        if (this.$refs.teacherFormRef) {
          this.$refs.teacherFormRef.clearValidate()
        }
      })
    },

    // 10. 一键批量添加所有识别的教师
    batchAddRecognizedTeachers() {
      if (!this.recognitionResults || this.recognitionResults.length === 0) {
        Message.warning('没有可添加的教师信息')
        return
      }

      // 确认弹窗
      MessageBox.confirm(
        `确定要批量添加 ${this.recognitionResults.length} 条教师信息吗？`,
        '批量添加确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        this.batchLoading = true
        
        // 准备批量添加的数据
        const teachersToAdd = this.recognitionResults.map(teacher => ({
          username: teacher.username || '',
          name: teacher.name || '',
          phone: teacher.phone || '',
          email: teacher.email || '',
          password: teacher.password || '123456',
          enabled: teacher.enabled || '1'
        }))

        // 调用批量添加API
        axios.post('/api/admin/teacher/batchAdd', teachersToAdd)
          .then(response => {
            this.batchLoading = false
            if (response.data.code === 200) {
              const { successCount, failureCount, total, errorDetails } = response.data.data
              let msg = `批量添加完成！成功: ${successCount}, 失败: ${failureCount}, 总数: ${total}`
              if (errorDetails && errorDetails.trim() !== '') {
                msg += '\n\n失败详情：' + errorDetails
              }
              Message.success(msg)
              // 关闭弹窗并刷新列表
              this.aiRecognitionVisible = false
              this.recognitionResults = []
              this.getProfessorList()
            } else {
              Message.error('批量添加失败：' + (response.data.message || '未知错误'))
            }
          })
          .catch(error => {
            this.batchLoading = false
            console.error('批量添加错误：', error)
            Message.error('批量添加失败，请检查网络连接或重试')
          })
      }).catch(() => {
        Message.info('已取消批量添加')
      })
    }
  }
}
</script>

<style scoped>
.professor-manage-wrap {
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

.header-buttons {
  display: flex;
  gap: 10px;
}

.search-form {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
  margin-bottom: 10px;
}

::v-deep .el-select .el-input__inner {
  border-radius: 6px;
  height: 38px;
}

::v-deep .el-select-dropdown__item {
  padding: 10px 16px;
}
</style>