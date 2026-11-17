<!-- 竞赛公告列表页面 -->
<template>
  <div class="articles-page-wrapper" style="margin-top: 40px">
    <div class="articles-area">
      <el-card class="article-card">
        <div class="page-header">
          <h2>竞赛公告</h2>
        </div>
        <div v-if="articles.length === 0" class="empty-tip">
          暂无竞赛公告
        </div>
        <div v-for="(article, index) in articles" :key="article.id" class="article-item">
          <div class="article-content">
            <router-link class="article-link" :to="{ path: '/jotter/article', query: { id: article.id } }">
              <span class="article-title">{{ article.title }}</span>
            </router-link>
            <el-divider content-position="left" class="date-divider">{{ article.createDate }}</el-divider>
            <router-link class="article-link" :to="{ path: '/jotter/article', query: { id: article.id } }">
              <p class="article-abstract">{{ article.abstracts }}</p>
            </router-link>
          </div>
          <el-image v-if="article.cover" :src="article.cover" fit="cover" class="cover-image">
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline" style="font-size: 30px; color: #ccc;"></i>
            </div>
          </el-image>
          <el-divider v-if="index < articles.length - 1" class="item-divider"></el-divider>
        </div>
      </el-card>
    </div>
    <div class="pagination-wrapper">
      <el-pagination background layout="total, prev, pager, next, jumper" @current-change="handleCurrentChange"
        :page-size="pageSize" :total="total" class="article-pagination">
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
  name: 'CompetitionNotice',
  components: { FloatingBall, About },
  data() {
    return {
      articles: [],
      pageSize: 10,
      total: 0
    }
  },
  mounted() {
    this.loadArticles()
  },
  methods: {
    loadArticles(page = 1) {
      var _this = this
      this.$axios.post('/content/article/' + this.pageSize + '/' + page, { articleType: 'competition' }).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.articles = resp.data.data.list || []
          _this.total = resp.data.data.total || 0
        }
      }).catch(error => {
        console.log('加载竞赛公告失败:', error)
      })
    },
    handleCurrentChange(page) {
      this.loadArticles(page)
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
  margin: 0 auto;
  max-width: 100%;
  padding: 0 20px;
}

.article-card {
  text-align: left;
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e8e8e8;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #333;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 15px;
}

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

.pagination-wrapper {
  margin: 40px 0 60px;
  display: flex;
  justify-content: center;
  width: 100%;
}

/* 响应式适配 */
@media (max-width: 1024px) {
  .articles-area {
    width: 95%;
  }
}

@media (max-width: 768px) {
  .articles-area {
    width: 100%;
    padding: 0 10px;
    margin-top: 20px;
  }

  .article-item {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  .article-content {
    float: none;
    width: 100%;
    height: auto;
  }

  .cover-image {
    float: none;
    margin: 0 auto;
    width: 150px;
    height: 150px;
  }

  .article-title {
    font-size: 18px;
  }
}
</style>
