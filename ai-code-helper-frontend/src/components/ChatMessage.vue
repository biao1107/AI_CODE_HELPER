<template>
  <div class="chat-message" :class="{ 'user-message': isUser, 'ai-message': !isUser }">
    <div class="message-avatar">
      <div class="avatar" :class="{ 'user-avatar': isUser, 'ai-avatar': !isUser }">
        {{ isUser ? '你' : 'AI' }}
      </div>
    </div>

    <div class="message-content">
      <div class="message-bubble">
        <pre v-if="isUser" class="message-text">{{ message }}</pre>
        <div v-else class="message-markdown" v-html="renderedMessage"></div>
      </div>
      <div class="message-time">{{ formatTime(timestamp) }}</div>
    </div>
  </div>
</template>

<script>
import { marked } from 'marked'
import { formatTime } from '../utils/index.js'

export default {
  name: 'ChatMessage',
  props: {
    message: {
      type: String,
      required: true
    },
    isUser: {
      type: Boolean,
      default: false
    },
    timestamp: {
      type: Date,
      default: () => new Date()
    }
  },
  computed: {
    renderedMessage() {
      if (this.isUser) {
        return this.message
      }

      marked.setOptions({
        breaks: true,
        gfm: true,
        sanitize: false,
        highlight(code) {
          return code
        }
      })

      return marked(this.message)
    }
  },
  methods: {
    formatTime
  }
}
</script>

<style scoped>
.chat-message {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  margin-bottom: 18px;
  padding: 0 28px;
  animation: message-in 220ms ease-out;
}

.user-message {
  justify-content: flex-end;
}

.user-message .message-avatar {
  order: 2;
}

.user-message .message-content {
  order: 1;
  align-items: flex-end;
}

.ai-message .message-avatar {
  order: 1;
}

.ai-message .message-content {
  order: 2;
  align-items: flex-start;
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

.user-avatar {
  background: linear-gradient(135deg, #0f172a, #334155);
}

.ai-avatar {
  background: linear-gradient(135deg, var(--brand-500), #0f766e);
}

.message-content {
  max-width: min(780px, calc(100% - 56px));
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.message-bubble {
  width: fit-content;
  max-width: 100%;
  padding: 14px 18px;
  border-radius: 22px;
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  word-break: break-word;
}

.user-message .message-bubble {
  background: linear-gradient(135deg, var(--brand-500), #4f7cff 110%);
  color: #ffffff;
  border-bottom-right-radius: 8px;
}

.ai-message .message-bubble {
  border: 1px solid rgba(216, 225, 236, 0.94);
  background: rgba(255, 255, 255, 0.88);
  color: var(--text-primary);
  border-bottom-left-radius: 8px;
}

.message-text {
  margin: 0;
  font: inherit;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-time {
  margin-top: 8px;
  padding: 0 6px;
  color: var(--text-muted);
  font-size: 12px;
}

.user-message .message-time {
  text-align: right;
}

.ai-message .message-time {
  text-align: left;
}

@keyframes message-in {
  from {
    opacity: 0;
    transform: translateY(12px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .chat-message {
    gap: 10px;
    padding: 0 16px;
  }

  .avatar {
    width: 38px;
    height: 38px;
    border-radius: 12px;
  }

  .message-content {
    max-width: calc(100% - 48px);
  }

  .message-bubble {
    padding: 13px 15px;
  }
}
</style>
