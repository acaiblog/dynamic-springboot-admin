package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String nickname;

    private String email;

    private String phone;

    private Integer status;

    private String remark;

    private Set<Long> roleIds;

    private Set<Long> groupIds;
}
