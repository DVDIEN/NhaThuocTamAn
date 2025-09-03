<template>
  <div class="chatbot-wrapper">
    <div class="chat-box" ref="chatBox">
      <div v-for="(msg, index) in messages" :key="index" class="chat-message">
        <!-- User -->
        <div v-if="msg.sender === 'user'" class="message user">
          <span>{{ msg.text }}</span>
        </div>

        <!-- Bot -->
        <div v-else class="message bot">
          <span>{{ msg.text }}</span>
        </div>
      </div>
    </div>

    <div class="chat-input">
      <input
        v-model="userInput"
        @keyup.enter="sendMessage"
        type="text"
        placeholder="Nhập câu hỏi..."
      />
      <button @click="sendMessage">Gửi</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ChatBot',
  data() {
    return {
      userInput: '',
      messages: [],
    }
  },
  mounted() {
    // ✅ Lời chào mặc định
    this.messages.push({
      sender: 'bot',
      text: 'Xin chào 👋! Tôi là trợ lý của Nhà thuốc Tâm An. Tôi có thể giúp gì cho bạn?',
    })
  },
  methods: {
    async sendMessage() {
      if (!this.userInput.trim()) return

      this.messages.push({ sender: 'user', text: this.userInput })

      try {
        const res = await axios.post('http://localhost:8080/api/chatbot/ask', {
          message: this.userInput,
        })

        this.messages.push({
          sender: 'bot',
          text: res.data.reply || 'Xin lỗi, không có phản hồi.',
        })
      } catch (err) {
        console.error(err)
        this.messages.push({
          sender: 'bot',
          text: '⚠️ Lỗi kết nối tới server!',
        })
      }

      this.userInput = ''

      // ✅ Tự động scroll xuống cuối
      this.$nextTick(() => {
        const box = this.$refs.chatBox
        box.scrollTop = box.scrollHeight
      })
    },
  },
}
</script>

<style scoped>
.chatbot-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fb;
  border-radius: 10px;
  border: 1px solid #ddd;
  font-family: 'Segoe UI', sans-serif;
}

.chat-box {
  flex: 1;
  padding: 12px;
  overflow-y: auto; /* ✅ Thanh cuộn */
  max-height: 400px; /* Giới hạn chiều cao */
  scrollbar-width: thin;
  scrollbar-color: #bbb transparent;
}

.chat-box::-webkit-scrollbar {
  width: 6px;
}

.chat-box::-webkit-scrollbar-thumb {
  background-color: #bbb;
  border-radius: 10px;
}

.chat-message {
  display: flex;
  margin: 6px 0;
}

.message {
  padding: 10px 14px;
  border-radius: 18px;
  max-width: 75%;
  line-height: 1.4;
  word-break: break-word;
  font-size: 14px;
}

.user {
  margin-left: auto;
  background: #28a745;
  color: white;
  border-bottom-right-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.bot {
  margin-right: auto;
  background: #ffffff;
  color: #333;
  border: 1px solid #ddd;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.chat-input {
  display: flex;
  border-top: 1px solid #ddd;
  padding: 8px;
  background: #fff;
}

.chat-input input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 20px;
  outline: none;
  font-size: 14px;
  margin-right: 8px;
}

.chat-input input:focus {
  border-color: #28a745;
}

.chat-input button {
  background: #28a745;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 0 16px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.chat-input button:hover {
  background: #1e7e34;
}
</style>
