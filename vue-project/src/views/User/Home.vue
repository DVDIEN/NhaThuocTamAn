<template>
  <div>
    <Header />
    <Navbar />
    <BannerSlide />
    <Banner />
    <FlashSale />
    <Banner2 />
    <SanPhamBanChay />
    <News />
    <Footer />

    <!-- Floating chat -->
    <div class="chatbot-floating">
      <button class="chat-toggle" @click="toggleChat">💬</button>

      <div v-if="showChat" class="chat-window">
        <div class="chat-header">
          <span>Chatbot Tư Vấn</span>
          <button class="close-btn" @click="toggleChat">×</button>
        </div>
        <ChatBot />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Header from './Header.vue'
import Navbar from './Navbar.vue'
import BannerSlide from './BannerSlide.vue'
import Banner from './Banner.vue'
import Banner2 from './Banner2.vue'
import FlashSale from '../DanhMucThuoc/FlashSale.vue'
import SanPhamBanChay from '../DanhMucThuoc/SanPhamBanChay.vue'
import News from './News.vue'
import Footer from './Footer.vue'
import ChatBot from './ChatBot.vue'

const showChat = ref(false)
const toggleChat = () => {
  showChat.value = !showChat.value
}
</script>

<style scoped>
body {
  font-family: 'Segoe UI', sans-serif;
  background-color: #f8f9fa;
}

.chatbot-floating {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 999;
}

/* Nút mở chat */
.chat-toggle {
  background: #28a745;
  color: white;
  border: none;
  border-radius: 50%;
  width: 55px;
  height: 55px;
  font-size: 24px;
  cursor: pointer;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
  transition: background 0.3s ease;
}

.chat-toggle:hover {
  background: #1e7e34;
}

/* Hộp chat */
.chat-window {
  width: 350px;
  height: 500px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  position: absolute;
  bottom: 70px;
  right: 0;
}

/* Header chat */
.chat-header {
  background: #28a745;
  color: white;
  padding: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-btn {
  background: transparent;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
}
</style>
