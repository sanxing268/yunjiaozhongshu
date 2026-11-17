<!--  结题审核-->
<template>
  <section class="app-container">
    <el-row style="margin: 18px 0px 0px 18px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>项目结题管理</el-breadcrumb-item>
        <el-breadcrumb-item>结题申请</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <!--工具条-->
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px">
      <el-form :inline="true" :model="filters" @submit.native.prevent>
        <el-form-item>
          <el-input v-model="filters.name" placeholder="赛事名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-on:click="getConclusion">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <!--列表-->
    <el-table :data="conclusionData" border highlight-current-row style="width: 100%" cell-style="font-weight: 700;">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="project.name" label="赛事名称" min-width="120">
      </el-table-column>
      <el-table-column prop="teamVo.teamNo" sortable label="团队编号" min-width="120">
      </el-table-column>
      <el-table-column prop="teamVo.teamName" label="团队名称" min-width="110">
      </el-table-column>
      <el-table-column prop="teamVo.enrollTime" sortable label="报名时间" min-width="150">
      </el-table-column>
      <el-table-column prop="project.organization" label="组赛单位" min-width="120">
      </el-table-column>
      <el-table-column prop="project.phone" label="联系电话" min-width="120">
      </el-table-column>
      <el-table-column prop="project.mail" label="联系邮箱" min-width="120">
      </el-table-column>
      <el-table-column prop="project.format" label="赛制" min-width="120" :formatter="formatFormat">
      </el-table-column>
      <el-table-column prop="teamVo.conStatus" label="审核状态" :filter-method="filterStatus" width="100" :filters="[
        { text: '通过', value: '3' },
        { text: '不通过', value: '2' },
        { text: '待审核', value: '1' },
        { text: '待提交', value: '0' },
      ]" filter-placement="bottom-end">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.teamVo.conStatus == '0'"
            :type="scope.row.teamVo.conStatus === '3' ? 'primary' : 'success'" disable-transitions>待提交
          </el-tag>
          <el-tag v-if="scope.row.teamVo.conStatus == '1'"
            :type="scope.row.teamVo.conStatus === '3' ? 'primary' : 'success'" disable-transitions>待审核
          </el-tag>
          <el-tag v-if="scope.row.teamVo.conStatus == '3'"
            :type="scope.row.teamVo.conStatus === '3' ? 'primary' : 'success'" disable-transitions>已通过
          </el-tag>
          <el-tag v-if="scope.row.teamVo.conStatus == '2'"
            :type="scope.row.teamVo.conStatus === '3' ? 'primary' : 'success'" disable-transitions>不通过
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="180" align="center">
        <template slot-scope="scope">
          <el-button size="small" v-show="scope.row.teamVo.conStatus === '1'" type="primary"
            @click="handleEdit(scope.$index, scope.row)">审核
          </el-button>
          <el-button size="small" type="success" @click="handleDetail(scope.$index, scope.row)">详情
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
    <el-dialog :title="titleMap[dialogStatus]" width="960px" :visible.sync="dialogFormVisible"
      :close-on-click-modal="false">
      <el-form :model="editForm" label-width="120px" :rules="editFormRules" ref="editForm">
        <el-row>
          <el-col :span="8">
            <el-form-item label="赛事名称" prop="project.name">
              <el-input v-model="competitionName" disabled />
            </el-form-item>
            <el-form-item label="获奖名次" prop="prize.level">
              <el-select v-model="editForm.prize.level" :disabled="dialogStatus == 'detail'" @change="Changed"
                filterable placeholder="请选择获奖名次">
                <el-option v-for="item in prizeData" :key="item.prizeId" :label="item.level" :value="item.prizeId">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="项目编号" prop="projectId">
              <el-input v-model="editForm.project.projectId" disabled filterable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="团队编号" prop="teamNo">
              <el-input v-model="selectTeamNo" disabled filterable />
            </el-form-item>
          </el-col>
          <el-table :data="memberData" highlight-current-row style="width: 100%; border: solid slategray 1px">
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
            <el-table-column prop="mail" label="邮箱" min-width="120">
            </el-table-column>
            <el-table-column prop="mobileNumber" label="手机号码" min-width="120">
            </el-table-column>
          </el-table>
          <!--添加or修改-->
          <el-form-item label="结题附件" style="margin-top: 20px">
            <el-input v-model="fileName" disabled filterable />
          </el-form-item>
          <el-button type="text" @click="download()">下载结题附件</el-button>
        </el-row>
      </el-form>
      <!--审核意见界面-->
      <el-form :model="editForm" label-width="120px" :rules="editFormRules" :hidden="dialogStatus == 'detail'"
        ref="editForm">
        <el-form-item label="结题审核意见" prop="opinion">
          <el-input type="textarea" v-model="editForm.opinion" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="审核" prop="conStatus">
          <el-radio-group v-model="editForm.conStatus">
            <el-radio border label="3">通过</el-radio>
            <el-radio border label="2">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <el-form :model="projectForm" label-width="120px" v-show="dialogStatus == 'detail'">
        <el-row>
          <input type="hidden" v-model="projectForm.id" />
          <el-col :span="8">
            <el-form-item label="申请立项日期">
              <el-date-picker v-model="projectForm.projectDate" type="datetime" disabled
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="指导老师">
              <el-input v-model="projectForm.teacherId" :disabled="dialogStatus == 'detail'" auto-complete="off"
                style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="竞赛结束日期">
              <el-date-picker v-model="projectForm.endDate" type="datetime" disabled value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="竞赛起始日期">
              <el-date-picker v-model="projectForm.startDate" type="datetime" disabled
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="专业">
              <el-input v-model="projectForm.major" disabled auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="组赛单位">
              <el-input v-model="projectForm.organization" disabled auto-complete="off" style="width: 220px"></el-input>
            </el-form-item>
            <el-form-item label="赛制">
              <el-radio-group v-model="projectForm.format">
                <el-radio :disabled="dialogStatus == 'detail'" class="radio" label="1">单人赛
                </el-radio>
                <el-radio :disabled="dialogStatus == 'detail'" class="radio" label="2">团体赛
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="主办单位">
              <el-input v-model="projectForm.sponsor" disabled auto-complete="off" style="width: 180px"></el-input>
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="projectForm.phone" disabled auto-complete="off" style="width: 180px"></el-input>
            </el-form-item>
            <el-form-item label="邮件">
              <el-input v-model="projectForm.mail" disabled auto-complete="off" style="width: 180px"></el-input>
            </el-form-item>
            <el-form-item label="竞赛承办单位">
              <el-input v-model="projectForm.contractor" disabled auto-complete="off" style="width: 180px"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="论证组赛的目的和意义">
          <el-input type="textarea" v-model="projectForm.objective" disabled auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="审核意见" v-show="projectForm.opinion != undefined">
          <el-input type="textarea" v-model="projectForm.opinion" disabled auto-complete="off"></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" v-show="dialogStatus != 'detail'">取 消
        </el-button>
        <el-button @click.native="dialogFormVisible = false" v-show="dialogStatus == 'detail'">关闭
        </el-button>
        <el-button v-if="dialogStatus == 'update'" type="primary" @click="examine">修改
        </el-button>
      </div>
    </el-dialog>
  </section>
