package org.example.finreport.module.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.module.dashboard.mapper.DashboardMapper;
import org.example.finreport.module.dashboard.vo.DashboardSummaryVO;
import org.example.finreport.module.dashboard.vo.TopBranchVO;
import org.example.finreport.module.dashboard.vo.ProfitTrendVO;
import org.example.finreport.module.dashboard.vo.RiskAlertVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardMapper dashboardMapper;

    @Autowired @Lazy
    private DashboardService self;

    @Cacheable("dashboard:summary")
    public DashboardSummaryVO getSummary() {
        DashboardSummaryVO vo = dashboardMapper.selectSummary();
        if (vo == null) vo = new DashboardSummaryVO();
        vo.setTradeGrowthRate(calcRate(vo.getTodayTradeAmount(), vo.getYesterdayTradeAmount()));
        vo.setLoanGrowthRate(calcRate(vo.getTodayLoanAmount(), vo.getYesterdayLoanAmount()));
        vo.setCustomerGrowthRate(calcRate(BigDecimal.valueOf(vo.getTodayNewCustomers()),
                BigDecimal.valueOf(vo.getYesterdayNewCustomers())));
        vo.setRepaymentGrowthRate(calcRate(vo.getTodayRepaymentAmount(), vo.getYesterdayRepaymentAmount()));
        return vo;
    }

    @Cacheable(value = "dashboard:topBranches", key = "#limit")
    public List<TopBranchVO> getTopBranches(int limit) {
        return dashboardMapper.selectTopBranches(limit);
    }
    @Cacheable(value = "dashboard:profitTrend", key = "#months")
    public List<ProfitTrendVO> getProfitTrend(int months) {
        return dashboardMapper.selectProfitTrend(months);
    }
    @Cacheable(value = "dashboard:riskAlerts", key = "#limit")
    public List<RiskAlertVO> getRiskAlerts(int limit) {
        return dashboardMapper.selectRiskAlerts(limit);
    }

    @Cacheable("dashboard:full")
    public Map<String, Object> getFullDashboard() {
        Map<String, Object> r = new HashMap<>();
        r.put("summary", self.getSummary());
        r.put("topBranches", self.getTopBranches(10));
        r.put("profitTrend", self.getProfitTrend(6));
        r.put("riskAlerts", self.getRiskAlerts(10));
        return r;
    }

    private double calcRate(BigDecimal today, BigDecimal yesterday) {
        if (yesterday == null || yesterday.compareTo(BigDecimal.ZERO) == 0) return 0;
        return today.subtract(yesterday).multiply(BigDecimal.valueOf(100))
                .divide(yesterday, 2, RoundingMode.HALF_UP).doubleValue();
    }
}
