<template>
  <div class="header">
    <div class="header-left">
      <i :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'" @click="$emit('toggle')" class="toggle-btn"></i>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="header-right">
      <!-- AI 助手 -->
      <el-tooltip content="智慧AI助手" placement="bottom">
        <span class="header-icon robot-icon" @click="openAI">
          <img :src="robotImg" width="20" height="20" alt="AI">
        </span>
      </el-tooltip>

      <!-- 全屏 -->
      <el-tooltip :content="isFullscreen ? '退出全屏' : '全屏'" placement="bottom">
        <span class="header-icon" @click="toggleFullscreen">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
            <path v-if="!isFullscreen" d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"/>
            <path v-else d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z"/>
          </svg>
        </span>
      </el-tooltip>

      <!-- 主题切换 -->
      <el-tooltip :content="isDark ? '白天模式' : '夜间模式'" placement="bottom">
        <span class="header-icon" @click="toggleTheme">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
            <path v-if="!isDark" d="M12 3c-4.97 0-9 4.03-9 9s4.03 9 9 9 9-4.03 9-9c0-.46-.04-.92-.1-1.36-.98 1.37-2.58 2.26-4.4 2.26-2.98 0-5.4-2.42-5.4-5.4 0-1.81.89-3.42 2.26-4.4-.44-.06-.9-.1-1.36-.1z"/>
            <path v-else d="M12 3c.46 0 .93.04 1.38.12-1.84 1.12-3.08 3.14-3.08 5.48 0 3.54 2.86 6.4 6.4 6.4 2.34 0 4.36-1.24 5.48-3.08.08.45.12.92.12 1.38 0 4.97-4.03 9-9 9s-9-4.03-9-9 4.03-9 9-9z"/>
          </svg>
        </span>
      </el-tooltip>

      <el-dropdown trigger="click" @command="handleCommand">
        <span class="user-info">
          <i class="el-icon-user-solid"></i> {{ username || '用户' }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="profile">个人中心</el-dropdown-item>
          <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <AIChatPanel :visible="showAI" @close="showAI = false"/>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import robotImg from '@/static/robot.png'
import AIChatPanel from '@/components/AIChatPanel'

export default {
  name: 'Header',
  components: { AIChatPanel },
  props: {
    isCollapse: Boolean
  },
  data() {
    return {
      robotImg,
      showAI: false,
      isFullscreen: false,
      isDark: localStorage.getItem('theme') === 'dark'
    }
  },
  computed: {
    ...mapState('user', ['username']),
    currentTitle() {
      return this.$route.meta?.title || ''
    }
  },
  mounted() {
    this.applyTheme()
    document.addEventListener('fullscreenchange', this.onFullscreenChange)
  },
  beforeDestroy() {
    document.removeEventListener('fullscreenchange', this.onFullscreenChange)
  },
  methods: {
    handleCommand(command) {
      if (command === 'profile') {
        this.$router.push('/system/profile')
      } else if (command === 'logout') {
        this.$store.dispatch('user/logout')
        this.$router.push('/login')
      }
    },
    openAI() {
      this.showAI = true
    },
    toggleFullscreen() {
      if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen()
      } else {
        document.exitFullscreen()
      }
    },
    onFullscreenChange() {
      this.isFullscreen = !!document.fullscreenElement
    },
    toggleTheme() {
      this.isDark = !this.isDark
      localStorage.setItem('theme', this.isDark ? 'dark' : 'light')
      this.applyTheme()
    },
    applyTheme() {
      document.body.className = this.isDark ? 'theme-dark' : 'theme-light'
    }
  }
}
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 50px;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  box-shadow: 0 1px 4px rgba(0,0,0,.08);
  transition: background .3s, border-color .3s;
}
.theme-dark .header {
  background: #1d1e1f;
  border-bottom-color: #333;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.toggle-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}
.toggle-btn:hover { color: #409EFF; }
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  color: #606266;
  transition: all .2s;
}
.header-icon:hover {
  background: rgba(64,158,255,.1);
  color: #409EFF;
}
.user-info { color: #606266; font-size: 14px; cursor: pointer; }
.robot-icon {
  animation: bounce 10s ease-in-out infinite;
}
.robot-icon:hover {
  animation-play-state: paused;
  background: rgba(64,158,255,.12) !important;
}
.robot-icon img {
  display: block;
}
@keyframes bounce {
  0%, 90%, 100% { transform: translateY(0); }
  95% { transform: translateY(-6px); }
}
</style>
