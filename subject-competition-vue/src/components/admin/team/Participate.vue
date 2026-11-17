<!-- 竞赛报名 -->
<template>
  <section class="app-container">
    <el-row style="margin: 18px 0px 0px 18px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>竞赛管理</el-breadcrumb-item>
        <el-breadcrumb-item>竞赛报名</el-breadcrumb-item>
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
        <el-form-item>
          <el-button type="primary" @click="handleAdd">报名</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <!--列表-->
    <el-table :data="teamData" border highlight-current-row style="width: 100%; height: 100%"
      :cell-style="{ fontWeight: '700' }">
      <el-table-column type="selection" width="55"> </el-table-column>
      <el-table-column prop="projectId" label="项目编号" sortable min-width="100">
      </el-table-column>
      <el-table-column prop="title" label="赛事名称" min-width="120">
      </el-table-column>
      <el-table-column prop="teamNo" label="团队编号" min-width="120">
      </el-table-column>
      <el-table-column prop="teamName" label="团队名称" min-width="120">
      </el-table-column>
      <el-table-column prop="enrollTime" sortable label="报名时间" min-width="120">
      </el-table-column>
      <el-table-column prop="status" label="审核状态" :filter-method="filterStatus" width="100"
        filter-placement="bottom-end">
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
          <el-button size="small" type="text" v-show="scope.row.status === '0' || scope.row.status === '2'"
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
    <!--点击编辑按钮的弹窗-->
    <el-dialog :title="textMap[dialogStatus]" customClass="dialog-width" :visible.sync="dialogFormVisible"
      append-to-body :close-on-click-modal="false">
      <el-form :model="editForm" label-width="90px" :rules="editFormRules" ref="editForm">
        <el-row>
          <el-col :span="6" style="margin: 0px">
            <el-form-item label="赛事名称" prop="competitionName" style="width: 235px">
              <el-select v-model="competitionName" :disabled="dialogStatus == 'detail'" filterable
                placeholder="请选择赛事名称">
                <el-option v-for="item in projectList" :key="item.name" :label="item.name" :value="item.name">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-tooltip class="item" effect="dark" content="请先选择赛事名称" placement="top">
              <el-form-item label="项目编号" prop="projectId" style="width: 230px">
                <el-input v-model="editForm.projectId" :disabled="dialogStatus == 'detail'" filterable
                  placeholder="请选择项目编号">
                </el-input>
              </el-form-item>
            </el-tooltip>
          </el-col>
          <el-col :span="7">
            <el-form-item label="团队名称" prop="teamName" style="width: 230px">
              <el-input v-model="editForm.teamName" :disabled="dialogStatus == 'detail'" auto-complete="off"
                placeholder="请输入团队名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" style="margin-left: 8px; width: 110px" v-show="dialogStatus != 'detail'"
              @click="handleAddTeam">添加团队成员
            </el-button>
          </el-col>
        </el-row>
        <!-- 所属赛事团队成员信息 -->
        <el-table :data="memberData" highlight-current-row style="width: 100%">
          <el-table-column type="index" width="30"> </el-table-column>
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
          <el-table-column fixed="right" label="操作" width="180" v-if="dialogStatus != 'detail'">
            <template slot-scope="scope">
              <el-button size="small" v-show="dialogStatus != 'detail'" @click="memberEdit(scope.$index, scope.row)">编辑
              </el-button>
              <!-- 团队成员删除按钮 -->
              <el-button type="danger" v-show="dialogStatus != 'detail'" size="small"
                @click="memberDel(scope.$index, scope.row)">删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-form-item label="审核意见" style="width: 400px; margin-top: 20px"
          v-show="dialogStatus == 'detail' && (editForm.status == '3' || editForm.status == '2')">
          <el-input type="textarea" v-model="editForm.opinion" disabled auto-complete="off"></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <!-- 添加团队信息后最终保存按钮 -->
        <el-button type="primary" v-show="dialogStatus == 'add'" @click="teamSave">添 加</el-button>
        <!-- 编辑信息后的最终修改提交按钮 -->
        <el-button type="primary" v-show="dialogStatus == 'update'" @click="teamUpdate">修 改</el-button>
      </div>
    </el-dialog>

    <!--  点击添加团队成员按钮弹窗-->
    <el-dialog customClass="teamDialogClass" :title="MemberMap[memberStatus]" :visible.sync="innerVisible">
      <el-form :model="member" label-width="80px" :rules="memberRules" ref="member">
        <el-row>
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="member.studentNo" :disabled="memberStatus == 'detail'" auto-complete="off"
                style="width: 230px"></el-input>
            </el-form-item>
            <el-form-item label="学生姓名" prop="studentName">
              <el-input v-model="member.studentName" :disabled="memberStatus == 'detail'" auto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="所属学院" prop="collegeName">
              <el-input v-model="member.collegeName" :disabled="memberStatus == 'detail'" disabledauto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="班级名称" prop="className">
              <el-input v-model="member.className" :disabled="memberStatus == 'detail'" auto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="年级" prop="grade">
              <el-input v-model="member.grade" :disabled="memberStatus == 'detail'" auto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="专业" prop="major">
              <el-input v-model="member.major" :disabled="memberStatus == 'detail'" auto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="mail">
              <el-input v-model="member.mail" :disabled="memberStatus == 'detail'" auto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="手机号码" prop="mobileNumber">
              <el-input v-model="member.mobileNumber" type="number" :disabled="memberStatus == 'detail'"
                auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="innerVisible = false">取 消</el-button>
        <!-- 点击"添加团队成员"按钮时 默认是新增 加上sql语句 实现真实插入 -->
        <el-button type="primary" v-show="memberStatus == 'add'" @click="addMember">添 加</el-button>
        <!-- 点击操作栏目下的"编辑"按钮时，设置为修改 -->
        <el-button type="primary" v-show="memberStatus == 'update'" @click="editMember">修 改</el-button>
      </div>
    </el-dialog>
  </section>
