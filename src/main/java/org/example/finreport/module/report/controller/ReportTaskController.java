package org.example.finreport.module.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.aop.Log;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.report.entity.ReportTask;
import org.example.finreport.module.report.service.ReportTaskService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "自动报表任务")
@RestController
@RequestMapping("/api/report/task")
@RequiredArgsConstructor
public class ReportTaskController {

    private final ReportTaskService reportTaskService;

    @GetMapping("/page")
    @Operation(summary = "分页查询任务")
    public Result<PageResult<ReportTask>> page(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) String taskName,
                                                @RequestParam(required = false) String reportType) {
        return Result.ok(reportTaskService.page(page, size, taskName, reportType));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取任务详情")
    public Result<ReportTask> get(@PathVariable Long id) {
        return Result.ok(reportTaskService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建任务")
    @Log(title = "新建定时任务", businessType = "INSERT")
    public Result<Void> save(@RequestBody ReportTask task) {
        if (task.getEnabled() == null) task.setEnabled(1);
        reportTaskService.save(task);
        return Result.ok();
    }

    @PutMapping
    @Operation(summary = "更新任务")
    @Log(title = "编辑定时任务", businessType = "UPDATE")
    public Result<Void> update(@RequestBody ReportTask task) {
        reportTaskService.updateById(task);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务")
    @Log(title = "删除定时任务", businessType = "DELETE")
    public Result<Void> delete(@PathVariable Long id) {
        reportTaskService.removeById(id);
        return Result.ok();
    }

    @PutMapping("/enable/{id}")
    @Operation(summary = "启用任务")
    @Log(title = "启用定时任务", businessType = "UPDATE")
    public Result<Void> enable(@PathVariable Long id) {
        reportTaskService.enableTask(id);
        return Result.ok();
    }

    @PutMapping("/disable/{id}")
    @Operation(summary = "停用任务")
    @Log(title = "停用定时任务", businessType = "UPDATE")
    public Result<Void> disable(@PathVariable Long id) {
        reportTaskService.disableTask(id);
        return Result.ok();
    }

    @PostMapping("/run/{id}")
    @Operation(summary = "立即执行")
    @Log(title = "立即执行定时任务", businessType = "OTHER")
    public Result<Void> runOnce(@PathVariable Long id) {
        reportTaskService.triggerNow(id);
        return Result.ok();
    }
}
