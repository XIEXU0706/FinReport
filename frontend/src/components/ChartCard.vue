<template>
  <el-card class="chart-card" shadow="hover">
    <div slot="header" class="chart-header">
      <span>{{ title }}</span>
      <slot name="extra" />
    </div>
    <div :id="chartId" :style="{ height: height + 'px' }" class="chart-body"></div>
  </el-card>
</template>

<script>
import * as echarts from 'echarts'
export default {
  name: 'ChartCard',
  props: {
    title: { type: String, default: '' },
    chartId: { type: String, required: true },
    height: { type: Number, default: 350 },
    option: { type: Object, default: () => ({}) }
  },
  data() {
    return { chart: null }
  },
  watch: {
    option: {
      deep: true,
      handler() { this.$nextTick(this.updateChart) }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.chart = echarts.init(document.getElementById(this.chartId))
      this.updateChart()
    })
  },
  beforeDestroy() {
    if (this.chart) this.chart.dispose()
  },
  methods: {
    updateChart() {
      if (this.chart && this.option) {
        this.chart.setOption(this.option, true)
        this.$nextTick(() => this.chart.resize())
      }
    }
  }
}
</script>

<style scoped>
.chart-card { margin-bottom: 16px; }
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
}
.chart-body { width: 100%; }
</style>
