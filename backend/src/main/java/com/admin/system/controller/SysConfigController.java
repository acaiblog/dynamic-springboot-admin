package com.admin.system.controller;

import com.admin.system.common.result.Result;
import com.admin.system.dto.ConfigDTO;
import com.admin.system.service.SysConfigService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/config")
public class SysConfigController {

    private final SysConfigService configService;

    public SysConfigController(SysConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/list")
    public Result<List<ConfigDTO>> list() {
        return Result.ok(configService.getAll());
    }

    @GetMapping("/{id}")
    public Result<ConfigDTO> getById(@PathVariable Long id) {
        return Result.ok(configService.getById(id).orElse(null));
    }

    @GetMapping("/key/{key}")
    public Result<String> getByKey(@PathVariable String key) {
        return Result.ok(configService.getValueByKey(key).orElse(""));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody ConfigDTO dto) {
        configService.save(dto);
        return Result.ok("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ConfigDTO dto) {
        configService.update(id, dto);
        return Result.ok("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        configService.delete(id);
        return Result.ok("删除成功", null);
    }

    @GetMapping("/title")
    public Result<String> getTitle() {
        String title = configService.getValueByKey("system_title").orElse("后台管理系统");
        return Result.ok(title);
    }

    @PutMapping("/title")
    public Result<Void> setTitle(@RequestBody java.util.Map<String, String> body) {
        configService.saveOrUpdate("system_title", body.get("title"));
        return Result.ok("标题更新成功", null);
    }
}
