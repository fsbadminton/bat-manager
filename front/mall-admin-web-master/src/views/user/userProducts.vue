<template>
  <div class="app-container">
    <el-card shadow="never">
      <div>
        <el-form :inline="true" :model="listQuery" size="small" label-width="120px">
          <el-form-item label="关键字">
            <el-input v-model="listQuery.name" placeholder="商品名称" />
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

      <el-alert v-if="productLoadError" :title="productLoadError" type="error" :closable="false" show-icon />
      <el-table
        :data="productList"
        v-loading="loadingProducts"
        :empty-text="productLoadError ? '加载失败' : '暂无球拍'"
        border
        style="width: 100%"
      >
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.productId }}</template>
        </el-table-column>
        <el-table-column label="商品图片" width="120" align="center">
          <template slot-scope="scope"><img style="height: 80px" :src="scope.row.imageUrl" /></template>
        </el-table-column>
        <el-table-column label="商品名称" align="center">
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
            <el-button v-if="canCreateOrder" type="primary" size="mini" @click="openOrderDialog(scope.row)">购买</el-button>
            <el-tag v-else type="info" size="mini">仅查看</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleProductSizeChange"
          @current-change="handleProductCurrentChange"
          layout="total, sizes, prev, pager, next, jumper"
          :page-size="listQuery.pageSize"
          :page-sizes="[5, 10, 15]"
          :current-page.sync="listQuery.pageNum"
          :total="totalProducts"
        />
      </div>

      <el-dialog title="下单购买" :visible.sync="orderDialogVisible" width="500px">
        <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="100px">
          <el-form-item label="球拍名称">
            <el-input v-model="orderForm.productName" disabled />
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
          <el-form-item label="优惠券">
            <el-select v-model="orderForm.couponHistoryId" clearable placeholder="不使用优惠券" style="width: 100%">
              <el-option v-for="item in filteredCouponOptions" :key="item.id" :label="buildCouponLabel(item)" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="订单预估">
            <div>原价：{{ currentOrderOriginalAmount.toFixed(2) }} 元</div>
            <div>优惠：{{ selectedCouponAmount.toFixed(2) }} 元</div>
            <div>应付：{{ previewPayAmount.toFixed(2) }} 元</div>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="orderDialogVisible = false">取消</el-button>
          <el-button :disabled="!canCreateOrder" type="primary" @click="submitOrder">下单</el-button>
        </span>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { listUserProducts, listUserBrands, createUserOrder } from '@/api/userLogin'
import { listMyCoupons } from '@/api/userCoupon'
import { hasPermission } from '@/utils/permission'

