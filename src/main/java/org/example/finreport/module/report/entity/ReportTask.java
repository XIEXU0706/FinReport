package org.example.finreport.module.report.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportTask {
    private Long id;
    private String taskName;
    private String reportType;
    private String cronExpression;
    private Integer enabled;
    private LocalDateTime lastRunTime;
    private String lastStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
