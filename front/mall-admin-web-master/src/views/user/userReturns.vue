<template>
  <div class="app-container">
    <el-card shadow="never">
      <el-alert v-if="loadError" :title="loadError" type="error" :closable="false" show-icon />
      <el-table
        :data="returnList"
        v-loading="loading"
        :empty-text="loadError ? '加载失败' : '暂无退货申请'"
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="orderSn" label="订单编号" width="220"/>
        <el-table-column prop="reason" label="退货原因"/>
        <el-table-column prop="status" label="处理状态" width="120">
          <template slot-scope="scope">{{ scope.row.status | formatStatus }}</template>
        </el-table-column>
        <el-table-column prop="companyAddress" label="退回地址" width="220">
          <template slot-scope="scope">{{ scope.row.companyAddress || '待商家确认' }}</template>
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
      loadError: '',
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
      this.loadError = ''
      const params = { pageNum: this.query.pageNum, pageSize: this.query.pageSize }
      listUserReturnApplies(params).then(res => {
        this.loading = false
        const outer = res && res.data ? res.data : res
        const data = outer && outer.data ? outer.data : outer
        const list = Array.isArray(data) ? data : (data.records || data.list || [])
        this.returnList = list
        this.total = data && data.total ? data.total : list.length
      }).catch(() => {
        this.loading = false
        this.returnList = []
        this.total = 0
        this.loadError = '退货记录加载失败，请稍后重试'
      })
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
      const map = { 0: '待处理', 1: '退货中', 2: '已完成', 3: '已拒绝' }
      return map[val] || '未知'
    }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 15px }
.el-alert { margin-bottom: 12px }
</style>
