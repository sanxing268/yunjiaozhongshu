<!-- 项目立项审核 -->
<template>
  <section class="app-container">
    <el-row style="margin: 18px 0px 0px 18px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }" >管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>项目管理</el-breadcrumb-item>
        <el-breadcrumb-item>项目审核</el-breadcrumb-item>
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
      </el-form>
    </el-col>

    <!--列表-->
    <el-table :data="projectData" border highlight-current-row style="width: 100% "   :cell-style="{ fontWeight: '700' }">
      <el-table-column type="selection" width="55"> </el-table-column>
      <el-table-column prop="projectId" sortable label="项目编号" min-width="100">
      </el-table-column>
      <el-table-column prop="name" label="赛事名称" min-width="120">
      </el-table-column>
      <el-table-column prop="format" label="赛制" min-width="100" :formatter="formatFormat">
      </el-table-column>
      <el-table-column prop="major" label="专业" min-width="120">
      </el-table-column>
      <el-table-column prop="projectDate" sortable label="申请立项日期" min-width="160">
      </el-table-column>
      <el-table-column prop="sponsor" label="主办单位" min-width="100">
      </el-table-column>
      <el-table-column prop="status" label="审核状态" :filter-method="filterStatus" width="100" filter-placement="bottom-end">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == '0'" :type="scope.row.status === '3' ? 'primary' : 'success'" disable-transitions>待提交
          </el-tag>
          <el-tag v-if="scope.row.status == '1'" :type="scope.row.status === '3' ? 'primary' : 'success'" disable-transitions>待审核
          </el-tag>
          <el-tag v-if="scope.row.status == '3'" :type="scope.row.status === '3' ? 'primary' : 'success'" disable-transitions>已通过
          </el-tag>
          <el-tag v-if="scope.row.status == '2'" :type="scope.row.status === '3' ? 'primary' : 'success'" disable-transitions>不通过
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" min-width="200" align="center">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleDetail(scope.$index, scope.row)">查看详情(审核)</el-button>
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
          <input type="hidden" v-model="editForm.projectId" />
          <el-col :span="14">
            <el-form-item label="赛事名称" prop="name">
              <el-input v-model="editForm.name" disabled auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="项目负责人" prop="teacherId">
              <el-input v-model="editForm.teacherId" disabled auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="组赛单位" prop="organization">
              <el-input v-model="editForm.organization" disabled auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="专业" prop="major">
              <el-input v-model="editForm.major" disabled auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="竞赛起始日期" prop="startDate">
              <el-input v-show="dialogStatus == 'detail'" v-model="editForm.startDate" disabled auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="竞赛结束日期" prop="endDate">
              <el-input v-model="editForm.endDate" disabled disabledauto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="赛制" prop="format" :disabled="dialogStatus == 'detail'">
              <el-input v-text="editForm.format === '1' ? '单人赛' : '团体赛'" disabled auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="申请立项日期" prop="projectDate">
              <el-input v-model="editForm.projectDate" disabled auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="主办单位" prop="sponsor">
              <el-input v-model="editForm.sponsor" disabled auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="竞赛承办单位" prop="contractor">
              <el-input v-model="editForm.contractor" disabled auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="editForm.phone" disabled auto-complete="off" style="width: 180px"></el-input>
            </el-form-item>
            <el-form-item label="邮件" prop="mail">
              <el-input v-model="editForm.mail" disabled auto-complete="off" style="width: 180px"></el-input>
            </el-form-item>
            <el-form-item label="论证组赛的目的和意义" prop="objective">
              <el-input type="textarea" v-model="editForm.objective" disabled auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <!--审核意见界面-->
      <el-form :model="editForm" label-width="120px" :rules="editFormRules" ref="editForm">
        <el-form-item label="审核意见" prop="opinion">
          <el-input type="textarea" v-model="editForm.opinion" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="审核" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio border label="3">通过</el-radio>
            <el-radio border label="2">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button v-on:click="clear">取 消</el-button>
        <el-button type="primary" @click="examine()">确 定</el-button>
      </span>
    </el-dialog>
  </section>
</template>

<script>
export default {
  data () {
    return {
      dialogFormVisible: false, // 审核意见对话框

      filters: {
        name: ''
      },
      textMap: {
        examine: '项目立项审核'
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
        opinion: '',
        status: '3',
        // 项目基本信息
        project: {
          name: '',
          charge: '',
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
        opinion: [
          { required: true, message: '请输入审核意见', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择审核结果', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    computedformat: {
      get: function () {
        return function (format) {
          return format === '1' ? '单人赛' : '团体赛'
        }
      },
      set: function (value) {
        this.format = value
      }
    }
  },

  mounted () {
    this.getProject()
  },
  methods: {
    getProject: function () {
      let params = {
        name: this.filters.name,
        status: '1'
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
        name: this.filters.name,
        status: '1'
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
    // 审核
    examine () {
      let project = {
        projectId: this.editForm.projectId,
        status: this.editForm.status,
        opinion: this.editForm.opinion
      }
      let _this = this
      if (
        _this.editForm.status === null ||
        _this.editForm.status === undefined ||
        _this.editForm.status === '1'
      ) {
        _this.$message.warning('请选择审核结果')
        return
      }
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          _this.$axios.put('/admin/project/examine', project).then((res) => {
            if (res.data.code === 200) {
              _this.getProject()
              _this.$message.success(res.data.message)
            } else {
              _this.$message.error(res.data.message)
            }
          })
          this.dialogFormVisible = false
        }
      })
    },
    clear () {
      this.dialogFormVisible = false
      this.$refs.editForm.resetFields()
    },
    // 显示详情框
    handleDetail (index, row) {
      this.dialogStatus = 'detail'
      this.dialogFormVisible = true
      this.editForm = Object.assign({}, row)
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
