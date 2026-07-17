<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="query" inline size="small">
        <el-form-item label="支行名称">
          <el-input v-model="query.branchName" clearable @keyup.enter="search"/>
        </el-form-item>
        <el-form-item label="所属区域">
          <el-select v-model="query.region" clearable style="width:130px">
            <el-option label="华北" value="华北"/><el-option label="华东" value="华东"/>
            <el-option label="华南" value="华南"/><el-option label="华中" value="华中"/>
            <el-option label="西南" value="西南"/><el-option label="西北" value="西北"/>
            <el-option label="东北" value="东北"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable style="width:100px">
            <el-option label="启用" value="ACTIVE"/><el-option label="停用" value="INACTIVE"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" style="margin-top:12px">
      <el-button type="primary" size="small" @click="add">新增支行</el-button>
      <el-table :data="list" stripe style="width:100%;margin-top:12px" v-loading="loading">
        <el-table-column prop="branchCode" label="支行编号" width="120"/>
        <el-table-column prop="branchName" label="支行名称" width="160"/>
        <el-table-column prop="region" label="所属区域" width="120"/>
        <el-table-column prop="status" label="状态" width="70">
          <template slot-scope="{row}">
            <el-tag :type="row.status==='ACTIVE'?'success':'danger'" size="mini">{{ row.status==='ACTIVE'?'启用':'停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="150">
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
    <el-dialog :title="form.id?'编辑支行':'新增支行'" :visible.sync="dialogVisible" width="450px">
      <el-form :model="form" label-width="90px" size="small">
        <el-form-item label="支行名称">
          <el-input v-model="form.branchName"/>
        </el-form-item>
        <el-form-item label="所属区域">
          <el-select v-model="form.region" style="width:100%">
            <el-option label="华北" value="华北"/><el-option label="华东" value="华东"/>
            <el-option label="华南" value="华南"/><el-option label="华中" value="华中"/>
            <el-option label="西南" value="西南"/><el-option label="西北" value="西北"/>
            <el-option label="东北" value="东北"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="启用" value="ACTIVE"/><el-option label="停用" value="INACTIVE"/>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary"
                                                                                             @click="save">保存</el-button></span>
    </el-dialog>
    <el-dialog title="支行详情" :visible.sync="detailVisible" width="500px">
      <el-form label-width="100px" size="small" v-if="detail">
        <el-row>
          <el-col :span="12"><el-form-item label="支行编号">: {{ detail.branchCode }}</el-form-item></el-col>
          <el-col :span="12"><el-form-item label="支行名称">: {{ detail.branchName }}</el-form-item></el-col>
        </el-row>
        <el-row>
          <el-col :span="12"><el-form-item label="所属区域">: {{ detail.region }}</el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态">: <el-tag :type="detail.status==='ACTIVE'?'success':'danger'" size="mini">{{ detail.status==='ACTIVE'?'启用':'停用' }}</el-tag></el-form-item></el-col>
        </el-row>
        <el-form-item label="创建时间">: {{ formatDate(detail.createdAt) }}</el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {page as pageApi, save, update, remove, get as getApi} from '@/api/business/branch'

export default {
  name: 'Branch',
  data() {
    return {
      list: [], total: 0, page: 1, size: 10, loading: false, dialogVisible: false,
      query: {branchName: '', region: '', status: ''}, form: {},
      detailVisible: false, detail: {}
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
      this.query = {branchName: '', region: '', status: ''};
      this.search()
    },
    add() {
      this.form = {status: 'ACTIVE'};
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
    }
  }
}
</script>
