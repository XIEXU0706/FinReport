package org.example.finreport.module.system.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysRole {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer sortOrder;
    private String status;
    private LocalDateTime createdAt;
}
