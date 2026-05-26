<template>
  <div class="app">
    <div class="app-shell">
      <header class="app-header">
        <div class="header-copy">
          <span class="header-badge">AI Code Helper</span>
          <h1 class="app-title">AI 编程小助手</h1>
          <p class="app-subtitle">
            面向学习、排错与求职准备的智能编程对话助手，让问题拆解和代码理解更高效。
          </p>
        </div>
        <div class="header-status" :class="statusTone">
          <span class="status-dot"></span>
          <span>{{ statusText }}</span>
        </div>
      </header>

      <section class="chat-panel">
        <div class="messages-container" ref="messagesContainer">
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="welcome-card">
              <span class="welcome-kicker">开始对话</span>
              <div class="welcome-icon">AI</div>
              <h2>欢迎来到你的编程工作台</h2>
              <p>
                你可以在这里提问代码问题、分析报错、准备面试，或者让我们一起拆解一个功能方案。
              </p>
              <div class="welcome-grid">
                <div
                  v-for="feature in welcomeFeatures"
                  :key="feature"
                  class="welcome-item"
                >
                  {{ feature }}
                </div>
              </div>
              <div class="welcome-tip">
                <span class="tip-dot"></span>
                <span>按 Enter 发送，Shift + Enter 换行。</span>
              </div>
            </div>
          </div>

          <ChatMessage
            v-for="message in messages"
            :key="message.id"
            :message="message.content"
            :is-user="message.isUser"
            :timestamp="message.timestamp"
          />

          <div v-if="isAiTyping" class="chat-message ai-message is-streaming">
            <div class="message-avatar">
              <div class="avatar ai-avatar">AI</div>
            </div>
            <div class="message-content">
              <div class="message-bubble">
                <div class="streaming-label">AI 正在生成回答</div>
                <div class="ai-typing-content">
                  <div
                    class="ai-response-text message-markdown"
                    v-html="currentAiResponseRendered"
                  ></div>
                  <LoadingDots v-if="isStreaming" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <ChatInput
          :disabled="isAiTyping"
          @send-message="sendMessage"
          placeholder="请输入你的编程问题，或贴出需要一起分析的代码片段..."
        />
      </section>
    </div>

    <div v-if="connectionError" class="connection-error" role="alert">
      <div class="error-content">
        <span class="error-badge">!</span>
        <span>连接服务失败，请确认后端服务已经启动。</span>
      </div>
    </div>
  </div>
</template>

<script>
import { marked } from 'marked'
import ChatInput from './components/ChatInput.vue'
import ChatMessage from './components/ChatMessage.vue'
import LoadingDots from './components/LoadingDots.vue'
import { chatWithSSE } from './api/chatApi.js'
import { generateMemoryId } from './utils/index.js'

