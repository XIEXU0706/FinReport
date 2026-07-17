<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="query" inline size="small">
        <el-form-item label="账号">
          <el-input v-model="query.accountNo" clearable @keyup.enter="search" style="width:160px"/>
        </el-form-item>
        <el-form-item label="客户编号">
          <el-select v-model="query.customerNo" clearable filterable style="width:160px" placeholder="搜索客户">
            <el-option v-for="c in customers" :key="c.customerNo" :label="c.customerNo + ' (' + c.name + ')'"
                       :value="c.customerNo"/>
          </el-select>
        </el-form-item>
        <el-form-item label="账户类型">
          <el-select v-model="query.accountType" clearable style="width:120px">
            <el-option label="储蓄" value="SAVING"/>
            <el-option label="支票" value="CHECKING"/>
            <el-option label="信用卡" value="CREDIT"/>
          </el-select>
        </el-form-item>
        <el-form-item label="币种">
          <el-select v-model="query.currency" clearable style="width:120px">
            <el-option label="人民币" value="CNY"/>
            <el-option label="美元" value="USD"/>
            <el-option label="欧元" value="EUR"/>
            <el-option label="英镑" value="GBP"/>
            <el-option label="日元" value="JPY"/>
            <el-option label="港币" value="HKD"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable style="width:100px">
            <el-option label="正常" value="ACTIVE"/>
            <el-option label="停用" value="INACTIVE"/>
            <el-option label="冻结" value="FROZEN"/>
            <el-option label="销户" value="CLOSED"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" style="margin-top:12px">
      <el-button type="primary" size="small" @click="add">新增账户</el-button>
      <el-table :data="list" stripe style="width:100%;margin-top:12px" v-loading="loading">
        <el-table-column prop="accountNo" label="账号" width="160"/>
        <el-table-column prop="customerNo" label="客户编号" width="120"/>
        <el-table-column prop="accountType" label="账户类型" width="90">
          <template slot-scope="{row}">
            {{ {SAVING: '储蓄', CHECKING: '支票', CREDIT: '信用卡'}[row.accountType] || row.accountType }}
          </template>
        </el-table-column>
        <el-table-column prop="currency" label="币种" width="80">
          <template slot-scope="{row}">{{ currencyMap[row.currency] || row.currency }}</template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="140">
          <template slot-scope="{row}">
            {{ row.balance?.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70">
          <template slot-scope="{row}">
            <el-tag :type="statusTag(row.status)" size="mini">
              {{ statusMap[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="开户日期" width="150">
          <template slot-scope="{row}">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" type="success" @click="viewDetail(row)">查看详情</el-button>
            <el-button size="mini" @click="edit(row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination @current-change="getData" :current-page="page" :page-size="size" :total="total"
                     layout="total,prev,pager,next" style="margin-top:12px;text-align:right"/>
    </el-card>
    <el-dialog :title="form.id?'编辑账户':'新增账户'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="90px" size="small">
        <el-form-item label="客户编号" prop="customerNo">
          <el-select v-model="form.customerNo" filterable style="width:100%" placeholder="搜索选择客户">
            <el-option v-for="c in customers" :key="c.customerNo" :label="c.customerNo + ' (' + c.name + ')'"
                       :value="c.customerNo"/>
          </el-select>
        </el-form-item>
        <el-form-item label="账户类型">
          <el-select v-model="form.accountType" style="width:100%">
            <el-option label="储蓄" value="SAVING"/>
            <el-option label="支票" value="CHECKING"/>
            <el-option label="信用卡" value="CREDIT"/>
          </el-select>
        </el-form-item>
        <el-form-item label="币种" prop="currency">
          <el-select v-model="form.currency" style="width:100%">
            <el-option label="人民币" value="CNY"/>
            <el-option label="美元" value="USD"/>
            <el-option label="欧元" value="EUR"/>
            <el-option label="英镑" value="GBP"/>
            <el-option label="日元" value="JPY"/>
            <el-option label="港币" value="HKD"/>
          </el-select>
        </el-form-item>
        <el-form-item label="余额">
          <el-input v-model="form.balance"/>
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="form.id">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="正常" value="ACTIVE"/>
            <el-option label="停用" value="INACTIVE"/>
            <el-option label="冻结" value="FROZEN"/>
            <el-option label="销户" value="CLOSED"/>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </span>
    </el-dialog>
    <el-dialog title="账户详情" :visible.sync="detailVisible" width="500px">
      <el-form label-width="100px" size="small" v-if="detail">
        <el-row>
          <el-col :span="12">
            <el-form-item label="账号">
              : {{ detail.accountNo }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户编号">
              : {{ detail.customerNo }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="账户类型">
              : {{ {SAVING: '储蓄', CHECKING: '支票', CREDIT: '信用卡'}[detail.accountType] || detail.accountType }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="币种">
              : {{ currencyMap[detail.currency] || detail.currency }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="余额">
              : {{ detail.balance?.toLocaleString() }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              : {{ statusMap[detail.status] || detail.status }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="开户日期">
          : {{ formatDate(detail.createdAt) }}
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {page as pageApi, save, update, remove, get as getApi} from '@/api/business/account'
import {listAll as listCustomers} from '@/api/business/customer'

export default {
  name: 'Account',
  data() {
    return {
      list: [],
      total: 0,
      page: 1,
      size: 10,
      loading: false,
      dialogVisible: false,
      detailVisible: false,
      detail: {},
      customers: [],
      query: {accountNo: '', customerNo: '', accountType: '', currency: '', status: ''},
      form: {},
      statusMap: {ACTIVE: '正常', INACTIVE: '停用', FROZEN: '冻结', CLOSED: '销户'},
      currencyMap: {CNY: '人民币', USD: '美元', EUR: '欧元', GBP: '英镑', JPY: '日元', HKD: '港币'}
    }
  },
  async mounted() {
    this.getData()
    const res = await listCustomers()
    this.customers = res.data
  },
  methods: {
    async getData(p) {
      if (p) this.page = p
      this.loading = true
      const res = await pageApi({page: this.page, size: this.size, ...this.query})
      this.list = res.data.records;
      console.log(this.list)
      this.total = res.data.total
      this.loading = false
    },
    search() {
      this.page = 1;
      this.getData()
    },
    reset() {
      this.query = {accountNo: '', customerNo: '', accountType: '', currency: '', status: ''};
      this.search()
    },
    add() {
      this.form = {};
      this.dialogVisible = true
    },
    statusTag(s) {
      return {ACTIVE: 'success', INACTIVE: 'danger', FROZEN: 'warning', CLOSED: 'info'}[s] || 'info'
    },
    edit(row) {
      this.form = {...row};
      this.dialogVisible = true
    },
    async save() {
      if (this.form.id) {
        await update(this.form)
      } else {
        await save(this.form)
      }
      this.$message.success('保存成功');
      this.dialogVisible = false;
      this.getData()
    },
    del(row) {
      this.$confirm('确认删除？', '提示', {type: 'warning'}).then(async () => {
        await remove(row.id)
        this.$message.success('删除成功');
        this.getData()
      }).catch(() => {
      })
    },
    formatDate(d) {
      if (!d) return ''
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
