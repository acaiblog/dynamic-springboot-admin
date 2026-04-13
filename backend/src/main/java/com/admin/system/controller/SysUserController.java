package com.admin.system.controller;

import com.admin.system.common.result.PageResult;
import com.admin.system.common.result.Result;
import com.admin.system.dto.UserDTO;
import com.admin.system.entity.SysUser;
import com.admin.system.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @GetMapping("/page")
    // @PreAuthorize("hasAuthority('sys:user:list')")
    public Result<PageResult<?>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        return Result.ok(userService.getPage(username, status, page, size));
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('sys:user:query')")
    public Result<Object> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    // @PreAuthorize("hasAuthority('sys:user:add')")
    public Result<Void> create(@Valid @RequestBody UserDTO dto) {
        userService.save(dto);
        return Result.ok();
    }

    @PutMapping
    // @PreAuthorize("hasAuthority('sys:user:edit')")
    public Result<Void> update(@Valid @RequestBody UserDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        userService.update(dto.getId(), dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }

    @PostMapping("/{id}/resetPassword")
    // @PreAuthorize("hasAuthority('sys:user:edit')")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        userService.updateStatus(id, status);
        return Result.ok();
    }
}
