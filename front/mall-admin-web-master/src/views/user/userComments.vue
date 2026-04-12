<template>
  <div class="app-container">
    <el-card shadow="never">
      <el-form :inline="true" :model="commentQuery" size="small" label-width="120px">
        <el-form-item label="商品名">
          <el-input v-model="commentQuery.productName" placeholder="输入商品名字" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getComments">查询</el-button>
        </el-form-item>
      </el-form>

      <div v-if="canCreateOwnReview" style="margin: 10px 0">
        <div style="margin-bottom: 8px; display: flex; gap: 8px; align-items: center">
          <el-select
            v-model="selectedCommentProduct"
            placeholder="请选择已购买的商品进行评价"
            clearable
            @change="onSelectCommentProduct"
            style="min-width: 320px"
          >
            <el-option v-for="item in purchasedProductOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button type="text" @click="loadPurchasedProducts">刷新已购商品</el-button>
        </div>
        <el-input type="textarea" v-model="newCommentText" placeholder="发布评论" />
        <div style="margin-top: 8px; display: flex; gap: 8px; align-items: center">
          <el-rate v-model="newCommentStar" :max="5" show-text />
          <el-button type="primary" style="margin-top: 0" @click="submitNewComment">发布</el-button>
        </div>
      </div>

      <el-table :data="commentList" v-loading="loadingComments" border style="width: 100%">
        <el-table-column label="评论ID" prop="id" width="120" align="center" />
        <el-table-column label="评分" prop="star" width="150" align="center">
          <template slot-scope="scope">
            <el-rate :value="scope.row.star" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column label="内容" prop="content" align="center" />
        <el-table-column label="时间" prop="createTime" width="180" align="center" />
        <el-table-column label="操作" width="220" align="center">
          <template slot-scope="scope">
            <el-button v-if="canUpdateOwnReview" size="mini" @click="openEditComment(scope.row)">修改</el-button>
            <el-button v-if="canDeleteOwnReview" size="mini" type="danger" @click="deleteComment(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleCommentSizeChange"
          @current-change="handleCommentCurrentChange"
          layout="total, sizes, prev, pager, next, jumper"
          :page-size="commentQuery.pageSize"
          :page-sizes="[5, 10, 15]"
          :current-page.sync="commentQuery.pageNum"
          :total="totalComments"
        />
      </div>

      <el-dialog title="修改评论" :visible.sync="editCommentDialogVisible" width="500px">
        <el-form :model="editingComment" label-width="100px">
          <el-form-item label="评论ID">
            <el-input v-model="editingComment.id" disabled />
          </el-form-item>
          <el-form-item label="内容">
            <el-input type="textarea" v-model="editingComment.content" />
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="editCommentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditComment">保存</el-button>
        </span>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { listUserReviews, addUserReview, updateUserReview, deleteUserReview, listUserOrders } from '@/api/userLogin'
import { hasPermission } from '@/utils/permission'

export default {
  name: 'UserComments',
  data() {
    return {
      commentQuery: { productName: null, pageNum: 1, pageSize: 5 },
      commentList: [],
      totalComments: 0,
      loadingComments: false,
      newCommentText: '',
      newCommentStar: 5,
      commentForm: { orderId: null, productId: null },
      selectedCommentProduct: null,
      purchasedProductOptions: [],
      editCommentDialogVisible: false,
      editingComment: { id: null, content: '' }
    }
  },
  created() {
    this.getComments()
    this.loadPurchasedProducts()
  },
  computed: {
    canCreateOwnReview() {
      return hasPermission(this.$store.getters.permissions, 'review:create:own')
    },
    canUpdateOwnReview() {
      return hasPermission(this.$store.getters.permissions, 'review:update:own')
    },
    canDeleteOwnReview() {
      return hasPermission(this.$store.getters.permissions, 'review:delete:own')
    }
  },
  methods: {
    getComments() {
      this.loadingComments = true
      const params = { pageNum: this.commentQuery.pageNum, pageSize: this.commentQuery.pageSize }
      if (this.commentQuery.productName) {
        params.productName = this.commentQuery.productName
      }
      listUserReviews(params)
        .then(res => {
          this.loadingComments = false
          const outer = res && res.data ? res.data : res
          const data = outer && outer.data ? outer.data : outer
          this.commentList = data.records || data.list || []
          this.totalComments = data.total || 0
        })
        .catch(() => {
          this.loadingComments = false
        })
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
      if (!this.canCreateOwnReview) return
      if (!this.commentForm.orderId || !this.commentForm.productId) {
        this.$message({ type: 'warning', message: '请先选择已购买的商品以绑定订单与商品', duration: 2000 })
        return
      }
      const payload = {
        orderId: this.commentForm.orderId,
        productId: this.commentForm.productId,
        content: this.newCommentText,
        star: this.newCommentStar || 5
      }
      addUserReview(payload)
        .then(() => {
          this.$message({ type: 'success', message: '发布成功', duration: 1000 })
          this.newCommentText = ''
          this.selectedCommentProduct = null
          this.commentForm.orderId = null
          this.commentForm.productId = null
          this.getComments()
        })
        .catch(err => {
          console.error('提交评论失败', err)
          this.$message({ type: 'error', message: '提交失败，请稍后重试' })
        })
    },
    loadPurchasedProducts() {
      listUserOrders({ pageNum: 1, pageSize: 100 })
        .then(res => {
          const outer = res && res.data ? res.data : res
          const data = outer && outer.data ? outer.data : outer
          const orders = data.records || data.list || []
          const opts = []
          for (let i = 0; i < orders.length; i++) {
            const ord = orders[i]
            const orderId = ord.id || ord.orderId || ord.orderNo
            const orderSn = ord.orderSn || ''
            if (ord.orderItems && Array.isArray(ord.orderItems)) {
              for (let j = 0; j < ord.orderItems.length; j++) {
                const it = ord.orderItems[j]
                const pid = it.productId || it.id
                const pname = it.productName || it.name || `商品 ${pid}`
                opts.push({ value: `${orderId}__${pid}`, label: `${pname}（订单${orderSn || orderId}）` })
              }
            }
          }
          this.purchasedProductOptions = opts
        })
        .catch(err => {
          console.error('获取订单失败', err)
        })
    },
    onSelectCommentProduct(val) {
      if (!val) {
        this.commentForm.orderId = null
        this.commentForm.productId = null
        return
      }
      const parts = String(val).split('__')
      if (parts.length >= 2) {
        this.commentForm.orderId = parts[0]
        this.commentForm.productId = parts[1]
      }
    },
    openEditComment(row) {
      if (!this.canUpdateOwnReview) return
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
      if (!this.canDeleteOwnReview) return
      deleteUserReview(row.id).then(() => {
        this.$message({ type: 'success', message: '删除成功', duration: 1000 })
        this.getComments()
      })
    }
  }
}
</script>

<style scoped>
.pagination-container {
  margin-top: 15px;
}
</style>
