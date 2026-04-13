package com.admin.system;

import com.admin.system.entity.*;
import com.admin.system.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;

@SpringBootApplication
public class AdminSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(SysUserRepository userRepo,
                                      SysRoleRepository roleRepo,
                                      SysMenuRepository menuRepo,
                                      SysGroupRepository groupRepo,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepo.findByUsername("admin").isEmpty()) {
                // 创建角色
                SysRole superAdmin = new SysRole();
                superAdmin.setCode("super_admin");
                superAdmin.setName("超级管理员");
                superAdmin.setRemark("拥有所有权限");
                superAdmin.setStatus(1);
                superAdmin = roleRepo.save(superAdmin);

                SysRole admin = new SysRole();
                admin.setCode("admin");
                admin.setName("系统管理员");
                admin.setRemark("系统管理权限");
                admin.setStatus(1);
                admin = roleRepo.save(admin);

                // 创建菜单
                List<SysMenu> allMenus = new ArrayList<>();

                SysMenu dir1 = new SysMenu();
                dir1.setName("系统管理");
                dir1.setPath("/system");
                dir1.setIcon("Setting");
                dir1.setType(0);
                dir1.setParentId(0L);
                dir1.setOrderNum(0);
                dir1.setStatus(1);
                dir1.setVisible(1);
                dir1.setIsCache(0);
                dir1 = menuRepo.save(dir1);

                SysMenu mUser = new SysMenu();
                mUser.setName("用户管理");
                mUser.setPath("/system/user");
                mUser.setComponent("system/user/index");
                mUser.setIcon("User");
                mUser.setType(1);
                mUser.setParentId(dir1.getId());
                mUser.setOrderNum(1);
                mUser.setStatus(1);
                mUser.setVisible(1);
                mUser.setIsCache(0);
                mUser = menuRepo.save(mUser);

                SysMenu mRole = new SysMenu();
                mRole.setName("角色管理");
                mRole.setPath("/system/role");
                mRole.setComponent("system/role/index");
                mRole.setIcon("Role");
                mRole.setType(1);
                mRole.setParentId(dir1.getId());
                mRole.setOrderNum(2);
                mRole.setStatus(1);
                mRole.setVisible(1);
                mRole.setIsCache(0);
                mRole = menuRepo.save(mRole);

                SysMenu mMenu = new SysMenu();
                mMenu.setName("菜单管理");
                mMenu.setPath("/system/menu");
                mMenu.setComponent("system/menu/index");
                mMenu.setIcon("Menu");
                mMenu.setType(1);
                mMenu.setParentId(dir1.getId());
                mMenu.setOrderNum(3);
                mMenu.setStatus(1);
                mMenu.setVisible(1);
                mMenu.setIsCache(0);
                mMenu = menuRepo.save(mMenu);

                SysMenu mGroup = new SysMenu();
                mGroup.setName("用户组管理");
                mGroup.setPath("/system/group");
                mGroup.setComponent("system/group/index");
                mGroup.setIcon("Group");
                mGroup.setType(1);
                mGroup.setParentId(dir1.getId());
                mGroup.setOrderNum(4);
                mGroup.setStatus(1);
                mGroup.setVisible(1);
                mGroup.setIsCache(0);
                mGroup = menuRepo.save(mGroup);

                // 按钮
                allMenus.addAll(List.of(mUser, mRole, mMenu, mGroup));
                addButton(menuRepo, "用户", "user", mUser.getId(), List.of("sys:user:list","sys:user:add","sys:user:edit","sys:user:delete","sys:user:reset"));
                addButton(menuRepo, "角色", "role", mRole.getId(), List.of("sys:role:list","sys:role:add","sys:role:edit","sys:role:delete"));
                addButton(menuRepo, "菜单", "menu", mMenu.getId(), List.of("sys:menu:list","sys:menu:add","sys:menu:edit","sys:menu:delete"));
                addButton(menuRepo, "用户组", "group", mGroup.getId(), List.of("sys:group:list","sys:group:add","sys:group:edit","sys:group:delete"));

                // 给超级管理员赋所有菜单
                superAdmin.setMenus(new HashSet<>(menuRepo.findAll()));
                roleRepo.save(superAdmin);

                // 创建管理员用户
                SysUser adminUser = new SysUser();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setNickname("超级管理员");
                adminUser.setEmail("admin@example.com");
                adminUser.setStatus(1);
                adminUser.setRoles(new HashSet<>(List.of(superAdmin)));
                userRepo.save(adminUser);

                System.out.println("========================================");
                System.out.println("  默认管理员账号: admin / admin123");
                System.out.println("========================================");
            }
        };
    }

    private void addButton(SysMenuRepository menuRepo, String label, String prefix, Long parentId, List<String> perms) {
        int i = 0;
        for (String perm : perms) {
            SysMenu btn = new SysMenu();
            btn.setName(label + (perm.contains("list") ? "列表" : perm.contains("add") ? "新增" : perm.contains("edit") ? "编辑" : perm.contains("delete") ? "删除" : "重置"));
            btn.setPermission(perm);
            btn.setType(2);
            btn.setParentId(parentId);
            btn.setOrderNum(i++);
            btn.setStatus(1);
            btn.setVisible(1);
            btn.setIsCache(0);
            menuRepo.save(btn);
        }
    }
}
