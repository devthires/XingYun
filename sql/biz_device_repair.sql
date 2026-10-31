-- ============================================================
-- 设备报修管理模块 - 数据库初始化脚本
-- 适用框架：RuoYi-Vue (前后端分离版)
-- 适用数据库：MySQL 5.7 / 8.0，字符集 utf8mb4
-- 说明：遵循 RuoYi 规范（逻辑删除 del_flag、标准审计字段、
--       状态用 char(1) 字典码，不建物理外键，仅建逻辑外键+索引）
-- ============================================================

-- ------------------------------------------------------------
-- 0. 创建数据库（RuoYi-Vue 默认数据源库名为 ry-vue）
-- ------------------------------------------------------------
create database if not exists `ry-vue` default character set utf8mb4 collate utf8mb4_general_ci;
use `ry-vue`;

-- ------------------------------------------------------------
-- 1. 设备表 biz_device
-- ------------------------------------------------------------
drop table if exists biz_device;
create table biz_device (
  device_id        bigint(20)      not null auto_increment    comment '设备ID（主键）',
  device_code      varchar(50)     not null                   comment '设备编号（唯一，如 DEV-001）',
  device_name      varchar(100)    not null                   comment '设备名称',
  device_model     varchar(100)    default ''                 comment '设备型号',
  device_location  varchar(200)    default ''                 comment '设备位置（安装/存放地点）',
  status           char(1)         default '0'                comment '设备状态（0正常 1停用 2维修中）',
  del_flag         char(1)         default '0'                comment '删除标志（0代表存在 1代表删除）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (device_id),
  unique key uk_device_code (device_code),
  key idx_device_name (device_name),
  key idx_device_status (status)
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci comment='设备信息表';

-- ------------------------------------------------------------
-- 2. 报修单表 biz_repair_order
--    与 biz_device 通过 device_id 逻辑外键关联（不建物理外键，
--    便于后期分库分表及使用 RuoYi 代码生成器）
-- ------------------------------------------------------------
drop table if exists biz_repair_order;
create table biz_repair_order (
  repair_id        bigint(20)      not null auto_increment    comment '报修单ID（主键）',
  repair_no        varchar(50)     not null                   comment '报修单号（唯一，规则：BX+yyyyMMdd+3位流水，如 BX20260810001）',
  device_id        bigint(20)      not null                   comment '设备ID（关联 biz_device.device_id）',
  repair_user      varchar(64)     not null                   comment '报修人',
  repair_phone     varchar(20)     default ''                 comment '报修人联系电话',
  problem_desc     varchar(500)    not null                   comment '问题描述',
  status           char(1)         default '0'                comment '报修状态（0待处理 1处理中 2已完成）',
  handler_name     varchar(64)     default ''                 comment '处理人（维修人员）',
  finish_time      datetime                                   comment '完成时间（状态为已完成时填写）',
  del_flag         char(1)         default '0'                comment '删除标志（0代表存在 1代表删除）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间（即报修时间）',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注（可记录处理结果）',
  primary key (repair_id),
  unique key uk_repair_no (repair_no),
  key idx_device_id (device_id),
  key idx_status (status),
  key idx_create_time (create_time)
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci comment='设备报修单表';

-- ------------------------------------------------------------
-- 3. 模拟数据：5 条设备
-- ------------------------------------------------------------
insert into biz_device (device_code, device_name, device_model, device_location, status, create_by, create_time, remark) values
('DEV-001', '惠普激光打印机', 'HP LaserJet M405dn', '行政楼3楼办公区',  '0', 'admin', '2026-08-01 09:00:00', '行政部公共打印设备'),
('DEV-002', '格力柜式空调',   'KFR-72LW/(72591)',   '综合楼1楼大厅',    '0', 'admin', '2026-08-01 09:05:00', '夏季高频使用设备'),
('DEV-003', '联想台式电脑',   'ThinkCentre M760t',  '研发部2楼工位区',  '2', 'admin', '2026-08-01 09:10:00', '研发部开发用机，维修中'),
('DEV-004', '海康威视摄像头', 'DS-2CD3T46WDV3',     '厂区南门岗亭',     '0', 'admin', '2026-08-01 09:15:00', '安防监控设备'),
('DEV-005', '戴尔机架服务器', 'PowerEdge R740',     '信息中心B1机房',   '0', 'admin', '2026-08-01 09:20:00', '核心业务服务器，严禁随意断电');

-- ------------------------------------------------------------
-- 4. 模拟数据：10 条报修单（device_id 关联上面 5 台设备）
--    状态分布：4 条待处理(0)、3 条处理中(1)、3 条已完成(2)
-- ------------------------------------------------------------
insert into biz_repair_order (repair_no, device_id, repair_user, repair_phone, problem_desc, status, handler_name, finish_time, create_by, create_time, update_time, remark) values
('BX20260810001', 3, '张伟', '13800000001', '开机蓝屏反复重启，无法进入系统',           '1', '李海军', null,                'admin', '2026-08-10 09:15:00', '2026-08-11 10:00:00', '初步判断内存条故障，已更换测试中'),
('BX20260815001', 2, '李娜', '13800000002', '空调制冷效果差，出风不凉',                 '0', '',       null,                'admin', '2026-08-15 10:30:00', null,                  null),
('BX20260820001', 1, '王强', '13800000003', '打印时频繁卡纸，出纸歪斜',                 '2', '王技术', '2026-08-22 14:00:00', 'admin', '2026-08-20 14:20:00', '2026-08-22 14:00:00', '更换搓纸轮后恢复正常'),
('BX20260901001', 5, '赵敏', '13800000004', '服务器第3块硬盘告警灯亮黄色',              '1', '李海军', null,                'admin', '2026-09-01 08:50:00', '2026-09-01 13:30:00', '已备份数据，等待更换硬盘'),
('BX20260905001', 4, '刘洋', '13800000005', '南门摄像头画面模糊，疑似镜头起雾',         '2', '王技术', '2026-09-08 16:30:00', 'admin', '2026-09-05 11:05:00', '2026-09-08 16:30:00', '清洁镜头并重新对焦，画面恢复清晰'),
('BX20260912001', 1, '陈晨', '13800000006', '电脑无法搜索到打印机，网络打印不可用',     '0', '',       null,                'admin', '2026-09-12 15:40:00', null,                  null),
('BX20260918001', 5, '杨帆', '13800000007', '机房服务器风扇噪音异常增大',               '2', '李海军', '2026-09-20 17:10:00', 'admin', '2026-09-18 09:25:00', '2026-09-20 17:10:00', '清理灰尘并更换风扇，恢复正常'),
('BX20260925001', 2, '周婷', '13800000008', '空调内机漏水，地面有积水',                 '2', '王技术', '2026-09-27 10:45:00', 'admin', '2026-09-25 13:50:00', '2026-09-27 10:45:00', '疏通排水管并补充制冷剂'),
('BX20261008001', 4, '吴斌', '13800000009', '夜间红外模式不启动，夜视画面全黑',         '0', '',       null,                'admin', '2026-10-08 20:15:00', null,                  null),
('BX20261020001', 3, '郑雪', '13800000010', '键盘部分按键失灵，输入偶尔中断',           '0', '',       null,                'admin', '2026-10-20 16:00:00', null,                  null);

-- ============================================================
-- 字典数据见同目录 biz_dict_data.sql
-- （需先导入 RuoYi 官方 ry_xxx.sql 建 sys_dict_type/sys_dict_data
--   表后再执行；前端状态标签渲染依赖字典）
-- ============================================================
