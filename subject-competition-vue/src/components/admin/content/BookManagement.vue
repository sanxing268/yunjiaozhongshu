<!-- 项目一览组件 -->
<template>
  <div>
    <el-row style="margin: 18px 0px 0px 18px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>内容管理</el-breadcrumb-item>
        <el-breadcrumb-item>竞赛项目一览</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <edit-form @onSubmit="loadBooks()" ref="edit"></edit-form>
    <!--工具条-->
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px">
      <el-form :inline="true" :model="filters" @submit.native.prevent>
        <el-form-item>
          <el-input v-model="filters.name" placeholder="学科竞赛名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.cname" placeholder="请选择分类">
            <el-option v-for="item in categorylist" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-on:click="loadBooks">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <el-card style="margin: 18px 2%; width: 95%">
      <el-table :data="books" stripe ref="multipleTable" @selection-change="selsChange" style="width: 100%"   :cell-style="{ fontWeight: '700' }">
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline>
              <el-form-item>
                <span>{{ props.row.abs }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="学科竞赛（展开查看摘要）" fit>
        </el-table-column>
        <el-table-column prop="category.name" label="分类" width="100">
        </el-table-column>
        <el-table-column prop="author" label="主办方" width="80"> </el-table-column>
        <el-table-column prop="date" label="发布日期" width="160">
        </el-table-column>
        <el-table-column prop="press" label="承办方" fit> </el-table-column>
        <el-table-column fixed="right" label="操作" width="140">
          <template slot-scope="scope">
            <el-button @click.native.prevent="editBook(scope.row)" type="text" size="small">编辑
            </el-button>
            <el-button @click.native.prevent="deleteBook(scope.row.id)" type="text" size="small">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin: 20px 0 20px 0; float: left">
        <el-button @click="toggleSelection()">取消选择</el-button>
        <el-button type="danger" @click="batchRemove" :disabled="this.sels.length === 0">批量删除</el-button>
      </div>
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
    </el-card>
  </div>
</template>

<script>
import EditForm from './EditForm'

export default {
  name: 'BookManagement',
  components: { EditForm },
  data () {
    return {
      filters: {
        name: '',
        cname: ''
      },
      total: 0,
      pageSize: 10, // 每页数量
      page: 1, // 当前页
      sels: [], // 列表选中列
      books: [],
      categorylist: []
    }
  },
  mounted () {
    this.loadBooks()
    this.loadCategorys()
  },
  methods: {
    loadBooks () {
      let info = {
        name: this.filters.name,
        cid: this.filters.cname
      }
      var _this = this
      this.$axios
        .post('/admin/projectView/' + this.pageSize + '/1', info)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.books = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    handleCurrentChange (page) {
      let info = {
        name: this.filters.name,
        cid: this.filters.cname
      }
      var _this = this
      this.$axios
        .post('/admin/projectView/' + this.pageSize + '/' + page, info)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.books = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    loadCategorys () {
      var _this = this
      this.$axios.get('/categoryList').then((resp) => {
        if (resp && resp.data.code === 200) {
          _this.categorylist = resp.data.data.list
        }
      })
    },
    deleteBook (id) {
      this.$confirm('此操作将永久删除该项目, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.$axios
            .get('/admin/content/projectView/delete/' + id)
            .then((resp) => {
              if (resp && resp.data.code === 200) {
                this.loadBooks()
              }
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    editBook (item) {
      this.$refs.edit.dialogFormVisible = true
      this.$refs.edit.form = {
        id: item.id,
        // 现在封面在数据库中直接保存为完整的 URL（COS 地址），无需再做字符串截取
        cover: item.cover,
        title: item.title,
        author: item.author,
        date: item.date,
        press: item.press,
        abs: item.abs,
        category: {
          id: item.category.id.toString(),
          name: item.category.name
        }
      }
    },

    // 批量删除
    batchRemove: function () {
      var _this = this
      this.$confirm('确认删除选中记录吗？', '提示', {
        type: 'warning'
      })
        .then(() => {
          let ids = []
          _this.sels.forEach((obj) => {
            ids.push(obj.id)
          })
          if (this.sels.map((item) => item.id).toString() !== undefined) {
            _this.$axios
              .post('/admin/content/projectView/batchDelete', ids)
              .then(function (res) {
                if (res.data.code === 200) {
                  _this.listUsers()
                  _this.$message.success(res.data.message)
                } else {
                  _this.$message.error(res.data.message)
                }
              })
              .catch((error) => {
                console.log('error--->', error)
                _this.$message.error('删除失败')
              })
          }
        })
        .catch(() => {
          console.log('cancel')
        })
    },
    // 全选单选多选
    selsChange (sels) {
      this.sels = sels
    },
    toggleSelection (rows) {
      if (rows) {
        rows.forEach((row) => {
          this.$refs.multipleTable.toggleRowSelection(row)
        })
      } else {
        this.$refs.multipleTable.clearSelection()
      }
    }
  }
}
</script>

<style scoped>
</style>
