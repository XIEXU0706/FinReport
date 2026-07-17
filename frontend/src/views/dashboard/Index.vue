<template>
  <div class="dashboard">
    <el-row :gutter="16" class="summary-row">
      <el-col :span="6" v-for="card in summaryCards" :key="card.label">
        <el-card shadow="hover" class="summary-card" @click.native="$router.push(card.route)">
          <div class="summary-value">{{ card.value }}</div>
          <div class="summary-label">{{ card.label }}</div>
          <div class="summary-rate" :class="card.rate >= 0 ? 'up' : 'down'">
            <i :class="card.rate >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            {{ Math.abs(card.rate) }}%
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="24">
        <el-card shadow="hover" style="cursor:pointer;background:linear-gradient(135deg,#FEF0F0,#FFF)"
                 @click.native="$router.push('/alert/log')">
          <div style="display:flex;align-items:center;justify-content:space-between">
            <span><i class="el-icon-warning" style="color:#F56C6C;font-size:18px;margin-right:8px"></i>未处理预警</span>
            <span style="font-size:28px;font-weight:700;color:#F56C6C">{{ alertCount }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12">
        <ChartCard title="支行交易 TOP10" chart-id="topBranches" :option="topBranchesOption" :height="300"/>
      </el-col>
      <el-col :span="12">
        <ChartCard title="利润趋势" chart-id="profitTrend" :option="profitTrendOption" :height="300"/>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="24">
        <ChartCard title="风险预警" chart-id="riskAlerts" :height="200" :option="riskAlertOption"/>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="24">
        <el-card shadow="hover" style="height:250px">
          <div slot="header" style="font-weight:600">待办事项</div>
          <div v-if="todoItems.length">
            <div v-for="(item, idx) in todoItems" :key="idx"
                 style="display:flex;align-items:center;padding:8px 0;border-bottom:1px solid #f0f0f0">
              <i :class="item.icon" :style="{color:item.color,fontSize:'16px',marginRight:'10px'}"></i>
              <span style="flex:1">{{ item.text }}</span>
              <el-tag :type="item.tagType" size="mini">{{ item.count }}</el-tag>
            </div>
          </div>
          <div v-else style="text-align:center;line-height:200px;color:#909399">暂无待办事项</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import ChartCard from '@/components/ChartCard'
import {full} from '@/api/dashboard'
import {count as alertCountApi} from '@/api/alert/log'

export default {
  name: 'Dashboard',
  components: {ChartCard},
  data() {
    return {
      summary: {},
      topBranches: [],
      profitTrend: [],
      riskAlerts: [],
      alertCount: 0
    }
  },
  computed: {
    summaryCards() {
      return [
        {
          label: '今日交易额',
          value: this.formatMoney(this.summary.todayTradeAmount),
          rate: this.summary.tradeGrowthRate || 0,
          route: '/business/trans-log'
        },
        {
          label: '今日放贷额',
          value: this.formatMoney(this.summary.todayLoanAmount),
          rate: this.summary.loanGrowthRate || 0,
          route: '/business/loan'
        },
        {
          label: '新增客户',
          value: this.summary.todayNewCustomers ?? '-',
          rate: this.summary.customerGrowthRate || 0,
          route: '/business/customer'
        },
        {
          label: '今日还款额',
          value: this.formatMoney(this.summary.todayRepaymentAmount),
          rate: this.summary.repaymentGrowthRate || 0,
          route: '/business/loan'
        }
      ]
    },
    topBranchesOption() {
      return {
        tooltip: {trigger: 'axis'},
        xAxis: {type: 'category', data: this.topBranches.map(i => i.branchName), axisLabel: {rotate: 30}},
        yAxis: {type: 'value', axisLabel: {formatter: v => v.toFixed(0) + '万'}},
        series: [{type: 'bar', data: this.topBranches.map(i => i.tradeAmount / 10000), itemStyle: {color: '#67C23A'}}],
        grid: {left: '8%', right: '4%', bottom: '22%'}
      }
    },
    profitTrendOption() {
      return {
        tooltip: {trigger: 'axis'},
        xAxis: {type: 'category', data: this.profitTrend.map(i => i.month)},
        yAxis: {type: 'value'},
        series: [{
          type: 'line', data: this.profitTrend.map(i => i.profit),
          smooth: true, areaStyle: {opacity: .3}, lineStyle: {color: '#F56C6C'},
          itemStyle: {color: '#F56C6C'}
        }],
        grid: {left: '8%', right: '4%', bottom: '22%'}
      }
    },
    riskAlertOption() {
      const colorMap = {HIGH: '#F56C6C', MEDIUM: '#E6A23C', LOW: '#409EFF'}
      const alerts = this.riskAlerts
      const n = Math.min(alerts.length, 4)
      if (n === 0) return {series: []}
      const gap = 100 / (n + 1)
      const series = alerts.slice(0, 4).map((item, idx) => {
        const rate = item.rate || 0
        const color = colorMap[item.level] || '#909399'
        return {
          type: 'pie',
          radius: ['50%', '80%'],
          center: [(gap * (idx + 1)) + '%', '50%'],
          data: [
            {value: rate, itemStyle: {color}},
            {value: 100 - rate, itemStyle: {color: '#F0F0F0'}}
          ],
          label: {
            show: true,
            position: 'center',
            formatter: () => rate + '%',
            fontSize: 14,
            fontWeight: 'bold',
            color: color
          },
          emphasis: {scale: false},
          tooltip: {formatter: () => item.content}
        }
      })
      return {tooltip: {trigger: 'item'}, series}
    },
    todoItems() {
      const items = []
      if (this.alertCount > 0) items.push({
        icon: 'el-icon-warning',
        color: '#F56C6C',
        text: '未处理预警',
        count: this.alertCount,
        tagType: 'danger'
      })
      const overdueCount = this.riskAlerts.filter(r => r.content.includes('逾期')).length
      if (overdueCount > 0) items.push({
        icon: 'el-icon-document',
        color: '#E6A23C',
        text: '贷款逾期监控',
        count: overdueCount,
        tagType: 'warning'
      })
      items.push({icon: 'el-icon-circle-check', color: '#67C23A', text: '系统运行正常', count: '✓', tagType: 'success'})
      return items
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        const res = await full()
        const d = res.data
        this.summary = d.summary || {}
        this.topBranches = d.topBranches || []
        this.profitTrend = d.profitTrend || []
        this.riskAlerts = d.riskAlerts || []
      } catch {
      }
      try {
        const alertRes = await alertCountApi()
        this.alertCount = alertRes.data
      } catch {
      }
    },
    formatMoney(v) {
      if (v == null) return '-'
      const n = Number(v)
      if (n >= 1e8) return (n / 1e8).toFixed(2) + '亿'
      if (n >= 1e4) return (n / 1e4).toFixed(2) + '万'
      return n.toLocaleString()
    }
  }
}
</script>

<style scoped>
.dashboard {
  max-width: 1600px;
  margin: 0 auto;
}

.summary-row {
  margin-bottom: 16px;
}

.summary-card {
  cursor: pointer;
  text-align: center;
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.summary-label {
  font-size: 14px;
  color: #909399;
  margin: 4px 0;
}

.summary-rate {
  font-size: 13px;
}

.summary-rate.up {
  color: #67C23A;
}

.summary-rate.down {
  color: #F56C6C;
}
</style>
