<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="query" inline size="small">
        <el-form-item label="客户编号">
          <el-select v-model="query.customerNo" clearable filterable style="width:150px" placeholder="搜索客户">
            <el-option v-for="c in customers" :key="c.customerNo" :label="c.customerNo + ' (' + c.name + ')'"
                       :value="c.customerNo"/>
          </el-select>
        </el-form-item>
        <el-form-item label="账户编号">
          <el-select v-model="query.accountNo" clearable filterable style="width:150px" placeholder="搜索账户">
            <el-option v-for="a in accounts" :key="a.accountNo" :label="a.accountNo" :value="a.accountNo"/>
          </el-select>
        </el-form-item>
        <el-form-item label="交易类型">
          <el-select v-model="query.transType" clearable style="width:130px">
            <el-option label="存款" value="DEPOSIT"/>
            <el-option label="取款" value="WITHDRAW"/>
            <el-option label="转账" value="TRANSFER"/>
            <el-option label="消费" value="PAYMENT"/>
            <el-option label="还款" value="REPAYMENT"/>
          </el-select>
        </el-form-item>
        <el-form-item label="渠道">
          <el-select v-model="query.channel" clearable style="width:120px">
            <el-option label="柜面" value="COUNTER"/>
            <el-option label="网银" value="ONLINE"/>
            <el-option label="手机银行" value="MOBILE"/>
            <el-option label="ATM" value="ATM"/>
          </el-select>
        </el-form-item>
        <el-form-item label="起始日">
          <el-date-picker v-model="query.dateFrom" type="date" value-format="yyyy-MM-dd" style="width:150px"/>
        </el-form-item>
        <el-form-item label="截止日">
          <el-date-picker v-model="query.dateTo" type="date" value-format="yyyy-MM-dd" style="width:150px"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" style="margin-top:12px">
      <el-table :data="list" stripe style="width:100%" v-loading="loading">
        <el-table-column prop="transNo" label="交易编号" width="180"/>
        <el-table-column prop="customerNo" label="客户编号" width="120"/>
        <el-table-column prop="accountNo" label="账户编号" width="120"/>
        <el-table-column prop="transType" label="类型" width="80">
          <template slot-scope="{row}">
            {{
              {
                DEPOSIT: '存款',
                WITHDRAW: '取款',
                TRANSFER: '转账',
                PAYMENT: '消费',
                REPAYMENT: '还款'
              }[row.transType] || row.transType
            }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="交易金额" width="120">
          <template slot-scope="{row}">
            {{ row.amount?.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="balanceBefore" label="交易前账户余额" width="130">
          <template slot-scope="{row}">{{ row.balanceBefore?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="balanceAfter" label="交易后账户余额" width="130">
          <template slot-scope="{row}">{{ row.balanceAfter?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="fee" label="手续费" width="80"/>
        <el-table-column prop="channel" label="渠道" width="100">
          <template slot-scope="{row}">
            {{ {COUNTER: '柜面', ONLINE: '网银', MOBILE: '手机银行', ATM: 'ATM'}[row.channel] || row.channel }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70">
          <template slot-scope="{row}">
            <el-tag :type="row.status==='SUCCESS'?'success':'danger'" size="mini">
              {{ row.status === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="transTime" label="交易时间" width="150">
          <template slot-scope="{row}">{{ formatDate(row.transTime) }}</template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="160"/>
        <el-table-column label="操作" width="80" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" type="success" @click="viewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination @current-change="getData" :current-page="page" :page-size="size" :total="total"
                     layout="total,prev,pager,next" style="margin-top:12px;text-align:right"/>
    </el-card>
    <el-dialog title="交易详情" :visible.sync="detailVisible" width="600px">
      <el-form label-width="110px" size="small" v-if="detail">
        <el-row>
          <el-col :span="12">
            <el-form-item label="交易编号">: {{ detail.transNo }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交易类型">: {{
                {
                  DEPOSIT: '存款',
                  WITHDRAW: '取款',
                  TRANSFER: '转账',
                  PAYMENT: '消费',
                  REPAYMENT: '还款'
                }[detail.transType] || detail.transType
              }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户编号">: {{ detail.customerNo }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账户编号">: {{ detail.accountNo }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="交易前余额">: {{ detail.balanceBefore?.toLocaleString() }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交易后余额">: {{ detail.balanceAfter?.toLocaleString() }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手续费">: {{ detail.fee }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="渠道">: {{
                {
                  COUNTER: '柜面',
                  ONLINE: '网银',
                  MOBILE: '手机银行',
                  ATM: 'ATM'
                }[detail.channel] || detail.channel
              }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态">:
              <el-tag :type="detail.status==='SUCCESS'?'success':'danger'" size="mini">
                {{ detail.status === 'SUCCESS' ? '成功' : '失败' }}
              </el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交易时间">: {{ formatDate(detail.transTime) }}</el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述">: {{ detail.description }}</el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {page as pageApi, get as getApi} from '@/api/business/trans-log'
import {listAll as listCustomers} from '@/api/business/customer'
import {listAll as listAccounts} from '@/api/business/account'

export default {
  name: 'TransLog',
  data() {
    return {
      list: [],
      total: 0,
      page: 1,
      size: 10,
      loading: false,
      detailVisible: false,
      detail: {},
      customers: [],
      accounts: [],
      query: {transType: '', channel: '', dateFrom: '', dateTo: '', customerNo: '', accountNo: ''}
    }
  },
  mounted() {
    this.getData()
    this.loadOptions()
  },
  methods: {
    async loadOptions() {
      const [cr, ar] = await Promise.all([listCustomers(), listAccounts()])
      this.customers = cr.data
      this.accounts = ar.data
    },
    async getData(p) {
      if (p) this.page = p
      this.loading = true
      const res = await pageApi({page: this.page, size: this.size, ...this.query})
      this.list = res.data.records;
      this.total = res.data.total
      this.loading = false
    },
    search() {
      this.page = 1;
      this.getData()
    },
    reset() {
      this.query = {transType: '', channel: '', dateFrom: '', dateTo: '', customerNo: '', accountNo: ''};
      this.search()
    },
    formatDate(d) {
      if (!d) return '';
      return d.replace('T', ' ')
    },
    async viewDetail(row) {
      const res = await getApi(row.id)
      this.detail = res.data
      this.detailVisible = true
    }
  }
}
</script>
