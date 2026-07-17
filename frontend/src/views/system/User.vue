<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="query" inline size="small">
        <el-form-item label="用户名">
          <el-input v-model="query.username" clearable @keyup.enter="search"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable style="width:120px">
            <el-option label="正常" value="NORMAL"/>
            <el-option label="停用" value="DISABLED"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" style="margin-top:12px">
      <el-button type="primary" size="small" @click="add">新增用户</el-button>
      <el-table :data="list" stripe style="width:100%;margin-top:12px" v-loading="loading">
        <el-table-column prop="username" label="用户名" width="100"/>
        <el-table-column prop="realName" label="真实姓名" width="100"/>
        <el-table-column prop="phone" label="手机号" width="130"/>
        <el-table-column prop="email" label="邮箱" width="130"/>
        <el-table-column prop="status" label="状态" width="70">
          <template slot-scope="{row}">
            <el-tag :type="row.status==='NORMAL'?'success':'danger'" size="mini">
              {{ row.status === 'NORMAL' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginIp" label="最后登录IP" width="130"/>
        <el-table-column prop="lastLoginTime" label="最后登录" width="140"/>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" @click="edit(row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination @current-change="getData" :current-page="page" :page-size="size" :total="total"
                     layout="total,prev,pager,next" style="margin-top:12px;text-align:right"/>
    </el-card>
    <el-dialog :title="form.id?'编辑用户':'新增用户'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="90px" size="small">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="form.username"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名">
              <el-input v-model="form.realName"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="密码">
              <el-input v-model="form.password" type="password" show-password/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="邮箱">
          <el-input v-model="form.email"/>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary"
                                                                                             @click="save">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import {page as pageApi, save, update, remove} from '@/api/system/user'

export default {
  name: 'SysUser',
  data() {
    return {
      list: [], total: 0, page: 1, size: 10, loading: false, dialogVisible: false,
      query: {username: '', status: ''}, form: {}
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
      this.query = {username: '', status: ''};
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
    }
  }
}
</script>
