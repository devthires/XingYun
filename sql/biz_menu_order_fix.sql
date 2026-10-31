-- ============================================================
-- 菜单排序修正：业务模块前置，平台菜单靠后
-- 结果：首页 → 资产管理 → 耗材管理 → 系统管理 → 系统监控 → 系统工具
-- ============================================================
use `ry-vue`;

update sys_menu set order_num = 1 where menu_id = 2010;  -- 资产管理
update sys_menu set order_num = 2 where menu_id = 2060;  -- 耗材管理
update sys_menu set order_num = 3 where menu_id = 1;     -- 系统管理
update sys_menu set order_num = 4 where menu_id = 2;     -- 系统监控
update sys_menu set order_num = 5 where menu_id = 3;     -- 系统工具
