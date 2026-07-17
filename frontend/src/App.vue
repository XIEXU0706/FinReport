<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
body {
  margin: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  transition: background .3s, color .3s;
}

/* 夜间模式全局变量 */
body.theme-dark {
  background: #141414;
  color: #e0e0e0;
}
body.theme-dark .el-card {
  background: #1d1e1f !important;
  border-color: #333 !important;
  color: #e0e0e0;
}
body.theme-dark .el-table {
  background: #1d1e1f !important;
  color: #e0e0e0;
}
body.theme-dark .el-table th {
  background: #252627 !important;
  color: #e0e0e0;
}
body.theme-dark .el-table tr {
  background: #1d1e1f !important;
}
body.theme-dark .el-table--striped .el-table__body tr.el-table__row--striped td {
  background: #252627 !important;
}
body.theme-dark .el-table__body tr:hover > td {
  background: #2a2b2c !important;
}
body.theme-dark .el-pagination {
  color: #e0e0e0;
}
body.theme-dark .el-input__inner {
  background: #252627 !important;
  border-color: #333 !important;
  color: #e0e0e0;
}
body.theme-dark .el-dialog {
  background: #1d1e1f !important;
  border-color: #333 !important;
}
body.theme-dark .el-dialog__title {
  color: #e0e0e0;
}
body.theme-dark .el-form-item__label {
  color: #ccc;
}
body.theme-dark .el-select-dropdown {
  background: #1d1e1f !important;
  border-color: #333 !important;
}
body.theme-dark .el-select-dropdown__item {
  color: #e0e0e0 !important;
}
body.theme-dark .el-select-dropdown__item.hover {
  background: #252627 !important;
}
body.theme-dark .el-table__empty-text {
  color: #999;
}
body.theme-dark .el-breadcrumb {
  color: #999;
}
body.theme-dark .el-breadcrumb__inner {
  color: #ccc !important;
}
</style>
