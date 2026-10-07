<template>
  <el-menu class="navbar" mode="horizontal">
    <hamburger class="hamburger-container" :toggleClick="toggleSideBar" :isActive="sidebar.opened" />
    <breadcrumb />
    <el-popover
      v-if="isAdmin"
      placement="bottom"
      width="320"
      trigger="click"
      popper-class="todo-reminder-popover"
    >
      <div class="todo-reminder-panel">
        <div class="reminder-header">
          <span>待办提醒</span>
          <span class="reminder-total">共 {{ pendingTodoCount }} 项</span>
        </div>

        <div v-if="pendingOrderCount > 0" class="reminder-section">
          <div class="reminder-section-header">
            <span>待发货订单（{{ pendingOrderCount }}）</span>
            <el-button type="text" @click="goToPendingOrderPage">查看全部</el-button>
          </div>
          <div
            v-for="item in pendingOrderItems"
            :key="`order-${item.id}`"
            class="reminder-item"
            @click="goToOrderDetail(item.id)"
          >
            <div class="reminder-title">新订单 #{{ item.orderSn || item.id }}</div>
            <div class="reminder-meta">
              {{ item.memberUsername || '未知用户' }} · {{ formatOrderTime(item.createTime) }}
            </div>
          </div>
        </div>

        <div v-if="pendingReturnCount > 0" class="reminder-section">
          <div class="reminder-section-header">
            <span>退货待审核（{{ pendingReturnCount }}）</span>
            <el-button type="text" @click="goToReturnApplyPage">查看全部</el-button>
          </div>
          <div
            v-for="item in pendingReturnItems"
            :key="`return-${item.id}`"
            class="reminder-item"
            @click="goToReturnApplyDetail(item.id)"
          >
            <div class="reminder-title">用户退货申请 #{{ item.id }}</div>
            <div class="reminder-meta">{{ item.username || '未知用户' }} · 订单 {{ item.orderId }}</div>
            <div class="reminder-reason">{{ item.reason || '未填写退货原因' }}</div>
          </div>
        </div>
        <div v-if="pendingTodoCount === 0" class="reminder-empty">暂无待处理事项</div>
      </div>
      <el-badge slot="reference" :value="pendingTodoCount" :max="99" :hidden="pendingTodoCount === 0" class="todo-reminder-badge">
        <div class="todo-reminder-trigger" title="待办提醒">
          <i class="el-icon-bell"></i>
        </div>
      </el-badge>
    </el-popover>
    <el-dropdown class="avatar-container" trigger="click">
      <div class="avatar-wrapper">
        <img class="user-avatar" :src="avatar" />
        <i class="el-icon-caret-bottom" />
      </div>
      <el-dropdown-menu class="user-dropdown" slot="dropdown">
        <router-link class="inlineBlock" to="/admin/home">
          <el-dropdown-item>首页</el-dropdown-item>
        </router-link>
        <el-dropdown-item divided>
          <span @click="logout" style="display: block">退出</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </el-menu>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import { fetchList as fetchReturnApplyList } from '@/api/returnApply'
import { fetchList as fetchOrderList } from '@/api/order'

