package org.example.finreport.module.dashboard.mapper;

import org.example.finreport.module.dashboard.vo.DashboardSummaryVO;
import org.example.finreport.module.dashboard.vo.TopBranchVO;
import org.example.finreport.module.dashboard.vo.ProfitTrendVO;
import org.example.finreport.module.dashboard.vo.RiskAlertVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardMapper {
    DashboardSummaryVO selectSummary();
    List<TopBranchVO> selectTopBranches(int limit);
    List<ProfitTrendVO> selectProfitTrend(int months);
    List<RiskAlertVO> selectRiskAlerts(int limit);
}
