# 球拍管理系统（Bat Manager）

基于 Spring Boot 和 Vue 2 的球拍商城及后台管理系统，包含商品、订单、发货、优惠券、退货、评价、用户和后台权限等功能。

## 项目结构

- `back/`：Spring Boot 3.4、MyBatis 后端，Java 17。
- `front/mall-admin-web-master/`：Vue 2、Element UI 前端，同时提供管理端和用户端。
- `database/batnew.sql`：当前 `batnew` 数据库的完整结构与数据快照，适合首次初始化。
- `database/migrations/`：增量迁移脚本，适合已有数据库升级。
- `docker-compose.dev.yml`：仅用于本地开发的 MySQL/Redis 编排，首次创建数据卷时自动导入演示快照和审计迁移。
- `MCP_TOOL_CONTRACT.md`：Nexus 商城/身份 MCP 工具、错误码、幂等与并发契约。
- `PROJECT_IMPROVEMENT_PLAN.md`：项目问题分析和改进计划。

## 环境要求

- JDK 17
- Maven 3.8+（可直接使用仓库内的 `back/mvnw`）
- Node.js 14 或 16、npm（该 Vue 2 / webpack 3 项目建议使用 Node 14/16）
- MySQL 8.x
- Redis 6+：商城基础浏览可不启动 Redis；验证码注册、平台身份绑定及 AI 跨平台身份解析需要 Redis
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

平台身份管理员审计功能需要先在商城数据库执行 `database/migrations/20261111_identity_binding_audit.sql`。该表只保存由 `AI_CONTEXT_SECRET` 生成的 HMAC-SHA256 外部平台 ID 摘要，不保存原始 ID；身份映射由 Redis 管理，正向 key 和反向集合成员都使用域分离 HMAC 稳定引用，身份 metadata 中的外部 ID 使用 AES-GCM 随机 nonce 加密，Redis 索引不直接保存外部 ID。绑定列表 API 只向网页返回 HMAC 绑定引用，不返回原始 Telegram/QQ 用户 ID；解绑和管理员迁移也使用该引用定位身份。绑定码有效期为 5 分钟，nonce 和绑定幂等记录也使用短期 TTL。身份映射在用户解绑或管理员迁移前持续有效，不因缓存 TTL 自行过期。旧版普通 SHA-256 key 和反向明文成员会在首次访问时惰性迁移；密钥轮换后应按迁移流程处理旧索引。

身份绑定通过 Redis Lua 脚本原子更新正向映射、反向索引和版本元数据。当前脚本涉及多个 Redis key，要求这些 key 能在同一个 Redis 原子执行域内访问；Redis Cluster 默认跨 slot，不能直接使用。部署可使用单主 Redis 或 Redis Sentinel。生产环境还应配置 Redis 持久化、备份与恢复演练。

AI 客服转人工使用 MySQL `support_ticket` 表。已有数据库需执行 `database/migrations/20261112_ai_support_ticket.sql` 和 `database/migrations/20261113_ai_support_ticket_reply.sql`；Docker 开发环境首次初始化会自动导入基础表。用户可以通过网站、Telegram 或 QQ 创建和查询自己的工单，管理员在后台“订单 → 客服工单”查看、回复并更新状态。客户回复与内部备注分开保存，AI 和用户端只会看到客户回复，不会看到内部备注。工单创建使用用户和消息级幂等键；AI 工具不能设置用户 ID，也不能修改工单处理状态。

迁移脚本可以重复执行，不会重复发放系统优惠券。数据库快照含有演示账号和演示订单，不应作为生产数据使用。

本地也可以用 Docker 一次启动 MySQL 和 Redis。该配置只适合开发/演示，首次创建数据卷时会自动导入快照和身份审计迁移；删除数据卷会清空本地数据：

```bash
MYSQL_ROOT_PASSWORD='bat-manager-local' docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.dev.yml ps
```

然后用下面的环境变量启动后端：

```bash
DB_URL='jdbc:mysql://127.0.0.1:3306/batnew?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false' \
DB_USERNAME=root \
DB_PASSWORD='bat-manager-local' \
REDIS_HOST=127.0.0.1 \
REDIS_PORT=6379 \
JWT_SECRET='replace-with-a-random-secret-at-least-32-bytes' \
./back/mvnw -f back/pom.xml spring-boot:run
```

生产环境不要使用该 Compose 文件中的演示快照或默认密码；应使用独立数据库用户、密钥管理和经过备份策略的 MySQL/Redis 实例。

### 2. 配置后端

`back/src/main/resources/application.yml` 使用环境变量配置数据库。默认数据库地址为 `localhost:3306/batnew`、用户名 `root`；数据库密码和 JWT 密钥没有默认值，启动前必须注入。Redis 默认地址为 `localhost:6379`。基础商城启动需要配置数据库和 JWT，验证码、身份绑定与 AI 客服还需要 Redis：

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

