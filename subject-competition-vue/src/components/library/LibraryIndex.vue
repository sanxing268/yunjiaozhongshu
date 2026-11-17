<!-- 前台竞赛一览的整体父容器，引入相关组件 -->
<template>
  <div class="library-wrapper">
    <div class="library-content">
      <div class="library-search-layout">
      <!-- 左侧搜索栏 -->
      <div class="library-sidebar">
        <div class="sidebar-section">
          <h4 class="sidebar-title">竞赛搜索</h4>
          <div class="search-input-wrapper">
            <el-input
              v-model="keywords"
              placeholder="搜索竞赛关键字"
              prefix-icon="el-icon-search"
              size="small"
              clearable
              @keyup.enter.native="performSearch"
              @clear="performSearch">
            </el-input>
          </div>
        </div>

        <div class="results-count-sidebar">共 {{ filteredBooks.length }} 个竞赛</div>
      </div>

      <!-- 右侧竞赛列表 -->
      <div class="library-main-new">
        <!-- 分类筛选（放在卡片上方） -->
        <div class="categories-top">
          <div class="filter-group">
            <span class="filter-label">级别：</span>
            <a href="javascript:void(0)" class="filter-link" @click="handleLevelClick('all')" :class="{active: level === 'all'}">全部</a>
            <a href="javascript:void(0)" class="filter-link" @click="handleLevelClick('国际级')" :class="{active: level === '国际级'}">国际级</a>
            <a href="javascript:void(0)" class="filter-link" @click="handleLevelClick('国家级')" :class="{active: level === '国家级'}">国家级</a>
            <a href="javascript:void(0)" class="filter-link" @click="handleLevelClick('省级')" :class="{active: level === '省级'}">省级</a>
          </div>
          <div class="filter-group">
            <span class="filter-label">分类：</span>
            <a href="javascript:void(0)" class="filter-link" @click="handleCategoryClick('0')" :class="{active: cid === '0'}">全部</a>
            <a 
              v-for="(item, i) in categorylist" 
              :key="i + 1" 
              href="javascript:void(0)" 
              class="filter-link" 
              @click="handleCategoryClick((i + 1).toString())"
              :class="{active: cid === (i + 1).toString()}">
              {{ item.name }}
            </a>
          </div>
        </div>
        <!-- 竞赛卡片列表 -->
        <books class="books-area" ref="booksArea" :books="filteredBooks"></books>
      </div>
    </div>
    </div>

    <!-- 页脚 -->
    <div class="footer-wrapper">
      <about id="about"></about>
    </div>
    
    <!-- AI浮动球 -->
    <floating-ball></floating-ball>
  </div>
</template>

<script>
  import Books from './Books'
  import FloatingBall from '../ai/FloatingBall.vue'
  import About from '../common/About.vue'

  export default {
    name: 'AppLibrary',
    components: {Books, FloatingBall, About},
    data() {
      return {
        keywords: '',
        categorylist: [],
        cid: '0',
        level: 'all', // 添加级别筛选变量
        allBooks: [],
        totalBooks: 0
      }
    },
    computed: {
      filteredBooks() {
        let filtered = this.allBooks
        // 按分类筛选
        if (this.cid !== '0') {
          const category = this.categorylist[parseInt(this.cid) - 1]
          const categoryId = category ? category.id : null
          if (categoryId) {
            filtered = filtered.filter(book => book.cid === categoryId)
          }
        }
        // 按级别筛选
        if (this.level !== 'all') {
          filtered = filtered.filter(book => book.author === this.level)
        }
        // 按关键词搜索
        if (this.keywords && this.keywords.trim()) {
          const keyword = this.keywords.trim().toLowerCase()
          filtered = filtered.filter(book => 
            (book.title && book.title.toLowerCase().includes(keyword)) ||
            (book.abs && book.abs.toLowerCase().includes(keyword)) ||
            (book.author && book.author.toLowerCase().includes(keyword)) ||
            (book.press && book.press.toLowerCase().includes(keyword))
          )
        }
        return filtered
      }
    },
    mounted() {
      this.loadCategorys()
      this.loadAllBooks()
      // 检查是否有预览参数
      this.checkPreviewId()
    },
    watch: {
      '$route'(to, from) {
        // 监听路由变化，检查是否有预览参数
        if (to.query.previewId) {
          this.checkPreviewId()
        }
      }
    },
    methods: {
      loadCategorys() {
        const _this = this
        this.$axios.get('/categoryList').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.categorylist = resp.data.data.list
          }
        })
      },
      loadAllBooks() {
        const _this = this
        // 固定返回全部
        this.$axios.get('/categories/0/books').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.allBooks = resp.data.data.list || []
            _this.totalBooks = _this.allBooks.length
          }
        }).catch(err => {
          console.error('Failed to load books:', err)
        })
      },
      performSearch() {
        // 搜索自动通过 computed 的 filteredBooks 过滤
      },
      handleCategoryClick(cid) {
        this.cid = cid
      },
      // 处理级别筛选点击
      handleLevelClick(level) {
        this.level = level
      },
      // 检查并打开预览
      checkPreviewId() {
        const previewId = this.$route.query.previewId
        if (previewId) {
          // 如果数据还没加载，等待数据加载完成
          if (this.allBooks.length === 0) {
            // 等待数据加载
            setTimeout(() => {
              this.checkPreviewId()
            }, 300)
            return
          }
          // 等待DOM更新后再打开弹窗
          this.$nextTick(() => {
            // 将 previewId 转换为数字进行比较（如果 id 是数字类型）
            const previewIdNum = Number(previewId)
            // 先尝试在所有书籍中查找（不依赖筛选条件）
            let targetBook = this.allBooks.find(book => {
              // 支持字符串和数字类型的比较
              return book.id === previewId || book.id === previewIdNum || String(book.id) === String(previewId)
            })
            
            // 如果没找到，再尝试在筛选后的书籍中查找
            if (!targetBook) {
              targetBook = this.filteredBooks.find(book => {
                return book.id === previewId || book.id === previewIdNum || String(book.id) === String(previewId)
              })
            }
            
            if (targetBook) {
              // 确保 Books 组件已经渲染
              if (this.$refs.booksArea) {
                // 调用Books组件的打开详情方法
                this.$refs.booksArea.openDetailDialog(targetBook)
                // 清除URL中的预览参数，但保持页面状态
                this.$router.replace({ path: '/library', query: {} })
              } else {
                // 如果 Books 组件还没渲染，再等待一下
                setTimeout(() => {
                  if (this.$refs.booksArea) {
                    this.$refs.booksArea.openDetailDialog(targetBook)
                    this.$router.replace({ path: '/library', query: {} })
                  }
                }, 200)
              }
            }
          })
        }
      }
    }
  }
