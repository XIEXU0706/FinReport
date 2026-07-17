<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="query" inline size="small">
        <el-form-item label="任务名称">
          <el-input v-model="query.taskName" placeholder="任务名称" clearable style="width:200px"
                    @keyup.enter.native="search"/>
        </el-form-item>
        <el-form-item label="报表类型">
          <el-select v-model="query.reportType" placeholder="全部" clearable style="width:120px" @change="search">
            <el-option label="交易分析" value="DAILY"/>
            <el-option label="贷款分析" value="MONTHLY"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 任务列表 -->
    <el-card shadow="never" style="margin-top:12px">
      <el-button type="primary" size="small" @click="add">新建任务</el-button>
      <el-table :data="list" stripe style="width:100%;margin-top:12px" v-loading="loading">
        <el-table-column prop="taskName" label="任务名称" min-width="160"/>
        <el-table-column label="报表类型" width="110">
          <template slot-scope="{row}">
            <el-tag :type="row.reportType && row.reportType.startsWith('LOAN') ? 'warning' : 'primary'" size="mini">
              {{ row.reportType && row.reportType.startsWith('LOAN') ? '贷款分析' : '交易分析' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="执行周期" width="180">
          <template slot-scope="{row}">
            {{ formatCron(row.cronExpression) }}
          </template>
        </el-table-column>
        <el-table-column prop="lastRunTime" label="上次执行" width="160" :formatter="fmtDate"/>
        <el-table-column label="上次状态" width="90">
          <template slot-scope="{row}">
            <el-tag v-if="row.lastStatus === 'SUCCESS'" type="success" size="mini">成功</el-tag>
            <el-tag v-else-if="row.lastStatus === 'FAILED'" type="danger" size="mini">失败</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template slot-scope="{row}">
            <el-tag :type="row.enabled === 1 ? 'success' : 'danger'" size="mini">
              {{ row.enabled === 1 ? '开启' : '暂停' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="310" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" @click="edit(row)">编辑</el-button>
            <el-button size="mini" type="success" @click="runOnce(row)">立即执行</el-button>
            <el-button v-if="row.enabled === 1" size="mini" type="warning" @click="disable(row)">暂停</el-button>
            <el-button v-else size="mini" type="success" @click="enable(row)">启用</el-button>
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
          style="margin-top:12px;text-align:right"
      />
    </el-card>

    <!-- 新建任务框  -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="550px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="110px" size="small">
        <el-form-item label="报表模板" prop="taskName">
          <el-select v-model="form.taskName" placeholder="请选择报表模板" style="width:100%" @change="onTemplateChange">
            <el-option label="每日交易日报" value="每日交易日报"/>
            <el-option label="每周交易周报" value="每周交易周报"/>
            <el-option label="每月交易月报" value="每月交易月报"/>
            <el-option label="每日贷款日报" value="每日贷款日报"/>
            <el-option label="每周贷款周报" value="每周贷款周报"/>
            <el-option label="每月贷款月报" value="每月贷款月报"/>
          </el-select>
        </el-form-item>
        <el-form-item label="报表类型" prop="reportType">
          <el-select v-model="form.reportType" style="width:100%" disabled>
            <el-option label="交易分析" value="DAILY"/>
            <el-option label="贷款分析" value="LOAN_DAILY"/>
          </el-select>
        </el-form-item>
        <el-form-item label="执行周期" prop="cronPeriod">
          <el-radio-group v-model="form.cronPeriod" @change="buildCron">
            <el-radio label="daily">每天</el-radio>
            <el-radio label="weekly">每周</el-radio>
            <el-radio label="monthly">每月</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.cronPeriod === 'weekly'" label="选择星期">
          <el-checkbox-group v-model="form.cronWeekDays" @change="buildCron">
            <el-checkbox label="1">周一</el-checkbox>
            <el-checkbox label="2">周二</el-checkbox>
            <el-checkbox label="3">周三</el-checkbox>
            <el-checkbox label="4">周四</el-checkbox>
            <el-checkbox label="5">周五</el-checkbox>
            <el-checkbox label="6">周六</el-checkbox>
            <el-checkbox label="0">周日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item v-if="form.cronPeriod === 'monthly'" label="执行日期">
          <el-input-number v-model="form.cronMonthDay" :min="1" :max="28" @change="buildCron"/>
          <span style="margin-left:8px;color:#909399;">号</span>
        </el-form-item>
        <el-form-item label="执行时间">
          <el-time-picker v-model="form.cronTime" format="HH:mm" placeholder="选择时间" style="width:100%"
                          @change="buildCron"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" active-text="启用"
                     inactive-text="暂停"/>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {taskPage, taskGet, taskSave, taskUpdate, taskRemove, taskEnable, taskDisable, taskRunOnce} from '@/api/report'

export default {
  name: 'ReportTask',
  data() {
    return {
      list: [],
      total: 0,
      page: 1,
      size: 10,
      loading: false,
      query: {taskName: '', reportType: ''},
      dialogVisible: false,
      dialogTitle: '',
      saving: false,
      form: {
        taskName: '', reportType: 'DAILY', cronExpression: '0 1 * * * ?',
        cronPeriod: 'daily', cronWeekDays: [], cronMonthDay: 1, cronTime: null, enabled: 1
      },
      rules: {
        taskName: [
            {
              required: true, message: '请选择报表模板', trigger: 'change'
            }
        ],
        reportType: [
            {
              required: true, message: '请选择报表类型', trigger: 'change'
            }
        ]
      },
      templates: {
        '每日交易日报': {
          reportType: 'DAILY',
          period: 'daily',
          weekDays: [],
          monthDay: 1,
          hour: 1,
          min: 0
        },
        '每周交易周报': {
          reportType: 'DAILY',
          period: 'weekly',
          weekDays: ['1', '2', '3', '4', '5'],
          monthDay: 1,
          hour: 9,
          min: 0
        },
        '每月交易月报': {
          reportType: 'DAILY',
          period: 'monthly',
          weekDays: [],
          monthDay: 1,
          hour: 8,
          min: 0
        },
        '每日贷款日报': {
          reportType: 'LOAN_DAILY',
          period: 'daily',
          weekDays: [],
          monthDay: 1,
          hour: 1,
          min: 0
        },
        '每周贷款周报': {
          reportType: 'LOAN_WEEKLY',
          period: 'weekly',
          weekDays: ['1', '2', '3', '4', '5'],
          monthDay: 1,
          hour: 9,
          min: 0
        },
        '每月贷款月报': {
          reportType: 'LOAN_MONTHLY',
          period: 'monthly',
          weekDays: [],
          monthDay: 1,
          hour: 8,
          min: 0
        }
      },
      editId: null
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
        if (this.query.taskName) params.taskName = this.query.taskName
        if (this.query.reportType) params.reportType = this.query.reportType
        const res = await taskPage(params)
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
      this.query = {taskName: '', reportType: ''};
      this.search()
    },
    onSizeChange(val) {
      this.size = val;
      this.getData()
    },
    fmtDate(row, col, val) {
      return val ? val.replace('T', ' ') : ''
    },

    onTemplateChange(name) {
      const tpl = this.templates[name]
      if (!tpl) return
      this.form.reportType = tpl.reportType
      this.form.cronPeriod = tpl.period
      this.form.cronWeekDays = [...tpl.weekDays]
      this.form.cronMonthDay = tpl.monthDay
      this.form.cronTime = new Date().setHours(tpl.hour, tpl.min, 0, 0)
      this.buildCron()
    },

    formatCron(cron) {
      if (!cron) return '-'
      const p = cron.trim().split(/\s+/)
      if (p.length >= 5) {
        const t = (p[2] || '0').padStart(2, '0') + ':' + (p[1] || '0').padStart(2, '0')
        if (p[3] !== '*' && p[3] !== '?') return '每月 ' + parseInt(p[3]) + ' 号 ' + t
        if (p[5] && p[5] !== '*' && p[5] !== '?') {
          const d = {'0': '周日', '1': '周一', '2': '周二', '3': '周三', '4': '周四', '5': '周五', '6': '周六'}
          return '每周 ' + p[5].split(',').map(x => d[x] || x).join('、') + ' ' + t
        }
        return '每天 ' + t
      }
      return cron
    },

    buildCron() {
      if (!this.form.cronTime) return
      const d = new Date(this.form.cronTime)
      const h = d.getHours(), m = d.getMinutes()
      if (this.form.cronPeriod === 'daily') {
        this.form.cronExpression = '0 ' + m + ' ' + h + ' * * ?'
      } else if (this.form.cronPeriod === 'weekly') {
        if (this.form.cronWeekDays.length === 0) return
        this.form.cronExpression = '0 ' + m + ' ' + h + ' * * ' + [...this.form.cronWeekDays].sort().join(',')
      } else if (this.form.cronPeriod === 'monthly') {
        this.form.cronExpression = '0 ' + m + ' ' + h + ' ' + this.form.cronMonthDay + ' * ?'
      }
    },

    parseCron(cron) {
      const p = cron.trim().split(/\s+/)
      const r = {period: 'daily', weekDays: [], monthDay: 1, hour: 0, min: 0}
      if (p.length >= 5) {
        r.min = parseInt(p[1] || '0');
        r.hour = parseInt(p[2] || '0')
        if (p[3] !== '*' && p[3] !== '?') {
          r.period = 'monthly';
          r.monthDay = parseInt(p[3])
        } else if (p[5] && p[5] !== '*' && p[5] !== '?') {
          r.period = 'weekly';
          r.weekDays = p[5].split(',')
        }
      }
      return r
    },

    add() {
      this.editId = null
      this.dialogTitle = '新建任务'
      this.form = {
        taskName: '', reportType: 'DAILY', cronExpression: '0 1 * * * ?',
        cronPeriod: 'daily', cronWeekDays: [], cronMonthDay: 1,
        cronTime: null, enabled: 1
      }
      this.dialogVisible = true
    },

    async edit(row) {
      this.editId = row.id
      this.dialogTitle = '编辑任务'
      const res = await taskGet(row.id)
      const t = res.data
      this.form.taskName = t.taskName
      this.form.reportType = t.reportType
      this.form.cronExpression = t.cronExpression
      this.form.enabled = t.enabled ?? 1
      const cp = this.parseCron(t.cronExpression)
      this.form.cronPeriod = cp.period
      this.form.cronWeekDays = cp.weekDays
      this.form.cronMonthDay = cp.monthDay
      this.form.cronTime = new Date().setHours(cp.hour, cp.min, 0, 0)
      this.dialogVisible = true
    },

    async save() {
      if (!this.form.taskName) {
        this.$message.warning('请选择报表模板');
        return
      }
      if (this.form.cronPeriod === 'weekly' && this.form.cronWeekDays.length === 0) {
        this.$message.warning('请选择至少一个星期');
        return
      }
      if (!this.form.cronTime) {
        this.$message.warning('请选择执行时间');
        return
      }
      this.saving = true
      try {
        const data = {
          taskName: this.form.taskName,
          reportType: this.form.reportType,
          cronExpression: this.form.cronExpression,
          enabled: this.form.enabled
        }
        if (this.editId) {
          data.id = this.editId
          await taskUpdate(data)
          this.$message.success('修改成功')
        } else {
          await taskSave(data)
          this.$message.success('新建成功')
        }
        this.dialogVisible = false
        this.getData()
      } catch (e) {

      }
      this.saving = false
    },

    async enable(row) {
      await taskEnable(row.id)
      this.$message.success('已启用')
      this.getData()
    },

    async disable(row) {
      this.$confirm('确认暂停任务「' + row.taskName + '」？', '提示', {type: 'warning'}).then(async () => {
        await taskDisable(row.id)
        this.$message.success('已暂停')
        this.getData()
      }).catch(() => {
      })
    },

    async runOnce(row) {
      this.$confirm('确认立即执行任务「' + row.taskName + '」？', '提示', {type: 'info'}).then(async () => {
        await taskRunOnce(row.id)
        this.$message.success('已触发执行，报表将稍后自动生成')
      }).catch(() => {
      })
    },

    del(row) {
      this.$confirm('确认删除任务「' + row.taskName + '」？（关联的 Quartz 触发器也将移除）', '提示', {type: 'warning'}).then(async () => {
        await taskRemove(row.id)
        this.$message.success('删除成功')
        this.getData()
      }).catch(() => {
      })
    }
  }
}
</script>
