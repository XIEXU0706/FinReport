<template>
  <transition name="slide">
    <div v-if="visible" class="ai-panel-overlay" @click.self="close">
      <div class="ai-panel">
        <!-- 标题栏 -->
        <div class="ai-header">
          <div class="ai-header-left">
            <img :src="robotImg" width="28" height="28" alt="AI" class="ai-header-icon">
            <div>
              <div class="ai-header-title">智慧AI助手</div>
              <div class="ai-header-status" :class="{ online: connected }">
                {{ connected ? '在线' : '连接中...' }}
              </div>
            </div>
          </div>
          <div class="ai-header-actions">
            <el-tooltip content="历史会话" placement="bottom">
              <span class="ai-header-btn" :class="{ active: showHistory }" @click="showHistory = !showHistory">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path
                      d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
                </svg>
              </span>
            </el-tooltip>
            <el-tooltip content="新建会话" placement="bottom">
              <span class="ai-header-btn" :class="{ disabled: messages.length === 0 }" @click="newSession">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                </svg>
              </span>
            </el-tooltip>
            <el-tooltip content="清空对话" placement="bottom">
              <span class="ai-header-btn" @click="clearChat">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                </svg>
              </span>
            </el-tooltip>
            <span class="ai-header-btn" @click="close">
              <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <path
                    d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
              </svg>
            </span>
          </div>
        </div>

        <div class="ai-body">
          <!-- 历史会话侧栏 -->
          <transition name="history-slide">
            <div v-if="showHistory" class="ai-history">
              <div class="ai-history-header">
                <span>历史会话</span>
                <span class="ai-history-count">{{ sessions.length }}条</span>
              </div>
              <div class="ai-history-list">
                <div v-if="sessions.length === 0" class="ai-history-empty">暂无历史会话</div>
                <div
                    v-for="s in sessions" :key="s.id"
                    class="ai-history-item"
                    :class="{ active: s.id === backendSessionId }"
                    @click="loadSession(s)"
                >
                  <div class="ai-history-item-title">{{ s.title }}</div>
                  <div class="ai-history-item-meta">
                    <span>{{ s.message_count }}条消息</span>
                    <span>{{ s.create_time?.slice(0, 10) }}</span>
                    <span class="ai-history-del" @click.stop="deleteSession(s.id)">
                      <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                        <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                      </svg>
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </transition>

          <!-- 消息区 -->
          <div class="ai-messages" ref="messagesRef">
            <div v-if="messages.length === 0" class="ai-empty">
              <img :src="robotImg" width="64" height="64" alt="AI" class="ai-empty-icon">
              <p class="ai-empty-title">您好！我是智慧AI助手</p>
              <p class="ai-empty-desc">您可以向我询问业务数据或要求生成分析报告</p>
              <div class="ai-suggestions">
                <span v-for="(q, i) in suggestions" :key="i" class="ai-suggestion-tag" @click="ask(q)">{{ q }}</span>
              </div>
            </div>

            <div v-for="(msg, i) in messages" :key="i" class="ai-msg-row" :class="msg.role">
              <div v-if="msg.role === 'assistant'" class="ai-msg-avatar">
                <img :src="robotImg" width="32" height="32" alt="AI">
              </div>
              <div class="ai-msg-content">
                <div class="ai-msg-bubble" v-html="renderMarkdown(msg.content)"></div>
                <div v-if="msg.role === 'assistant' && msg.content.length > 20 && !loading" class="ai-msg-actions">
                  <span class="ai-export-btn" @click="exportWord(msg.content, i)">导出 Word</span>
                </div>
              </div>
            </div>

            <div v-if="loading" class="ai-msg-row assistant">
              <div class="ai-msg-avatar"><img :src="robotImg" width="32" height="32" alt="AI"></div>
              <div class="ai-msg-content">
                <div class="ai-msg-bubble ai-typing">
                  <span class="dot dot-1"></span><span class="dot dot-2"></span><span class="dot dot-3"></span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区 -->
        <div class="ai-input-area">
          <div class="ai-input-wrap">
            <textarea
                v-model="inputText"
                class="ai-input"
                placeholder="输入您的问题..."
                rows="1"
                @keydown.enter.prevent="sendMessage"
                @input="autoResize"
            ></textarea>
            <el-button type="primary" class="ai-send-btn" :disabled="!inputText.trim() || loading" @click="sendMessage">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
              </svg>
            </el-button>
          </div>
          <div class="ai-input-tip">AI 回答仅供参考，关键数据请以系统报表为准</div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
