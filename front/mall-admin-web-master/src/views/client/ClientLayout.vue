<template>
  <div class="client-layout">
    <header class="client-header">
      <span class="client-title">球拍商城</span>
      <el-menu
        class="client-nav"
        mode="horizontal"
        router
        :default-active="$route.path"
      >
        <el-menu-item index="/client/products">球拍商城</el-menu-item>
        <el-menu-item index="/client/orders">我的订单</el-menu-item>
        <el-menu-item index="/client/coupons">我的优惠券</el-menu-item>
        <el-menu-item index="/client/returns">我的退货</el-menu-item>
        <el-menu-item index="/client/comments">我的评价</el-menu-item>
      </el-menu>
      <div class="client-account">
        <span class="client-user" :title="username">{{ username }}</span>
        <el-button type="text" icon="el-icon-switch-button" @click="logout">退出</el-button>
      </div>
    </header>
    <app-main />
  </div>
</template>

<script>
import { AppMain } from '@/views/layout/components'
import { getUsername } from '@/utils/auth'

export default {
  name: 'ClientLayout',
  components: { AppMain },
  computed: {
    username() {
      return getUsername('USER') || '用户'
    }
  },
  methods: {
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        this.$router.replace('/client/login')
      })
    }
  }
}
</script>

<style scoped>
.client-layout {
  min-height: 100%;
  background: #f5f7fa;
}

.client-header {
  min-height: 60px;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 28px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

.client-title {
  flex: 0 0 auto;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
}

.client-nav {
  flex: 1;
  min-width: 0;
  border-bottom: 0;
}

.client-nav.el-menu--horizontal > .el-menu-item {
  height: 59px;
  line-height: 59px;
}

.client-account {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  gap: 14px;
}

.client-user {
  max-width: 140px;
  overflow: hidden;
  color: #606266;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 900px) {
  .client-header {
    flex-wrap: wrap;
    gap: 0;
    padding: 10px 16px 0;
  }

  .client-title {
    flex: 1;
  }

  .client-account {
    margin-left: 16px;
  }

  .client-nav {
    order: 3;
    flex: 0 0 100%;
    width: 100%;
    overflow-x: auto;
    white-space: nowrap;
  }

  .client-nav.el-menu--horizontal {
    display: flex;
  }

  .client-nav.el-menu--horizontal > .el-menu-item {
    flex: 0 0 auto;
    height: 48px;
    padding: 0 14px;
    line-height: 48px;
  }
}
</style>
