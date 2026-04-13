package com.admin.system.controller;

import com.admin.system.common.result.Result;
import com.admin.system.dto.RoleDTO;
import com.admin.system.service.SysRoleService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleService roleService;

    public SysRoleController(SysRoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public Result<List<?>> list() {
        return Result.ok(roleService.getAll());
    }

    @GetMapping("/page")
    public Result<?> page(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(roleService.getPage(name, status, page, size));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody RoleDTO dto) {
        roleService.save(dto);
        return Result.ok("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody RoleDTO dto) {
        roleService.update(id, dto);
        return Result.ok("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.ok("删除成功", null);
    }

    @GetMapping("/{id}/menuIds")
    public Result<Set<Long>> getRoleMenuIds(@PathVariable Long id) {
        return Result.ok(roleService.getMenuIdsByRoleId(id));
    }

    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.ok("菜单分配成功", null);
    }
}
