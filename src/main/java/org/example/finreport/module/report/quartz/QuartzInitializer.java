package org.example.finreport.module.report.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finreport.module.report.service.ReportTaskService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzInitializer {

    private final ReportTaskService reportTaskService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("初始化定时报表任务...");
        int count = reportTaskService.initJobs();
        log.info("定时报表任务初始化完成，共注册 {} 个任务", count);
    }
}
