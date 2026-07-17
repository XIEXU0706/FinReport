package org.example.finreport.module.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.system.entity.SysMenu;
import org.example.finreport.module.system.service.SysMenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "系统管理 - 菜单管理")
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        return Result.ok(sysMenuService.getMenuTree());
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    public Result<Void> add(@RequestBody SysMenu menu) {
        sysMenuService.save(menu);
        return Result.ok();
    }

    @Operation(summary = "修改菜单")
    @PutMapping
    public Result<Void> update(@RequestBody SysMenu menu) {
        sysMenuService.updateById(menu);
        return Result.ok();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysMenuService.removeById(id);
        return Result.ok();
    }
}
