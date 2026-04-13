package com.admin.system.repository;

import com.admin.system.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SysMenuRepository extends JpaRepository<SysMenu, Long> {

    List<SysMenu> findByParentId(Long parentId);

    List<SysMenu> findByStatus(Integer status);

    List<SysMenu> findByType(Integer type);

    @Query("SELECT m FROM SysMenu m WHERE m.type != 2 AND m.status = 1 ORDER BY m.orderNum ASC")
    List<SysMenu> findMenusAndDirectories();

    // 查找用户拥有的所有菜单（通过角色关联）
    @Query(value = "SELECT DISTINCT sm.* FROM sys_menu sm " +
           "INNER JOIN sys_role_menu srm ON sm.id = srm.menu_id " +
           "INNER JOIN sys_user_role sur ON srm.role_id = sur.role_id " +
           "INNER JOIN sys_user su ON sur.user_id = su.id " +
           "WHERE su.username = :username AND sm.status = 1 " +
           "ORDER BY sm.order_num ASC", nativeQuery = true)
    List<SysMenu> findMenusByUsername(@Param("username") String username);

    // 查找用户拥有的菜单类路由（type=1）
    @Query(value = "SELECT DISTINCT sm.* FROM sys_menu sm " +
           "INNER JOIN sys_role_menu srm ON sm.id = srm.menu_id " +
           "INNER JOIN sys_user_role sur ON srm.role_id = sur.role_id " +
           "INNER JOIN sys_user su ON sur.user_id = su.id " +
           "WHERE su.username = :username AND sm.type = 1 AND sm.status = 1 " +
           "ORDER BY sm.order_num ASC", nativeQuery = true)
    List<SysMenu> findRouterMenusByUsername(@Param("username") String username);
}
