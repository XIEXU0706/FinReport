package org.example.finreport.module.alert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.alert.entity.AlertLog;
import org.example.finreport.module.alert.service.AlertLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "预警中心 - 预警列表")
@RestController
@RequestMapping("/api/alert/log")
@RequiredArgsConstructor
public class AlertLogController {

    private final AlertLogService alertLogService;

    @GetMapping("/page")
    @Operation(summary = "分页查询预警")
    public Result<PageResult<AlertLog>> page(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String severity,
                                             @RequestParam(required = false) String status) {
        return Result.ok(alertLogService.page(page, size, severity, status));
    }

    @GetMapping("/latest")
    @Operation(summary = "最近N条预警")
    public Result<List<AlertLog>> latest(@RequestParam(defaultValue = "5") int limit) {
        return Result.ok(alertLogService.latest(limit));
    }

    @GetMapping("/count")
    @Operation(summary = "未处理预警数")
    public Result<Long> count() {
        return Result.ok(alertLogService.countByStatus("NEW"));
    }

    @PutMapping("/ack/{id}")
    @Operation(summary = "确认预警")
    public Result<Void> acknowledge(@PathVariable Long id) {
        AlertLog log = alertLogService.getById(id);
        if (log != null) { log.setStatus("ACKNOWLEDGED"); alertLogService.updateById(log); }
        return Result.ok();
    }

    @PutMapping("/resolve/{id}")
    @Operation(summary = "处理预警")
    public Result<Void> resolve(@PathVariable Long id) {
        AlertLog log = alertLogService.getById(id);
        if (log != null) { log.setStatus("RESOLVED"); alertLogService.updateById(log); }
        return Result.ok();
    }
}
