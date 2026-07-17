package org.example.finreport.module.report.mq;

import lombok.extern.slf4j.Slf4j;
import org.example.finreport.common.oss.OssService;
import org.example.finreport.module.report.entity.Report;
import org.example.finreport.module.report.service.ReportAnalysisService;
import org.example.finreport.module.report.service.ReportService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ReportGenerateConsumer {

    private final ReportAnalysisService analysisService;
    private final ReportService reportService;
    private final OssService ossService;

    public ReportGenerateConsumer(ReportAnalysisService analysisService,
                                   ReportService reportService,
                                   OssService ossService) {
        this.analysisService = analysisService;
        this.reportService = reportService;
        this.ossService = ossService;
    }

    @RabbitListener(queues = "report.generate.queue")
    public void handleGenerate(Map<String, Object> message) {
        Long reportId = Long.valueOf(message.get("reportId").toString());
        String reportType = (String) message.get("reportType");
        String periodStart = (String) message.get("periodStart");
        String periodEnd = (String) message.get("periodEnd");

        log.info("开始异步生成报表: id={}, type={}, period={}~{}", reportId, reportType, periodStart, periodEnd);

        try {
            byte[] excelBytes = analysisService.generateExcel(reportType, periodStart, periodEnd);
            String objectKey = ossService.buildKey(reportType, reportId);
            String fileUrl = ossService.upload(objectKey, excelBytes);

            Report report = reportService.getById(reportId);
            if (report != null) {
                report.setFileUrl(fileUrl);
                report.setStatus("GENERATED");
                reportService.updateById(report);
            }
            log.info("报表生成完成: id={}, url={}", reportId, fileUrl);
        } catch (Exception e) {
            log.error("报表生成失败: id={}", reportId, e);
            Report report = reportService.getById(reportId);
            if (report != null) {
                report.setStatus("FAILED");
                reportService.updateById(report);
            }
        }
    }
}
