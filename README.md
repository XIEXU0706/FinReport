# FinReport - 金融业务数据分析与自动化报表平台

基于 Spring Boot 3 + Vue 2 的金融业务数据分析与自动化报表平台，提供报表生成、定时调度、风险预警、AI 智能分析等功能。

## 技术栈

| 模块 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.3.5, JDK 17 |
| ORM | MyBatis, MySQL |
| 前端 | Vue 2, Element UI |
| 定时任务 | Quartz |
| 消息队列 | RabbitMQ |
| 文件存储 | 阿里云 OSS |
| 数据导出 | EasyExcel |
| AI 服务 | Python FastAPI + DeepSeek API |

## 功能模块

- **仪表盘** — 经营数据总览
- **业务数据** — 客户、账户、交易流水、贷款、产品、支行管理
- **经营分析** — 多维度数据分析
- **报表中心** — 报表生成、历史查询、自动定时任务
- **预警模块** — 多指标风险预警规则
- **AI 助手** — 对话式数据查询与分析报告生成
- **系统管理** — 用户、角色、操作日志

## 快速启动

### 后端

```bash
# 复制个人凭证配置
cp config/credentials.example.yml config/credentials.yml
# 编辑 config/credentials.yml 填入实际的数据库密码和 OSS 密钥

# 导入数据库
mysql -u root -p < sql/fin_report.sql

# 启动
mvn spring-boot:run
```

### 前端

```bash
cd frontend
npm install
npm run serve
```

### AI 服务

```bash
cd ai-service/backend
pip install -r requirements.txt
python -m app.main
```

## 项目结构

```
FinReport/
├── src/                          # Java 后端
│   └── main/java/org/example/finreport/
│       ├── common/               # 公共组件（AOP 日志、OSS、分页）
│       ├── module/
│       │   ├── alert/            # 预警模块
│       │   ├── auth/             # 认证模块
│       │   ├── dashboard/        # 仪表盘
│       │   ├── report/           # 报表模块
│       │   └── system/           # 系统管理
│       └── resources/
│           ├── application.yml   # 主配置（模板）
│           └── mapper/           # MyBatis XML
├── frontend/                     # Vue 前端
├── ai-service/                   # Python AI 服务
├── config/
│   ├── credentials.yml           # 个人凭证（不提交）
│   └── credentials.example.yml   # 凭证模板
└── sql/
    └── fin_report.sql            # 完整建库脚本
```

## 配置文件说明

`application.yml` 使用 `${}` 占位符引用外部配置，个人凭证统一放在 `config/credentials.yml`（已加入 `.gitignore`），首次使用请复制模板并填入真实值：

```bash
cp config/credentials.example.yml config/credentials.yml
```
