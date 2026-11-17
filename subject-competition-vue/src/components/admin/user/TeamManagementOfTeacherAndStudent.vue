<!-- 新团队管理组件 -->
<template>
    <div class="page-wrap">
        <h2>团队管理</h2>
        <!-- 角色对应的标签页 -->
        <el-tabs v-model="activeTab" type="card" style="margin-top: 20px;">
            <!-- 「我创建的」标签（教师/学生通用） -->
            <el-tab-pane label="我创建的" name="created">
                <div class="team-card-wrap">
                    <!-- 创建团队按钮（仅「我创建的」标签显示） -->
                    <div style="display: flex; justify-content: flex-end; margin-bottom: 15px;">
                        <el-button type="primary" @click="openCreateTeamDialog">创建我的团队</el-button>
                    </div>
                    <!-- 我创建的团队列表 -->
                    <div class="team-card-list">
                        <el-card v-for="team in myCreatedTeams" :key="team.teamNo" class="team-card">
                            <div class="team-info">
                                <div class="team-info-item">
                                    <span class="label">团队编号：</span>
                                    <span class="value">{{ team.teamNo }}</span>
                                </div>
                                <div class="team-info-item">
                                    <span class="label">赛题：</span>
                                    <span class="value">{{ team.title }}</span>
                                </div>
                                <div class="team-info-item">
                                    <span class="label">团队名称：</span>
                                    <span class="value">{{ team.teamName }}</span>
                                </div>
                                <div class="team-info-item">
                                    <span class="label">指导老师：</span>
                                    <span class="value">{{ team.teacher }}</span>
                                </div>
                            </div>
                            <div class="team-actions">

                                <el-button type="text" @click="handleAddMember(team)">添加成员</el-button>
                                <el-button type="text" @click="handleEditMember(team)">编辑成员</el-button>
                                <!-- <el-button type="text" style="color: #f56c6c"
                                    @click="handleDeleteMember(team)">删除成员</el-button> -->
                                <el-button type="text" style="color: #f56c6c"
                                    @click="handleDeleteTeam(team)">删除团队</el-button>
                            </div>
                        </el-card>
                        <div v-if="myCreatedTeams.length === 0" class="empty-tip">暂无创建的团队</div>
                    </div>
                </div>
            </el-tab-pane>

            <!-- 第二个标签（教师显示我指导的/学生显示我参与的） -->
            <el-tab-pane :label="secondTabLabel" name="related">
                <div class="team-card-wrap">
                    <!-- 相关团队列表 -->
                    <div class="team-card-list">
                        <el-card v-for="team in myRelatedTeams" :key="team.teamNo" class="team-card">
                            <div class="team-info">
                                <div class="team-info-item">
                                    <span class="label">团队编号：</span>
                                    <span class="value">{{ team.teamNo }}</span>
                                </div>
                                <div class="team-info-item">
                                    <span class="label">赛题：</span>
                                    <span class="value">{{ team.title }}</span>
                                </div>
                                <div class="team-info-item">
                                    <span class="label">团队名称：</span>
                                    <span class="value">{{ team.teamName }}</span>
                                </div>
                                <div class="team-info-item">
                                    <span class="label">指导老师：</span>
                                    <span class="value">{{ team.teacher }}</span>
                                </div>
                            </div>
                            <div class="team-actions">

                                <el-button type="text" @click="handleAddMember(team)">添加成员</el-button>
                                <el-button type="text" @click="handleEditMember(team)">编辑成员</el-button>
                                <!-- <el-button type="text" style="color: #f56c6c"
                                    @click="handleDeleteMember(team)">删除成员</el-button> -->
                                <el-button type="text" style="color: #f56c6c"
                                    @click="handleDeleteTeam(team)">删除团队</el-button>
                            </div>
                        </el-card>
                        <div v-if="myRelatedTeams.length === 0" class="empty-tip">暂无相关团队</div>
                    </div>
                </div>
            </el-tab-pane>
        </el-tabs>

        <!-- 创建团队弹窗 -->
        <el-dialog :visible.sync="createTeamDialogVisible" title="创建我的团队" width="900px" destroy-on-close>
            <el-form :model="createTeamForm" label-width="80px" :rules="createTeamRules" ref="createTeamFormRef">
                <el-form-item label="赛事标题" prop="projectId">
                    <el-select v-model="createTeamForm.projectId" placeholder="请选择赛事标题" style="width: 100%;"
                        @change="handleProjectSelect">
                        <el-option v-for="item in projectOptions" :key="item.projectId" :label="item.name"
                            :value="item.projectId"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="团队名称" prop="teamName">
                    <el-input v-model="createTeamForm.teamName" placeholder="请输入团队名称" />
                </el-form-item>
                <!-- 为学生账号时 -->
                <el-form-item label="指导老师" prop="teacherId" v-if="userRole === 'student'">
                    <el-select v-model="createTeamForm.teacherId" placeholder="请选择指导老师" style="width: 100%;"
                        @change="handleTeacherSelect">
                        <el-option v-for="item in teacherOptions" :key="item.id" :label="item.name"
                            :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <!-- 为教师账号时 -->
                <el-form-item label="指导老师" prop="teacher" v-if="userRole === 'teacher'">
                    <el-input v-model="createTeamForm.teacher" disabled />
                </el-form-item>
                <!-- 团队成员列表 -->
                <el-form-item label="团队成员" prop="memberList">
                    <el-table :data="createTeamForm.memberList" border stripe style="width: 100%; margin-top: 10px;">
                        <el-table-column label="学号" align="center" min-width="100">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.studentNo" placeholder="请输入学号" />
                            </template>
                        </el-table-column>
                        <el-table-column label="学生姓名" align="center" min-width="100">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.studentName" placeholder="请输入姓名" />
                            </template>
                        </el-table-column>
                        <!-- 学院名称 -->
                        <el-table-column label="学院名称" align="center" min-width="120">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.collegeName" placeholder="请输入学院名称" />
                            </template>
                        </el-table-column>
                        <!--班级名称 -->
                        <el-table-column label="班级名称" align="center" min-width="120">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.className" placeholder="请输入班级名称" />
                            </template>
                        </el-table-column>
                        <!-- 年级 -->
                        <el-table-column label="年级" align="center" min-width="80">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.grade" placeholder="请输入年级" />
                            </template>
                        </el-table-column>
                        <!-- 专业 -->
                        <el-table-column label="专业" align="center" min-width="100">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.major" placeholder="请输入专业" />
                            </template>
                        </el-table-column>
                        <!-- 邮箱 -->
                        <el-table-column label="邮箱" align="center" min-width="150">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.mail" placeholder="请输入邮箱" />
                            </template>
                        </el-table-column>
                        <!-- 手机号码 -->
                        <el-table-column label="手机号码" align="center" min-width="120">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.mobileNumber" placeholder="请输入手机号码" />
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" align="center" width="80">
                            <template slot-scope="scope">
                                <el-button type="text" style="color: #f56c6c"
                                    @click="removeCreateMemberItem(scope.$index)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-button type="text" icon="el-icon-plus" @click="addCreateMemberItem">添加成员</el-button>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="createTeamDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitCreateTeam">提交</el-button>
            </div>
        </el-dialog>

        <!-- 成员操作弹窗 -->
        <!-- 点击添加成员，编辑成员的弹窗显示 -->
        <el-dialog :visible.sync="memberDialogVisible" :title="memberDialogTitle" width="900px" destroy-on-close>
            <el-form :model="memberForm" label-width="80px" :rules="memberRules" ref="memberFormRef">
                <el-form-item label="团队名称" prop="teamName">
                    <el-input v-model="memberForm.teamName" disabled />
                </el-form-item>
                <el-form-item label="成员信息" prop="members">
                    <el-table :data="memberForm.members" border stripe style="width: 100%;">
                        <el-table-column label="ID" prop="id" align="center" width="80"
                            v-if="memberDialogTitle === '编辑团队成员'">
                            <template slot-scope="scope">
                                <span>{{ scope.row.id || '-' }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="学号" align="center" min-width="100">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.studentNo" placeholder="请输入学号" />
                            </template>
                        </el-table-column>
                        <el-table-column label="学生姓名" align="center" min-width="100">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.studentName" placeholder="请输入姓名" />
                            </template>
                        </el-table-column>
                        <!-- 学院名称 -->
                        <el-table-column label="学院名称" align="center" min-width="120">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.collegeName" placeholder="请输入学院名称" />
                            </template>
                        </el-table-column>
                        <!-- 班级名称 -->
                        <el-table-column label="班级名称" align="center" min-width="120">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.className" placeholder="请输入班级名称" />
                            </template>
                        </el-table-column>
                        <!-- 年级 -->
                        <el-table-column label="年级" align="center" min-width="80">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.grade" placeholder="请输入年级" />
                            </template>
                        </el-table-column>
                        <!-- 专业 -->
                        <el-table-column label="专业" align="center" min-width="100">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.major" placeholder="请输入专业" />
                            </template>
                        </el-table-column>
                        <!--邮箱 -->
                        <el-table-column label="邮箱" align="center" min-width="150">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.mail" placeholder="请输入邮箱" />
                            </template>
                        </el-table-column>
                        <!-- 手机号码 -->
                        <el-table-column label="手机号码" align="center" min-width="120">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.mobileNumber" placeholder="请输入手机号码" />
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" align="center" width="80">
                            <template slot-scope="scope">
                                <el-button type="text" style="color: #f56c6c"
                                    @click="removeMember(scope.$index)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <!-- 点击弹窗下的添加成员按钮 -->
                    <el-button type="text" icon="el-icon-plus" @click="addMemberItem">添加成员</el-button>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="memberDialogVisible = false">取消</el-button>
                <!-- 添加成员按钮 -->
                <el-button type="primary" v-if="memberDialogTitle === '添加团队成员'" @click="submitAddMember">
                    添加成员
                </el-button>
                <!-- 编辑成员按钮 -->
                <el-button type="primary" v-if="memberDialogTitle === '编辑团队成员'" @click="submitMemberOperation">
                    确认修改
                </el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { Message, MessageBox } from 'element-ui'

export default {
    name: 'TeamManagementOfTeacherAndStudent',
    data() {
        return {
            userRole: localStorage.getItem('userRole') || 'student',
            activeTab: 'created',
            currentTeacherName: '',
            secondTabLabel: '',
            // 我创建的
            myCreatedTeams: [],
            // 我指导的（对于老师），我参与的（对于学生）
            myRelatedTeams: [],
            teacherOptions: [], // 存储教师下拉框选项
            projectOptions: [], // 赛事列表
            currentUserId: localStorage.getItem('userId') || '', // 当前用户ID
            currentStudentNo: localStorage.getItem('studentNo') || '', // 学生学号,若当前登录的角色是学生，则有该数据
            createTeamDialogVisible: false,
            createTeamForm: {
                title: '',
                projectId: '',
                teamName: '',
                teacher: '',
                teacherId: '',
                memberList: []
            },
            createTeamRules: {
                projectId: [{ required: true, message: '请选择赛事标题', trigger: 'change' }],
                teamName: [{ required: true, message: '请输入团队名称', trigger: 'blur' }],
                teacher: [{ required: true, message: '请输入指导老师', trigger: 'blur' }],
                teacherId: [{ required: true, message: '请选择指导老师', trigger: 'change' }], // 校验教师ID
                memberList: [{
                    required: true,
                    validator: (rule, value, callback) => value.length > 0 ? callback() : callback(new Error('请至少添加1名团队成员')),
                    trigger: 'change'
                }]
            },
            memberDialogVisible: false,
            memberDialogTitle: '',
            currentTeam: null,
            memberForm: {
                teamName: '',
                members: []
            },
            memberRules: {
                members: [{
                    required: true,
                    validator: (rule, value, callback) => value.length > 0 ? callback() : callback(new Error('请至少添加1名成员')),
                    trigger: 'change'
                }]
            }
        }
    },
    mounted() {
        this.secondTabLabel = this.userRole === 'teacher' ? '我指导的' : '我参与的'
        this.getTeamLists() //加载分类团队列表
        // 为教师角色账号时
        if (this.userRole === 'teacher') {
            // 当前指导老师为当前老师的真实姓名
            this.getTeacherRealName();
            // 进一步设置，教师id为当前用户userid
            this.createTeamForm.teacherId = this.currentUserId;
        }
    },
    methods: {
        // 对于老师账号，根据账号名查出真实名字
        async getTeacherRealName() {
            try {
                // 获取本地存储的username账户名
                const username = localStorage.getItem('login') || '';
                if (!username) {
                    this.currentTeacherName = '无';
                    this.createTeamForm.teacher = '无';
                    return;
                }
                // 调用后端接口查询用户详情
                const res = await this.$axios.post('/admin/user/select', {
                    username: username // 入参：username
                });
                // 取出返回的name字段，赋值给teacher
                const userInfo = res.data.data.user || {};
                this.currentTeacherName = userInfo.name || '无'; // 绑定实例属性
                this.createTeamForm.teacher = this.currentTeacherName; // 同步到表单
                // 同时确保teacherId仍为当前用户ID
                this.createTeamForm.teacherId = this.currentUserId;
            } catch (error) {
                console.error('查询教师姓名失败：', error);
                // 失败时给默认值
                this.createTeamForm.teacher = localStorage.getItem('login') || '无';
            }
        },

        // 加载赛事列表
        async getProjectList() {
            try {
                // 构造查询参数（查询所有未删除的项目）
                const queryDTO = {
                    status: '',
                    name: ''
                };
                const res = await this.$axios.post('/admin/project/list', queryDTO);
                this.projectOptions = res.data.data.list || []; // 后端返回的项目列表
            } catch (error) {
                console.error('获取赛事列表失败：', error);
                Message.error('获取赛事列表失败，请稍后重试');
                this.projectOptions = [];
            }
        },
        //选择赛事标题后，同步赛事标题到表单数据 
        handleProjectSelect(val) {
            const selectedProject = this.projectOptions.find(item => item.projectId === val);
            this.createTeamForm.title = selectedProject ? selectedProject.name : '';
        },

        // 选择后，同步显示老师的名字 为学生账号时
        handleTeacherSelect(val) {
            const selectedTeacher = this.teacherOptions.find(item => item.id === val);
            this.createTeamForm.teacher = selectedTeacher ? selectedTeacher.name : '无';
        },
        async getTeamLists() {
            try {
                const res = await this.$axios.post('/admin/team/100/1', {
                    title: '',
                    status: ''
                })
                const allTeams = res.data.data.list || []

                // 管理员账户都为空
                // 分角色过滤团队列表
                //为教师时
                console.log('当前用户角色', this.userRole)
                if (this.userRole === 'teacher') {
                    // 教师：我创建的（create_user_id = 当前用户ID）
                    console.log('当前教师用户的id', this.currentUserId)
                    this.myCreatedTeams = allTeams.filter(
                        team => team.createUserId === Number(this.currentUserId)
                    )
                    console.log('我创建的团队列表', this.myCreatedTeams)
                    // 教师：我指导的（teacher_id = 当前用户ID）
                    this.myRelatedTeams = allTeams.filter(
                        team => team.teacherId === Number(this.currentUserId)
                    )
                    console.log('我指导的团队列表', this.myRelatedTeams)
                } else if (this.userRole === 'student') {
                    //为学生时
                    // 学生：我创建的（create_user_id = 当前用户ID）
                    console.log('当前学生用户的id', this.currentUserId)
                    this.myCreatedTeams = allTeams.filter(
                        team => team.createUserId === Number(this.currentUserId)
                    )
                    // 学生：我参与的（需关联团队成员表查询）
                    this.getStudentRelatedTeams(allTeams)
                } else {
                    this.myCreatedTeams = []
                    this.myRelatedTeams = []
                }
            } catch (error) {
                console.error('获取团队列表失败：', error)
                this.myCreatedTeams = []
                this.myRelatedTeams = []
                Message.error('获取团队列表失败，请稍后重试')
            }
        },

        // 查询我参与的团队（关联 team_member 表）
        async getStudentRelatedTeams(allTeams) {
            try {
                // 调用接口获取所有包含当前学生学号的团队编号
                const res = await this.$axios.get(`/admin/team/member/student/${this.currentStudentNo}`)
                const relatedTeamNos = res.data.data.teamNos || [] // 后端返回格式：{ "teamNos": ["团队编号1", "团队编号2"] }

                // 过滤出我参与的团队，也就是要显示的内容
                this.myRelatedTeams = allTeams.filter(team =>
                    relatedTeamNos.includes(team.teamNo)
                )
            } catch (error) {
                console.error('获取参与团队失败：', error)
                this.myRelatedTeams = []
                Message.warning('获取参与的团队失败，仅显示创建的团队')
            }
        },
        openCreateTeamDialog() {
            // 首先加载赛事列表
            this.getProjectList();
            console.log('当前用户角色', this.userRole)
            if (this.userRole === 'teacher') { //为教师账号
                this.createTeamForm = {
                    title: '',
                    projectId: '',
                    teacher: this.currentTeacherName, // 赋值教师姓名
                    teamName: '',
                    teacherId: this.currentUserId, // 教师默认设置指导id为自己的id
                    memberList: []
                }
            } else {
                // 学生：指导老师为下拉框（先获取教师列表，通过 getTeacherList 加载）
                this.createTeamForm = {
                    title: '',
                    projectId: '',
                    teamName: '',
                    teacher: '', // 显示教师姓名
                    teacherId: '', // 存储教师ID（下拉框选中后赋值）
                    memberList: []
                }
                this.getTeacherList() // 加载教师列表供选择
            }
            this.createTeamDialogVisible = true
        },

        // 获取教师列表（供学生选择指导老师）
        async getTeacherList() {
            try {
                const res = await this.$axios.get('/admin/teacher/list') // 教师列表接口
                this.teacherOptions = res.data.data.teacherList || [] // 格式：[{ id: 1, name: '老师1' }, ...]
            } catch (error) {
                console.error('获取教师列表失败：', error)
                Message.error('获取指导老师列表失败，请稍后重试')
            }
        },

        addCreateMemberItem() {
            this.createTeamForm.memberList.push({
                studentNo: '',
                studentName: '',
                collegeName: '',
                className: '',
                grade: '',
                major: '',
                mail: '',
                mobileNumber: ''
            })
        },
        removeCreateMemberItem(index) {
            this.createTeamForm.memberList.splice(index, 1)
        },
        // 提交创建的团队
        async submitCreateTeam() {
            try {
                await this.$refs.createTeamFormRef.validate()
                const teamVo = {
                    ...this.createTeamForm,
                    createUserId: this.currentUserId, // 传递创建者ID
                    teacherId: this.createTeamForm.teacherId || '', // 传递指导老师ID（学生需选择，教师默认自己）
                    teacher: this.createTeamForm.teacher || '无',
                    status: '0',
                    conStatus: '0'
                }
                const res = await this.$axios.post('/admin/team/insert', teamVo)
                if (res.data.code === 200) {
                    Message.success('创建团队成功')
                    this.createTeamDialogVisible = false
                    // 刷新团队列表
                    this.getTeamLists()
                } else {
                    Message.error('创建失败：' + res.data.message)
                }
            } catch (error) {
                console.error('创建团队失败：', error)
                if (error.name !== 'ValidationError') {
                    Message.error('创建团队失败，请稍后重试')
                }
            }
        },
        openMemberDialog(team, type) {
            this.currentTeam = team
            this.memberDialogTitle = type === 'add' ? '添加团队成员' : '编辑团队成员'
            this.memberForm = {
                teamNo: team.teamNo,
                teamName: team.teamName,
                members: []
            }
            if (type === 'edit') {
                this.getTeamMembers(team.teamNo)
            }
            this.memberDialogVisible = true
        },
        handleAddMember(team) {
            this.openMemberDialog(team, 'add')
        },
        handleEditMember(team) {
            this.openMemberDialog(team, 'edit')
        },
        async getTeamMembers(teamNo) {
            try {
                const res = await this.$axios.get(`/admin/team/member/${teamNo}`)
                this.memberForm.members = res.data.data.memberList || []
            } catch (error) {
                console.error('获取团队成员失败：', error)
                this.memberForm.members = []
                Message.error('获取成员信息失败')
            }
        },
        addMemberItem() {
            this.memberForm.members.push({
                studentNo: '',
                studentName: '',
                collegeName: '',
                className: '',
                grade: '',
                major: '',
                mail: '',
                mobileNumber: ''
            })
        },
        removeMember(index) {
            this.memberForm.members.splice(index, 1)
        },
        // 新增成员的非覆盖式调用接口
        async submitAddMember() {
            try {
                // 1. 表单校验
                await this.$refs.memberFormRef.validate()

                // 2. 构造请求参数：包含团队编号和新增的成员列表
                const teamVo = {
                    teamNo: this.currentTeam.teamNo, // 团队编号
                    memberList: this.memberForm.members // 新增的成员列表
                }

                // 3. 调用后端新增成员接口
                const res = await this.$axios.put('/admin/team/member/insert', teamVo)

                // 4. 处理响应
                if (res.data.code === 200) {
                    Message.success('添加团队成员成功')
                    this.memberDialogVisible = false
                    this.getTeamLists() // 刷新团队列表
                } else {
                    Message.error('添加成员失败：' + res.data.message)
                }
            } catch (error) {
                console.error('添加成员失败：', error)
                if (error.name !== 'ValidationError') {
                    Message.error('添加成员失败，请稍后重试')
                }
            }
        },

        // 编辑成员按钮覆盖式更新数据
        async submitMemberOperation() {
            try {
                await this.$refs.memberFormRef.validate()
                const teamVo = {
                    projectId: this.currentTeam.projectId,
                    status: this.currentTeam.status,
                    teamNo: this.currentTeam.teamNo,
                    title: this.currentTeam.title,
                    teamName: this.currentTeam.teamName,
                    teacher: this.currentTeam.teacher,
                    memberList: this.memberForm.members
                }
                const res = await this.$axios.put('/admin/team/update', teamVo)
                if (res.data.code === 200) {
                    Message.success(`${this.memberDialogTitle}成功`)
                    this.memberDialogVisible = false
                    this.getTeamLists()
                } else {
                    Message.error(`${this.memberDialogTitle}失败：` + res.data.message)
                }
            } catch (error) {
                console.error('提交成员信息失败：', error)
                if (error.name !== 'ValidationError') {
                    Message.error(`${this.memberDialogTitle}失败`)
                }
            }
        },
        async handleDeleteMember(team) {
            if (this.memberDialogTitle !== '编辑团队成员' || this.memberForm.members.length === 0) {
                Message.warning('请先进入编辑成员页面选择要删除的成员')
                this.openMemberDialog(team, 'edit')
                return
            }
            try {
                const deleteMember = this.memberForm.members[0]
                if (!deleteMember.id) {
                    Message.warning('成员ID为空，无法删除')
                    return
                }
                await MessageBox.confirm(
                    `确定删除成员【${deleteMember.studentName}】吗？删除后无法恢复！`,
                    '提示',
                    { type: 'warning' }
                )
                const res = await this.$axios.delete(`/admin/member/delete/${deleteMember.id}`)
                if (res.data.code === 200) {
                    Message.success('删除成员成功')
                    this.getTeamMembers(team.teamNo)
                } else {
                    Message.error('删除失败：' + res.data.message)
                }
            } catch (error) {
                if (error.type !== 'cancel') {
                    console.error('删除成员失败：', error)
                    Message.error('删除成员失败，请稍后重试')
                }
            }
        },
        async handleDeleteTeam(team) {
            try {
                await MessageBox.confirm(
                    '确定删除该团队吗？删除后将无法恢复！',
                    '警告',
                    { type: 'warning' }
                )
                const res = await this.$axios.delete(`/admin/team/${team.teamNo}`)
                if (res.data.code === 200) {
                    Message.success('删除团队成功')
                    this.getTeamLists()
                } else {
                    Message.error('删除失败：' + res.data.message)
                }
            } catch (error) {
                if (error.type !== 'cancel') {
                    console.info('取消删除')

                }
            }
        }
    }
}
</script>

<style scoped>
.page-wrap {
    padding: 20px;
    background: #fff;
    min-height: calc(100vh - 60px);
}

/* 优化团队卡片布局：增大宽度+增加间距 */
.team-card-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    /* 卡片之间的间距 */
}

/* 增大卡片宽度，提供更多按钮显示空间 */
.team-card {
    width: 400px;
    /* 原300px → 增大到400px */
    padding: 15px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.team-info-item {
    margin-bottom: 8px;
    display: flex;
}

.team-info-item .label {
    font-weight: 600;
    width: 80px;
}

/* 优化按钮区域：支持自动换行+增加间距 */
.team-actions {
    display: flex;
    flex-wrap: wrap;
    /* 按钮超出宽度时自动换行 */
    gap: 8px;
    /* 按钮之间的间距 */
    justify-content: flex-start;
    /* 左对齐更紧凑 */
    border-top: 1px solid #ebeef5;
    padding-top: 10px;
}

.empty-tip {
    text-align: center;
    padding: 50px;
    color: #909399;
}

/* 响应式适配：小屏幕下卡片全屏显示 */
@media (max-width: 768px) {
    .team-card {
        width: 100%;
        /* 小屏幕下卡片占满宽度 */
        max-width: none;
    }
}
</style>