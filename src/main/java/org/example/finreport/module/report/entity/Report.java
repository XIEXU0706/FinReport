package org.example.finreport.module.report.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Report {
    private Long id;
    private String title;
    private String reportType;
    private String status;
    private String templateId;
    private String periodStart;
    private String periodEnd;
    private String generatedBy;
    private LocalDateTime generatedTime;
    private String approvedBy;
    private LocalDateTime approvedTime;
    private String fileUrl;
    private String description;
    private LocalDateTime createdAt;
}
