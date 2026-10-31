-- 第二期迁移补跑脚本（前半部分：设备并入资产、报修单映射已执行成功）
use `ry-vue`;

alter table biz_repair_order add index idx_asset_id (asset_id);
drop table if exists biz_device;

-- 资产变更流水表（只增不改的审计表）
drop table if exists biz_asset_record;
create table biz_asset_record (
  record_id       bigint(20)      not null auto_increment    comment '流水ID（主键）',
  asset_id        bigint(20)      not null                   comment '资产ID',
  record_type     char(1)         not null                   comment '变更类型（1入库 2领用出库 3归还入库 4送修 5维修完成 6变更 7注销）',
  content         varchar(500)    default ''                 comment '变更说明',
  before_status   char(1)         default null               comment '变更前资产状态',
  after_status    char(1)         default null               comment '变更后资产状态',
  operator        varchar(64)     default ''                 comment '操作人',
  create_time     datetime                                   comment '操作时间',
  primary key (record_id),
  key idx_record_asset_id (asset_id),
  key idx_record_type (record_type),
  key idx_record_time (create_time)
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci comment='资产变更流水表';

-- 资产盘点单表
drop table if exists biz_asset_inventory;
create table biz_asset_inventory (
  inventory_id    bigint(20)      not null auto_increment    comment '盘点单ID（主键）',
  inventory_no    varchar(50)     not null                   comment '盘点单号（PD+yyyyMMddHHmmss）',
  inventory_name  varchar(100)    not null                   comment '盘点任务名称',
  status          char(1)         default '0'                comment '盘点状态（0盘点中 1已完成）',
  total_count     int(11)         default 0                  comment '应盘总数',
  check_count     int(11)         default 0                  comment '已盘数量',
  normal_count    int(11)         default 0                  comment '正常数量',
  loss_count      int(11)         default 0                  comment '盘亏数量',
  finish_time     datetime                                   comment '完成时间',
  del_flag        char(1)         default '0'                comment '删除标志（0代表存在 1代表删除）',
  create_by       varchar(64)     default ''                 comment '创建者',
  create_time     datetime                                   comment '创建时间',
  update_by       varchar(64)     default ''                 comment '更新者',
  update_time     datetime                                   comment '更新时间',
  remark          varchar(500)    default null               comment '备注',
  primary key (inventory_id),
  unique key uk_inventory_no (inventory_no),
  key idx_inventory_status (status)
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci comment='资产盘点单表';

-- 资产盘点明细表
drop table if exists biz_asset_inventory_item;
create table biz_asset_inventory_item (
  item_id         bigint(20)      not null auto_increment    comment '明细ID（主键）',
  inventory_id    bigint(20)      not null                   comment '盘点单ID',
  asset_id        bigint(20)      not null                   comment '资产ID',
  snapshot_code   varchar(50)     default ''                 comment '资产编码快照',
  snapshot_name   varchar(100)    default ''                 comment '资产名称快照',
  snapshot_location varchar(200)  default ''                 comment '存放位置快照',
  book_status     char(1)         default '0'                comment '账面状态快照（0闲置 1在用 2维修中 3报废）',
  result          char(1)         default '0'                comment '盘点结果（0待盘点 1正常 2盘亏）',
  checker         varchar(64)     default ''                 comment '盘点人',
  check_time      datetime                                   comment '盘点时间',
  remark          varchar(500)    default null               comment '差异说明',
  primary key (item_id),
  key idx_item_inventory_id (inventory_id),
  key idx_item_asset_id (asset_id),
  key idx_item_result (result)
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci comment='资产盘点明细表';

-- 字典
insert into sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) values
('资产变更类型', 'biz_asset_record_type', '0', 'admin', sysdate(), '资产变更流水类型'),
('盘点状态',     'biz_inventory_status',  '0', 'admin', sysdate(), '资产盘点任务状态'),
('盘点结果',     'biz_inventory_result',  '0', 'admin', sysdate(), '盘点明细结果');

insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values
(1, '入库',     '1', 'biz_asset_record_type', '', 'success', 'N', '0', 'admin', sysdate(), ''),
(2, '领用出库', '2', 'biz_asset_record_type', '', 'primary', 'N', '0', 'admin', sysdate(), ''),
(3, '归还入库', '3', 'biz_asset_record_type', '', 'info',    'N', '0', 'admin', sysdate(), ''),
(4, '送修',     '4', 'biz_asset_record_type', '', 'warning', 'N', '0', 'admin', sysdate(), ''),
(5, '维修完成', '5', 'biz_asset_record_type', '', 'success', 'N', '0', 'admin', sysdate(), ''),
(6, '变更',     '6', 'biz_asset_record_type', '', 'primary', 'N', '0', 'admin', sysdate(), ''),
(7, '注销',     '7', 'biz_asset_record_type', '', 'danger',  'N', '0', 'admin', sysdate(), '');

insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values
(1, '盘点中', '0', 'biz_inventory_status', '', 'warning', 'N', '0', 'admin', sysdate(), ''),
(2, '已完成', '1', 'biz_inventory_status', '', 'success', 'N', '0', 'admin', sysdate(), '');

insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values
(1, '待盘点', '0', 'biz_inventory_result', '', 'info',    'N', '0', 'admin', sysdate(), ''),
(2, '正常',   '1', 'biz_inventory_result', '', 'success', 'N', '0', 'admin', sysdate(), ''),
(3, '盘亏',   '2', 'biz_inventory_result', '', 'danger',  'N', '0', 'admin', sysdate(), '');

-- 菜单
delete from sys_role_menu where menu_id between 2031 and 2032 or menu_id between 2041 and 2045;
delete from sys_menu where menu_id between 2031 and 2032 or menu_id between 2041 and 2045;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2031, '变更流水', 2010, 4, 'record', 'biz/assetrecord/index', '', '', 1, 0, 'C', '0', '0', 'biz:record:list', 'log', 'admin', sysdate(), '资产变更流水查询');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2032, '流水查询', 2031, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:record:query', '#', 'admin', sysdate(), '');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2041, '资产盘点', 2010, 5, 'inventory', 'biz/inventory/index', '', '', 1, 0, 'C', '0', '0', 'biz:inventory:list', 'clipboard', 'admin', sysdate(), '资产盘点管理');
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2042, '盘点查询', 2041, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:inventory:query',  '#', 'admin', sysdate(), ''),
       (2043, '盘点新增', 2041, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:inventory:add',    '#', 'admin', sysdate(), ''),
       (2044, '盘点确认', 2041, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:inventory:edit',   '#', 'admin', sysdate(), ''),
       (2045, '盘点删除', 2041, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:inventory:remove', '#', 'admin', sysdate(), '');
