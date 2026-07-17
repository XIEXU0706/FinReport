<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="query" inline size="small">
        <el-form-item label="报表名称">
          <el-input v-model="query.title" placeholder="报表名称" clearable style="width:180px"
                    @keyup.enter.native="search"/>
        </el-form-item>
        <el-form-item label="报表类型">
          <el-select v-model="query.reportType" placeholder="全部" clearable style="width:130px" @change="search">
            <el-option label="交易分析" value="DAILY"/>
            <el-option label="贷款分析" value="MONTHLY"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:120px" @change="search">
            <el-option label="生成中" value="GENERATING"/>
            <el-option label="已生成" value="GENERATED"/>
            <el-option label="已审核" value="APPROVED"/>
            <el-option label="失败" value="FAILED"/>
          </el-select>
        </el-form-item>
<!--        <el-form-item label="生成时间">-->
<!--          <el-date-picker v-model="query.dateRange" type="daterange" range-separator="~" start-placeholder="开始"-->
<!--                          end-placeholder="结束" value-format="yyyy-MM-dd" style="width:240px" @change="search"/>-->
<!--        </el-form-item>-->
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top:12px">
      <el-button type="primary" size="small" @click="openGenerate">生成报表</el-button>
      <el-table :data="list" stripe style="width:100%;margin-top:12px" v-loading="loading">
        <el-table-column prop="id" label="报表ID" width="120"/>
        <el-table-column prop="title" label="报表名称" width="220"/>
        <el-table-column label="报表类型" width="110">
          <template slot-scope="{row}">
            <el-tag :type="row.reportType && row.reportType.startsWith('LOAN') ? 'warning' : 'primary'" size="mini">
              {{ row.reportType && row.reportType.startsWith('LOAN') ? '贷款分析' : '交易分析' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="generatedTime" label="生成时间" width="150" sortable="custom" :formatter="fmtDate"/>
        <el-table-column label="生成方式" width="90">
          <template slot-scope="{row}">
            {{ row.generatedBy ? '手动' : '自动' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template slot-scope="{row}">
            <el-tag :type="statusTag(row.status)" size="mini">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
<!--        <el-table-column label="文件大小" width="90">-->
<!--          <template slot-scope="{row}">-->
<!--            {{ row.fileUrl ? (row.fileSize || '-') : '-' }}-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="{row}">
            <el-button v-if="row.fileUrl" size="mini" type="primary" @click="download(row)">下载</el-button>
            <el-button v-else size="mini" type="info" disabled>下载</el-button>
            <el-button size="mini" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          @current-change="getData"
          @size-change="onSizeChange"
          :current-page="page"
          :page-size="size"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          style="margin-top:16px;text-align:right"
      />
    </el-card>

    <!-- 生成报表对话框 -->
    <el-dialog title="生成报表" :visible.sync="genDialogVisible" width="500px" :close-on-click-modal="false">
      <el-form ref="genForm" :model="genForm" :rules="genRules" label-width="100px" size="small">
        <el-form-item label="报表名称" prop="title">
          <el-input v-model="genForm.title" placeholder="请输入报表名称" maxlength="100"/>
        </el-form-item>
        <el-form-item label="报表类型" prop="reportType">
          <el-select v-model="genForm.reportType" placeholder="请选择" style="width:100%">
            <el-option label="交易日报" value="DAILY"/>
            <el-option label="交易周报" value="WEEKLY"/>
            <el-option label="交易月报" value="MONTHLY"/>
            <el-option label="贷款日报" value="LOAN_DAILY"/>
            <el-option label="贷款周报" value="LOAN_WEEKLY"/>
            <el-option label="贷款月报" value="LOAN_MONTHLY"/>
          </el-select>
        </el-form-item>
        <el-form-item label="统计区间" prop="periodRange">
          <el-date-picker v-model="genForm.periodRange" type="daterange" range-separator="~"
                          start-placeholder="开始日期" end-placeholder="结束日期"
                          value-format="yyyy-MM-dd" style="width:100%"/>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="genDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGenerate" :loading="genSaving">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {page as pageApi, remove as removeApi, downloadUrl, generate as generateApi} from '@/api/report'

export default {
  name: 'ReportHistory',
  data() {
    return {
      list: [], total: 0, page: 1, size: 10, loading: false,
      query: {title: '', reportType: '', status: '', dateRange: null},
      genDialogVisible: false,
      genSaving: false,
      genForm: {title: '', reportType: 'DAILY', periodRange: null},
      genRules: {
        title: [{required: true, message: '请输入报表名称', trigger: 'blur'}],
        reportType: [{required: true, message: '请选择报表类型', trigger: 'change'}],
        periodRange: [{required: true, message: '请选择统计区间', trigger: 'change'}]
      }
    }
  },
  mounted() {
    this.getData()
  },
  methods: {
    async getData(p) {
      if (p) this.page = p
      this.loading = true
      try {
        const params = {page: this.page, size: this.size}
        if (this.query.title) params.title = this.query.title
        if (this.query.reportType) params.reportType = this.query.reportType
        if (this.query.status) params.status = this.query.status
        const res = await pageApi(params)
        this.list = res.data.records
        this.total = res.data.total
      } catch (e) {

      }
      this.loading = false
    },
    search() {
      this.page = 1;
      this.getData()
    },
    reset() {
      this.query = {title: '', reportType: '', status: '', dateRange: null};
      this.search()
    },
    onSizeChange(val) {
      this.size = val;
      this.getData()
    },
    fmtDate(row, col, val) {
      return val ? val.replace('T', ' ') : ''
    },
    statusTag(s) {
      return {
        GENERATING: 'warning',
        GENERATED: 'success',
        APPROVED: 'primary',
        FAILED: 'danger'}[s] || 'info'
    },
    statusText(s) {
      return {
        GENERATING: '生成中',
        GENERATED: '已生成',
        APPROVED: '已审核',
        FAILED: '失败'}[s] || s
    },
    openGenerate() {
      this.genForm = {title: '', reportType: 'DAILY', periodRange: null}
      this.genDialogVisible = true
    },
    async submitGenerate() {
      this.$refs.genForm.validate(async valid => {
        if (!valid) return
        this.genSaving = true
        try {
          await generateApi({
            title: this.genForm.title,
            reportType: this.genForm.reportType,
            periodStart: this.genForm.periodRange[0],
            periodEnd: this.genForm.periodRange[1],
            generatedBy: this.$store.state.user.username
          })
          this.$message.success('报表已提交生成')
          this.genDialogVisible = false
          this.getData()
        } catch (e) {
          this.$message.error('提交失败')
        }
        this.genSaving = false
      })
    },
    async download(row) {
      try {
        const token = this.$store.state.user.token
        const url = downloadUrl(row.id)
        const a = document.createElement('a')
        a.setAttribute('download', (row.title || '报表') + '.xlsx')
        const resp = await fetch(url, {headers: {Authorization: 'Bearer ' + token}})
        if (!resp.ok) {
          this.$message.error('下载失败');
          return
        }
        const blob = await resp.blob()
        const blobUrl = window.URL.createObjectURL(blob)
        a.href = blobUrl
        a.click()
        window.URL.revokeObjectURL(blobUrl)
        this.$message.success('下载成功')
      } catch (e) {
        this.$message.error('下载失败')
      }
    },
    del(row) {
      this.$confirm('确认删除报表「' + row.title + '」？<br><span style="color:#e6a23c;">将同时删除 OSS 文件</span>', '提示', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning', dangerouslyUseHTMLString: true
      }).then(async () => {
        await removeApi(row.id)
        this.$message.success('删除成功')
        this.getData()
      }).catch(() => {
      })
    }
  }
}
</script>
