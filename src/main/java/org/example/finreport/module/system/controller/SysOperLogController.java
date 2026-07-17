package org.example.finreport.module.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.system.entity.SysOperLog;
import org.example.finreport.module.system.service.SysOperLogService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "系统管理 - 操作日志")
@RestController
@RequestMapping("/api/system/oper-log")
@RequiredArgsConstructor
public class SysOperLogController {

    private final SysOperLogService sysOperLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    public Result<PageResult<SysOperLog>> page(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int pageSize,
                                               @RequestParam(required = false) String operName,
                                               @RequestParam(required = false) String businessType) {
        return Result.ok(sysOperLogService.page(page, pageSize, operName, businessType));
    }

    @Operation(summary = "获取日志详情")
    @GetMapping("/{id}")
    public Result<SysOperLog> get(@PathVariable Long id) {
        return Result.ok(sysOperLogService.getById(id));
    }
}
