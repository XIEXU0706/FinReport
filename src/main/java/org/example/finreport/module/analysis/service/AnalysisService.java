package org.example.finreport.module.analysis.service;

import lombok.RequiredArgsConstructor;
import org.example.finreport.module.analysis.mapper.AnalysisMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisMapper analysisMapper;

    // ========== 交易分析 ==========

    public Map<String, Object> getTradeAnalysis(int days) {
        Map<String, Object> result = new HashMap<>();
        result.put("summary", analysisMapper.selectTradeSummary(days));
        result.put("dailyTrend", analysisMapper.selectTradeDailyTrend(days));
        result.put("channelRatio", analysisMapper.selectTradeChannelRatio(days));
        result.put("hourlyDist", analysisMapper.selectTradeHourlyDist24h());
        result.put("largeTrades", analysisMapper.selectLargeTrades(100000, days));
        result.put("typeDist", analysisMapper.selectTradeTypeDist(days));
        return result;
    }

    // ========== 客户分析 ==========

    public Map<String, Object> getCustomerAnalysis() {
        Map<String, Object> result = new HashMap<>();
        result.put("summary", analysisMapper.selectCustomerSummary());
        result.put("levelDist", analysisMapper.selectCustomerLevelDist());
        result.put("monthlyGrowth", analysisMapper.selectCustomerMonthlyGrowth(12));
        result.put("ageDist", analysisMapper.selectCustomerAgeDist());
        result.put("regionDist", analysisMapper.selectCustomerRegionDist());
        return result;
    }

    // ========== 贷款分析 ==========

    public Map<String, Object> getLoanAnalysis() {
        Map<String, Object> result = new HashMap<>();
        result.put("summary", analysisMapper.selectLoanSummary());
        result.put("statusDist", analysisMapper.selectLoanStatusDist());
        result.put("monthlyTrend", analysisMapper.selectLoanMonthlyTrend(12));
        result.put("termDist", analysisMapper.selectLoanTermDist());
        result.put("overdueAnalysis", analysisMapper.selectLoanOverdueAnalysis());
        return result;
    }

    // ========== 产品分析 ==========

    public Map<String, Object> getProductAnalysis() {
        Map<String, Object> result = new HashMap<>();
        result.put("salesRank", analysisMapper.selectProductSalesRank());
        result.put("typeDist", analysisMapper.selectProductTypeDist());
        result.put("returnRank", analysisMapper.selectProductReturnRank());
        return result;
    }
}
