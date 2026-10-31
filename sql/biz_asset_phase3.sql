-- ============================================================
-- 星云设备与资产管理系统 - 第三期
-- 内容：1.按人收件箱 biz_message
--       2.领用单逾期提醒标记列
--       3.耗材库存 biz_consumable + 出入库记录 biz_consumable_record
--       4.相关字典、菜单、逾期提醒定时任务
-- ============================================================
use `ry-vue`;

-- ------------------------------------------------------------
-- 1. 站内消息表（按人收件箱）
-- ------------------------------------------------------------
drop table if exists biz_message;
create table biz_message (
  message_id      bigint(20)      not null auto_increment    comment '消息ID（主键）',
  user_id         bigint(20)      not null                   comment '收件人ID（sys_user.user_id）',
  msg_type        char(1)         default '3'                comment '消息类型（1领用审批 2逾期提醒 3盘点 4系统通知）',
  title           varchar(100)    not null                   comment '消息标题',
  content         varchar(500)    default ''                 comment '消息内容',
  read_flag       char(1)         default '0'                comment '已读标志（0未读 1已读）',
  read_time       datetime                                   comment '阅读时间',
  create_time     datetime                                   comment '发送时间',
  primary key (message_id),
  key idx_message_user_id (user_id),
  key idx_message_read_flag (read_flag)
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci comment='站内消息表';

-- 领用单增加逾期提醒标记（每张单只提醒一次，防止定时任务重复轰炸）
alter table biz_asset_use add column overdue_remind char(1) default '0' comment '逾期提醒标志（0未提醒 1已提醒）';

-- ------------------------------------------------------------
-- 2. 耗材库存表 + 出入库记录表
-- ------------------------------------------------------------
drop table if exists biz_consumable;
create table biz_consumable (
  consumable_id   bigint(20)      not null auto_increment    comment '耗材ID（主键）',
  consumable_code varchar(50)     not null                   comment '耗材编码（唯一）',
  consumable_name varchar(100)    not null                   comment '耗材名称',
  spec            varchar(100)    default ''                 comment '规格型号',
  unit            varchar(20)     default ''                 comment '计量单位（个/箱/卷/盒）',
  stock_qty       int(11)         default 0                  comment '当前库存数量',
  warn_qty        int(11)         default 0                  comment '库存预警阈值（低于该值预警）',
  del_flag        char(1)         default '0'                comment '删除标志（0代表存在 1代表删除）',
  create_by       varchar(64)     default ''                 comment '创建者',
  create_time     datetime                                   comment '创建时间',
  update_by       varchar(64)     default ''                 comment '更新者',
  update_time     datetime                                   comment '更新时间',
  remark          varchar(500)    default null               comment '备注',
  primary key (consumable_id),
  unique key uk_consumable_code (consumable_code),
  key idx_consumable_name (consumable_name)
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci comment='耗材库存表';

drop table if exists biz_consumable_record;
create table biz_consumable_record (
  record_id       bigint(20)      not null auto_increment    comment '记录ID（主键）',
  consumable_id   bigint(20)      not null                   comment '耗材ID',
  record_type     char(1)         not null                   comment '记录类型（1入库 2出库）',
  quantity        int(11)         not null                   comment '变动数量',
  before_qty      int(11)         default 0                  comment '变动前库存',
  after_qty       int(11)         default 0                  comment '变动后库存',
  content         varchar(500)    default ''                 comment '说明',
  operator        varchar(64)     default ''                 comment '操作人',
  create_time     datetime                                   comment '操作时间',
  primary key (record_id),
  key idx_crecord_consumable_id (consumable_id),
  key idx_crecord_time (create_time)
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci comment='耗材出入库记录表';

-- 耗材测试数据（覆盖正常/预警/零库存场景）
insert into biz_consumable (consumable_code, consumable_name, spec, unit, stock_qty, warn_qty, create_by, create_time, remark) values
('HC-001', 'A4复印纸',   '70g/500张', '箱', 25, 10, 'admin', sysdate(), '办公用品'),
('HC-002', '硒鼓',       'HP 88A',    '个', 4,  5,  'admin', sysdate(), '库存低于预警值'),
('HC-003', '酒精喷雾',   '500ml',     '瓶', 12, 6,  'admin', sysdate(), '清洁用品'),
('HC-004', '网线',       '超六类3米', '根', 0,  10, 'admin', sysdate(), '已用完需采购');

-- ------------------------------------------------------------
-- 3. 字典：消息类型 / 耗材出入库类型
-- ------------------------------------------------------------
insert into sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) values
('消息类型',        'biz_message_type',        '0', 'admin', sysdate(), '站内消息类型'),
('耗材出入库类型', 'biz_consumable_record_type', '0', 'admin', sysdate(), '耗材出入库记录类型');

insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values
(1, '领用审批', '1', 'biz_message_type', '', 'primary', 'N', '0', 'admin', sysdate(), ''),
(2, '逾期提醒', '2', 'biz_message_type', '', 'danger',  'N', '0', 'admin', sysdate(), ''),
(3, '盘点',     '3', 'biz_message_type', '', 'warning', 'N', '0', 'admin', sysdate(), ''),
(4, '系统通知', '4', 'biz_message_type', '', 'info',    'N', '0', 'admin', sysdate(), '');

insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values
(1, '入库', '1', 'biz_consumable_record_type', '', 'success', 'N', '0', 'admin', sysdate(), ''),
(2, '出库', '2', 'biz_consumable_record_type', '', 'warning', 'N', '0', 'admin', sysdate(), '');

-- ------------------------------------------------------------
-- 4. 菜单：耗材管理目录（2060）/ 消息中心（2070 隐藏）/ 数据大屏（2071 隐藏）
-- ------------------------------------------------------------
delete from sys_role_menu where menu_id between 2060 and 2066 or menu_id in (2070, 2071);
delete from sys_menu where menu_id between 2060 and 2066 or menu_id in (2070, 2071);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2060, '耗材管理', 0, 6, 'consumablemgr', null, '', '', 1, 0, 'M', '0', '0', '', 'component', 'admin', sysdate(), '耗材库存管理目录');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2061, '耗材库存', 2060, 1, 'stock', 'biz/consumable/index', '', '', 1, 0, 'C', '0', '0', 'biz:consumable:list', 'shopping', 'admin', sysdate(), '耗材库存管理');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2062, '耗材查询', 2061, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:consumable:query',  '#', 'admin', sysdate(), ''),
       (2063, '耗材新增', 2061, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:consumable:add',    '#', 'admin', sysdate(), ''),
       (2064, '耗材修改', 2061, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:consumable:edit',   '#', 'admin', sysdate(), ''),
       (2065, '耗材删除', 2061, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:consumable:remove', '#', 'admin', sysdate(), '');

-- 消息中心/数据大屏：隐藏菜单（不在导航显示，由铃铛与首页入口进入）
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2070, '消息中心', 0, 0, 'message', 'biz/message/index', '', '', 1, 0, 'C', '1', '0', '', 'message', 'admin', sysdate(), '站内消息中心（铃铛入口）');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2071, '数据大屏', 0, 0, 'bigscreen', 'biz/bigscreen/index', '', '', 1, 0, 'C', '1', '0', '', 'chart', 'admin', sysdate(), '数据大屏（首页入口）');

-- ------------------------------------------------------------
-- 5. 定时任务：每日 9:00 扫描逾期未还的领用单并发站内提醒
-- ------------------------------------------------------------
delete from sys_job where invoke_target = 'bizMessageTask.remindOverdueAssetUse';
insert into sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
values ('资产领用逾期提醒', 'DEFAULT', 'bizMessageTask.remindOverdueAssetUse', '0 0 9 * * ?', '3', '1', '1', 'admin', sysdate(), '每日9点提醒逾期未归还的领用单（新建后暂停，确认无误再启用）');