export default {
  name: 'App',
  components: {
    ChatInput,
    ChatMessage,
    LoadingDots
  },
  data() {
    return {
      messages: [],
      memoryId: null,
      isAiTyping: false,
      isStreaming: false,
      currentAiResponse: '',
      currentEventSource: null,
      connectionError: false,
      welcomeFeatures: [
        '代码调试与报错定位',
        '算法与实现思路讲解',
        '面试问答与求职准备',
        '功能拆解与方案建议'
      ]
    }
  },
  computed: {
    currentAiResponseRendered() {
      if (!this.currentAiResponse) return ''

      marked.setOptions({
        breaks: true,
        gfm: true,
        sanitize: false,
        highlight(code) {
          return code
        }
      })

      return marked(this.currentAiResponse)
    },
    statusText() {
      if (this.connectionError) {
        return '服务连接异常'
      }

      if (this.isAiTyping) {
        return 'AI 正在回复'
      }

      return '准备就绪'
    },
    statusTone() {
      if (this.connectionError) {
        return 'is-danger'
      }

      if (this.isAiTyping) {
        return 'is-busy'
      }

      return 'is-ready'
    }
  },
  methods: {
    sendMessage(message) {
      this.addMessage(message, true)
      this.startAiResponse(message)
    },
    addMessage(content, isUser = false) {
      const message = {
        id: Date.now() + Math.random(),
        content,
        isUser,
        timestamp: new Date()
      }

      this.messages.push(message)
      this.scrollToBottom()
    },
    startAiResponse(userMessage) {
      this.isAiTyping = true
      this.isStreaming = true
      this.currentAiResponse = ''
      this.connectionError = false

      if (this.currentEventSource) {
        this.currentEventSource.close()
      }

      this.currentEventSource = chatWithSSE(
        this.memoryId,
        userMessage,
        this.handleAiMessage,
        this.handleAiError,
        this.handleAiClose
      )
    },
    handleAiMessage(data) {
      this.currentAiResponse += data
      this.scrollToBottom()
    },
    handleAiError(error) {
      console.error('AI 回复出错:', error)
      this.connectionError = true
      this.finishAiResponse()

      setTimeout(() => {
        this.connectionError = false
      }, 5000)
    },
    handleAiClose() {
      this.finishAiResponse()
    },
    finishAiResponse() {
      this.isStreaming = false

      if (this.currentAiResponse.trim()) {
        this.addMessage(this.currentAiResponse.trim(), false)
      }

      this.isAiTyping = false
      this.currentAiResponse = ''
      this.connectionError = false

      if (this.currentEventSource) {
        this.currentEventSource.close()
        this.currentEventSource = null
      }
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer

        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },
    initializeChat() {
      this.memoryId = generateMemoryId()
      console.log('聊天室 ID:', this.memoryId)
    }
  },
  mounted() {
    this.initializeChat()
  },
  beforeUnmount() {
    if (this.currentEventSource) {
      this.currentEventSource.close()
    }
  }
}
</script>

<style scoped>
.app {
  min-height: 100dvh;
  padding: 24px;
}

.app-shell {
  height: calc(100dvh - 48px);
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.app-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  padding: 24px 28px;
  border: 1px solid rgba(255, 255, 255, 0.72);
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: var(--shadow-md);
  backdrop-filter: blur(18px);
}

.header-copy {
  max-width: 760px;
}

.header-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.1);
  color: var(--brand-700);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.app-title {
  margin: 14px 0 8px;
  font-size: clamp(30px, 4vw, 42px);
  line-height: 1.1;
  letter-spacing: -0.03em;
  color: var(--text-primary);
}

.app-subtitle {
  margin: 0;
  max-width: 620px;
  font-size: 15px;
  line-height: 1.7;
  color: var(--text-secondary);
}

.header-status {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: currentColor;
}

.is-ready {
  color: var(--accent-500);
  background: rgba(15, 118, 110, 0.12);
}

.is-ready .status-dot {
  box-shadow: 0 0 0 6px rgba(15, 118, 110, 0.12);
}

.is-busy {
  color: var(--brand-700);
  background: rgba(37, 99, 235, 0.12);
}

.is-busy .status-dot {
  box-shadow: 0 0 0 6px rgba(37, 99, 235, 0.12);
  animation: pulse-ring 1.6s infinite;
}

.is-danger {
  color: #b42318;
  background: rgba(239, 68, 68, 0.12);
}

.is-danger .status-dot {
  box-shadow: 0 0 0 6px rgba(239, 68, 68, 0.12);
}

.chat-panel {
  position: relative;
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid rgba(216, 225, 236, 0.92);
  border-radius: 30px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.88), rgba(244, 247, 252, 0.96));
  box-shadow: var(--shadow-lg);
}

.chat-panel::before {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at top, rgba(255, 255, 255, 0.55), transparent 42%),
    linear-gradient(180deg, rgba(37, 99, 235, 0.04), transparent 32%);
}

.messages-container {
  position: relative;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 28px 0 24px;
}

.welcome-message {
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28px;
}

.welcome-card {
  width: min(720px, 100%);
  padding: 34px 32px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.84);
  box-shadow: var(--shadow-md);
  text-align: center;
  backdrop-filter: blur(16px);
}