import robotImg from '@/static/robot.png'
const AI_API_BASE = 'http://localhost:9096'

export default {
  name: 'AIChatPanel',
  props: {
    visible: Boolean
  },
  data() {
    return {
      robotImg,
      connected: false,
      inputText: '',
      loading: false,
      messages: [],
      backendSessionId: null,
      showHistory: false,
      sessions: [],
      suggestions: [
        '昨天交易总额是多少？',
        '这个月贷款情况怎么样？',
        '当前有哪些预警未处理？',
        '写一份本周经营分析报告'
      ]
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.fetchSessions()
        this.createSession()
        setTimeout(() => this.connected = true, 800)
        this.$nextTick(() => this.scrollToBottom())
      } else {
        this.connected = false
        this.showHistory = false
        this.updateSessionTitle()
      }
    }
  },
  methods: {
      close() {
        this.$emit('close')
      },

      /* ── 会话管理 ── */
      // 获取历史会话列表
      async fetchSessions() {
        try {
          const res = await fetch(`${AI_API_BASE}/sessions`)
          // console.log("++++++++++",res)
          this.sessions = await res.json()
          // console.log(this.sessions)
        } catch {
          this.sessions = []
        }
      },

      // 加载历史会话的消息
      async loadSession(s) {
        this.showHistory = false
        if (s.id === this.backendSessionId) return
        this.messages = []
        this.backendSessionId = s.id
        try {
          const res = await fetch(`${AI_API_BASE}/sessions/${s.id}/messages`)
          const msgs = await res.json()
          this.messages = msgs.map(m => ({role: m.role, content: m.content}))
        } catch {
        }
        this.$nextTick(() => this.scrollToBottom())
      },

      // 创建新会话（后端生成 ID）
      async createSession() {
        try {
          const res = await fetch(`${AI_API_BASE}/sessions`, {method: 'POST'})
          this.backendSessionId = (await res.json()).id
        } catch {

        }
      },

      // 新建会话
      async newSession() {
        if (this.messages.length > 0) {
          await this.updateSessionTitle()
        }
        this.messages = []
        await this.createSession()
        this.$nextTick(() => this.scrollToBottom())
      },

      // 关闭面板时保存标题到后端
      async updateSessionTitle() {
        if (!this.backendSessionId || this.messages.length === 0) return
        const title = (this.messages[0]?.content || '').slice(0, 30) || '新会话'
        try {
          await fetch(`${AI_API_BASE}/sessions/${this.backendSessionId}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({title})
          })
          this.fetchSessions()
        } catch {
        }
      },

    // 删除会话
      async deleteSession(id) {
        try {
          await fetch(`${AI_API_BASE}/sessions/${id}`, {method: 'DELETE'})
          this.sessions = this.sessions.filter(s => s.id !== id)
          if (id === this.backendSessionId) {
            this.messages = []
            this.backendSessionId = null
            this.createSession()
          }
        } catch {
        }
      },

      clearChat() {
        this.messages = []
      },

      /* 导出 Word */
      async exportWord(content, msgIndex) {
        try {
          // 找到这条 AI 消息前面的用户提问，作为默认文件名
          const prevUser = this.messages.slice(0, msgIndex).reverse().find(m => m.role === 'user')
          const query = (prevUser?.content || '').replace(/^(写一份|生成|分析|给我|请|帮我)\s*/, '').slice(0, 30) || `${new Date().toISOString().slice(0, 10)}-经营分析报告`
          const res = await fetch(`${AI_API_BASE}/export/word`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({title: query, query: prevUser?.content || '', content})
          })
          if (!res.ok) return
          const blob = await res.blob()
          const url = URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          // 从 Content-Disposition 取文件名（后端提取的更准确），取不到就用前端的
          const cd = res.headers.get('content-disposition') || ''
          const match = cd.match(/filename\*?=UTF-8''(.+?)(?:;|$)/)
          a.download = match ? decodeURIComponent(match[1]) : (query + '.docx')
          a.click()
          URL.revokeObjectURL(url)
        } catch {
        }
      },

      /* ── AI 交互 ── */
      ask(text) {
        this.inputText = text
        this.sendMessage()
      },
      async sendMessage() {
        const text = this.inputText.trim()
        if (!text || this.loading || !this.backendSessionId) return

        this.messages.push({role: 'user', content: text})
        this.inputText = ''
        this.loading = true
        this.$nextTick(() => this.scrollToBottom())

        try {
          let aiContent = ''
          let aiMsgIndex = -1

          const res = await fetch(`${AI_API_BASE}/fin_agent/ask`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({query: text, session_id: this.backendSessionId})
          })

          console.log('res::::', res)
          const reader = res.body.getReader()
          const decoder = new TextDecoder()
          let buffer = ''

          while (true) {
            const {done, value} = await reader.read()  // 读取流数据块
            if (done) break
            buffer += decoder.decode(value, {stream: true})  // 解码并追加到缓冲区
            for (const line of buffer.split('\n').slice(0, -1)) {
              if (line.startsWith('data: ')) {
                try {
                  const data = JSON.parse(line.slice(6))
                  if (data.type === 'content') {
                    aiContent += data.content
                    if (aiMsgIndex === -1) {
                      aiMsgIndex = this.messages.length
                      this.messages.push({role: 'assistant', content: ''})
                    }
                    this.messages[aiMsgIndex].content = aiContent
                    this.$nextTick(() => this.scrollToBottom())
                  }
                } catch {
                }
              }
            }
            buffer = buffer.split('\n').pop() || ''  // 移除最后一行未完成的行
          }
        } catch {
          this.messages.push({role: 'assistant', content: '抱歉，服务暂时不可用，请稍后再试。'})
        } finally {
          this.loading = false
          this.$nextTick(() => this.scrollToBottom())
        }
      },

      scrollToBottom() {
        const el = this.$refs.messagesRef
        if (el) el.scrollTop = el.scrollHeight
      },
      autoResize(e) {
        e.target.style.height = 'auto'
        e.target.style.height = Math.min(e.target.scrollHeight, 120) + 'px'
      },

      renderMarkdown(text) {
        if (!text) return ''
        let html = text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        html = html.replace(/```(\w*)\n([\s\S]*?)```/g, '<pre><code>$2</code></pre>')
        html = html.replace(/`([^`]+)`/g, '<code>$1</code>')
        html = html.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
        html = html.replace(/\*([^*]+)\*/g, '<em>$1</em>')
        html = html.replace(/^- (.+)$/gm, '<li>$1</li>')
        html = html.replace(/(<li>[\s\S]*?<\/li>)/g, '<ul>$1</ul>').replace(/<\/ul>\s*<ul>/g, '')
        html = html.replace(/\n{2,}/g, '</p><p>').replace(/\n/g, '<br>')
        return '<p>' + html + '</p>'
      }
    }
  }
</script>

<style scoped>
.ai-panel-overlay {
  position: fixed;
  inset: 0;
  z-index: 2000;
  background: rgba(0, 0, 0, .2);
  display: flex;
  justify-content: flex-end;
}

.ai-panel {
  width: 440px;
  height: 100%;
  background: #fff;
  display: flex;
  flex-direction: column;
  box-shadow: -8px 0 32px rgba(0, 0, 0, .12);
}

/* ===== 标题栏 ===== */
.ai-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #eef0f4;
  flex-shrink: 0;
}

.ai-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-header-icon {
  border-radius: 8px;
}

.ai-header-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a202c;
}

