package org.example.finreport.module.system.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysMenu {
    private Long id;
    private String menuName;
    private Long parentId;
    private Integer sortOrder;
    private String path;
    private String component;
    private String menuType;
    private String visible;
    private String icon;
    private String perms;
    private LocalDateTime createdAt;
}
