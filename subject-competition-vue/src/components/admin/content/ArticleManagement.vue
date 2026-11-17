<!-- 公告管理页面 -->
<template>
  <div>
    <el-row style="margin: 18px 0px 0px 18px">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>内容管理</el-breadcrumb-item>
        <el-breadcrumb-item>公告管理</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-link href="/admin/content/editor" :underline="false" target="_blank" class="add-link">
      <el-button type="success">写公告</el-button>
    </el-link>
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px">
      <el-form :inline="true" :model="filters" @submit.native.prevent>
        <el-form-item>
          <el-input v-model="filters.name" placeholder="题目名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-on:click="loadArticles">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <el-card style="margin: 18px 2%; width: 95%">
      <el-table :data="articles" stripe ref="multipleTable" @selection-change="selsChange" style="width: 100%"   cell-style="font-weight: 700;">
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline>
              <el-form-item>
                <span>{{ props.row.abstracts }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="题目（展开查看摘要）" fit>
        </el-table-column>
        <el-table-column prop="createDate" label="发布日期" width="200">
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="180">
          <template slot-scope="scope">
            <el-button @click.native.prevent="viewArticle(scope.row.id)" type="text" size="small">查看</el-button>
            <el-button @click.native.prevent="editArticle(scope.row)" type="text" size="small">编辑</el-button>
            <el-button @click.native.prevent="deleteArticle(scope.row.id)" type="text" size="small">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin: 20px 0 20px 0; float: left">
        <el-button @click="toggleSelection()">取消选择</el-button>
        <el-button type="danger" @click="batchRemove" :disabled="this.sels.length === 0">批量删除</el-button>
      </div>
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
export default {
  name: 'ArticleManagement',
  data () {
    return {
      filters: {
        name: ''
      },
      sels: [], // 列表选中列
      articles: [],
      pageSize: 10,
      total: 0
    }
  },
  mounted () {
    this.loadArticles()
  },

  methods: {
    loadArticles () {
      let info = {
        title: this.filters.name
      }
      var _this = this
      this.$axios
        .post('/admin/content/article/' + this.pageSize + '/1', info)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.articles = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    handleCurrentChange (page) {
      let info = {
        title: this.filters.name
      }
      var _this = this
      this.$axios
        .post('/admin/content/article/' + this.pageSize + '/' + page, info)
        .then((resp) => {
          if (resp && resp.data.code === 200) {
            _this.articles = resp.data.data.list
            _this.total = resp.data.data.total
          }
        })
    },
    viewArticle (id) {
      let articleUrl = this.$router.resolve({
        path: '../../jotter/article',
        query: {
          id: id
        }
      })
      window.open(articleUrl.href, '_blank')
    },
    editArticle (article) {
      this.$router.push({
        name: 'Editor',
        params: {
          article: article
        }
      })
    },
    deleteArticle (id) {
      this.$confirm('此操作将永久删除该公告, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.$axios.delete('/admin/content/article/' + id).then((resp) => {
            if (resp && resp.data.code === 200) {
              this.loadArticles()
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
              .post('/admin/content/article/batchDelete', ids)
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
.add-link {
  margin: 18px 0 15px 10px;
  float: left;
}
</style>