.ai-header-status {
  font-size: 12px;
  color: #a0aec0;
  margin-top: 2px;
}

.ai-header-status.online {
  color: #48bb78;
}

.ai-header-actions {
  display: flex;
  gap: 4px;
}

.ai-header-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  cursor: pointer;
  color: #a0aec0;
  transition: all .2s;
}

.ai-header-btn:hover, .ai-header-btn.active {
  background: #f0f2f5;
  color: #606266;
}

.ai-header-btn.active {
  color: #409EFF;
}

.ai-header-btn.disabled {
  opacity: .35;
  cursor: not-allowed;
}

.ai-header-btn.disabled:hover {
  background: transparent;
  color: #a0aec0;
}

/* ===== 主体（消息 / 历史） ===== */
.ai-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* ===== 历史侧栏 ===== */
.ai-history {
  width: 200px;
  flex-shrink: 0;
  border-right: 1px solid #eef0f4;
  background: #fafbfc;
  display: flex;
  flex-direction: column;
}

.ai-history-header {
  padding: 14px 12px 10px;
  font-size: 13px;
  font-weight: 600;
  color: #1a202c;
  display: flex;
  justify-content: space-between;
}

.ai-history-count {
  font-weight: 400;
  color: #a0aec0;
  font-size: 12px;
}