export default {
  name: 'UserProducts',
  data() {
    return {
      listQuery: { name: null, pageNum: 1, pageSize: 5, categoryId: null, brandId: null },
      productList: [],
      totalProducts: 0,
      loadingProducts: false,
      productLoadError: '',
      brandOptions: [],
      productCateOptions: [],
      orderDialogVisible: false,
      orderForm: { productId: null, productName: '', productCategoryId: null, productPrice: 0, quantity: 1, receiverName: '', receiverPhone: '', address: '', note: '', couponHistoryId: null },
      myCouponList: [],
      orderRules: {
        receiverPhone: [
          {
            validator: (rule, value, callback) => {
              if (!value) return callback(new Error('请输入手机号'))
              const v = String(value).trim()
              if (!/^\d{11}$/.test(v)) return callback(new Error('手机号码必须为11位数字'))
              callback()
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  created() {
    this.getProducts()
    this.loadBrandOptions()
  },
  computed: {
    isUserAccount() {
      const roles = this.$store.getters.roles || []
      return roles.some(r => String(r).toUpperCase() === 'USER')
    },
    canCreateOrder() {
      return hasPermission(this.$store.getters.permissions, 'order:create:own') || this.isUserAccount
    },
    currentOrderOriginalAmount() {
      const price = Number(this.orderForm.productPrice || 0)
      const quantity = Number(this.orderForm.quantity || 0)
      return price * quantity
    },
    filteredCouponOptions() {
      const amount = this.currentOrderOriginalAmount
      const productId = this.orderForm.productId
      const categoryId = this.orderForm.productCategoryId
      const now = new Date().getTime()
      return this.myCouponList.filter(item => {
        if (item.useStatus !== 0) return false
        if (item.startTime && new Date(item.startTime).getTime() > now) return false
        if (item.endTime && new Date(item.endTime).getTime() < now) return false
        if (Number(amount) < Number(item.minPoint || 0)) return false
        if (item.useType === 2) {
          const list = item.productRelationList || []
          return list.some(rel => Number(rel.productId) === Number(productId))
        }
        if (item.useType === 1) {
          const list = item.productCategoryRelationList || []
          return list.some(rel => Number(rel.productCategoryId) === Number(categoryId))
        }
        return true
      })
    },
    selectedCouponAmount() {
      const selected = this.myCouponList.find(item => Number(item.id) === Number(this.orderForm.couponHistoryId))
      if (!selected) return 0
      return Math.min(Number(selected.amount || 0), this.currentOrderOriginalAmount)
    },
    previewPayAmount() {
      return Math.max(this.currentOrderOriginalAmount - this.selectedCouponAmount, 0)
    }
  },
  methods: {
    getProducts() {
      this.loadingProducts = true
      this.productLoadError = ''
      const mapped = {
        pageNum: this.listQuery.pageNum || 1,
        pageSize: this.listQuery.pageSize || 5,
        name: this.listQuery.name,
        categoryId: this.listQuery.categoryId,
        brandId: this.listQuery.brandId
      }
      listUserProducts(mapped)
        .then(res => {
          this.loadingProducts = false
          const outer = res && res.data ? res.data : res
          const data = outer && outer.data ? outer.data : outer
          this.productList = data.records || data.list || data.items || []
          // 用户端不再调用管理员分类接口，避免弹出“重新登录”提示
          this.rebuildCategoryOptions(this.productList)
          this.totalProducts = data.total || data.totalCount || data.totalElements || 0
        })
        .catch(() => {
          this.loadingProducts = false
          this.productList = []
          this.totalProducts = 0
          this.productLoadError = '球拍列表加载失败，请稍后重试'
        })
    },
    resetProductFilters() {
      this.listQuery = { name: null, pageNum: 1, pageSize: 5, categoryId: null, brandId: null }
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
      listUserBrands({ pageNum: 1, pageSize: 100 })
        .then(response => {
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
        .catch(() => {
          this.brandOptions = []
        })
    },
    rebuildCategoryOptions(products) {
      const list = Array.isArray(products) ? products : []
      const map = new Map()
      for (let i = 0; i < list.length; i++) {
        const p = list[i] || {}
        const value = p.categoryId || p.category || p.categoryName
        if (value === undefined || value === null || value === '') continue
        const label = p.categoryName || `分类${value}`
        if (!map.has(String(value))) {
          map.set(String(value), { label, value })
        }
      }
      this.productCateOptions = Array.from(map.values())
    },
    openOrderDialog(row) {
      if (!this.canCreateOrder) return
      this.orderForm = {
        productId: row.productId || row.id,
        productName: row.name || row.productName || '',
        productCategoryId: row.category || row.categoryId || null,
        productPrice: row.price || 0,
        quantity: 1,
        receiverName: '',
        receiverPhone: '',
        address: '',
        note: '',
        couponHistoryId: null
      }
      this.loadMyCoupons()
      this.orderDialogVisible = true
    },
    loadMyCoupons() {
      listMyCoupons({ pageNum: 1, pageSize: 100, useStatus: 0 }).then(res => {
        const data = res && res.data ? res.data : {}
        this.myCouponList = data.records || data.list || []
      }).catch(() => {
        this.myCouponList = []
      })
    },
    buildCouponLabel(item) {
      return `${item.couponName} - 满${item.minPoint || 0}减${item.amount || 0}`
    },
    submitOrder() {
      if (!this.canCreateOrder) {
        this.$message({ type: 'warning', message: '当前账号没有下单权限' })
        return
      }
      if (this.$refs.orderFormRef) {
        this.$refs.orderFormRef.validate(valid => {
          if (!valid) return
          this._doSubmitOrder()
        })
      } else {
        this._doSubmitOrder()
      }
    },
    _doSubmitOrder() {
      const payload = {
        productId: this.orderForm.productId,
        quantity: this.orderForm.quantity,
        receiverName: this.orderForm.receiverName,
        receiverPhone: this.orderForm.receiverPhone,
        address: this.orderForm.address,
        note: this.orderForm.note,
        couponHistoryId: this.orderForm.couponHistoryId
      }
      createUserOrder(payload)
        .then(res => {
          if (res.code !== 1) {
            this.$message({ type: 'warning', message: res.msg || res.message || '下单失败', duration: 4000 })
            return
          }
          this.$message({ type: 'success', message: '下单成功', duration: 1000 })
          this.orderDialogVisible = false
          this.getProducts()
        })
        .catch(err => {
          let errorMessage = '服务器内部错误(500)，请检查后端日志'
          try {
            if (err && err.msg) {
              errorMessage = err.msg
            } else if (err && err.message) {
              errorMessage = err.message
            } else if (err && typeof err === 'string') {
              errorMessage = err
            }
          } catch (e) {
            console.error('解析错误信息失败', e)
          }
          this.$message({ type: 'error', message: errorMessage, duration: 4000 })
        })
    }
  }
}
</script>

<style scoped>
.pagination-container {
  margin-top: 15px;
}

.el-alert {
  margin-bottom: 12px;
}
</style>
