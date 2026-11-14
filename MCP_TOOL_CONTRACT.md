# Nexus 商城 MCP 工具契约

本文定义 Nexus Bot 调用 Bat Manager 商城/身份能力的 MCP 边界。Bat Manager 是用户、订单、优惠券和平台身份的权威数据源；模型不得从对话中推断或填写商城用户 ID。工具名、Schema、成功/失败包络和权限规则是 Adapter 与后端共同遵守的接口。

## 身份与调用上下文

渠道 Adapter 从已认证的消息事件生成上下文：`platform`、`external_user_id`、`chat_id`、`message_id`、`session_id`、`is_dm`。平台仅接受 `telegram`、`qq`；`external_user_id` 必须是渠道提供的稳定用户 ID。用户 ID 不能来自模型参数、自然语言或可由用户编辑的会话变量。

Nexus MCP 使用独立的 `AI_INTERNAL_TOKEN` 访问后端，并使用 `AI_CONTEXT_SECRET` 对身份上下文签名。渠道身份签名包括身份/请求类型、平台、外部 ID、`is_dm`、时间戳及随机 nonce；绑定确认签名还包括绑定码和幂等键。后端校验时间窗、HMAC 和一次性 nonce，个人工具和绑定接口必须验证签名中的私聊标记。密钥不可写入 MCP 参数或日志。网站客服使用已认证的商城 Web 用户 ID，仍需签名；不得把 Web 会话身份伪装成 Telegram/QQ 身份。

Web Adapter 收到的 `X-AI-Username` 不作为可信身份来源。Adapter 使用已签名的 Web 用户 ID 调用 Bat Manager `/internal/ai/identity/web/status`，由后端解析账号状态与用户名；MCP 查询业务数据时仍按签名 Web 用户 ID 再解析用户。平台外部 ID 只保留在签名调用上下文中，不拼入模型提示词。

群聊只开放公开商品及售后政策工具。绑定状态、绑定和任何个人信息查询必须在机器人私聊中调用。群聊请求这类能力时返回 `FORBIDDEN`，不能尝试降级为群成员身份查询。

## 工具清单

所有工具参数均为 JSON object，并设置 `additionalProperties: false`。未知字段、类型不匹配和越界输入一律返回 `INVALID_ARGUMENT`。

| 工具名 | 参数 | 权限及行为 |
| --- | --- | --- |
| `mall.search_products` | `keyword?: string`（最多 100 字符）；`brand_name?: string`（最多 100）；`category_id?: integer`（最小 1）；`page_size?: integer`（默认 10，1-20） | 公开；只返回已上架、未删除商品；响应使用 AI 商品视图，不含删除、审核、上下架状态及数据库内部字段 |
| `mall.get_product` | `product_id: integer`（最小 1） | 公开；未上架或不存在时返回 `NOT_FOUND`；响应使用 AI 商品视图，不含删除、审核、上下架状态及数据库内部字段 |
| `mall.list_my_orders` | `page_size?: integer`（默认 10，1-20） | 私聊且已绑定；用户身份从签名上下文解析；返回 AI 专用订单视图，不含收件人联系方式/地址、用户备注、管理员备注或物流单号 |
| `mall.get_my_order` | `order_id: integer`（最小 1） | 私聊且已绑定；只能获取当前映射商城用户的订单，其他用户订单按 `NOT_FOUND` 处理；返回 AI 专用订单视图，不含收件人联系方式/地址、用户备注、管理员备注或物流单号 |
| `mall.list_my_coupons` | `page_size?: integer`（默认 10，1-20） | 私聊且已绑定；只查询当前映射用户的优惠券；返回 AI 专用视图，不含会员用户名/昵称、优惠券兑换码或适用范围原始 JSON |
| `mall.get_return_policy` | `{}` | 公开；返回商城配置/后端提供的售后政策，不承诺审核结果或时效 |
| `identity.get_binding_status` | `{}` | Telegram/QQ 私聊；只返回当前渠道身份是否绑定，不回显外部 ID |
| `identity.bind_platform` | `token: string`（20-128 字符） | Telegram/QQ 私聊；一次性消费商城用户端生成的 5 分钟绑定码，平台和外部 ID 只取自渠道上下文 |
| `ticket.create` | `title: string`（1-120）；`description: string`（1-4000）；`priority?: low\|medium\|high\|urgent` | 网站登录用户或 Telegram/QQ 私聊的当前身份；服务端生成幂等键并创建本人客服工单 |
| `ticket.list_my` | `limit?: integer`（默认 10，1-20） | 只返回当前身份创建的工单 |
| `ticket.get_my` | `ticket_no: string`（3-24 字符） | 只返回当前身份自己的工单；他人或不存在的工单统一 `NOT_FOUND` |

