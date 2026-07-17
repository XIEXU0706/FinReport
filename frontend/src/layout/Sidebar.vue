<template>
  <div class="sidebar">
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      router>
      <div class="sidebar-logo">
        <svg viewBox="0 0 24 24" width="24" height="24" fill="#409EFF" style="vertical-align:middle;margin-right:6px;">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15l-5-5 1.41-1.41L11 14.17l7.59-7.59L20 8l-9 9z"/>
        </svg>
        <h2 v-show="!isCollapse">FinReport</h2>
        <h2 v-show="isCollapse">FR</h2>
      </div>
      <el-menu-item index="/dashboard">
        <i class="el-icon-s-home"></i>
        <span slot="title">首页</span>
      </el-menu-item>
      <el-submenu index="/business">
        <template slot="title">
          <i class="el-icon-s-data"></i>
          <span>业务数据</span>
        </template>
        <el-menu-item index="/business/customer">客户管理</el-menu-item>
        <el-menu-item index="/business/account">账户管理</el-menu-item>
        <el-menu-item index="/business/trans-log">交易流水</el-menu-item>
        <el-menu-item index="/business/loan">贷款业务</el-menu-item>
        <el-menu-item index="/business/product">产品管理</el-menu-item>
        <el-menu-item index="/business/branch">支行信息</el-menu-item>
      </el-submenu>
      <el-menu-item index="/analysis/center">
        <i class="el-icon-s-marketing"></i>
        <span slot="title">经营分析</span>
      </el-menu-item>
      <el-submenu index="/alert">
        <template slot="title">
          <i class="el-icon-warning"></i>
          <span>预警中心</span>
        </template>
        <el-menu-item index="/alert/log">预警列表</el-menu-item>
        <el-menu-item index="/alert/rule">规则配置</el-menu-item>
      </el-submenu>
      <el-submenu index="/report">
        <template slot="title">
          <i class="el-icon-document"></i>
          <span>报表中心</span>
        </template>
        <el-menu-item index="/report/history">我的报表</el-menu-item>
        <el-menu-item index="/report/task">自动报表任务</el-menu-item>
      </el-submenu>
      <el-submenu index="/system">
        <template slot="title">
          <i class="el-icon-setting"></i>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/system/user">用户管理</el-menu-item>
        <el-menu-item index="/system/role">角色管理</el-menu-item>
        <el-menu-item index="/system/oper-log">操作日志</el-menu-item>
        <el-menu-item index="/system/profile">个人中心</el-menu-item>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: 'Sidebar',
  props: {
    isCollapse: Boolean
  },
  computed: {
    activeMenu() {
      const path = this.$route.path
      return path
    }
  }
}
</script>

<style scoped>
.sidebar {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
}
.sidebar-logo {
  height: 56px;
  line-height: 56px;
  text-align: center;
  color: #fff;
  background: #263445;
  border-bottom: 1px solid #1f2d3d;
}
.sidebar-logo h2 {
  display: inline;
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  vertical-align: middle;
}
.el-menu {
  border-right: none;
}
</style>
