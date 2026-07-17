package org.example.finreport.module.alert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.alert.entity.AlertRule;
import org.example.finreport.module.alert.service.AlertRuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "预警中心 - 规则配置")
@RestController
@RequestMapping("/api/alert/rule")
@RequiredArgsConstructor
public class AlertRuleController {

    private final AlertRuleService alertRuleService;

    @GetMapping("/page")
    @Operation(summary = "分页查询规则")
    public Result<PageResult<AlertRule>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return Result.ok(alertRuleService.page(page, size));
    }

    @GetMapping("/list")
    @Operation(summary = "全部规则")
    public Result<List<AlertRule>> list() {
        return Result.ok(alertRuleService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "规则详情")
    public Result<AlertRule> get(@PathVariable Long id) {
        return Result.ok(alertRuleService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增规则")
    public Result<Void> add(@RequestBody AlertRule rule) {
        rule.setStatus("ENABLED");
        alertRuleService.save(rule);
        return Result.ok();
    }

    @PutMapping
    @Operation(summary = "修改规则")
    public Result<Void> update(@RequestBody AlertRule rule) {
        alertRuleService.updateById(rule);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除规则")
    public Result<Void> delete(@PathVariable Long id) {
        alertRuleService.removeById(id);
        return Result.ok();
    }
}
