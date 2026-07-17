-- ============================================================
-- FinReport 完整数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS fin_report CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fin_report;

-- ============================================================
-- 业务数据表
-- ============================================================

CREATE TABLE IF NOT EXISTS branch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_code VARCHAR(32) UNIQUE,
    branch_name VARCHAR(100),
    region VARCHAR(50),
    status VARCHAR(10) DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '支行信息';

CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_no VARCHAR(32) UNIQUE,
    name VARCHAR(100),
    id_type VARCHAR(10),
    id_number VARCHAR(32),
    gender VARCHAR(4),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    customer_level VARCHAR(20),
    birth_date DATE,
    occupation VARCHAR(50),
    branch_id BIGINT,
    status VARCHAR(10) DEFAULT 'ACTIVE',
    open_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_branch_id (branch_id),
    KEY idx_customer_level (customer_level),
    KEY idx_open_date (open_date)
) COMMENT '客户信息';

CREATE TABLE IF NOT EXISTS account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_no VARCHAR(32) UNIQUE,
    customer_id BIGINT,
    account_type VARCHAR(20),
    currency VARCHAR(10) DEFAULT 'CNY',
    balance DECIMAL(18,2) DEFAULT 0,
    status VARCHAR(10) DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_customer_id (customer_id)
) COMMENT '账户信息';

CREATE TABLE IF NOT EXISTS trans_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trans_no VARCHAR(64) UNIQUE,
    account_id BIGINT,
    customer_id BIGINT,
    trans_type VARCHAR(30),
    amount DECIMAL(18,2),
    fee DECIMAL(18,2) DEFAULT 0,
    channel VARCHAR(20),
    status VARCHAR(10),
    trans_time DATETIME,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_trans_time (trans_time),
    KEY idx_customer_id (customer_id),
    KEY idx_trans_type (trans_type),
    KEY idx_channel (channel)
) COMMENT '交易流水';

CREATE TABLE IF NOT EXISTS loan_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_no VARCHAR(64) UNIQUE,
    customer_id BIGINT,
    product_id BIGINT,
    amount DECIMAL(18,2),
    term_months INT,
    interest_rate DECIMAL(5,2),
    loan_date DATE,
    due_date DATE,
    balance DECIMAL(18,2),
    repay_method VARCHAR(20),
    status VARCHAR(20),
    branch_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_loan_date (loan_date),
    KEY idx_status (status),
    KEY idx_customer_id (customer_id)
) COMMENT '贷款记录';

CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(32) UNIQUE,
    product_name VARCHAR(100),
    product_type VARCHAR(30),
    risk_level VARCHAR(10),
    expected_return DECIMAL(5,2),
    min_amount DECIMAL(18,2),
    term_days INT,
    status VARCHAR(10),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '理财产品';

CREATE TABLE IF NOT EXISTS prod_sale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_no VARCHAR(64) UNIQUE,
    product_no VARCHAR(32),
    customer_no VARCHAR(32),
    amount DECIMAL(18,2),
    sale_date DATE,
    status VARCHAR(10),
    branch_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prod_sale_customer FOREIGN KEY (customer_no) REFERENCES customer(customer_no),
    CONSTRAINT fk_prod_sale_product FOREIGN KEY (product_no) REFERENCES product(product_code),
    CONSTRAINT fk_prod_sale_branch FOREIGN KEY (branch_id) REFERENCES branch(id),
    KEY idx_sale_date (sale_date)
) COMMENT '产品销售记录';

-- ============================================================
-- 预警模块表
-- ============================================================

CREATE TABLE IF NOT EXISTS alert_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(200) NOT NULL COMMENT '规则名称',
    metric VARCHAR(100) NOT NULL COMMENT '指标名称',
    operator VARCHAR(10) NOT NULL COMMENT '比较运算符',
    threshold VARCHAR(50) NOT NULL COMMENT '阈值',
    severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '严重程度',
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
    description VARCHAR(500) COMMENT '规则描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '预警规则';

CREATE TABLE IF NOT EXISTS alert_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_id BIGINT NOT NULL COMMENT '规则ID',
    rule_name VARCHAR(200) NOT NULL COMMENT '规则名称',
    metric VARCHAR(100) NOT NULL COMMENT '指标名称',
    actual_value DOUBLE COMMENT '实际值',
    threshold VARCHAR(50) COMMENT '阈值',
    severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '严重程度',
    content VARCHAR(500) COMMENT '预警内容',
    status VARCHAR(20) NOT NULL DEFAULT 'NEW' COMMENT '状态(NEW/ACKNOWLEDGED/RESOLVED)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_status (status),
    KEY idx_severity (severity),
    KEY idx_created_at (created_at)
) COMMENT '预警日志';

