<template>
  <div class="identity-page">
    <el-card shadow="never">
      <div slot="header" class="page-header">
        <div>
          <div class="title">平台账号绑定</div>
          <div class="subtitle">绑定后可在 Telegram 或 QQ 客服中查询当前商城账号信息</div>
        </div>
        <el-button icon="el-icon-refresh" @click="loadBindings">刷新</el-button>
      </div>

      <div class="bind-actions">
        <el-button type="primary" icon="el-icon-link" :loading="loadingPlatform === 'telegram'" @click="start('telegram')">
          生成 Telegram 绑定码
        </el-button>
        <el-button icon="el-icon-link" :loading="loadingPlatform === 'qq'" @click="start('qq')">
          生成 QQ 绑定码
        </el-button>
      </div>

      <el-alert
        v-if="token"
        class="token-alert"
        type="info"
        :closable="false"
        title="请在对应平台私聊 Nexus Bot，发送 /bind 加空格和绑定码。绑定码 5 分钟内有效且只能使用一次。"
      >
        <div class="token-row">
          <code>{{ token.token }}</code>
          <el-button size="mini" icon="el-icon-document-copy" @click="copyToken">复制</el-button>
        </div>
        <div class="token-expiry">平台：{{ token.platform }}，过期时间：{{ token.expiresAt }}</div>
      </el-alert>

      <el-table v-loading="loading" :data="bindings" empty-text="暂未绑定平台账号">
        <el-table-column prop="platform" label="平台" width="140" />
        <el-table-column prop="bindingReference" label="绑定引用" min-width="220" />
        <el-table-column prop="boundAt" label="绑定时间" min-width="220" />
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button type="text" @click="remove(scope.row)">解绑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { listIdentityBindings, startIdentityBinding, unbindIdentity } from '@/api/identityBinding'

export default {
  name: 'UserIdentityBindings',
  data() {
    return { bindings: [], loading: false, loadingPlatform: '', token: null }
  },
  created() { this.loadBindings() },
  methods: {
    async loadBindings() {
      this.loading = true
      try {
        const response = await listIdentityBindings()
        this.bindings = response.data || []
      } finally { this.loading = false }
    },
    async start(platform) {
      this.loadingPlatform = platform
      try {
        const response = await startIdentityBinding(platform)
        this.token = response.data
        this.$message.success('绑定码已生成')
      } finally { this.loadingPlatform = '' }
    },
    copyToken() {
      if (!this.token) return
      navigator.clipboard.writeText(this.token.token).then(() => this.$message.success('已复制'))
    },
    remove(row) {
      this.$confirm('解绑后该平台将不能查询商城个人信息，是否继续？', '确认解绑', { type: 'warning' })
        .then(async () => {
          await unbindIdentity(row.platform, row.bindingReference)
          this.$message.success('已解绑')
          await this.loadBindings()
        }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.identity-page { padding: 24px; }
.page-header { display: flex; align-items: center; justify-content: space-between; }
.title { color: #303133; font-size: 18px; font-weight: 600; }
.subtitle { margin-top: 8px; color: #909399; font-size: 13px; }
.bind-actions { margin-bottom: 20px; }
.token-alert { margin-bottom: 20px; }
.token-row { display: flex; align-items: center; gap: 12px; margin-top: 12px; }
.token-row code { padding: 8px 12px; background: #f4f4f5; color: #303133; font-size: 18px; letter-spacing: 1px; }
.token-expiry { margin-top: 8px; color: #909399; font-size: 12px; }
</style>
