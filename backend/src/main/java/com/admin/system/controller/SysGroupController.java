package com.admin.system.controller;

import com.admin.system.common.result.Result;
import com.admin.system.dto.GroupDTO;
import com.admin.system.service.SysGroupService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/system/group")
public class SysGroupController {

    private final SysGroupService groupService;

    public SysGroupController(SysGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/list")
    public Result<List<?>> list() {
        return Result.ok(groupService.getAll());
    }

    @GetMapping("/page")
    public Result<?> page(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(groupService.getPage(name, status, page, size));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.ok(groupService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody GroupDTO dto) {
        groupService.save(dto);
        return Result.ok("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody GroupDTO dto) {
        groupService.update(id, dto);
        return Result.ok("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        groupService.delete(id);
        return Result.ok("删除成功", null);
    }

    @GetMapping("/{id}/roleIds")
    public Result<Set<Long>> getGroupRoleIds(@PathVariable Long id) {
        return Result.ok(groupService.getRoleIdsByGroupId(id));
    }

    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody Set<Long> roleIds) {
        groupService.assignRoles(id, roleIds);
        return Result.ok("角色分配成功", null);
    }

    @GetMapping("/{id}/userIds")
    public Result<Set<Long>> getGroupUserIds(@PathVariable Long id) {
        return Result.ok(groupService.getUserIdsByGroupId(id));
    }

    @PutMapping("/{id}/users")
    public Result<Void> assignUsers(@PathVariable Long id, @RequestBody Set<Long> userIds) {
        groupService.assignUsers(id, userIds);
        return Result.ok("成员分配成功", null);
    }
}
