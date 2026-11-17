<!-- 定义404页面 -->
<template>
    <div class="error-page"></div>
</template>

<script>
    export default {
        name: 'Error404'
    }
</script>

<style scoped>
  .error-page {
    background: url("../../assets/img/bg/404.png") no-repeat center center;
    background-size: cover;
    margin: 0;
    height: 100vh;
    width: 100%;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 1;
  }
</style>
