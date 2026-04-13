package com.admin.system.controller;

import com.admin.system.common.result.Result;
import com.admin.system.dto.MenuDTO;
import com.admin.system.service.SysMenuService;
import com.admin.system.vo.MenuVO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    private final SysMenuService menuService;

    public SysMenuController(SysMenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/tree")
    public Result<List<MenuVO>> getTree() {
        return Result.ok(menuService.getTree());
    }

    @GetMapping("/routers")
    public Result<List<?>> getRouters() {
        return Result.ok(menuService.getRouters());
    }

    @GetMapping("/list")
    public Result<List<?>> list() {
        return Result.ok(menuService.getAll());
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.ok(menuService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody MenuDTO dto) {
        menuService.save(dto);
        return Result.ok("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MenuDTO dto) {
        menuService.update(id, dto);
        return Result.ok("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.ok("删除成功", null);
    }
}