订单列表、订单详情和优惠券查询不得添加 `user_id`、`username`、`platform` 或 `external_user_id` 等可覆写身份的参数。新增工具须先扩充本契约、权限矩阵和后端接口测试，再开放给模型。

## 返回结构

工具 `TextContent.text` 必须是 UTF-8 JSON。成功：

```json
{
  "ok": true,
  "request_id": "渠道消息或请求关联 ID",
  "data": {},
  "meta": {
    "tool": "mall.list_my_orders",
    "elapsed_ms": 42
  }
}
```

失败：

```json
{
  "ok": false,
  "request_id": "渠道消息或请求关联 ID",
  "error": {
    "code": "IDENTITY_NOT_BOUND",
    "message": "当前平台身份尚未绑定商城账号",
    "retryable": false,
    "details": {}
  },
  "meta": {
    "tool": "mall.list_my_orders",
    "elapsed_ms": 12
  }
}
```

`request_id` 在错误路径也必须返回；`meta.tool` 与 `meta.elapsed_ms` 对成功和失败响应都必填。`details` 只放非敏感、稳定的机器可读信息，不放原始外部 ID、绑定码、令牌、订单地址/电话或上游响应全文。

## 错误码与重试

| 错误码 | 含义 | `retryable` |
| --- | --- | --- |
| `INVALID_ARGUMENT` | 参数不符合 Schema | `false` |
| `INVALID_CONTEXT` / `UNAUTHENTICATED` | 签名、服务认证或渠道上下文无效 | `false` |
| `IDENTITY_NOT_BOUND` | 当前平台身份尚未绑定 | `false`；引导用户生成绑定码 |
| `FORBIDDEN` | 群聊调用个人能力或身份无权访问 | `false` |
| `NOT_FOUND` | 商品、订单或资源不存在/不可见 | `false` |
| `CONFLICT` | 身份已绑定其他账号、幂等键被复用或绑定码已撤销 | `false`；需人工确认或重新生成绑定码 |
| `UPSTREAM_TIMEOUT` | 后端调用超时 | `true`；调用方最多重试一次，需复用同一幂等键 |
| `UPSTREAM_ERROR` | 网络、后端或依赖服务暂时失败 | 通常 `true`；受重试上限和熔断约束 |
| `RATE_LIMITED` | 会话或服务并发/速率限制 | `true`；遵循 `retry_after_ms`（若提供） |
| `INTERNAL_ERROR` | 未分类内部错误 | `false`；记录 request ID 并转人工/稍后重试 |

失败时 Agent 不得根据上下文编造订单或库存信息。它应简短说明失败原因；仅 `retryable=true` 时可自动重试一次，仍失败则提示稍后再试或转人工。授权/参数/未绑定错误不能通过反复重试解决。

## 幂等与并发

