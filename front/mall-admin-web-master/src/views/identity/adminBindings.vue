<template>
  <div class="app-container">
    <el-form :inline="true" :model="query" size="small" @submit.native.prevent="load">
      <el-form-item label="商城用户 ID">
        <el-input v-model="query.customerId" placeholder="输入商城用户 ID" clearable @keyup.enter.native="load" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" :loading="loading" @click="load">查询绑定</el-button>
        <el-button icon="el-icon-document" :loading="auditLoading" @click="loadAudit">查看审计</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="bindings" border empty-text="暂无绑定记录" v-loading="loading">
      <el-table-column prop="platform" label="平台" width="140" />
      <el-table-column prop="bindingReference" label="绑定引用" min-width="220" />
      <el-table-column prop="bindingVersion" label="版本" width="90" />
      <el-table-column prop="boundAt" label="绑定时间" min-width="200" />
      <el-table-column label="操作" width="110">
        <template slot-scope="scope">
          <el-button type="text" @click="move(scope.row)">迁移身份</el-button>
          <el-button type="text" @click="unbind(scope.row)">强制解绑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-divider content-position="left">管理员操作审计</el-divider>
    <el-table :data="auditRows" border empty-text="暂无审计记录" v-loading="auditLoading">
      <el-table-column prop="createdAt" label="时间" min-width="180" />
      <el-table-column prop="operatorName" label="操作人" width="140" />
      <el-table-column prop="action" label="操作" width="130" />
      <el-table-column prop="platform" label="平台" width="100" />
      <el-table-column prop="externalUserIdHash" label="平台 ID 摘要" width="150" />
      <el-table-column prop="reason" label="原因" min-width="220" />
      <el-table-column prop="outcome" label="结果" width="110" />
    </el-table>

    <el-dialog :title="dialogMode === 'move' ? '迁移平台身份' : '管理员解绑'" :visible.sync="dialogVisible" width="460px">
      <el-form :model="unbindForm" label-width="90px">
        <el-form-item label="平台">{{ selected.platform }}</el-form-item>
        <el-form-item v-if="dialogMode === 'move'" label="目标用户 ID">
          <el-input v-model="unbindForm.targetCustomerId" type="number" min="1" />
        </el-form-item>
        <el-form-item label="解绑原因">
          <el-input v-model="unbindForm.reason" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button :type="dialogMode === 'move' ? 'primary' : 'danger'" :loading="saving" :disabled="!unbindForm.reason.trim()" @click="confirmAction">{{ dialogMode === 'move' ? '确认迁移' : '确认解绑' }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { adminMoveIdentity, adminUnbindIdentity, listAdminIdentityBindings, listIdentityBindingAudit } from '@/api/identityBinding'

export default {
  name: 'AdminIdentityBindings',
  data() {
    return {
      query: { customerId: '' },
      bindings: [],
      auditRows: [],
      loading: false,
      auditLoading: false,
      saving: false,
      dialogVisible: false,
      selected: {},
      unbindForm: { reason: '', targetCustomerId: '' },
      dialogMode: 'unbind'
    }
  },
  methods: {
    validCustomerId() {
      if (!/^\d+$/.test(this.query.customerId)) {
        this.$message.warning('请输入有效的商城用户 ID')
        return false
      }
      return true
    },
    async load() {
      if (!this.validCustomerId()) return
      this.loading = true
      try {
        const response = await listAdminIdentityBindings(this.query.customerId)
        this.bindings = response.data || []
      } finally { this.loading = false }
    },
    async loadAudit() {
      if (!this.validCustomerId()) return
      this.auditLoading = true
      try {
        const response = await listIdentityBindingAudit(this.query.customerId)
        this.auditRows = response.data || []
      } finally { this.auditLoading = false }
    },
    unbind(row) {
      this.selected = row
      this.dialogMode = 'unbind'
      this.unbindForm = { reason: '', targetCustomerId: '' }
      this.dialogVisible = true
    },
    move(row) {
      this.selected = row
      this.dialogMode = 'move'
      this.unbindForm = { reason: '', targetCustomerId: '' }
      this.dialogVisible = true
    },
    async confirmAction() {
      if (!this.unbindForm.reason.trim() || !this.validCustomerId()) return
      if (this.dialogMode === 'move' && !/^\d+$/.test(this.unbindForm.targetCustomerId)) {
        this.$message.warning('请输入有效的目标用户 ID')
        return
      }
      this.saving = true
      try {
        const data = {
          platform: this.selected.platform,
          bindingReference: this.selected.bindingReference,
          reason: this.unbindForm.reason.trim()
        }
        if (this.dialogMode === 'move') {
          await adminMoveIdentity(this.query.customerId, { ...data, targetCustomerId: Number(this.unbindForm.targetCustomerId) })
        } else {
          await adminUnbindIdentity(this.query.customerId, data)
        }
        this.dialogVisible = false
        this.$message.success(this.dialogMode === 'move' ? '平台身份已迁移' : '平台身份已解绑')
        await Promise.all([this.load(), this.loadAudit()])
      } finally { this.saving = false }
    }
  }
}
</script>

<style scoped>
.app-container { padding: 20px; }
</style>
