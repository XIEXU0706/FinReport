<template>
  <div class="login">
    <div class="login-bg">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>
    <div class="login-container">
      <!-- 左侧品牌区域 -->
      <div class="login-brand">
        <div class="brand-content">
          <svg viewBox="0 0 24 24" width="48" height="48" fill="#fff" class="brand-icon">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15l-5-5 1.41-1.41L11 14.17l7.59-7.59L20 8l-9 9z"/>
          </svg>
          <h1 class="brand-title">FinReport</h1>
          <p class="brand-subtitle">金融业务数据分析与自动化报表平台</p>
          <div class="brand-features">
            <div class="feature-item">
              <span class="feature-dot"></span>
              实时数据穿透分析
            </div>
            <div class="feature-item">
              <span class="feature-dot"></span>
              自动化报表生成
            </div>
            <div class="feature-item">
              <span class="feature-dot"></span>
              多维度风险预警
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录区域 -->
      <div class="login-form-wrap">
        <div class="login-form-box">
          <div class="form-header">
            <h2 class="form-title">账户登录</h2>
            <p class="form-desc">欢迎使用 FinReport-金融业务数据分析与自动化报表平台</p>
          </div>
          <el-form ref="form" :model="form" :rules="rules" size="medium" class="login-form">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="用户名" prefix-icon="el-icon-user"/>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="el-icon-lock"
                        show-password/>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">登 录</el-button>
            </el-form-item>
          </el-form>
          <div class="form-footer">
            <span>Copyright © 2026 FinReport. All rights reserved.</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      form: {username: 'admin', password: '123456'},
      loading: false,
      rules: {
        username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
        password: [{required: true, message: '请输入密码', trigger: 'blur'}]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await this.$store.dispatch('user/login', this.form)
          this.$message.success('登录成功')
          this.$router.push('/dashboard')
        } catch {
          // error handled by interceptor
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0a1628 0%, #132244 30%, #1a3a5c 70%, #0d2847 100%);
  overflow: hidden;
  font-family: 'Helvetica Neue', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* ===== 背景装饰 ===== */
.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: .15;
}
.shape-1 {
  width: 600px;
  height: 600px;
  background: #4fc3f7;
  top: -200px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}
.shape-2 {
  width: 400px;
  height: 400px;
  background: #7c4dff;
  bottom: -150px;
  left: -100px;
  animation: float 10s ease-in-out infinite reverse;
}
.shape-3 {
  width: 300px;
  height: 300px;
  background: #00e676;
  top: 50%;
  left: 60%;
  animation: float 12s ease-in-out infinite 2s;
}
@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(30px) scale(1.05); }
}

/* ===== 容器 ===== */
.login-container {
  position: relative;
  z-index: 1;
  display: flex;
  width: 900px;
  min-height: 560px;
  background: rgba(255,255,255,.06);
  border-radius: 20px;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 25px 60px rgba(0,0,0,.5);
  overflow: hidden;
  animation: fadeUp .6s ease-out;
}
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ===== 左侧品牌 ===== */
.login-brand {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: linear-gradient(135deg, rgba(64,158,255,.15) 0%, rgba(100,80,255,.1) 100%);
}
.brand-content {
  text-align: center;
  color: #fff;
}
.brand-icon {
  margin-bottom: 16px;
  filter: drop-shadow(0 4px 12px rgba(64,158,255,.4));
}
.brand-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #fff 0%, #90caf9 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.brand-subtitle {
  font-size: 14px;
  opacity: .7;
  margin: 0 0 40px;
  letter-spacing: 4px;
}
.brand-features {
  text-align: left;
  display: inline-block;
}
.feature-item {
  font-size: 14px;
  margin-bottom: 16px;
  opacity: .85;
  display: flex;
  align-items: center;
  gap: 12px;
}
.feature-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #4fc3f7;
  box-shadow: 0 0 8px rgba(79,195,247,.5);
}

/* ===== 右侧登录 ===== */
.login-form-wrap {
  width: 400px;
  background: rgba(255,255,255,.95);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}
.login-form-box {
  width: 100%;
  max-width: 320px;
}
.form-header {
  margin-bottom: 32px;
}
.form-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 8px;
}
.form-desc {
  font-size: 14px;
  color: #718096;
  margin: 0;
}
.form-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 12px;
  color: #a0aec0;
}
.login-btn {
  width: 100%;
  height: 42px;
  font-size: 16px;
  letter-spacing: 4px;
  border: none;
  background: linear-gradient(135deg, #1a73e8, #0d47a1);
  transition: all .3s;
}
.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(26,115,232,.4);
}
.login-btn:active {
  transform: translateY(0);
}
</style>
