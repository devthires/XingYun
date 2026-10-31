-- ============================================================
-- 星云设备与资产管理系统 - 资产主表（最小可用版）
-- 说明：biz_asset_info 为资产领用(biz_asset_use)的前置依赖表，
--       当前为最小字段集，后续资产模块可直接在此基础上扩展
-- ============================================================
use `ry-vue`;

drop table if exists biz_asset_info;
create table biz_asset_info (
  asset_id        bigint(20)      not null auto_increment    comment '资产ID（主键）',
  asset_code      varchar(50)     not null                   comment '资产编码（唯一，如 XY-ZC-001）',
  asset_name      varchar(100)    not null                   comment '资产名称',
  asset_model     varchar(100)    default ''                 comment '资产型号/规格',
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
  key idx_asset_status (status)
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci comment='资产信息表';

-- 资产状态字典
insert into sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) values
('资产状态', 'biz_asset_status', '0', 'admin', sysdate(), '资产状态列表');

insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values
(1, '闲置',   '0', 'biz_asset_status', '', 'info',    'N', '0', 'admin', sysdate(), '资产闲置可领用'),
(2, '在用',   '1', 'biz_asset_status', '', 'primary', 'N', '0', 'admin', sysdate(), '资产领用使用中'),
(3, '维修中', '2', 'biz_asset_status', '', 'warning', 'N', '0', 'admin', sysdate(), '资产维修中不可领用'),
(4, '报废',   '3', 'biz_asset_status', '', 'danger',  'N', '0', 'admin', sysdate(), '资产已报废');

-- 测试资产：覆盖全部状态（1、3 为闲置可领用；2 在用/4 维修中/5 报废 用于验证领用拦截）
insert into biz_asset_info (asset_code, asset_name, asset_model, status, create_by, create_time, remark) values
('XY-ZC-001', '联想笔记本电脑',   'ThinkPad T14p',    '0', 'admin', sysdate(), '闲置可领用'),
('XY-ZC-002', '戴尔显示器',       'U2723QE',          '1', 'admin', sysdate(), '已被张三领用'),
('XY-ZC-003', '罗技无线鼠标',     'MX Master 3S',     '0', 'admin', sysdate(), '闲置可领用'),
('XY-ZC-004', '爱普生投影仪',     'CB-FH52',          '2', 'admin', sysdate(), '灯泡故障维修中'),
('XY-ZC-005', '苹果测试手机',     'iPhone 15',        '3', 'admin', sysdate(), '屏幕碎裂已报废');
