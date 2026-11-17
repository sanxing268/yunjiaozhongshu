<!-- 图片以及结题附件上传组件 -->
<template>
  <el-upload
    class="upload-demo"
    ref="upload"
    action="/api/admin/upload"
    with-credentials
    :on-preview="handlePreview"
    :on-remove="handleRemove"
    :before-remove="beforeRemove"
    :on-success="handleSuccess"
    :on-error="handleError"
    :before-upload="beforeAvatarUpload"
    :auto-upload="true"
    :limit="1"
    :on-exceed="handleExceed"
    :file-list="fileList"
    :disabled="uploading"
  >
    <el-button type="primary">上传结题附件</el-button>
  </el-upload>
</template>

<script>
export default {
  name: 'ImgUpload',
  data () {
    return {
      fileList: [],
      url: '',
      flag: true,
      uploading: false // 防止重复上传
    }
  },
  methods: {
    handleRemove (file, fileList) {
      // 重置上传状态
      this.uploading = false

      // 如果文件已经上传成功，需要删除COS中的文件
      const fileUrl = file.response || this.url
      if (fileUrl && typeof fileUrl === 'string' && fileUrl.startsWith('http')) {
        // 调用后端删除接口（使用POST方式，因为DELETE可能不支持body）
        this.$axios.post('/admin/file/delete', {
          fileUrl: fileUrl
        }).then((resp) => {
          if (resp && resp.data && resp.data.code === 200) {
            console.log('COS文件删除成功')
            this.$message.success('文件已从云端删除')
          } else {
            const msg = resp && resp.data ? resp.data.message : ''
            console.warn('COS文件删除失败:', msg)
            this.$message.warning('云端文件删除失败，但已从列表中移除')
          }
        }).catch((err) => {
          console.error('删除COS文件时出错:', err)
          this.$message.warning('删除云端文件时出错，但已从列表中移除')
        })
      }
      this.url = ''
      this.fileList = []
      this.$emit('onUpload')
    },
    handlePreview (file) {
      // 此处的 file 是整个文件
      // console.log(file.response)
    },
    handleExceed (files, fileList) {
      this.$message.warning(
        `当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${
          files.length + fileList.length
        } 个文件`
      )
    },
    beforeRemove (file, fileList) {
      // this.url = ''
      // this.$emit('onUpload')
      if (this.flag) {
        return this.$confirm(`确定移除 ${file.name}？`)
      }
    },
    beforeAvatarUpload (file, fileList) {
      // 防止重复上传：如果正在上传，阻止新的上传
      if (this.uploading) {
        this.$message.warning('文件正在上传中，请勿重复操作')
        return false
      }

      // 如果已经有文件在上传列表，先移除旧文件
      if (this.fileList.length > 0) {
        this.$message.warning('请先移除已上传的文件')
        return false
      }

      const isLt5M = file.size / 1024 / 1024 < 5
      if (!isLt5M) {
        this.flag = false
        this.$message.warning(`上传文件大小不能超过 5MB!`)
        return false
      }

      // 开始上传，设置上传状态
      this.uploading = true
      this.flag = true
      return true
    },
    handleSuccess (response, file, fileList) {
      // 上传完成，重置上传状态
      this.uploading = false

      // 后端现在直接返回 COS 的完整访问 URL（字符串）
      // response 可能是字符串，也可能是对象，需要兼容处理
      if (typeof response === 'string') {
        this.url = response
      } else if (response && typeof response === 'object') {
        // 如果后端返回的是对象，尝试获取 URL 字段
        this.url = response.url || response.data || response
      } else {
        this.url = response
      }

      // 确保 fileList 只保留当前上传的文件
      this.fileList = fileList

      this.$emit('onUpload')
      this.$message.success('上传成功')
      console.log('上传成功，文件URL:', this.url)
    },
    handleError (err, file, fileList) {
      // 上传失败，重置上传状态
      this.uploading = false
      console.error('上传失败:', err, file)
      this.$message.error('上传失败: ' + (err.message || '未知错误'))
      // 移除失败的文件
      this.fileList = []
      this.url = ''
    },
    clear () {
      this.uploading = false
      this.url = ''
      this.fileList = []
      this.$refs.upload.clearFiles()
    }
  }
}
</script>

<style scoped>
</style>
