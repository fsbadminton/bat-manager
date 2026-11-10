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
- Maven 3.8+（可直接使用仓库内的 `back/mvnw`）
- Node.js 14 或 16、npm（该 Vue 2 / webpack 3 项目建议使用 Node 14/16）
- MySQL 8.x
- Redis 6+：启动应用不强制要求 Redis，但发送注册验证码和新用户注册需要 Redis
- 阿里云 OSS：只有上传图片时需要配置
- AI 客服额外需要 Python 3.11+、Nanobot 运行时、Nexus Bot 工作区和可用的模型 API Key

其他系统请安装对应版本的 JDK、Node.js、MySQL、Redis 和 Python。MySQL、Redis、后端、前端和 AI Adapter 默认分别使用 `3306`、`6379`、`8080`、`8090`、`8091` 端口。

## 首次启动

### 1. 初始化数据库

先创建数据库（MySQL 8）：

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

导入会创建并填充商城演示数据。若已有业务数据，不要直接覆盖；请先备份并确认是否需要全量导入。若本机已经有旧版 `batnew` 数据库，先确认当前结构，再执行适用的增量迁移：

```powershell
mysql -uroot -p --default-character-set=utf8mb4 -D batnew -e "source D:/work/bat-manager/database/migrations/20261007_coupon_schema.sql"
```

迁移脚本可以重复执行，不会重复发放系统优惠券。数据库快照含有演示账号和演示订单，不应作为生产数据使用。

### 2. 配置后端

`back/src/main/resources/application.yml` 使用环境变量配置数据库。默认数据库地址为 `localhost:3306/batnew`、用户名 `root`、密码 `123456`；建议为自己的 MySQL 单独创建用户并通过环境变量覆盖。Redis 默认地址为 `localhost:6379`。启动前后端和数据库的本地开发环境只需设置数据库和 JWT：

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

只有需要图片上传时才配置 OSS 密钥。`JWT_SECRET` 应使用至少 32 字节的随机值。可以在 IDEA 的 Spring Boot Run Configuration 中设置这些变量；命令行启动示例：

```bash
cd back
DB_URL='jdbc:mysql://127.0.0.1:3306/batnew?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false' \
DB_USERNAME=bat_manager \
DB_PASSWORD='your-database-password' \
JWT_SECRET='replace-with-a-random-secret-at-least-32-bytes' \
./mvnw spring-boot:run
```

Windows 可在 IDEA 中配置环境变量后运行 `com.fsb.BatManagerApplication`，或使用已安装的 Maven 执行 `mvn spring-boot:run`。后端默认地址为 `http://localhost:8080`。

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

仓库中的 `database/batnew.sql` 是演示快照，示例管理员为 `admin`，用户为 `zhangsan`，默认演示密码均为 `123456`。首次登录后应修改密码；不要将这些演示账号用于公网或生产环境。

## AI 客服（可选）

AI 客服依赖独立的 Nexus Bot 和 Nanobot 工作区；这两个项目不包含在本仓库中。普通商城开发不需要安装 AI 依赖。需要启用 AI 客服时，准备：

- Python 3.11 或更高版本
- [HKUDS/nanobot](https://github.com/HKUDS/nanobot) 源码/运行时，安装其项目依赖；本地源码模式可用虚拟环境执行 `pip install -e '.[api]'`
- Nexus Bot 工作区（包含 `api_server.py`、`config.json`、`mcp_servers/mall_server.py` 和 `mcp_servers/ticket_server.py`）
- 与模型服务匹配的 API Key、API Base URL 和模型名称

创建 Python 虚拟环境，并分别安装 Nanobot 与 Nexus Bot 依赖（将路径替换成各自的 clone 目录）：

```bash
python3 -m venv /path/to/nexus-bot/.venv
source /path/to/nexus-bot/.venv/bin/activate
cd /path/to/nanobot
pip install -e '.[api]'
cd /path/to/nexus-bot
pip install -r requirements.txt
```

先在 Nexus Bot 目录创建并配置 `.env`（不要提交该文件）：

```dotenv
WORKSPACE=/absolute/path/to/nexus-bot
NEXUS_API_KEY=your-model-api-key
NEXUS_API_BASE=https://your-model-provider.example/v1
AI_BACKEND_URL=http://127.0.0.1:8080
AI_INTERNAL_TOKEN=replace-with-a-long-random-token
```

将 `config.json` 中的模型名改为供应商实际支持的模型。Adapter 和 Spring Boot 必须使用相同的 `AI_INTERNAL_TOKEN`。确认商城后端和数据库已经启动后，在 Nexus Bot 目录启动 Adapter：

```bash
cd /absolute/path/to/nexus-bot
source .venv/bin/activate
PYTHONPATH="$PWD" python api_server.py
```

还需要在 Spring Boot 环境变量中设置：

```text
AI_ADAPTER_URL=http://127.0.0.1:8091
AI_INTERNAL_TOKEN=与Nexus-Bot的.env中相同的随机值
```

最后启动或重启后端，访问用户端并登录即可使用右下角客服。健康检查 `http://127.0.0.1:8091/health` 只表示 Adapter 在运行；模型 Key、模型名或供应商 API 配置错误仍会导致对话失败。不要把模型 API Key 或内部服务 Token 提交到 Git。

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

后端测试（需要可连接的 MySQL 配置）：

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
- `CannotGetJdbcConnectionException`：确认 MySQL 服务已启动、`batnew` 已导入，并检查 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`；确认 MySQL 正在监听配置的端口。
- 前端一直加载：确认后端 8080 端口已启动，并在浏览器 Network 中检查 `/api` 请求。
- 图片上传失败：配置 OSS 环境变量；不使用上传功能时可以留空。
- AI 客服连接失败：确认 Adapter `8091` 与后端 `8080` 正常运行，两个进程的 `AI_INTERNAL_TOKEN` 一致，并检查 Nexus Bot `.env`、模型名和模型服务凭据。

## 安全说明

不要将生产数据库密码、JWT 密钥或 OSS 密钥提交到仓库。生产环境应通过环境变量或独立配置中心注入敏感信息，并按实际域名调整前端代理和后端跨域配置。

## 许可证

前端模板沿用其原有 Apache-2.0 许可；新增业务代码请依据项目实际发布策略使用。
