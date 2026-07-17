<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="query" inline size="small">
        <el-form-item label="贷款编号">
          <el-input v-model="query.loanNo" clearable @keyup.enter="search" style="width:130px"/>
        </el-form-item>
        <el-form-item label="客户编号">
          <el-select v-model="query.customerNo" clearable filterable style="width:150px" placeholder="搜索客户">
            <el-option v-for="c in customers" :key="c.customerNo" :label="c.customerNo + ' (' + c.name + ')'"
                       :value="c.customerNo"/>
          </el-select>
        </el-form-item>
        <el-form-item label="还款方式">
          <el-select v-model="query.repayMethod" clearable style="width:120px">
            <el-option label="等额本息" value="EQUAL_INSTALLMENT"/>
            <el-option label="等额本金" value="EQUAL_PRINCIPAL"/>
            <el-option label="到期还本" value="MATURITY"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable style="width:100px">
            <el-option label="正常" value="NORMAL"/>
            <el-option label="逾期" value="OVERDUE"/>
            <el-option label="结清" value="SETTLED"/>
          </el-select>
        </el-form-item>
        <el-form-item label="借贷日期起">
          <el-date-picker v-model="query.dateFrom" type="date" value-format="yyyy-MM-dd" style="width:140px"/>
        </el-form-item>
        <el-form-item label="止">
          <el-date-picker v-model="query.dateTo" type="date" value-format="yyyy-MM-dd" style="width:140px"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" style="margin-top:12px">
      <el-button type="primary" size="small" @click="add">新增贷款</el-button>
      <el-table :data="list" stripe style="width:100%;margin-top:12px" v-loading="loading">
        <el-table-column prop="loanNo" label="贷款编号" width="120"/>
        <el-table-column prop="customerNo" label="客户编号" width="90"/>
        <el-table-column prop="amount" label="贷款金额" width="120">
          <template slot-scope="{row}">{{ row.amount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="termMonths" label="期限(月)" width="60"/>
        <el-table-column prop="interestRate" label="利率" width="65">
          <template slot-scope="{row}">{{ row.interestRate }}%</template>
        </el-table-column>
        <el-table-column prop="maturityInterest" label="到期利息" width="110">
          <template slot-scope="{row}">{{ row.maturityInterest?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="maturityTotal" label="到期应还" width="120">
          <template slot-scope="{row}">{{ row.maturityTotal?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="repayMethod" label="还款方式" width="90">
          <template slot-scope="{row}">{{
              {
                EQUAL_PRINCIPAL: '等额本金',
                EQUAL_INSTALLMENT: '等额本息',
                MATURITY: '到期还本'
              }[row.repayMethod] || row.repayMethod
            }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="65">
          <template slot-scope="{row}">
            <el-tag :type="{NORMAL:'success',OVERDUE:'danger',SETTLED:'info'}[row.status]||'info'" size="mini">
              {{ {NORMAL: '正常', OVERDUE: '逾期', SETTLED: '结清'}[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="loanDate" label="借贷日期" width="100"/>
        <el-table-column prop="dueDate" label="应还日期" width="100"/>
        <el-table-column prop="repayDate" label="还贷日期" width="100"/>
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
    <el-dialog :title="form.id?'编辑贷款':'新增贷款'" :visible.sync="dialogVisible" width="550px">
      <el-form :model="form" label-width="100px" size="small">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户编号">
              <el-select v-model="form.customerNo" filterable style="width:100%" placeholder="搜索客户">
                <el-option v-for="c in customers" :key="c.customerNo" :label="c.customerNo + ' (' + c.name + ')'"
                           :value="c.customerNo"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="贷款金额">
              <el-input v-model="form.amount"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期限(月)">
              <el-input v-model="form.termMonths"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="利率(%)">
              <el-input v-model="form.interestRate"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="还款方式">
              <el-select v-model="form.repayMethod" style="width:100%">
                <el-option label="等额本息" value="EQUAL_INSTALLMENT"/>
                <el-option label="等额本金" value="EQUAL_PRINCIPAL"/>
                <el-option label="到期还本" value="MATURITY"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="放贷日期">
              <el-date-picker v-model="form.loanDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期日期">
              <el-date-picker v-model="form.dueDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer"><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary"
                                                                                             @click="save">保存</el-button></span>
    </el-dialog>
    <el-dialog title="贷款详情" :visible.sync="detailVisible" width="550px">
      <el-form label-width="100px" size="small" v-if="detail">
        <el-row>
          <el-col :span="12">
            <el-form-item label="贷款编号">: {{ detail.loanNo }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户编号">: {{ detail.customerNo }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="贷款金额">: {{ detail.amount?.toLocaleString() }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期限(月)">: {{ detail.termMonths }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="利率">: {{ detail.interestRate }}%</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="还款方式">: {{
                {
                  EQUAL_PRINCIPAL: '等额本金',
                  EQUAL_INSTALLMENT: '等额本息',
                  MATURITY: '到期还本'
                }[detail.repayMethod] || detail.repayMethod
              }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="到期利息">: {{ detail.maturityInterest?.toLocaleString() }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期应还">: {{ detail.maturityTotal?.toLocaleString() }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="借贷日期">: {{ detail.loanDate }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应还日期">: {{ detail.dueDate }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="还贷日期">: {{ detail.repayDate || '-' }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">:
              <el-tag :type="{NORMAL:'success',OVERDUE:'danger',SETTLED:'info'}[detail.status]||'info'" size="mini">
                {{ {NORMAL: '正常', OVERDUE: '逾期', SETTLED: '结清'}[detail.status] || detail.status }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {page as pageApi, save, update, remove, get as getApi} from '@/api/business/loan'
import {listAll as listCustomers} from '@/api/business/customer'

export default {
  name: 'Loan',
  data() {
    return {
      list: [],
      total: 0,
      page: 1,
      size: 10,
      loading: false,
      dialogVisible: false,
      customers: [],
      query: {status: '', dateFrom: '', dateTo: '', loanNo: '', customerNo: '', repayMethod: ''},
      form: {},
      detailVisible: false,
      detail: {}
    }
  },
  mounted() {
    this.getData();
    this.loadOptions()
  },
  methods: {
    async loadOptions() {
      const cr = await listCustomers()
      this.customers = cr.data
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
      this.query = {status: '', dateFrom: '', dateTo: '', loanNo: '', customerNo: '', repayMethod: ''};
      this.search()
    },
    add() {
      this.form = {repayMethod: 'EQUAL_INSTALLMENT'};
      this.dialogVisible = true
    },
    edit(row) {
      this.form = {...row};
      this.dialogVisible = true
    },
    async viewDetail(row) {
      const res = await getApi(row.id)
      this.detail = res.data
      this.detailVisible = true
    },
    async save() {
      // 清理百分号和逗号
      const data = {...this.form}
      if (typeof data.interestRate === 'string') data.interestRate = data.interestRate.replace('%', '')
      if (typeof data.amount === 'string') data.amount = data.amount.replace(/,/g, '')
      if (this.form.id) {
        await update(data)
      } else {
        await save(data)
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
    }
  }
}
</script>
