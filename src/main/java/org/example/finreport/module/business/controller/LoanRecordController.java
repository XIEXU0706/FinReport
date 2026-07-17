package org.example.finreport.module.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.business.entity.LoanRecord;
import org.example.finreport.module.business.service.LoanRecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "业务数据 - 贷款业务")
@RestController
@RequestMapping("/api/business/loan")
@RequiredArgsConstructor
public class LoanRecordController {

    private final LoanRecordService loanRecordService;

    @GetMapping("/page")
    @Operation(summary = "分页查询贷款记录")
    public Result<PageResult<LoanRecord>> page(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(required = false) String status,
                                               @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate dateFrom,
                                               @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate dateTo,
                                               @RequestParam(required = false) String loanNo,
                                               @RequestParam(required = false) String customerNo,
                                               @RequestParam(required = false) String repayMethod) {
        return Result.ok(loanRecordService.page(page, size, status, dateFrom, dateTo, loanNo, customerNo, repayMethod));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取贷款详情")
    public Result<LoanRecord> get(@PathVariable Long id) {
        return Result.ok(loanRecordService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增贷款")
    public Result<Void> add(@RequestBody LoanRecord loan) {
        loanRecordService.save(loan);
        return Result.ok();
    }

    @PutMapping
    @Operation(summary = "修改贷款")
    public Result<Void> update(@RequestBody LoanRecord loan) {
        loanRecordService.updateById(loan);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除贷款")
    public Result<Void> delete(@PathVariable Long id) {
        loanRecordService.removeById(id);
        return Result.ok();
    }
}
