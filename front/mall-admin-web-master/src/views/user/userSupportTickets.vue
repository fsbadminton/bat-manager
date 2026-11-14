<template>
  <div class="app-container ticket-page">
    <div class="ticket-heading">
      <div>
        <h2>我的客服工单</h2>
        <span>查看处理进度和客服回复</span>
      </div>
      <div class="heading-actions">
        <el-button icon="el-icon-refresh" :loading="loading" @click="load">刷新</el-button>
        <el-button type="primary" icon="el-icon-edit" @click="openCreate">提交工单</el-button>
      </div>
    </div>

    <el-alert v-if="loadError" :title="loadError" type="error" :closable="false" show-icon />
    <el-table :data="tickets" v-loading="loading" :empty-text="loadError ? '加载失败' : '暂无客服工单'" border>
      <el-table-column prop="ticketNo" label="工单号" width="170" />
      <el-table-column prop="title" label="问题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="priority" label="优先级" width="100">
        <template slot-scope="scope"><el-tag size="mini" :type="priorityType(scope.row.priority)">{{ priorityLabel(scope.row.priority) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="status" label="处理状态" width="120">
        <template slot-scope="scope"><el-tag size="mini" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="updatedAt" label="最近更新" min-width="170" />
      <el-table-column label="详情" width="100" fixed="right">
        <template slot-scope="scope"><el-button type="text" @click="openDetail(scope.row)">查看</el-button></template>
      </el-table-column>
    </el-table>

    <el-dialog title="提交客服工单" :visible.sync="createVisible" width="520px" :close-on-click-modal="!submissionAttempt">
      <el-alert v-if="submissionError" :title="submissionError" type="error" :closable="false" show-icon />
      <el-form :model="form" label-width="90px">
        <el-form-item label="问题标题">
          <el-input v-model="form.title" maxlength="120" show-word-limit :disabled="!!submissionAttempt" placeholder="简要描述您遇到的问题" />
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input v-model="form.description" type="textarea" :rows="6" maxlength="4000" show-word-limit :disabled="!!submissionAttempt" placeholder="请提供处理问题所需的信息，不要填写银行卡密码等敏感信息" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority" :disabled="!!submissionAttempt">
            <el-option label="普通" value="medium" />
            <el-option label="高" value="high" />
            <el-option label="紧急" value="urgent" />
            <el-option label="低" value="low" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button v-if="submissionAttempt" @click="cancelRetry">取消本次重试</el-button>
        <el-button v-else @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" :disabled="!canSubmit" @click="submit">
          {{ submissionAttempt ? '使用相同请求重试' : '提交工单' }}
        </el-button>
      </span>
    </el-dialog>

    <el-dialog title="客服工单" :visible.sync="detailVisible" width="560px">
      <div v-if="selected.ticketNo" class="ticket-detail">
        <div class="ticket-detail-head">
          <strong>{{ selected.ticketNo }}</strong>
          <el-tag size="mini" :type="statusType(selected.status)">{{ statusLabel(selected.status) }}</el-tag>
        </div>
        <h3>{{ selected.title }}</h3>
        <div class="field-label">问题描述</div>
        <p class="ticket-description">{{ selected.description }}</p>
        <div class="field-label">客服回复</div>
        <p v-if="selected.adminReply" class="ticket-reply">{{ selected.adminReply }}</p>
        <el-empty v-else description="客服暂未回复" :image-size="56" />
        <div class="ticket-updated">更新于 {{ selected.updatedAt || selected.createdAt }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMySupportTicket, listMySupportTickets } from '@/api/userSupportTicket'

const statuses = { open: '待处理', in_progress: '处理中', pending_user: '等待您回复', resolved: '已解决', closed: '已关闭' }
const priorities = { low: '低', medium: '普通', high: '高', urgent: '紧急' }

export default {
  name: 'UserSupportTickets',
  data() {
    return {
      tickets: [], loading: false, loadError: '', detailVisible: false, selected: {},
      createVisible: false, submitting: false, submissionError: '', submissionAttempt: null,
      form: { title: '', description: '', priority: 'medium' }
    }
  },
  computed: {
    canSubmit() {
      return this.submissionAttempt || (this.form.title.trim() && this.form.description.trim())
    }
  },
  created() { this.load() },
  methods: {
    async load() {
      this.loading = true
      this.loadError = ''
      try {
        const response = await listMySupportTickets()
        this.tickets = response.data || []
      } catch (error) {
        this.tickets = []
        this.loadError = '客服工单加载失败，请稍后重试'
      } finally { this.loading = false }
    },
    async openDetail(row) {
      try {
        const response = await getMySupportTicket(row.ticketNo)
        this.selected = response.data || row
        this.detailVisible = true
      } catch (error) {
        this.$message.error('工单详情加载失败，请稍后重试')
      }
    },
    openCreate() {
      if (!this.submissionAttempt) {
        this.form = { title: '', description: '', priority: 'medium' }
        this.submissionError = ''
      }
      this.createVisible = true
    },
    async submit() {
      if (this.submitting || !this.canSubmit) return
      if (!this.submissionAttempt) {
        this.submissionAttempt = {
          title: this.form.title.trim(),
          description: this.form.description.trim(),
          priority: this.form.priority,
          idempotencyKey: this.newIdempotencyKey()
        }
      }
      this.submitting = true
      this.submissionError = ''
      try {
        const response = await createMySupportTicket(this.submissionAttempt)
        this.createVisible = false
        this.submissionAttempt = null
        this.$message.success(`工单已提交：${response.data.ticketNo}`)
        await this.load()
      } catch (error) {
        this.submissionError = '提交结果未确认。可使用相同请求重试，或取消后重新填写。'
      } finally { this.submitting = false }
    },
    async cancelRetry() {
      try {
        await this.$confirm('如果上一次请求已被服务端接收，重新提交可能创建另一张工单。确定丢弃本次重试吗？', '取消重试', { type: 'warning' })
        this.submissionAttempt = null
        this.submissionError = ''
        this.form = { title: '', description: '', priority: 'medium' }
      } catch (error) {}
    },
    newIdempotencyKey() {
      if (window.crypto && window.crypto.randomUUID) return `web-${window.crypto.randomUUID()}`
      return `web-${Date.now()}-${Math.random().toString(36).slice(2)}-${Math.random().toString(36).slice(2)}`
    },
    statusLabel(value) { return statuses[value] || value },
    priorityLabel(value) { return priorities[value] || value },
    statusType(value) { return ({ open: 'warning', in_progress: 'primary', pending_user: 'info', resolved: 'success', closed: 'info' })[value] || 'info' },
    priorityType(value) { return ({ high: 'warning', urgent: 'danger', low: 'info' })[value] || '' }
  }
}
</script>

<style scoped>
.ticket-heading { display: flex; align-items: center; justify-content: space-between; margin-bottom: 18px; }
.ticket-heading h2 { margin: 0; color: #303133; font-size: 20px; }
.ticket-heading span { display: block; margin-top: 6px; color: #909399; font-size: 13px; }
.heading-actions { display: flex; gap: 8px; }
.ticket-detail-head { display: flex; align-items: center; justify-content: space-between; color: #303133; }
.ticket-detail h3 { margin: 18px 0; font-size: 17px; }
.field-label { margin: 14px 0 7px; color: #606266; font-size: 13px; font-weight: 600; }
.ticket-description, .ticket-reply { margin: 0; padding: 12px; white-space: pre-wrap; overflow-wrap: anywhere; line-height: 1.65; border-radius: 4px; }
.ticket-description { color: #606266; background: #f5f7fa; }
.ticket-reply { color: #275d46; background: #f0f9f4; border-left: 3px solid #67c23a; }
.ticket-updated { margin-top: 16px; color: #909399; font-size: 12px; text-align: right; }
</style>
