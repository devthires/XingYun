# XingYun 星云资产管理系统

基于 RuoYi-Vue 开发的资产全生命周期管理系统，包含资产台账、领用、报修、盘点、耗材库存、数据看板等模块。

## 技术栈

| 端 | 技术 |
| --- | --- |
| 后端 | Spring Boot 4、MyBatis、Spring Security、JWT、MySQL、Redis |
| 前端 | Vue 3、Vite、Element Plus、ECharts |
| 基础框架 | RuoYi-Vue |

## 功能模块

- 资产台账：增删改查、分类/状态筛选、二维码标签、Excel 导出
- 资产领用：申请、审批通过、驳回、归还
- 报修管理：新增、开始处理、完成
- 资产盘点：创建任务、账面快照、逐条确认、盘亏说明、完成
- 变更流水：入库、领用出库、归还入库、送修、维修完成、变更、注销
- 耗材库存：入库、出库、库存预警、出入库记录
- 数据看板：资产状态分布、报修趋势、最近报修
- 系统管理：用户、角色、菜单、部门、字典、参数、公告
- 系统监控：在线用户、定时任务、缓存、登录/操作日志

## 目录结构

```
XingYun-Server/      后端（Maven 多模块）
XingYun-Web/         前端（Vue 3 + Vite）
sql/                 业务初始化 SQL
```

## 快速开始

### 1. 准备环境

- JDK 17+
- MySQL 8
- Redis
- Node.js 18+

### 2. 初始化数据库

按顺序执行：

```text
sql/ry_20260417.sql
sql/quartz.sql
sql/biz_*.sql
```

### 3. 配置

修改 `XingYun-Server/xingyun-admin/src/main/resources/application-druid.yml`：

```yaml
username: root
password: change_me
```

修改 `application.yml` 中的 JWT 密钥：

```yaml
token:
  secret: change_me_to_a_long_random_secret
```

> 仓库中的密码和密钥均为占位符，启动前必须替换。默认账号 `admin / admin123` 仅用于本地演示，部署后请立即修改。

### 4. 启动后端

```bash
cd XingYun-Server
mvn clean package -DskipTests
java -jar xingyun-admin/target/xingyun-admin.jar
```

### 5. 启动前端

```bash
cd XingYun-Web
npm install
npm run dev
```

默认后端地址 `http://localhost:8080`，前端通过 `/dev-api` 代理。

## 测试

项目的测试过程、用例、缺陷报告与自动化资产单独维护在 QA 作品集仓库中，不在本仓库内。

## 说明

- 本项目基于 RuoYi-Vue（MIT License）二次开发，保留原项目版权声明；
- 仓库已移除构建产物、依赖目录、日志和上传文件；
- 数据库密码、JWT 密钥等敏感配置已替换为占位符。