-- ============================================================
-- 报表表
-- ============================================================

CREATE TABLE IF NOT EXISTS report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200),
    report_type VARCHAR(50),
    status VARCHAR(20),
    template_id VARCHAR(50),
    period_start VARCHAR(20),
    period_end VARCHAR(20),
    generated_by VARCHAR(50),
    generated_time DATETIME,
    approved_by VARCHAR(50),
    approved_time DATETIME,
    file_url VARCHAR(500),
    description VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_status (status),
    KEY idx_report_type (report_type)
) COMMENT '报表';

CREATE TABLE IF NOT EXISTS report_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    report_type VARCHAR(50) NOT NULL COMMENT '报表类型(DAILY/MONTHLY)',
    cron_expression VARCHAR(100) NOT NULL COMMENT 'cron 表达式',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用 1-启用 0-禁用',
    last_run_time DATETIME COMMENT '上次执行时间',
    last_status VARCHAR(20) COMMENT '上次执行状态(SUCCESS/FAILED)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_enabled (enabled),
    KEY idx_report_type (report_type)
) COMMENT '自动报表任务';

-- ============================================================
-- 系统管理表
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(200) NOT NULL,
    real_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    avatar VARCHAR(500),
    dept_id BIGINT,
    status VARCHAR(10) DEFAULT 'NORMAL',
    last_login_ip VARCHAR(50),
    last_login_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '系统用户';

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50),
    role_code VARCHAR(50) UNIQUE,
    description VARCHAR(200),
    sort_order INT DEFAULT 0,
    status VARCHAR(10) DEFAULT 'NORMAL',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '系统角色';

CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_name VARCHAR(50),
    parent_id BIGINT DEFAULT 0,
    sort_order INT DEFAULT 0,
    path VARCHAR(200),
    component VARCHAR(200),
    menu_type VARCHAR(10),
    visible VARCHAR(10) DEFAULT '1',
    icon VARCHAR(100),
    perms VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '系统菜单';

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    role_id BIGINT,
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT '用户角色关联';

CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT,
    menu_id BIGINT,
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) COMMENT '角色菜单关联';

-- ============================================================
-- 操作日志表
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_oper_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    business_type VARCHAR(20),
    method VARCHAR(200),
    request_method VARCHAR(10),
    oper_name VARCHAR(50),
    oper_url VARCHAR(200),
    oper_ip VARCHAR(50),
    oper_params TEXT,
    oper_result TEXT,
    status VARCHAR(10),
    error_msg TEXT,
    oper_time DATETIME,
    cost_time BIGINT,
    KEY idx_oper_time (oper_time),
    KEY idx_oper_name (oper_name)
) COMMENT '操作日志';

-- ============================================================
-- AI 会话表
-- ============================================================

CREATE TABLE IF NOT EXISTS sessions (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    title VARCHAR(255),
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    KEY ix_sessions_id (id)
) COMMENT 'AI 会话';

CREATE TABLE IF NOT EXISTS messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(36),
    role VARCHAR(50),
    content TEXT,
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    KEY session_id (session_id),
    KEY ix_messages_id (id),
    CONSTRAINT fk_messages_session FOREIGN KEY (session_id) REFERENCES sessions(id)
) COMMENT 'AI 对话消息';

-- ============================================================
-- 初始数据
-- ============================================================

INSERT IGNORE INTO sys_user (username, password, real_name, status) VALUES
('admin', '$2a$10$VWi3qLM/ga1vPmbGVQ72gepWkP3zGyIY2OxnP1FLbEBmJhkKRclUm', '系统管理员', 'NORMAL');

INSERT IGNORE INTO sys_role (role_name, role_code, description, sort_order) VALUES
('管理员', 'ADMIN', '系统管理员', 1),
('普通用户', 'USER', '普通用户', 2);

INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES (1, 1);

INSERT IGNORE INTO sys_menu (id, menu_name, parent_id, sort_order, path, component, menu_type, icon) VALUES
(1, '首页', 0, 1, '/dashboard', 'dashboard/Index', 'M', 'HomeFilled'),
(2, '业务数据', 0, 2, '/business', NULL, 'M', 'DataBoard'),
(3, '经营分析', 0, 3, '/analysis', NULL, 'M', 'DataAnalysis'),
(4, '报表中心', 0, 4, '/report', NULL, 'M', 'Document'),
(5, '系统管理', 0, 5, '/system', NULL, 'M', 'Setting'),

