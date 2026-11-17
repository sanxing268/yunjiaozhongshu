<!-- 前台公告列表组件 -->
<template>
  <div class="articles-page-wrapper" style="margin-top: 40px">
    <!--<el-button @click="addArticle()">添加文章</el-button>-->
    <div class="articles-area">
      <el-card class="article-card">
        <div v-for="(article, index) in articles" :key="article.id" class="article-item">
          <div class="article-content">
            <!-- 点击具体的公告时 -->
            <router-link class="article-link" :to="{ path: 'jotter/article', query: { id: article.id } }">
              <span class="article-title" v-html="highlightKeyword(article.title)"></span>
            </router-link>
            <!-- 公告的创建日期 -->
            <el-divider content-position="left" class="date-divider">{{ article.createDate }}</el-divider>
            <!-- 显示公告的摘要 -->
            <!-- 具体点进去来到此公告的详细页面 -->
            <router-link class="article-link" :to="{ path: 'jotter/article', query: { id: article.id } }">
              <p class="article-abstract">{{ article.abstracts }}</p>
            </router-link>
          </div>
          <!-- 公告封面图：加载失败时显示 COS 上的默认图片 -->
          <el-image :src="article.cover || defaultCover" fit="cover" class="cover-image">
            <div slot="error" class="image-slot">
              <img :src="defaultCover" class="error-image">
            </div>
          </el-image>
          <el-divider v-if="index < articles.length - 1" class="item-divider"></el-divider>
        </div>
      </el-card>
    </div>
    <div class="pagination-wrapper">
      <el-pagination background :layout="paginationLayout" @current-change="handleCurrentChange" :page-size="pageSize"
        :total="total" class="article-pagination">
      </el-pagination>
    </div>
    <!-- 页脚 -->
    <div style="margin-top: auto;">
      <about id="about"></about>
    </div>
    <!-- AI浮动球 -->
    <floating-ball></floating-ball>
  </div>
</template>

<script>
import FloatingBall from '../ai/FloatingBall.vue'
import About from '../common/About.vue'

export default {
  name: 'Articles',
  components: {
    FloatingBall,
    About
  },
  data() {
    return {
      articles: [],
      pageSize: 4,
      total: 0,
      searchKeyword: ''
    }
  },
  computed: {
    // 使用全局默认图片
    defaultCover() {
      return this.$defaultImage
    },
    // 响应式分页布局
    paginationLayout() {
      if (window.innerWidth <= 768) {
        return 'prev, pager, next'
      } else if (window.innerWidth <= 1024) {
        return 'total, prev, pager, next'
      } else {
        return 'total, prev, pager, next, jumper'
      }
    }
  },
  mounted() {
    // 检查是否有搜索关键词
    if (this.$route.query.keyword) {
      this.searchArticles(this.$route.query.keyword)
    } else {
      this.loadArticles()
    }
  },
  watch: {
    // 监听路由变化
    '$route'(to, from) {
      if (to.query.keyword) {
        this.searchArticles(to.query.keyword)
      } else if (to.path === '/jotter' && !to.query.keyword) {
        this.loadArticles()
      }
    }
  },
  methods: {
    // 加载所有公告展示在前台
    loadArticles(page = 1) {
      var _this = this
      this.$axios.post('/content/article/' + this.pageSize + '/' + page).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.articles = resp.data.data.list
          _this.total = resp.data.data.total
        }
      })
    },
    // 搜索公告
    searchArticles(keyword) {
      var _this = this
      _this.searchKeyword = keyword
      // 这里需要根据实际的搜索API来调用
      // 假设有搜索API，如果没有可以前端过滤
      this.$axios.get(`/api/search/article?keyword=${encodeURIComponent(keyword)}`).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.articles = resp.data.data.list || []
          _this.total = resp.data.data.total || 0
        }
      }).catch(error => {
        // 如果API不存在，先加载所有文章，然后前端过滤
        console.log('搜索API不存在，使用前端过滤')
        this.$axios.post('/content/article/1000/1').then(resp => {
          if (resp && resp.data.code === 200) {
            const allArticles = resp.data.data.list || []
            const filtered = allArticles.filter(article =>
              (article.title && article.title.includes(keyword)) ||
              (article.abstracts && article.abstracts.includes(keyword))
            )
            _this.articles = filtered
            _this.total = filtered.length
          }
        }).catch(err => {
          console.error('加载文章失败：', err)
        })
      })
    },
    // 高亮匹配的关键词
    highlightKeyword(text) {
      if (!text || !this.searchKeyword) {
        return text
      }
      const regex = new RegExp(`(${this.escapeRegex(this.searchKeyword)})`, 'gi')
      return text.replace(regex, '<span style="color: #ff0000; font-weight: bold;">$1</span>')
    },
    // 转义正则表达式特殊字符
    escapeRegex(str) {
      return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    },
    handleCurrentChange(page) {
      var _this = this
      this.$axios.post('/content/article/' + this.pageSize + '/' + page).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.articles = resp.data.data.list
          _this.total = resp.data.data.total
        }
      })
    }
  }
}
</script>

