package org.example.finreport.module.report.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageParam;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.module.report.entity.ReportTask;
import org.example.finreport.module.report.mapper.ReportTaskMapper;
import org.example.finreport.module.report.quartz.QuartzService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportTaskService {

    private final ReportTaskMapper mapper;
    private final QuartzService quartzService;

    public ReportTask getById(Long id) {
        return mapper.selectById(id);
    }

    public List<ReportTask> list() {
        return mapper.selectList();
    }

    public PageResult<ReportTask> page(int page, int size, String taskName, String reportType) {
        PageParam pp = new PageParam(page, size);
        List<ReportTask> list = mapper.selectPage(pp.getOffset(), pp.getSize(), taskName, reportType);
        long total = mapper.selectCount(taskName, reportType);
        return PageResult.of(list, total, page, size);
    }

    @Transactional
    public void save(ReportTask task) {
        mapper.insert(task);
        if (task.getEnabled() == 1) {
            quartzService.addJob(task.getId(), task.getCronExpression());
        }
    }

    @Transactional
    public void updateById(ReportTask task) {
        ReportTask old = mapper.selectById(task.getId());
        mapper.updateById(task);

        boolean wasEnabled = old != null && old.getEnabled() == 1;
        boolean nowEnabled = task.getEnabled() == 1;
        String cron = task.getCronExpression() != null ? task.getCronExpression() : old.getCronExpression();

        if (wasEnabled && !nowEnabled) {
            quartzService.pauseJob(task.getId());
        } else if (!wasEnabled && nowEnabled) {
            quartzService.addJob(task.getId(), cron);
        } else if (wasEnabled && nowEnabled && task.getCronExpression() != null
                && !task.getCronExpression().equals(old.getCronExpression())) {
            quartzService.deleteJob(task.getId());
            quartzService.addJob(task.getId(), cron);
        }
    }

    @Transactional
    public void removeById(Long id) {
        quartzService.deleteJob(id);
        mapper.deleteById(id);
    }

    public void enableTask(Long id) {
        ReportTask task = mapper.selectById(id);
        if (task != null && task.getEnabled() != 1) {
            task.setEnabled(1);
            mapper.updateById(task);
            quartzService.addJob(id, task.getCronExpression());
        }
    }

    public void disableTask(Long id) {
        ReportTask task = mapper.selectById(id);
        if (task != null && task.getEnabled() == 1) {
            task.setEnabled(0);
            mapper.updateById(task);
            quartzService.pauseJob(id);
        }
    }

    public void triggerNow(Long id) {
        quartzService.triggerJob(id);
    }

    public int initJobs() {
        List<ReportTask> tasks = mapper.selectEnabledList();
        int count = 0;
        for (ReportTask task : tasks) {
            if (!quartzService.existsJob(task.getId())) {
                quartzService.addJob(task.getId(), task.getCronExpression());
                count++;
            }
        }
        return count;
    }
}
