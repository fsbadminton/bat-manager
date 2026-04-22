<template>
  <div class="app-container">
    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        
          <div>
            <el-form :inline="true" :model="listQuery" size="small" label-width="120px">
              <el-form-item label="关键词">
                <el-input v-model="listQuery.name" placeholder="球拍名称"></el-input>
              </el-form-item>
              <el-form-item label="分类">
                <el-select v-model="listQuery.categoryId" placeholder="请选择分类" clearable style="min-width: 160px">
                  <el-option v-for="item in productCateOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
              <el-form-item label="品牌">
                <el-select v-model="listQuery.brandId" placeholder="请选择品牌" clearable style="min-width: 160px">
                  <el-option v-for="item in brandOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="getProducts">查询</el-button>
                <el-button @click="resetProductFilters">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="productList" v-loading="loadingProducts" border style="width: 100%">
            <el-table-column label="编号" width="100" align="center">
              <template slot-scope="scope">{{ scope.row.productId }}</template>
            </el-table-column>
            <el-table-column label="球拍图片" width="120" align="center">
              <template slot-scope="scope"><img style="height: 80px" :src="scope.row.imageUrl"/></template>
            </el-table-column>
            <el-table-column label="球拍名称" align="center">
              <template slot-scope="scope">
                <p>{{ scope.row.name }}</p>
                <p>品牌：{{ scope.row.brandName }}</p>
              </template>
            </el-table-column>
            <el-table-column label="价格/货号" width="160" align="center">
              <template slot-scope="scope">
                <p>价格：￥{{ scope.row.price }}</p>
                <p>货号：{{ scope.row.productSn }}</p>
              </template>
            </el-table-column>
            <el-table-column label="库存数量" width="120" align="center">
              <template slot-scope="scope">
                {{ scope.row.stock || scope.row.quantity || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template slot-scope="scope">
                <el-button type="primary" size="mini" @click="openOrderDialog(scope.row)">购买</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-container">
            <el-pagination background @size-change="handleProductSizeChange" @current-change="handleProductCurrentChange" layout="total, sizes, prev, pager, next, jumper" :page-size="listQuery.pageSize" :page-sizes="[5,10,15]" :current-page.sync="listQuery.pageNum" :total="totalProducts" />
          </div>
          <el-dialog title="下单购买" :visible.sync="orderDialogVisible" width="500px">
            <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="100px">
              <el-form-item label="球拍ID">
                <el-input v-model="orderForm.productId" disabled/>
              </el-form-item>
              <el-form-item label="数量">
                <el-input v-model.number="orderForm.quantity" type="number" min="1" />
              </el-form-item>
              <el-form-item label="收货人">
                <el-input v-model="orderForm.receiverName" placeholder="请输入收货人姓名" />
              </el-form-item>
              <el-form-item label="手机号" prop="receiverPhone">
                <el-input v-model="orderForm.receiverPhone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="收货地址">
                <el-input v-model="orderForm.address" />
              </el-form-item>
              <el-form-item label="备注">
                <el-input v-model="orderForm.note" />
              </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
              <el-button @click="orderDialogVisible=false">取消</el-button>
              <el-button type="primary" @click="submitOrder">下单</el-button>
            </span>
          </el-dialog>
        
        
          <div>
            <el-table :data="orderList" v-loading="loadingOrders" border style="width: 100%">
              <el-table-column label="订单号" prop="orderSn" width="300" align="center"/>
              <!-- <el-table-column label="球拍" prop="productName" align="center"/>
              <el-table-column label="数量" prop="quantity" width="100" align="center"/> -->
              <!-- 球拍 -->
            <el-table-column label="球拍" width="300">
              <template slot-scope="scope">
                <span>
                  {{ scope.row.orderItems && scope.row.orderItems.length > 0
                    ? scope.row.orderItems[0].productName
                    : '无' }}
                </span>
              </template>
            </el-table-column>

  <!-- 数量 -->
  <el-table-column label="数量" width="200">
    <template slot-scope="scope">
      <span>
        {{ scope.row.orderItems && scope.row.orderItems.length > 0
          ? scope.row.orderItems[0].productQuantity
          : '0' }}
      </span>
    </template>
  </el-table-column>
              <el-table-column label="金额" prop="totalAmount" width="300" align="center"/>
              <el-table-column label="状态" width="300" align="center">
                <template slot-scope="scope">
                  {{ scope.row.status | formatOrderStatus }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="300" align="center">
                <template slot-scope="scope">
                  <el-button size="mini" @click="openEditOrder(scope.row)" v-if="scope.row.status === 1">修改</el-button>
                  <el-button size="mini" @click="openCommentDialog(scope.row)">评价</el-button>
                </template>
              </el-table-column>
              
            </el-table>
            <div class="pagination-container">
              <el-pagination background @size-change="handleOrderSizeChange" @current-change="handleOrderCurrentChange" layout="total, sizes, prev, pager, next, jumper" :page-size="orderQuery.pageSize" :page-sizes="[5,10,15]" :current-page.sync="orderQuery.pageNum" :total="totalOrders" />
            </div>
            <el-dialog title="修改订单" :visible.sync="orderEditDialogVisible" width="500px">
                <el-form ref="orderEditFormRef" :model="orderEditForm" :rules="orderRules" label-width="100px">
                  <el-form-item label="订单号">
                  <el-input v-model="orderEditForm.orderSn" disabled/>
                  </el-form-item>
                  <el-form-item label="球拍名称">
                    <el-input v-model="orderEditForm.productName" disabled/>
                  </el-form-item>
                  <el-form-item label="数量">
                  <el-input v-model.number="orderEditForm.quantity" type="number" min="1" />
                  </el-form-item>
                  <el-form-item label="收货人">
                  <el-input v-model="orderEditForm.receiverName" placeholder="请输入收货人姓名" />
                  </el-form-item>
                <el-form-item label="手机号" prop="receiverPhone">
                  <el-input v-model="orderEditForm.receiverPhone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="收货地址">
                  <el-input v-model="orderEditForm.address" />
                </el-form-item>
              </el-form>
              <span slot="footer" class="dialog-footer">
                <el-button @click="orderEditDialogVisible=false">取消</el-button>
                <el-button type="primary" @click="submitEditOrder">保存</el-button>
              </span>
            </el-dialog>
          </div>
        
        
          <div>
            <el-form :inline="true" :model="commentQuery" size="small" label-width="120px">
              <el-form-item label="球拍名">
                <el-input v-model="commentQuery.productName" placeholder="输入球拍名字"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="getComments">查询</el-button>
              </el-form-item>
            </el-form>
            <div style="margin: 10px 0">
              <div style="margin-bottom:8px; display:flex; gap:8px; align-items:center;">
                <el-select v-model="selectedCommentProduct" placeholder="请选择已购买的商品以绑定订单/商品" clearable @change="onSelectCommentProduct" style="min-width: 320px">
                  <el-option v-for="item in purchasedProductOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
                <el-button type="text" @click="refreshPurchasedProducts">刷新已购商品</el-button>
              </div>
              <el-input type="textarea" v-model="newCommentText" placeholder="发布评论" />
              <div style="margin-top:8px; display:flex; gap:8px; align-items:center;">
                <el-rate v-model="newCommentStar" :max="5" show-text></el-rate>
                <el-button type="primary" @click="submitNewComment">发布</el-button>
              </div>
            </div>
            <el-table :data="commentList" v-loading="loadingComments" border style="width:100%">
              <el-table-column label="评分" prop="star" width="150" align="center">
                <template slot-scope="scope">
                  <el-rate
                    :value="scope.row.star"
                    disabled
                    show-score
                  ></el-rate>
                </template>
              </el-table-column>
              <el-table-column label="内容" prop="content" align="center"/>
              <el-table-column label="时间" prop="createTime" width="180" align="center"/>
              <el-table-column label="操作" width="220" align="center">
                <template slot-scope="scope">
                  <el-button size="mini" @click="openEditComment(scope.row)">修改</el-button>
                  <el-button size="mini" type="danger" @click="deleteComment(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination-container">
              <el-pagination background @size-change="handleCommentSizeChange" @current-change="handleCommentCurrentChange" layout="total, sizes, prev, pager, next, jumper" :page-size="commentQuery.pageSize" :page-sizes="[5,10,15]" :current-page.sync="commentQuery.pageNum" :total="totalComments" />
            </div>
            <el-dialog title="修改评论" :visible.sync="editCommentDialogVisible" width="500px">
              <el-form :model="editingComment" label-width="100px">
                <el-form-item label="内容">
                  <el-input type="textarea" v-model="editingComment.content" />
                </el-form-item>
              </el-form>
              <span slot="footer" class="dialog-footer">
                <el-button @click="editCommentDialogVisible=false">取消</el-button>
                <el-button type="primary" @click="submitEditComment">保存</el-button>
              </span>
            </el-dialog>
          </div>
        
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'
import { listUserProducts, listUserBrands, createUserOrder, listUserOrders, getUserOrderDetail, updateUserOrder, addUserReview, updateUserReview, deleteUserReview, listUserReviews } from '@/api/userLogin'
import { fetchCategoryList } from '@/api/productCate'

export default {
  name: 'UserDashboard',
  data() {
    return {
      activeTab: 'products',
      listQuery: { keyword: null, pageNum: 1, pageSize: 5, productCategoryId: null, brandId: null },
      productList: [],
      totalProducts: 0,
      loadingProducts: false,
      brandOptions: [],
      productCateOptions: [],
      orderDialogVisible: false,
      orderForm: { productId: null, quantity: 1, receiverName: '', receiverPhone: '', address: '',note:'' },
      orderRules: {
        receiverPhone: [
          { validator: (rule, value, callback) => {
              if (!value) return callback(new Error('请输入手机号'));
              const v = String(value).trim();
              if (!/^\d{11}$/.test(v)) return callback(new Error('手机号必须为11位数字'));
              callback();
            }, trigger: 'blur' }
        ]
      },
      orderList: [],
      orderQuery: { pageNum: 1, pageSize: 5 },
      totalOrders: 0,
      loadingOrders: false,
      orderEditDialogVisible: false,
      orderEditForm: { id: null, orderSn: '', productName: '', quantity: 1, receiverName: '', receiverPhone: '', address: '', note:'' },
      commentQuery: { productName: null, pageNum: 1, pageSize: 5 },
      commentList: [],
      commentDialogVisible: false,
      newCommentText: '',
      newCommentStar: 5,
      commentForm: { orderId: null, productId: null },
      selectedCommentProduct: null,
      purchasedProductOptions: [],
      totalComments: 0,
      loadingComments: false,
      newCommentText: '',
      editCommentDialogVisible: false,
      editingComment: { id: null, content: '' }
    }
  },
  created() {
    this.syncTabFromRoute()
    this.getProducts()
    this.loadBrandOptions()
    this.loadCategoryOptions()
    this.getOrders()
    this.getComments() // 页面加载时自动查全部评论
  },
  methods: {
    syncTabFromRoute() {
      const name = (this.$route && this.$route.name) || ''
      if (name === 'userProducts') this.activeTab = 'products'
      else if (name === 'userOrders') this.activeTab = 'orders'
      else if (name === 'userComments') this.activeTab = 'comments'
      else this.activeTab = 'products'
    },
    openCommentDialog(row) {
    const item = row.orderItems[0]; // 单商品订单
    this.commentForm.orderId = row.id;
    this.commentForm.productId = item.productId;
    // 同步下拉选择值（如果存在对应选项）
    const val = `${row.id}__${item.productId}`;
    const found = this.purchasedProductOptions.find(p => p.value === val);
    if (found) this.selectedCommentProduct = val;

    this.commentDialogVisible = true;
  },
    getProducts() {
      this.loadingProducts = true
      const mapped = {
    pageNum: this.listQuery.pageNum || 1,
    pageSize: this.listQuery.pageSize || 5,
    name: this.listQuery.name,            // 球拍名称关键词
    categoryId: this.listQuery.categoryId,
    brandId: this.listQuery.brandId
  };

  listUserProducts(mapped)
    .then(res => {
      this.loadingProducts = false;
      const outer = (res && res.data) ? res.data : res;
      const data = (outer && outer.data) ? outer.data : outer;

      this.productList = data.records || data.list || data.items || [];
      this.totalProducts = data.total || data.totalCount || data.totalElements || 0;
    })
    .catch(() => { this.loadingProducts = false; });
    },
    resetProductFilters() {
      this.listQuery = { keyword: null, pageNum: 1, pageSize: 5, productCategoryId: null, brandId: null }
      this.getProducts()
    },
    handleProductSizeChange(val) {
      this.listQuery.pageSize = val
      this.listQuery.pageNum = 1
      this.getProducts()
    },
    handleProductCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getProducts()
    },
    loadBrandOptions() {
      listUserBrands({ pageNum: 1, pageSize: 100 }).then(response => {
        let list = []
        const outer = response && response.data ? response.data : response
        const data = outer && outer.data ? outer.data : outer
        if (Array.isArray(data)) list = data
        else list = data.records || data.list || []
        this.brandOptions = []
        for (let i = 0; i < list.length; i++) {
          const b = list[i]
          this.brandOptions.push({ label: b.name || b.brandName || b.label, value: b.id || b.value || b.name })
        }
      })
    },
    loadCategoryOptions() {
      fetchCategoryList({ pageNum: 1, pageSize: 100 }).then(response => {
        let list = []
        if (response && response.data) {
          if (Array.isArray(response.data)) list = response.data
          else list = response.data.records || response.data.list || []
        } else if (Array.isArray(response)) list = response
        this.productCateOptions = []
        for (let i = 0; i < list.length; i++) {
          const item = list[i]
          if (typeof item === 'number' || typeof item === 'string') this.productCateOptions.push({ label: `分类${item}`, value: Number(item) || item })
          else this.productCateOptions.push({ label: item.name || item.label, value: item.id || item.value })
        }
      })
    },
    openOrderDialog(row) {
      this.orderForm = { productId: row.productId || row.id, quantity: 1, receiverName: '', receiverPhone: '', address: '',note:'' }
      this.orderDialogVisible = true
    },
    submitOrder() {
      if (this.$refs.orderFormRef) {
        this.$refs.orderFormRef.validate(valid => {
          if (!valid) return;
          this._submitOrderDo()
        })
      } else {
        this._submitOrderDo()
      }
    },
    _submitOrderDo() {
      const payload = {
        productId: this.orderForm.productId,
        quantity: this.orderForm.quantity,
        receiverName: this.orderForm.receiverName,
        receiverPhone: this.orderForm.receiverPhone,
        address: this.orderForm.address,
        note:this.orderForm.note
      }
      createUserOrder(payload).then(res => {
        if(res.code !== 1){
          this.$message({ type: 'warning', message: res.msg || res.message || '下单失败', duration: 4000 });
          return;
        }
        this.$message({ type: 'success', message: '下单成功', duration: 1000 })
        this.orderDialogVisible = false
        this.getOrders()
        this.getProducts()
      }).catch(err => {
        let errorMessage = '服务器内部错误(500)，请检查后端日志';
        try {
          if(err && err.msg) {
            errorMessage = err.msg;
          } else if(err && err.message) {
            errorMessage = err.message;
          } else if(err && typeof err === 'string') {
            errorMessage = err;
          }
        } catch(e) {
          console.error('解析错误信息失败', e);
        }
        this.$message({ type: 'error', message: errorMessage, duration: 4000 });
      })
    },
    getOrders() {
      this.loadingOrders = true
      const params = { pageNum: this.orderQuery.pageNum, pageSize: this.orderQuery.pageSize }
      listUserOrders(params).then(res => {
        this.loadingOrders = false
        const outer = res && res.data ? res.data : res
        const data = outer && outer.data ? outer.data : outer
        this.orderList = data.records || data.list || []
        this.totalOrders = data.total || 0
        // 构建已购商品选项用于评论绑定
        this.buildPurchasedProductOptions();
      }).catch(() => { this.loadingOrders = false })
    },
    handleOrderSizeChange(val) {
      this.orderQuery.pageSize = val
      this.orderQuery.pageNum = 1
      this.getOrders()
    },
    handleOrderCurrentChange(val) {
      this.orderQuery.pageNum = val
      this.getOrders()
    },
    openEditOrder(order) {
      this.orderEditForm = {
        id: order.id,
        orderSn: order.orderSn || '',
        productName: order.orderItems && order.orderItems.length > 0 ? order.orderItems[0].productName : '未命名商品',
        quantity: order.orderItems && order.orderItems.length > 0 ? order.orderItems[0].productQuantity : 1,
        receiverName: order.receiverName || '',
        receiverPhone: order.receiverPhone || '',
        address: order.address,
        note:order.note
      }
      this.orderEditDialogVisible = true
    },
    submitEditOrder() {
      if (this.$refs.orderEditFormRef) {
        this.$refs.orderEditFormRef.validate(valid => {
          if (!valid) return;
          this._submitEditOrderDo()
        })
      } else {
        this._submitEditOrderDo()
      }
    },
    _submitEditOrderDo() {
      const payload = {
        id: this.orderEditForm.id,
        quantity: this.orderEditForm.quantity,
        receiverName: this.orderEditForm.receiverName,
        receiverPhone: this.orderEditForm.receiverPhone,
        address: this.orderEditForm.address,
        note:this.orderEditForm.note
      }
      updateUserOrder(payload).then(() => {
        this.$message({ type: 'success', message: '修改成功', duration: 1000 })
        this.orderEditDialogVisible = false
        this.getOrders()
      })
    },
    getComments() {
      this.loadingComments = true
      const params = { pageNum: this.commentQuery.pageNum, pageSize: this.commentQuery.pageSize };
      if (this.commentQuery.productName) {
        params.productName = this.commentQuery.productName;
      }
      listUserReviews(params).then(res => {
        this.loadingComments = false
        const outer = res && res.data ? res.data : res
        const data = outer && outer.data ? outer.data : outer
        this.commentList = data.records || data.list || []
        this.totalComments = data.total || 0
      }).catch(() => { this.loadingComments = false })
    },
    buildPurchasedProductOptions() {
      const opts = [];
      if (!this.orderList || this.orderList.length === 0) return (this.purchasedProductOptions = opts);
      for (let i = 0; i < this.orderList.length; i++) {
        const ord = this.orderList[i];
        const orderId = ord.id || ord.orderId || ord.orderNo;
        if (ord.orderItems && Array.isArray(ord.orderItems)) {
          for (let j = 0; j < ord.orderItems.length; j++) {
            const it = ord.orderItems[j];
            const pid = it.productId || it.id || it.productId;
            const pname = it.productName || it.name || `商品 ${pid}`;
            const label = `${pname} （订单:${ord.orderSn || orderId}）`;
            opts.push({ value: `${orderId}__${pid}`, label });
          }
        }
      }
      this.purchasedProductOptions = opts;
    },
    onSelectCommentProduct(val) {
      if (!val) {
        this.commentForm.orderId = null;
        this.commentForm.productId = null;
        return;
      }
      const parts = (val || '').toString().split('__');
      if (parts.length >= 2) {
        this.commentForm.orderId = parts[0];
        this.commentForm.productId = parts[1];
      }
    },
    refreshPurchasedProducts() {
      // 重新拉取订单并重建选项
      this.getOrders();
    },
    handleCommentSizeChange(val) {
      this.commentQuery.pageSize = val
      this.commentQuery.pageNum = 1
      this.getComments()
    },
    handleCommentCurrentChange(val) {
      this.commentQuery.pageNum = val
      this.getComments()
    },
    submitNewComment() {
      // 验证：必须有 orderId 与 productId
      if (!this.commentForm.orderId || !this.commentForm.productId) {
        this.$message({ type: 'warning', message: '请先选择或从订单进入“评价”以绑定订单与商品。', duration: 2000 })
        return;
      }
      const payload = {
        orderId: this.commentForm.orderId,
        productId: this.commentForm.productId,
        content: this.newCommentText,
        star: this.newCommentStar || 5
      }
      addUserReview(payload).then(() => {
        this.$message({ type: 'success', message: '发布成功', duration: 1000 })
        this.newCommentText = ''
        this.selectedCommentProduct = null
        this.commentForm.orderId = null
        this.commentForm.productId = null
        this.getComments()
      }).catch(err => {
        console.error('发布评论失败:', err);
        this.$message({ type: 'error', message: '发布失败，请稍后重试', duration: 2000 })
      })
    },
    openEditComment(row) {
      this.editingComment = { id: row.id, content: row.content }
      this.editCommentDialogVisible = true
    },
    submitEditComment() {
      const payload = { id: this.editingComment.id, content: this.editingComment.content }
      updateUserReview(payload).then(() => {
        this.$message({ type: 'success', message: '修改成功', duration: 1000 })
        this.editCommentDialogVisible = false
        this.getComments()
      })
    },
    deleteComment(row) {
      deleteUserReview(row.id).then(() => {
        this.$message({ type: 'success', message: '删除成功', duration: 1000 })
        this.getComments()
      })
    }
  }
  ,
  filters: {
    formatOrderStatus(value) {
      const statusMap = {
        0: '待付款',
        1: '待发货',
        2: '已发货',
        3: '已完成',
        4: '已关闭',
        5: '无效订单'
      }
      return statusMap[value] || '未知状态'
    }
  },
  watch: {
    '$route'(to) {
      this.syncTabFromRoute()
      if (this.activeTab === 'products') this.getProducts()
      else if (this.activeTab === 'orders') this.getOrders()
      else if (this.activeTab === 'comments') this.getComments()
    }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 15px }
</style>
