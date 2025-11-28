# 后端 API 实现指导

本文档基于前端仪表盘（用户端 Dashboard）的需求，提供后端接口实现指导。

## 1. 用户商品列表接口

**前端调用**：`listUserProducts(mapped)` 
- 文件：`src/api/userLogin.js`

**接口设计**

```
GET /user/product/list
或
POST /user/product/list
```

**请求参数（Query 或 Body）**

```json
{
  "pageNum": 1,
  "pageSize": 5,
  "name": "球拍名称关键词（可选）",
  "categoryId": 1,
  "brandId": 1
}
```

**响应格式**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "records": [
      {
        "productId": 1,
        "name": "李宁N8球拍",
        "imageUrl": "https://oss-url/product1.jpg",
        "price": 299.99,
        "productSn": "LN-N8-001",
        "stock": 50,
        "brandName": "Li-Ning",
        "categoryId": 1
      },
      ...
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 5,
    "totalCount": 100,
    "totalElements": 100
  }
}
```

---

## 2. 用户订单列表接口

**前端调用**：`listUserOrders(params)`
- 文件：`src/api/userLogin.js`

**接口设计**

```
GET /user/order/list
或
POST /user/order/list
```

**请求参数**

```json
{
  "pageNum": 1,
  "pageSize": 5
}
```

**响应格式**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 100,
        "orderSn": "2025112900001",
        "status": 1,
        "totalAmount": 599.98,
        "createTime": "2025-11-29 10:00:00",
        "orderItems": [
          {
            "id": 1001,
            "productId": 1,
            "productName": "李宁N8球拍",
            "productQuantity": 2,
            "productPrice": 299.99
          }
        ]
      },
      ...
    ],
    "total": 20,
    "pageNum": 1,
    "pageSize": 5
  }
}
```

**status 字段含义**
- 0: 待付款
- 1: 待发货
- 2: 已发货
- 3: 已完成
- 4: 已关闭

---

## 3. 创建订单接口

**前端调用**：`createUserOrder(payload)`

**接口设计**

```
POST /user/order/create
```

**请求体**

```json
{
  "productId": 1,
  "quantity": 2,
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "address": "广东省深圳市南山区科技中路1号",
  "note": "请放在门口"
}
```

**响应格式（成功）**

```json
{
  "code": 1,
  "msg": "下单成功",
  "data": {
    "orderId": 100,
    "orderSn": "2025112900001"
  }
}
```

**响应格式（失败 - 库存不足）**

```json
{
  "code": 0,
  "msg": "商品库存不足",
  "data": null
}
```

---

## 4. 用户评论列表接口

**前端调用**：`listUserReviews(params)`

**接口设计**

```
GET /user/review/list
或
POST /user/review/list
```

**请求参数**

```json
{
  "pageNum": 1,
  "pageSize": 5,
  "productName": "球拍名称（可选）"
}
```

**响应格式**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderId": 100,
        "productId": 1,
        "productName": "李宁N8球拍",
        "content": "质量很好，推荐购买",
        "star": 5,
        "createTime": "2025-11-29 10:00:00"
      },
      ...
    ],
    "total": 10,
    "pageNum": 1,
    "pageSize": 5
  }
}
```

---

## 5. 创建评论接口

**前端调用**：`addUserReview(payload)`

**接口设计**

```
POST /user/review/create
```

**请求体**

```json
{
  "orderId": 100,
  "productId": 1,
  "content": "质量很好，推荐购买",
  "star": 5
}
```

**响应格式（成功）**

```json
{
  "code": 1,
  "msg": "评论发布成功",
  "data": {
    "reviewId": 1
  }
}
```

**响应格式（失败）**

```json
{
  "code": 0,
  "msg": "订单不存在或已评论",
  "data": null
}
```

---

## 6. 更新评论接口

**前端调用**：`updateUserReview(payload)`

**接口设计**

```
POST /user/review/update
```

**请求体**

```json
{
  "id": 1,
  "content": "更新后的评论内容"
}
```

**响应格式**

```json
{
  "code": 1,
  "msg": "评论更新成功",
  "data": null
}
```

---

## 7. 删除评论接口

**前端调用**：`deleteUserReview(id)`

**接口设计**

```
DELETE /user/review/{id}
或
POST /user/review/delete
```

**请求体（如果用 POST）**

```json
{
  "id": 1
}
```

**响应格式**

```json
{
  "code": 1,
  "msg": "评论删除成功",
  "data": null
}
```

---

## 8. 品牌列表接口

**前端调用**：`listUserBrands(params)`

**接口设计**

```
GET /user/brand/list
```

**请求参数**

```json
{
  "pageNum": 1,
  "pageSize": 100
}
```

**响应格式**

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "Li-Ning"
    },
    {
      "id": 2,
      "name": "Yonex"
    }
  ]
}
```

