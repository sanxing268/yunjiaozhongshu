<!-- 后台轮播图管理组件 -->
<template>
    <div class="pic-manage-wrap">
<!-- 面包屑导航 -->
      <el-row style="margin: 10px 0px 0px 3px">
            <el-breadcrumb separator-class="el-icon-arrow-right">
                <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
                <el-breadcrumb-item>内容管理</el-breadcrumb-item>
                <el-breadcrumb-item>轮播图管理</el-breadcrumb-item>
            </el-breadcrumb>
        </el-row>

        <!-- 页面头部：标题 + 新增按钮 -->
        <div class="page-header">
            <el-button type="success" icon="el-icon-plus" @click="openPicDialog(true)">新增轮播图</el-button>
        </div>

        <!-- 轮播图列表表格 -->
        <el-card class="pic-table-card">
            <el-table v-loading="loading" :data="picList" border stripe style="width: 100%" row-key="id">
                <el-table-column prop="id" label="ID" align="center" width="80" />
                <el-table-column prop="title" label="轮播图标题" align="center" min-width="200" />
                <el-table-column label="图片预览" align="center" width="180">
                    <template slot-scope="scope">
                        <el-image :src="scope.row.url" style="width: 120px; height: 80px; border-radius: 4px"
                            fit="cover" @error="handleImageError(scope.row)">
                            <div slot="error" class="image-error">图片加载失败</div>
                        </el-image>
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="160">
                    <template slot-scope="scope">
                        <el-button type="text" icon="el-icon-edit" @click="openPicDialog(false, scope.row)"
                            size="small">编辑</el-button>
                        <el-button type="text" icon="el-icon-delete" @click="deletePic(scope.row.id)" size="small"
                            style="color: #f56c6c">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 新增/编辑轮播图弹窗 -->
        <el-dialog :title="dialogTitle" :visible.sync="picDialogVisible" width="500px" destroy-on-close
            :close-on-click-modal="false">
            <el-form :model="picForm" :rules="picRules" ref="picFormRef" label-width="100px" label-position="right">
                <!-- 隐藏ID：编辑时使用 -->
                <el-form-item prop="id" hidden>
                    <el-input v-model="picForm.id" />
                </el-form-item>

                <el-form-item label="轮播图标题" prop="title">
                    <el-input v-model="picForm.title" placeholder="请输入轮播图标题" maxlength="50" show-word-limit />
                </el-form-item>

                <el-form-item label="轮播图图片" prop="url">
                    <el-upload class="pic-upload" :action="uploadUrl" :headers="uploadHeaders"
                        :on-success="handleUploadSuccess" :on-error="handleUploadError" :before-upload="beforeUpload"
                        :file-list="fileList" list-type="picture-card" :limit="1" :on-exceed="handleExceed"
                        with-credentials>

                        <i class="el-icon-plus" />
                    </el-upload>
                    <div class="upload-tip" style="margin-top: 8px; font-size: 12px; color: #999">
                        提示：仅支持 JPG/PNG 格式，图片大小不超过 2MB
                    </div>
                </el-form-item>
            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button @click="picDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitPicForm">确定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
// import axios from 'axios'
import { Message } from 'element-ui'

