package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfigDTO {
    private Long id;

    @NotBlank(message = "配置键不能为空")
    private String configKey;

    private String configValue;

    private String remark;

    private Integer status = 1;
}
