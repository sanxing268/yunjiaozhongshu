<!-- 添加或者修改学科竞赛组件 -->
<template>
  <div style="float: left">
    <el-button class="add-button" type="success" @click="dialogFormVisible = true">添加学科竞赛一览</el-button>
    <el-dialog title="添加/修改学科竞赛一览" :visible.sync="dialogFormVisible" @close="clear">
      <el-form v-model="form" style="text-align: left" ref="dataForm">
        <el-form-item label="学科竞赛" :label-width="formLabelWidth" prop="title">
          <el-input v-model="form.title" autocomplete="off" placeholder="不加《》"></el-input>
        </el-form-item>
        <el-form-item label="主办方" :label-width="formLabelWidth" prop="author">
          <el-input v-model="form.author" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="发布日期" :label-width="formLabelWidth" prop="date">
          <el-input v-model="form.date" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="承办方" :label-width="formLabelWidth" prop="press">
          <el-input v-model="form.press" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="封面" :label-width="formLabelWidth" prop="cover">
          <el-input v-model="form.cover" autocomplete="off" placeholder="图片 URL"></el-input>
          <img-upload @onUpload="uploadImg" ref="imgUpload"></img-upload>
        </el-form-item>
        <el-form-item label="简介" :label-width="formLabelWidth" prop="abs">
          <el-input type="textarea" v-model="form.abs" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="分类" :label-width="formLabelWidth" prop="category">
          <el-select v-model="form.category.name" placeholder="请选择分类" @change="categoryChanged">
            <el-option v-for="item in categorylist" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="id" style="height: 0">
          <el-input type="hidden" v-model="form.id" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="id" style="height: 0">
          <el-input type="hidden" v-model="form.category.id" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import ImgUpload from './ImgUpload'

  export default {
    name: 'EditForm',
    components: {ImgUpload},
    data () {
      return {
        categorylist: [],
        dialogFormVisible: false,
        url: '',
        form: {
          id: '',
          title: '',
          author: '',
          date: '',
          press: '',
          cover: '',
          abs: '',
          cid: '',
          category: {
            id: '',
            name: ''
          }
        },
        formLabelWidth: '120px'
      }
    },
    mounted () {
      this.loadCategorys()
    },
    methods: {
      loadCategorys () {
        var _this = this
        this.$axios.get('/categoryList').then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.categorylist = resp.data.data.list
          }
        })
      },
      categoryChanged (val) {
        var obj = {}
        obj = this.categorylist.find(function (item) {
          return item.id === val
        })
        this.form.category = {
          id: val,
          name: obj.name
        }
      },
      clear () {
        this.$refs.imgUpload.clear()
        this.form = {
          id: '',
          title: '',
          author: '',
          date: '',
          press: '',
          cover: '',
          abs: '',
          category: {
            id: '',
            name: ''
          }
        }
      },
      onSubmit () {
        let projectViewInfo = {
          id: this.form.id,
          cover: this.form.cover,
          title: this.form.title,
          author: this.form.author,
          date: this.form.date,
          press: this.form.press,
          abs: this.form.abs,
          category: this.form.category
        }
        this.$axios
          .post('/admin/content/projectView', projectViewInfo)
          .then((resp) => {
            if (resp && resp.data.code === 200) {
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: resp.data.message
              })
              this.$emit('onSubmit')
            } else {
              this.$notify.error({
                title: '错误',
                message: resp.data.message
              })
            }
          })
      },
      uploadImg () {
        // this.url = 'http://localhost:8443/' + this.$refs.imgUpload.url
        this.form.cover = this.$refs.imgUpload.url
      }
    }
  }
</script>

<style scoped>
  .add-button {
    margin: 18px 0 0 10px;
  }
</style>
