<template>
  <div class="page">
    <el-card shadow="never">
      <el-form :model="form" label-width="100px" size="small">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled/>
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
            <el-form-item label="手机号">
              <el-input v-model="form.phone"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import {get} from '@/api/system/user'
import {mapState} from 'vuex'

export default {
  name: 'Profile',
  data() {
    return {
      form: {username: '', realName: '', phone: '', email: ''}
    }
  },
  computed: {...mapState('user', ['userId', 'username'])},
  mounted() {
    this.form.username = this.username
    this.loadProfile()
  },
  methods: {
    async loadProfile() {
      const res = await get(this.userId)
      const u = res.data
      this.form = {username: u.username, realName: u.realName, phone: u.phone, email: u.email}
    }
  }
}
</script>
