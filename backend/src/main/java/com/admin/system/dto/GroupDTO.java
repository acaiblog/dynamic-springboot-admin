package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class GroupDTO {
    private Long id;

    // 编码由后端自动生成，前端不需要传
    private String code;

    @NotBlank(message = "用户组名称不能为空")
    private String name;

    private String remark;

    private Integer status;

    private Set<Long> userIds;

    private Set<Long> roleIds;
}
