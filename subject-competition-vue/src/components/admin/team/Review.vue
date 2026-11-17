<!-- 竞赛报名审核组件 -->
<template>
  <section class="app-container">
    <el-row style="margin: 18px 0px 0px 18px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>竞赛管理</el-breadcrumb-item>
        <el-breadcrumb-item>竞赛报名审核</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <!--工具条-->
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px">
      <el-form :inline="true" :model="filters" @submit.native.prevent>
        <el-form-item>
          <el-input v-model="filters.name" placeholder="赛事名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-on:click="getTeam">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <!--列表-->
    <el-table :data="teamData" border highlight-current-row style="width: 100%; height: 100%"   :cell-style="{ fontWeight: '700' }">
      <el-table-column type="selection" width="55"> </el-table-column>
      <el-table-column prop="projectId" sortable label="项目编号" min-width="100">
      </el-table-column>
      <el-table-column prop="title" label="赛事名称" min-width="120">
      </el-table-column>
      <el-table-column prop="teamNo" label="团队编号" min-width="120">
      </el-table-column>
      <el-table-column prop="teamName" label="团队名称" min-width="120">
      </el-table-column>

      <el-table-column
        prop="enrollTime"
        sortable
        label="注册时间"
        min-width="120"
      >
      </el-table-column>
      <el-table-column
        prop="project.status"
        label="审核状态"
        :filter-method="filterStatus"
        min-width="100"
        :filters="[
          { text: '通过', value: '3' },
          { text: '不通过', value: '2' },
          { text: '待审核', value: '1' },
          { text: '待提交', value: '0' },
        ]"
        filter-placement="bottom-end"
      >
        <template slot-scope="scope">
          <el-tag
            v-if="scope.row.status == '0'"
            :type="scope.row.status === '3' ? 'primary' : 'success'"
            disable-transitions
            >待提交
          </el-tag>
          <el-tag
            v-if="scope.row.status == '1'"
            :type="scope.row.status === '3' ? 'primary' : 'success'"
            disable-transitions
            >待审核
          </el-tag>
          <el-tag
            v-if="scope.row.status == '3'"
            :type="scope.row.status === '3' ? 'primary' : 'success'"
            disable-transitions
            >已通过
          </el-tag>
          <el-tag
            v-if="scope.row.status == '2'"
            :type="scope.row.status === '3' ? 'primary' : 'success'"
            disable-transitions
            >不通过
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
        label="操作"
        fixed="right"
        min-width="200"
        align="center"
      >
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="small"
            @click="handleDetail(scope.$index, scope.row)"
            >查看详情(审核)</el-button
          >
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
    <!--团队界面-->
    <el-dialog
      :title="textMap[dialogStatus]"
      customClass="dialog-width"
      :visible.sync="dialogFormVisible"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form
        :model="editForm"
        label-width="90px"
        :rules="editFormRules"
        ref="editForm"
      >
        <el-row>
          <el-col :span="6" style="margin: 0px">
            <el-form-item
              label="赛事名称"
              prop="competitionName"
              style="width: 235px"
            >
              <el-select
                v-model="competitionName"
                :disabled="dialogStatus == 'examine'"
                filterable
                placeholder="请选择赛事名称"
              >
                <el-option
                  v-for="item in projectList"
                  :key="item.name"
                  :label="item.name"
                  :value="item.name"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-tooltip
              class="item"
              effect="dark"
              content="请先选择赛事名称，并选择对应的项目编号"
              placement="top"
            >
              <el-form-item
                label="项目编号"
                prop="projectId"
                style="width: 230px"
              >
                <el-select
                  v-model="editForm.projectId"
                  :disabled="dialogStatus == 'examine'"
                  filterable
                  placeholder="请选择项目编号"
                >
                  <el-option
                    v-for="item in projectList"
                    :key="item.projectId"
                    :label="item.projectId"
                    :value="item.projectId + ''"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-tooltip>
          </el-col>
          <el-col :span="7">
            <el-form-item label="团队名称" prop="teamName" style="width: 230px">
              <el-input
                v-model="editForm.teamName"
                :disabled="dialogStatus == 'examine'"
                auto-complete="off"
                placeholder="请输入团队名称"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-table :data="memberData" highlight-current-row style="width: 100%">
          <el-table-column type="index" width="30"> </el-table-column>
          <!--<el-table-column prop="teamNo" label="团队编号" min-width="120">
          </el-table-column>-->
          <el-table-column prop="studentNo" label="学生学号" min-width="100">
          </el-table-column>
          <el-table-column prop="studentName" label="学生姓名" min-width="80">
          </el-table-column>
          <el-table-column prop="collegeName" label="所属学院" min-width="120">
          </el-table-column>
          <el-table-column prop="className" label="班级名称" min-width="120">
          </el-table-column>
          <el-table-column prop="grade" label="年级" min-width="100">
          </el-table-column>
          <el-table-column prop="major" label="专业" min-width="100">
          </el-table-column>
          <el-table-column prop="mail" label="邮箱" min-width="140">
          </el-table-column>
          <el-table-column prop="mobileNumber" label="手机号码" min-width="120">
          </el-table-column>
        </el-table>
      </el-form>
      <!--审核意见界面-->
      <el-form
        :model="editForm"
        label-width="120px"
        :rules="editFormRules"
        ref="editForm"
      >
        <el-form-item label="审核意见" prop="opinion">
          <el-input
            type="textarea"
            v-model="editForm.opinion"
            auto-complete="off"
          ></el-input>
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
      teamData: [], // 通过后台获取的团队数据
      memberList: [], // 添加的团队成员信息
      projectList: [], // 通过审核的项目信息
      memberData: [], // 团队成员信息存储
      memberIndex: 0, // 当前选中团队成员的索引
      member: {
        // 单个团队成员信息
        teamNo: '',
        studentNo: '',
        studentName: '',
        collegeName: '',
        className: '',
        grade: '',
        major: '',
        mail: '',
        mobileNumber: ''
      },
      total: 0,
      page: 1, // 当前页
      pageSize: 10, // 每页数量
      projectData: [], // 存储项目信息
      competitionName: '', // 选择的赛事名称
      filters: {
        name: ''
      },
      dialogStatus: '',
      memberStatus: '',
      dialogFormVisible: false,
      innerVisible: false,
      textMap: {
        examine: '学生报名信息审核'
      },
      // 编辑界面数据
      editForm: {
        title: '',
        projectId: '',
        teamNo: '',
        teamName: '',
        enrollTime: '',
        status: '',
        opinion: '',
        memberList: {
          memberNo: '',
          studentNo: '',
          studentName: '',
          collegeName: '',
          className: '',
          grade: '',
          major: '',
          mail: '',
          mobileNumber: ''
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
  watch: {
    // 监听选中的赛事名称
    competitionName (value) {
      this.projectData = []
      this.projectList.forEach((obj) => {
        if (obj.name === value) {
          this.projectData.push(obj)
        }
      })
    }
  },
  mounted () {
    this.getTeam()
  },
  methods: {
    // 查询团队信息
    getTeam () {
      /*     let teacher = localStorage.getItem('teacherId')
            if (teacher === 'undefined' || teacher === 'null' || teacher === undefined || teacher === null) {
              teacher = ''
            } */
      let params = {
        name: this.filters.name,
        status: '1'
      }
      var _this = this
      this.$axios
        .post('/admin/team/' + this.pageSize + '/1', params)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.teamData = resp.data.data.list
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
        .post('/admin/team/' + this.pageSize + '/' + page, params)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.projectData = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    // 审核
    examine () {
      let teamInfo = {
        teamNo: this.editForm.teamNo,
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
          _this.$axios.put('/admin/team/examine', teamInfo).then((res) => {
            if (res.data.code === 200) {
              _this.getTeam()
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
      this.dialogStatus = 'examine'
      this.dialogFormVisible = true
      this.memberData = []
      this.memberData = this.teamData[index].memberList
      this.competitionName = row.title
      this.editForm = Object.assign({}, row)
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

<style>
.dialog-width {
  min-width: 980px;
  min-height: 380px;
}

.teamDialogClass {
  min-width: 700px;
  min-height: 300px;
}
</style>
