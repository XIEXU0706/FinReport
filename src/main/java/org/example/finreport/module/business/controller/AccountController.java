package org.example.finreport.module.business.controller;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.business.entity.Account;
import org.example.finreport.module.business.service.AccountService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "业务数据 - 账户管理")
@RestController
@RequestMapping("/api/business/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/list")
    @Operation(summary = "获取所有账户列表")
    public Result<List<Account>> list() {
        return Result.ok(accountService.list());
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询账户")
    public Result<PageResult<Account>> page(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String accountNo,
                                            @RequestParam(required = false) String customerNo,
                                            @RequestParam(required = false) String accountType,
                                            @RequestParam(required = false) String currency,
                                            @RequestParam(required = false) String status) {
        return Result.ok(accountService.page(page, size, accountNo, customerNo, accountType, currency, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取账户详情")
    public Result<Account> get(@PathVariable Long id) {
        return Result.ok(accountService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增账户")
    public Result<Void> add(@RequestBody Account account) {
        accountService.save(account);
        return Result.ok();
    }

    @PutMapping
    @Operation(summary = "修改账户")
    public Result<Void> update(@RequestBody Account account) {
        accountService.updateById(account);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除账户")
    public Result<Void> delete(@PathVariable Long id) {
        accountService.removeById(id);
        return Result.ok();
    }
}