---

## 9. 分类列表接口

**前端调用**：`fetchCategoryList(params)`

**接口设计**

```
GET /admin/category/list
```

**请求参数**

```json
{
  "pageNum": 1,
  "pageSize": 100
}
```

**响应格式**

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "羽毛球拍"
    },
    {
      "id": 2,
      "name": "乒乓球拍"
    }
  ]
}
```

---

## 后端实现要点

### 1. 权限控制
- 所有 `/user/*` 接口都需要验证用户登录状态（从 token 获取当前用户 ID）
- 用户只能查看和操作自己的订单和评论

### 2. 分页
- 支持 `pageNum` 和 `pageSize` 参数
- 返回 `total`、`records`、`pageNum`、`pageSize` 等分页信息

### 3. 错误处理
- 成功响应：`code: 1`
- 失败响应：`code: 0`（或其他非 1 的值）
- 务必在 `msg` 字段返回用户友好的错误信息

### 4. 库存检查
- 在创建订单时验证商品库存
- 库存不足时返回 `{"code": 0, "msg": "商品库存不足"}`

### 5. 评论约束
- 用户只能对已购买的商品进行评论
- 同一订单的商品不能重复评论（可选）

### 6. 数据映射
- 前端期望的字段名称（如 `orderSn`、`totalAmount`、`productQuantity`）需要与后端返回一致
- 确保时间格式统一（建议 `YYYY-MM-DD HH:mm:ss`）

---

## 技术建议

### Spring Boot 实现示例（Controller 层伪代码）

```java
@RestController
@RequestMapping("/user")
public class UserController {
    
    @GetMapping("/product/list")
    public Result<PageResult<ProductDTO>> listProducts(
        @RequestParam(defaultValue = "1") int pageNum,
        @RequestParam(defaultValue = "5") int pageSize,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Long brandId
    ) {
        // 1. 获取当前用户ID (从token)
        Long userId = getCurrentUserId();
        
        // 2. 调用 Service 层查询商品列表
        PageResult<ProductDTO> products = productService.listProducts(pageNum, pageSize, name, categoryId, brandId);
        
        // 3. 返回 {code:1, msg:'success', data:{...}}
        return Result.success(products);
    }
    
    @GetMapping("/order/list")
    public Result<PageResult<OrderDTO>> listOrders(
        @RequestParam(defaultValue = "1") int pageNum,
        @RequestParam(defaultValue = "5") int pageSize
    ) {
        Long userId = getCurrentUserId();
        PageResult<OrderDTO> orders = orderService.listUserOrders(userId, pageNum, pageSize);
        return Result.success(orders);
    }
    
    @PostMapping("/order/create")
    public Result<OrderCreateResponse> createOrder(@RequestBody CreateOrderRequest req) {
        Long userId = getCurrentUserId();
        
        // 1. 检查库存
        if (!productService.hasStock(req.getProductId(), req.getQuantity())) {
            return Result.fail("商品库存不足");
        }
        
        // 2. 创建订单
        Order order = orderService.createOrder(userId, req);
        
        // 3. 返回订单信息
        return Result.success(new OrderCreateResponse(order.getId(), order.getOrderSn()));
    }
}
```

---

## 通用响应格式

前端期望的标准响应结构：

```json
{
  "code": 1,
  "msg": "success",
  "data": {}
}
```

其中：
- `code=1` 表示操作成功
- `code=0` 表示业务逻辑失败（如库存不足）
- `code=401` 表示未授权（需要重新登录）
- `code=500` 表示服务器错误
- `msg` 字段中应包含对用户友好的错误描述（中文）
- `data` 字段包含实际返回的数据

