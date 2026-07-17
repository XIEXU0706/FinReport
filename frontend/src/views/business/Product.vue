<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="query" inline size="small">
        <el-form-item label="产品编号">
          <el-input v-model="query.productCode" clearable @keyup.enter="search" style="width:130px"/>
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input v-model="query.productName" clearable @keyup.enter="search" style="width:150px"/>
        </el-form-item>
        <el-form-item label="产品类型">
          <el-select v-model="query.productType" clearable style="width:130px">
            <el-option label="理财" value="FINANCIAL"/>
            <el-option label="基金" value="FUND"/>
            <el-option label="保险" value="INSURANCE"/>
            <el-option label="存款" value="DEPOSIT"/>
          </el-select>
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="query.riskLevel" clearable style="width:120px">
            <el-option label="低风险" value="LOW"/>
            <el-option label="中风险" value="MEDIUM"/>
            <el-option label="高风险" value="HIGH"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable style="width:100px">
            <el-option label="在售" value="SELLING"/>
            <el-option label="停售" value="STOPPED"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" style="margin-top:12px">
      <el-button type="primary" size="small" @click="add">新增产品</el-button>
      <el-button type="success" size="small" @click="$router.push('/business/prod-sale')" style="margin-left:8px">产品销售记录</el-button>
      <el-table :data="list" stripe style="width:100%;margin-top:12px" v-loading="loading">
        <el-table-column prop="productCode" label="产品编号" width="100"/>
        <el-table-column prop="productName" label="产品名称" width="150"/>
        <el-table-column prop="productType" label="类型" width="80">
          <template slot-scope="{row}">{{
              {
                FINANCIAL: '理财',
                FUND: '基金',
                INSURANCE: '保险',
                DEPOSIT: '存款'
              }[row.productType] || row.productType
            }}
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级" width="80">
          <template slot-scope="{row}">
            <el-tag :type="{LOW:'success',MEDIUM:'warning',HIGH:'danger'}[row.riskLevel]" size="mini">{{
                {
                  LOW: '低',
                  MEDIUM: '中',
                  HIGH: '高'
                }[row.riskLevel]
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expectedReturn" label="预期收益" width="90">
          <template slot-scope="{row}">
            {{ row.productType === 'INSURANCE' && row.expectedReturn == 0 ? '保障型' : row.expectedReturn + '%' }}
          </template>
        </el-table-column>
        <el-table-column prop="minAmount" label="起购金额" width="120">
          <template slot-scope="{row}">{{ row.minAmount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="termDays" label="期限(天)" width="80"/>
        <el-table-column prop="status" label="状态" width="70">
          <template slot-scope="{row}">
            <el-tag :type="{SELLING:'success',STOPPED:'info'}[row.status]||'info'" size="mini">{{
                {
                  SELLING: '在售',
                  STOPPED: '停售'
                }[row.status] || row.status
              }}
            </el-tag>
          </template>
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
    <el-dialog :title="form.id?'编辑产品':'新增产品'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="90px" size="small">
        <el-form-item label="产品名称">
          <el-input v-model="form.productName"/>
        </el-form-item>
        <el-form-item label="产品类型">
          <el-select v-model="form.productType" style="width:100%">
            <el-option label="理财" value="FINANCIAL"/>
            <el-option label="基金" value="FUND"/>
            <el-option label="保险" value="INSURANCE"/>
            <el-option label="存款" value="DEPOSIT"/>
          </el-select>
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="form.riskLevel" style="width:100%">
            <el-option label="低风险" value="LOW"/>
            <el-option label="中风险" value="MEDIUM"/>
            <el-option label="高风险" value="HIGH"/>
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="预期收益(%)">
              <el-input v-model="form.expectedReturn"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="起购金额">
              <el-input v-model="form.minAmount"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="期限(天)">
          <el-input v-model="form.termDays"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="在售" value="SELLING"/>
            <el-option label="停售" value="STOPPED"/>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary"
                                                                                             @click="save">保存</el-button></span>
    </el-dialog>
    <el-dialog title="产品详情" :visible.sync="detailVisible" width="500px">
      <el-form label-width="100px" size="small" v-if="detail">
        <el-row>
          <el-col :span="12">
            <el-form-item label="产品编号">: {{ detail.productCode }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品名称">: {{ detail.productName }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="类型">: {{
                {
                  FINANCIAL: '理财',
                  FUND: '基金',
                  INSURANCE: '保险',
                  DEPOSIT: '存款'
                }[detail.productType] || detail.productType
              }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="风险等级">:
              <el-tag :type="{LOW:'success',MEDIUM:'warning',HIGH:'danger'}[detail.riskLevel]" size="mini">
                {{ {LOW: '低', MEDIUM: '中', HIGH: '高'}[detail.riskLevel] }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="预期收益">: {{
                detail.productType === 'INSURANCE' && detail.expectedReturn == 0 ? '保障型' : detail.expectedReturn + '%'
              }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="起购金额">: {{ detail.minAmount?.toLocaleString() }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="期限(天)">: {{ detail.termDays }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">:
              <el-tag :type="{SELLING:'success',STOPPED:'info'}[detail.status]||'info'" size="mini">{{
                  {
                    SELLING: '在售',
                    STOPPED: '停售'
                  }[detail.status] || detail.status
                }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {page as pageApi, save, update, remove, get as getApi} from '@/api/business/product'

export default {
  name: 'Product',
  data() {
    return {
      list: [], total: 0, page: 1, size: 10, loading: false, dialogVisible: false,
      query: {productType: '', riskLevel: '', status: '', productCode: '', productName: ''}, form: {},
      detailVisible: false,
      detail: {}
    }
  },
  mounted() {
    this.getData()
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
    search() {
      this.page = 1;
      this.getData()
    },
    reset() {
      this.query = {productType: '', riskLevel: '', status: '', productCode: '', productName: ''};
      this.search()
    },
    add() {
      this.form = {};
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
      if (typeof data.expectedReturn === 'string') data.expectedReturn = data.expectedReturn.replace('%', '')
      if (typeof data.minAmount === 'string') data.minAmount = data.minAmount.replace(/,/g, '')
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
