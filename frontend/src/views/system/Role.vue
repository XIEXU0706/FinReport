<template>
  <div class="page">
    <el-card shadow="never">
      <el-button type="primary" size="small" @click="add">新增角色</el-button>
      <el-table :data="list" stripe style="width:100%;margin-top:12px" v-loading="loading">
        <el-table-column prop="roleName" label="角色名称" width="150"/>
        <el-table-column prop="roleCode" label="角色编码" width="150"/>
        <el-table-column prop="description" label="描述" min-width="200"/>
        <el-table-column prop="sortOrder" label="排序" width="60"/>
        <el-table-column prop="status" label="状态" width="70">
          <template slot-scope="{row}">
            <el-tag :type="row.status==='NORMAL'?'success':'danger'" size="mini">
              {{ row.status === 'NORMAL' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
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
    <el-dialog :title="form.id?'编辑角色':'新增角色'" :visible.sync="dialogVisible" width="450px">
      <el-form :model="form" label-width="90px" size="small">
        <el-form-item label="角色名称">
          <el-input v-model="form.roleName"/>
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="form.roleCode"/>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description"/>
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model="form.sortOrder"/>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary"
                                                                                             @click="save">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import {page as pageApi, save, update, remove} from '@/api/system/role'

export default {
  name: 'SysRole',
  data() {
    return {
      list: [], total: 0, page: 1, size: 10, loading: false, dialogVisible: false, form: {}
    }
  },
  mounted() {
    this.getData()
  },
  methods: {
    async getData(p) {
      if (p) this.page = p
      this.loading = true
      const res = await pageApi({page: this.page, size: this.size})
      this.list = res.data.records;
      this.total = res.data.total
      this.loading = false
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
