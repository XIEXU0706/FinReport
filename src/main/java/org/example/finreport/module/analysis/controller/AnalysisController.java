package org.example.finreport.module.analysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.analysis.service.AnalysisService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "经营分析中心")
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/trade")
    @Operation(summary = "交易分析")
    public Result<Map<String, Object>> tradeAnalysis(@RequestParam(defaultValue = "30") int days) {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(analysisService.getTradeAnalysis(days));
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
        return Result.ok(analysisService.getTradeAnalysis(days));
    }

    @GetMapping("/customer")
    @Operation(summary = "客户分析")
    public Result<Map<String, Object>> customerAnalysis() {
        return Result.ok(analysisService.getCustomerAnalysis());
    }

    @GetMapping("/loan")
    @Operation(summary = "贷款分析")
    public Result<Map<String, Object>> loanAnalysis() {
        return Result.ok(analysisService.getLoanAnalysis());
    }

    @GetMapping("/product")
    @Operation(summary = "产品分析")
    public Result<Map<String, Object>> productAnalysis() {
        return Result.ok(analysisService.getProductAnalysis());
    }
}
