package com.admin.system.controller;

import com.admin.system.common.result.Result;
import com.admin.system.dto.LoginDTO;
import com.admin.system.service.AuthService;
import com.admin.system.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok("登录成功", authService.login(dto));
    }

    @GetMapping("/userInfo")
    public Result<LoginVO> getUserInfo() {
        return Result.ok(authService.getUserInfo());
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok("退出成功", null);
    }
}
