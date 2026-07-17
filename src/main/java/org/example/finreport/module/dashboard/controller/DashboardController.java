package org.example.finreport.module.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.finreport.common.result.Result;
import org.example.finreport.module.dashboard.service.DashboardService;
import org.example.finreport.module.dashboard.vo.DashboardSummaryVO;
import org.example.finreport.module.dashboard.vo.TopBranchVO;
import org.example.finreport.module.dashboard.vo.ProfitTrendVO;
import org.example.finreport.module.dashboard.vo.RiskAlertVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Dashboard - 经营驾驶舱")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "首页汇总（今日 vs 昨日 含增长率）")
    public Result<DashboardSummaryVO> summary() { return Result.ok(dashboardService.getSummary()); }

    @GetMapping("/top-branches")
    @Operation(summary = "Top N 支行交易额")
    public Result<List<TopBranchVO>> topBranches(@RequestParam(defaultValue = "10") int limit) {
        return Result.ok(dashboardService.getTopBranches(limit));
    }

    @GetMapping("/profit-trend")
    @Operation(summary = "月度利润趋势")
    public Result<List<ProfitTrendVO>> profitTrend(@RequestParam(defaultValue = "6") int months) {
        return Result.ok(dashboardService.getProfitTrend(months));
    }

    @GetMapping("/risk-alerts")
    @Operation(summary = "风险预警列表")
    public Result<List<RiskAlertVO>> riskAlerts(@RequestParam(defaultValue = "10") int limit) {
        return Result.ok(dashboardService.getRiskAlerts(limit));
    }

    @GetMapping("/full")
    @Operation(summary = "完整首页数据聚合")
    public Result<Map<String, Object>> full() {
        return Result.ok(dashboardService.getFullDashboard());
    }
}
