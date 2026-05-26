<template>
  <div class="chat-input">
    <div class="input-shell" :class="{ 'is-disabled': disabled }">
      <div class="input-container">
        <textarea
          ref="inputRef"
          v-model="inputMessage"
          :placeholder="placeholder"
          :disabled="disabled"
          class="input-textarea"
          rows="1"
          @keydown="handleKeyDown"
          @input="adjustHeight"
        />

        <button
          :disabled="disabled || !inputMessage.trim()"
          class="send-button"
          title="发送消息"
          aria-label="发送消息"
          @click="sendMessage"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 11.4 20.4 4.2c.9-.4 1.8.5 1.4 1.4l-7.2 17.4c-.4 1-1.9.9-2.2-.1l-1.7-5.9-5.9-1.7c-1-.3-1.1-1.8-.1-2.2Z" fill="currentColor" />
          </svg>
        </button>
      </div>

      <div class="input-meta">
        <span>Enter 发送，Shift + Enter 换行</span>
        <span>{{ disabled ? 'AI 正在回复中' : '支持多行输入与代码片段粘贴' }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatInput',
  props: {
    disabled: {
      type: Boolean,
      default: false
    },
    placeholder: {
      type: String,
      default: '请输入你的问题...'
    }
  },
  data() {
    return {
      inputMessage: ''
    }
  },
  methods: {
    sendMessage() {
      if (this.inputMessage.trim() && !this.disabled) {
        this.$emit('send-message', this.inputMessage.trim())
        this.inputMessage = ''
        this.adjustHeight()
      }
    },
    handleKeyDown(event) {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        this.sendMessage()
      }
    },
    adjustHeight() {
      this.$nextTick(() => {
        const textarea = this.$refs.inputRef

        if (!textarea) {
          return
        }

        textarea.style.height = 'auto'
        textarea.style.height = `${Math.min(textarea.scrollHeight, 160)}px`
      })
    },
    focus() {
      this.$refs.inputRef.focus()
    }
  },
  mounted() {
    this.adjustHeight()
  }
}
</script>

<style scoped>
.chat-input {
  position: relative;
  padding: 18px 22px 22px;
  border-top: 1px solid rgba(216, 225, 236, 0.92);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.58), rgba(248, 250, 252, 0.94));
  backdrop-filter: blur(14px);
}

.input-shell {
  max-width: 920px;
  margin: 0 auto;
  padding: 14px 16px 12px;
  border: 1px solid rgba(216, 225, 236, 0.96);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: var(--shadow-md);
  transition:
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    transform var(--transition-base);
}

.input-shell:focus-within {
  border-color: rgba(37, 99, 235, 0.36);
  box-shadow: 0 20px 34px rgba(37, 99, 235, 0.14);
  transform: translateY(-1px);
}

.input-shell.is-disabled {
  background: rgba(248, 250, 252, 0.94);
}

.input-container {
  display: flex;
  align-items: flex-end;
  gap: 14px;
}

.input-textarea {
  flex: 1;
  min-height: 46px;
  max-height: 160px;
  padding: 9px 4px 6px;
  border: none;
  background: transparent;
  color: var(--text-primary);
  font-size: 15px;
  line-height: 1.7;
  resize: none;
  outline: none;
  overflow-y: auto;
}

.input-textarea::placeholder {
  color: var(--text-muted);
}

.input-textarea:disabled {
  cursor: not-allowed;
  color: var(--text-muted);
}

.send-button {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
  display: grid;
  place-items: center;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, var(--brand-500), #4f7cff);
  color: #ffffff;
  cursor: pointer;
  box-shadow: 0 16px 28px rgba(37, 99, 235, 0.28);
  transition:
    transform var(--transition-base),
    box-shadow var(--transition-base),
    filter var(--transition-base);
}

.send-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 18px 32px rgba(37, 99, 235, 0.34);
  filter: saturate(1.08);
}

.send-button:focus-visible {
  outline: 3px solid rgba(37, 99, 235, 0.18);
  outline-offset: 3px;
}

.send-button:disabled {
  background: linear-gradient(135deg, #c9d3df, #b0bccb);
  box-shadow: none;
  cursor: not-allowed;
}

.input-meta {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: var(--text-muted);
  font-size: 12px;
}

@media (max-width: 768px) {
  .chat-input {
    padding: 14px 14px 16px;
  }

  .input-shell {
    padding: 12px 14px 10px;
    border-radius: 20px;
  }

  .input-container {
    gap: 10px;
  }

  .input-textarea {
    font-size: 16px;
  }

  .send-button {
    width: 44px;
    height: 44px;
    border-radius: 14px;
  }

  .input-meta {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
