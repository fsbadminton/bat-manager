# 球拍管理系统（Bat Manager）

这是一个面向羽毛球/球拍商品的前后端分离管理系统，包含商品目录、订单、优惠券、退货申请、评价、用户与后台权限等功能。项目由 Spring Boot 后端、Vue 管理端和 MySQL 初始化脚本组成。

## 项目结构

- `back/`：Spring Boot 3.4 + MyBatis 后端服务（Java 17）。提供 `/admin/**` 管理接口和 `/user/**` 用户端接口，并集成 JWT、Redis、阿里云 OSS。
- `front/mall-admin-web-master/`：Vue 2 管理后台，使用 Vue Router、Vuex、Element UI、Axios 和 ECharts。
- `bat.sql`：MySQL 数据库 `batnew` 的表结构及基础数据脚本。
- `总体/`、`temp_images/`：项目设计文档及文档配图（未纳入当前 Git 提交）。

## 环境要求

- JDK 17（项目编译配置兼容 Java 16，推荐使用 JDK 17）
- Maven 3.8+（或使用 `back/mvnw`）
- Node.js 14+、npm
- MySQL 8.x
- Redis 6+
- 阿里云 OSS（仅在启用文件上传时需要）

## 快速启动

### 1. 初始化数据库

创建数据库并导入脚本：

```sql
CREATE DATABASE batnew DEFAULT CHARACTER SET utf8mb4;
```

```bash
mysql -uroot -p batnew < bat.sql
```

根据本机环境修改 `back/src/main/resources/application.yml` 中的 MySQL 和 Redis 连接信息。阿里云 OSS 凭据通过环境变量提供：

```bash
ALIOSS_ACCESS_KEY_ID=your-access-key-id
ALIOSS_ACCESS_KEY_SECRET=your-access-key-secret
```

### 2. 启动后端

```bash
cd back
mvn spring-boot:run
```

服务默认监听 `http://localhost:8080`。

### 3. 启动管理端

```bash
cd front/mall-admin-web-master
npm install
npm run dev
```

开发服务器默认访问地址为 `http://localhost:8090`。开发环境 API 地址在 `config/dev.env.js` 的 `BASE_API` 中配置，默认指向 `http://localhost:8080`。

生产构建：

```bash
npm run build
```

## 主要接口模块

- 管理端：仪表盘、商品/品牌/分类、订单与发货、优惠券、退货原因与售后、评价、用户和权限管理。
- 用户端：注册登录、商品浏览、购物车/订单、优惠券、评价和退货申请。
- 公共能力：JWT 鉴权、Redis 验证码/状态存储、OSS 图片上传。

## 配置与安全

请勿将数据库密码、JWT 密钥或 OSS 密钥提交到仓库。生产环境应通过环境变量或独立配置文件注入敏感信息，并按实际域名调整前端 `BASE_API` 和后端跨域设置。

## 许可证

仓库中的前端模板沿用其原有 Apache-2.0 许可；新增业务代码请依据项目实际发布策略使用。
