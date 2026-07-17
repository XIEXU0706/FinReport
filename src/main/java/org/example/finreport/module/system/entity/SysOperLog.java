package org.example.finreport.module.system.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysOperLog {
    private Long id;
    private String title;
    private String businessType;
    private String method;
    private String requestMethod;
    private String operName;
    private String operUrl;
    private String operIp;
    private String operParams;
    private String operResult;
    private String status;
    private String errorMsg;
    private LocalDateTime operTime;
    private Long costTime;
}
