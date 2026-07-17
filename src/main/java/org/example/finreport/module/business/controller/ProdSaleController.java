package org.example.finreport.module.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.PageResult;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.business.service.ProdSaleService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "业务数据 - 产品销售")
@RestController
@RequestMapping("/api/business/prod-sale")
@RequiredArgsConstructor
public class ProdSaleController {

    private final ProdSaleService prodSaleService;

    @GetMapping("/page")
    @Operation(summary = "分页查询产品销售记录")
    public Result<PageResult<Map<String, Object>>> page(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return Result.ok(prodSaleService.page(page, size));
    }
}