- 所有查询工具为只读幂等操作。
- `ticket.create` 使用 Adapter 根据当前会话消息 ID和身份生成的幂等键；若渠道没有稳定消息 ID，则为本次 MCP 调用生成随机请求 ID，并在本次调用重试期间复用。相同键重复请求返回原工单，使用相同键提交不同内容返回 `CONFLICT`。工单状态由后台客服处理，Agent 不提供任意状态修改工具。
- `identity.bind_platform` 的幂等键由 MCP 根据渠道身份与绑定码摘要生成，不接受模型提供。后端在 Redis 原子脚本中消费一次性绑定码并写入正向映射、反向索引、版本和幂等记录。相同身份、绑定码和幂等键重试返回原绑定结果；同一幂等键用于其他请求返回 `CONFLICT`。
- 同一 `session_id` 的 Agent turn 串行执行，不共享可变用户上下文。Nanobot `BaseChannel` 依据 `channel + chat_id + message_id` 先做最多 10,000 条的本地缓存，再对配置的 Redis 使用 `SET NX EX` 做 10 分钟跨进程重复投递抑制；没有稳定消息 ID 的入口不去重，Redis 超时或故障时限时退回本地去重。Nexus HTTP Adapter 按进程限制 Agent turn 并发（`AI_MAX_CONCURRENT_TURNS`，默认 32），商城 MCP Server 按进程限制工具并发（`AI_MAX_CONCURRENT_TOOL_CALLS`，默认 16）；分别由有限排队窗口控制，超限返回 HTTP 429 或 MCP `RATE_LIMITED`。Bat Manager 的 `/internal/ai/**` 入口另外使用 Redis ZSET 租约做跨实例全局并发保护（`AI_MAX_CONCURRENT_REQUESTS`，默认 64；`AI_CONCURRENCY_LEASE_SECONDS`，默认 60 秒），请求完成释放，进程异常由租约过期回收；并发存储不可用时快速返回 `UPSTREAM_ERROR`，禁止绕过保护继续访问数据库。多进程部署时仍需为 Adapter 和 MCP 进程设置合理的本地上限。
- Redis Lua 脚本是身份正反向索引的并发原子边界。禁止在脚本外先读再写作为唯一冲突保护。旧版 SHA-256 身份 key 和反向明文成员在查询或变更时惰性迁移到 HMAC key 与 HMAC 反向引用；身份 metadata 中的外部 ID 使用 AES-GCM 随机 nonce 密文保存。迁移会原子更新索引、元数据和版本；新旧映射冲突时拒绝访问，需人工核对。脚本当前跨多个 key，Redis Cluster 跨 slot 部署不兼容；使用单主/哨兵 Redis，或先改成同 hash slot 的 key 设计。
- 工具上游请求超时上限为 8 秒，MCP 总调用上限为 10 秒；写操作超时后可用同一个幂等键查询/重试，不可换新幂等键重复绑定。

## 日志与隐私

每次调用记录 `request_id`、`tool`、耗时、结果类别/错误码、重试次数及限流/超时信息。不得记录完整对话、绑定码、服务 Token、签名密钥、原始外部 ID或完整订单个人资料。正向 Redis 查找 key、反向集合成员及管理审计中的外部 ID 摘要使用服务端 HMAC-SHA256 与域分离；外部 ID 密文只保存在身份 metadata。Redis 仍需限制网络访问、启用认证并使用受控持久化。签名密钥轮换需迁移身份索引并保留审计解释策略。

## 当前实现差异与验收门槛

Nexus Bot 当前已有上述商城与身份工具及 JSON Schema 校验；MCP 所有成功/失败路径统一填充 `request_id`、工具名和耗时，后端提供的 `errorCode` 会映射到稳定 MCP 错误码，不依赖本地化错误消息文本。MCP 包络、错误码映射、群聊拒绝、并发饱和响应、Nanobot 共享 Redis 去重及 Bat Manager 的 Redis 跨实例并发租约已有实现；Nanobot 的 Redis 去重测试和真实 Telegram/QQ 多实例端到端仍需运行验收。

发布前至少验证：未绑定访客可以查公开商品但不能查个人数据；群聊个人数据工具被拒绝；私聊绑定后只能查本人订单/优惠券；另一平台绑定到同一商城用户后得到同一数据范围；重复消息/绑定重试不重复产生副作用；解绑/迁移并发无双重归属；签名过期及 nonce 重放均被拒绝。
