<template>
  <div class="app-container">
    <el-form :inline="true" size="small" @submit.native.prevent>
      <el-form-item label="状态">
        <el-select v-model="statusFilter" clearable placeholder="全部状态" @change="filterRows">
          <el-option label="待处理" value="open" />
          <el-option label="处理中" value="in_progress" />
          <el-option label="等待用户" value="pending_user" />
          <el-option label="已解决" value="resolved" />
          <el-option label="已关闭" value="closed" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-refresh" :loading="loading" @click="load">刷新</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="visibleRows" border stripe empty-text="暂无客服工单">
      <el-table-column prop="ticketNo" label="工单号" width="170" />
      <el-table-column prop="title" label="问题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" min-width="260" show-overflow-tooltip />
      <el-table-column prop="adminReply" label="客户回复" min-width="220" show-overflow-tooltip />
      <el-table-column prop="userId" label="用户 ID" width="100" />
      <el-table-column prop="priority" label="优先级" width="110">
        <template slot-scope="scope"><el-tag :type="priorityType(scope.row.priority)">{{ priorityLabel(scope.row.priority) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope"><el-tag :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" min-width="170" />
      <el-table-column label="处理" width="100" fixed="right">
        <template slot-scope="scope"><el-button type="text" @click="openTicket(scope.row)">处理工单</el-button></template>
      </el-table-column>
    </el-table>

    <el-dialog title="处理客服工单" :visible.sync="dialogVisible" width="560px">
      <div v-if="selected.ticketNo" class="ticket-detail">
        <div><strong>{{ selected.ticketNo }}</strong><span>{{ selected.title }}</span></div>
        <p>{{ selected.description }}</p>
        <el-form label-width="90px" size="small">
          <el-form-item label="处理状态">
            <el-select v-model="form.status">
              <el-option label="待处理" value="open" />
              <el-option label="处理中" value="in_progress" />
              <el-option label="等待用户" value="pending_user" />
              <el-option label="已解决" value="resolved" />
              <el-option label="已关闭" value="closed" />
            </el-select>
          </el-form-item>
          <el-form-item label="客户回复">
            <el-input v-model="form.adminReply" type="textarea" :rows="4" maxlength="2000" show-word-limit placeholder="客户可在工单查询中看到此回复" />
          </el-form-item>
          <el-form-item label="内部备注">
            <el-input v-model="form.adminNote" type="textarea" :rows="4" maxlength="2000" show-word-limit />
          </el-form-item>
          <el-form-item v-if="selected.handledBy" label="处理人">{{ selected.handledBy }}</el-form-item>
        </el-form>
      </div>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存处理结果</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listSupportTickets, updateSupportTicket } from '@/api/supportTicket'

const statusLabels = { open: '待处理', in_progress: '处理中', pending_user: '等待用户', resolved: '已解决', closed: '已关闭' }
const priorityLabels = { low: '低', medium: '普通', high: '高', urgent: '紧急' }

export default {
  name: 'SupportTicketAdmin',
  data() {
    return { rows: [], visibleRows: [], loading: false, saving: false, statusFilter: '', dialogVisible: false, selected: {}, form: { status: 'open', adminReply: '', adminNote: '' } }
  },
  created() { this.load() },
  methods: {
    async load() {
      this.loading = true
      try { const response = await listSupportTickets(); this.rows = response.data || []; this.filterRows() } finally { this.loading = false }
    },
    filterRows() { this.visibleRows = this.statusFilter ? this.rows.filter(row => row.status === this.statusFilter) : this.rows },
    openTicket(row) {
      this.selected = row
      this.form = { status: row.status, adminReply: row.adminReply || '', adminNote: row.adminNote || '' }
      this.dialogVisible = true
    },
    async save() {
      this.saving = true
      try {
        await updateSupportTicket(this.selected.ticketNo, this.form)
        this.$message.success('工单回复和处理状态已更新')
        this.dialogVisible = false
        await this.load()
      } finally { this.saving = false }
    },
    statusLabel(value) { return statusLabels[value] || value },
    priorityLabel(value) { return priorityLabels[value] || value },
    statusType(value) { return ({ open: 'warning', in_progress: 'primary', pending_user: 'info', resolved: 'success', closed: 'info' })[value] || 'info' },
    priorityType(value) { return ({ high: 'warning', urgent: 'danger', low: 'info' })[value] || '' }
  }
}
</script>

<style scoped>
.ticket-detail > div { display: flex; gap: 14px; align-items: baseline; color: #303133; }
.ticket-detail p { margin: 14px 0 22px; padding: 12px; white-space: pre-wrap; line-height: 1.6; background: #f5f7fa; border-radius: 4px; }
</style>
