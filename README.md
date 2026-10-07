# 球拍管理系统（Bat Manager）

基于 Spring Boot 和 Vue 2 的球拍商城及后台管理系统，包含商品、订单、发货、优惠券、退货、评价、用户和后台权限等功能。

## 项目结构

- `back/`：Spring Boot 3.4、MyBatis 后端，Java 17。
- `front/mall-admin-web-master/`：Vue 2、Element UI 前端，同时提供管理端和用户端。
- `database/batnew.sql`：当前 `batnew` 数据库的完整结构与数据快照，适合首次初始化。
- `database/migrations/`：增量迁移脚本，适合已有数据库升级。
- `PROJECT_IMPROVEMENT_PLAN.md`：项目问题分析和改进计划。

## 环境要求

- JDK 17
- Maven 3.8+，也可以使用 `back/mvnw`
- Node.js 14+、npm
- MySQL 8.x
- Redis 6+：后台管理、商品和普通登录不依赖 Redis；发送注册验证码和新用户注册时必须启动 Redis
- 阿里云 OSS：只有上传图片时需要配置

## 首次启动

### 1. 初始化数据库

先创建数据库：

```sql
CREATE DATABASE batnew DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

Windows PowerShell 或 IDEA Terminal 推荐使用 MySQL 的 `source` 命令，将路径替换为仓库的实际绝对路径：

```powershell
mysql -uroot -p --default-character-set=utf8mb4 -D batnew -e "source D:/work/bat-manager/database/batnew.sql"
```

Linux/macOS 也可以使用重定向导入：

```bash
mysql -uroot -p --default-character-set=utf8mb4 batnew < database/batnew.sql
```

如果本机已经有旧版 `batnew` 数据库，不要重新导入全量快照，执行增量迁移即可：

```powershell
mysql -uroot -p --default-character-set=utf8mb4 -D batnew -e "source D:/work/bat-manager/database/migrations/20261007_coupon_schema.sql"
```

迁移脚本可以重复执行，不会重复发放系统优惠券。

### 2. 配置后端

默认开发配置位于 `back/src/main/resources/application.yml`：数据库为 `localhost:3306/batnew`，用户名 `root`，密码 `123456`；Redis 为 `localhost:6379`。推荐通过环境变量覆盖本机配置：

```text
DB_URL=jdbc:mysql://localhost:3306/batnew
DB_USERNAME=root
DB_PASSWORD=your-database-password
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=replace-with-a-random-secret-at-least-32-bytes
ALIOSS_ACCESS_KEY_ID=your-access-key-id
ALIOSS_ACCESS_KEY_SECRET=your-access-key-secret
```

在 IDEA 中打开 `back`，将这些值填写到 Spring Boot Run Configuration 的 `Environment variables`，然后运行 `com.fsb.BatManagerApplication`。也可以使用命令行：

```bash
cd back
mvn spring-boot:run
```

后端默认地址为 `http://localhost:8080`。

### 3. 启动前端

```bash
cd front/mall-admin-web-master
npm install
npm run dev
```

前端默认地址为 `http://localhost:8090`，开发代理会把 `/api` 请求转发到 `http://localhost:8080`。

## 登录入口

| 入口 | 地址 | 示例账号 | 示例密码 |
| --- | --- | --- | --- |
| 管理端 | `http://localhost:8090/admin/login` | `admin` | `123456` |
| 用户端 | `http://localhost:8090/client/login` | `zhangsan` | `123456` |

旧的 `/login`、`/pms`、`/oms`、`/sms`、`/ums` 和 `/user` 页面路径仅作为兼容入口，新代码统一使用 `/admin` 和 `/client` 前缀。

## 常用操作

### 管理端

1. 在“商品”中维护球拍、分类、品牌、售价和库存。
2. 在“订单列表”中查看最新订单。待发货订单会显示醒目的红色状态，并出现在右上角铃铛提醒中。
3. 打开订单详情后可以进入发货页，填写物流公司和物流单号。
4. 在“退货申请”中审核用户退货，待审核记录也会出现在右上角铃铛中。
5. 在“优惠券”中查看发行量、领取量、已使用量和剩余量，并维护活动信息。

### 用户端

1. 登录后浏览球拍商城并提交订单。
2. 在“我的订单”中修改待发货订单信息、确认收货、申请退货或评价已完成订单。
3. 在“我的优惠券”中查看可用、已使用和已过期优惠券。现有用户会获得一张满 1000 减 200 优惠券，新注册用户会获得一张无门槛 50 元优惠券。
4. 新用户注册需要先获取邮箱验证码，此功能依赖 Redis；当前开发环境会在后端日志中输出验证码。

## 测试与构建

后端测试：

```bash
cd back
mvn test
```

前端生产构建：

```bash
cd front/mall-admin-web-master
npm run build
```

构建结果位于 `front/mall-admin-web-master/dist/`。

## 更新数据库快照

完成数据库结构或种子数据调整后，可以重新导出快照：

```powershell
mysqldump -uroot -p --default-character-set=utf8mb4 --single-transaction --routines --events --triggers --set-gtid-purged=OFF --hex-blob --complete-insert --skip-extended-insert --result-file=database/batnew.sql batnew
```

提交前应检查导出文件中是否包含真实用户隐私或生产凭据。本仓库快照仅用于本地开发和演示。

## 常见问题

- 启动时报 `jwtUtil` 初始化失败：检查 `JWT_SECRET` 是否至少 32 字节，或使用仓库默认的本地开发值。
- 注册或发送验证码时报 Redis 连接异常：启动 Redis，并核对 `REDIS_HOST`、`REDIS_PORT`；管理端普通功能不需要 Redis。
- 页面跳转到 404：确认使用 `/admin/...` 或 `/client/...` 路径，并重启前端开发服务器。
- 前端一直加载：确认后端 8080 端口已启动，并在浏览器 Network 中检查 `/api` 请求。
- 图片上传失败：配置 OSS 环境变量；不使用上传功能时可以留空。

## 安全说明

不要将生产数据库密码、JWT 密钥或 OSS 密钥提交到仓库。生产环境应通过环境变量或独立配置中心注入敏感信息，并按实际域名调整前端代理和后端跨域配置。

## 许可证

前端模板沿用其原有 Apache-2.0 许可；新增业务代码请依据项目实际发布策略使用。