.welcome-kicker {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(15, 118, 110, 0.1);
  color: var(--accent-500);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.welcome-icon {
  width: 74px;
  height: 74px;
  margin: 18px auto 20px;
  display: grid;
  place-items: center;
  border-radius: 24px;
  background: linear-gradient(135deg, var(--brand-500), #0f766e);
  color: #ffffff;
  font-size: 28px;
  font-weight: 800;
  box-shadow: 0 18px 34px rgba(37, 99, 235, 0.28);
}

.welcome-card h2 {
  margin: 0 0 12px;
  font-size: clamp(24px, 3.2vw, 34px);
  line-height: 1.15;
  letter-spacing: -0.03em;
  color: var(--text-primary);
}

.welcome-card p {
  margin: 0 auto;
  max-width: 560px;
  font-size: 15px;
  line-height: 1.75;
  color: var(--text-secondary);
}

.welcome-grid {
  margin-top: 24px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.welcome-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border: 1px solid rgba(219, 228, 240, 0.92);
  border-radius: 18px;
  background: rgba(248, 250, 252, 0.9);
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 600;
  text-align: left;
}

.welcome-item::before {
  content: '';
  width: 8px;
  height: 8px;
  flex-shrink: 0;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--brand-500), #0f766e);
}

.welcome-tip {
  margin-top: 20px;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: var(--text-muted);
  font-size: 13px;
}

.tip-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: var(--brand-500);
}

.chat-message {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  margin-bottom: 18px;
  padding: 0 28px;
}

.message-avatar {
  display: flex;
  align-items: flex-start;
}

.avatar {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  color: #ffffff;
  font-size: 13px;
  font-weight: 800;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.12);
}

.ai-avatar {
  background: linear-gradient(135deg, var(--brand-500), #0f766e);
}

.message-content {
  max-width: min(780px, calc(100% - 56px));
  min-width: 0;
}

.message-bubble {
  padding: 16px 18px;
  border: 1px solid rgba(216, 225, 236, 0.94);
  border-radius: 22px 22px 22px 8px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: var(--shadow-sm);
}

.streaming-label {
  margin-bottom: 10px;
  color: var(--text-muted);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.ai-typing-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ai-response-text {
  min-height: 24px;
  color: var(--text-primary);
}

.connection-error {
  position: fixed;
  left: 50%;
  bottom: 24px;
  transform: translateX(-50%);
  z-index: 20;
  animation: toast-in 220ms ease-out;
}

.error-content {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  border: 1px solid rgba(248, 113, 113, 0.35);
  border-radius: 18px;
  background: rgba(127, 29, 29, 0.92);
  color: #ffffff;
  box-shadow: 0 18px 32px rgba(127, 29, 29, 0.24);
}

.error-badge {
  width: 22px;
  height: 22px;
  display: grid;
  place-items: center;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  font-size: 13px;
  font-weight: 800;
}

@keyframes pulse-ring {
  0%,
  100% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.08);
  }
}

@keyframes toast-in {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(12px);
  }

  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

@media (max-width: 980px) {
  .app {
    padding: 16px;
  }

  .app-shell {
    height: calc(100dvh - 32px);
  }

  .app-header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-status {
    width: fit-content;
  }
}

@media (max-width: 768px) {
  .app {
    padding: 12px;
  }

  .app-shell {
    height: calc(100dvh - 24px);
    gap: 12px;
  }

  .app-header {
    padding: 18px;
    border-radius: 22px;
  }

  .messages-container {
    padding: 20px 0 18px;
  }

  .welcome-message {
    padding: 16px;
  }

  .welcome-card {
    padding: 24px 18px;
    border-radius: 24px;
  }

  .welcome-grid {
    grid-template-columns: 1fr;
  }

  .chat-message {
    padding: 0 16px;
  }

  .message-content {
    max-width: calc(100% - 56px);
  }

  .connection-error {
    left: 12px;
    right: 12px;
    bottom: 12px;
    transform: none;
  }

  .error-content {
    display: flex;
    justify-content: center;
  }
}
</style>
