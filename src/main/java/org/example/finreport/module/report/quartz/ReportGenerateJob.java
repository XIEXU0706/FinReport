package org.example.finreport.module.report.quartz;

import lombok.extern.slf4j.Slf4j;
import org.example.finreport.common.config.SpringContext;
import org.example.finreport.common.oss.OssService;
import org.example.finreport.module.report.entity.Report;
import org.example.finreport.module.report.entity.ReportTask;
import org.example.finreport.module.report.service.ReportAnalysisService;
import org.example.finreport.module.report.service.ReportService;
import org.example.finreport.module.report.service.ReportTaskService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
public class ReportGenerateJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        Long taskId = data.getLong("taskId");

        try {
            ReportTaskService taskService = SpringContext.getBean(ReportTaskService.class);
            ReportTask task = taskService.getById(taskId);
            if (task == null || task.getEnabled() != 1) return;

            log.info("定时任务触发: taskId={}, name={}", taskId, task.getTaskName());

            ReportAnalysisService analysisService = SpringContext.getBean(ReportAnalysisService.class);
            OssService ossService = SpringContext.getBean(OssService.class);
            ReportService reportService = SpringContext.getBean(ReportService.class);

            LocalDate today = LocalDate.now();
            String periodEnd = today.toString();
            String periodStart = calcPeriodStart(task.getCronExpression(), today);

            byte[] excelBytes = analysisService.generateExcel(task.getReportType(), periodStart, periodEnd);

            Report report = new Report();
            report.setTitle(task.getTaskName() + "_" + today);
            report.setReportType(task.getReportType());
            report.setStatus("GENERATED");
            report.setGeneratedTime(LocalDateTime.now());
            report.setPeriodStart(periodStart);
            report.setPeriodEnd(periodEnd);
            reportService.save(report);

            String objectKey = ossService.buildKey(task.getReportType(), report.getId());
            String fileUrl = ossService.upload(objectKey, excelBytes);
            report.setFileUrl(fileUrl);
            reportService.updateById(report);

            task.setLastRunTime(LocalDateTime.now());
            task.setLastStatus("SUCCESS");
            taskService.updateById(task);

            log.info("定时报表生成完成: taskId={}, reportId={}", taskId, report.getId());
        } catch (Exception e) {
            log.error("定时报表生成失败: taskId={}", taskId, e);
            try {
                ReportTaskService taskService = SpringContext.getBean(ReportTaskService.class);
                ReportTask task = taskService.getById(taskId);
                if (task != null) {
                    task.setLastRunTime(LocalDateTime.now());
                    task.setLastStatus("FAILED");
                    taskService.updateById(task);
                }
            } catch (Exception ex) {
                log.error("更新任务状态失败", ex);
            }
        }
    }

    /** 根据 cron 推断统计周期的起始时间 */
    private String calcPeriodStart(String cron, LocalDate today) {
        String[] p = cron.trim().split("\\s+");
        if (p.length >= 6) {
            String dom = p[3]; // day of month
            String dow = p[5]; // day of week
            if (dom != null && !dom.equals("*") && !dom.equals("?")) {
                return today.minusMonths(1).toString();      // 每月任务 → 近一个月
            }
            if (dow != null && !dow.equals("*") && !dow.equals("?")) {
                return today.minusDays(7).toString();         // 每周任务 → 近一周
            }
        }
        return today.toString();                               // 每天任务 → 当天
    }
}
