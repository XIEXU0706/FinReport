<template>
  <div class="layout">
    <div class="sidebar-container" :class="{ collapse: isCollapse }">
      <Sidebar :isCollapse="isCollapse" />
    </div>
    <div class="main-container" :class="{ collapse: isCollapse }">
      <Header :isCollapse="isCollapse" @toggle="isCollapse = !isCollapse" />
      <div class="app-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import Sidebar from './Sidebar'
import Header from './Header'
export default {
  name: 'Layout',
  components: { Sidebar, Header },
  data() {
    return { isCollapse: false }
  }
}
</script>

<style scoped>
.layout {
  height: 100vh;
  display: flex;
  overflow: hidden;
}
.sidebar-container {
  width: 210px;
  background: #304156;
  transition: width .28s;
  overflow: hidden;
  flex-shrink: 0;
}
.sidebar-container.collapse { width: 64px; }
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.app-main {
  flex: 1;
  padding: 20px;
  background: #f0f2f5;
  overflow-y: auto;
}
</style>