(6, '客户管理', 2, 1, '/business/customer', 'business/Customer', 'C', NULL),
(7, '账户管理', 2, 2, '/business/account', 'business/Account', 'C', NULL),
(8, '交易流水', 2, 3, '/business/trans-log', 'business/TransLog', 'C', NULL),
(9, '贷款业务', 2, 4, '/business/loan', 'business/Loan', 'C', NULL),
(10, '产品管理', 2, 5, '/business/product', 'business/Product', 'C', NULL),
(11, '支行信息', 2, 6, '/business/branch', 'business/Branch', 'C', NULL),

(12, '分析中心', 3, 1, '/analysis/center', 'analysis/Center', 'C', NULL),

(13, '报表生成', 4, 1, '/report/generate', 'report/Generate', 'C', NULL),
(14, '报表历史', 4, 2, '/report/history', 'report/History', 'C', NULL),

(15, '用户管理', 5, 1, '/system/user', 'system/User', 'C', NULL),
(16, '角色管理', 5, 2, '/system/role', 'system/Role', 'C', NULL),
(17, '操作日志', 5, 3, '/system/oper-log', 'system/OperLog', 'C', NULL),
(18, '个人中心', 5, 4, '/system/profile', 'system/Profile', 'C', NULL);

-- 产品销售演示数据
INSERT IGNORE INTO prod_sale (sale_no, product_no, customer_no, amount, sale_date, status, branch_id) VALUES
('S00000001','P000001','C000001',50000.00,'2026-01-15','NORMAL',1),
('S00000002','P000002','C000005',200000.00,'2026-01-18','NORMAL',3),
('S00000003','P000009','C000010',10000.00,'2026-01-20','NORMAL',2),
('S00000004','P000024','C000015',5000.00,'2026-01-22','NORMAL',5),
('S00000005','P000017','C000020',50000.00,'2026-01-25','NORMAL',4),
('S00000006','P000005','C000025',150000.00,'2026-02-10','NORMAL',6),
('S00000007','P000012','C000030',50000.00,'2026-02-14','NORMAL',7),
('S00000008','P000025','C000035',10000.00,'2026-02-18','NORMAL',8),
('S00000009','P000003','C000040',30000.00,'2026-02-22','NORMAL',1),
('S00000010','P000018','C000045',20000.00,'2026-03-05','NORMAL',2),
('S00000011','P000006','C000050',300000.00,'2026-03-10','NORMAL',3),
('S00000012','P000010','C000055',5000.00,'2026-03-15','NORMAL',4),
('S00000013','P000026','C000060',30000.00,'2026-03-20','NORMAL',5),
('S00000014','P000020','C000065',100000.00,'2026-03-25','NORMAL',6),
('S00000015','P000007','C000070',500000.00,'2026-04-08','NORMAL',7),
('S00000016','P000013','C000075',200000.00,'2026-04-12','NORMAL',8),
('S00000017','P000004','C000001',50000.00,'2026-04-18','NORMAL',1),
('S00000018','P000027','C000005',50000.00,'2026-04-22','NORMAL',2),
('S00000019','P000021','C000010',1000.00,'2026-04-28','NORMAL',3),
('S00000020','P000008','C000015',100000.00,'2026-05-06','NORMAL',4),
('S00000021','P000014','C000020',100000.00,'2026-05-12','NORMAL',5),
('S00000022','P000029','C000025',500000.00,'2026-05-18','NORMAL',6),
('S00000023','P000001','C000030',80000.00,'2026-05-22','NORMAL',7),
('S00000024','P000011','C000035',20000.00,'2026-06-05','NORMAL',8),
('S00000025','P000019','C000040',50000.00,'2026-06-10','NORMAL',1),
('S00000026','P000028','C000045',100000.00,'2026-06-15','NORMAL',2),
('S00000027','P000015','C000050',300000.00,'2026-06-20','NORMAL',3),
('S00000028','P000002','C000055',100000.00,'2026-06-25','NORMAL',4),
('S00000029','P000022','C000060',500.00,'2026-07-02','NORMAL',5),
('S00000030','P000030','C000065',5000.00,'2026-07-08','NORMAL',6);
