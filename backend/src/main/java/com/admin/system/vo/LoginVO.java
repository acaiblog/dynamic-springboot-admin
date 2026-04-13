package com.admin.system.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private String email;
    private String avatar;
    private List<String> permissions;
    private List<Map<String, Object>> menus;
}
