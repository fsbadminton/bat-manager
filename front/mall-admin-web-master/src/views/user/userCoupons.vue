<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="never">
          <div slot="header" class="clearfix">
            <span>可领取优惠券</span>
          </div>
          <el-table :data="availableList" v-loading="availableLoading" border style="width: 100%">
            <el-table-column label="名称" prop="name" />
            <el-table-column label="门槛" width="120" align="center">
              <template slot-scope="scope">满{{ scope.row.minPoint || 0 }}元</template>
            </el-table-column>
            <el-table-column label="面额" width="100" align="center">
              <template slot-scope="scope">{{ scope.row.amount }}元</template>
            </el-table-column>
            <el-table-column label="限领" width="100" align="center" prop="perLimit" />
            <el-table-column label="操作" width="120" align="center">
              <template slot-scope="scope">
                <el-button size="mini" type="primary" @click="handleClaim(scope.row)">领取</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <div slot="header" class="clearfix">
            <span>我的优惠券</span>
          </div>
          <el-table :data="myList" v-loading="myLoading" border style="width: 100%">
            <el-table-column label="名称" prop="couponName" />
            <el-table-column label="优惠码" prop="couponCode" width="180" />
            <el-table-column label="面额" width="100" align="center">
              <template slot-scope="scope">{{ scope.row.amount }}元</template>
            </el-table-column>
            <el-table-column label="状态" width="120" align="center">
              <template slot-scope="scope">
                <el-tag :type="formatStatusType(scope.row.useStatus)">{{ formatStatusText(scope.row.useStatus) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="有效期" width="200" align="center">
              <template slot-scope="scope">{{ formatDate(scope.row.startTime) }} 至 {{ formatDate(scope.row.endTime) }}</template>
            </el-table-column>
            <el-table-column label="使用时间" width="180" align="center">
              <template slot-scope="scope">{{ formatDate(scope.row.useTime) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { claimCoupon, listAvailableCoupons, listMyCoupons } from '@/api/userCoupon'

export default {
  name: 'UserCoupons',
  data() {
    return {
      availableList: [],
      availableLoading: false,
      myList: [],
      myLoading: false
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loadAvailable()
      this.loadMine()
    },
    loadAvailable() {
      this.availableLoading = true
      listAvailableCoupons({ pageNum: 1, pageSize: 100 })
        .then(res => {
          this.availableLoading = false
          const data = res && res.data ? res.data : {}
          this.availableList = data.list || []
        })
        .catch(() => {
          this.availableLoading = false
        })
    },
    loadMine() {
      this.myLoading = true
      listMyCoupons({ pageNum: 1, pageSize: 100 })
        .then(res => {
          this.myLoading = false
          const data = res && res.data ? res.data : {}
          this.myList = data.list || []
        })
        .catch(() => {
          this.myLoading = false
        })
    },
    handleClaim(row) {
      claimCoupon(row.id).then(() => {
        this.$message({ type: 'success', message: '领取成功', duration: 1000 })
        this.loadData()
      })
    },
    formatStatusText(status) {
      const map = { 0: '未使用', 1: '已使用', 2: '已过期' }
      return map[status] || '未知'
    },
    formatStatusType(status) {
      const map = { 0: 'success', 1: 'info', 2: 'danger' }
      return map[status] || 'info'
    },
    formatDate(value) {
      if (!value) return '未使用'
      return String(value).replace('T', ' ').slice(0, 16)
    }
  }
}
</script>
