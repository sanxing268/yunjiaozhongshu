本项目

**1.node.js版本不高于12**

大家先下载一个nvm来管理一下node.js版本

把node.js的版本改为12.22.12(不高于12)

下载nvm 1.1.12(确保是这个版本，再高也会报错)安装包，点击下面链接下载（自家服务器下载点，安全）[点我下载](https://staroy-1349382502.cos.ap-guangzhou.myqcloud.com/nvm-setup.exe?q-sign-algorithm=sha1&q-ak=AKIDNVEaqBXJetLBIFFUhO0JZ-I3611SUguXmOgNVreEjizlxkbl_Yk-pNmKJO5QAqqt&q-sign-time=1765977909;1765981509&q-key-time=1765977909;1765981509&q-header-list=host&q-url-param-list=&q-signature=f571635f8958b17a9f7ae19e252bad8f88b659ab&x-cos-security-token=vgu0tC7TRdDHcB5bAmWXjeyEljxH5OUa0f780d612effd4ad688827da2f3a8eb1X0ppvNCKVM1lnXweStvm7KftacuLZgecxLSEmMEStRvi1f0T1Ww1i94R3OQAzHD3PZMrcT51Uz8hoA-ali0-Ays_UfRHojbDVADxyWIwokQPlEaghtGqb1m7UR2wJwVYAaER2Mu4409MEoN6qCRPre0Hae-Cfo8_Y4Sifv8uNAXKJNOrGLC2xCy14umJVqvQpJhDIQhX7U_F5DnIVYtLXk0fCrYO65jXEzmOh8UoADIxg9Hggrx4VVx6gGDsZITULChWHUvX1LV3OyA7VepxOg)

打开文件所在位置，找到settings.txt

在最后添加

```
node_mirror: https://npmmirror.com/mirrors/node/
```

![image-20251217210327655](https://s2.loli.net/2025/12/17/RCPSAkpLurQyv2T.png)

前端项目运行的时候，我们需要安装dart-sass

#### 2.替换 node-sass 为 dart-sass

1. 卸载旧的 node-sass：

   ```bash
   npm uninstall node-sass --save
   ```

2. 安装 dart-sass（sass）

   ```bash
   npm install sass --save-dev
   ```




然后就可以直接运行了

然后如果后端无法运行可能是因为你的java版本的问题，看看多少，这个项目建议说用jdk1.8，可以尝试直接在idea改，可以参考下面的链接

[【IEDA】已解决：IDEA中jdk的版本切换-腾讯云开发者社区-腾讯云](https://cloud.tencent.com/developer/article/2524145)

---

## 🎯 AI教师识别功能（新增 2026年1月14日）

### 📌 功能说明

在教师管理系统中添加了**AI驱动的自动识别功能**。用户可以：
1. 上传包含教师信息的图片
2. 系统自动识别姓名、手机号、邮箱
3. 自动生成递增账号（从1000001开始）
4. 一键保存到数据库

### 🚀 快速使用

```bash
# 1. 启动后端（需要Java 1.8+）
cd subject-competition-master
java -jar target/subject-competition-master-0.0.1-SNAPSHOT.jar

# 2. 启动前端（新终端）
cd subject-competition-vue
npm run dev

# 3. 打开浏览器
# http://localhost:8080
# 登录 → 用户管理 → 教师管理 → 点击"AI自动识别"按钮
```

### 📚 完整文档

| 文档 | 说明 |
|------|------|
| [AI教师识别_快速参考.md](AI教师识别_快速参考.md) | ⚡ 快速入门（5分钟） |
| [AI自动识别使用指南.md](AI自动识别使用指南.md) | 📖 用户操作手册 |
| [AI识别功能部署测试指南.md](AI识别功能部署测试指南.md) | 🔧 部署和测试指南 |
| [AI识别教师信息功能说明.md](AI识别教师信息功能说明.md) | 🏗️ 技术实现细节 |
| [AI识别教师信息项目总结.md](AI识别教师信息项目总结.md) | 📊 项目总结报告 |
| [AI教师识别功能_验收检查清单.md](AI教师识别功能_验收检查清单.md) | ✅ 验收检查清单 |
| [AI教师识别功能_最终交付清单.md](AI教师识别功能_最终交付清单.md) | 📦 交付清单 |
| [项目文件结构与变更总览.md](项目文件结构与变更总览.md) | 📁 文件结构和变更 |

### 🔑 API端点

```http
POST /api/admin/teacher/recognize
Content-Type: multipart/form-data

参数：file（图片文件）

响应：
{
  "code": 200,
  "message": "识别成功",
  "data": {
    "results": [
      {
        "username": "1000001",
        "name": "张三",
        "phone": "13912345678",
        "email": "zhangsan@example.com",
        "password": "123456",
        "enabled": "1"
      }
    ],
    "count": 1
  }
}
```

### 📊 项目成果

- ✅ 前端UI实现完成
- ✅ 后端服务实现完成
- ✅ 数据库集成完成
- ✅ 编译打包成功
- ✅ 8份完整文档
- ✅ 正式代码审查清单

### ⚠️ 当前限制

- 🔄 当前使用Mock数据（示例结果），支持真实API后期集成
- 📝 初始密码固定为123456（用户首次登录应修改）
- 📤 单次仅支持单文件上传

### 🔧 修改的文件

**新增：**
- `subject-competition-master/src/main/java/com/subject/service/AIRecognitionService.java`
- `subject-competition-master/src/main/java/com/subject/service/impl/AIRecognitionServiceImpl.java`

**修改：**
- `subject-competition-vue/src/components/admin/user/TeacherManagement.vue` (+120行)
- `subject-competition-master/src/main/java/com/subject/controller/TeacherController.java`
- `subject-competition-master/src/main/java/com/subject/mapper/UserMapper.java`
- `subject-competition-master/src/main/resources/mapper/UserMapper.xml`

### 📋 建议下一步

1. **今天**：启动后端、执行基础测试
2. **明天**：申请千问API Key、集成真实识别
3. **后天**：完整测试、性能优化
4. **下周**：生产部署、用户培训

---

**了解更多**：查看项目根目录的各份文档

**项目状态**：✅ 开发完成，准备部署 🚀







对更新项目的解释

请重新导入这个数据库，提交了图片链接，放在云端存储桶中

所有的角色密码全部改正为123456

角色有以下几种

游客：无需登录，看图一

学生：如图

教师：如图

超级管理员：

管理员有权限的分类，超级管理员可以把部分内容给管理员，让管理员变成内容管理员，数据管理员等等



![image-20251218221935134](https://s2.loli.net/2025/12/18/WtRwaFo3LsErI4y.png)

![image-20251218222058461](https://s2.loli.net/2025/12/18/OrCAlkEFY14XgDu.png)

![image-20251218222129249](C:\Users\24450\AppData\Roaming\Typora\typora-user-images\image-20251218222129249.png)

![image-20251218222154339](https://s2.loli.net/2025/12/18/T84a5dcYLUm6iqO.png)





























