<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="query" inline size="small">
        <el-form-item label="客户姓名">
          <el-input v-model="query.name" clearable @keyup.enter="search"/>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="query.phone" clearable @keyup.enter="search"/>
        </el-form-item>
        <el-form-item label="客户等级">
          <el-select v-model="query.customerLevel" clearable style="width:120px">
            <el-option label="普通" value="NORMAL"/>
            <el-option label="白银" value="SILVER"/>
            <el-option label="白金" value="PLATINUM"/>
            <el-option label="黄金" value="GOLD"/>
            <el-option label="钻石" value="DIAMOND"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable style="width:120px">
            <el-option label="正常" value="ACTIVE"/>
            <el-option label="停用" value="INACTIVE"/>
            <el-option label="冻结" value="FROZEN"/>
            <el-option label="销户" value="CLOSED"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" style="margin-top:12px">
      <el-button type="primary" size="small" @click="add">新增客户</el-button>
      <el-table :data="list" stripe style="width:100%;margin-top:12px" v-loading="loading">
        <el-table-column prop="id" label="客户ID" width="120"/>
        <el-table-column prop="customerNo" label="客户编号" width="120"/>
        <el-table-column prop="name" label="姓名" width="100"/>
        <el-table-column prop="gender" label="性别" width="50"/>
        <el-table-column prop="phone" label="手机号" width="130"/>
        <el-table-column prop="customerLevel" label="等级" width="80">
          <template slot-scope="{row}">
            <el-tag :type="levelTag(row.customerLevel)" :style="levelTagStyle(row.customerLevel)" size="mini">
              {{ levelMap[row.customerLevel] || row.customerLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="idType" label="证件类型" width="90">
          <template slot-scope="{row}">
            {{ idTypeMap[row.idType] || row.idType }}
          </template>
        </el-table-column>
        <el-table-column prop="idNumber" label="证件号" min-width="160"/>
        <el-table-column prop="address" label="地址" min-width="160"/>
        <el-table-column prop="status" label="状态" width="70">
          <template slot-scope="{row}">
            <el-tag :type="statusTag(row.status)" size="mini">
              {{ statusMap[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="openDate" label="开户日期" width="150">
          <template slot-scope="{row}">{{ formatDate(row.openDate) }}</template>
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
    <el-dialog :title="form.id?'编辑客户':'新增客户'" :visible.sync="dialogVisible" width="600px">
      <el-form ref="form" :model="form" :rules="rules" label-width="90px" size="small">
        <el-row>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" style="width:100%">
                <el-option label="男" value="男"/>
                <el-option label="女" value="女"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" maxlength="11"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户等级" prop="customerLevel">
              <el-select v-model="form.customerLevel" style="width:100%">
                <el-option label="普通" value="NORMAL"/>
                <el-option label="白银" value="SILVER"/>
                <el-option label="白金" value="PLATINUM"/>
                <el-option label="黄金" value="GOLD"/>
                <el-option label="钻石" value="DIAMOND"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width:100%">
                <el-option label="正常" value="ACTIVE"/>
                <el-option label="停用" value="INACTIVE"/>
                <el-option label="冻结" value="FROZEN"/>
                <el-option label="销户" value="CLOSED"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="证件类型" prop="idType">
              <el-select v-model="form.idType" style="width:100%">
                <el-option label="身份证" value="ID_CARD"/>
                <el-option label="护照" value="PASSPORT"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证件号" prop="idNumber">
              <el-input v-model="form.idNumber"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职业">
              <el-input v-model="form.occupation"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址">
          <el-input v-model="form.address"/>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </span>
    </el-dialog>
    <el-dialog title="客户详情" :visible.sync="detailVisible" width="550px">
      <el-form label-width="100px" size="small" v-if="detail">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户编号">: {{ detail.customerNo }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户ID">: {{ detail.id }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="姓名">: {{ detail.name }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">: {{ detail.gender }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号">: {{ detail.phone }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户等级">: {{ levelMap[detail.customerLevel] || detail.customerLevel }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="证件类型">: {{ idTypeMap[detail.idType] || detail.idType }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证件号">: {{ detail.idNumber }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮箱">: {{ detail.email }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职业">: {{ detail.occupation }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态">: {{ statusMap[detail.status] || detail.status }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开户日期">: {{ formatDate(detail.openDate) }}</el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址">{{ detail.address }}</el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {page as pageApi, save, update, remove, get as getApi} from '@/api/business/customer'

export default {
  name: 'Customer',
  data() {
    return {
      list: [],
      total: 0,
      page: 1,
      size: 10,
      loading: false,
      dialogVisible: false,
      query: {name: '', phone: '', customerLevel: '', status: ''},
      form: {},
      detailVisible: false,
      detail: {},
      statusMap: {ACTIVE: '正常', INACTIVE: '停用', FROZEN: '冻结', CLOSED: '销户'},
      levelMap: {NORMAL: '普通', SILVER: '白银', GOLD: '黄金', PLATINUM: '白金', DIAMOND: '钻石'},
      idTypeMap: {ID_CARD: '身份证', PASSPORT: '护照'},
      rules: {
        name: [{required: true, message: '请输入姓名', trigger: 'blur'}],
        gender: [{required: true, message: '请选择性别', trigger: 'change'}],
        phone: [
          {required: true, message: '请输入手机号', trigger: 'blur'},
          {pattern: /^1\d{10}$/, message: '手机号格式不正确（11位数字，1开头）', trigger: 'blur'}
        ],
        customerLevel: [{required: true, message: '请选择客户等级', trigger: 'change'}],
        idType: [{required: true, message: '请选择证件类型', trigger: 'change'}],
        idNumber: [
          {required: true, message: '请输入证件号', trigger: 'blur'},
          {validator: this.validateIdNumber, trigger: 'blur'}
        ],
        email: [
          {pattern: /^$|^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: '邮箱格式不正确', trigger: 'blur'}
        ]
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
      const res = await pageApi({page: this.page, size: this.size, ...this.query})
      this.list = res.data.records;
      console.log(res.data.records)
      this.total = res.data.total
      this.loading = false
    },
    search() {
      this.page = 1;
      this.getData()
    },
    reset() {
      this.query = {name: '', phone: '', customerLevel: '', status: ''};
      this.search()
    },
    add() {
      this.form = {};
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form?.clearValidate())
    },
    edit(row) {
      this.form = {...row};
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form?.clearValidate())
    },
    async viewDetail(row) {
      const res = await getApi(row.id)
      this.detail = res.data
      this.detailVisible = true
    },
    async save() {
      try {
        await this.$refs.form.validate()
      } catch {
        return
      }
      if (this.form.id) {
        await update(this.form)
      } else {
        await save(this.form)
      }
      this.$message.success('保存成功')
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
    levelTag(l) {
      if (l === 'SILVER') return ''
      return {
        NORMAL: 'info',
        GOLD: 'warning',
        PLATINUM: 'success',
        DIAMOND: 'danger'
      }[l] || 'info'
    },
    levelTagStyle(l) {
      return l === 'SILVER' ? 'background:#d8d8d8;color:#555;border-color:#c0c0c0' : ''
    },
    statusTag(s) {
      return {ACTIVE: 'success', INACTIVE: 'danger', FROZEN: 'warning', CLOSED: 'info'}[s] || 'info'
    },
    formatDate(d) {
      if (!d) return ''
      return d.replace('T', ' ')
    },

    validateIdNumber(rule, value, callback) {
      if (!value) return callback()
      if (this.form.idType === 'ID_CARD') {
        if (!/^\d{17}[\dX]$/.test(value.toUpperCase())) {
          return callback(new Error('身份证号必须为18位（数字或末位X）'))
        }
      } else if (this.form.idType === 'PASSPORT') {
        if (value.length < 5 || value.length > 20) {
          return callback(new Error('护照号长度不正确'))
        }
      }
      callback()
    }
  }
}
</script>
