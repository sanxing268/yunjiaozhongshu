<!-- 文件下载管理页面 -->
<template>
  <div class="download-manage-wrap">
    <!-- 面包屑导航 -->
    <el-row style="margin: 10px 0px 0px 3px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>内容管理</el-breadcrumb-item>
        <el-breadcrumb-item>文件下载管理</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>

    <!-- 页面头部：搜索 + 新增按钮 -->
    <div class="page-header">
      <el-form :inline="true" :model="filters" @submit.native.prevent class="filter-form">
        <el-form-item>
          <el-input v-model="filters.title" placeholder="文件标题" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.category" placeholder="文件分类" clearable>
            <el-option label="通知文件" value="通知文件"></el-option>
            <el-option label="报名表" value="报名表"></el-option>
            <el-option label="比赛规则" value="比赛规则"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="loadDownloads">查询</el-button>
        </el-form-item>
      </el-form>
      <el-button type="success" icon="el-icon-plus" @click="openDialog(true)">新增文件</el-button>
    </div>

    <!-- 文件列表表格 -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="downloads" border stripe style="width: 100%"
        ref="multipleTable" @selection-change="selsChange">
        <el-table-column type="selection" width="50" align="center"></el-table-column>
        <el-table-column prop="downloadId" label="ID" width="60" align="center"></el-table-column>
        <el-table-column prop="title" label="文件标题" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="category" label="分类" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="small">{{ scope.row.category || '未分类' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="文件大小" width="100" align="center"></el-table-column>
        <el-table-column prop="downloadCount" label="下载次数" width="100" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.downloadCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="fileUrl" label="下载链接" min-width="180" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.fileUrl === '#'" style="color: #999;">未配置</span>
            <a v-else :href="scope.row.fileUrl" target="_blank" style="color: #409EFF;">{{ scope.row.fileUrl }}</a>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="文件描述" min-width="160" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="160" align="center"></el-table-column>
        <el-table-column fixed="right" label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" size="small" @click="openDialog(false, scope.row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" size="small" style="color: #f56c6c" @click="deleteDownload(scope.row.downloadId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 20px; display: flex; justify-content: space-between; align-items: center;">
        <el-button type="danger" size="small" @click="batchRemove" :disabled="sels.length === 0">批量删除</el-button>
        <span style="color: #999; font-size: 13px;">共 {{ downloads.length }} 条记录</span>
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="isAdd ? '新增下载文件' : '编辑下载文件'" :visible.sync="dialogVisible" width="600px"
      :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="文件标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文件标题" maxlength="100" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="文件分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择文件分类" style="width: 100%">
            <el-option label="通知文件" value="通知文件"></el-option>
            <el-option label="报名表" value="报名表"></el-option>
            <el-option label="比赛规则" value="比赛规则"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="下载链接" prop="fileUrl">
          <el-input v-model="form.fileUrl" placeholder="请输入文件下载链接（如COS地址）"></el-input>
        </el-form-item>
        <el-form-item label="文件大小" prop="fileSize">
          <el-input v-model="form.fileSize" placeholder="如：25KB、1.2MB"></el-input>
        </el-form-item>
        <el-form-item label="文件描述" prop="description">
          <el-input type="textarea" v-model="form.description" placeholder="请输入文件描述" :rows="3"
            maxlength="500" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'DownloadManagement',
  data() {
    return {
      loading: false,
      downloads: [],
      sels: [],
      filters: {
        title: '',
        category: ''
      },
      dialogVisible: false,
      isAdd: true,
      form: {
        downloadId: null,
        title: '',
        category: '',
        fileUrl: '',
        fileSize: '',
        description: ''
      },
      rules: {
        title: [{ required: true, message: '请输入文件标题', trigger: 'blur' }],
        fileUrl: [{ required: true, message: '请输入下载链接', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.loadDownloads()
  },
  methods: {
    // 加载文件列表
    loadDownloads() {
      this.loading = true
      this.$axios.get('/download/list').then(resp => {
        if (resp && resp.data.code === 200) {
          let list = resp.data.data.list || []
          // 前端过滤
          if (this.filters.title) {
            list = list.filter(item => item.title && item.title.includes(this.filters.title))
          }
          if (this.filters.category) {
            list = list.filter(item => item.category === this.filters.category)
          }
          this.downloads = list
        }
      }).catch(error => {
        console.log('加载文件列表失败:', error)
        this.$message.error('加载文件列表失败')
      }).finally(() => {
        this.loading = false
      })
    },

    // 打开新增/编辑弹窗
    openDialog(isAdd, row = {}) {
      this.isAdd = isAdd
      this.dialogVisible = true
      this.$nextTick(() => {
        if (this.$refs.formRef) {
          this.$refs.formRef.clearValidate()
        }
        if (isAdd) {
          this.form = { downloadId: null, title: '', category: '', fileUrl: '', fileSize: '', description: '' }
        } else {
          this.form = {
            downloadId: row.downloadId,
            title: row.title,
            category: row.category,
            fileUrl: row.fileUrl,
            fileSize: row.fileSize,
            description: row.description
          }
        }
      })
    },

    // 提交表单
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (!valid) return
        if (this.isAdd) {
          this.$axios.post('/download/add', this.form).then(resp => {
            if (resp && resp.data.code === 200) {
              this.$message.success('新增成功')
              this.dialogVisible = false
              this.loadDownloads()
            } else {
              this.$message.error(resp.data.message || '新增失败')
            }
          }).catch(() => { this.$message.error('新增异常') })
        } else {
          this.$axios.put('/download/update', this.form).then(resp => {
            if (resp && resp.data.code === 200) {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.loadDownloads()
            } else {
              this.$message.error(resp.data.message || '修改失败')
            }
          }).catch(() => { this.$message.error('修改异常') })
        }
      })
    },

    // 删除单条
    deleteDownload(id) {
      this.$confirm('确定要删除该文件记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.delete('/download/delete/' + id).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('删除成功')
            this.loadDownloads()
          } else {
            this.$message.error(resp.data.message || '删除失败')
          }
        })
      }).catch(() => {})
    },

    // 批量删除
    batchRemove() {
      this.$confirm('确认删除选中的 ' + this.sels.length + ' 条记录吗？', '提示', {
        type: 'warning'
      }).then(() => {
        let promises = this.sels.map(item => this.$axios.delete('/download/delete/' + item.downloadId))
        Promise.all(promises).then(() => {
          this.$message.success('批量删除成功')
          this.loadDownloads()
        }).catch(() => {
          this.$message.error('部分删除失败')
          this.loadDownloads()
        })
      }).catch(() => {})
    },

    // 多选变化
    selsChange(sels) {
      this.sels = sels
    }
  }
}
</script>

<style scoped>
.download-manage-wrap {
  padding: 0 20px 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin: 18px 0 0 0;
}

.filter-form {
  display: flex;
  align-items: center;
}

.table-card {
  margin-top: 18px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}
</style>
