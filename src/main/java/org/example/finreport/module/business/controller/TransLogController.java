package org.example.finreport.module.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.business.entity.TransLog;
import org.example.finreport.module.business.service.TransLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "业务数据 - 交易流水")
@RestController
@RequestMapping("/api/business/trans-log")
@RequiredArgsConstructor
public class TransLogController {

    private final TransLogService transLogService;

    @GetMapping("/page")
    @Operation(summary = "分页查询交易流水")
    public Result<PageResult<TransLog>> page(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String transType,
                                             @RequestParam(required = false) String channel,
                                             @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate dateFrom,
                                             @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate dateTo,
                                             @RequestParam(required = false) String customerNo,
                                             @RequestParam(required = false) String accountNo) {
        var from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        var to = dateTo != null ? dateTo.plusDays(1).atStartOfDay() : null;
        return Result.ok(transLogService.page(page, size, transType, channel, from, to, customerNo, accountNo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取交易详情")
    public Result<TransLog> get(@PathVariable Long id) {
        return Result.ok(transLogService.getById(id));
    }
}
