<template>
  <div class="page">
    <el-card shadow="never">
      <el-button type="primary" size="small" @click="add">新增规则</el-button>
      <el-table :data="list" stripe style="width:100%;margin-top:12px" v-loading="loading">
        <el-table-column prop="ruleName" label="规则名称" width="140"/>
        <el-table-column prop="metric" label="指标" width="150">
          <template slot-scope="{row}">
            {{
              {
                TRADE_AMOUNT_DAILY: '日交易金额',
                LOAN_OVERDUE_RATE: '贷款逾期率',
                NEW_CUSTOMERS_DAILY: '日新增客户',
                LARGE_TRADE_COUNT: '大额交易笔数',
                LOAN_AMOUNT_DAILY: '日放贷金额'
              }[row.metric] || row.metric
            }}
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="运算符" width="60">
          <template slot-scope="{row}">{{ row.operator }}</template>
        </el-table-column>
        <el-table-column prop="threshold" label="阈值" width="90"/>
        <el-table-column prop="severity" label="严重程度" width="90">
          <template slot-scope="{row}">
            <el-tag :type="{CRITICAL:'danger',HIGH:'warning',MEDIUM:'info',LOW:'success'}[row.severity]" size="mini">
              {{ {CRITICAL: '紧急', HIGH: '高', MEDIUM: '中', LOW: '低'}[row.severity] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{row}">
            <el-tag :type="row.status==='ENABLED'?'success':'danger'" size="mini">
              {{ row.status === 'ENABLED' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="130"/>
        <el-table-column prop="createdAt" label="创建时间" width="150">
          <template slot-scope="{row}">{{ row.createdAt?.replace('T', ' ') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" @click="edit(row)">编辑</el-button>
            <el-button size="mini" :type="row.status==='ENABLED'?'warning':'success'" @click="toggle(row)">
              {{ row.status === 'ENABLED' ? '停用' : '启用' }}
            </el-button>
            <el-button size="mini" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination @current-change="getData" :current-page="page" :page-size="size" :total="total"
                     layout="total,prev,pager,next" style="margin-top:12px;text-align:right"/>
    </el-card>
    <el-dialog :title="form.id?'编辑规则':'新增规则'" :visible.sync="dialogVisible" width="550px">
      <el-form :model="form" label-width="100px" size="small">
        <el-form-item label="规则名称">
          <el-input v-model="form.ruleName"/>
        </el-form-item>
        <el-form-item label="指标">
          <el-select v-model="form.metric" style="width:100%">
            <el-option label="日交易金额" value="TRADE_AMOUNT_DAILY"/>
            <el-option label="贷款逾期率" value="LOAN_OVERDUE_RATE"/>
            <el-option label="日新增客户" value="NEW_CUSTOMERS_DAILY"/>
            <el-option label="大额交易笔数" value="LARGE_TRADE_COUNT"/>
            <el-option label="日放贷金额" value="LOAN_AMOUNT_DAILY"/>
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="运算符">
              <el-select v-model="form.operator">
                <el-option label=">" value=">"/>
                <el-option label=">=" value=">="/>
                <el-option label="<" value="<"/>
                <el-option label="<=" value="<="/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="阈值">
              <el-input v-model="form.threshold"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="严重程度">
              <el-select v-model="form.severity">
                <el-option label="紧急" value="CRITICAL"/>
                <el-option label="高" value="HIGH"/>
                <el-option label="中" value="MEDIUM"/>
                <el-option label="低" value="LOW"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="规则描述">
          <el-input v-model="form.description" type="textarea" :rows="2"/>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary"
                                                                                             @click="save">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import {page as pageApi, save, update, remove} from '@/api/alert/rule'

export default {
  name: 'AlertRule',
  data() {
    return {
      list: [], total: 0, page: 1, size: 10, loading: false, dialogVisible: false,
      form: {}
    }
  },
  mounted() {
    this.getData()
  },
  methods: {
    async getData(p) {
      if (p) this.page = p
      this.loading = true
      const res = await pageApi({page: this.page, size: this.size})
      this.list = res.data.records;
      this.total = res.data.total
      this.loading = false
    },
    add() {
      this.form = {severity: 'MEDIUM', operator: '>'};
      this.dialogVisible = true
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
    async toggle(row) {
      const newStatus = row.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
      await update({id: row.id, status: newStatus})
      this.$message.success(newStatus === 'ENABLED' ? '已启用' : '已停用');
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
