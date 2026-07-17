package org.example.finreport.module.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.business.entity.Product;
import org.example.finreport.module.business.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "业务数据 - 产品管理")
@RestController
@RequestMapping("/api/business/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/page")
    @Operation(summary = "分页查询产品")
    public Result<PageResult<Product>> page(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String productType,
                                            @RequestParam(required = false) String riskLevel,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(required = false) String productCode,
                                            @RequestParam(required = false) String productName) {
        return Result.ok(productService.page(page, size, productType, riskLevel, status, productCode, productName));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有产品列表")
    public Result<List<Product>> list() {
        return Result.ok(productService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取产品详情")
    public Result<Product> get(@PathVariable Long id) {
        return Result.ok(productService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增产品")
    public Result<Void> add(@RequestBody Product product) {
        productService.save(product);
        return Result.ok();
    }

    @PutMapping
    @Operation(summary = "修改产品")
    public Result<Void> update(@RequestBody Product product) {
        productService.updateById(product);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除产品")
    public Result<Void> delete(@PathVariable Long id) {
        productService.removeById(id);
        return Result.ok();
    }
}
