# 阿里云 OSS 球拍图片上传本地测试指南

## 改进总结

已完成以下 OSS 上传集成改进：

### ✅ 修改清单

| 文件 | 改动 |
|------|------|
| `src/components/Upload/singleUpload.vue` | 启用 OSS 模式（`useOss: true`），添加文件校验，改进 URL 生成逻辑 |
| `src/components/Upload/multiUpload.vue` | 启用 OSS 模式（`useOss: true`），添加文件校验，改进 URL 生成逻辑 |
| `src/utils/ossUpload.js` | 新建 OSS 工具模块，包含生成签名 key、验证文件、处理 URL 等函数 |

### 核心改进点

1. **动态 Key 生成** — 每个文件上传时生成唯一的 OSS key（路径），避免覆盖
   - 格式：`{dir}/{timestamp}_{random}.{ext}`
   - 确保多次上传同一文件不会相互覆盖

2. **文件验证** — 上传前检查文件大小和类型
   - 限制大小：10MB
   - 支持格式：jpg, png, jpeg
   - 验证失败会提示用户并阻止上传

3. **OSS 表单参数** — 完善 OSS 直传表单参数
   - 修改 `ossaccessKeyId` → `OSSAccessKeyId`（符合 OSS API 规范）
   - 添加 `success_action_status: '200'`（上传成功返回 200）

4. **错误处理** — 增强错误提示和日志
   - 获取策略失败时提示用户
   - 上传成功后获取 URL 失败时提示用户
   - 详细的控制台日志

5. **URL 生成** — 根据后端返回的 host 和 key 正确拼接完整 URL

---

## 本地测试步骤

### 环境准备

在项目根目录，使用 PowerShell 执行：

```powershell
# 1. 进入项目目录
cd d:\work\bat-manager\front\mall-admin-web-master

# 2. 安装依赖（如需）
npm install

# 3. 启动开发服务器
npm run dev
```

> 若已启动过服务器，按 `Ctrl+C` 停止后重新启动确保加载最新代码。

### 功能测试流程

#### 测试场景 1：单图上传（产品主图）

1. **进入产品编辑页面**
   - 在浏览器打开 `http://localhost:8080`
   - 进入 "商品管理 > 产品列表"
   - 点击某个产品的 "编辑" 按钮

2. **上传产品主图**
   - 在 "第三步，填写详细信息" 页面中
   - 在 "商品主图" 区域点击 "点击上传"
   - 选择一个 JPG/PNG 图片（<10MB）

3. **验证上传结果**
   - ✅ 应该看到 "文件上传成功" 提示
   - ✅ 图片预览应该显示上传的图片
   - ✅ 浏览器开发者工具中，检查网络请求：
     - 应向 `http://macro-oss.oss-cn-shenzhen.aliyuncs.com` 发送 POST 请求
     - 表单中应包含：`policy`, `signature`, `OSSAccessKeyId`, `key`, `success_action_status`
   - ✅ 浏览器控制台应无错误或警告（除了可能的跨域警告）

#### 测试场景 2：多图上传（产品画册）

1. **在同一产品编辑页**
   - 滚动到 "商品相册" 区域
   - 点击 "+" 按钮添加多张图片

2. **上传多张图片**
   - 连续上传 3-5 张图片
   - 每张图片应显示单独的上传进度

3. **验证上传结果**
   - ✅ 每张图片上传后应显示 "文件上传成功" 提示
   - ✅ 所有图片应显示在图片列表中
   - ✅ 网络请求中，每张图片的 key 应是唯一的（不同的时间戳和随机数）
   - ✅ 删除图片时应能正常移除

#### 测试场景 3：文件验证

1. **尝试上传不符合要求的文件**
   - 上传一个 BMP 格式的图片
   - 预期：收到 "不支持的文件格式，仅支持: jpg, png, jpeg" 提示

2. **尝试上传超大文件**
   - 上传一个 >10MB 的图片
   - 预期：收到 "文件大小超过限制（最大 10MB）" 提示

### 网络调试检查清单

使用 Chrome 开发者工具（F12 → Network 标签）检查：

```
✓ OSS 上传请求：
  URL: http://macro-oss.oss-cn-shenzhen.aliyuncs.com
  Method: POST
  Status: 200 OK
  
  FormData 字段：
  - policy: (base64 字符串)
  - signature: (签名字符串)
  - OSSAccessKeyId: (阿里云 AccessKeyId)
  - key: (唯一的对象 key，格式：dir/timestamp_random.ext)
  - success_action_status: 200
  - file: (二进制文件数据)

✓ 响应检查：
  - 200 OK：表示上传成功
  - 响应头中 Location: (文件的最终 URL)
```