</template>

<script>
export default {
  data() {
    return {
      teamData: [], // 通过后台获取的团队数据
      memberList: [], // 添加的团队成员信息
      projectList: [], // 通过审核的项目信息
      memberData: [], // 团队成员信息存储
      memberIndex: 0, // 当前选中团队成员的索引
      member: {
        // 单个团队成员信息
        memberNo: '',
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
      // 弹窗的标题设置文本
      textMap: {
        update: '团队信息修改',
        add: '添加团队',
        detail: '团队详情'
        // examine: "项目立项审核",
      },
      // 成员信息修改标题设置文本
      MemberMap: {
        update: '团队成员信息修改',
        add: '添加团队成员信息',
        detail: '团队成员详情'
      },
      // 编辑界面数据
      editForm: {
        title: '',
        projectId: '',
        teamNo: '',
        teamName: '',
        enrollTime: '',
        status: '',
        format: '',
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
      // 数据校验
      editFormRules: {
        // title: [{required: true, message: '请选择赛事名称', trigger: 'blur'}],
        //  competitionName: [{required: true, message: '请选择赛事名称', trigger: 'blur'}],
        // enrollTime: [{required: true, message: '请选择注册日期', trigger: 'blur'}],
        teamName: [
          { required: true, message: '请输入团队项目', trigger: 'blur' }
        ],
        projectId: [
          { required: true, message: '请输入项目编号', trigger: 'blur' }
        ],
        teamNo: [
          { required: true, message: '请输入团队编号', trigger: 'blur' }
        ],
        format: [
          { required: true, message: '请输入项目编号', trigger: 'blur' }
        ]
      },
      memberRules: {
        studentNo: [
          { required: true, message: '请输入学生学号', trigger: 'blur' }
        ],
        studentName: [
          { required: true, message: '请输入学生姓名', trigger: 'blur' }
        ],
        className: [
          { required: true, message: '请输入班级名称', trigger: 'blur' }
        ],
        collegeName: [
          { required: true, message: '请输入所属学院', trigger: 'blur' }
        ],
        grade: [{ required: true, message: '请输入年级', trigger: 'blur' }],
        major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
        mail: [{ required: true, message: '请输入邮箱', trigger: 'blur' },
        {
          pattern: /^([0-9A-Za-z\-_\\.]+)@([0-9a-z]+\.[a-z]{2,3}(\.[a-z]{2})?)$/g,
          message: '请输入正确的邮箱',
          trigger: 'blur'
        }],
        mobileNumber: [
          { required: true, message: '请输入手机号', trigger: 'change' },
          { min: 7, max: 11, message: '长度在7~11个字符', trigger: 'blur' },
          {
            pattern: /^1[3|5|7|8|9]\d{9}$/,
            message: '请输入正确的号码格式',
            trigger: 'change'
          }
        ]
      }
    }
  },
  watch: {
    // 监听选中的赛事名称
    competitionName(value) {
      this.editForm.projectId = ''
      this.projectList.forEach((obj) => {
        if (obj.name === value) {
          this.editForm.projectId = obj.projectId
          this.editForm.format = obj.format
          if (this.editForm.format === '1') {
            this.editForm.format = '单人赛'
          } else {
            this.editForm.format = '团体赛'
          }
        }
      })
    }
  },
  mounted() {
    this.getTeam()
    this.getProject()
  },
  methods: {
    // 查询团队信息
    getTeam() {
      /*     let teacher = localStorage.getItem('teacherId')
            if (teacher === 'undefined' || teacher === 'null' || teacher === undefined || teacher === null) {
              teacher = ''
            } */
      let params = {
        title: this.filters.name
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
    handleCurrentChange(page) {
      let params = {
        title: this.filters.name
      }
      var _this = this
      this.$axios
        .post('/admin/team/' + this.pageSize + '/' + page, params)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.teamData = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    // 查询项目立项信息
    getProject: function () {
      let params = {
        name: this.filters.name,
        status: '3'
      }
      var _this = this
      this.$axios.post('/admin/project/list', params).then((resp) => {
        if (resp && resp.data.code === 200) {
          _this.projectList = resp.data.data.list
        }
      })
    },
    // 保存or更新成员信息 真实执行sql语句
    teamSave() {
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          this.$confirm('确认添加吗？', '提示', {}).then(() => {
            if (this.memberData !== '' && this.memberData.length !== 0) {
              // const para = Object.assign({}, this.editForm)
              // if (this.dialogStatus === "update") {
              //   saveFlag = '1';
              // }
              let teacher = localStorage.getItem('username')
              // 判断当前用户是否为空
              if (
                teacher === 'undefined' ||
                teacher === 'null' ||
                teacher === undefined ||
                teacher === null
              ) {
                teacher = ''
              }
              let _this = this
              let teamInfo = {
                title: this.competitionName,
                projectId: this.editForm.projectId,
                teamNo: this.editForm.teamNo,
                teamName: _this.editForm.teamName,
                teacher: teacher,
                status: 0,
                memberList: this.memberData
              }
              this.$axios
                .post('/admin/team/insert', teamInfo)
                .then(function (res) {
                  _this.dialogFormVisible = false
                  if (res.data.code === 200) {
                    // 保存成功
                    _this.memberData.forEach((obj) => {
                      obj.teamNo = res.data.teamNo
                    })
                    _this.getTeam()
                  } else {
                    // 保存失败
                    _this.$message.error(res.data.message)
                  }
                })
                .catch((error) => {
                  _this.dialogFormVisible = false
                  _this.$message.error('添加失败：' + error)
                })
            } else {
              this.$message({
                message: '请先添加团队成员',
                type: 'error'
              })
            }
          })
        }
      })
    },
    // 修改
    teamUpdate() {
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          this.$confirm('确认修改吗？', '提示', {}).then(() => {
            if (this.memberData !== '' && this.memberData.length !== 0) {
              let teacher = localStorage.getItem('username')
              // 判断当前用户是否是教师
              // if (teacher === 'undefined' || teacher === 'null' || teacher === undefined || teacher === null) {
              //     teacher = ''
              // }
              let _this = this
              let teamInfo = {
                title: this.competitionName,
                projectId: this.editForm.projectId,
                teamNo: this.editForm.teamNo,
                teamName: _this.editForm.teamName,
                teacher: teacher,
                status: '0',
                memberList: this.memberData
              }
              this.$axios
                .put('/admin/team/update', teamInfo)
                .then(function (res) {
                  _this.dialogFormVisible = false
                  if (res.data.code === 200) {
                    // 保存成功
                    _this.$message.success(res.data.message)
                    _this.getTeam()
                  } else {
                    // 保存失败
                    _this.$message.error(res.data.message)
                  }
                })
                .catch((error) => {
                  _this.dialogFormVisible = false
                  _this.$message.error('修改失败：' + error)
                })
            } else {
              this.$message({
                message: '请先添加团队成员',
                type: 'error'
              })
            }
          })
        }
      })
    },
    // 添加团队成员 仅为前端展示 实际还未插入数据库
    addMember() {
      this.$refs.member
        .validate((valid) => {
          if (valid) {
            this.$confirm('确认添加吗？', '提示', {}).then(() => {
              this.memberData.push(this.member)
              this.innerVisible = false
            })
          }
        })
        .catch(() => { })
    },
    // 提交参赛申请
    handleCommit(index, row) {
      this.$confirm('确认提交参赛信息吗?', '提示', {
        type: 'warning'
      })
        .then(() => {
          if (row.status === '0') {
            let team = {
              teamNo: row.teamNo,
              status: '1',
              conStatus: '1'
            }
            let _this = this
            _this.$axios
              .put('/admin/team/commit', team)
              .then((res) => {
                if (res.data.code === 200) {
                  _this.getTeam()
                  _this.$message.success(res.data.message)
                } else {
                  _this.$message.error(res.data.message)
                }
              })
              .catch((error) => {
                console.log('participate error-->', error)
              })
          } else if (row.status === '1') {
            this.$message.error('已经提交，不必重复提交')
          }
        })
        .catch(() => { })
    },
    // 显示新增界面
    handleAdd: function () {
      this.dialogStatus = 'add'
      this.dialogFormVisible = true
      // 置空
      this.memberData = []
      this.editForm = {}
      this.competitionName = ''
    },
    // 处理添加团队成员操作
    handleAddTeam() {
      // 先判断是否选择了参赛项目
      if (
        this.editForm.projectId === null ||
        this.editForm.projectId === undefined ||
        this.editForm.projectId === ''
      ) {
        this.$message.warning('请选择参赛项目')
        return
      }

      // 找到第一个匹配条件的项目后直接返回，无需遍历全部
      const currentProject = this.projectList.find(obj => {
        // 匹配当前选中的项目编号
        return obj.projectId === this.editForm.projectId
      })

      if (currentProject) {

        let formatStr = ''

        if (currentProject.format) {
          formatStr = currentProject.format.trim()
        }

        const maxMemberCount = Number(formatStr || '1')

        const validMaxCount = isNaN(maxMemberCount) ? 1 : maxMemberCount

        //当前成员数 >= 最大允许成员数时禁止添加
        if (this.memberData.length >= validMaxCount) {
          this.$message.warning(`对不起不能再添加成员！该赛事最多允许${validMaxCount}名成员`)
          return
        }
      }

      // 若不是单人制/成员数未达上限，正常弹出添加成员弹窗
      this.memberStatus = 'add'
      this.innerVisible = true
      this.member = {}
    },
    // 显示编辑界面
    handleEdit(index, row) {
      this.memberData = []
      this.dialogStatus = 'update'
      this.dialogFormVisible = true

      this.memberData = this.teamData[index].memberList
      this.competitionName = row.title
      this.editForm = Object.assign({}, row)
    },
    // 显示详情框
    handleDetail(index, row) {
      this.dialogStatus = 'detail'
      this.dialogFormVisible = true
      this.memberData = []
      this.memberData = this.teamData[index].memberList

      this.competitionName = row.title
      this.editForm = Object.assign({}, row)
    },
    // 显示成员编辑界面
    memberEdit(index, row) {
      this.memberStatus = 'update'
      this.innerVisible = true
      this.member = row

      this.memberIndex = index
      // this.editForm = Object.assign({}, row);
    },
    // 修改团队成员信息
    editMember() {
      this.memberData[this.memberIndex] = this.member
      this.innerVisible = false
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
    },

    // 删除团队成员信息
    memberDel(index, row) {
      this.$confirm('确认删除选中记录吗？', '提示', {
        type: 'warning'
      })
        .then(() => {
          if (this.dialogStatus === 'add') {
            // 实际未插入，仅展示在前端，直接删除前端资源
            this.memberData.splice(index)
            this.$message.success('删除成功')
          } else if (this.dialogStatus === 'update') {
            // 根据是否有memberId来判断是否为前端资源或者真实的数据库记录
            const memberId = row.memberNo
            if (!memberId) {
              this.memberData.splice(index)
              this.$message.success('删除成功')
              // 直接返回 不再继续执行调用sql语句
              return
            }
            // 实际为数据库记录，则调用接口删除
            this.$axios
              .delete('/admin/member/delete/' + memberId)
              .then((res) => {
                if (res.data.code === 200) {
                  //  仅删除当前记录
                  this.memberData.splice(index);
                  this.$message.success(res.data.message);
                } else {
                  this.$message.error(res.data.message);
                }
              })
              .catch((error) => {
                console.error('删除接口异常：', error);
                this.$message.error("删除失败");
              })
          }
        })
        .catch((error) => {
          // 取消删除操作
          // this.$message.warning(error)
        })
    },
    // 删除团队
    singleDel(index, row) {
      let _this = this
      this.$confirm('确认删除团队名为' + row.title + '的记录吗?', '提示', {
        type: 'warning'
      })
        .then(() => {
          if (row.status !== '3') {
            if (row.teamNo !== undefined) {
              _this.$axios
                .delete('/admin/team/' + row.teamNo)
                .then(function (resp) {
                  if (resp.data.code === 200) {
                    _this.$message.success(resp.data.message)
                    _this.getTeam()
                  } else {
                    _this.$message.error(resp.data.message)
                  }
                })
            }
          } else {
            _this.$message.error('删除失败,不能删除已经通过审核的报名信息')
          }
        })
        .catch(() => { })
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
