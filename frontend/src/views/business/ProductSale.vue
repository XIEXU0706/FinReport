<template>
  <div class="page">
    <el-card shadow="never">
      <div slot="header">
        <el-button type="primary" size="small" @click="$router.push('/business/product')">← 返回产品管理</el-button>
        <span style="margin-left:12px;font-weight:600">产品销售记录</span>
      </div>
      <el-table :data="list" stripe size="small" v-loading="loading" style="width:100%">
        <el-table-column prop="saleNo" label="销售编号" width="120"/>
        <el-table-column prop="productNo" label="产品编号" width="90"/>
        <el-table-column prop="productName" label="产品名称" width="150"/>
        <el-table-column prop="customerNo" label="客户编号" width="100"/>
        <el-table-column prop="customerName" label="客户名称" width="100"/>
        <el-table-column prop="amount" label="金额" width="120">
          <template slot-scope="{row}">{{ row.amount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="saleDate" label="销售日期" width="110"/>
        <el-table-column prop="branchName" label="支行" width="120"/>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{row}">
            <el-tag :type="{NORMAL:'success'}[row.status]||'info'" size="mini">{{
                {NORMAL: '正常', CANCELLED: '已撤销'}[row.status] || row.status
              }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination @current-change="getData" :current-page="page" :page-size="size" :total="total"
                     layout="total,prev,pager,next" style="margin-top:12px;text-align:right"/>
    </el-card>
  </div>
</template>

<script>
import {salePage} from '@/api/business/prod-sale'

export default {
  name: 'ProductSale',
  data() {
    return {
      list: [], total: 0, page: 1, size: 10, loading: false
    }
  },
  mounted() {
    this.getData()
  },
  methods: {
    async getData(p) {
      if (p) this.page = p
      this.loading = true
      const res = await salePage({page: this.page, size: this.size})
      this.list = res.data.records
      this.total = res.data.total
      this.loading = false
    }
  }
}
</script>
