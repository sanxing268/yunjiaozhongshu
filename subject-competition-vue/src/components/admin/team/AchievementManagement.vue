<!-- 竞赛成果管理页面 -->
<template>
  <div class="achievement-manage-wrap">
    <!-- 面包屑导航 -->
     <el-row style="margin: 10px 0px 0px 3px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>竞赛管理</el-breadcrumb-item>
        <el-breadcrumb-item>竞赛成果管理</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>

    <!-- 页面头部：搜索 + 新增按钮 -->
    <div class="page-header">
      <el-form :inline="true" :model="filters" @submit.native.prevent class="filter-form">
        <el-form-item>
          <el-input v-model="filters.title" placeholder="项目名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.level" placeholder="奖励级别" clearable>
            <el-option label="国家级" value="国家级"></el-option>
            <el-option label="省级" value="省级"></el-option>
            <el-option label="市级" value="市级"></el-option>
            <el-option label="校级" value="校级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="loadAchievements">查询</el-button>
        </el-form-item>
      </el-form>
      <el-button type="success" icon="el-icon-plus" @click="openDialog(true)">新增成果</el-button>
    </div>

    <!-- 成果列表表格 -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="achievements" border stripe style="width: 100%"
        ref="multipleTable" @selection-change="selsChange">
        <el-table-column type="selection" width="50" align="center"></el-table-column>
        <el-table-column prop="achievementId" label="ID" width="60" align="center"></el-table-column>
        <el-table-column prop="title" label="项目名称" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="level" label="奖励级别" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="levelTagType(scope.row.level)" size="small">{{ scope.row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="team" label="获奖团队" min-width="160" show-overflow-tooltip></el-table-column>
        <el-table-column prop="awardDate" label="获奖日期" width="120" align="center"></el-table-column>
        <el-table-column prop="description" label="成果描述" min-width="180" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center"></el-table-column>
        <el-table-column fixed="right" label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" size="small" @click="openDialog(false, scope.row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" size="small" style="color: #f56c6c" @click="deleteAchievement(scope.row.achievementId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 20px; display: flex; justify-content: space-between; align-items: center;">
        <el-button type="danger" size="small" @click="batchRemove" :disabled="sels.length === 0">批量删除</el-button>
        <span style="color: #999; font-size: 13px;">共 {{ achievements.length }} 条记录</span>
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="isAdd ? '新增竞赛成果' : '编辑竞赛成果'" :visible.sync="dialogVisible" width="600px"
      :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="项目名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入获奖项目名称" maxlength="100" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="奖励级别" prop="level">
          <el-select v-model="form.level" placeholder="请选择奖励级别" style="width: 100%">
            <el-option label="国家级" value="国家级"></el-option>
            <el-option label="省级" value="省级"></el-option>
            <el-option label="市级" value="市级"></el-option>
            <el-option label="校级" value="校级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="获奖团队" prop="team">
          <el-input v-model="form.team" placeholder="请输入获奖团队/个人名称" maxlength="100" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="获奖日期" prop="awardDate">
          <el-date-picker v-model="form.awardDate" type="date" placeholder="选择获奖日期"
            value-format="yyyy-MM-dd" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="成果描述" prop="description">
          <el-input type="textarea" v-model="form.description" placeholder="请输入成果描述" :rows="4"
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
  name: 'AchievementManagement',
  data() {
    return {
      loading: false,
      achievements: [],
      sels: [],
      filters: {
        title: '',
        level: ''
      },
      dialogVisible: false,
      isAdd: true,
      form: {
        achievementId: null,
        title: '',
        level: '',
        team: '',
        awardDate: '',
        description: ''
      },
      rules: {
        title: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
        level: [{ required: true, message: '请选择奖励级别', trigger: 'change' }],
        team: [{ required: true, message: '请输入获奖团队', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.loadAchievements()
  },
  methods: {
    // 加载成果列表
    loadAchievements() {
      this.loading = true
      this.$axios.get('/achievement/list').then(resp => {
        if (resp && resp.data.code === 200) {
          let list = resp.data.data.list || []
          // 前端过滤
          if (this.filters.title) {
            list = list.filter(item => item.title && item.title.includes(this.filters.title))
          }
          if (this.filters.level) {
            list = list.filter(item => item.level === this.filters.level)
          }
          this.achievements = list
        }
      }).catch(error => {
        console.log('加载竞赛成果失败:', error)
        this.$message.error('加载竞赛成果失败')
      }).finally(() => {
        this.loading = false
      })
    },

    // 级别标签颜色
    levelTagType(level) {
      if (!level) return 'info'
      if (level.indexOf('国家') !== -1) return 'danger'
      if (level.indexOf('省') !== -1) return 'warning'
      if (level.indexOf('市') !== -1) return ''
      return 'success'
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
          this.form = { achievementId: null, title: '', level: '', team: '', awardDate: '', description: '' }
        } else {
          this.form = {
            achievementId: row.achievementId,
            title: row.title,
            level: row.level,
            team: row.team,
            awardDate: row.awardDate,
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
          this.$axios.post('/achievement/add', this.form).then(resp => {
            if (resp && resp.data.code === 200) {
              this.$message.success('新增成功')
              this.dialogVisible = false
              this.loadAchievements()
            } else {
              this.$message.error(resp.data.message || '新增失败')
            }
          }).catch(() => { this.$message.error('新增异常') })
        } else {
          this.$axios.put('/achievement/update', this.form).then(resp => {
            if (resp && resp.data.code === 200) {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.loadAchievements()
            } else {
              this.$message.error(resp.data.message || '修改失败')
            }
          }).catch(() => { this.$message.error('修改异常') })
        }
      })
    },

    // 删除单条
    deleteAchievement(id) {
      this.$confirm('确定要删除该竞赛成果吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.delete('/achievement/delete/' + id).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('删除成功')
            this.loadAchievements()
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
        let promises = this.sels.map(item => this.$axios.delete('/achievement/delete/' + item.achievementId))
        Promise.all(promises).then(() => {
          this.$message.success('批量删除成功')
          this.loadAchievements()
        }).catch(() => {
          this.$message.error('部分删除失败')
          this.loadAchievements()
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
.achievement-manage-wrap {
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
