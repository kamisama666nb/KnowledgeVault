# 📱 如何获取编译好的APK

## 方法一：GitHub Actions 自动编译（推荐）

### 第一步：上传到GitHub

1. **创建GitHub仓库**
   - 访问 https://github.com/new
   - 仓库名称填：`KnowledgeVault`
   - 设为 Public（公开）或 Private（私有）都可以
   - **不要**勾选 "Add a README file"
   - 点击 "Create repository"

2. **上传项目**（两种方式任选）

   **方式A：用GitHub网页直接上传**
   - 在仓库页面点击 "uploading an existing file"
   - 把整个 `KnowledgeVault` 文件夹里的所有文件拖进去
   - 点击 "Commit changes"

   **方式B：用Git命令（如果你会用）**
   ```bash
   cd KnowledgeVault
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin https://github.com/你的用户名/KnowledgeVault.git
   git branch -M main
   git push -u origin main
   ```

### 第二步：等待自动编译

1. 上传完成后，GitHub会自动开始编译
2. 进入仓库页面，点击顶部的 **"Actions"** 标签
3. 你会看到一个正在运行的工作流（黄色圆圈🟡）
4. 等待3-5分钟，直到变成绿色✅（表示成功）

### 第三步：下载APK

1. 点击那个成功的工作流
2. 往下滚动，在 **"Artifacts"** 区域
3. 点击 **"KnowledgeVault-debug"** 下载
4. 解压zip文件，得到 `app-debug.apk`

### 第四步：安装到手机

1. 把APK传到手机（通过数据线、网盘、或任何方式）
2. 手机上打开APK文件
3. 如果提示"不允许安装未知来源应用"：
   - 进入设置 → 允许该来源的安装
4. 点击安装

---

## 方法二：手动触发编译

如果你修改了代码，想重新编译：

1. 进入仓库的 **Actions** 页面
2. 左侧选择 "Build Android APK"
3. 点击右侧的 **"Run workflow"** 按钮
4. 点击绿色的 "Run workflow"
5. 等待编译完成，下载新的APK

---

## 方法三：每次代码修改自动编译

只要你：
- 修改代码后上传到GitHub
- 或者在GitHub网页上直接编辑文件

GitHub都会自动重新编译，生成新的APK！

---

## ⚠️ 可能遇到的问题

### 问题1：编译失败（红叉❌）

点击失败的工作流查看日志，常见原因：
- 代码有语法错误
- 缺少某个文件

把错误信息发给我，我帮你修复！

### 问题2：找不到Artifacts

确保：
- 工作流已经完成（绿色✅）
- 往下滚动到页面底部才能看到

### 问题3：APK无法安装

- 确保手机系统是 Android 8.0 或更高
- 检查是否允许安装未知来源应用

---

## 🎯 第一次使用建议

1. **先不配置AI**，直接试用基础功能
2. **手动添加几篇文档**，测试搜索功能
3. **添加一个RSS订阅**（比如你喜欢的博客）
4. 觉得不错再配置AI功能（参考 API_CONFIG.md）

---

## 📞 需要帮助？

- 编译失败了？把错误截图发给我
- 不会用GitHub？告诉我，我详细教你
- 想修改功能？随时回来找我

祝你使用愉快！🚀