### 浏览器控制台检查清单

打开 Chrome 开发者工具 → Console 标签，检查：

```javascript
// 应该看到成功日志：
// "获取 OSS 策略成功"（或类似的成功提示）
// 不应看到：
// "Uncaught ReferenceError" 或其他 JavaScript 错误
// "CORS 错误" 或跨域相关的网络错误（OSS 应已配置 CORS）

// 可以在控制台中验证工具函数：
// console.log(window.$utils.generateOSSKey('ballracket', {name: 'test.jpg'}))
// 应该输出类似：ballracket/1732710240000_12345678.jpg
```

---

## 常见问题排查

### 问题 1：上传失败，返回 403 Forbidden

**原因**：OSS 策略签名失败或 AccessKeyId 无效

**排查步骤**：
1. 检查后端 `/aliyun/oss/policy` 接口是否正常响应
2. 在浏览器 Network 标签中查看该接口的响应，确保返回了 `policy`, `signature`, `accessKeyId` 等字段
3. 验证阿里云 AccessKey 配置是否正确

### 问题 2：上传后看不到图片，URL 为空或 404

**原因**：OSS 上传成功但 URL 生成错误

**排查步骤**：
1. 在浏览器 Network 中检查 OSS 上传请求的响应头中是否有 `Location` 字段
2. 确认后端 `/aliyun/oss/policy` 返回的 `host` 字段是否正确
3. 检查组件中的 `getOSSFileUrl` 函数是否正确拼接了 URL

### 问题 3：上传时收到 "获取上传策略失败" 提示

**原因**：后端 API 请求失败或超时

**排查步骤**：
1. 确保开发服务器正在运行（`npm run dev`）
2. 检查浏览器 Network 标签中对 `/aliyun/oss/policy` 的请求
3. 如果返回 404，可能是接口路径错误；如果返回 500，可能是后端服务异常

### 问题 4：文件类型检查提示 "不支持的文件格式"

**原因**：上传了不被允许的文件类型

**解决方案**：
- 目前仅支持 `jpg`, `png`, `jpeg` 格式
- 若需支持其他格式，在 `singleUpload.vue` 和 `multiUpload.vue` 中修改 `validateUploadFile` 调用，添加需要的扩展名

---

## 后端 API 说明

### GET /aliyun/oss/policy

获取阿里云 OSS 上传所需的临时凭证和签名信息。

**响应示例**：
```json
{
  "code": 1,
  "data": {
    "policy": "eyJleHBpcmF0aW9uIjoiMjAyNS0xMS0yOFQxMjowMDowMFoiLCJjb25kaXRpb25zIjpbWyJjb250ZW50LWxlbmd0aC1yYW5nZSIsIDAsIDEwNDg1NzYwMF0seyJidWNrZXQiOiJtYWNyby1vc3MifSx7ImtleSI6ImphdnluL3RdXX0=",
    "signature": "t9zg5xFaQ2K8x+m9pL2q4R7sT8uVwX9y0zAbCdEfG+I=",
    "accessKeyId": "LTAI4GexxxxxxxxvNrPx",
    "dir": "ballracket/2025-11-28",
    "host": "http://macro-oss.oss-cn-shenzhen.aliyuncs.com"
  }
}
```

**必需返回字段**：
- `policy`: Base64 编码的 OSS 上传策略
- `signature`: OSS 请求签名
- `accessKeyId`: 阿里云 Access Key ID
- `dir`: 上传目录前缀（用于组织文件）
- `host`: OSS Bucket 的访问主机 URL

---

## 性能优化建议

1. **分片上传**：对于超大文件（>100MB），考虑实现分片上传
2. **上传进度条**：可以在 `el-upload` 的 `on-progress` 事件中显示上传进度
3. **断点续传**：实现本地缓存，支持上传中断后的恢复
4. **CDN 加速**：将 OSS 的 URL 配置为 CDN 域名以提升访问速度

---

## 回滚方案

若需临时回退到 MinIO 上传，修改以下文件中的 `useOss` 值：

- `src/components/Upload/singleUpload.vue`：第 37 行改为 `useOss:false`
- `src/components/Upload/multiUpload.vue`：第 21 行改为 `useOss:false`

然后重启开发服务器即可。

---

**文档版本**：1.0  
**更新时间**：2025-11-28
