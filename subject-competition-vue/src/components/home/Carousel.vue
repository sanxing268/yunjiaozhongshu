<!-- 前台轮播图组件 -->
<template>
  <div class="card-carousel">
    <el-carousel v-if="items.length > 0" class="push" :interval="4000" arrow="hover" indicator-position="inside" height="380px">
      <el-carousel-item v-for="item in items" :key="item.id">
        <a :href="item.link || 'javascript:void(0)'" :target="item.link ? '_blank' : ''">
          <el-image 
            :src="item.img" 
            :alt="item.title"
            style="width: 100%; height: 100%;"
            fit="fill"
            @error="handleImageError">
          </el-image>
        </a>
      </el-carousel-item>
    </el-carousel>
    <div v-else class="carousel-empty">
      <p>暂无轮播图数据</p>
    </div>
  </div>
</template>

<style>
  .card-carousel {
    margin: 0;
    width: 100%;
    height: 100%;
    overflow: hidden;
    position: relative;
    border: 1px solid #e8e8e8;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
  }
  
  .card-carousel >>> .el-carousel {
    height: 100%;
  }
  
  .card-carousel >>> .el-carousel__container {
    height: 100%;
  }

  .card-carousel >>> .el-carousel__item {
    height: 100%;
  }
  
  .card-carousel >>> .el-carousel__item a {
    display: block;
    width: 100%;
    height: 100%;
  }

  .card-carousel >>> .el-image {
    display: block;
  }
  
  .card-carousel >>> .el-image__inner {
    width: 100%;
    height: 100%;
  }

  .carousel-empty {
    width: 100%;
    height: 380px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #999;
    font-size: 14px;
  }
  
  /* 指示器样式 - 在图片上方 */
  .card-carousel >>> .el-carousel__indicators {
    bottom: 20px !important;
  }
  
  .card-carousel >>> .el-carousel__indicator {
    padding: 8px 5px !important;
    background-color: transparent !important;
  }
  
  .card-carousel >>> .el-carousel__button {
    width: 20px !important;
    height: 10px !important;
    border-radius: 3px !important;
    background-color: rgba(255, 255, 255, 0.4) !important;
    transition: all 0.3s ease !important;
    opacity: 0.6;
  }
  
  .card-carousel >>> .el-carousel__indicator:hover .el-carousel__button {
    background-color: rgba(255, 255, 255, 0.7) !important;
    opacity: 0.9;
    transform: scale(1.15);
  }
  
  .card-carousel >>> .el-carousel__indicator.is-active .el-carousel__button {
    width: 36px !important;
    height: 6px !important;
    border-radius: 3px !important;
    background-color: #fff !important;
    opacity: 1;
  }

  /* 响应式适配 */
  @media (max-width: 1024px) {
    .card-carousel {
      width: 95%;
      margin: 0 auto;
      height: 280px;
    }
  }

  @media (max-width: 768px) {
    .card-carousel {
      width: 100%;
      margin: 0;
      height: 200px;
    }
  }

  @media (max-width: 480px) {
    .card-carousel {
      height: 160px;
    }
  }
</style>

<script>
  export default {
    name: 'Carousel',
    data: function () {
      return {
        items: []
      }
    },
    mounted() {
      this.loadCarouselData()
    },
    methods: {
      loadCarouselData() {
        const _this = this
        this.$axios.get('/pic/list').then(resp => {
          console.log('轮播图API完整响应:', JSON.stringify(resp, null, 2))
          console.log('轮播图API响应data:', resp.data)
          console.log('轮播图API响应code:', resp.data ? resp.data.code : '无code')
          
          if (resp && resp.data && resp.data.code === 200) {
            const list = resp.data.data ? resp.data.data.list : []
            console.log('轮播图数据列表:', list)
            
            if (list && list.length > 0) {
              _this.items = list.map(item => ({
                id: item.id,
                title: item.title || '',
                img: item.url || '',
                link: '' // 如果需要链接，可以在数据库中添加 link 字段
              }))
              console.log('处理后的轮播图items:', _this.items)
            } else {
              console.warn('轮播图数据列表为空')
              _this.items = []
            }
          } else {
            console.warn('轮播图API返回异常 - 完整响应:', JSON.stringify(resp, null, 2))
            console.warn('轮播图API返回异常 - resp.data:', resp.data)
            _this.items = []
          }
        }).catch(err => {
          console.error('Failed to load carousel data - 错误详情:', err)
          console.error('Failed to load carousel data - 响应:', err.response)
          // 如果加载失败，使用默认数据
          _this.items = [{
            id: 1,
            title: '默认图片',
            img: '../../../static/img/carousel/404.png',
            link: ''
          }]
        })
      },
      handleImageError (e) {
        e.target.src = this.$defaultImage
      }
    }
  }
</script>
