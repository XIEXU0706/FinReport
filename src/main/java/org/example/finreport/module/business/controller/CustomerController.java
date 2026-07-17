package org.example.finreport.module.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.business.entity.Customer;
import org.example.finreport.module.business.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "业务数据 - 客户管理")
@RestController
@RequestMapping("/api/business/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/page")
    @Operation(summary = "分页查询客户")
    public Result<PageResult<Customer>> page(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) String phone,
                                             @RequestParam(required = false) String customerLevel,
                                             @RequestParam(required = false) String status) {
        System.out.println("=================================================================");
        System.out.println(customerService.page(page, size, name, phone, customerLevel, status));
        System.out.println("=================================================================");
        return Result.ok(customerService.page(page, size, name, phone, customerLevel, status));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有客户列表（下拉框用）")
    public Result<List<Customer>> list() {
        return Result.ok(customerService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取客户详情")
    public Result<Customer> get(@PathVariable Long id) {
        return Result.ok(customerService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增客户")
    public Result<Void> add(@RequestBody Customer customer) {
        customerService.save(customer);
        return Result.ok();
    }

    @PutMapping
    @Operation(summary = "修改客户")
    public Result<Void> update(@RequestBody Customer customer) {
        customerService.updateById(customer);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除客户")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.removeById(id);
        return Result.ok();
    }
}
