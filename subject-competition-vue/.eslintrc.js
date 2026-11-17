// https://eslint.org/docs/user-guide/configuring
module.exports = {
  root: true,
  parserOptions: {
    parser: 'babel-eslint'
  },
  env: {
    browser: true,
  },
  extends: [
    'plugin:vue/essential',
    'standard'
  ],
  plugins: [
    'vue'
  ],
  rules: {
    // 允许 async-await
    'generator-star-spacing': 'off',
    // 取消强制驼峰命名
    'camelcase': 'off',
    // 关闭缩进检查
    'indent': ["off", 2],
    // 开发环境允许 debugger，生产环境禁止
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    // 关闭空行/行尾空格检查
    'no-multiple-empty-lines': 'off',
    'no-trailing-spaces': 'off',
    // 关闭文件末尾必须留换行的检查
    'eol-last': 'off',
    'spaced-comment': 'off',
    'padded-blocks': 'off',
    'handle-callback-err': 'off',
    // 禁用多空格校验规则，不再报错
    'no-multi-spaces': 'off',
    'semi': 'off',
    "quotes": ["off", "single"],
    "space-infix-ops": "off",
    "key-spacing": "off",
    //关闭函数括号前空格的检查
    'space-before-function-paren': 'off'
  }
}