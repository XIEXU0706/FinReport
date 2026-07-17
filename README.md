# FinReport - 金融业务数据分析与自动化报表平台

基于 Spring Boot 3 + Vue 2 的企业级金融业务数据分析平台，提供多维度业务分析、自动化报表生成与调度、风险预警监控、AI 智能分析对话等功能。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.3.5 |
| JDK | Java | 17 |
| 安全框架 | Spring Security + JWT | 0.12.6 |
| ORM | MyBatis + JPA/Hibernate | 3.0.4 |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis | - |
| 消息队列 | RabbitMQ | - |
| 定时调度 | Quartz | - |
| 文件存储 | 阿里云 OSS | 3.18.1 |
| Excel 处理 | EasyExcel | 4.0.3 |
| API 文档 | Knife4j | 4.5.0 |
| 前端框架 | Vue 2 | 2.7.14 |
| UI 组件 | Element UI | 2.15.14 |
| 可视化 | ECharts | 5.5.1 |
| 状态管理 | Vuex | 3.6.2 |
| AI 服务 | Python FastAPI + DeepSeek | - |

## 功能模块

### 经营驾驶舱
- 核心指标总览：交易额、贷款额、新增客户、还款额的今日与昨日对比及增长率
- Top 支行交易排名
- 月度利润趋势
- 风险预警概览
- 一键加载全量看板数据

### 业务数据管理
- **客户管理** — 客户信息增删改查，按姓名、手机号、等级、状态筛选
- **账户管理** — 账户信息管理，按账户类型、币种、状态筛选
- **交易流水** — 交易记录查询，按类型、渠道、日期范围筛选
- **贷款业务** — 贷款记录管理，按状态、还款方式、日期筛选
- **产品管理** — 理财产品增删改查，按类型、风险等级筛选
- **支行信息** — 支行机构管理，按区域、状态筛选
- **产品销售记录** — 产品销售明细查询

### 经营分析
- **交易分析** — 交易总览、每日趋势、渠道占比、时段分布、大额交易、类型分布（30 天）
- **客户分析** — 客户总览、等级分布、月度增长趋势（12 个月）、年龄分布、地区分布
- **贷款分析** — 贷款总览、状态分布、月度趋势、期限分布、逾期分析
- **产品分析** — 产品销量排名、类型分布、收益率排名

### 报表中心
- **报表生成** — 支持 5 种报表类型：

| 报表类型 | 内容 |
|----------|------|
| DAILY | 每日交易报表（日期、交易笔数、总金额、失败笔数） |
| MONTHLY | 月度交易报表（月份、交易笔数、金额） |
| LOAN_DAILY | 每日贷款报表（日期、贷款笔数、总金额、逾期笔数） |
| LOAN_WEEKLY | 每周贷款报表（周、贷款笔数、总金额、逾期笔数） |
| LOAN_MONTHLY | 每月贷款报表（月份、贷款笔数、总金额、逾期笔数） |

- **异步生成** — 通过 RabbitMQ 消息队列异步生成，队列不可用时自动回退同步模式
- **文件存储** — 生成的 Excel 上传至阿里云 OSS，支持下载
- **报表审核** — 报表生成后可进行审核操作
- **自动报表任务** — 基于 Quartz 的定时任务配置，支持 Cron 表达式，可启用/停用/立即执行
- **历史查询** — 按报表类型、标题、状态筛选，分页展示

### 预警中心
- **规则配置** — 自定义预警规则（指标 + 运算符 + 阈值 + 严重级别）

| 指标 | 说明 | 数据源 |
|------|------|--------|
| TRADE_AMOUNT_DAILY | 每日成功交易总额 | trans_log |
| LOAN_OVERDUE_RATE | 贷款逾期率（百分比） | loan_record |
| NEW_CUSTOMERS_DAILY | 每日新增客户数 | customer |
| LARGE_TRADE_COUNT | 每日大额交易笔数（>10万） | trans_log |
| LOAN_AMOUNT_DAILY | 每日贷款总额 | loan_record |

- **预警日志** — 自动检查（每日 18:30 定时执行）、预警列表展示、确认/处理操作、未处理数量统计
- **严重级别** — LOW / MEDIUM / HIGH / CRITICAL

