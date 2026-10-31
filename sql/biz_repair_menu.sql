-- ============================================================
-- 设备报修管理模块 - 菜单权限脚本
-- 说明：menu_id 固定使用 2000~2006（RuoYi 默认菜单未占用该区段），
--       脚本可重复执行（先删后插）。admin 为超管自动拥有全部权限，
--       其他角色请在【系统管理-角色管理】中勾选本菜单后授权。
-- ============================================================
use `ry-vue`;

delete from sys_role_menu where menu_id between 2000 and 2006;
delete from sys_menu where menu_id between 2000 and 2006;

-- 1. 一级目录：设备报修
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2000, '设备报修', 0, 5, 'device', null, '', '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', sysdate(), '设备报修管理目录');

-- 2. 菜单：报修单管理（前端组件路径 biz/repair/index，前端页面待开发）
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2001, '报修单管理', 2000, 1, 'repair', 'biz/repair/index', '', '', 1, 0, 'C', '0', '0', 'biz:repair:list', 'form', 'admin', sysdate(), '设备报修单菜单');

-- 3. 按钮权限
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
values (2002, '报修单查询', 2001, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:repair:query',  '#', 'admin', sysdate(), ''),
       (2003, '报修单新增', 2001, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:repair:add',    '#', 'admin', sysdate(), ''),
       (2004, '报修单修改', 2001, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:repair:edit',   '#', 'admin', sysdate(), ''),
       (2005, '报修单删除', 2001, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:repair:remove', '#', 'admin', sysdate(), ''),
       (2006, '报修单导出', 2001, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'biz:repair:export', '#', 'admin', sysdate(), '');
