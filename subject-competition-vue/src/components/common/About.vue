<!-- 前台页面底部信息栏 -->
<template>
  <div class="footer">
    <div style="border-top:1px #1F1F1F solid;">
      <p class="alt" style="color:#999; line-height:0">&copy; 版权所有：STARoy &nbsp;
        <span>技术支持：</span>
        <a href="" target="_blank" style="color:#999;">bili746</a>
      </p>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'Footer'
  }
</script>

<style scoped>

  * {
    margin: 0;
    padding: 0;
    font-family: '微软雅黑', Arial, Helvetica, sans-serif;
  }

  body {
    font-size: 12px;
  }

  div {
    font-size: 12px;
  }

  span {
    font-size: 12px;
  }

  .footer {
    background: #2b2e33;
  }

  .footer .alt {
    overflow: hidden;
    padding: 20px 0;
    width: 100%;
    max-width: 1160px;
    margin: 0 auto;
    text-align: center;
  }

  .footer .alt p {
    text-align: center;
    line-height: 1.5;
  }

  .footer .alt .foot {
    width: 138px;
    float: left;
  }

  .footer .alt .foot p {
    color: #FFF;
    line-height: 28px;
  }

  a{
    text-decoration: none;
  }

  .footer .alt .foot a {
    color: #999;
    display: block;
    line-height: 24px;
  }

  /* 响应式适配 - 确保在所有屏幕尺寸下都居中 */
  @media (max-width: 1200px) {
    .footer .alt {
      max-width: 95%;
      padding: 20px 15px;
    }
  }

  @media (max-width: 768px) {
    .footer .alt {
      max-width: 100%;
      padding: 15px 10px;
    }

    .footer .alt p {
      font-size: 12px;
      line-height: 1.8;
      word-wrap: break-word;
    }
  }

  @media (max-width: 480px) {
    .footer .alt {
      padding: 12px 8px;
    }

    .footer .alt p {
      font-size: 11px;
      line-height: 1.6;
    }
  }
</style>