### AI 智能助手
- **对话式数据查询** — 自然语言查询业务数据（交易额、贷款情况、客户统计等）
- **经营分析报告** — AI 根据最新数据自动撰写分析报告
- **数据对接** — 通过 LangChain Agent + 自定义 Tool 查询 MySQL 实时数据
- **SSE 流式输出** — 实时流式返回 AI 回答
- **会话管理** — 多会话支持，历史记录保存与切换
- **Markdown 渲染** — AI 回答中的 Markdown 内容在前端格式化展示
- **Word 导出** — 将 AI 回答导出为 .docx 文件
- 后端服务：Python FastAPI（端口 9096），使用 DeepSeek Chat API

### 系统管理
- **用户管理** — 系统用户增删改查
- **角色管理** — 角色配置（管理员/普通用户）
- **菜单管理** — 动态菜单树配置
- **操作日志** — 基于 AOP 切面的自动操作记录，记录操作人、IP、耗时、状态等

## API 接口总览

### 认证
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 登录，返回 JWT Token |
| GET | `/api/auth/info` | 获取当前用户信息 |

### 经营看板
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/dashboard/summary` | 核心指标总览 |
| GET | `/api/dashboard/top-branches` | Top 支行排名 |
| GET | `/api/dashboard/profit-trend` | 月度利润趋势 |
| GET | `/api/dashboard/risk-alerts` | 风险预警列表 |
| GET | `/api/dashboard/full` | 全量看板数据 |

### 业务数据
| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 客户 | GET/POST/PUT/DELETE | `/api/business/customer/**` | CRUD + 分页 + 列表 |
| 账户 | GET/POST/PUT/DELETE | `/api/business/account/**` | CRUD + 分页 + 列表 |
| 交易 | GET | `/api/business/trans-log/**` | 分页查询 + 详情 |
| 贷款 | GET/POST/PUT/DELETE | `/api/business/loan/**` | CRUD + 分页 |
| 产品 | GET/POST/PUT/DELETE | `/api/business/product/**` | CRUD + 分页 + 列表 |
| 支行 | GET/POST/PUT/DELETE | `/api/business/branch/**` | CRUD + 分页 + 列表 |
| 销售 | GET | `/api/business/prod-sale/page` | 分页查询 |

### 经营分析
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/analysis/trade` | 交易分析（30 天趋势/渠道/时段） |
| GET | `/api/analysis/customer` | 客户分析（等级/增长/年龄/地区） |
| GET | `/api/analysis/loan` | 贷款分析（状态/趋势/期限/逾期） |
| GET | `/api/analysis/product` | 产品分析（销量/类型/收益率排名） |

### 预警中心
| 方法 | 路径 | 说明 |
|------|------|------|
| GET/POST/PUT/DELETE | `/api/alert/rule/**` | 预警规则 CRUD |
| GET | `/api/alert/log/page` | 预警日志分页 |
| GET | `/api/alert/log/latest` | 最新 N 条预警 |
| GET | `/api/alert/log/count` | 未处理预警数量 |
| PUT | `/api/alert/log/ack/{id}` | 确认预警 |
| PUT | `/api/alert/log/resolve/{id}` | 处理预警 |

### 报表中心
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/report/page` | 报表分页查询 |
| POST | `/api/report/generate` | 生成报表（异步/同步回退） |
| PUT | `/api/report/approve/{id}` | 审核报表 |
| DELETE | `/api/report/{id}` | 删除报表 |
| GET | `/api/report/download/{id}` | 下载报表 Excel |
| GET | `/api/report/export` | 导出报表列表 |
| POST | `/api/report/task` | 创建定时任务 |
| PUT | `/api/report/task` | 编辑定时任务 |
| DELETE | `/api/report/task/{id}` | 删除定时任务 |
| PUT | `/api/report/task/enable/{id}` | 启用任务 |
| PUT | `/api/report/task/disable/{id}` | 停用任务 |
| POST | `/api/report/task/run/{id}` | 立即执行 |

### 系统管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET/POST/PUT/DELETE | `/api/system/user/**` | 用户 CRUD |
| GET/POST/PUT/DELETE | `/api/system/role/**` | 角色 CRUD |
| GET/POST/PUT/DELETE | `/api/system/menu/**` | 菜单管理 |
| GET | `/api/system/oper-log/page` | 操作日志分页 |

### AI 服务
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/sessions` | 会话列表 |
| POST | `/sessions` | 创建会话 |
| DELETE | `/sessions/{id}` | 删除会话 |
| GET | `/sessions/{id}/messages` | 获取消息 |
| POST | `/fin_agent/ask` | AI 对话（SSE 流式） |
| POST | `/export/word` | 导出 Word |

## 项目结构

```
FinReport/
├── src/main/java/org/example/finreport/
│   ├── common/                     # 公共组件
│   │   ├── aop/                    # @Log 注解 + AOP 切面（操作日志）
│   │   ├── config/                 # 跨域 / Knife4j / MyBatis / Redis / Spring 上下文
│   │   ├── exception/              # 统一异常处理
│   │   ├── oss/                    # 阿里云 OSS 文件操作
│   │   └── result/                 # 统一返回结果 + 分页
│   ├── security/                   # 安全认证
│   │   ├── auth/                   # 登录控制器 + UserDetails + 登录用户 Holder
│   │   ├── config/                 # Spring Security 配置
│   │   └── jwt/                    # JWT 过滤器 + 工具类
│   ├── module/
│   │   ├── alert/                  # 预警模块（规则 + 日志 + 定时检查）
│   │   ├── analysis/               # 经营分析（交易 / 客户 / 贷款 / 产品）
│   │   ├── business/               # 业务数据（客户 / 账户 / 交易 / 贷款 / 产品 / 支行 / 销售）
│   │   ├── dashboard/              # 经营看板（摘要 / 排名 / 趋势 / 风险）
│   │   ├── report/                 # 报表模块（生成 / 审核 / OSS / RabbitMQ / Quartz）
│   │   └── system/                 # 系统管理（用户 / 角色 / 菜单 / 操作日志）
│   └── resources/
│       ├── application.yml         # 主配置（个人凭证通过 ${} 占位符引用）
│       └── mapper/                 # MyBatis XML
├── frontend/src/
│   ├── api/                        # API 封装（axios）
│   ├── components/                 # 公共组件（AIChatPanel 等）
│   ├── layout/                     # 布局（Header / Sidebar）
│   ├── router/                     # 路由配置
│   ├── store/                      # Vuex 状态管理
│   ├── static/                     # 静态资源
│   └── views/                      # 页面组件
│       ├── login/                  # 登录页
│       ├── dashboard/              # 经营看板
│       ├── business/               # 业务数据（7 个页面）
│       ├── analysis/               # 经营分析
│       ├── alert/                  # 预警中心（2 个页面）
│       ├── report/                 # 报表中心（3 个页面）
│       └── system/                 # 系统管理（4 个页面）
├── ai-service/backend/app/         # Python AI 服务
│   ├── main.py                     # FastAPI 入口（端口 9096）
│   ├── api.py                      # REST API + SSE 流式对话
│   ├── agent.py                    # LangChain Agent
│   ├── tools.py                    # 数据查询工具
│   ├── database.py                 # 数据库连接
│   ├── models.py                   # SQLAlchemy 模型
│   └── export.py                   # Word 导出
├── config/
│   ├── credentials.yml             # 个人凭证（已 gitignore）
│   └── credentials.example.yml     # 凭证模板
└── sql/
    └── fin_report.sql              # 完整建库脚本 + 演示数据
```

## 数据库

完整数据库包含 18 张表，涵盖了业务、报表、预警、系统管理、AI 会话等模块。`sql/fin_report.sql` 中包含全部建表语句及演示数据（200 条客户、6000+ 条交易流水、80 条贷款记录、30 条理财产品、300 条销售记录等）。

## 快速启动

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis
- Node.js 18+
- Python 3.10+（AI 服务）
- RabbitMQ（可选，不用则自动回退同步模式）

### 1. 数据库初始化

```bash
mysql -u root -p < sql/fin_report.sql
```

### 2. 配置个人凭证

```bash
cp config/credentials.example.yml config/credentials.yml
```

编辑 `config/credentials.yml`，填入实际的数据库密码和阿里云 OSS 密钥。

### 3. 启动后端

```bash
mvn spring-boot:run
```

启动后访问：
- Web 服务：http://localhost:9094
- API 文档：http://localhost:9094/doc.html

### 4. 启动前端

```bash
cd frontend
npm install
npm run serve
```

访问 http://localhost:8083，默认账号 admin / 123456

### 5. 启动 AI 服务（可选）

```bash
cd ai-service/backend
pip install -r requirements.txt
python -m app.main
```

AI 服务运行在 http://localhost:9096

## 安全说明

- 认证方式：JWT（24 小时过期）
- 密码加密：BCrypt
- 操作审计：基于 AOP 注解 `@Log` 自动记录操作日志
- 个人凭证统一存放在 `config/credentials.yml`（已加入 `.gitignore`），`application.yml` 中通过 `${}` 占位符引用
- 生产环境建议通过环境变量注入数据库密码和 OSS 密钥
