<!-- 团队管理页面 -->
<template>
    <div class="page-wrap">
        <!-- 面包屑导航 -->
        <el-row style="margin: 10px 0px 0px 3px">
            <el-breadcrumb separator-class="el-icon-arrow-right">
                <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
                <el-breadcrumb-item>用户管理</el-breadcrumb-item>
                <el-breadcrumb-item>团队管理</el-breadcrumb-item>
            </el-breadcrumb>
        </el-row>
        <!-- 新增团队按钮 -->
        <div class="page-header" style="margin-top: 20px;">
            <el-button type="success" @click="addTeamDialogVisible = true">新增团队</el-button>
        </div>

        <!-- 筛选表单 -->
        <el-form :model="searchForm" inline class="search-form" @submit.prevent="getTeamList">
            <el-form-item label="赛题" prop="title">
                <el-input v-model="searchForm.title" placeholder="请输入赛题名称" clearable
                    @keyup.enter.native="handleEnterSearch" />
            </el-form-item>
            <el-form-item label="审核状态" prop="status">
                <el-select v-model="searchForm.status" placeholder="请选择审核状态" clearable>
                    <el-option label="待提交" value="0" />
                    <el-option label="待审核" value="1" />
                    <el-option label="审核不通过" value="2" />
                    <el-option label="审核通过" value="3" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="getTeamList">查询</el-button>
                <el-button @click="resetSearchForm">重置</el-button>
            </el-form-item>
        </el-form>

        <!-- 团队列表表格 -->
        <el-table v-loading="loading" :data="teamList" border stripe style="width: 100%; margin-top: 10px">
            <el-table-column prop="teamNo" label="团队编号" align="center" min-width="120" />
            <el-table-column prop="teamName" label="团队名称" align="center" min-width="150" />
            <el-table-column prop="title" label="赛题" align="center" min-width="200" />
            <el-table-column prop="projectId" label="项目编号" align="center" width="100" />
            <el-table-column prop="enrollTime" label="注册时间" align="center" width="180" />
            <el-table-column prop="teacher" label="指导老师" align="center" min-width="120" />
            <el-table-column prop="status" label="审核状态" align="center" width="120">
                <template slot-scope="scope">
                    <el-tag :type="getStatusTagType(scope.row.status)">
                        {{ getStatusText(scope.row.status) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="conStatus" label="结题状态" align="center" width="120">
                <template slot-scope="scope">
                    <el-tag :type="getConStatusTagType(scope.row.conStatus)">
                        {{ getConStatusText(scope.row.conStatus) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="300">
                <template slot-scope="scope">
                    <el-button type="text" @click="viewTeamMember(scope.row)">查看成员</el-button>
                    <el-button type="text" :disabled="scope.row.status === '0' || scope.row.status === '3'"
                        @click="showAuditDialog(scope.row)">
                        审核
                    </el-button>
                    <el-button type="text"
                        :disabled="(scope.row.conStatus === null || scope.row.conStatus === undefined ? '' : scope.row.conStatus).toString() !== '0'"
                        @click="commitProject(scope.row)"
                        :class="{ 'btn-commit-disabled': (scope.row.conStatus === null || scope.row.conStatus === undefined ? '' : scope.row.conStatus).toString() !== '0' }">
                        提交项目
                    </el-button>
                    <el-button type="text" @click="deleteTeam(scope.row)" style="color: #f56c6c">
                        删除团队
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页组件-->
        <div class="pagination-wrap" style="margin-top: 20px; text-align: right">
            <el-pagination :current-page="pagination.pageNum" :page-size="pagination.pageSize" :total="pagination.total"
                layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" />
        </div>

        <!-- 团队成员查看弹窗 -->
        <el-dialog :visible.sync="memberDialogVisible" title="团队成员信息" width="800px" destroy-on-close>
            <el-table :data="memberList" border stripe style="width: 100%">
                <el-table-column prop="studentNo" label="学号" align="center" min-width="120" />
                <el-table-column prop="studentName" label="学生姓名" align="center" min-width="100" />
                <el-table-column prop="collegeName" label="学院名称" align="center" min-width="150" />
                <el-table-column prop="className" label="班级名称" align="center" min-width="120" />
                <el-table-column prop="grade" label="年级" align="center" width="80" />
                <el-table-column prop="major" label="专业" align="center" min-width="120" />
                <el-table-column prop="mail" label="邮箱" align="center" min-width="180" />
                <el-table-column prop="mobileNumber" label="手机号码" align="center" width="110" />
                <el-table-column label="操作" align="center" width="100">
                    <template slot-scope="scope">
                        <el-button type="text" style="color: #f56c6c" @click="deleteTeamMember(scope.row)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div slot="footer">
                <el-button @click="memberDialogVisible = false">关闭</el-button>
            </div>
        </el-dialog>

        <!-- 团队审核弹窗 -->
        <el-dialog :visible.sync="auditDialogVisible" title="团队审核" width="500px" destroy-on-close>
            <el-form :model="auditForm" label-width="80px" :rules="auditRules" ref="auditFormRef">
                <el-form-item label="团队编号" prop="teamNo">
                    <el-input v-model="auditForm.teamNo" disabled />
                </el-form-item>
                <el-form-item label="团队名称" prop="teamName">
                    <el-input v-model="auditForm.teamName" disabled />
                </el-form-item>
                <el-form-item label="审核结果" prop="status" required>
                    <el-select v-model="auditForm.status" placeholder="请选择审核结果">
                        <el-option label="待审核" value="1" />
                        <el-option label="审核不通过" value="2" />
                        <el-option label="审核通过" value="3" />
                    </el-select>
                </el-form-item>
                <el-form-item label="审核意见" prop="opinion">
                    <el-input v-model="auditForm.opinion" type="textarea" rows="4" placeholder="请输入审核意见（选填）" />
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="auditDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitAudit">提交审核</el-button>
            </div>
        </el-dialog>

        <!-- 新增团队弹窗 -->
        <el-dialog :visible.sync="addTeamDialogVisible" title="新增团队" width="800px" destroy-on-close
            @close="handleAddTeamDialogClose">
            <el-form :model="addTeamForm" label-width="80px" :rules="addTeamRules" ref="addTeamFormRef">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="团队编号" prop="teamNo">
                            <el-input v-model="addTeamForm.teamNo" placeholder="系统自动生成，无需输入" disabled />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="团队名称" prop="teamName">
                            <el-input v-model="addTeamForm.teamName" placeholder="请输入团队名称" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <!-- 赛题改为下拉框选择 -->
                        <el-form-item label="赛题" prop="title">
                            <el-select v-model="addTeamForm.title" placeholder="请选择赛题" clearable
                                @change="handleProjectChange">
                                <el-option v-for="item in projectList" :key="item.projectId" :label="item.name"
                                    :value="item.name" :data-project-id="item.projectId" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <!-- 项目编号改为禁用，设置为自动填充 -->
                        <el-form-item label="项目编号" prop="projectId">
                            <el-input v-model="addTeamForm.projectId" type="number" placeholder="选择赛题后自动填充" disabled />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <!-- 指导老师改为下拉框 -->
                        <el-form-item label="指导老师" prop="teacher">
                            <el-select v-model="addTeamForm.teacher" placeholder="请选择指导老师" clearable>
                                <el-option v-for="item in teacherList" :key="item.id" :label="item.name"
                                    :value="item.name" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="注册时间" prop="enrollTime">
                            <el-date-picker v-model="addTeamForm.enrollTime" type="datetime" placeholder="选择注册时间"
                                format="yyyy-MM-dd HH:mm:ss" value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <!-- 新增团队成员 -->
                <el-form-item label="团队成员" prop="memberList">
                    <el-button type="primary" text @click="addTeamMemberItem">添加成员</el-button>

                    <el-table :data="addTeamForm.memberList" border stripe style="width: 100%; margin-top: 10px">
                        <el-table-column label="学号" align="center" min-width="120">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.studentNo" placeholder="请输入学号" />
                            </template>
                        </el-table-column>
                        <el-table-column label="学生姓名" align="center" min-width="100">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.studentName" placeholder="请输入姓名" />
                            </template>
                        </el-table-column>
                        <el-table-column label="学院名称" align="center" min-width="150">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.collegeName" placeholder="请输入学院名称" />
                            </template>
                        </el-table-column>
                        <el-table-column label="班级名称" align="center" min-width="120">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.className" placeholder="请输入班级名称" />
                            </template>
                        </el-table-column>
                        <el-table-column label="年级" align="center" width="80">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.grade" placeholder="请输入年级" />
                            </template>
                        </el-table-column>
                        <el-table-column label="专业" align="center" min-width="120">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.major" placeholder="请输入专业" />
                            </template>
                        </el-table-column>
                        <el-table-column label="邮箱" align="center" min-width="180">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.mail" placeholder="请输入邮箱" />
                            </template>
                        </el-table-column>
                        <el-table-column label="手机号码" align="center" width="110">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.mobileNumber" placeholder="请输入手机号" />
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" align="center" width="80">
                            <template slot-scope="scope">
                                <el-button type="text" style="color: #f56c6c"
                                    @click="removeTeamMemberItem(scope.$index)">
                                    删除
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="addTeamDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitAddTeam">提交</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import axios from '../../../utils/axios'
import { Message, MessageBox } from 'element-ui'

export default {
    name: 'TeamManagement',
    data() {
        return {
            // 加载状态
            loading: false,
            // 筛选表单
            searchForm: {
                title: '',
                status: ''
            },
            // 分页参数
            pagination: {
                pageNum: 1, //当前页码
                pageSize: 10, //每页显示行数
                total: 0
            },
            // 团队列表
            teamList: [],
            // 成员弹窗
            memberDialogVisible: false,
            memberList: [],
            // 审核弹窗
            auditDialogVisible: false,
            auditForm: {
                teamNo: '',
                teamName: '',
                status: '',
                opinion: ''
            },
            // 审核表单校验规则
            auditRules: {
                status: [{ required: true, message: '请选择审核结果', trigger: 'change' }]
            },
            // 新增团队弹窗
            addTeamDialogVisible: false,
            addTeamForm: {
                teamNo: '',
                teamName: '',
                title: '',
                projectId: '',
                teacher: '',
                enrollTime: '',
                status: '0',
                conStatus: '0',
                memberList: []
            },
            // 赛题列表（从sys_project查询）
            projectList: [],
            // 指导老师列表（admin_role表中name为teacher的用户） 
            teacherList: [],
            // 新增团队校验规则
            addTeamRules: {
                teamName: [{ required: true, message: '请输入团队名称', trigger: 'blur' }],
                // 赛题校验触发方式改为change
                title: [{ required: true, message: '请选择赛题', trigger: 'change' }],
                // 项目编号校验触发方式改为change，提示语适配自动填充
                projectId: [{ required: true, message: '请选择赛题以自动填充项目编号', trigger: 'change' }],
                // 指导老师校验触发方式改为change
                teacher: [{ required: true, message: '请选择指导老师', trigger: 'change' }],
                enrollTime: [{ required: true, message: '请选择注册时间', trigger: 'change' }],
                // 校验成员列表至少有1条数据
                memberList: [{
                    required: true,
                    validator: (rule, value, callback) => {
                        if (value && value.length > 0) {
                            callback()
                        } else {
                            callback(new Error('请至少添加1名团队成员'))
                        }
                    },
                    trigger: 'change'
                }]
            }
        }
    },
    mounted() {
        // 获取团队列表
        this.getTeamList()
        // 初始化加载赛题和老师列表
        this.getProjectList()
        this.getTeacherList()
    },
    methods: {
        handleEnterSearch() {
            this.pagination.pageNum = 1 // 重置页码为第1页
            this.getTeamList()
        },
        // 格式化时间
        formatDate(dateStr) {
            if (!dateStr) return ''
            try {
                const date = new Date(dateStr)
                return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`
            } catch (error) {
                return dateStr
            }
        },
        // 获取审核状态文本
        getStatusText(status) {
            switch (status) {
                case '0':
                    return '待提交'
                case '1':
                    return '待审核'
                case '2':
                    return '审核不通过'
                case '3':
                    return '审核通过'
                default:
                    return '-'
            }
        },
        // 获取审核状态标签类型
        getStatusTagType(status) {
            switch (status) {
                case '0':
                    return 'info'
                case '1':
                    return 'warning'
                case '2':
                    return 'danger'
                case '3':
                    return 'success'
                default:
                    return ''
            }
        },
        // 获取结题状态文本
        getConStatusText(conStatus) {
            switch (conStatus) {
                case '0':
                    return '待提交'
                case '1':
                    return '待审核'
                case '2':
                    return '结题不通过'
                case '3':
                    return '结题通过'
                default:
                    return '-'
            }
        },
        // 获取结题状态标签类型
        getConStatusTagType(conStatus) {
            switch (conStatus) {
                case '0':
                    return 'info'
                case '1':
                    return 'warning'
                case '2':
                    return 'danger'
                case '3':
                    return 'success'
                default:
                    return ''
            }
        },
        // 获取团队列表
        async getTeamList() {
            this.loading = true
            try {
                const teamQuery = {
                    title: this.searchForm.title,
                    status: this.searchForm.status
                }
                const url = `/api/admin/team/${this.pagination.pageSize}/${this.pagination.pageNum}`
                const res = await axios.post(url, teamQuery)
                if (res.data.code === 200) {
                    this.teamList = res.data.data.list
                    this.pagination.total = res.data.data.total
                } else {
                    Message.error('获取团队列表失败：' + res.data.message)
                }
            } catch (error) {
                console.error('获取团队列表异常：', error)
                Message.error('获取团队列表失败，请稍后重试')
            } finally {
                this.loading = false
            }
        },
        // 获取赛题列表（从sys_project表查询）
        async getProjectList() {
            try {
                // 后端实际接口
                const res = await axios.post('/api/admin/project/list', { status: '3' })
                if (res.data.code === 200) {
                    this.projectList = res.data.data.list
                    console.log('获取赛题列表成功：', this.projectList)
                } else {
                    Message.error('获取赛题列表失败：' + res.data.message)
                }
            } catch (error) {
                console.error('获取赛题列表异常：', error)
                Message.error('获取赛题列表失败，请稍后重试')
            }
        },
        // 获取指导老师列表（角色为teacher的用户）
        async getTeacherList() {
            try {
                // 后端实际接口
                const res = await axios.get('/api/admin/teacher/list')
                if (res.data.code === 200) {
                    this.teacherList = res.data.data.teacherList

                } else {
                    Message.error('获取指导老师列表失败：' + res.data.message)
                }
            } catch (error) {
                console.error('获取指导老师列表异常：', error)
                Message.error('获取指导老师列表失败，请稍后重试')
            }
        },
        // 赛题选择变更事件：自动填充项目编号
        handleProjectChange(val) {
            if (!val) {
                this.addTeamForm.projectId = ''
                return
            }
            // 找到选中的赛题，提取projectId
            const selectedProject = this.projectList.find(item => item.name === val)
            if (selectedProject) {
                this.addTeamForm.projectId = selectedProject.projectId
            }
        },
        // 重置筛选表单
        resetSearchForm() {
            this.searchForm.title = ''
            this.searchForm.status = ''
            this.pagination.pageNum = 1
            this.getTeamList()
        },
        // 分页-每页条数改变
        handleSizeChange(val) {
            this.pagination.pageSize = val
            this.getTeamList()
        },
        // 分页-页码改变
        handleCurrentChange(val) {
            this.pagination.pageNum = val
            this.getTeamList()
        },
        // 查看团队成员
        async viewTeamMember(team) {
            if (!team.teamNo) {
                this.$message.warning('团队编号不能为空')
                return
            }
            this.loading = true
            try {
                const res = await axios.get(`/api/admin/team/member/${team.teamNo}`)
                if (res.data.code === 200) {
                    this.memberList = res.data.data.memberList
                    this.memberDialogVisible = true
                    this.auditForm.teamNo = team.teamNo
                } else {
                    this.$message.error('获取团队成员失败：' + res.data.message)
                }
            } catch (error) {
                console.error('获取团队成员异常：', error)
                this.$message.error('获取团队成员失败，请稍后重试')
            } finally {
                this.loading = false
            }
        },
        // 删除团队成员
        async deleteTeamMember(member) {
            if (!member.memberNo) {
                Message.warning('成员编号不能为空')
                return
            }
            try {
                await MessageBox.confirm(
                    '确定要删除该团队成员吗？',
                    '提示',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'info'
                    }
                )
                this.loading = true
                const res = await axios.delete(`/api/admin/member/delete/${member.memberNo}`)
                if (res.data.code === 200) {
                    Message.success('删除成员成功')
                    this.viewTeamMember({ teamNo: this.auditForm.teamNo })
                } else {
                    Message.error('删除成员失败：' + res.data.message)
                }
            } catch (error) {
                if (error.type === 'cancel') {
                    console.info('取消删除')
                  
                }
            } finally {
                this.loading = false
            }
        },
        // 显示审核弹窗
        showAuditDialog(team) {
            if (!team.teamNo) {
                Message.warning('团队编号不能为空')
                return
            }
            this.auditForm.teamNo = team.teamNo
            this.auditForm.teamName = team.teamName
            this.auditForm.status = team.status
            this.auditForm.opinion = team.opinion || ''
            this.auditDialogVisible = true
        },
        // 提交审核
        async submitAudit() {
            try {
                await this.$refs.auditFormRef.validate()
                this.loading = true
                const res = await axios.put('/api/admin/team/examine', {
                    teamNo: this.auditForm.teamNo,
                    status: this.auditForm.status,
                    opinion: this.auditForm.opinion
                })
                if (res.data.code === 200) {
                    Message.success('审核提交成功')
                    this.auditDialogVisible = false
                    // 刷新列表
                    this.getTeamList()
                } else {
                    Message.error('审核提交失败：' + res.data.message)
                }
            } catch (error) {
                if (error.name !== 'ValidationError') {
                    console.error('提交审核异常：', error)
                    Message.error('审核提交失败，请稍后重试')
                }
            } finally {
                this.loading = false
            }
        },
        // 删除团队
        async deleteTeam(team) {
            if (!team.teamNo) {
                Message.warning('团队编号不能为空')
                return
            }
            try {
                await MessageBox.confirm(
                    '确定要删除该团队吗？删除后将无法恢复！',
                    '警告',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                )
                this.loading = true
                const res = await axios.delete(`/api/admin/team/${team.teamNo}`)
                if (res.data.code === 200) {
                    Message.success('删除团队成功')
                    this.getTeamList()
                } else {
                    console.info('取消删除操作')
                }
            } catch (error) {
                if (error.type === 'cancel') {
                    console.info('取消删除操作')
                }
            } finally {
                this.loading = false
            }
        },
        // 提交项目
        async commitProject(team) {
            if (!team.teamNo) {
                Message.warning('团队编号不能为空')
                return
            }
            try {
                await MessageBox.confirm(
                    '确定要提交该项目吗？提交后将进入结题审核流程',
                    '提示',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'info'
                    }
                )
                this.loading = true
                // 提交项目设置提交状态为1
                const res = await axios.put('/api/admin/team/commit', {
                    teamNo: team.teamNo,
                    status: '1',
                    conStatus: '1'
                })
                if (res.data.code === 200) {
                    Message.success('项目提交成功')
                    this.getTeamList()
                } else {
                    Message.error('项目提交失败：' + res.data.message)
                }
            } catch (error) {
                if (error.type === 'cancel') {
                    console.info('取消提交操作')
                }
            } finally {
                this.loading = false
            }
        },
        // 添加团队成员项
        addTeamMemberItem() {
            this.addTeamForm.memberList.push({
                studentNo: '',
                studentName: '',
                collegeName: '',
                className: '',
                grade: '',
                major: '',
                mail: '',
                mobileNumber: '',
                deleteFlag: 0
            })
        },
        // 移除团队成员项
        removeTeamMemberItem(index) {
            this.addTeamForm.memberList.splice(index, 1)
        },
        // 提交新增团队
        async submitAddTeam() {
            try {
                // 校验表单
                await this.$refs.addTeamFormRef.validate()

                const teamVo = { ...this.addTeamForm }
                // 清空 teamNo（由后端自动生成）
                teamVo.teamNo = ''

                this.loading = true
                // 调用后端接口
                const res = await axios.post('/api/admin/team/insert', teamVo)

                if (res.data.code === 200) {
                    Message.success('添加团队成功')
                    //关闭弹窗 + 重置表单 + 清空成员列表 + 刷新团队列表
                    this.addTeamDialogVisible = false
                    this.$refs.addTeamFormRef.resetFields()
                    this.addTeamForm.memberList = [] // 清空成员列表
                    this.getTeamList() //刷新团队列表
                } else {
                    Message.error('添加团队失败：' + res.data.message)
                }
            } catch (error) {
                if (error.name !== 'ValidationError') {
                    Message.error('添加团队失败，必填项没填')
                }
            } finally {
                this.loading = false
            }
        },
        // 弹窗关闭时清空赛题、项目编号、指导老师选择
        handleAddTeamDialogClose() {
            this.addTeamForm.memberList = []
            this.addTeamForm.title = ''
            this.addTeamForm.projectId = ''
            this.addTeamForm.teacher = ''
            if (this.$refs.addTeamFormRef) {
                this.$refs.addTeamFormRef.resetFields()
            }
        }
    }
}
</script>

<style scoped>
.btn-commit-disabled {
    color: #c0c4cc !important;
    cursor: not-allowed !important;
    pointer-events: none;
}

.page-wrap {
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

.pagination-wrap {
    margin-top: 16px;
}
</style>