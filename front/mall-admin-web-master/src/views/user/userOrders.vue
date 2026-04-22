<template>
  <div class="app-container">
    <el-card shadow="never">
      <el-table :data="orderList" v-loading="loadingOrders" border style="width: 100%">
        <el-table-column label="订单号" prop="orderSn" width="300" align="center" />
        <el-table-column label="商品" width="300">
          <template slot-scope="scope">
            <span>
              {{ scope.row.orderItems && scope.row.orderItems.length > 0 ? scope.row.orderItems[0].productName : '无' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="200">
          <template slot-scope="scope">
            <span>
              {{ scope.row.orderItems && scope.row.orderItems.length > 0 ? scope.row.orderItems[0].productQuantity : '0' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="金额" prop="totalAmount" width="300" align="center" />
        <el-table-column label="状态" width="300" align="center">
          <template slot-scope="scope">
            <span>{{ getOrderStatusText(scope.row) }}</span>
            <el-tag v-if="scope.row.returnApplyStatus !== null && scope.row.returnApplyStatus !== undefined" size="mini" style="margin-left: 8px" :type="getReturnTagType(scope.row.returnApplyStatus)">
              {{ getReturnStatusText(scope.row.returnApplyStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="400" align="center">
          <template slot-scope="scope">
            <el-button v-if="canEditOrder(scope.row)" size="mini" @click="openEditOrder(scope.row)">修改</el-button>
            <el-button v-if="canViewLogistics(scope.row)" size="mini" @click="openLogisticsDialog(scope.row)">查看物流</el-button>
            <el-button v-if="canConfirmReceive(scope.row)" size="mini" type="success" @click="confirmReceive(scope.row)">确认收货</el-button>
            <el-button v-if="canCommentOrder(scope.row)" size="mini" @click="openCommentDialog(scope.row)">评价</el-button>
            <el-button v-if="canReturnOrder(scope.row)" size="mini" type="danger" @click="openReturnDialog(scope.row)">退货</el-button>
            <el-tag v-if="!hasAnyOrderAction(scope.row)" size="mini" type="info">当前无需操作</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleOrderSizeChange"
          @current-change="handleOrderCurrentChange"
          layout="total, sizes, prev, pager, next, jumper"
          :page-size="orderQuery.pageSize"
          :page-sizes="[5, 10, 15]"
          :current-page.sync="orderQuery.pageNum"
          :total="totalOrders"
        />
      </div>

      <el-dialog title="退货申请" :visible.sync="returnDialogVisible" width="500px">
        <el-form :model="returnForm" label-width="100px">
          <el-form-item label="退货原因">
            <el-select v-model="returnForm.reason" placeholder="请选择退货原因" style="width: 100%">
              <el-option v-for="item in returnReasonList" :key="item.id" :label="item.name" :value="item.name" />
              <el-option label="其他（请填写）" value="__custom__" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="returnForm.reason === '__custom__'" label="自定义原因">
            <el-input v-model="returnForm.customReason" placeholder="请输入自定义退货原因" />
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="returnDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReturnApply">提交</el-button>
        </span>
      </el-dialog>

      <el-dialog title="修改订单" :visible.sync="orderEditDialogVisible" width="500px">
        <el-form ref="orderEditFormRef" :model="orderEditForm" :rules="orderRules" label-width="100px">
          <el-form-item label="订单号">
            <el-input v-model="orderEditForm.orderSn" disabled />
          </el-form-item>
          <el-form-item label="球拍名称">
            <el-input v-model="orderEditForm.productName" disabled />
          </el-form-item>
          <el-form-item label="数量">
            <el-input v-model.number="orderEditForm.quantity" type="number" min="1" />
          </el-form-item>
          <el-form-item label="收货人">
            <el-input v-model="orderEditForm.receiverName" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="手机号码" prop="receiverPhone">
            <el-input v-model="orderEditForm.receiverPhone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="收货地址">
            <el-input v-model="orderEditForm.address" />
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="orderEditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditOrder">保存</el-button>
        </span>
      </el-dialog>
      <logistics-dialog v-model="logisticsDialogVisible" :records="logisticsRecords" />
    </el-card>
  </div>
</template>

<script>
import { listUserOrders, updateUserOrder, confirmUserOrderReceive } from '@/api/userLogin'
import { createUserReturnApply, listUserReturnApplies } from '@/api/userReturnApply'
import { listUserReturnReasons } from '@/api/userReturnReason'
import { hasPermission } from '@/utils/permission'
import LogisticsDialog from '@/views/oms/order/components/logisticsDialog'

export default {
  name: 'UserOrders',
  components: { LogisticsDialog },
  data() {
    return {
      orderList: [],
      orderQuery: { pageNum: 1, pageSize: 5 },
      totalOrders: 0,
      loadingOrders: false,
      orderEditDialogVisible: false,
      orderEditForm: { id: null, orderSn: '', productName: '', quantity: 1, receiverName: '', receiverPhone: '', address: '' },
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
      },
      returnDialogVisible: false,
      returnForm: { orderId: null, reason: '', customReason: '' },
      returnReasonList: [],
      logisticsDialogVisible: false,
      logisticsRecords: []
    }
  },
  mounted() {
    this.loadReturnReasons()
  },
  created() {
    this.getOrders()
  },
  computed: {
    canUpdateOwnOrder() {
      return hasPermission(this.$store.getters.permissions, 'order:update:own')
    },
    canCreateOwnReview() {
      return hasPermission(this.$store.getters.permissions, 'review:create:own')
    },
    canApplyRefund() {
      return hasPermission(this.$store.getters.permissions, 'refund:apply:own')
    }
  },
  methods: {
    loadReturnReasons() {
      listUserReturnReasons()
        .then(res => {
          const data = res && res.data ? res.data : res
          if (Array.isArray(data)) {
            this.returnReasonList = data
          } else if (Array.isArray(data.data)) {
            this.returnReasonList = data.data
          } else {
            this.returnReasonList = data.list || data.records || []
          }
        })
        .catch(err => {
          console.warn('获取退货原因失败（已静默）', err)
          this.returnReasonList = []
        })
    },
    loadUserReturnMap() {
      listUserReturnApplies({ pageNum: 1, pageSize: 1000 })
        .then(res => {
          const outer = res && res.data ? res.data : res
          const data = outer && outer.data ? outer.data : outer
          const list = data.records || data.list || data || []
          for (let i = 0; i < list.length; i++) {
            const item = list[i]
            const order = this.orderList.find(orderItem => orderItem.id === item.orderId)
            if (order) {
              order.returnApplyStatus = item.status
              order.returnApplyId = item.id
            }
          }
        })
        .catch(err => {
          console.warn('加载用户退货映射失败（已静默）', err)
        })
    },
    openReturnDialog(order) {
      if (!this.canApplyRefund) return
      this.returnForm = { orderId: order.id, reason: '', customReason: '' }
      this.returnDialogVisible = true
    },
    submitReturnApply() {
      const reason = this.returnForm.reason === '__custom__' ? this.returnForm.customReason : this.returnForm.reason
      if (!reason) {
        this.$message({ type: 'warning', message: '请选择或输入退货原因' })
        return
      }
      createUserReturnApply({ orderId: this.returnForm.orderId, reason }).then(() => {
        this.$message({ type: 'success', message: '退货申请已提交', duration: 1000 })
        this.returnDialogVisible = false
        this.getOrders()
      })
    },
    getOrders() {
      this.loadingOrders = true
      const params = { pageNum: this.orderQuery.pageNum, pageSize: this.orderQuery.pageSize }
      listUserOrders(params)
        .then(res => {
          this.loadingOrders = false
          const outer = res && res.data ? res.data : res
          const data = outer && outer.data ? outer.data : outer
          this.orderList = data.records || data.list || []
          this.totalOrders = data.total || 0
          this.loadUserReturnMap()
        })
        .catch(() => {
          this.loadingOrders = false
        })
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
      if (!this.canEditOrder(order)) return
      const firstItem = order.orderItems && order.orderItems.length > 0 ? order.orderItems[0] : {}
      this.orderEditForm = {
        id: order.id,
        orderSn: order.orderSn || '',
        productName: firstItem.productName || '未命名商品',
        quantity: firstItem.productQuantity || 1,
        receiverName: order.receiverName || '',
        receiverPhone: order.receiverPhone || '',
        address: order.address || ''
      }
      this.orderEditDialogVisible = true
    },
    submitEditOrder() {
      const runSubmit = () => {
        const payload = {
          id: this.orderEditForm.id,
          quantity: this.orderEditForm.quantity,
          receiverName: this.orderEditForm.receiverName,
          receiverPhone: this.orderEditForm.receiverPhone,
          address: this.orderEditForm.address
        }
        updateUserOrder(payload).then(() => {
          this.$message({ type: 'success', message: '修改成功', duration: 1000 })
          this.orderEditDialogVisible = false
          this.getOrders()
        })
      }

      if (this.$refs.orderEditFormRef) {
        this.$refs.orderEditFormRef.validate(valid => {
          if (!valid) return
          runSubmit()
        })
      } else {
        runSubmit()
      }
    },
    openCommentDialog() {
      if (!this.canCreateOwnReview) return
      this.$router.push({ path: '/user/comments' })
    },
    getOrderStatusText(order) {
      const returnStatus = order.returnApplyStatus
      if (returnStatus === 0) return '售后审核中'
      if (returnStatus === 1) return '退货中'
      if (returnStatus === 2) return '已退货'
      if (returnStatus === 3) return '退货被拒绝'
      return this.$options.filters.formatOrderStatus(order.status)
    },
    getReturnStatusText(status) {
      const statusMap = {
        0: '退货待审核',
        1: '退货处理中',
        2: '退货已完成',
        3: '退货被拒绝'
      }
      return statusMap[status] || '售后中'
    },
    getReturnTagType(status) {
      const map = {
        0: 'warning',
        1: '',
        2: 'success',
        3: 'danger'
      }
      return map[status] || 'info'
    },
    canEditOrder(order) {
      return this.canUpdateOwnOrder && order.status === 1 && (order.returnApplyStatus === null || order.returnApplyStatus === undefined)
    },
    canCommentOrder(order) {
      if (!this.canCreateOwnReview) return false
      if (order.returnApplyStatus === 0 || order.returnApplyStatus === 1) return false
      return order.status === 3 || order.returnApplyStatus === 2 || order.returnApplyStatus === 3
    },
    canViewLogistics(order) {
      return !!(order.deliveryCompany || order.deliverySn || order.status === 2 || order.status === 3)
    },
    canConfirmReceive(order) {
      if (!this.canUpdateOwnOrder) return false
      if (order.returnApplyStatus === 0 || order.returnApplyStatus === 1 || order.returnApplyStatus === 2) return false
      return order.status === 2
    },
    canReturnOrder(order) {
      if (!this.canApplyRefund) return false
      if (order.returnApplyStatus !== null && order.returnApplyStatus !== undefined) return false
      return order.status === 1 || order.status === 2 || order.status === 3
    },
    hasAnyOrderAction(order) {
      return this.canEditOrder(order) || this.canViewLogistics(order) || this.canConfirmReceive(order) || this.canCommentOrder(order) || this.canReturnOrder(order)
    },
    buildLogisticsRecords(order) {
      const records = []
      if (order.createTime) {
        records.push({ name: '订单已提交', time: order.createTime })
      }
      if (order.status >= 1) {
        records.push({ name: '商家已接单，等待发货', time: order.updateTime || order.createTime || '' })
      }
      if (order.deliveryTime) {
        records.push({ name: `商家已发货（${order.deliveryCompany || '物流公司待补充'} ${order.deliverySn || ''}）`, time: order.deliveryTime })
      }
      if (order.returnApplyStatus === 0) {
        records.push({ name: '用户已提交退货申请，等待商家审核', time: order.updateTime || '' })
      }
      if (order.returnApplyStatus === 1) {
        records.push({ name: '商家已同意退货，等待用户寄回商品', time: order.updateTime || '' })
      }
      if (order.returnApplyStatus === 2) {
        records.push({ name: '用户退货已完成，商品已回仓', time: order.updateTime || '' })
      }
      if (order.returnApplyStatus === 3) {
        records.push({ name: '退货申请被驳回，订单继续履约', time: order.updateTime || '' })
      }
      if (order.status === 3) {
        records.push({ name: '用户已确认收货', time: order.updateTime || order.deliveryTime || '' })
      }
      if (records.length === 0) {
        records.push({ name: '暂无物流信息', time: '' })
      }
      return records
    },
    openLogisticsDialog(order) {
      this.logisticsRecords = this.buildLogisticsRecords(order)
      this.logisticsDialogVisible = true
    },
    confirmReceive(order) {
      this.$confirm('确认已经收到商品了吗？确认后订单将变为已完成。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        confirmUserOrderReceive(order.id).then(() => {
          this.$message({ type: 'success', message: '确认收货成功', duration: 1000 })
          this.getOrders()
        })
      })
    }
  },
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
  }
}
</script>

<style scoped>
.pagination-container {
  margin-top: 15px;
}
</style>
