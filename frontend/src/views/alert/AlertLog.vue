<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="query" inline size="small">
        <el-form-item label="严重程度">
          <el-select v-model="query.severity" clearable style="width:120px">
            <el-option label="紧急" value="CRITICAL"/>
            <el-option label="高" value="HIGH"/>
            <el-option label="中" value="MEDIUM"/>
            <el-option label="低" value="LOW"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable style="width:120px">
            <el-option label="未处理" value="NEW"/>
            <el-option label="已确认" value="ACKNOWLEDGED"/>
            <el-option label="已处理" value="RESOLVED"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" style="margin-top:12px">
      <div style="margin-bottom:8px">
        <el-badge :value="unresolvedCount" class="unresolved-badge">
          <el-tag type="danger">未处理预警</el-tag>
        </el-badge>
      </div>
      <el-table :data="list" stripe style="width:100%" v-loading="loading">
        <el-table-column prop="ruleName" label="规则名称" width="140"/>
        <el-table-column prop="content" label="预警内容" min-width="220"/>
        <el-table-column prop="actualValue" label="实际值" width="100">
          <template slot-scope="{row}">{{ row.actualValue?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="threshold" label="阈值" width="80"/>
        <el-table-column prop="severity" label="严重程度" width="90">
          <template slot-scope="{row}">
            <el-tag :type="{CRITICAL:'danger',HIGH:'warning',MEDIUM:'info',LOW:'success'}[row.severity]" size="mini">
              {{ {CRITICAL: '紧急', HIGH: '高', MEDIUM: '中', LOW: '低'}[row.severity] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template slot-scope="{row}">
            <el-tag :type="{NEW:'danger',ACKNOWLEDGED:'warning',RESOLVED:'success'}[row.status]" size="mini">
              {{ {NEW: '未处理', ACKNOWLEDGED: '已确认', RESOLVED: '已处理'}[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="触发时间" width="150">
          <template slot-scope="{row}">{{ row.createdAt?.replace('T', ' ') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" v-if="row.status==='NEW'" @click="ack(row)" type="warning">确认</el-button>
            <el-button size="mini" v-if="row.status!=='RESOLVED'" @click="resolve(row)" type="success">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination @current-change="getData" :current-page="page" :page-size="size" :total="total"
                     layout="total,prev,pager,next" style="margin-top:12px;text-align:right"/>
    </el-card>
  </div>
</template>

<script>
import {page as pageApi, count as countApi, ack as ackApi, resolve as resolveApi} from '@/api/alert/log'

export default {
  name: 'AlertLog',
  data() {
    return {
      list: [], total: 0, page: 1, size: 10, loading: false,
      query: {severity: '', status: ''}, unresolvedCount: 0
    }
  },
  mounted() {
    this.getData();
    this.getUnresolvedCount()
  },
  methods: {
    async getData(p) {
      if (p) this.page = p
      this.loading = true
      const res = await pageApi({page: this.page, size: this.size, ...this.query})
      this.list = res.data.records;
      this.total = res.data.total
      this.loading = false
    },
    async getUnresolvedCount() {
      const res = await countApi()
      this.unresolvedCount = res.data
    },
    search() {
      this.page = 1;
      this.getData()
    },
    reset() {
      this.query = {severity: '', status: ''};
      this.search()
    },
    async ack(row) {
      await ackApi(row.id)
      this.$message.success('已确认');
      this.getData();
      this.getUnresolvedCount()
    },
    async resolve(row) {
      await resolveApi(row.id)
      this.$message.success('已处理');
      this.getData();
      this.getUnresolvedCount()
    }
  }
}
</script>

<style>
.unresolved-badge .el-badge__content {
  background: #F56C6C;
}
</style>