</template>

<script>
import ImgUpload from './ImgUpload'

export default {
  components: { ImgUpload },
  data() {
    return {
      uploadFlag: false, // 上传文件标识
      conclusionData: [], // 项目结题信息
      teamList: [], // 通过后台获取的团队数据
      memberList: [], // 添加的团队成员信息
      projectList: [], // 审核通过的项目信息
      memberData: [], // 团队成员信息存储
      teamData: [], // 团队信息存储
      prizeData: [], // 奖项信息存储
      conclusionFile: '', // 结题附件名称
      fileUrl: '', // 添加结题附件时访问路径
      fileName: '',
      titleMap: {
        update: '项目结题审核',
        detail: '项目结题详情'
      },
      url: '',
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
      projectData: [], // 项目立项数据
      sels: [], // 列表选中列
      competitionName: '', // 选择的赛事名称
      selectTeamNo: '', // 选中的团队编号
      filters: {
        name: ''
      },
      dialogStatus: '',
      memberStatus: '',
      dialogFormVisible: false,
      innerVisible: false,
      textMap: {
        update: '项目结题',
        detail: '项目结题详情'
      },
      // 项目信息
      projectForm: {
        // id:0,
        projectId: '',
        // status:"",
        // opinion:"",
        // 项目基本信息
        name: '',
        teacherId: '',
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
      },
      // 编辑界面数据
      editForm: {
        conclusionId: 0,
        applyId: '',
        teamNo: '',
        file: '',
        prizeId: '',
        capitalId: '',
        teacherId: '',
        status: '',
        // 项目基本信息
        project: {
          projectId: '',
          name: '',
          teacherId: '',
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
        },

        // 获奖情况
        prize: {
          prizeId: 0,
          level: ''
        }
      },
      // 数据校验
      editFormRules: {
        opinion: [
          { required: true, message: '请输入审核意见', trigger: 'blur' }
        ],
        conStatus: [
          { required: true, message: '请选择审核结果', trigger: 'blur' }
        ],
        // 获奖
        prize: {
          level: [
            { required: true, message: '请选择获奖级别', trigger: 'blur' }
          ]
        }
      }
    }
  },
  mounted() {
    this.getConclusion()
    this.getPrize()
  },
  methods: {
    // 查询项目结题信息
    getConclusion() {
      let conclusionInfo = {
        name: this.filters.name
      }
      let _this = this
      this.$axios
        .post('/admin/conclusion/' + this.pageSize + '/1', conclusionInfo)
        .then((resp) => {
          // 1. 打印后端返回的「完整响应对象」
          console.log('后端返回的完整响应数据：', resp)

          // 2. 打印后端返回的「核心业务数据」
          console.log('后端返回的业务核心数据：', resp.data)

          // 3. 按需打印更细粒度的数据（替换可选链，使用 && 做存在性判断，兼容旧环境）
          console.log('项目结题列表数据：', resp && resp.data && resp.data.data && resp.data.data.list)
          console.log('团队数据：', resp && resp.data && resp.data.data && resp.data.data.teamVo)
          console.log('数据总数：', resp && resp.data && resp.data.data && resp.data.data.total)

          if (resp && resp.data.code === 200) {
            _this.conclusionData = resp.data.data.list
            _this.teamData = resp.data.data.teamVo
            _this.teamData.forEach((item) => {
              _this.memberList.push(item.memberList)
            })
            _this.total = resp.data.data.total
          }
        })
        .catch((error) => {
          // 打印错误信息及详细响应
          console.log('conclusion- error --->', error)
          console.log('错误响应详情：', error && error.response) // 同样替换可选链为 &&
        })
    },
    handleCurrentChange(page) {
      let conclusionInfo = {
        name: this.filters.name
      }
      var _this = this
      this.$axios
        .post('/admin/conclusion/' + this.pageSize + '/' + page, conclusionInfo)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.conclusionData = resp.data.data.list
            _this.teamData = resp.data.data.teamVo
            _this.teamData.forEach((item) => {
              this.memberList.push(item.memberList)
            })
            _this.total = resp.data.data.total
          }
        })
        .catch((error) => {
          console.log('conclusion- error --->', error)
        })
    },
    getPrize() {
      var _this = this
      this.$axios
        .get('/admin/conclusion/prize')
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.prizeData = resp.data.data.list
          }
        })
        .catch((error) => {
          console.log('prize- error --->', error)
        })
    },
    examine() {
      let updateInfo = {
        conclusionId: this.editForm.conclusionId,
        conStatus: this.editForm.conStatus,
        teamNo: this.editForm.teamVo.teamNo,
        opinion: this.editForm.opinion,
        prizeId: this.editForm.prize.prizeId
      }
      let _this = this
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          this.$confirm('确认修改吗？', '提示', {}).then(() => {
            // var conclusionInfo = Object.assign({}, this.editForm)
            this.$axios
              .put('/admin/conclusion/examine', updateInfo)
              .then((res) => {
                if (res.data.code === 200) {
                  _this.dialogFormVisible = false
                  _this.getConclusion()
                  _this.$message.success(res.data.message)
                } else {
                  _this.$message.error(res.data.message)
                }
              })
              .catch((error) => {
                console.log('conclusion---add-->', error)
              })
          })
        }
      })
    },
    // 显示编辑界面
    handleEdit(index, row) {
      this.memberData = []
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.memberData = this.memberList[index]
      // 获取当前选中的项目结题附件信息
      if (row.file !== undefined && row.file !== null) {
        this.fileName = row.file.substring(row.file.lastIndexOf('/') + 1)
      } else {
        this.fileName = ''
      }
      this.selectTeamNo = row.teamVo.teamNo
      this.competitionName = row.project.name
      this.editForm = Object.assign({}, row)
      if (this.$refs.editForm !== undefined) {
        this.$refs.editForm.resetFields()
      }
    },
    download() {
      // 文件已经存储在 COS，直接使用 COS URL 下载
      const fileUrl = this.editForm.file || this.url

      if (!fileUrl || fileUrl === '') {
        this.$message.warning('没有可下载的文件')
        return
      }

      // 提取文件名
      let fileName = this.fileName
      if (!fileName && fileUrl) {
        const lastSlashIndex = fileUrl.lastIndexOf('/')
        if (lastSlashIndex !== -1) {
          // 提取文件名，可能包含时间戳前缀，需要处理
          const fullFileName = fileUrl.substring(lastSlashIndex + 1)
          // 如果文件名包含时间戳前缀（格式：时间戳_原文件名），提取原文件名
          const underscoreIndex = fullFileName.indexOf('_')
          if (underscoreIndex !== -1 && underscoreIndex > 0) {
            // 检查是否是时间戳格式（纯数字）
            const prefix = fullFileName.substring(0, underscoreIndex)
            if (/^\d+$/.test(prefix)) {
              // 是时间戳，提取后面的原文件名
              fileName = fullFileName.substring(underscoreIndex + 1)
            } else {
              fileName = fullFileName
            }
          } else {
            fileName = fullFileName
          }
        } else {
          fileName = fileUrl
        }
      }

      // 如果文件 URL 是完整的 HTTPS URL（COS），直接下载
      if (fileUrl.startsWith('http://') || fileUrl.startsWith('https://')) {
        // 创建下载链接
        const link = document.createElement('a')
        link.style.display = 'none'
        link.href = fileUrl
        link.setAttribute('download', fileName || 'download')
        link.setAttribute('target', '_blank') // 新窗口打开，避免跳转
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        this.$message.success('开始下载文件')
      } else {
        this.$message.error('文件 URL 格式不正确')
      }
    },
    // 显示详情框
    handleDetail(index, row) {
      this.dialogStatus = 'detail'
      this.dialogFormVisible = true
      this.memberData = []
      // 获取当前选中的项目结题附件信息
      if (row.file !== undefined && row.file !== null) {
        this.fileName = row.file.substring(row.file.lastIndexOf('/') + 1)
      } else {
        this.fileName = ''
      }
      this.memberData = this.memberList[index]
      this.selectTeamNo = row.teamVo.teamNo
      this.competitionName = row.project.name
      this.projectForm = Object.assign({}, row.project)
      this.editForm = Object.assign({}, row)
      if (this.$refs.editForm !== undefined) {
        this.$refs.editForm.resetFields()
      }
    },
    Changed(val) {
      var obj = {}
      obj = this.prizeData.find(function (item) {
        return item.prizeId === val
      })
      this.editForm.prize = {
        prizeId: val,
        level: obj.level
      }
    },
    formatFormat: function (row, column) {
      return row.format === '1' ? '单人赛' : '团体赛'
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
    filterStatus(value, row) {
      return row.teamVo.conStatus === value
    }
  }
}
</script>

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

.file {
  position: relative;
  display: inline-block;
  background: #d0eeff;
  border: 1px solid #99d3f5;
  border-radius: 4px;
  padding: 4px 12px;
  overflow: hidden;
  color: #1e88c7;
  text-decoration: none;
  text-indent: 0;
  line-height: 20px;
}

.file input {
  position: absolute;
  font-size: 100px;
  right: 0;
  top: 0;
  opacity: 0;
  cursor: pointer;
}

.file:hover {
  background: #aadffd;
  border-color: #78c3f3;
  color: #004974;
  text-decoration: none;
}
</style>