.ai-history-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 8px 8px;
}

.ai-history-empty {
  text-align: center;
  color: #a0aec0;
  font-size: 13px;
  padding: 24px 0;
}

.ai-history-item {
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 4px;
  transition: background .15s;
}

.ai-history-item:hover {
  background: #f0f2f5;
}

.ai-history-item.active {
  background: rgba(64, 158, 255, .08);
}

.ai-history-item-title {
  font-size: 13px;
  color: #1a202c;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ai-history-item-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: #a0aec0;
  margin-top: 4px;
}

.ai-history-del {
  margin-left: auto;
  cursor: pointer;
  display: inline-flex;
  opacity: 0;
  transition: opacity .15s;
}

.ai-history-item:hover .ai-history-del {
  opacity: 1;
}

.ai-history-del:hover {
  color: #f56c6c;
}

/* ===== 消息区 ===== */
.ai-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f7f8fa;
}

.ai-messages::-webkit-scrollbar {
  width: 4px;
}

.ai-messages::-webkit-scrollbar-thumb {
  background: #d0d5dd;
  border-radius: 2px;
}

.ai-empty {
  text-align: center;
  padding: 40px 20px;
}

.ai-empty-icon {
  border-radius: 16px;
  margin-bottom: 16px;
}

.ai-empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 8px;
}

.ai-empty-desc {
  font-size: 14px;
  color: #718096;
  margin: 0 0 24px;
}

.ai-suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.ai-suggestion-tag {
  padding: 6px 14px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid #e2e8f0;
  font-size: 13px;
  color: #4a5568;
  cursor: pointer;
  transition: all .2s;
}

.ai-suggestion-tag:hover {
  border-color: #409EFF;
  color: #409EFF;
  background: rgba(64, 158, 255, .05);
}

/* 对话气泡 */
.ai-msg-row {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  animation: fadeIn .3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.ai-msg-row.user {
  flex-direction: row-reverse;
}

.ai-msg-avatar {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  background: #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-msg-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ai-msg-content {
  max-width: 80%;
}

.ai-msg-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.ai-msg-row.assistant .ai-msg-bubble {
  background: #fff;
  color: #1a202c;
  border: 1px solid #e8ecf1;
  border-top-left-radius: 4px;
}

.ai-msg-row.user .ai-msg-bubble {
  background: linear-gradient(135deg, #1a73e8, #0d47a1);
  color: #fff;
  border-top-right-radius: 4px;
}

.ai-msg-bubble :deep(p) {
  margin: 0 0 8px;
}

.ai-msg-bubble :deep(p:last-child) {
  margin: 0;
}

.ai-msg-bubble :deep(code) {
  padding: 1px 5px;
  border-radius: 4px;
  background: #f0f2f5;
  font-size: 13px;
  font-family: 'Courier New', monospace;
}

.ai-msg-bubble :deep(pre) {
  background: #1e293b;
  color: #e2e8f0;
  padding: 12px 16px;
  border-radius: 8px;
  overflow-x: auto;
  font-size: 13px;
  margin: 8px 0;
}

.ai-msg-bubble :deep(pre code) {
  background: none;
  padding: 0;
  color: inherit;
}

.ai-msg-bubble :deep(ul) {
  margin: 4px 0;
  padding-left: 20px;
}

.ai-msg-bubble :deep(li) {
  margin-bottom: 4px;
}

/* 导出按钮 */
.ai-msg-actions {
  text-align: right;
  margin-top: 4px;
  padding-right: 4px;
}

.ai-export-btn {
  font-size: 12px;
  color: #409EFF;
  cursor: pointer;
  opacity: 0;
  transition: opacity .2s;
  user-select: none;
}

.ai-export-btn:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.ai-msg-row.assistant:hover .ai-export-btn {
  opacity: 1;
}

/* 打字动画 */
.ai-typing {
  display: flex;
  gap: 4px;
  padding: 14px 18px !important;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #a0aec0;
  animation: typing 1.4s infinite;
}

.dot-2 {
  animation-delay: .2s;
}

.dot-3 {
  animation-delay: .4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: .4;
  }
  30% {
    transform: translateY(-6px);
    opacity: 1;
  }
}

/* ===== 输入区 ===== */
.ai-input-area {
  padding: 12px 16px 16px;
  border-top: 1px solid #eef0f4;
  flex-shrink: 0;
}

.ai-input-wrap {
  display: flex;
  gap: 8px;
  align-items: flex-end;
}

.ai-input {
  flex: 1;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1.5;
  resize: none;
  outline: none;
  font-family: inherit;
  max-height: 120px;
  transition: border-color .2s;
  background: #f7f8fa;
}

.ai-input:focus {
  border-color: #409EFF;
  background: #fff;
}

.ai-send-btn {
  height: 40px;
  width: 40px;
  padding: 0;
  border-radius: 10px;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a73e8, #0d47a1);
  border: none;
}

.ai-send-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(26, 115, 232, .35);
}

