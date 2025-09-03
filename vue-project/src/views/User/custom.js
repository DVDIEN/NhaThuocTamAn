// custom.js - hiển thị thời gian hiện tại khi tải trang

function updateTime() {
  const now = new Date()

  const timeStr = now.toLocaleTimeString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  })

  const dateStr = now.toLocaleDateString('vi-VN', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })

  const clockElement = document.getElementById('clock')
  if (clockElement) {
    clockElement.innerHTML = `<strong>${timeStr}</strong><br/><small>${dateStr}</small>`
  }
}

document.addEventListener('DOMContentLoaded', () => {
  updateTime() // ⏱ chỉ cập nhật 1 lần
})