export default {
  components: {
    Breadcrumb,
    Hamburger
  },
  data() {
    return {
      pendingOrderCount: 0,
      pendingOrderItems: [],
      pendingReturnCount: 0,
      pendingReturnItems: [],
      reminderTimer: null,
      knownPendingOrderIds: [],
      knownPendingIds: []
    }
  },
  computed: {
    ...mapGetters(['sidebar', 'avatar', 'roles']),
    isAdmin() {
      const roles = this.roles || []
      return roles.some(role => String(role).toUpperCase() === 'ADMIN')
    },
    pendingTodoCount() {
      return this.pendingOrderCount + this.pendingReturnCount
    }
  },
  created() {
    if (this.isAdmin) {
      this.loadPendingReminders(false)
      this.startReminderPolling()
    }
  },
  beforeDestroy() {
    this.stopReminderPolling()
  },
  watch: {
    isAdmin: {
      immediate: false,
      handler(val) {
        if (val) {
          this.loadPendingReminders(false)
          this.startReminderPolling()
        } else {
          this.stopReminderPolling()
          this.pendingOrderCount = 0
          this.pendingOrderItems = []
          this.pendingReturnCount = 0
          this.pendingReturnItems = []
          this.knownPendingOrderIds = []
          this.knownPendingIds = []
        }
      }
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('ToggleSideBar')
    },
    startReminderPolling() {
      this.stopReminderPolling()
      this.reminderTimer = setInterval(() => {
        this.loadPendingReminders(true)
      }, 30000)
    },
    stopReminderPolling() {
      if (this.reminderTimer) {
        clearInterval(this.reminderTimer)
        this.reminderTimer = null
      }
    },
    loadPendingReminders(shouldNotify) {
      this.loadPendingOrders(shouldNotify)
      this.loadPendingReturns(shouldNotify)
    },
    loadPendingOrders(shouldNotify) {
      fetchOrderList({ pageNum: 1, pageSize: 5, status: 1 }, { silent: true })
        .then(response => {
          const data = this.extractPageData(response)
          const records = data.records || []
          const currentIds = records.map(item => item.id)
          if (shouldNotify) {
            const newIds = currentIds.filter(id => this.knownPendingOrderIds.indexOf(id) === -1)
            if (newIds.length > 0) {
              this.$notify({
                title: '新订单提醒',
                message: `有 ${newIds.length} 条新订单待发货`,
                type: 'warning',
                duration: 4000
              })
            }
          }
          this.pendingOrderCount = Number(data.total) || records.length
          this.pendingOrderItems = records
          this.knownPendingOrderIds = currentIds
        })
        .catch(() => {})
    },
    loadPendingReturns(shouldNotify) {
      fetchReturnApplyList({ pageNum: 1, pageSize: 5, status: 0 }, { silent: true })
        .then(response => {
          const data = this.extractPageData(response)
          const records = data.records || []
          const currentIds = records.map(item => item.id)
          if (shouldNotify) {
            const newIds = currentIds.filter(id => this.knownPendingIds.indexOf(id) === -1)
            if (newIds.length > 0) {
              this.$notify({
                title: '退货提醒',
                message: `有 ${newIds.length} 条新的用户退货申请待审核`,
                type: 'warning',
                duration: 4000
              })
            }
          }
          this.pendingReturnCount = Number(data.total) || records.length
          this.pendingReturnItems = records
          this.knownPendingIds = currentIds
        })
        .catch(() => {})
    },
    extractPageData(response) {
      const outer = response && response.data ? response.data : response
      return outer && outer.data ? outer.data : (outer || {})
    },
    formatOrderTime(value) {
      if (!value) return '时间未知'
      return String(value).replace('T', ' ')
    },
    goToPendingOrderPage() {
      this.$router.push({ path: '/admin/oms/order', query: { status: 1, pageNum: 1, pageSize: 10 } })
    },
    goToOrderDetail(id) {
      this.$router.push({ path: '/admin/oms/orderDetail', query: { id } })
    },
    goToReturnApplyPage() {
      this.$router.push({ path: '/admin/oms/returnApply' })
    },
    goToReturnApplyDetail(id) {
      this.$router.push({ path: '/admin/oms/returnApplyDetail', query: { id } })
    },
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        // 退出后直接回登录页，避免被另一端 token 干扰跳转
        const role = (this.roles[0] || 'ADMIN').toUpperCase()
        this.$router.replace(role === 'USER' ? '/client/login' : '/admin/login')
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.navbar {
  height: 50px;
  line-height: 50px;
  border-radius: 0 !important;

  .hamburger-container {
    line-height: 58px;
    height: 50px;
    float: left;
    padding: 0 10px;
  }

  .screenfull {
    position: absolute;
    right: 90px;
    top: 16px;
    color: red;
  }

  .todo-reminder-badge {
    position: absolute;
    right: 110px;
    top: 10px;
  }

  .todo-reminder-trigger {
    width: 32px;
    height: 32px;
    line-height: 32px;
    text-align: center;
    border-radius: 50%;
    cursor: pointer;
    color: #606266;
    transition: background-color 0.2s ease;
  }

  .todo-reminder-trigger:hover {
    background: #ecf5ff;
    color: #409eff;
  }

  .avatar-container {
    height: 50px;
    display: inline-block;
    position: absolute;
    right: 35px;

    .avatar-wrapper {
      cursor: pointer;
      margin-top: 5px;
      position: relative;

      .user-avatar {
        width: 40px;
        height: 40px;
        border-radius: 10px;
      }

      .el-icon-caret-bottom {
        position: absolute;
        right: -20px;
        top: 25px;
        font-size: 12px;
      }
    }
  }
}

.todo-reminder-panel {
  .reminder-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    margin-bottom: 10px;
  }

  .reminder-total {
    color: #909399;
    font-size: 12px;
    font-weight: 400;
  }

  .reminder-section + .reminder-section {
    margin-top: 12px;
  }

  .reminder-section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #606266;
    font-size: 13px;
    font-weight: 600;
  }

  .reminder-item {
    padding: 10px 0;
    border-top: 1px solid #ebeef5;
    cursor: pointer;
  }

  .reminder-item:first-of-type {
    border-top: 0;
  }

  .reminder-title {
    font-size: 13px;
    font-weight: 600;
    color: #303133;
  }

  .reminder-meta,
  .reminder-reason {
    font-size: 12px;
    color: #909399;
    line-height: 1.6;
  }

  .reminder-empty {
    color: #909399;
    font-size: 13px;
    text-align: center;
    padding: 14px 0;
  }
}
</style>
