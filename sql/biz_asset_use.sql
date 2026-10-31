-- ============================================================
-- 星云设备与资产管理系统 - 资产领用模块
-- 表：biz_asset_use（资产领用表）
-- 依赖：RuoYi 官方系统表（sys_dict_type / sys_dict_data）已导入
-- 规范：遵循 RuoYi 惯例（逻辑删除 del_flag、标准审计字段、
--       状态 char(1) 字典码、不建物理外键，仅逻辑外键+索引）
-- 注意：asset_id 为逻辑外键，资产主表（biz_asset）建成后自然关联
-- ============================================================
use `ry-vue`;

-- ------------------------------------------------------------
-- 1. 资产领用表
-- ------------------------------------------------------------
drop table if exists biz_asset_use;
create table biz_asset_use (
  use_id              bigint(20)   not null auto_increment    comment '领用ID（主键）',
  asset_id            bigint(20)   not null                   comment '资产ID（逻辑外键，关联资产主表）',
  user_id             bigint(20)   not null                   comment '领用人ID（逻辑外键，关联 sys_user.user_id）',
  use_time            datetime                                comment '领用时间',
  expect_return_time  datetime                                comment '预计归还时间',
  actual_return_time  datetime                                comment '实际归还时间（状态为已归还时填写）',
  status              char(1)      default '0'                comment '状态（0审批中 1使用中 2已归还 3已驳回）',
  del_flag            char(1)      default '0'                comment '删除标志（0代表存在 1代表删除）',
  create_by           varchar(64)  default ''                 comment '创建者',
  create_time         datetime                                comment '创建时间（即申请时间）',
  update_by           varchar(64)  default ''                 comment '更新者',
  update_time         datetime                                comment '更新时间',
  remark              varchar(500) default null               comment '备注（可记录审批意见/驳回原因）',
  primary key (use_id),
  key idx_asset_id (asset_id),
  key idx_user_id (user_id),
  key idx_status (status),
  key idx_use_time (use_time),
  key idx_expect_return (expect_return_time)
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci comment='资产领用表';

-- ------------------------------------------------------------
-- 2. 字典类型 + 字典数据（前端标签渲染用）
-- ------------------------------------------------------------
insert into sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) values
('资产领用状态', 'biz_asset_use_status', '0', 'admin', sysdate(), '资产领用状态列表');

insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values
(1, '审批中', '0', 'biz_asset_use_status', '', 'warning', 'N', '0', 'admin', sysdate(), '领用申请待审批'),
(2, '使用中', '1', 'biz_asset_use_status', '', 'primary', 'N', '0', 'admin', sysdate(), '资产领用使用中'),
(3, '已归还', '2', 'biz_asset_use_status', '', 'success', 'N', '0', 'admin', sysdate(), '资产已归还'),
(4, '已驳回', '3', 'biz_asset_use_status', '', 'danger',  'N', '0', 'admin', sysdate(), '领用申请已驳回');

-- ------------------------------------------------------------
-- 3. 测试数据：5 条（覆盖全部 4 种状态）
--    user_id 对应 sys_user：1=admin，2=ry
--    asset_id 为待建资产主表的占位 ID
-- ------------------------------------------------------------
insert into biz_asset_use (asset_id, user_id, use_time, expect_return_time, actual_return_time, status, create_by, create_time, update_time, remark) values
(1, 1, '2026-10-10 09:00:00', '2026-10-17 18:00:00', null,                '1', 'admin', '2026-10-10 09:00:00', '2026-10-10 10:30:00', '审批通过，设备已交付'),
(2, 2, '2026-10-12 14:30:00', '2026-10-19 18:00:00', '2026-10-18 16:20:00', '2', 'admin', '2026-10-12 14:30:00', '2026-10-18 16:20:00', '按时归还，外观完好'),
(3, 1, '2026-10-26 10:15:00', '2026-11-02 18:00:00', null,                '0', 'admin', '2026-10-26 10:15:00', null,                  null),
(4, 2, '2026-10-15 09:40:00', '2026-10-22 18:00:00', null,                '3', 'admin', '2026-10-15 09:40:00', '2026-10-16 11:00:00', '审批驳回：该资产已预留给项目组'),
(5, 1, '2026-10-27 15:00:00', '2026-11-03 18:00:00', null,                '1', 'admin', '2026-10-27 15:00:00', '2026-10-27 15:30:00', null);
