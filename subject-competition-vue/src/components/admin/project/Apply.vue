<!--  项目立项申请 -->
<template>
  <section class="app-container">
    <el-row style="margin: 18px 0px 0px 18px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>项目管理</el-breadcrumb-item>
        <el-breadcrumb-item>项目立项申请</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <!--工具条-->
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px">
      <el-form :inline="true" :model="filters" @submit.native.prevent>
        <el-form-item>
          <el-input v-model="filters.name" placeholder="赛事名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-on:click="getProject">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <!--列表-->
    <el-table :data="projectData" border highlight-current-row @selection-change="selsChange" style="width: 100%"   :cell-style="{ fontWeight: '700' }">
      <el-table-column type="selection" width="55"> </el-table-column>
      <el-table-column prop="projectId" sortable label="项目编号" min-width="100">
      </el-table-column>
      <el-table-column prop="name" label="赛事名称" min-width="120">
      </el-table-column>
      <el-table-column prop="format" label="赛制" min-width="100" :formatter="formatFormat">
      </el-table-column>
      <el-table-column prop="major" label="专业" min-width="120">
      </el-table-column>
      <el-table-column label="日期" min-width="180" sortable>
        <template slot-scope="scope">
          <i class="el-icon-time"></i>
          <span style="margin-left: 10px">{{ scope.row.projectDate }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sponsor" label="主办单位" min-width="100">
      </el-table-column>
      <el-table-column prop="project.status" label="审核状态" :filter-method="filterStatus" min-width="100"
        :filters="[
          { text: '通过', value: '3' },
          { text: '不通过', value: '2' },
          { text: '待审核', value: '1' },
          { text: '待提交', value: '0' },
        ]"
        filter-placement="bottom-end"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == '0'" :type="scope.row.status === '3' ? 'primary' : 'success'" disable-transitions>待提交</el-tag>
          <el-tag v-if="scope.row.status == '1'" :type="scope.row.status === '3' ? 'primary' : 'success'" disable-transitions>待审核
          </el-tag>
          <el-tag v-if="scope.row.status == '3'" :type="scope.row.status === '3' ? 'primary' : 'success'" disable-transitions>已通过
          </el-tag>
          <el-tag v-if="scope.row.status == '2'" :type="scope.row.status === '3' ? 'primary' : 'success'" disable-transitions>不通过
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" min-width="185" align="center">
        <template slot-scope="scope">
          <el-button size="small" type="text" v-show="scope.row.status === '0'" @click="handleCommit(scope.$index, scope.row)">提交
          </el-button>
          <el-button size="small" type="text" v-show="scope.row.status === '0'" @click="handleEdit(scope.$index, scope.row)">编辑
          </el-button>
          <el-button size="small" type="text" @click="handleDetail(scope.$index, scope.row)">详情</el-button>
          <el-button type="text" v-show="scope.row.status === '2' || scope.row.status === '0'" size="small" @click="singleDel(scope.$index, scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!--分页工具-->
    <div style="margin: 20px 0 50px 0">
      <el-pagination
        background
        style="float: right"
        layout="total, prev, pager, next, jumper"
        @current-change="handleCurrentChange"
        :page-size="pageSize"
        :total="total"
      >
      </el-pagination>
    </div>
    <!--编辑界面-->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="120px" :rules="editFormRules" ref="editForm">
        <el-row>
          <input type="hidden" v-model="editForm.id" />
          <el-col :span="14">
            <el-form-item label="赛事名称" prop="name">
              <el-input v-model="editForm.name" :disabled="dialogStatus == 'detail'" auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="组赛单位" prop="organization">
              <el-input v-model="editForm.organization" :disabled="dialogStatus == 'detail'" auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="专业" prop="major">
              <el-input v-model="editForm.major" :disabled="dialogStatus == 'detail'" auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="竞赛起始日期" prop="startDate">
              <el-date-picker v-model="editForm.startDate" type="datetime" :disabled="dialogStatus == 'detail'" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="竞赛结束日期" prop="endDate">
              <el-date-picker v-model="editForm.endDate" type="datetime" :disabled="dialogStatus == 'detail'" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="赛制" prop="format">
              <el-radio-group v-model="editForm.format">
                <el-radio :disabled="dialogStatus == 'detail'" class="radio" label="1" style="width: 50px">单人赛
                </el-radio>
                <el-radio :disabled="dialogStatus == 'detail'" class="radio" label="2" style="width: 50px">团体赛
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="申请立项日期" prop="projectDate">
              <el-date-picker v-model="editForm.projectDate" :disabled="dialogStatus == 'detail'" value-format="yyyy-MM-dd HH:mm:ss" type="datetime" placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="主办单位" prop="sponsor">
              <el-input v-model="editForm.sponsor" :disabled="dialogStatus == 'detail'" auto-complete="off" style="width: 220px">
              </el-input>
            </el-form-item>
            <el-form-item label="竞赛承办单位" prop="contractor">
              <el-input v-model="editForm.contractor" :disabled="dialogStatus == 'detail'" auto-complete="off" style="width: 220px">
              </el-input>
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="editForm.phone" :disabled="dialogStatus == 'detail'" auto-complete="off" style="width: 180px">
              </el-input>
            </el-form-item>
            <el-form-item label="邮件" prop="mail">
              <el-input v-model="editForm.mail" :disabled="dialogStatus == 'detail'" auto-complete="off" style="width: 180px">
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="论证组赛的目的和意义" prop="objective">
          <el-input type="textarea" v-model="editForm.objective" :disabled="dialogStatus == 'detail'" auto-complete="off">
          </el-input>
        </el-form-item>
        <el-form-item label="审核意见" v-show=" dialogStatus == 'detail' &&  (editForm.status == '3' || editForm.status == '2') ">
          <el-input type="textarea" v-model="editForm.opinion" disabled auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native="dialogFormVisible = false" v-show="dialogStatus != 'detail'">取消</el-button>
        <el-button @click.native="dialogFormVisible = false" v-show="dialogStatus == 'detail'">关闭</el-button>
        <el-button v-if="dialogStatus == 'add'" type="primary" @click="projectAdd">添加</el-button>
        <el-button v-show="dialogStatus == 'update'" type="primary" @click="projectUpdate">修改</el-button>
      </div>
    </el-dialog>
  </section>
