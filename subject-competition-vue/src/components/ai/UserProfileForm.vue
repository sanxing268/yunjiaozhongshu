<!-- 用户竞赛画像调研问卷组件 -->
<template>
  <el-dialog
    title="用户竞赛画像调研问卷"
    :visible.sync="visible"
    width="70%"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="onDialogClose"
  >
    <el-form ref="form" :model="formData" label-width="120px" class="profile-form">
      <!-- 一、基础信息 -->
      <div class="form-section">
        <h3>一、基础信息</h3>
        <el-form-item label="学校名称" prop="school">
          <el-input v-model="formData.school" placeholder="请输入学校名称"></el-input>
        </el-form-item>
        <el-form-item label="学院名称" prop="college">
          <el-input v-model="formData.college" placeholder="请输入学院名称"></el-input>
        </el-form-item>
        <el-form-item label="专业名称" prop="major">
          <el-input v-model="formData.major" placeholder="请输入专业名称"></el-input>
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-radio-group v-model="formData.grade">
            <el-radio label="大一">大一</el-radio>
            <el-radio label="大二">大二</el-radio>
            <el-radio label="大三">大三</el-radio>
            <el-radio label="大四">大四</el-radio>
            <el-radio label="研究生">研究生</el-radio>
            <el-radio label="其他">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
            <el-radio label="不愿透露">不愿透露</el-radio>
          </el-radio-group>
        </el-form-item>
      </div>

      <!-- 二、证书资质 -->
      <div class="form-section">
        <h3>二、证书资质（可多选）</h3>
        <el-checkbox-group v-model="formData.certificates">
          <el-checkbox label="大学英语四级">大学英语四级</el-checkbox>
          <el-checkbox label="大学英语六级">大学英语六级</el-checkbox>
          <el-checkbox label="雅思">雅思</el-checkbox>
          <el-checkbox label="托福">托福</el-checkbox>
          <el-checkbox label="小语种等级证书">小语种等级证书</el-checkbox>
          <el-checkbox label="Adobe认证(PS/AI等)">Adobe认证(PS/AI等)</el-checkbox>
          <el-checkbox label="工业设计类证书">工业设计类证书</el-checkbox>
          <el-checkbox label="UI/UX设计相关证书">UI/UX设计相关证书</el-checkbox>
          <el-checkbox label="软考(初级/中级/高级)">软考(初级/中级/高级)</el-checkbox>
          <el-checkbox label="计算机二级/三级/四级">计算机二级/三级/四级</el-checkbox>
          <el-checkbox label="编程类证书(Python/Java等)">编程类证书(Python/Java等)</el-checkbox>
          <el-checkbox label="云计算认证(阿里云/华为云等)">云计算认证(阿里云/华为云等)</el-checkbox>
          <el-checkbox label="教师资格证">教师资格证</el-checkbox>
          <el-checkbox label="会计从业/初级/中级证书">会计从业/初级/中级证书</el-checkbox>
          <el-checkbox label="法律职业资格证">法律职业资格证</el-checkbox>
          <el-checkbox label="普通话等级证书">普通话等级证书</el-checkbox>
          <el-checkbox label="创新创业相关培训证书">创新创业相关培训证书</el-checkbox>
          <el-checkbox label="无上述证书">无上述证书</el-checkbox>
        </el-checkbox-group>
      </div>

      <!-- 三、获奖经历 -->
      <div class="form-section">
        <h3>三、获奖经历（可多选）</h3>
        <el-checkbox-group v-model="formData.awards">
          <el-checkbox label="数学建模竞赛(省奖/国奖)">数学建模竞赛(省奖/国奖)</el-checkbox>
          <el-checkbox label="英语竞赛(大英赛/外研社杯等)">英语竞赛(大英赛/外研社杯等)</el-checkbox>
          <el-checkbox label="专业核心课程竞赛">专业核心课程竞赛</el-checkbox>
          <el-checkbox label="互联网+大学生创新创业大赛">互联网+大学生创新创业大赛</el-checkbox>
          <el-checkbox label="挑战杯系列竞赛">挑战杯系列竞赛</el-checkbox>
          <el-checkbox label="创业计划大赛">创业计划大赛</el-checkbox>
          <el-checkbox label="编程竞赛(ACM/蓝桥杯等)">编程竞赛(ACM/蓝桥杯等)</el-checkbox>
          <el-checkbox label="设计类竞赛(全国大学生广告艺术大赛等)">设计类竞赛(全国大学生广告艺术大赛等)</el-checkbox>
          <el-checkbox label="职业技能竞赛">职业技能竞赛</el-checkbox>
          <el-checkbox label="校级/省级/国家级奖学金">校级/省级/国家级奖学金</el-checkbox>
          <el-checkbox label="三好学生">三好学生</el-checkbox>
          <el-checkbox label="优秀学生干部">优秀学生干部</el-checkbox>
          <el-checkbox label="无上述获奖经历">无上述获奖经历</el-checkbox>
        </el-checkbox-group>
      </div>

      <!-- 四、核心技能 -->
      <div class="form-section">
        <h3>四、核心技能（可多选）</h3>
        <el-checkbox-group v-model="formData.skills">
          <el-checkbox label="Python">Python</el-checkbox>
          <el-checkbox label="Java">Java</el-checkbox>
          <el-checkbox label="C/C++">C/C++</el-checkbox>
          <el-checkbox label="JavaScript">JavaScript</el-checkbox>
          <el-checkbox label="平面设计">平面设计</el-checkbox>
          <el-checkbox label="3D建模">3D建模</el-checkbox>
          <el-checkbox label="视频剪辑">视频剪辑</el-checkbox>
          <el-checkbox label="文案策划">文案策划</el-checkbox>
          <el-checkbox label="文献检索与分析">文献检索与分析</el-checkbox>
          <el-checkbox label="论文写作">论文写作</el-checkbox>
          <el-checkbox label="数据分析">数据分析</el-checkbox>
          <el-checkbox label="实验设计">实验设计</el-checkbox>
          <el-checkbox label="项目管理">项目管理</el-checkbox>
          <el-checkbox label="团队协作">团队协作</el-checkbox>
          <el-checkbox label="演讲表达">演讲表达</el-checkbox>
          <el-checkbox label="市场营销">市场营销</el-checkbox>
          <el-checkbox label="无突出核心技能">无突出核心技能</el-checkbox>
        </el-checkbox-group>
      </div>

      <!-- 五、兴趣爱好 -->
      <div class="form-section">
        <h3>五、兴趣爱好（可多选）</h3>
        <el-checkbox-group v-model="formData.interests">
          <el-checkbox label="学术探索">学术探索</el-checkbox>
          <el-checkbox label="创新创业">创新创业</el-checkbox>
          <el-checkbox label="设计创作">设计创作</el-checkbox>
          <el-checkbox label="编程开发">编程开发</el-checkbox>
          <el-checkbox label="赛事竞技">赛事竞技</el-checkbox>
          <el-checkbox label="社会实践">社会实践</el-checkbox>
          <el-checkbox label="文艺活动">文艺活动</el-checkbox>
        </el-checkbox-group>
      </div>

      <!-- 六、竞赛偏好 -->
      <div class="form-section">
        <h3>六、竞赛偏好</h3>
        <el-form-item label="意向竞赛类型（可多选）">
          <el-checkbox-group v-model="formData.competitionPreference.competitionTypes">
            <el-checkbox label="学科竞赛">学科竞赛</el-checkbox>
            <el-checkbox label="创新创业竞赛">创新创业竞赛</el-checkbox>
            <el-checkbox label="技能竞赛">技能竞赛</el-checkbox>
            <el-checkbox label="文艺竞赛">文艺竞赛</el-checkbox>
            <el-checkbox label="综合类竞赛">综合类竞赛</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="竞赛参与形式">
          <el-radio-group v-model="formData.competitionPreference.participationForm">
            <el-radio label="个人赛">个人赛</el-radio>
            <el-radio label="团队赛(2-3人)">团队赛(2-3人)</el-radio>
            <el-radio label="团队赛(4人及以上)">团队赛(4人及以上)</el-radio>
            <el-radio label="均可">均可</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="期望竞赛级别（可多选）">
          <el-checkbox-group v-model="formData.competitionPreference.expectedLevels">
            <el-checkbox label="校级">校级</el-checkbox>
            <el-checkbox label="省级">省级</el-checkbox>
            <el-checkbox label="国家级">国家级</el-checkbox>
            <el-checkbox label="国际级">国际级</el-checkbox>
            <el-checkbox label="均可">均可</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="可投入准备时间">
          <el-radio-group v-model="formData.competitionPreference.availableTime">
            <el-radio label="每周1-3小时">每周1-3小时</el-radio>
            <el-radio label="每周4-6小时">每周4-6小时</el-radio>
            <el-radio label="每周7-10小时">每周7-10小时</el-radio>
            <el-radio label="10小时以上">10小时以上</el-radio>
            <el-radio label="灵活调整">灵活调整</el-radio>
          </el-radio-group>
        </el-form-item>
      </div>

      <el-form-item>
        <el-checkbox v-model="remindNextLogin">下次登录不再提醒</el-checkbox>
      </el-form-item>
    </el-form>

    <span slot="footer" class="dialog-footer">
      <el-button @click="onCancel">下次再说</el-button>
      <el-button type="primary" @click="onSubmit" :loading="loading">提交问卷</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'UserProfileForm',
  data() {
    return {
      visible: false,
      loading: false,
      remindNextLogin: false,
      formData: {
        userId: null,
        school: '',
        college: '',
        major: '',
        grade: '',
        gender: '',
        certificates: [],
        awards: [],
        skills: [],
        interests: [],
        competitionPreference: {
          competitionTypes: [],
          participationForm: '',
          expectedLevels: [],
          availableTime: ''
        },
        remindNextLogin: 1
      }
    }
  },
  computed: {
    ...mapState(['user'])
  },
  mounted() {
    // 监听来自父组件的显示事件
    this.$root.$on('showProfileForm', this.show)
  },
  beforeDestroy() {
    this.$root.$off('showProfileForm')
  },
  methods: {
    show() {
      // 初始化用户ID
      if (this.user && this.user.id) {
        this.formData.userId = this.user.id
        this.visible = true
      }
    },
    onSubmit() {
      this.loading = true
      const payload = {
        ...this.formData,
        remindNextLogin: this.remindNextLogin ? 0 : 1
      }

      this.$axios
        .post('/ai/profile/save', payload)
        .then((response) => {
          if (response.data.code === 200) {
            this.$message.success('问卷已提交，AI正在为您生成专属计划...')
            this.visible = false
            this.$emit('submitted')
          } else {
            this.$message.error(response.data.message || '提交失败')
          }
        })
        .catch((error) => {
          this.$message.error('提交失败：' + (error.message || '网络错误'))
        })
        .finally(() => {
          this.loading = false
        })
    },
    onCancel() {
      // 更新下次登录提醒状态
      if (this.formData.userId) {
        this.$axios
          .put(`/ai/profile/remind/${this.formData.userId}`)
          .then(() => {
            this.$message.info('已记录，下次登录再提醒您')
          })
          .catch(() => {})
      }
      this.visible = false
    },
    onDialogClose() {
      // 如果没有提交就关闭，询问是否下次再说
      if (!this.remindNextLogin) {
        this.$confirm('您确定不填写问卷了吗？我们会在下次登录时再次提醒您', '提示', {
          confirmButtonText: '下次再说',
          cancelButtonText: '继续填写',
          type: 'warning'
        })
          .then(() => {
            this.onCancel()
          })
          .catch(() => {
            this.visible = true
          })
      }
    }
  }
}
</script>

<style scoped lang="scss">
.profile-form {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;

  .form-section {
    margin-bottom: 30px;

    h3 {
      font-size: 16px;
      font-weight: bold;
      margin-bottom: 15px;
      color: #333;
      border-bottom: 2px solid #409eff;
      padding-bottom: 10px;
    }

    :deep(.el-checkbox) {
      margin-right: 20px;
      margin-bottom: 10px;
    }

    :deep(.el-radio) {
      margin-right: 20px;
      margin-bottom: 10px;
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 15px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 640px) {
  .profile-form :deep(.el-form-item__label) {
    float: none;
    display: block;
    width: auto !important;
    padding: 0 0 6px;
    line-height: 1.4;
    text-align: left;
  }

  .profile-form :deep(.el-form-item__content) {
    margin-left: 0 !important;
  }

  .profile-form :deep(.el-checkbox),
  .profile-form :deep(.el-radio) {
    margin-right: 12px;
  }

  .dialog-footer {
    flex-wrap: wrap;
  }
}
</style>
