<!-- 前台首页的图片框 -->
<template>
    <div class="quick-nav">
      <img src="../../assets/img/icon/icon2.png" alt="" class="monster">
      <img src="../../assets/img/icon/icon5.png" alt="" class="readme">
      <img src="../../assets/img/icon/icon7.png" alt="" class="spark">
    </div>
</template>

<script>
  export default {
    name: 'QuickNav'
  }
</script>

<style scoped>
  .quick-nav {
    width: 303px;
    height: 318px;
    background-color: transparent;
  }

  .monster {
    width: 280px;
    height: 240px;
    /*position: absolute;*/
    /*top: 150px;*/
    /*left: 700px;*/
  }

  .readme {
    width: 300px;
    height: 285px;
    position: absolute;
    margin-left: 50%;
    top: 300px;
    left: 230px;
  }

  .spark {
    position: absolute;
    margin-left: 50%;
    top: 100px;
    left: 20px;
  }

  .logo {
    width: 280px;
    position: absolute;
    margin-left: 50%;
    top: 50px;
    left: 210px;
  }

</style>
