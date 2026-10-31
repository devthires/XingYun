-- ============================================================
-- 星云设备与资产管理系统 - 第一期：资产台账完整版
-- 内容：biz_asset_info 全量字段重建（测试数据按原ID重建）
--       资产分类字典、资产管理/资产领用菜单权限
-- ============================================================
use `ry-vue`;

-- ------------------------------------------------------------
-- 1. 资产信息表（全量字段）
-- ------------------------------------------------------------
drop table if exists biz_asset_info;
create table biz_asset_info (
  asset_id        bigint(20)      not null auto_increment    comment '资产ID（主键）',
  asset_code      varchar(50)     not null                   comment '资产编码（唯一，留空时后端自动生成）',
  asset_name      varchar(100)    not null                   comment '资产名称',
  asset_model     varchar(100)    default ''                 comment '资产型号/规格',
  category        varchar(10)     default ''                 comment '资产分类（字典 biz_asset_category）',
  brand           varchar(100)    default ''                 comment '品牌',
  price           decimal(12,2)   default null               comment '采购价格（元）',
  purchase_date   date                                       comment '采购日期',
  location        varchar(200)    default ''                 comment '存放位置',
  owner_id        bigint(20)      default null               comment '保管责任人ID（关联 sys_user.user_id）',
  pic             varchar(500)    default ''                 comment '资产图片（相对路径）',
  status          char(1)         default '0'                comment '资产状态（0闲置 1在用 2维修中 3报废）',
  del_flag        char(1)         default '0'                comment '删除标志（0代表存在 1代表删除）',
  create_by       varchar(64)     default ''                 comment '创建者',
  create_time     datetime                                   comment '创建时间',
  update_by       varchar(64)     default ''                 comment '更新者',
  update_time     datetime                                   comment '更新时间',
  remark          varchar(500)    default null               comment '备注',
  primary key (asset_id),
  unique key uk_asset_code (asset_code),
  key idx_asset_name (asset_name),
  key idx_asset_status (status),
  key idx_category (category),
  key idx_owner_id (owner_id)
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci comment='资产信息表';

-- 测试资产重建（ID 保持 1~5，覆盖全部状态；1、3 闲置可领用）
insert into biz_asset_info (asset_code, asset_name, asset_model, category, brand, price, purchase_date, location, owner_id, status, create_by, create_time, remark) values
('XY-ZC-001', '联想笔记本电脑', 'ThinkPad T14p',  '1', '联想',   8999.00, '2025-03-15', '研发部2楼A区', 1, '0', 'admin', sysdate(), '闲置可领用'),
('XY-ZC-002', '戴尔显示器',     'U2723QE',        '1', '戴尔',   3299.00, '2025-03-15', '研发部2楼B区', 2, '1', 'admin', sysdate(), '已被领用'),
('XY-ZC-003', '罗技无线鼠标',   'MX Master 3S',   '1', '罗技',    649.00, '2025-06-01', '行政楼3楼库房', 1, '0', 'admin', sysdate(), '闲置可领用'),
('XY-ZC-004', '爱普生投影仪',   'CB-FH52',        '1', '爱普生', 4599.00, '2024-11-20', '综合楼1楼会议室', 2, '2', 'admin', sysdate(), '灯泡故障维修中'),
('XY-ZC-005', '苹果测试手机',   'iPhone 15',      '1', '苹果',   5999.00, '2023-12-01', '信息中心B1机房', 1, '3', 'admin', sysdate(), '屏幕碎裂已报废');

-- ------------------------------------------------------------
-- 2. 资产分类字典
-- ------------------------------------------------------------
insert into sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) values
('资产分类', 'biz_asset_category', '0', 'admin', sysdate(), '资产分类列表');

insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values
(1, '电子设备', '1', 'biz_asset_category', '', 'primary', 'N', '0', 'admin', sysdate(), '电脑外设等电子设备'),
(2, '办公家具', '2', 'biz_asset_category', '', 'info',    'N', '0', 'admin', sysdate(), '桌椅柜等家具'),
(3, '网络设备', '3', 'biz_asset_category', '', 'success', 'N', '0', 'admin', sysdate(), '交换机路由器等'),
(4, '工具仪器', '4', 'biz_asset_category', '', 'warning', 'N', '0', 'admin', sysdate(), '维修检测仪器'),
(5, '其他',     '5', 'biz_asset_category', '', 'danger',  'N', '0', 'admin', sysdate(), '其他类资产');

-- ------------------------------------------------------------
-- 3. 菜单权限：资产管理目录（报修单管理并入）
-- ------------------------------------------------------------
delete from sys_role_menu where menu_id between 2010 and 2026;
delete from sys_menu where menu_id between 2010 and 2026;

-- 3.1 一级目录：资产管理
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2010, '资产管理', 0, 4, 'asset', null, '', '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', sysdate(), '资产管理目录');

-- 3.2 资产台账菜单 + 按钮
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2011, '资产台账', 2010, 1, 'ledger', 'biz/asset/index', '', '', 1, 0, 'C', '0', '0', 'biz:asset:list', 'list', 'admin', sysdate(), '资产台账菜单');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2012, '资产查询', 2011, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:asset:query',  '#', 'admin', sysdate(), ''),
       (2013, '资产新增', 2011, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:asset:add',    '#', 'admin', sysdate(), ''),
       (2014, '资产修改', 2011, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:asset:edit',   '#', 'admin', sysdate(), ''),
       (2015, '资产删除', 2011, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:asset:remove', '#', 'admin', sysdate(), ''),
       (2016, '资产导出', 2011, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:asset:export', '#', 'admin', sysdate(), '');

-- 3.3 资产领用菜单 + 按钮
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2021, '资产领用', 2010, 2, 'use', 'biz/assetuse/index', '', '', 1, 0, 'C', '0', '0', 'biz:use:list', 'form', 'admin', sysdate(), '资产领用菜单');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2022, '领用查询', 2021, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:use:query',  '#', 'admin', sysdate(), ''),
       (2023, '领用新增', 2021, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:use:add',    '#', 'admin', sysdate(), ''),
       (2024, '领用修改', 2021, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:use:edit',   '#', 'admin', sysdate(), ''),
       (2025, '领用删除', 2021, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:use:remove', '#', 'admin', sysdate(), ''),
       (2026, '领用导出', 2021, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:use:export', '#', 'admin', sysdate(), '');

-- 3.4 报修单管理并入资产管理目录，删除原空目录
update sys_menu set parent_id = 2010, order_num = 3 where menu_id = 2001;
delete from sys_role_menu where menu_id = 2000;
delete from sys_menu where menu_id = 2000;
