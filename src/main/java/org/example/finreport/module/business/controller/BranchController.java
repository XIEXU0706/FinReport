package org.example.finreport.module.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.business.entity.Branch;
import org.example.finreport.module.business.service.BranchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "业务数据 - 支行信息")
@RestController
@RequestMapping("/api/business/branch")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping("/page")
    @Operation(summary = "分页查询支行")
    public Result<PageResult<Branch>> page(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String branchName,
                                           @RequestParam(required = false) String region,
                                           @RequestParam(required = false) String status) {
        return Result.ok(branchService.page(page, size, branchName, region, status));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有支行")
    public Result<List<Branch>> list() {
        return Result.ok(branchService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取支行详情")
    public Result<Branch> get(@PathVariable Long id) {
        return Result.ok(branchService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增支行")
    public Result<Void> add(@RequestBody Branch branch) {
        branchService.save(branch);
        return Result.ok();
    }

    @PutMapping
    @Operation(summary = "修改支行")
    public Result<Void> update(@RequestBody Branch branch) {
        branchService.updateById(branch);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除支行")
    public Result<Void> delete(@PathVariable Long id) {
        branchService.removeById(id);
        return Result.ok();
    }
}
