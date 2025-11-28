<template>
  <div class="app-container">
    <el-card shadow="never">
      <el-table :data="returnList" v-loading="loading" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="orderId" label="订单ID" width="180"/>
        <el-table-column prop="reason" label="退货原因"/>
        <el-table-column prop="status" label="处理状态" width="120">
          <template slot-scope="scope">{{ scope.row.status | formatStatus }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="200"/>
      </el-table>
      <div class="pagination-container">
        <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" layout="total, sizes, prev, pager, next, jumper" :page-size="query.pageSize" :page-sizes="[5,10,15]" :current-page.sync="query.pageNum" :total="total" />
      </div>
    </el-card>
  </div>
</template>

<script>
import { listUserReturnApplies } from '@/api/userReturnApply'
export default {
  name: 'UserReturns',
  data() {
    return {
      returnList: [],
      loading: false,
      total: 0,
      query: { pageNum: 1, pageSize: 5 }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const params = { pageNum: this.query.pageNum, pageSize: this.query.pageSize }
      listUserReturnApplies(params).then(res => {
        // this.loading = false
        // const outer = res && res.data ? res.data : res
        // const data = outer && outer.data ? outer.data : outer
        // this.returnList = data.records || data.list || []
        // this.total = data.total || 0
        this.loading = false
    const data = res && res.data ? res.data : res
    this.returnList = Array.isArray(data) ? data : (data.records || data.list || [])
    this.total = this.returnList.length
      }).catch(() => { this.loading = false })
    },
    handleSizeChange(val) {
      this.query.pageSize = val
      this.query.pageNum = 1
      this.getList()
    },
    handleCurrentChange(val) {
      this.query.pageNum = val
      this.getList()
    }
  },
  filters: {
    formatStatus(val) {
      const map = { 0: '待处理', 1: '通过', 2: '拒绝' }
      return map[val] || '未知'
    }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 15px }
</style>
