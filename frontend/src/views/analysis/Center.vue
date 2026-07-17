<template>
  <div class="analysis">
    <el-tabs v-model="activeTab">
      <!-- 交易分析  -->
      <el-tab-pane label="交易分析" name="trade">
        <div style="margin-bottom:12px">
          <span style="margin-right:8px">分析天数:</span>
          <el-radio-group v-model="tradeDays" @change="fetchTrade" size="small">
            <el-radio-button :label="7">7天</el-radio-button>
            <el-radio-button :label="30">30天</el-radio-button>
            <el-radio-button :label="90">90天</el-radio-button>
          </el-radio-group>
        </div>

        <!--  四个小标签   -->
        <el-row :gutter="16">
          <el-col :span="6" v-for="s in tradeSummary" :key="s.label">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value">{{ formatMoney(s.value) }}</div>
              <div class="stat-label">{{ s.label }}</div>
            </el-card>
          </el-col>
        </el-row>

        <!--  四个ECharts图  -->
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <ChartCard title="每日交易趋势" chart-id="tradeDaily" :option="tradeDailyOption"/>
          </el-col>
          <el-col :span="12">
            <ChartCard title="渠道占比" chart-id="tradeChannel" :option="tradeChannelOption" :height="300"/>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <ChartCard title="交易类型分布" chart-id="tradeTypeDist" :option="tradeTypeDistOption" :height="300"/>
          </el-col>
          <el-col :span="12">
            <ChartCard title="24小时分布" chart-id="tradeHourly" :option="tradeHourlyOption" :height="300"/>
          </el-col>
        </el-row>

        <!--  大额交易数据  -->
        <el-card shadow="never" style="margin-top:16px">
          <div slot="header">
            <span>大额交易（≥10万）</span>
          </div>
          <el-table :data="largeTrades" stripe size="small" v-loading="loading" max-height="300">
            <el-table-column prop="transNo" label="交易编号" width="170"/>
            <el-table-column prop="customerName" label="客户名称" width="120"/>
            <el-table-column prop="transType" label="类型" width="80">
              <template slot-scope="{row}">{{
                  {
                    DEPOSIT: '存款',
                    WITHDRAW: '取款',
                    TRANSFER: '转账',
                    PAYMENT: '消费'
                  }[row.transType] || row.transType
                }}
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="120">
              <template slot-scope="{row}">{{ row.amount?.toLocaleString() }}</template>
            </el-table-column>
            <el-table-column prop="channel" label="渠道" width="90">
              <template slot-scope="{row}">{{
                  {
                    COUNTER: '柜面',
                    ONLINE: '网银',
                    MOBILE: '手机银行',
                    ATM: 'ATM'
                  }[row.channel] || row.channel
                }}
              </template>
            </el-table-column>
            <el-table-column prop="transTime" label="时间" width="150">
              <template slot-scope="{row}">{{ row.transTime?.replace('T', ' ') }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 客户分析 -->
      <el-tab-pane label="客户分析" name="customer">
        <el-row :gutter="16">
          <el-col :span="6" v-for="s in customerSummary" :key="s.label">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value">{{ s.value }}</div>
              <div class="stat-label">{{ s.label }}</div>
            </el-card>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <ChartCard title="年龄分布" chart-id="ageDist" :option="ageDistOption" :height="300"/>
          </el-col>
          <el-col :span="12">
            <ChartCard title="月增长趋势" chart-id="monthlyGrowth" :option="monthlyGrowthOption" :height="300"/>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <ChartCard title="客户等级分布" chart-id="levelDist" :option="levelDistOption" :height="300"/>
          </el-col>
          <el-col :span="12">
            <ChartCard title="地区分布" chart-id="regionDist" :option="regionDistOption" :height="300"/>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!--  贷款分析    -->
      <el-tab-pane label="贷款分析" name="loan">
        <el-row :gutter="16">
          <el-col :span="6" v-for="s in loanSummary" :key="s.label">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value">{{ formatMoney(s.value) }}</div>
              <div class="stat-label">{{ s.label }}</div>
            </el-card>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <ChartCard title="贷款状态分布" chart-id="loanStatusDist" :option="loanStatusOption" :height="300"/>
          </el-col>
          <el-col :span="12">
            <ChartCard title="逾期分析" chart-id="overdueAnalysis" :option="overdueOption" :height="300"/>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <ChartCard title="期限分布" chart-id="loanTermDist" :option="loanTermOption" :height="300"/>
          </el-col>
          <el-col :span="12">
            <ChartCard title="月度放贷趋势" chart-id="loanMonthlyTrend" :option="loanMonthlyOption" :height="300"/>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!--  产品分析    -->
      <el-tab-pane label="产品分析" name="product">
        <el-row :gutter="16">
          <el-col :span="12">
            <ChartCard title="产品销售排行" chart-id="productRank" :option="productRankOption" :height="350"/>
          </el-col>
          <el-col :span="12">
            <ChartCard title="产品类型分布" chart-id="productTypeDist" :option="productTypeDistOption" :height="350"/>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import ChartCard from '@/components/ChartCard'
import {trade as tradeApi, customer as customerApi, loan as loanApi, product as productApi} from '@/api/analysis'

export default {
  name: 'AnalysisCenter',
  components: {
    ChartCard
  },
  data() {
    return {
      activeTab: 'trade',
      tradeDays: 30,
      loading: false,
      tradeSummary: [],
      tradeDaily: [],
      tradeChannel: [],
      tradeTypeDist: [],
      tradeHourly: [],
      largeTrades: [],
      customerSummary: [],
      ageDist: [],
      monthlyGrowth: [],
      levelDist: [],
      regionDist: [],
      loanSummary: [],
      overdueAnalysis: [],
      loanStatusDist: [],
      loanTermDist: [],
      loanMonthlyTrend: [],
      productSalesRank: [],
      productTypeDist: []
    }
  },
  computed: {
    // 交易趋势
    tradeDailyOption() {
      return {
        // 当鼠标悬停在图表上时，弹出一个浮层显示数据详情
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {type: 'category', data: this.tradeDaily.map(i => i.date), axisLabel: {rotate: 45}},
        yAxis: [
          {type: 'value', axisLabel: {formatter: v => v.toLocaleString() + '元'}},
          {type: 'value', axisLabel: {formatter: v => v + '次'}, splitLine: {show: false}}
        ],
        series: [
          {name: '金额', type: 'line', data: this.tradeDaily.map(i => i.amount), smooth: true},
          {name: '笔数', type: 'bar', yAxisIndex: 1, data: this.tradeDaily.map(i => i.count)}
        ],
        legend: {data: ['金额', '笔数']},
        grid: {left: '14%', right: '8%', bottom: '18%'}
      }
    },

    // 交易渠道
    tradeChannelOption() {
      return {
        tooltip: {
          trigger: 'item',
          formatter: (params) => {
            // params.data 就是你的原始数据项
            return `${params.name}: ${params.data.amount.toLocaleString()}元\n占比: (${params.value}%)`
          }
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            data: this.tradeChannel,
            label: {
              formatter: (params) => {
                return `${params.name}\n${params.value}%`
              }
            }
          }
        ]
      }
    },

    // 交易类型
    tradeTypeDistOption() {
      return {
        tooltip: {
          trigger: 'item',
          formatter: (params) => {
            // params.data 就是你的原始数据项
            return `${params.name}: ${params.data.amount.toLocaleString()}元\n占比: (${params.value}%)`
          }
        },
        series: [
            {
              type: 'pie',
              data: this.tradeTypeDist,
              label: {
                formatter: (params) => {
                  return `${params.name}\n${params.value}%`
                }
              }
            }
        ]
      }
    },

    // 交易时段
    tradeHourlyOption() {
      return {
        tooltip: {trigger: 'axis'},
        xAxis: {type: 'category', data: this.tradeHourly.map(i => i.hour + '时')},
        yAxis: {type: 'value'},
        series: [{type: 'line', data: this.tradeHourly.map(i => i.amount), smooth: true, areaStyle: {opacity: .3}}],
        grid: {left: '14%', right: '4%', bottom: '10%'}
      }
    },

    ageDistOption() {
      return {
        tooltip: {trigger: 'axis'},
        xAxis: {type: 'category', data: this.ageDist.map(i => i.ageGroup)},
        yAxis: {type: 'value'},
        series: [{type: 'bar', data: this.ageDist.map(i => i.count), itemStyle: {color: '#409EFF'}, barWidth: '50%'}],
        grid: {left: '6%', right: '4%', bottom: '10%'}
      }
    },
    monthlyGrowthOption() {
      return {
        tooltip: {trigger: 'axis'},
        xAxis: {type: 'category', data: this.monthlyGrowth.map(i => i.month)},
        yAxis: {type: 'value'},
        series: [{
          type: 'bar',
          data: this.monthlyGrowth.map(i => i.newCustomers),
          itemStyle: {color: '#67C23A'},
          barWidth: '50%'
        }],
        grid: {left: '6%', right: '4%', bottom: '10%'}
      }
    },
    levelDistOption() {
      return {
        tooltip: {
          trigger: 'item',
          formatter: (params) => {
            return `${params.name}: ${params.value} (${params.data.pct}%)`
          }
        },
        series: [
            {
              type: 'pie',
              radius: ['35%', '65%'],
              data: this.levelDist,
              label: {
                formatter: (params) => {
                  return `${params.name}\n${params.data.pct}%`
                }
              }
            }
        ]
      }
    },
    regionDistOption() {
      return {
        tooltip: {
          trigger: 'item',
          formatter: (params) => {
            return `${params.name}: ${params.value} (${params.data.pct}%)`
          }
        },
        series: [{type: 'pie', radius: ['35%', '65%'], data: this.regionDist, label: {formatter: (params) => `${params.name}\n${params.data.pct}%`}}]
      }
    },
    overdueOption() {
      return {
        tooltip: {trigger: 'axis'},
        xAxis: {type: 'category', data: this.overdueAnalysis.map(i => i.month || i.category || '')},
        yAxis: [{type: 'value', name: '金额'}, {type: 'value', name: '比率%', max: 100}],
        series: [
          {name: '逾期金额', type: 'bar', data: this.overdueAnalysis.map(i => i.overdueAmount), yAxisIndex: 0},
          {name: '逾期率', type: 'line', data: this.overdueAnalysis.map(i => i.overdueRate), yAxisIndex: 1}
        ],
        legend: {data: ['逾期金额', '逾期率']},
        grid: {left: '14%', right: '8%', bottom: '10%'}
      }
    },
    loanStatusOption() {
      return {
        tooltip: {
          trigger: 'item',
          formatter: (params) => {
            return `${params.name}: ${params.value} (${params.data.pct}%)`
          }
        },
        series: [{
          type: 'pie',
          radius: ['35%', '65%'],
          data: this.loanStatusDist,
          label: {
            formatter: (params) => {
              return `${params.name}\n${params.data.pct}%`
            }
          }
        }]
      }
    },
    loanTermOption() {
      return {
        tooltip: {trigger: 'axis'},
        xAxis: {type: 'category', data: this.loanTermDist.map(i => i.term)},
        yAxis: {type: 'value', axisLabel: {formatter: v => v.toLocaleString() + '元'}},
        series: [{
          type: 'bar',
          data: this.loanTermDist.map(i => i.amount),
          itemStyle: {color: '#409EFF'},
          barWidth: '50%'
        }],
        grid: {left: '14%', right: '4%', bottom: '10%'}
      }
    },
    loanMonthlyOption() {
      return {
        tooltip: {trigger: 'axis'},
        xAxis: {type: 'category', data: this.loanMonthlyTrend.map(i => i.month)},
        yAxis: {type: 'value', axisLabel: {formatter: v => v.toLocaleString() + '元'}},
        series: [{
          type: 'bar',
          data: this.loanMonthlyTrend.map(i => i.amount),
          itemStyle: {color: '#E6A23C'},
          barWidth: '50%'
        }],
        grid: {left: '14%', right: '4%', bottom: '10%'}
      }
    },
    productRankOption() {
      return {
        tooltip: {trigger: 'axis'},
        xAxis: {type: 'category', data: this.productSalesRank.map(i => i.name), axisLabel: {rotate: 0, interval: 0, formatter: v => v.split('').join('\n')}},
        yAxis: {type: 'value', axisLabel: {formatter: v => v.toLocaleString() + '元'}},
        series: [{type: 'bar', data: this.productSalesRank.map(i => i.saleAmount), itemStyle: {color: '#E6A23C'}}],
        grid: {left: '14%', right: '4%', bottom: '32%'}
      }
    },
    productTypeDistOption() {
      return {
        tooltip: {
          trigger: 'item',
          formatter: (params) => {
            return `${params.name}: ${params.value} (${params.data.pct}%)`
          }
        },
        series: [{
          type: 'pie',
          radius: ['35%', '65%'],
          data: this.productTypeDist,
          label: {
            formatter: (params) => {
              return `${params.name}\n${params.data.pct}%`
            }
          }
        }]
      }
    }
  },
  mounted() {
    this.fetchTrade()
  },
  methods: {
    // 获取交易数据
    async fetchTrade() {
      const res = await tradeApi({days: this.tradeDays})
      const d = res.data
      console.log(d)
      this.tradeSummary = [
        {label: '总交易额', value: d.summary?.totalAmount},
        {label: '总笔数', value: d.summary?.totalCount},
        {label: '平均金额', value: d.summary?.avgAmount},
        {label: '存款总额', value: d.summary?.depositAmount}
      ]
      this.tradeDaily = d.dailyTrend || []
      this.tradeChannel = (d.channelRatio || []).map(i => ({
        name: {
          COUNTER: '柜面',
          ONLINE: '网银',
          MOBILE: '手机银行',
          ATM: 'ATM'
        }[i.name] || i.name,
        value: i.pct,
        amount: i.amount
      }))
      console.log("++++++++++++++++++++++")
      console.log(this.tradeChannel)
      console.log("++++++++++++++++++++++")
      this.tradeTypeDist = (d.typeDist || []).map(i => ({
        name: {
          PAYMENT: '消费',
          WITHDRAW: '取款',
          DEPOSIT: '存款',
          TRANSFER: '转账',
          REPAYMENT: '还款'
        }[i.name] || i.name,
        value: i.pct,
        amount: i.amount
      }))
      this.tradeHourly = d.hourlyDist || []
      this.largeTrades = (d.largeTrades || []).map(i => ({
        transNo: i.trans_no,
        customerName: i.customerName,
        transType: i.trans_type,
        amount: i.amount,
        channel: i.channel,
        transTime: i.trans_time
      }))
    },

    // 客户分析
    async fetchCustomer() {
      const res = await customerApi()
      const d = res.data
      this.customerSummary = [
        {label: '客户总数', value: d.summary?.totalCustomers},
        {label: '活跃客户', value: d.summary?.activeCustomers},
        {label: '钻石客户', value: d.summary?.vipCustomers},
        {label: '平均年龄', value: d.summary?.avgAge}
      ]
      this.ageDist = d.ageDist || []
      this.monthlyGrowth = d.monthlyGrowth || []
      this.levelDist = (d.levelDist || []).map(i => ({
        name: {
          NORMAL: '普通',
          SILVER: '白银',
          GOLD: '黄金',
          PLATINUM: '钻石',
        }[i.name] || i.name, value: i.value, pct: i.pct
      }))
      this.regionDist = (d.regionDist || []).map(i => ({name: i.name, value: i.value, pct: i.pct}))
      console.log(this.regionDist)
    },

    // 贷款分析
    async fetchLoan() {
      const res = await loanApi()
      const d = res.data
      this.loanSummary = [
        {label: '借贷总额', value: d.summary?.totalAmount},
        {label: '到期应收总额', value: d.summary?.totalBalance},
        {label: '平均期限(月)', value: d.summary?.avgTerm},
        {label: '平均利率(%)', value: d.summary?.avgRate}
      ]
      console.log(this.loanSummary)
      this.overdueAnalysis = d.overdueAnalysis || []
      this.loanStatusDist = (d.statusDist || []).map(i => ({
        name: {
          NORMAL: '正常',
          OVERDUE: '逾期',
          SETTLED: '结清',
          BAD: '不还款'
        }[i.name] || i.name, value: i.value, pct: i.pct
      }))
      this.loanTermDist = d.termDist || []
      this.loanMonthlyTrend = d.monthlyTrend || []
    },

    // 产品分析
    async fetchProduct() {
      const res = await productApi()
      const d = res.data
      this.productSalesRank = d.salesRank || []
      this.productTypeDist = (d.typeDist || []).map(i => ({
        name: {
          FINANCIAL: '理财',
          FUND: '基金',
          INSURANCE: '保险',
          DEPOSIT: '存款'
        }[i.name] || i.name,
        value: i.saleAmount || i.amount,
        pct: i.pct
      }))
    },

    // 金额格式化
    formatMoney(v) {
      if (v == null) return '-'
      const n = Number(v)
      if (n >= 1e8) return (n / 1e8).toFixed(2) + '亿'
      if (n >= 1e4) return (n / 1e4).toFixed(2) + '万'
      return n.toLocaleString()
    }
  },
  watch: {
    activeTab(tab) {
      if (tab === 'customer' && !this.customerSummary.length) this.fetchCustomer()
      if (tab === 'loan' && !this.loanSummary.length) this.fetchLoan()
      if (tab === 'product' && !this.productSalesRank.length) this.fetchProduct()
    }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
  margin-bottom: 12px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
</style>