</script>

<style scoped>
  .library-wrapper {
    min-height: calc(100vh - 200px);
    display: flex;
    flex-direction: column;
  }

  .library-content {
    flex: 1;
  }

  .footer-wrapper {
    margin-top: 60px;
    margin-bottom: 0;
  }

  .library-search-layout {
    width: 1200px;
    margin: 30px auto 0;
    display: flex;
    gap: 30px;
    padding: 0 20px;
  }

  /* 左侧搜索栏 */
  .library-sidebar {
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

  /* 右侧竞赛列表 */
  .library-main-new {
    flex: 1;
    min-width: 0;
  }

  .books-area {
    width: 100%;
  }

  /* 分类筛选（放在卡片上方） */
  .categories-top {
    background-color: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
    margin-bottom: 20px;
  }

  .filter-group {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-bottom: 15px;
    flex-wrap: wrap;
  }

  .filter-group:last-child {
    margin-bottom: 0;
  }

  .filter-label {
    font-weight: 600;
    color: #333;
    font-size: 14px;
    white-space: nowrap;
  }

  .filter-link {
    color: #0066cc;
    text-decoration: none;
    font-size: 14px;
    padding: 4px 12px;
    border-radius: 4px;
    transition: all 0.3s ease;
    cursor: pointer;
    display: inline-block;
  }

  .filter-link:hover {
    background-color: #f0f0f0;
    color: #0047ab;
  }

  .filter-link.active {
    background-color: #0066cc;
    color: white;
  }

  /* 响应式适配 */
  @media (max-width: 1200px) {
    .library-search-layout {
      width: 95%;
    }
  }

  @media (max-width: 1024px) {
    .library-search-layout {
      width: 95%;
      flex-direction: column;
      gap: 20px;
    }

    .library-sidebar {
      width: 100%;
    }

    .library-main-new {
      width: 100%;
    }
  }

  @media (max-width: 768px) {
    .library-wrapper {
      padding: 10px 0;
    }

    .library-search-layout {
      width: 100%;
      padding: 0 12px;
      gap: 15px;
    }

    .sidebar-section {
      padding: 15px;
    }

    .sidebar-title {
      font-size: 16px;
    }

    .categories-top {
      padding: 15px;
    }

    .filter-group {
      gap: 10px;
    }

    .filter-link {
      font-size: 13px;
      padding: 3px 10px;
    }
  }

  @media (max-width: 480px) {
    .library-search-layout {
      padding: 0 10px;
    }

    .sidebar-section {
      padding: 12px;
    }

    .categories-top {
      padding: 12px;
    }

    .filter-group {
      gap: 8px;
    }

    .filter-label {
      font-size: 13px;
    }

    .filter-link {
      font-size: 12px;
      padding: 2px 8px;
    }
  }
</style>
