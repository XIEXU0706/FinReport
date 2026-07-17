package org.example.finreport.module.alert.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AlertLog {
    private Long id;
    private Long ruleId;
    private String ruleName;
    private String metric;
    private Double actualValue;
    private String threshold;
    private String severity;
    private String content;
    private String status;
    private LocalDateTime createdAt;
}