<style scoped>
.articles-page-wrapper {
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.articles-area {
  width: 990px;
  height: 750px;
  margin-left: auto;
  margin-right: auto;
}

.article-card {
  text-align: left;
}

/* PC端原有布局 - 使用float布局 */
.article-item {
  position: relative;
  overflow: hidden;
  clear: both;
}

.article-content {
  float: left;
  width: 85%;
  height: 150px;
}

.date-divider {
  margin: 10px 0;
}

.item-divider {
  margin: 15px 0;
  clear: both;
}

.article-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.article-abstract {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.cover-image {
  margin: 18px 0 0 30px;
  width: 100px;
  height: 100px;
  float: right;
}

.error-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-link {
  text-decoration: none;
  color: #606266;
  display: block;
}

.article-link:hover {
  color: #409EFF;
}

.article-link:hover .article-title {
  color: #409EFF;
}

/* 分页组件样式 */
.pagination-wrapper {
  margin: 40px 0 60px;
  display: flex;
  justify-content: center;
  width: 100%;
}

.article-pagination {
  display: inline-block;
}

/* 美化分页组件 */
.article-pagination>>>.el-pagination {
  padding: 0;
}

.article-pagination>>>.el-pager li {
  background-color: #fff;
  border: 1px solid #e4e7ed;
  color: #606266;
  font-weight: 500;
  transition: all 0.3s ease;
}

.article-pagination>>>.el-pager li:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.article-pagination>>>.el-pager li.active {
  background-color: #1890ff;
  border-color: #1890ff;
  color: #fff;
}

.article-pagination>>>.btn-prev,
.article-pagination>>>.btn-next {
  background-color: #fff;
  border: 1px solid #e4e7ed;
  color: #606266;
  transition: all 0.3s ease;
}

.article-pagination>>>.btn-prev:hover,
.article-pagination>>>.btn-next:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.article-pagination>>>.el-pagination__total {
  color: #606266;
  font-weight: 500;
}

.article-pagination>>>.el-pagination__jump {
  color: #606266;
}

.article-pagination>>>.el-pagination__editor {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.article-pagination>>>.el-pagination__editor:focus {
  border-color: #1890ff;
}

/* 响应式适配 */
@media (max-width: 1024px) {
  .articles-area {
    width: 95%;
    height: auto;
    min-height: 500px;
    padding: 0 10px;
  }
}

@media (max-width: 768px) {
  .articles-area {
    width: 100%;
    height: auto;
    padding: 0 10px;
    margin-top: 20px;
  }

  .article-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }

  .article-content {
    float: none;
    width: 100%;
    height: auto;
    order: 1;
  }

  .cover-image {
    float: none;
    margin: 0 auto;
    width: 150px;
    height: 150px;
    order: 2;
  }

  .article-title {
    font-size: 18px;
    text-align: center;
  }

  .article-abstract {
    font-size: 14px;
    text-align: center;
  }

  .article-pagination {
    margin-top: 20px;
    text-align: center;
  }

  .article-pagination>>>.el-pagination__total {
    display: none;
  }

  .article-pagination>>>.el-pagination__jump {
    display: none;
  }
}

@media (max-width: 480px) {
  .articles-area {
    padding: 0 8px;
    margin-top: 15px;
  }

  .article-item {
    padding: 12px 0;
  }

  .article-title {
    font-size: 16px;
  }

  .article-abstract {
    font-size: 13px;
  }

  .cover-image {
    width: 120px;
    height: 120px;
  }

  .article-pagination {
    margin-top: 15px;
  }
}
</style>
