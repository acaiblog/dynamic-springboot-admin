package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "sys_menu")
public class SysMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String permission;

    @Column(length = 100)
    private String path;

    @Column(length = 100)
    private String component;

    @Column(length = 50)
    private String icon;

    @Column(nullable = false)
    private Integer type;  // 0: 目录 1: 菜单 2: 按钮

    @Column(name = "parent_id")
    private Long parentId = 0L;

    @Column(nullable = false)
    private Integer orderNum = 0;

    @Column(nullable = false)
    private Integer status = 1;

    @Column(name = "visible", nullable = false)
    private Integer visible = 1;  // 1: 显示 0: 隐藏

    @Column(name = "is_cache", nullable = false)
    private Integer isCache = 0;  // 1: 缓存 0: 不缓存

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
