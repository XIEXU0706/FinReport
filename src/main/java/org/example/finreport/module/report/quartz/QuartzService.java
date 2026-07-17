package org.example.finreport.module.report.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzService {

    private final Scheduler scheduler;

    public void addJob(Long taskId, String cron) {
        try {
            JobDetail job = JobBuilder.newJob(ReportGenerateJob.class)
                    .withIdentity("reportTask_" + taskId, "reportTasks")
                    .usingJobData("taskId", taskId)
                    .storeDurably()
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("reportTrigger_" + taskId, "reportTasks")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();

            scheduler.scheduleJob(job, trigger);   //  把任务注册到调度器
            log.info("Quartz 任务注册成功: taskId={}, cron={}", taskId, cron);
        } catch (SchedulerException e) {
            log.error("Quartz 任务注册失败: taskId={}, cron={}", taskId, cron, e);
            throw new RuntimeException("Quartz 任务注册失败: taskId=" + taskId, e);
        }
    }

    public void pauseJob(Long taskId) { // 暂停任务
        try {
            scheduler.pauseJob(JobKey.jobKey("reportTask_" + taskId, "reportTasks"));
        } catch (SchedulerException e) {
            throw new RuntimeException("Quartz 任务暂停失败: taskId=" + taskId, e);
        }
    }

    public void resumeJob(Long taskId) { // 回复任务
        try {
            scheduler.resumeJob(JobKey.jobKey("reportTask_" + taskId, "reportTasks"));
        } catch (SchedulerException e) {
            throw new RuntimeException("Quartz 任务恢复失败: taskId=" + taskId, e);
        }
    }

    public void deleteJob(Long taskId) {
        try {
            scheduler.deleteJob(JobKey.jobKey("reportTask_" + taskId, "reportTasks"));
        } catch (SchedulerException e) {
            throw new RuntimeException("Quartz 任务删除失败: taskId=" + taskId, e);
        }
    }

    public boolean existsJob(Long taskId) {
        try {
            return scheduler.checkExists(JobKey.jobKey("reportTask_" + taskId, "reportTasks"));
        } catch (SchedulerException e) {
            return false;
        }
    }

    public void triggerJob(Long taskId) {
        try {
            JobKey key = JobKey.jobKey("reportTask_" + taskId, "reportTasks");
            scheduler.triggerJob(key);
        } catch (SchedulerException e) {
            throw new RuntimeException("Quartz 任务立即执行失败: taskId=" + taskId, e);
        }
    }
}
