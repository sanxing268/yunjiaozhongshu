<!--  竞赛项目 -->
<template>
  <section class="app-container">
    <el-row style="margin: 18px 0px 0px 18px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>竞赛管理</el-breadcrumb-item>
        <el-breadcrumb-item>竞赛项目</el-breadcrumb-item>
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
    <el-table :data="projectData" border highlight-current-row style="width: 100%" :cell-style="{ fontWeight: '700' }">
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
      <el-table-column prop="project.status" label="审核状态" min-width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == '0'" :type="scope.row.status === '3' ? 'primary' : 'success'"
            disable-transitions>待提交
          </el-tag>
          <el-tag v-if="scope.row.status == '1'" :type="scope.row.status === '3' ? 'primary' : 'success'"
            disable-transitions>待审核
          </el-tag>
          <el-tag v-if="scope.row.status == '3'" :type="scope.row.status === '3' ? 'primary' : 'success'"
            disable-transitions>已通过
          </el-tag>
          <el-tag v-if="scope.row.status == '2'" :type="scope.row.status === '3' ? 'primary' : 'success'"
            disable-transitions>不通过
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" min-width="185" align="center">
        <template slot-scope="scope">
          <el-button size="small" type="text" v-show="scope.row.status === '0'"
            @click="handleCommit(scope.$index, scope.row)">提交
          </el-button>
          <el-button size="small" type="text" v-show="scope.row.status === '0'"
            @click="handleEdit(scope.$index, scope.row)">编辑
          </el-button>
          <el-button size="small" type="text" @click="handleDetail(scope.$index, scope.row)">详情</el-button>
          <el-button type="text" v-show="scope.row.status === '2' || scope.row.status === '0'" size="small"
            @click="singleDel(scope.$index, scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!--分页工具-->
    <div style="margin: 20px 0 50px 0">
      <el-pagination background style="float: right" layout="total, prev, pager, next, jumper"
        @current-change="handleCurrentChange" :page-size="pageSize" :total="total">
      </el-pagination>
    </div>
    <!--编辑界面-->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="120px">
        <el-row>
          <input type="hidden" v-model="editForm.id" />
          <el-col :span="14">
            <el-form-item label="赛事名称" prop="name">
              <el-input v-model="editForm.name" :disabled="dialogStatus == 'detail'" auto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="组赛单位" prop="organization">
              <el-input v-model="editForm.organization" :disabled="dialogStatus == 'detail'" auto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="专业" prop="major">
              <el-input v-model="editForm.major" :disabled="dialogStatus == 'detail'" auto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="竞赛起始日期" prop="startDate">
              <el-date-picker v-model="editForm.startDate" type="datetime" :disabled="dialogStatus == 'detail'"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="竞赛结束日期" prop="endDate">
              <el-date-picker v-model="editForm.endDate" type="datetime" :disabled="dialogStatus == 'detail'"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期时间">
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
              <el-date-picker v-model="editForm.projectDate" :disabled="dialogStatus == 'detail'"
                value-format="yyyy-MM-dd HH:mm:ss" type="datetime" placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="主办单位" prop="sponsor">
              <el-input v-model="editForm.sponsor" :disabled="dialogStatus == 'detail'" auto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="竞赛承办单位" prop="contractor">
              <el-input v-model="editForm.contractor" :disabled="dialogStatus == 'detail'" auto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="editForm.phone" :disabled="dialogStatus == 'detail'" auto-complete="off"
                style="width: 180px"></el-input>
            </el-form-item>
            <el-form-item label="邮件" prop="mail">
              <el-input v-model="editForm.mail" :disabled="dialogStatus == 'detail'" auto-complete="off"
                style="width: 180px"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="论证组赛的目的和意义" prop="objective">
          <el-input type="textarea" v-model="editForm.objective" :disabled="dialogStatus == 'detail'"
            auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native="dialogFormVisible = false" v-show="dialogStatus == 'detail'">关闭</el-button>
      </div>
    </el-dialog>
  </section>
</template>

<script>
export default {
  data() {
    return {
      dialogFormVisible: false,
      opinionDialogVisible: false, // 审核意见对话框
      filters: {
        name: ''
      },
      textMap: {
        detail: '项目立项详情'
      },
      total: 0,
      pageSize: 10, // 每页数量
      page: 1, // 当前页
      projectData: [], // 项目立项数据
      teacherList: [],
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
      }
    }
  },
  mounted() {
    this.getProject()
  },
  // 查询竞赛项目列表内容
  methods: {
    getProject: function () {
      let params = {
        name: this.filters.name,
        status: '3'
      }
      var _this = this
      this.$axios
        .post('/admin/project/' + this.pageSize + '/1', params)
        .then((resp) => {
          if (resp && resp.status === 200) {
            _this.projectData = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    handleCurrentChange(page) {
      let params = {
        name: this.filters.name,
        status: '3'
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
    // 显示详情框
    handleDetail(index, row) {
      this.dialogStatus = 'detail'
      this.dialogFormVisible = true
      this.editForm = Object.assign({}, row)
    },
    formatFormat: function (row, column) {
      return row.format === '1' ? '单人赛' : '团体赛'
    },
    filterStatus(value, row) {
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
