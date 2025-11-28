<template>
  <div class="app-container">
    <el-card shadow="never">
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
        <el-form :model="orderForm" label-width="100px">
          <el-form-item label="球拍ID">
            <el-input v-model="orderForm.productId" disabled/>
          </el-form-item>
          <el-form-item label="数量">
            <el-input v-model.number="orderForm.quantity" type="number" min="1" />
          </el-form-item>
          <el-form-item label="收货人">
            <el-input v-model="orderForm.receiverName" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="手机号">
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
    </el-card>
  </div>
</template>

<script>
import { listUserProducts, listUserBrands, createUserOrder } from '@/api/userLogin'
import { fetchCategoryList } from '@/api/productCate'

export default {
  name: 'UserProducts',
  data() {
    return {
      listQuery: { name: null, pageNum: 1, pageSize: 5, categoryId: null, brandId: null },
      productList: [],
      totalProducts: 0,
      loadingProducts: false,
      brandOptions: [],
      productCateOptions: [],
      orderDialogVisible: false,
      orderForm: { productId: null, quantity: 1, receiverName: '', receiverPhone: '', address: '', note: '' }
    }
  },
  created() {
    this.getProducts()
    this.loadBrandOptions()
    this.loadCategoryOptions()
  },
  methods: {
    getProducts() {
      this.loadingProducts = true
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
          const outer = (res && res.data) ? res.data : res
          const data = (outer && outer.data) ? outer.data : outer
          this.productList = data.records || data.list || data.items || []
          this.totalProducts = data.total || data.totalCount || data.totalElements || 0
        })
        .catch(() => { this.loadingProducts = false })
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
      this.orderForm = { productId: row.productId || row.id, quantity: 1, receiverName: '', receiverPhone: '', address: '', note: '' }
      this.orderDialogVisible = true
    },
    submitOrder() {
      const payload = {
        productId: this.orderForm.productId,
        quantity: this.orderForm.quantity,
        receiverName: this.orderForm.receiverName,
        receiverPhone: this.orderForm.receiverPhone,
        address: this.orderForm.address,
        note: this.orderForm.note
      }
      createUserOrder(payload)
    .then(res => {
      // 判断后端自定义 code
      if(res.code !== 1){ // 假设 code=1表示成功
        this.$message({ type: 'warning', message: res.msg || res.message || '下单失败', duration: 4000 });
        return;
      }
      this.$message({ type: 'success', message: '下单成功', duration: 1000 });
      this.orderDialogVisible = false;
      this.getProducts(); // 刷新库存
    })
    .catch(err => {
      // 网络或服务器异常，或被拦截器转发的业务错误
      let errorMessage = '服务器内部错误(500)，请检查后端日志';
      try {
        // err 可能是拦截器传来的完整响应对象 {code:0, msg:'...'}
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
    }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 15px }
</style>