.ai-input-tip {
  font-size: 11px;
  color: #a0aec0;
  text-align: center;
  margin-top: 8px;
}

/* ===== 动画 ===== */
.slide-enter-active, .slide-leave-active {
  transition: opacity .25s;
}

.slide-leave-active .ai-panel, .slide-enter-active .ai-panel {
  transition: transform .25s ease-out;
}

.slide-enter-from, .slide-leave-to {
  opacity: 0;
}

.slide-enter-from .ai-panel, .slide-leave-to .ai-panel {
  transform: translateX(100%);
}

.history-slide-enter-active, .history-slide-leave-active {
  transition: all .2s ease;
}

.history-slide-enter-from, .history-slide-leave-to {
  width: 0;
  opacity: 0;
  overflow: hidden;
  padding: 0;
}

/* ===== 暗黑 ===== */
:root.theme-dark .ai-panel {
  background: #1d1e1f;
}

:root.theme-dark .ai-messages {
  background: #141414;
}

:root.theme-dark .ai-header, :root.theme-dark .ai-input-area, :root.theme-dark .ai-history {
  border-color: #333;
}

:root.theme-dark .ai-header-title, :root.theme-dark .ai-history-header, :root.theme-dark .ai-history-item-title {
  color: #e0e0e0;
}

:root.theme-dark .ai-history {
  background: #1a1b1c;
}

:root.theme-dark .ai-history-item:hover {
  background: #252627;
}

:root.theme-dark .ai-history-item.active {
  background: rgba(64, 158, 255, .12);
}

:root.theme-dark .ai-msg-row.assistant .ai-msg-bubble {
  background: #252627;
  color: #e0e0e0;
  border-color: #333;
}

:root.theme-dark .ai-input {
  background: #252627;
  border-color: #333;
  color: #e0e0e0;
}

:root.theme-dark .ai-input:focus {
  background: #2a2b2c;
  border-color: #409EFF;
}

:root.theme-dark .ai-suggestion-tag {
  background: #252627;
  border-color: #333;
  color: #ccc;
}

:root.theme-dark .ai-empty-title {
  color: #e0e0e0;
}

:root.theme-dark .ai-empty-desc {
  color: #999;
}

:root.theme-dark .ai-header-btn:hover, :root.theme-dark .ai-header-btn.active {
  background: #2a2b2c;
}

:root.theme-dark .ai-header-btn.disabled:hover {
  background: transparent;
}

:root.theme-dark .ai-export-btn {
  color: #5c9cff;
}

:root.theme-dark .ai-export-btn:hover {
  color: #8cbaff;
}
</style>
