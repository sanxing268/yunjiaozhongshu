<!-- 新增编辑学科竞赛组件内部的图片上传组件 -->
<template>
  <el-upload
    class="img-upload"
    ref="upload"
    action="/api/admin/content/view/covers"
    with-credentials
    :on-preview="handlePreview"
    :on-remove="handleRemove"
    :before-remove="beforeRemove"
    :on-success="handleSuccess"
    :on-error="handleError"
    :before-upload="beforeAvatarUpload"
    multiple
    :limit="1"
    :on-exceed="handleExceed"
    :file-list="fileList"
  >
    <el-button size="small" type="primary">点击上传</el-button>
    <div slot="tip" class="el-upload__tip">
      只能上传jpg/png文件，且不超过5MB
    </div>
  </el-upload>
</template>

<script>
export default {
  name: 'ImgUpload',
  data () {
    return {
      fileList: [],
      url: '',
      flag: true
    }
  },
  methods: {
    handleRemove (file, fileList) {
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
      const testmsg = file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase()
      const extension = testmsg === 'jpg' || testmsg === 'jpeg' || testmsg === 'png'
      const isLt5M = file.size / 1024 / 1024 < 5
      if (!extension) {
        this.flag = false
        this.$message.warning(`当前限制上传文件只能是 jpg、png格式!`)
        return false
      }
      if (!isLt5M) {
        this.flag = false
        this.$message.warning(`上传文件大小不能超过 5MB!`)
        return false
      }
      this.flag = true
      return true
    },
    handleSuccess (response, file, fileList) {
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
      this.$emit('onUpload')
      this.$message.success('上传成功')
      console.log('上传成功，文件URL:', this.url)
    },
    handleError (err, file, fileList) {
      console.error('上传失败:', err, file)
      this.$message.error('上传失败: ' + (err.message || '未知错误'))
    },
    clear () {
      this.$refs.upload.clearFiles()
    }
  }
}
</script>

<style scoped>
.img-upload {
  height: 200px;
}
</style>