</template>

<script>
export default {
  data () {
    return {
      dialogFormVisible: false,
      opinionDialogVisible: false, // 审核意见对话框
      filters: {
        name: ''
      },
      textMap: {
        update: '项目立项修改',
        add: '项目立项申请',
        detail: '项目立项详情'
      },
      total: 0,
      pageSize: 10, // 每页数量
      page: 1, // 当前页
      projectData: [], // 项目立项数据
      teacherList: [],
      sels: [], // 列表选中列
      dialogStatus: '',
      fileName: '',
      // 编辑界面数据
      editForm: {
        id: 0,
        projectId: 0,
        status: '',
        opinion: '',
        // 项目基本信息
        project: {
          name: '',
          major: '',
          format: '',
          projectDate: '',
          sponsor: '',
          contractor: '',
          objective: '',
          invitation: [],
          phone: '',
          mail: '',
          organization: '',
          startDate: '',
          endDate: ''
        }
      },
      editFormRules: {
        name: [{ required: true, message: '请输入赛事名称', trigger: 'blur' }],
        major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
        format: [{ required: true, message: '请选择赛制', trigger: 'blur' }],
        mail: [{ required: true, message: '请输入邮箱', trigger: 'blur' },
          { pattern: /^([0-9A-Za-z\-_\\.]+)@([0-9a-z]+\.[a-z]{2,3}(\.[a-z]{2})?)$/g,
            message: '请输入正确的邮箱',
            trigger: 'blur'}],
        phone: [{ required: true, message: '请输入手机号', trigger: 'change' },
          { min: 7, max: 11, message: '长度在7~11个字符', trigger: 'blur' },
          {
            pattern: /^1[3|5|7|8|9]\d{9}$/,
            message: '请输入正确的号码格式',
            trigger: 'change'
          }],
        organization: [
          { required: true, message: '请输入组赛单位', trigger: 'blur' }
        ],
        startDate: [
          { required: true, message: '请输入竞赛起始日期', trigger: 'blur' }
        ],
        endDate: [
          { required: true, message: '请输入竞赛结束日期', trigger: 'blur' }
        ],
        contractor: [
          { required: true, message: '请输入竞赛承办单位', trigger: 'blur' }
        ],
        sponsor: [
          { required: true, message: '请输入主办单位', trigger: 'blur' }
        ],
        objective: [
          {
            required: true,
            message: '请输入论证组赛的目的和意义',
            trigger: 'blur'
          }
        ],
        projectDate: [
          { required: true, message: '请输入申请立项日期', trigger: 'blur' }
        ]
      }
    }
  },
  mounted () {
    this.getProject()
  },
  methods: {
    getProject: function () {
      let params = {
        name: this.filters.name
      }
      var _this = this
      this.$axios
        .post('/admin/project/' + this.pageSize + '/1', params)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.projectData = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    handleCurrentChange (page) {
      let params = {
        name: this.filters.name
      }
      var _this = this
      this.$axios
        .post('/admin/project/' + this.pageSize + '/' + page, params)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.projectData = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    // 显示新增界面
    handleAdd: function () {
      this.dialogStatus = 'add'
      this.dialogFormVisible = true
      this.editForm = {
        project: {
          format: '1'
        }
      }
      if (this.$refs.editForm !== undefined) {
        this.$refs.editForm.resetFields()
      }
    },
    // 显示详情框
    handleDetail (index, row) {
      this.dialogStatus = 'detail'
      this.dialogFormVisible = true
      this.editForm = Object.assign({}, row)
      if (this.$refs.editForm !== undefined) {
        this.$refs.editForm.resetFields()
      }
    },
    // 显示编辑界面
    handleEdit (index, row) {
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.editForm = Object.assign({}, row)
      if (this.$refs.editForm !== undefined) {
        this.$refs.editForm.resetFields()
      }
    },

    // 新增项目信息
    projectAdd: function () {
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          this.$confirm('确认提交吗？', '提示', {})
            .then(() => {
              const project = Object.assign({}, this.editForm)
              var _this = this
              // 项目信息的保存or更新
              _this.$axios
                .post('/admin/project/insert', project)
                .then((res) => {
                  _this.dialogFormVisible = false
                  if (res.data.code === 200) {
                    _this.dialogFormVisible = false
                    _this.$message.success(res.data.message)
                    _this.getProject()
                  } else {
                    _this.$message.error(res.data.message)
                  }
                })
                .catch((error) => {
                  _this.dialogFormVisible = false
                  console.log('error--->', error)
                })
            })
            .catch((e) => {
              // 打印一下错误
              console.log('保存or更新出错:' + e)
            })
        }
      })
    },
    // 项目信息修改
    projectUpdate: function () {
      let _this = this
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          this.$confirm('确认提交吗？', '提示', {})
            .then(() => {
              var project = Object.assign({}, _this.editForm)
              // 项目信息的保存or更新
              _this.$axios
                .put('/admin/project/update', project)
                .then(function (res) {
                  _this.dialogFormVisible = false
                  if (res.data.success === true) {
                    _this.dialogFormVisible = false
                    _this.$message.success(res.data.message)
                    _this.getProject()
                  } else {
                    _this.$message.error(res.data.message)
                  }
                })
                .catch((error) => {
                  _this.dialogFormVisible = false
                  _this.$message.error(error)
                })
            })
            .catch((e) => {
              // 打印一下错误
              console.log('保存or更新出错:' + e)
            })
        }
      })
    },
    // 提交项目立项申请
    handleCommit (index, row) {
      this.$confirm('确认提交该项目立项吗?', '提示', {
        type: 'warning'
      })
        .then(() => {
          if (row.status === '0') {
            let project = {
              projectId: row.projectId,
              status: '1'
            }
            let _this = this
            _this.$axios
              .put('/admin/project/commit', project)
              .then((res) => {
                if (res.data.code === 200) {
                  _this.getProject()
                  _this.$message.success(res.data.message)
                } else {
                  _this.$message.error(res.data.message)
                }
              })
              .catch((error) => {
                console.log('projectApply error-->', error)
              })
          } else if (row.status === '1') {
            this.$message.error('已经提交，不必重复提交')
          }
        })
        .catch(() => {})
    },
    // 删除
    singleDel (index, row) {
      let _this = this
      this.$confirm('确认删除项目名为' + row.name + '的记录吗?', '提示', {
        type: 'warning'
      })
        .then(() => {
          if (row.status !== '3') {
            if (row.projectId !== undefined) {
              _this.$axios
                .delete('/admin/project/' + row.projectId)
                .then(function (resp) {
                  if (resp.data.code === 200) {
                    _this.$message.success(resp.data.message)
                    _this.getProject()
                  } else {
                    _this.$message.error(resp.data.message)
                  }
                })
            }
          } else {
            _this.$message.error('删除失败,不能删除已经通过审核的项目立项')
          }
        })
        .catch(() => {})
    },

    // 全选单选多选
    selsChange (sels) {
      this.sels = sels
    },

    formatFormat: function (row, column) {
      return row.format === '1' ? '单人赛' : '团体赛'
    },
    filterStatus (value, row) {
      return row.status === value
    },
    formatStatus: function (row) {
      if (row.status === '0') {
        return '待提交'
      } else if (row.status === '1') {
        return '待审核'
      } else if (row.status === '2') {
        return '不通过'
      } else if (row.status === '3') {
        return '已批准'
      }
    }
  }
}
</script>
<style rel="stylesheet/css">
@keyframes treeTableShow {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@-webkit-keyframes treeTableShow {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
<style scoped>
a:link {
  color: blue;
}

/* 未访问的链接 */
a:visited {
  color: orange;
}

/* 已访问的链接 */
a:hover {
  color: green;
}

/* 鼠标划过链接 */
a:active {
  color: red;
}

/* 已选中的链接 */
</style>
