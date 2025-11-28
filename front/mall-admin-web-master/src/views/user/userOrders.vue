<template>
  <div class="app-container">
    <el-card shadow="never">
      <el-table :data="orderList" v-loading="loadingOrders" border style="width: 100%">
        <el-table-column label="订单号" prop="orderSn" width="300" align="center"/>
        <el-table-column label="球拍" width="300">
          <template slot-scope="scope">
            <span>
              {{ scope.row.orderItems && scope.row.orderItems.length > 0
                ? scope.row.orderItems[0].productName
                : '无' }}
            </span>
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="400" align="center">
          <template slot-scope="scope">
                <template v-if="orderReturnMap[scope.row.id] === 0">
                  <el-tag type="warning">退货中</el-tag>
                </template>
                <template v-else>
                  <el-button size="mini" @click="openEditOrder(scope.row)" v-if="scope.row.status === 1">修改</el-button>
                  <el-button size="mini" @click="openCommentDialog(scope.row)">评价</el-button>
                  <el-button size="mini" type="danger" v-if="scope.row.status === 1" @click="openReturnDialog(scope.row)">退货</el-button>
                </template>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination background @size-change="handleOrderSizeChange" @current-change="handleOrderCurrentChange" layout="total, sizes, prev, pager, next, jumper" :page-size="orderQuery.pageSize" :page-sizes="[5,10,15]" :current-page.sync="orderQuery.pageNum" :total="totalOrders" />
      </div>
      <el-dialog title="退货申请" :visible.sync="returnDialogVisible" width="500px">
        <el-form :model="returnForm" label-width="100px">
          <el-form-item label="退货原因">
            <el-select v-model="returnForm.reason" placeholder="请选择退货原因" style="width: 100%">
              <el-option v-for="item in returnReasonList" :key="item.id" :label="item.name" :value="item.name" />
              <el-option label="其他（请填写）" value="__custom__" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="returnForm.reason==='__custom__'" label="自定义原因">
            <el-input v-model="returnForm.customReason" placeholder="请输入自定义退货原因" />
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="returnDialogVisible=false">取消</el-button>
          <el-button type="primary" @click="submitReturnApply">提交</el-button>
        </span>
      </el-dialog>

      <el-dialog title="修改订单" :visible.sync="orderEditDialogVisible" width="500px">
        <el-form :model="orderEditForm" label-width="100px">
          <el-form-item label="订单号">
            <el-input v-model="orderEditForm.id" disabled/>
          </el-form-item>
          <el-form-item label="数量">
            <el-input v-model.number="orderEditForm.quantity" type="number" min="1" />
          </el-form-item>
          <el-form-item label="收货人">
            <el-input v-model="orderEditForm.receiver" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="orderEditForm.phone" placeholder="请输入手机号" />
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
    </el-card>
  </div>
</template>

<script>

import { listUserOrders, updateUserOrder, addUserReview } from '@/api/userLogin'
import { createUserReturnApply, listUserReturnApplies } from '@/api/userReturnApply'
import { listUserReturnReasons } from '@/api/userReturnReason'

export default {
  name: 'UserOrders',
  data() {
    return {
      orderList: [],
      orderQuery: { pageNum: 1, pageSize: 5 },
      totalOrders: 0,
      loadingOrders: false,
      orderEditDialogVisible: false,
      orderEditForm: { id: null, quantity: 1, receiver: '', phone: '', address: '' },
      returnDialogVisible: false,
      returnForm: { orderId: null, reason: '', customReason: '' },
      returnReasonList: [],
      // map of orderId -> latest return status (0: pending,1:approved,2:rejected)
      orderReturnMap: {}
    }
  },
  mounted() {
    this.loadReturnReasons();
  },
  created() {
    this.getOrders()
  },
  methods: {
    loadReturnReasons() {
      listUserReturnReasons().then(res => {
        // 兼容后端多种返回格式：直接数组、{data: [...]}, 或分页包装
        const data = res && res.data ? res.data : res;
        if (Array.isArray(data)) {
          this.returnReasonList = data;
        } else if (Array.isArray(data.data)) {
          this.returnReasonList = data.data;
        } else {
          this.returnReasonList = data.list || data.records || [];
        }
      }).catch(err => {
        console.warn('获取退货原因失败（已静默）', err);
        this.returnReasonList = [];
      });
    },
    // 加载当前用户所有退货申请，建立 orderId 到状态的映射
    loadUserReturnMap() {
      // 读取全部退货记录（不分页或大页数）
      listUserReturnApplies({ pageNum: 1, pageSize: 1000 }).then(res => {
        const outer = res && res.data ? res.data : res
        const data = outer && outer.data ? outer.data : outer
        const list = data.records || data.list || data || []
        const map = {}
        for (let i = 0; i < list.length; i++) {
          const item = list[i]
          const oid = item.orderId || item.orderId || item.orderId // defensive
          const status = (item.status !== undefined && item.status !== null) ? item.status : (item.returnStatus !== undefined ? item.returnStatus : 0)
          if (oid != null) {
            // 若有多条申请，保留最新（按 id 或 createTime） — 这里直接覆盖，后端通常按时间返回
            map[oid] = status
          }
        }
        this.orderReturnMap = map
      }).catch(err => {
        console.warn('加载用户退货映射失败（已静默）', err)
        this.orderReturnMap = {}
      })
    },
    openReturnDialog(order) {
      this.returnForm = { orderId: order.id, reason: '', customReason: '' };
      this.returnDialogVisible = true;
    },
    submitReturnApply() {
      let reason = this.returnForm.reason === '__custom__' ? this.returnForm.customReason : this.returnForm.reason;
      if (!reason) {
        this.$message({ type: 'warning', message: '请选择或填写退货原因' });
        return;
      }
      createUserReturnApply({ orderId: this.returnForm.orderId, reason }).then(() => {
        this.$message({ type: 'success', message: '退货申请已提交', duration: 1000 });
        this.returnDialogVisible = false;
        this.getOrders();
      });
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
        // 刷新用户退货映射以更新列表中的退货状态显示
        this.loadUserReturnMap()
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
        quantity: order.quantity,
        receiver: order.receiver || order.consignee || order.consigneeName || order.receiverName || '',
        phone: order.phone || order.mobile || '',
        address: order.address
      }
      this.orderEditDialogVisible = true
    },
    submitEditOrder() {
      const payload = {
        id: this.orderEditForm.id,
        quantity: this.orderEditForm.quantity,
        receiver: this.orderEditForm.receiver,
        phone: this.orderEditForm.phone,
        address: this.orderEditForm.address
      }
      updateUserOrder(payload).then(() => {
        this.$message({ type: 'success', message: '修改成功', duration: 1000 })
        this.orderEditDialogVisible = false
        this.getOrders()
      })
    },
    openCommentDialog(row) {
      const item = row.orderItems[0]
      this.$router.push({ path: '/user/comments' })
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
.pagination-container { margin-top: 15px }
</style>