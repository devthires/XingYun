-- ============================================================
-- 设备报修管理模块 - 字典数据（可选）
-- 前置条件：已导入 RuoYi-Vue 官方 sql/ry_xxx.sql（存在 sys_dict_type / sys_dict_data）
-- 用途：前端下拉框与状态标签渲染（dict type: biz_device_status / biz_repair_status）
-- 执行方式：在 Navicat 中选中 ry-vue 库后整体运行本脚本
-- ============================================================
use `ry-vue`;

-- 1. 字典类型
insert into sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) values
('设备状态', 'biz_device_status',  '0', 'admin', sysdate(), '设备状态列表'),
('报修状态', 'biz_repair_status',  '0', 'admin', sysdate(), '设备报修单状态列表');

-- 2. 字典数据
-- 设备状态：0正常 1停用 2维修中
insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values
(1, '正常',   '0', 'biz_device_status', '', 'success', 'N', '0', 'admin', sysdate(), '设备状态正常'),
(2, '停用',   '1', 'biz_device_status', '', 'danger',  'N', '0', 'admin', sysdate(), '设备已停用'),
(3, '维修中', '2', 'biz_device_status', '', 'warning', 'N', '0', 'admin', sysdate(), '设备维修中');

-- 报修状态：0待处理 1处理中 2已完成
insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) values
(1, '待处理', '0', 'biz_repair_status', '', 'warning', 'N', '0', 'admin', sysdate(), '报修单待处理'),
(2, '处理中', '1', 'biz_repair_status', '', 'primary', 'N', '0', 'admin', sysdate(), '报修单处理中'),
(3, '已完成', '2', 'biz_repair_status', '', 'success', 'N', '0', 'admin', sysdate(), '报修单已完成');