只有需要图片上传时才配置 OSS 密钥。`JWT_SECRET` 应使用至少 32 字节的随机值。AI 跨平台身份功能启用时，后端的 `AI_INTERNAL_TOKEN` 和 `AI_CONTEXT_SECRET` 必须与 Nexus `.env` 中的值完全一致。可以在 IDEA 的 Spring Boot Run Configuration 中设置这些变量；命令行启动示例：

```bash
cd back
DB_URL='jdbc:mysql://127.0.0.1:3306/batnew?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false' \
DB_USERNAME=bat_manager \
DB_PASSWORD='your-database-password' \
JWT_SECRET='replace-with-a-random-secret-at-least-32-bytes' \
./mvnw spring-boot:run
```

也可以在当前终端导出变量后启动：

```bash
export DB_URL='jdbc:mysql://127.0.0.1:3306/batnew?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false'
export DB_USERNAME=bat_manager
export DB_PASSWORD='your-database-password'
export JWT_SECRET='replace-with-a-random-secret-at-least-32-bytes'
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
- [HKUDS/nanobot](https://github.com/HKUDS/nanobot) 源码/运行时，安装其项目依赖；本地源码模式可用虚拟环境执行 `pip install -e '.[api,redis]'`
- Nexus Bot 工作区（包含 `api_server.py`、`config.json`、`mcp_servers/mall_server.py` 和 `mcp_servers/ticket_server.py`）
- 与模型服务匹配的 API Key、API Base URL 和模型名称

创建 Python 虚拟环境，并分别安装 Nanobot 与 Nexus Bot 依赖（将路径替换成各自的 clone 目录）：

```bash
python3 -m venv /path/to/nexus-bot/.venv
source /path/to/nexus-bot/.venv/bin/activate
cd /path/to/nanobot
pip install -e '.[api,redis]'
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
AI_CONTEXT_SECRET=replace-with-a-different-random-secret-at-least-32-characters
NANOBOT_REDIS_URL=redis://127.0.0.1:6379/0
```

将 `config.json` 中的模型名改为供应商实际支持的模型。Adapter 和 Spring Boot 必须使用相同的 `AI_INTERNAL_TOKEN`。确认商城后端和数据库已经启动后，在 Nexus Bot 目录启动 Adapter：

```bash
cd /absolute/path/to/nexus-bot
source .venv/bin/activate
PYTHONPATH="$PWD" python api_server.py
```

还需要在 Spring Boot 环境变量中设置。`AI_INTERNAL_TOKEN` 与 Adapter 保持相同，`AI_CONTEXT_SECRET` 也必须与 Nexus Bot 的 `.env` 一致；两者都应使用至少 32 个字符的独立随机值：

```text
AI_ADAPTER_URL=http://127.0.0.1:8091
AI_INTERNAL_TOKEN=与Nexus-Bot的.env中相同的随机值
AI_CONTEXT_SECRET=与Nexus-Bot的.env中相同的另一随机值
AI_MAX_CONCURRENT_REQUESTS=64
AI_CONCURRENCY_LEASE_SECONDS=60
AI_ADAPTER_CONNECT_TIMEOUT_MS=3000
AI_ADAPTER_READ_TIMEOUT_MS=120000
```

最后启动或重启后端，访问用户端并登录即可使用右下角客服。要从 Telegram 或 QQ 查询个人订单/优惠券，先在用户端“平台绑定”生成对应平台的绑定码，再私聊机器人发送 `/bind <绑定码>`。绑定完成后，不同平台可以访问映射到同一个商城账号的个人数据；群聊只允许查询公开商品和售后政策。管理员身份迁移只改变平台身份映射，不合并商城用户、订单或优惠券。后端隔离环境已完成绑定、跨平台本人订单查询、幂等重试、解绑拒绝访问和管理员审计的 HTTP 验收；上线前仍需使用真实 Telegram/QQ 凭据验证平台收发链路。Nanobot 启动时设置 `NANOBOT_REDIS_URL` 并安装 `redis` extra 后，渠道用同一 Redis 对 `channel + chat_id + message_id` 做 10 分钟跨进程去重；Redis 不可用时会限时退回进程内去重。健康检查 `http://127.0.0.1:8091/health` 只表示 Adapter 在运行；模型 Key、模型名或供应商 API 配置错误仍会导致对话失败。后端内部 AI API（包括网站 `/user/ai/**` 和 Nexus 调用的 `/internal/ai/**`）使用 Redis 共享并发租约（默认全局 64 个请求，租约默认 60 秒），多实例部署时仍共享同一 Redis 上限；Redis 暂时不可用时 AI API 快速返回 `UPSTREAM_ERROR`，不会绕过并发保护。不要把模型 API Key、服务 Token、签名密钥或本地 `.env` 提交到 Git。

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

- 启动时报 `jwtUtil` 初始化失败：检查是否设置了至少 32 字节的 `JWT_SECRET`。
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
