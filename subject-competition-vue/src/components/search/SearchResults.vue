<template>
  <div class="search-results-container">
    <div class="search-wrapper">
      <!-- 左侧搜索栏与筛选 -->
      <div class="search-sidebar">
        <div class="sidebar-section">
          <h4 class="sidebar-title">文章搜索</h4>
          <div class="search-input-wrapper">
            <el-input
              v-model="keywords"
              placeholder="输入关键词"
              prefix-icon="el-icon-search"
              size="small"
              clearable
              @keyup.enter.native="performSearch"
              @clear="performSearch">
            </el-input>
          </div>
        </div>
        <div class="results-count-sidebar">共 {{ results.length }} 篇文章</div>
      </div>

      <!-- 右侧搜索结果列表 -->
      <div class="search-results">
        <div v-if="results.length > 0" class="results-list">
          <div style="text-align: left; font-weight: bold;">搜索结果</div>
          <div v-for="article in results" :key="article.id" class="result-item">
            <h3 class="result-title" @click="goToArticle(article.id)">
              <span v-html="highlightKeyword(article.title)"></span>
            </h3>
            <div class="result-meta">
              <span class="meta-time">{{ formatDate(article.createDate) }}</span>
              <span class="meta-module">竞赛发布</span>
            </div>
          </div>
          <!-- 分页 -->
          <div class="pagination-wrapper">
            <span class="pagination-text">共 {{ results.length }} 条</span>
            <el-pagination
              background
              layout="prev, pager, next"
              :page-size="pageSize"
              :total="results.length"
              class="search-pagination">
            </el-pagination>
          </div>
        </div>
        <div v-else class="no-results">
          <p v-if="searched">没有找到相关公告</p>
          <p v-else>输入关键词进行搜索</p>
        </div>
      </div>
    </div>

    <!-- AI浮动球 -->
    <floating-ball></floating-ball>
  </div>
</template>

<script>
import FloatingBall from '../ai/FloatingBall.vue'

export default {
  name: 'SearchResults',
  components: {
    FloatingBall
  },
  data () {
    return {
      keywords: '',
      results: [],
      searched: false,
      pageSize: 10
    }
  },
  mounted () {
    // 检查是否有搜索关键词
    if (this.$route.query.keyword) {
      this.keywords = this.$route.query.keyword
      this.performSearch()
    }
  },
  watch: {
    '$route' (to, from) {
      if (to.query.keyword) {
        this.keywords = to.query.keyword
        this.performSearch()
      }
    }
  },
  methods: {
    performSearch () {
      if (!this.keywords || !this.keywords.trim()) {
        this.results = []
        this.searched = false
        return
      }

      this.searched = true
      const keyword = this.keywords.trim()

      // 尝试调用后端搜索API
      this.$axios.get(`/api/search/article?keyword=${encodeURIComponent(keyword)}`).then(resp => {
        if (resp && resp.data.code === 200) {
          this.results = resp.data.data.list || []
        }
      }).catch(error => {
        // 如果API不存在，前端过滤
        console.log('搜索API不存在，使用前端过滤')
        this.$axios.post('/content/article/1000/1').then(resp => {
          if (resp && resp.data.code === 200) {
            const allArticles = resp.data.data.list || []
            this.results = allArticles.filter(article =>
              (article.title && article.title.includes(keyword)) ||
              (article.abstracts && article.abstracts.includes(keyword))
            )
          }
        }).catch(err => {
          console.error('加载文章失败：', err)
        })
      })
    },

    // 高亮匹配的关键词（红色）
    highlightKeyword (text) {
      if (!text || !this.keywords) {
        return text
      }
      const regex = new RegExp(`(${this.escapeRegex(this.keywords)})`, 'gi')
      return text.replace(regex, '<span style="color: #ff0000; font-weight: bold;">$1</span>')
    },

    // 转义正则表达式特殊字符
    escapeRegex (str) {
      return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    },

    // 格式化日期
    formatDate (dateStr) {
      if (!dateStr) return ''
      try {
        const date = new Date(dateStr)
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
      } catch (error) {
        return dateStr
      }
    },

    // 通过得到的id跳转到公告详情
    goToArticle (id) {
      this.$router.push({ path: 'jotter/article', query: { id: id } })
    }
  }
}
</script>

<style scoped>
.search-results-container {
  min-height: calc(100vh - 200px);
  padding: 40px 0;
  background-color: #f5f5f5;
}

.search-wrapper {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 30px;
  padding: 0 20px;
}

/* 左侧搜索栏 */
.search-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.sidebar-section {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
}

.sidebar-title {
  margin: 0 0 15px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.search-input-wrapper {
  width: 100%;
}

.results-count-sidebar {
  background-color: white;
  border-radius: 8px;
  padding: 15px 20px;
  text-align: center;
  font-size: 14px;
  color: #666;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}

/* 右侧搜索结果 */
.search-results {
  flex: 1;
  min-width: 0;
}

.results-list {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}

.result-item {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.result-item:last-of-type {
  border-bottom: none;
  padding-bottom: 0;
}

.result-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  text-align: left;
  color: #0066cc;
  cursor: pointer;
  transition: color 0.3s ease;
  line-height: 1.4;
}

.result-title:hover {
  color: #0047ab;
  text-decoration: underline;
}

.result-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 12px;
  color: #999;
}

.meta-time {
  display: inline-flex;
  align-items: center;
}

.meta-module {
  display: inline-flex;
  align-items: center;
  color: #999;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.pagination-text {
  font-size: 12px;
  color: #999;
}

.search-pagination /deep/ .el-pagination {
  text-align: right;
}

.no-results {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .search-wrapper {
    width: 95%;
  }
}

@media (max-width: 1024px) {
  .search-wrapper {
    width: 95%;
    flex-direction: column;
    gap: 20px;
  }

  .search-sidebar {
    width: 100%;
  }

  .search-results {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .search-results-container {
    padding: 20px 0;
  }

  .search-wrapper {
    width: 100%;
    padding: 0 12px;
  }

  .sidebar-section,
  .results-count-sidebar {
    padding: 15px;
  }

  .sidebar-title {
    font-size: 16px;
    margin-bottom: 12px;
  }

  .results-list {
    padding: 15px;
  }

  .result-title {
    font-size: 15px;
  }

  .result-meta {
    font-size: 11px;
    gap: 10px;
  }

  .pagination-wrapper {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .search-pagination /deep/ .el-pagination {
    text-align: left;
  }
}

@media (max-width: 480px) {
  .search-wrapper {
    padding: 0 10px;
  }

  .sidebar-section {
    padding: 12px;
  }

  .results-list {
    padding: 12px;
  }

  .result-item {
    padding: 12px 0;
  }

  .result-title {
    font-size: 14px;
  }

  .result-meta {
    font-size: 10px;
  }

  .pagination-text {
    font-size: 11px;
  }
}
</style>