export default {
    name: 'PicManage',
    data() {
        return {
            // 加载状态
            loading: false,
            // 轮播图列表
            picList: [],
            // 弹窗显示状态
            picDialogVisible: false,
            // 新增/编辑模式标识：true=新增，false=编辑
            isAdd: true,
            // 轮播图表单数据
            picForm: {
                id: '',
                title: '',
                url: '' // 图片地址
            },
            // 表单校验规则
            picRules: {
                title: [
                    { required: true, message: '请输入轮播图标题', trigger: 'blur' },
                    { max: 50, message: '轮播图标题长度不能超过50个字符', trigger: 'blur' }
                ],
                url: [
                    { required: true, message: '请上传轮播图图片', trigger: 'change' }
                ]
            },
            // 上传文件列表
            fileList: [],
            // 图片上传接口地址
            uploadUrl: '/api/admin/upload/carousel',
            // 上传请求头（如有token认证）
            uploadHeaders: {
                // Authorization: 'Bearer ' + localStorage.getItem('token')
            },
            uploading: false, // 控制上传状态
            // 默认错误图片
            defaultImg: '/static/img/404.png'
        }
    },
    computed: {
        // 弹窗标题
        dialogTitle() {
            return this.isAdd ? '新增轮播图' : '编辑轮播图'
        }
    },
    mounted() {
        // 页面加载时查询轮播图列表
        this.getPicList()
    },
    methods: {

        // 文件超出限制提示
        handleExceed(files, fileList) {
            Message.warning(`仅支持上传1张图片`);
        },

        // 1. 查询轮播图列表
        async getPicList() {
            this.loading = true
            try {
                // 替换为 this.$axios
                const res = await this.$axios.get('/pic/list')
                if (res.data && res.data.code === 200) {
                    this.picList = res.data.data.list || []
                } else {
                    Message.error('查询轮播图列表失败')
                    this.picList = []
                }
            } catch (err) {
                console.error('查询轮播图失败：', err)
                Message.error('查询轮播图列表异常，请稍后重试')
                this.picList = []
            } finally {
                this.loading = false
            }
        },


        // 2. 打开新增/编辑弹窗
        openPicDialog(isAdd, pic = {}) {
            this.isAdd = isAdd
            this.picDialogVisible = true
            this.fileList = [] // 重置上传文件列表
            this.uploading = false // 重置上传状态

            // 重置表单
            this.$nextTick(() => {
                if (this.$refs.picFormRef) {
                    this.$refs.picFormRef.clearValidate()
                }

                // 初始化表单数据
                this.picForm = {
                    id: pic.id || '',
                    title: pic.title || '',
                    url: pic.url || ''
                }

                // 编辑模式：回显图片上传列表
                if (!isAdd && pic.url) {
                    this.fileList = [
                        {
                            name: pic.title || '轮播图',
                            url: pic.url
                        }
                    ]
                }
            })
        },

        // 3. 图片上传前校验
        beforeUpload(file) {
            // 防止重复上传
            if (this.uploading) {
                Message.warning('图片正在上传中，请勿重复操作');
                return false;
            }
            // 校验逻辑
            const isImage = ['image/jpg', 'image/jpeg', 'image/png'].includes(file.type)
            const isLt2M = file.size / 1024 / 1024 < 2
            if (!isImage) {
                Message.error('仅支持上传 JPG/PNG 格式的图片！')
                return false
            }
            if (!isLt2M) {
                Message.error('图片大小不能超过 2MB！')
                return false
            }
            // 开始上传，设置状态
            this.uploading = true;
            return true
        },

        // 4. 图片上传成功回调
        handleUploadSuccess(response, file, fileList) {
            // 重置上传状态
            this.uploading = false;
            // 兼容后端返回字符串（COS URL）或对象格式
            let imgUrl = '';
            if (typeof response === 'string') {
                imgUrl = response;
            } else if (response && typeof response === 'object') {
                // 兼容后端返回 {code:200, data: "URL"} 或 {url: "URL"} 格式
                imgUrl = response.url || response.data || '';
            }
            // 验证图片URL有效性
            if (imgUrl && imgUrl.startsWith('http')) {
                this.picForm.url = imgUrl;
                this.fileList = fileList; // 同步更新文件列表
                Message.success('图片上传成功');
            } else {
                Message.error('图片上传失败：返回无效地址');
                this.fileList = []; // 重置文件列表
                this.picForm.url = '';
            }
        },

        // 5. 图片上传失败回调
        handleUploadError(err, file, fileList) { // 补充参数，对齐ImgUpload
            console.error('图片上传异常：', err)
            Message.error('图片上传失败，请稍后重试')
            this.uploading = false; // 重置上传状态
            this.fileList = []; // 重置文件列表
            this.picForm.url = '';
        },

        // 6. 提交新增/编辑表单
        async submitPicForm() {
            try {
                // 表单校验
                await this.$refs.picFormRef.validate()

                let res
                // 新增模式
                if (this.isAdd) {
                    res = await this.$axios.post('/pic/add', this.picForm)
                } else {
                    // 编辑模式
                    res = await this.$axios.put('/pic/update', this.picForm)
                }

                if (res.data.code === 200) {
                    Message.success(this.isAdd ? '新增轮播图成功' : '编辑轮播图成功')
                    this.picDialogVisible = false // 关闭弹窗
                    this.getPicList() // 刷新列表
                } else {
                    Message.error(res.data.message || (this.isAdd ? '新增轮播图失败' : '编辑轮播图失败'))
                }
            } catch (err) {
                console.error('提交轮播图表单异常：', err)
                if (err.name !== 'ValidationError') {
                    Message.error('操作失败，请稍后重试')
                }
            }
        },

        // 删除轮播图并同步删除COS图片
        async deletePic(id) {
            try {
                // 先根据ID查询该轮播图的图片 URL
                const targetPic = this.picList.find(item => item.id === id);
                if (!targetPic || !targetPic.url) {
                    Message.warning('未获取到轮播图图片地址，无法删除 COS 文件');
                    return;
                }

                // 确认删除
                await this.$confirm('确定要删除该轮播图吗？删除后无法恢复！', '警告', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });

                //  第一步：删除 COS上的图片文件
                const deleteCosRes = await this.$axios.post(
                    '/admin/file/delete',
                    { fileUrl: targetPic.url } // 传递 COS 图片 URL
                );

                // 第二步：删除数据库中的轮播图记录（无论 COS 删除是否成功，都尝试删除数据库记录，避免数据不一致）
                const deleteDbRes = await this.$axios.delete(`/pic/delete/${id}`);

                //  提示结果
                if (deleteDbRes.data.code === 200) {
                    Message.success('删除轮播图成功');
                    // 若 COS 删除失败，给出提示
                    if (deleteCosRes.data.code !== 200) {
                        Message.warning('轮播图记录已删除，但 COS 图片删除失败，请手动清理');
                    } else {
                        Message.success('云端轮播图删除成功');
                    }
                    this.getPicList(); // 刷新列表
                } else {
                    Message.error(deleteDbRes.data.message || '删除轮播图失败');
                }
            } catch (err) {
                if (err.type !== 'cancel') {
                    console.error('删除轮播图异常：', err);

                } else {
                    console.log('用户取消删除轮播图'); 
                }
            }
        },

        // 图片加载失败处理
        handleImageError(row) {
            row.url = this.defaultImg // 替换为默认图片
        }
    }
}
</script>

<style scoped>
.pic-manage-wrap {
    padding: 20px;
    background-color: #f5f7fa;
    min-height: calc(100vh - 60px);
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    margin-top: 20px;
}

.page-header h2 {
    margin: 0;
    color: #303133;
    font-weight: 600;
}

.pic-table-card {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.image-error {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f5f5;
    color: #999;
    font-size: 12px;
    border-radius: 4px;
}

.pic-upload {
    margin-bottom: 0;
}

.dialog-footer {
    text-align: right;
}
</style>