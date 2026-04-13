package com.admin.system.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MenuVO {
    private Long id;
    private String name;
    private String menuName;
    private String permission;
    private String path;
    private String component;
    private String icon;
    private Integer type;
    private Long parentId;
    private Integer orderNum;
    private Integer status;
    private Integer visible;
    private Integer isCache;
    private LocalDateTime createTime;
    private List<MenuVO> children;
}
