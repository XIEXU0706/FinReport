<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="query" inline size="small">
        <el-form-item label="操作人">
          <el-input v-model="query.operName" clearable @keyup.enter="search"/>
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="query.businessType" clearable style="width:130px" @change="search">
            <el-option label="新增" value="INSERT"/>
            <el-option label="修改" value="UPDATE"/>
            <el-option label="删除" value="DELETE"/>
            <el-option label="查询" value="SELECT"/>
            <el-option label="导出" value="EXPORT"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" style="margin-top:12px">
      <el-table :data="list" stripe style="width:100%" v-loading="loading">
        <el-table-column prop="title" label="操作标题" width="140"/>
        <el-table-column prop="businessType" label="业务类型" width="80">
          <template slot-scope="{row}">{{
              {
                INSERT: '新增',
                UPDATE: '修改',
                DELETE: '删除',
                SELECT: '查询',
                EXPORT: '导出'
              }[row.businessType] || row.businessType
            }}
          </template>
        </el-table-column>
        <el-table-column prop="operName" label="操作人" width="90"/>
        <el-table-column prop="operIp" label="IP地址" width="130"/>
        <el-table-column prop="operUrl" label="请求地址" min-width="200"/>
        <el-table-column prop="requestMethod" label="请求方式" width="80"/>
        <el-table-column prop="status" label="状态" width="60">
          <template slot-scope="{row}">
            <el-tag :type="row.status==='SUCCESS'?'success':'danger'" size="mini">{{
                row.status === 'SUCCESS' ? '成功' : '失败'
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="costTime" label="耗时(ms)" width="80"/>
        <el-table-column prop="operTime" label="操作时间" width="140" :formatter="fmtDate"/>
      </el-table>
      <el-pagination @current-change="getData" :current-page="page" :page-size="size" :total="total"
                     layout="total,prev,pager,next" style="margin-top:12px;text-align:right"/>
    </el-card>
  </div>
</template>

<script>
import {page as pageApi} from '@/api/system/oper-log'

export default {
  name: 'OperLog',
  data() {
    return {
      list: [], total: 0, page: 1, size: 10, loading: false,
      query: {operName: '', businessType: ''}
    }
  },
  mounted() {
    this.getData()
  },
  methods: {
    async getData(p) {
      if (p) this.page = p
      this.loading = true
      const res = await pageApi({page: this.page, pageSize: this.size, ...this.query})
      this.list = res.data.records;
      this.total = res.data.total
      this.loading = false
    },
    search() {
      this.page = 1;
      this.getData()
    },
    reset() {
      this.query = {operName: '', businessType: ''};
      this.search()
    },
    fmtDate(row, col, val) {
      return val ? val.replace('T', ' ') : ''
    }
  }
}
</script>
