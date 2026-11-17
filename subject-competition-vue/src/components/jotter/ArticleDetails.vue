<!-- 前台点击公告后跳转的公告详细页面的组件  -->
<template>
  <div class="articles-area">
    <el-card class="article-card">
      <div>
        <h1 class="article-title">{{article.title}}</h1>
        <el-divider content-position="left">{{article.createDate}}</el-divider>
        <div class="markdown-body">
          <div v-html="article.contentHtml"></div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'ArticleDetails',
    data () {
      return {
        article: []
      }
    },
    mounted () {
      this.loadArticle()
    },
    methods: {
      loadArticle () {
        var _this = this
        this.$axios.get('/article/' + this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.article = resp.data.data.list
          }
        })
      }
    }
  }
</script>

<style scoped>
  @import "../../styles/markdown.css";

  .articles-area {
    width: 100%;
    padding: 20px;
    margin-top: 20px;
  }

  .article-card {
    text-align: left;
    width: 990px;
    margin: 0 auto;
    max-width: 100%;
  }

  .article-title {
    font-size: 20px;
    font-weight: bold;
    margin: 0 0 15px 0;
    color: #333;
  }

  /* 响应式适配 */
  @media (max-width: 1024px) {
    .article-card {
      width: 95%;
    }

    .articles-area {
      padding: 15px 10px;
    }
  }

  @media (max-width: 768px) {
    .articles-area {
      padding: 10px;
      margin-top: 10px;
    }

    .article-card {
      width: 100%;
      margin: 0;
    }

    .article-title {
      font-size: 18px;
    }

    .markdown-body {
      font-size: 14px;
    }

    .markdown-body >>> img {
      max-width: 100%;
      height: auto;
    }
  }

  @media (max-width: 480px) {
    .articles-area {
      padding: 8px;
    }

    .article-title {
      font-size: 16px;
    }

    .markdown-body {
      font-size: 13px;
    }
  }
</style>
