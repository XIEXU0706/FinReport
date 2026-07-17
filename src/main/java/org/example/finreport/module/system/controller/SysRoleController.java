package org.example.finreport.module.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.system.entity.SysRole;
import org.example.finreport.module.system.service.SysRoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "系统管理 - 角色管理")
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<PageResult<SysRole>> page(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(sysRoleService.page(page, pageSize));
    }

    @Operation(summary = "获取所有角色")
    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.ok(sysRoleService.list());
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        sysRoleService.save(role);
        return Result.ok();
    }

    @Operation(summary = "修改角色")
    @PutMapping
    public Result<Void> update(@RequestBody SysRole role) {
        sysRoleService.updateById(role);
        return Result.ok();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return Result.ok();
    }
}
