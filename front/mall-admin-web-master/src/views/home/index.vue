<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <div>
        <h1>仪表盘</h1>
        <p>{{ currentDateText }}</p>
      </div>
      <el-tooltip content="刷新数据" placement="bottom">
        <el-button
          icon="el-icon-refresh"
          circle
          :loading="dashboardLoading"
          @click="loadDashboard"
        />
      </el-tooltip>
    </div>

    <el-row :gutter="16" class="metric-grid" v-loading="dashboardLoading">
      <el-col v-for="item in primaryMetrics" :key="item.label" :xs="24" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-icon" :class="item.colorClass">
            <i :class="item.icon"></i>
          </div>
          <div class="metric-content">
            <span class="metric-label">{{ item.label }}</span>
            <strong class="metric-value">{{ item.value }}</strong>
          </div>
        </div>
      </el-col>
    </el-row>

    <section class="dashboard-section">
      <div class="section-header">
        <h2>待处理事务</h2>
        <span>共 {{ pendingTotal }} 项</span>
      </div>
      <div class="todo-grid">
        <button
          v-for="item in todoItems"
          :key="item.label"
          type="button"
          class="todo-item"
          @click="item.action"
        >
          <span>{{ item.label }}</span>
          <strong :class="{ urgent: item.urgent && item.count > 0 }">{{ item.count }}</strong>
          <i class="el-icon-arrow-right"></i>
        </button>
      </div>
    </section>

    <el-row :gutter="16" class="overview-grid">
      <el-col :xs="24" :lg="12">
        <section class="dashboard-section overview-section">
          <div class="section-header">
            <h2>商品概览</h2>
            <el-button type="text" @click="goToProducts">查看商品</el-button>
          </div>
          <div class="overview-values">
            <div v-for="item in productOverview" :key="item.label" class="overview-item">
              <strong :class="{ warning: item.warning && item.value > 0 }">{{ item.value }}</strong>
              <span>{{ item.label }}</span>
            </div>
          </div>
        </section>
      </el-col>
      <el-col :xs="24" :lg="12">
        <section class="dashboard-section overview-section">
          <div class="section-header">
            <h2>用户概览</h2>
          </div>
          <div class="overview-values">
            <div v-for="item in userOverview" :key="item.label" class="overview-item">
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
            </div>
          </div>
        </section>
      </el-col>
    </el-row>

    <section class="dashboard-section statistics-section">
      <div class="section-header statistics-header">
        <h2>订单趋势</h2>
        <el-date-picker
          v-model="orderCountDate"
          size="small"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :picker-options="pickerOptions"
          @change="getChartData"
        />
      </div>
      <div class="statistics-content">
        <div class="period-summary">
          <div v-for="item in periodMetrics" :key="item.label" class="period-item">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
        <div class="chart-wrap">
          <ve-line
            ref="orderLine"
            height="340px"
            :data="chartData"
            :legend-visible="true"
            :loading="chartLoading"
            :data-empty="dataEmpty"
            :settings="chartSettings"
          />
        </div>
      </div>
    </section>

    <section class="dashboard-section latest-orders">
      <div class="section-header">
        <h2>最新订单</h2>
        <el-button type="text" @click="goToOrders">查看全部</el-button>
      </div>
      <el-table :data="latestOrders" v-loading="ordersLoading" empty-text="暂无订单">
        <el-table-column prop="orderSn" label="订单编号" min-width="180" />
        <el-table-column prop="memberUsername" label="用户" min-width="110" />
        <el-table-column label="订单金额" min-width="110">
          <template slot-scope="scope">￥{{ formatAmount(scope.row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template slot-scope="scope">
            <el-tag size="small" :type="statusTagType(scope.row.status)">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" min-width="170">
          <template slot-scope="scope">{{ formatDateTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="90" align="center">
          <template slot-scope="scope">
            <el-button type="text" @click="goToOrderDetail(scope.row.id)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script>
import { fetchDashboardSummary, fetchOrderChart } from '@/api/dashboard'
import { fetchList as fetchOrderList } from '@/api/order'

const emptySummary = {
  todayOrderCount: 0,
  todaySalesAmount: 0,
  yesterdaySalesAmount: 0,
  pendingPaymentCount: 0,
  pendingDeliveryCount: 0,
  shippedCount: 0,
  completedCount: 0,
  pendingReturnCount: 0,
  totalProductCount: 0,
  publishedProductCount: 0,
  unpublishedProductCount: 0,
  lowStockProductCount: 0,
  totalUserCount: 0,
  todayUserCount: 0,
  yesterdayUserCount: 0,
  monthUserCount: 0,
  monthOrderCount: 0,
  weekOrderCount: 0,
  monthSalesAmount: 0,
  weekSalesAmount: 0
}

export default {
  name: 'home',
  data() {
    return {
      summary: { ...emptySummary },
      latestOrders: [],
      dashboardLoading: false,
      ordersLoading: false,
      chartLoading: false,
      dataEmpty: false,
      orderCountDate: [],
      chartSettings: {
        xAxisType: 'time',
        area: true,
        axisSite: { right: ['orderAmount'] },
        labelMap: { orderCount: '订单数量', orderAmount: '订单金额' }
      },
      chartData: {
        columns: ['date', 'orderCount', 'orderAmount'],
        rows: []
      },
      pickerOptions: {
        shortcuts: [
          {
            text: '最近7天',
            onClick: picker => picker.$emit('pick', this.createDateRange(6))
          },
          {
            text: '最近30天',
            onClick: picker => picker.$emit('pick', this.createDateRange(29))
          }
        ]
      }
    }
  },
  computed: {
    currentDateText() {
      return new Date().toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      })
    },
    primaryMetrics() {
      return [
        { label: '今日订单', value: this.summary.todayOrderCount, icon: 'el-icon-document', colorClass: 'blue' },
        { label: '今日销售额', value: `￥${this.formatAmount(this.summary.todaySalesAmount)}`, icon: 'el-icon-coin', colorClass: 'green' },
        { label: '昨日销售额', value: `￥${this.formatAmount(this.summary.yesterdaySalesAmount)}`, icon: 'el-icon-data-line', colorClass: 'amber' },
        { label: '待处理事务', value: this.pendingTotal, icon: 'el-icon-bell', colorClass: 'red' }
      ]
    },
    pendingTotal() {
      return this.numberValue(this.summary.pendingPaymentCount) +
        this.numberValue(this.summary.pendingDeliveryCount) +
        this.numberValue(this.summary.pendingReturnCount)
    },
    todoItems() {
      return [
        { label: '待付款订单', count: this.summary.pendingPaymentCount, action: () => this.goToOrders(0) },
        { label: '待发货订单', count: this.summary.pendingDeliveryCount, urgent: true, action: () => this.goToOrders(1) },
        { label: '已发货订单', count: this.summary.shippedCount, action: () => this.goToOrders(2) },
        { label: '已完成订单', count: this.summary.completedCount, action: () => this.goToOrders(3) },
        { label: '退货待审核', count: this.summary.pendingReturnCount, urgent: true, action: this.goToReturns }
      ]
    },
    productOverview() {
      return [
        { label: '全部商品', value: this.summary.totalProductCount },
        { label: '已上架', value: this.summary.publishedProductCount },
        { label: '未上架', value: this.summary.unpublishedProductCount },
        { label: '库存紧张', value: this.summary.lowStockProductCount, warning: true }
      ]
    },
    userOverview() {
      return [
        { label: '今日新增', value: this.summary.todayUserCount },
        { label: '昨日新增', value: this.summary.yesterdayUserCount },
        { label: '本月新增', value: this.summary.monthUserCount },
        { label: '用户总数', value: this.summary.totalUserCount }
      ]
    },
    periodMetrics() {
      return [
        { label: '本周订单', value: this.summary.weekOrderCount },
        { label: '本月订单', value: this.summary.monthOrderCount },
        { label: '本周销售额', value: `￥${this.formatAmount(this.summary.weekSalesAmount)}` },
        { label: '本月销售额', value: `￥${this.formatAmount(this.summary.monthSalesAmount)}` }
      ]
    }
  },
  created() {
    this.orderCountDate = this.createDateRange(13).map(this.formatDate)
    this.loadDashboard()
  },
  methods: {
    loadDashboard() {
      this.dashboardLoading = true
      Promise.all([this.loadSummary(), this.loadLatestOrders(), this.getChartData()])
        .then(() => {
          this.dashboardLoading = false
        })
        .catch(() => {
          this.dashboardLoading = false
        })
    },
    loadSummary() {
      return fetchDashboardSummary().then(response => {
        this.summary = { ...emptySummary, ...(response.data || {}) }
      })
    },
    loadLatestOrders() {
      this.ordersLoading = true
      return fetchOrderList({ pageNum: 1, pageSize: 5 })
        .then(response => {
          this.latestOrders = response.data.records || []
          this.ordersLoading = false
        })
        .catch(error => {
          this.ordersLoading = false
          throw error
        })
    },
    getChartData() {
      if (!this.orderCountDate || this.orderCountDate.length !== 2) return Promise.resolve()
      this.chartLoading = true
      const start = this.normalizeDateValue(this.orderCountDate[0])
      const end = this.normalizeDateValue(this.orderCountDate[1])
      return fetchOrderChart({ start, end })
        .then(response => {
          const rows = Array.isArray(response.data) ? response.data : []
          this.chartData = {
            columns: ['date', 'orderCount', 'orderAmount'],
            rows: this.fillChartDates(start, end, rows)
          }
          this.dataEmpty = rows.length === 0
          this.chartLoading = false
        })
        .catch(error => {
          this.chartLoading = false
          this.dataEmpty = true
          throw error
        })
    },
    fillChartDates(start, end, rows) {
      const rowMap = rows.reduce((result, item) => {
        const date = this.normalizeDateValue(item.date)
        result[date] = item
        return result
      }, {})
      const result = []
      const cursor = new Date(`${start}T00:00:00`)
      const endDate = new Date(`${end}T00:00:00`)
      while (cursor <= endDate) {
        const date = this.formatDate(cursor)
        const item = rowMap[date] || {}
        result.push({
          date,
          orderCount: this.numberValue(item.orderCount),
          orderAmount: this.numberValue(item.orderAmount)
        })
        cursor.setDate(cursor.getDate() + 1)
      }
      return result
    },
    createDateRange(daysBeforeToday) {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - daysBeforeToday)
      return [start, end]
    },
    formatDate(date) {
      const value = date instanceof Date ? date : new Date(date)
      const year = value.getFullYear()
      const month = String(value.getMonth() + 1).padStart(2, '0')
      const day = String(value.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    normalizeDateValue(value) {
      if (!value) return ''
      if (value instanceof Date) return this.formatDate(value)
      return String(value).substring(0, 10)
    },
    formatDateTime(value) {
      return value ? String(value).replace('T', ' ') : '-'
    },
    formatAmount(value) {
      return this.numberValue(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    },
    numberValue(value) {
      const number = Number(value)
      return Number.isFinite(number) ? number : 0
    },
    statusText(status) {
      return ['待付款', '待发货', '已发货', '已完成', '已关闭', '无效订单'][status] || '未知状态'
    },
    statusTagType(status) {
      return ({ 0: 'warning', 1: 'danger', 2: '', 3: 'success', 4: 'info', 5: 'info' })[status] || 'info'
    },
    goToOrders(status) {
      const query = { pageNum: 1, pageSize: 10 }
      if (status !== undefined) query.status = status
      this.$router.push({ path: '/admin/oms/order', query })
    },
    goToOrderDetail(id) {
      this.$router.push({ path: '/admin/oms/orderDetail', query: { id } })
    },
    goToReturns() {
      this.$router.push({ path: '/admin/oms/returnApply', query: { status: 0 } })
    },
    goToProducts() {
      this.$router.push({ path: '/admin/pms/product' })
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background: #f5f7fa;
  min-height: calc(100vh - 50px);
}

.dashboard-header,
.section-header,
.statistics-content,
.overview-values,
.metric-card,
.todo-item {
  display: flex;
  align-items: center;
}

.dashboard-header {
  justify-content: space-between;
  margin-bottom: 18px;
}

.dashboard-header h1,
.section-header h2 {
  margin: 0;
  color: #303133;
  letter-spacing: 0;
}

.dashboard-header h1 {
  font-size: 22px;
}

.dashboard-header p {
  margin: 6px 0 0;
  color: #909399;
  font-size: 13px;
}

.metric-grid .el-col,
.overview-grid .el-col {
  margin-bottom: 16px;
}

.metric-card {
  min-height: 104px;
  padding: 18px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  box-sizing: border-box;
}

.metric-icon {
  width: 48px;
  height: 48px;
  margin-right: 14px;
  line-height: 48px;
  text-align: center;
  border-radius: 6px;
  font-size: 22px;
}

.metric-icon.blue { color: #409eff; background: #ecf5ff; }
.metric-icon.green { color: #67c23a; background: #f0f9eb; }
.metric-icon.amber { color: #e6a23c; background: #fdf6ec; }
.metric-icon.red { color: #f56c6c; background: #fef0f0; }

.metric-content {
  min-width: 0;
}

.metric-label,
.metric-value {
  display: block;
}

.metric-label {
  color: #909399;
  font-size: 13px;
}

.metric-value {
  margin-top: 8px;
  overflow: hidden;
  color: #303133;
  font-size: 22px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dashboard-section {
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #ebeef5;
}

.section-header {
  min-height: 48px;
  justify-content: space-between;
  padding: 0 18px;
  border-bottom: 1px solid #ebeef5;
  box-sizing: border-box;
}

.section-header h2 {
  font-size: 15px;
}

.section-header > span {
  color: #909399;
  font-size: 12px;
}

.todo-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
}

.todo-item {
  min-width: 0;
  height: 58px;
  padding: 0 16px;
  color: #606266;
  background: #fff;
  border: 0;
  border-right: 1px solid #ebeef5;
  cursor: pointer;
  font: inherit;
  text-align: left;
}

.todo-item:last-child {
  border-right: 0;
}

.todo-item:hover {
  color: #409eff;
  background: #f5f7fa;
}

.todo-item span {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.todo-item strong {
  margin-left: auto;
  color: #303133;
  font-size: 17px;
}

.todo-item strong.urgent {
  color: #f56c6c;
}

.todo-item i {
  margin-left: 8px;
  color: #c0c4cc;
}

.overview-grid {
  margin-bottom: 0;
}

.overview-section {
  margin-bottom: 0;
}

.overview-values {
  min-height: 116px;
  justify-content: space-around;
  padding: 12px;
  box-sizing: border-box;
}

.overview-item {
  min-width: 0;
  flex: 1;
  text-align: center;
}

.overview-item strong,
.overview-item span {
  display: block;
}

.overview-item strong {
  color: #303133;
  font-size: 24px;
}

.overview-item strong.warning {
  color: #e6a23c;
}

.overview-item span {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}

.statistics-header {
  flex-wrap: wrap;
  gap: 10px;
  padding-top: 8px;
  padding-bottom: 8px;
}

.statistics-content {
  align-items: stretch;
}

.period-summary {
  width: 190px;
  flex: 0 0 190px;
  padding: 18px;
  border-right: 1px solid #ebeef5;
  box-sizing: border-box;
}

.period-item + .period-item {
  margin-top: 20px;
}

.period-item span,
.period-item strong {
  display: block;
}

.period-item span {
  color: #909399;
  font-size: 12px;
}

.period-item strong {
  margin-top: 7px;
  color: #303133;
  font-size: 19px;
}

.chart-wrap {
  min-width: 0;
  flex: 1;
  padding: 8px 14px 0;
}

.latest-orders {
  margin-bottom: 0;
}

@media (max-width: 1100px) {
  .todo-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .todo-item {
    border-bottom: 1px solid #ebeef5;
  }
}

@media (max-width: 720px) {
  .dashboard-container {
    padding: 14px;
  }

  .todo-grid {
    grid-template-columns: 1fr;
  }

  .todo-item {
    border-right: 0;
  }

  .overview-values {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 24px 8px;
  }

  .statistics-content {
    display: block;
  }

  .period-summary {
    display: grid;
    width: auto;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 18px;
    border-right: 0;
    border-bottom: 1px solid #ebeef5;
  }

  .period-item + .period-item {
    margin-top: 0;
  }

  .statistics-header .el-date-editor {
    width: 100%;
  }
}
</style>
