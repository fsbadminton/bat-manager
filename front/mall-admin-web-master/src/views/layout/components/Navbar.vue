<template>
  <el-menu class="navbar" mode="horizontal">
    <hamburger class="hamburger-container" :toggleClick="toggleSideBar" :isActive="sidebar.opened" />
    <breadcrumb />
    <el-popover
      v-if="isAdmin"
      placement="bottom"
      width="320"
      trigger="click"
      popper-class="return-reminder-popover"
    >
      <div class="return-reminder-panel">
        <div class="reminder-header">
          <span>退货提醒</span>
          <el-button type="text" @click="goToReturnApplyPage">查看全部</el-button>
        </div>
        <div v-if="pendingReturnCount > 0">
          <div class="reminder-summary">当前有 {{ pendingReturnCount }} 条用户退货待审核</div>
          <div
            v-for="item in pendingReturnItems"
            :key="item.id"
            class="reminder-item"
            @click="goToReturnApplyDetail(item.id)"
          >
            <div class="reminder-title">用户退货申请 #{{ item.id }}</div>
            <div class="reminder-meta">{{ item.username || '未知用户' }} · 订单 {{ item.orderId }}</div>
            <div class="reminder-reason">{{ item.reason || '未填写退货原因' }}</div>
          </div>
        </div>
        <div v-else class="reminder-empty">暂无新的用户退货待审核</div>
      </div>
      <el-badge slot="reference" :value="pendingReturnCount" :hidden="pendingReturnCount === 0" class="return-reminder-badge">
        <div class="return-reminder-trigger" title="退货提醒">
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
        <router-link class="inlineBlock" to="/">
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

export default {
  components: {
    Breadcrumb,
    Hamburger
  },
  data() {
    return {
      pendingReturnCount: 0,
      pendingReturnItems: [],
      reminderTimer: null,
      knownPendingIds: []
    }
  },
  computed: {
    ...mapGetters(['sidebar', 'avatar', 'roles']),
    isAdmin() {
      const roles = this.roles || []
      return roles.some(role => String(role).toUpperCase() === 'ADMIN')
    }
  },
  created() {
    if (this.isAdmin) {
      this.loadPendingReturns(false)
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
          this.loadPendingReturns(false)
          this.startReminderPolling()
        } else {
          this.stopReminderPolling()
          this.pendingReturnCount = 0
          this.pendingReturnItems = []
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
        this.loadPendingReturns(true)
      }, 30000)
    },
    stopReminderPolling() {
      if (this.reminderTimer) {
        clearInterval(this.reminderTimer)
        this.reminderTimer = null
      }
    },
    loadPendingReturns(shouldNotify) {
      fetchReturnApplyList({ pageNum: 1, pageSize: 5, status: 0 }, { silent: true })
        .then(response => {
          const outer = response && response.data ? response.data : response
          const data = outer && outer.data ? outer.data : outer
          const records = data && data.records ? data.records : []
          const total = data && data.total ? data.total : records.length
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
          this.pendingReturnCount = total
          this.pendingReturnItems = records
          this.knownPendingIds = currentIds
        })
        .catch(() => {})
    },
    goToReturnApplyPage() {
      this.$router.push({ path: '/oms/returnApply' })
    },
    goToReturnApplyDetail(id) {
      this.$router.push({ path: '/oms/returnApplyDetail', query: { id } })
    },
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        // 退出后直接回登录页，避免被另一端 token 干扰跳转
        this.$router.replace('/login')
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

  .return-reminder-badge {
    position: absolute;
    right: 110px;
    top: 10px;
  }

  .return-reminder-trigger {
    width: 32px;
    height: 32px;
    line-height: 32px;
    text-align: center;
    border-radius: 50%;
    cursor: pointer;
    color: #606266;
    transition: background-color 0.2s ease;
  }

  .return-reminder-trigger:hover {
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

.return-reminder-panel {
  .reminder-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    margin-bottom: 10px;
  }

  .reminder-summary {
    margin-bottom: 10px;
    color: #e6a23c;
    font-size: 13px;
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
