<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="form" label-width="100px" size="small">
        <el-form-item label="报表标题">
          <el-input v-model="form.title" style="width:400px"/>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="报表类型">
              <el-select v-model="form.reportType" style="width:200px">
                <el-option label="日报" value="DAILY"/>
                <el-option label="月报" value="MONTHLY"/>
                <el-option label="季报" value="QUARTERLY"/>
                <el-option label="年报" value="YEARLY"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板ID">
              <el-input v-model="form.templateId" style="width:200px"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="起始周期">
              <el-date-picker v-model="form.periodStart" type="date" value-format="yyyy-MM-dd" style="width:200px"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束周期">
              <el-date-picker v-model="form.periodEnd" type="date" value-format="yyyy-MM-dd" style="width:200px"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" style="width:400px"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="generate" :loading="genLoading">生成报表</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import {generate as generateApi} from '@/api/report'

export default {
  name: 'ReportGenerate',
  data() {
    return {
      form: {
        title: '', reportType: 'MONTHLY', templateId: '',
        periodStart: '', periodEnd: '', description: ''
      },
      genLoading: false
    }
  },
  methods: {
    async generate() {
      if (!this.form.title) {
        this.$message.warning('请输入报表标题');
        return
      }
      this.genLoading = true
      try {
        await generateApi(this.form)
        this.$message.success('报表生成成功')
        this.reset()
      } finally {
        this.genLoading = false
      }
    },
    reset() {
      this.form = {title: '', reportType: 'MONTHLY', templateId: '', periodStart: '', periodEnd: '', description: ''}
    }
  }
}
</script>
