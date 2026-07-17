package org.example.finreport.module.alert.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AlertRule {
    private Long id;
    private String ruleName;
    private String metric;
    private String operator;
    private String threshold;
    private String severity;
    private String status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
